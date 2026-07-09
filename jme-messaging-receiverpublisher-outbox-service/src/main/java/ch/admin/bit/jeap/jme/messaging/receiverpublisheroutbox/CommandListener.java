package ch.admin.bit.jeap.jme.messaging.receiverpublisheroutbox;

import ch.admin.bit.jeap.audit.record.create.AuditEventType;
import ch.admin.bit.jeap.audit.record.create.AuditObjectDataRole;
import ch.admin.bit.jeap.jme.messaging.common.MessageContext;
import ch.admin.bit.jeap.jme.messaging.common.startdeclarationcommand.JmeCreateDeclarationCommandListener;
import ch.admin.bit.jeap.jme.messaging.receiverpublisheroutbox.inspection.ResentTestRepository;
import ch.admin.bit.jeap.messaging.idempotence.messagehandler.IdempotentMessageHandler;
import ch.admin.bit.jeap.messaging.kafka.tracing.TraceContextProvider;
import ch.admin.bit.jeap.messaging.model.MessageUser;
import ch.admin.bit.jme.declaration.JmeCreateDeclarationCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
class CommandListener implements JmeCreateDeclarationCommandListener {

    private static final String RESENT_TEST_TEXT = "resent-test";

    private final DeclarationService declarationService;
    private final Audit audit;
    private final ResentTestRepository resentTestRepository;
    private final TraceContextProvider traceContextProvider;

    // An idempotent message handler method execution must be part of a transaction. The transaction could have been started
    // prior to the call to the message handler or as in this example can be started with the method handler execution. If you
    // put the @Transactional and @IdempotentMessageHandler annotation on the same method see the comments in Application.
    @Transactional
    // The receive method will only be executed for a message (here: a command) if the message has not yet been processed
    // by the method in a successful surrounding transaction before. Messages are considered to be the same if they have
    // the same idempotence id and the same message type.
    @IdempotentMessageHandler
    @Override
    public void receive(JmeCreateDeclarationCommand command) {
        String text = command.getPayload().getText();
        log.info("Got JmeCreateDeclarationCommand {} with text '{}'.", command.getIdentity().getId(), text);
        if (Objects.equals(RESENT_TEST_TEXT, text)) {
            addMessageToResentRepository(command, text);
            throw new FailedMessageException(RESENT_TEST_TEXT);
        }

        Declaration newDeclaration = declarationService.createDeclaration(text);
        auditDeclarationCreated(command, newDeclaration);
    }

    private void addMessageToResentRepository(JmeCreateDeclarationCommand command, String text) {
        resentTestRepository.addMessage(new MessageContext(command.getIdentity().getId(),
                traceContextProvider.getTraceContext().getTraceIdString(),
                command.getPublisher().getService(),
                command.getIdentity().getIdempotenceId(),
                command.getType().getName(),
                command.getCommandVersion(),
                command.getIdentity().getCreatedZoned(),
                text,
                command.getOptionalUser().map(MessageUser::getId).orElse(null)
        ));
    }

    // The triggering system and service will be taken from the triggering message's publisher automatically
    private void auditDeclarationCreated(JmeCreateDeclarationCommand triggeringCommand, Declaration declaration) {
        audit.auditWithMessageTrigger(triggeringCommand, declaration.getCreatedAt().toInstant(), auditBuilder -> {
            // If we don't provide an idempotence id, the audit sender will use the one from the triggering message.
            auditBuilder.setEventType(AuditEventType.CREATED);
            auditBuilder.setAuditObject("Declaration", declaration.getId().toString());
            auditBuilder.addAuditObjectDataValue(AuditObjectDataRole.NEW, "text", declaration.getText());
            auditBuilder.setContext("System creates declaration", triggeringCommand.getProcessId());
        });
    }

}
