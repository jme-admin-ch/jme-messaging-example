package ch.admin.bit.jeap.jme.messaging.common.infrastructure;

import ch.admin.bit.jeap.jme.messaging.common.declarationcreatedevent.JmeDeclarationCreatedEventListener;
import ch.admin.bit.jme.declaration.JmeDeclarationCreatedEvent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;

/**
 * For each event type you want to consume in your application you have to define a consumer.
 * This is linking the listener beans to the topic. You can also do the acknowledgement and
 * error handling here so that the beans do not have to deal with this.
 * <p>
 * For each topic you need to have one consume method annotated with the {@link KafkaListener} annotation. You can
 * either have multiple such methods in one class or define multiple classes (in the 2nd case the
 * {@link ConditionalOnBean} will be way easier).
 * <p>
 * In the KafkaListener annotation the name of the topic must be static. However you can use SPEL expressions with
 * variables e.g. ${enviroment}, check {@link TopicConfiguration} for details.
 * <p>
 * This bean should only be loaded if there actually is an implementation of a Listener for this
 * Event, therefore the {@link ConditionalOnBean}.
 * <p>
 * NOTE: If you store this in a common jar, make sure this class is on the search path, e.g. by
 * adding it to spring factories
 */
@RequiredArgsConstructor
@Slf4j
public class KafkaEventConsumer {
    private final List<JmeDeclarationCreatedEventListener> jmeDeclarationCreatedEventListeners;

    @PostConstruct
    void info() {
        log.info("Started listening to events.");
    }

    @KafkaListener(topics = {JmeDeclarationCreatedEvent.TypeRef.DEFAULT_TOPIC})
    public void consume(final JmeDeclarationCreatedEvent event, Acknowledgment ack) {
        log.debug("Received JmeDeclarationCreatedEvent {}", event.getIdentity().getEventId());
        jmeDeclarationCreatedEventListeners.forEach(l -> l.receive(event));
        // Acknowledge event. In case of an error, the error handler will take care of acknowledging the event,
        // after having forwarded the event to the error handling service.
        ack.acknowledge();
        log.debug("Acknowledged JmeDeclarationCreatedEvent {}", event.getIdentity().getEventId());
    }
}
