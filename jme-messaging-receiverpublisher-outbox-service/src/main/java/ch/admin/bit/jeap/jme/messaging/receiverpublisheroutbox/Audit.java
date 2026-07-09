package ch.admin.bit.jeap.jme.messaging.receiverpublisheroutbox;

import ch.admin.bit.jeap.audit.command.builder.CreateAuditRecordCommandBuilder;
import ch.admin.bit.jeap.audit.record.create.CreateAuditRecordCommand;
import ch.admin.bit.jeap.audit.transactional.outbox.CreateAuditRecordCommandTransactionOutboxSender;
import ch.admin.bit.jeap.jme.messaging.common.infrastructure.TopicConfiguration;
import ch.admin.bit.jeap.messaging.annotations.JeapMessageProducerContract;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ch.admin.bit.jeap.messaging.model.Message;

import java.time.Instant;
import java.util.function.Consumer;

@Slf4j
@Component
@SuppressWarnings("UnnecessaryLocalVariable")
// Declare that this component produces messages of type CreateAuditRecordCommand
@JeapMessageProducerContract(value = CreateAuditRecordCommand.TypeRef.class, topic = TopicConfiguration.AUDIT_TOPIC)
public class Audit {

    private static final String OUR_DEPARTMENT_NAME = "BIT";
    private final String ourSystemName;
    private final String ourServiceName;
    // Using the outbox based audit sender to send the audit message ensures that the audit message will only be sent
    // when the state change to audit could be persisted successfully.
    private final CreateAuditRecordCommandTransactionOutboxSender auditSender;

    public Audit(@Value("${jeap.messaging.kafka.serviceName}") String serviceName,
                 @Value("${jeap.messaging.kafka.systemName}") String systemName,
                 CreateAuditRecordCommandTransactionOutboxSender auditSender) {
        this.ourSystemName = systemName;
        this.ourServiceName = serviceName;
        this.auditSender = auditSender;
    }

    // The triggering system and service will be taken from the triggering message's publisher
    public void auditWithMessageTrigger(Message triggeringMessage, Instant timestamp,
                                        Consumer<CreateAuditRecordCommandBuilder> auditBuilderConsumer) {
        // Assume only senders of our own department are authorized to send us messages
        String triggerDepartmentName = OUR_DEPARTMENT_NAME;
        log.info("Sending an audit command for a system event");
        auditSender.auditMessageTriggeredSystemEvent(ourServiceName, ourSystemName, triggerDepartmentName, timestamp, triggeringMessage, auditBuilderConsumer);
        log.info("Sent the audit command for the system event");
    }

    // The triggering user will be taken from the current security context
    public void auditWithUserTrigger(Instant timestamp, Consumer<CreateAuditRecordCommandBuilder> auditBuilderConsumer) {
        log.info("Sending an audit command for a user event");
        auditSender.auditUserTriggeredEvent(ourServiceName, ourSystemName, timestamp, auditBuilderConsumer);
        log.info("Sent the audit command for the user event");
    }

    public void auditWithInternalTrigger(Instant timestamp, Consumer<CreateAuditRecordCommandBuilder> auditBuilderConsumer) {
        CreateAuditRecordCommandBuilder auditBuilder = CreateAuditRecordCommandBuilder.
                createCommandBuilder(ourServiceName, ourSystemName, timestamp);
        auditBuilder.setTriggerSystem(OUR_DEPARTMENT_NAME, ourSystemName, ourServiceName);
        auditBuilderConsumer.accept(auditBuilder);
        log.info("Sending an audit command for an internally triggered event");
        auditSender.auditEvent(auditBuilder.build());
        log.info("Sent the audit command for the internally triggered event");
    }

}
