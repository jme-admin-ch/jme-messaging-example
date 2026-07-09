package ch.admin.bit.jeap.jme.messaging.common.infrastructure;

import ch.admin.bit.jeap.command.avro.AvroCommand;
import ch.admin.bit.jeap.messaging.api.MessagePublisher;
import ch.admin.bit.jeap.messaging.avro.AvroMessage;
import ch.admin.bit.jeap.messaging.avro.AvroMessageKey;
import ch.admin.bit.jeap.messaging.avro.errorevent.MessageHandlerException;
import ch.admin.bit.jeap.messaging.avro.errorevent.MessageHandlerExceptionInformation;
import ch.admin.bit.jme.declaration.JmeCreateDeclarationCommand;
import ch.admin.bit.jme.declaration.v2.JmeCreateDeclarationV2Command;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * You need to have one or multiple command sender implementations like this to send commands. The command sender
 * encapsulates how exactly the commands are sent e.g. to which message topics using which message keys etc.
 * <p>
 * Depending on your needs, a command sender can be very simple (as e.g. in this example where we use a simple topic
 * resolution) or quite complex (e.g. various command types sent to multiple topics depending on
 * some input). E.g. your command sender could use some kind of topic registry to compute the actual topic for a command.
 * If more input than just the command is needed in the send() method of the command sender, feel free to implement your
 * completely own command sender class which does not need to implement the MessagePublisher interface. You could also
 * send commands and publish events with one single MessagePublisher&lt;Message&gt; implementation, as commands and events
 * both simply are Message instances.
 * <p>
 * NOTE: If you store this in a common jar, make sure this class is on the search path, e.g. by
 * adding it to spring factories
 */
@Slf4j
@RequiredArgsConstructor
public class KafkaCommandSender implements MessagePublisher<AvroCommand> {
    private static final int SEND_TIMEOUT_SEC = 30;
    private final KafkaTemplate<AvroMessageKey, AvroMessage> kafkaTemplate;
    private final TopicConfiguration topicConfiguration;

    /**
     * This is the default send method as defined in {@link MessagePublisher}.
     * Here we send messages without a Kafka message key. In this case a command will be received by a random receiver.
     * This normally works fine, but if you have restrictions on the chronological order of the commands at the receiver
     * you need to use keys.
     */
    @Override
    public void send(final AvroCommand command) {
        final String topic = resolveFromCommand(command);

        log.debug("sending command '{}' to topic {}.", command, topic);
        // Note this does an asynchronous send. When this fails there would be no exception thrown by send().
        // We can add get() to block the call and get potential exceptions right here.
        try {
            kafkaTemplate.send(topic, command).get(SEND_TIMEOUT_SEC, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Sending command failed", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Sending command failed", e);
        } catch (TimeoutException e) {
            throw MessageHandlerException.builder().
                    message("Timeout while seding command").
                    errorCode("708").
                    temporality(MessageHandlerExceptionInformation.Temporality.TEMPORARY).
                    cause(e).
                    build();
        }
    }

    private String resolveFromCommand(AvroCommand command) {
        if (command instanceof JmeCreateDeclarationCommand) {
            return JmeCreateDeclarationCommand.TypeRef.DEFAULT_TOPIC;
        }
        if (command instanceof JmeCreateDeclarationV2Command) {
            return JmeCreateDeclarationV2Command.TypeRef.DEFAULT_TOPIC;
        }
        throw new IllegalArgumentException(String.format("No topic has been configured for command '%s'", command.getSchema().getName()));
    }

}
