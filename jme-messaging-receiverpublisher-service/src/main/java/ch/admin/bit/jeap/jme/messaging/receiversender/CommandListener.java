package ch.admin.bit.jeap.jme.messaging.receiversender;

import ch.admin.bit.jeap.domainevent.api.EventListener;
import ch.admin.bit.jeap.domainevent.avro.AvroDomainEventUser;
import ch.admin.bit.jeap.jme.messaging.common.declarationcreatedevent.JmeDeclarationCreatedEventBuilder;
import ch.admin.bit.jeap.jme.messaging.common.infrastructure.KafkaEventPublisher;
import ch.admin.bit.jeap.jme.messaging.common.startdeclarationcommand.JmeCreateDeclarationCommandListener;
import ch.admin.bit.jme.declaration.JmeCreateDeclarationCommand;
import ch.admin.bit.jme.declaration.JmeDeclarationCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * A bean that needs to act on events only needs to implement the {@link EventListener} interface
 * and will then be informed about the relevant events.
 * If you need more subtle routing (e.g. only some events should be send to this consumer) you
 * can also implement a more intelligent event consumer.
 * <p>
 * This example simple buffers the last 5 events and return them via a rest interface.
 */
@Component
@Slf4j
class CommandListener implements JmeCreateDeclarationCommandListener {

    private final KafkaEventPublisher eventPublisher;
    private final String applicationName;

    CommandListener(KafkaEventPublisher eventPublisher, @Value("${spring.application.name}") String applicationName) {
        this.eventPublisher = eventPublisher;
        this.applicationName = applicationName;
    }

    @Override
    public void receive(JmeCreateDeclarationCommand command) {
        String text = command.getPayload().getText();
        log.info("Got JmeCreateDeclarationCommand {} with text '{}' and user '{}'.",
                command.getIdentity().getId(), text, command.getUser());

        // Some business logic that creates a Declaration which shall be made known to others by a JmeDeclarationCreatedEvent.

        JmeDeclarationCreatedEvent event = JmeDeclarationCreatedEventBuilder.create().
                // We must make sure that for the same command we create the same event.
                        idempotenceId(command.getIdentity().getIdempotenceId()).
                serviceName(applicationName).
                user(createUserWithLimitedData()).
                message(text).
                build();
        eventPublisher.send(event);
        log.info("Sent JmeDeclarationCreatedEvent {}.", event.getIdentity().getId());
    }

    private AvroDomainEventUser createUserWithLimitedData() {
        return AvroDomainEventUser.newBuilder()
                .setFamilyName("Müller")
                .setId("zyxw")
                .build();
    }

}
