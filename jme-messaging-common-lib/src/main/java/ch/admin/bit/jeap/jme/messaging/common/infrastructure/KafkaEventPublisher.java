package ch.admin.bit.jeap.jme.messaging.common.infrastructure;

import ch.admin.bit.jeap.domainevent.avro.AvroDomainEvent;
import ch.admin.bit.jeap.messaging.api.MessagePublisher;
import ch.admin.bit.jeap.messaging.avro.AvroMessage;
import ch.admin.bit.jeap.messaging.avro.AvroMessageKey;
import ch.admin.bit.jeap.messaging.avro.errorevent.MessageHandlerException;
import ch.admin.bit.jeap.messaging.avro.errorevent.MessageHandlerExceptionInformation;
import ch.admin.bit.jme.declaration.BeanReferenceMessageKey;
import ch.admin.bit.jme.declaration.JmeDeclarationCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * You need to have one or multiple event publisher implementations like this to publish events. The event publisher
 * encapsulates how exactly the events are published e.g. to which message topics using which message keys etc.
 * <p>
 * Depending on your needs, an event publisher can be very simple (as e.g. in this example where we only publish a single
 * event type to a single topic) or quite complex (e.g. various event types published to multiple topics depending on
 * some input). E.g. your event publisher could use some kind of topic registry to compute the actual topic for an event.
 * If more input than just the event is needed in the send() method of the event publisher, feel free to implement your
 * completely own event publisher class which does not need to implement the MessagePublisher interface. You could also
 * publish events and send commands with one single MessagePublisher&lt;Message&gt; implementation, as events and commands
 * both simply are Message instances.
 * <p>
 * NOTE: If you store this in a common jar, make sure this class is on the search path, e.g. by
 * adding it to spring factories
 */
@Slf4j
@RequiredArgsConstructor
public class KafkaEventPublisher implements MessagePublisher<AvroDomainEvent> {
    private static final int SEND_TIMEOUT_SEC = 30;
    private final KafkaTemplate<AvroMessageKey, AvroMessage> kafkaTemplate;
    private final TopicConfiguration topicConfiguration;

    /**
     * This is the default send method as defined in {@link MessagePublisher}. Here it will
     * publish an event with a null-key. In this case the event will be received by a random subscriber.
     * This normally works fine but if you have restrictions on the chronological order of the events at the subscriber
     * you need to use keys.
     * <p>
     * In our case the key cannot be derived from the event itself and we need another send() method
     * (see below) with additional arguments that support computing the key.
     */
    @Override
    public void send(final AvroDomainEvent event) {
        final String topic = JmeDeclarationCreatedEvent.TypeRef.DEFAULT_TOPIC;
        log.debug("Publishing event {} with null key to topic {}.", event, topic);
        // Note this does an asynchronous send. When this fails there is no exception here,
        // we need to add get() to block the call and get the exception.
        try {
            kafkaTemplate.send(topic, event).get(SEND_TIMEOUT_SEC, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            handleInterruptedException(e);
        } catch (ExecutionException e) {
            handleExecutionException(e);
        } catch (TimeoutException e) {
            handleTimeoutException(e);
        }
    }

    public void send(final AvroDomainEvent event, final Class<?> senderBeanClass, final UUID senderBeanId) {
        // For this example, let's assume we want events to be partitioned (or compacted) by the 'sender' bean.
        // To enable such partitioning we need to use the bean reference as the Kakfa message's key.
        AvroMessageKey messageKey = BeanReferenceMessageKey.newBuilder()
                .setName(senderBeanClass.getName())
                .setNamespace(senderBeanClass.getPackageName())
                .setId(senderBeanId.toString())
                .build();
        final String topic = JmeDeclarationCreatedEvent.TypeRef.DEFAULT_TOPIC;
        log.debug("Publishing event {} with message key '{}' to topic {}.", event, messageKey, topic);
        // Note this does an asynchronous send. When this fails there would be no exception thrown by send().
        // We can add get() to block the call and get potential exceptions right here.
        try {
            kafkaTemplate.send(topic, messageKey, event).get(SEND_TIMEOUT_SEC, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            handleInterruptedException(e);
        } catch (ExecutionException e) {
            handleExecutionException(e);
        } catch (TimeoutException e) {
            handleTimeoutException(e);
        }
    }

    private void handleInterruptedException(InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new RuntimeException("Publishing event failed", e);
    }

    private void handleExecutionException(ExecutionException e) {
        throw new RuntimeException("Publishing event failed", e);
    }

    private void handleTimeoutException(TimeoutException e) {
        throw MessageHandlerException.builder().
                message("Timeout while publishing event").
                errorCode("709").
                temporality(MessageHandlerExceptionInformation.Temporality.TEMPORARY).
                cause(e).
                build();
    }
}
