package ch.admin.bit.jeap.jme.messaging.receiverpublisheroutbox;

import ch.admin.bit.jeap.domainevent.avro.AvroDomainEventUser;
import ch.admin.bit.jeap.jme.messaging.common.declarationcreatedevent.JmeDeclarationCreatedEventBuilder;
import ch.admin.bit.jeap.messaging.transactionaloutbox.outbox.TransactionalOutbox;
import ch.admin.bit.jme.declaration.JmeDeclarationCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class DeclarationService {

    private final TransactionalOutbox outbox;
    private final DeclarationRepository declarationRepository;
    private final String applicationName;

    DeclarationService(TransactionalOutbox outbox, DeclarationRepository declarationRepository, @Value("${spring.application.name}") String applicationName) {
        this.outbox = outbox;
        this.declarationRepository = declarationRepository;
        this.applicationName = applicationName;
    }

    // The transactional outbox must be used within an active transaction.
    @Transactional
    public Declaration createDeclaration(String text) {

        // Alter persistent state based on the message/request received
        Declaration declaration = Declaration.from(text);
        declaration = declarationRepository.save(declaration);

        // Create an event to notify interested applications about the state change. Publish the event to a badly named topic
        // if the declaration text starts with the magic word 'failed' which should result in a failed message.
        String topic = !text.startsWith("failed") ? JmeDeclarationCreatedEvent.TypeRef.DEFAULT_TOPIC : "bad!topic";
        JmeDeclarationCreatedEvent event = JmeDeclarationCreatedEventBuilder.create().
                serviceName(applicationName).
                idempotenceId(UUID.randomUUID().toString()).
                user(AvroDomainEventUser.newBuilder().setId("otbx").build()).
                message(text).
                build();

        // Send the event using one of the outbox send methods
        if (!text.startsWith("scheduled")) {
            // Register the event to be sent within the current thread immediately after the active transaction commits.
            outbox.sendMessage(event, topic);
        } else {
            // Register the event to be sent later within a scheduled poll of the outbox message relay process in a separate thread.
            outbox.sendMessageScheduled(event, topic);
        }

        // If this throws an exception the active transaction will be rolled back, the persistent state won't be altered and
        // the outbox won't send the event that has been registered with it during the transaction.
        if (text.equalsIgnoreCase("RuntimeException")) {
            throw new RuntimeException("oops");
        }

        log.info("Created the declaration: {}.", declaration);
        return declaration;
    }
    // After this method execution completes the transaction wrapping it will be committed (if there was no exception)
    // and the event registered with the outbox will be sent either immediately or later during a scheduled poll (depending
    // on the outbox method used to register the message). If there is a problem with Kafka during the immediate sending of
    // the message the outbox will send the event later during a scheduled poll. If there is a problem with Kafka when
    // sending the event during a scheduled poll the outbox will abort the sending in the poll and retry sending later
    // with the next scheduled poll.

}
