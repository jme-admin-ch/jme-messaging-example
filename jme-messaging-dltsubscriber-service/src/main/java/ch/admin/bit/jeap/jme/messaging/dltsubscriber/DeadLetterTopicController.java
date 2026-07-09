package ch.admin.bit.jeap.jme.messaging.dltsubscriber;

import ch.admin.bit.jeap.messaging.avro.errorevent.MessageProcessingFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@RequestMapping(path= "dead-letter")
@Slf4j
class DeadLetterTopicController {
    private final DeadLetterTopicConsumer consumer;

    @GetMapping
    public DeadLetterDto getEvents() {
        List<MessageProcessingFailedEvent> consumedMessages = consumer.getConsumedMessages();
        return new DeadLetterDto(consumedMessages.size(), consumedMessages.stream().map(MessageProcessingFailedEvent::toString).toList());
    }

}
