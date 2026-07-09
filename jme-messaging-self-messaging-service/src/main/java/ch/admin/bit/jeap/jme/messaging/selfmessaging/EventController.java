package ch.admin.bit.jeap.jme.messaging.selfmessaging;

import ch.admin.bit.jeap.messaging.avro.AvroMessage;
import ch.admin.bit.jeap.messaging.avro.AvroMessageKey;
import ch.admin.bit.jme.test.BeanReference;
import ch.admin.bit.jme.test.JmeBackwardSchemaEvolutionTestEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
class EventController {

    public static final String TOPIC = "jme-backward-schema-evolution-test-event";

    private final KafkaTemplate<AvroMessageKey, AvroMessage> kafkaTemplate;
    private final List<ch.admin.bit.jme.test.v2.JmeBackwardSchemaEvolutionTestEvent> receivedEvents = new ArrayList<>();

    @GetMapping(path = "/v1")
    // Should be PUT/POST but GET used to enable easy access of the example in browsers. Don't do this in real services!
    public void sendV1Event(@RequestParam String text, @RequestParam String idempotenceId) {
        BeanReference beanReference = BeanReference.newBuilder().setId("id").setName("name").setNamespace("namespace").setType("type").build();
        // Send a V1 Event
        JmeBackwardSchemaEvolutionTestEvent backwardSchemaEvolutionTestEvent = JmeBackwardSchemaEvolutionTestEventBuilder.create()
                .idempotenceId(idempotenceId)
                .message(text)
                .beanReference(beanReference).build();

        log.info("Sending V1 Event: {}", backwardSchemaEvolutionTestEvent);
        kafkaTemplate.send(TOPIC, backwardSchemaEvolutionTestEvent);
    }

    @GetMapping(path = "/v2")
    // Should be PUT/POST but GET used to enable easy access of the example in browsers. Don't do this in real services!
    public void sendV2Event(@RequestParam String text, @RequestParam String idempotenceId) {
        // Send a V2 Event
        ch.admin.bit.jme.test.v2.JmeBackwardSchemaEvolutionTestEvent backwardSchemaEvolutionTestEvent = JmeBackwardSchemaEvolutionTestEventV2Builder.create()
                .idempotenceId(idempotenceId)
                .message(text).build();

        log.info("Sending V2 Event: {}", backwardSchemaEvolutionTestEvent);
        kafkaTemplate.send(TOPIC, backwardSchemaEvolutionTestEvent);
    }

    @KafkaListener(topics = TOPIC,
            properties = "specific.avro.value.type=ch.admin.bit.jme.test.v2.JmeBackwardSchemaEvolutionTestEvent")
    public void onV2Event(ch.admin.bit.jme.test.v2.JmeBackwardSchemaEvolutionTestEvent event, Acknowledgment ack) {
        log.info("V2 Event consumed: {}", event);
        receivedEvents.add(event);
        ack.acknowledge();
    }

    @GetMapping(path = "/list-received-events")
    public List<Map<String, String>> sendV2Event() {
        return receivedEvents.stream()
                .map(event -> Map.of("idempotenceId", event.getIdentity().getIdempotenceId(), "text", event.getPayload().getMessage()))
                .toList();
    }

}
