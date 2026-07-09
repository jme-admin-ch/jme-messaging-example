package ch.admin.bit.jeap.jme.messaging.sender.order;

import ch.admin.bit.jeap.domainevent.avro.AvroDomainEvent;
import ch.admin.bit.jeap.jme.messaging.common.MessageContext;
import ch.admin.bit.jeap.jme.messaging.sender.order.event.JmeOrderCreatedEventBuilder;
import ch.admin.bit.jeap.jme.messaging.sender.order.event.JmeOrderPreparedEventBuilder;
import ch.admin.bit.jeap.jme.messaging.sender.order.event.JmeOrderShippedEventBuilder;
import ch.admin.bit.jeap.jme.messaging.sender.order.event.JmeOrderValidatedEventBuilder;
import ch.admin.bit.jeap.messaging.avro.AvroMessage;
import ch.admin.bit.jeap.messaging.avro.AvroMessageKey;
import ch.admin.bit.jeap.messaging.kafka.tracing.TraceContextProvider;
import ch.admin.bit.jeap.messaging.model.MessageUser;
import ch.admin.bit.jme.messaging.event.order.created.JmeOrderCreatedEvent;
import ch.admin.bit.jme.messaging.event.order.prepared.JmeOrderPreparedEvent;
import ch.admin.bit.jme.messaging.event.order.shipped.JmeOrderShippedEvent;
import ch.admin.bit.jme.messaging.event.order.validated.JmeOrderValidatedEvent;
import ch.admin.bit.jme.messaging.event.order.validated.ValidationType;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * This Controller sends JmeOrder*Events to test the functionality of the sequential inbox.
 *
 */

@RestController()
@RequestMapping("/send-order-events")
@RequiredArgsConstructor
@Slf4j
class SendOrderEventsController {

    private static final int SEND_TIMEOUT_SEC = 30;
    private static final String EVENT_VERSION = "1.0.0";

    private final KafkaTemplate<AvroMessageKey, AvroMessage> kafkaTemplate;
    private final TraceContextProvider traceContextProvider;

    @GetMapping(path = "/created")
    @SneakyThrows
    public MessageContext sendCreatedEvent(@RequestParam String orderId, @RequestParam(required = false) String type) {
        JmeOrderCreatedEvent event = JmeOrderCreatedEventBuilder.create()
                .idempotenceId(UUID.randomUUID().toString())
                .orderId(orderId)
                .type(type != null ? type : "SEQUENCED")
                .build();
        send(JmeOrderCreatedEvent.TypeRef.DEFAULT_TOPIC, event);
        return returnMessageContext(event, orderId);
    }

    @GetMapping(path = "/prepared")
    @SneakyThrows
    public MessageContext sendPrepared(@RequestParam String orderId) {
        JmeOrderPreparedEvent event = JmeOrderPreparedEventBuilder.create()
                .idempotenceId(UUID.randomUUID().toString())
                .orderId(orderId)
                .build();
        send(JmeOrderPreparedEvent.TypeRef.DEFAULT_TOPIC, event);
        return returnMessageContext(event, orderId);
    }

    @GetMapping(path = "/validated")
    @SneakyThrows
    public MessageContext sendValidated(@RequestParam String orderId, @RequestParam ValidationType validationType) {
        JmeOrderValidatedEvent event = JmeOrderValidatedEventBuilder.create()
                .idempotenceId(UUID.randomUUID().toString())
                .orderId(orderId)
                .validationType(validationType)
                .build();
        send(JmeOrderValidatedEvent.TypeRef.DEFAULT_TOPIC, event);
        return returnMessageContext(event, orderId);
    }

    @GetMapping(path = "/shipped")
    @SneakyThrows
    public MessageContext sendShipped(@RequestParam String orderId) {
        JmeOrderShippedEvent event = JmeOrderShippedEventBuilder.create()
                .idempotenceId(UUID.randomUUID().toString())
                .orderId(orderId)
                .build();
        send(JmeOrderShippedEvent.TypeRef.DEFAULT_TOPIC, event);
        return returnMessageContext(event, orderId);
    }

    @SneakyThrows
    public void send(final String topic, final AvroDomainEvent event) {
        log.debug("Publishing event {} with null key to topic {}.", event, topic);
        kafkaTemplate.send(topic, event).get(SEND_TIMEOUT_SEC, TimeUnit.SECONDS);
    }

    private MessageContext returnMessageContext(AvroMessage event, String orderId) {
        return new MessageContext(event.getIdentity().getId(),
                traceContextProvider.getTraceContext().getTraceIdString(),
                event.getPublisher().getService(),
                event.getIdentity().getIdempotenceId(),
                event.getType().getName(),
                EVENT_VERSION,
                event.getIdentity().getCreatedZoned(),
                orderId,
                event.getOptionalUser().map(MessageUser::getId).orElse(null));
    }

}
