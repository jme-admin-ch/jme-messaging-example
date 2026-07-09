package ch.admin.bit.jeap.jme.messaging.dltsubscriber;

import ch.admin.bit.jeap.messaging.avro.errorevent.MessageProcessingFailedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class DeadLetterTopicConsumer {

    private static final int MAX_SIZE = 100;

    private final List<MessageProcessingFailedEvent> consumedMessages = new ArrayList<>();

    @KafkaListener(topics = {"jme-messageprocessing-deadletter"}, id = "DeadLetterTopicConsumer")
    public void consume(final MessageProcessingFailedEvent message, Acknowledgment acknowledgment) {
        if (consumedMessages.size() >= MAX_SIZE) {
            consumedMessages.remove(0);
        }

        consumedMessages.add(message);

        log.info("Consuming message in DeadLetterTopicConsumer: {}", message);
        acknowledgment.acknowledge();
    }

    public List<MessageProcessingFailedEvent> getConsumedMessages() {
        return Collections.unmodifiableList(consumedMessages);
    }

}
