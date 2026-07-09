package ch.admin.bit.jeap.jme.messaging.sequentialinbox.kafka;

import ch.admin.bit.jeap.jme.messaging.sequentialinbox.inspection.MessageRecorder;
import ch.admin.bit.jeap.messaging.avro.AvroMessageKey;
import ch.admin.bit.jeap.messaging.sequentialinbox.spring.SequentialInboxMessageListener;
import ch.admin.bit.jme.messaging.event.order.created.JmeOrderCreatedEvent;
import ch.admin.bit.jme.messaging.event.order.prepared.JmeOrderPreparedEvent;
import ch.admin.bit.jme.messaging.event.order.shipped.JmeOrderShippedEvent;
import ch.admin.bit.jme.messaging.event.order.validated.JmeOrderValidatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JmeOrderEventsListener {

    private final MessageRecorder messageRecorder;

    /**
     * @param key     Avro message key, optional (enable jeap.messaging.kafka.expose-message-key-to-consumer if you need the key)
     * @param message Avro message
     */
    @SequentialInboxMessageListener
    public void onEvent(AvroMessageKey key, JmeOrderPreparedEvent message) {
        messageRecorder.recordMessage(message.getReferences().getReference().getOrderId(), "prepared");
    }

    @SequentialInboxMessageListener
    public void onEvent(JmeOrderValidatedEvent message) {
        messageRecorder.recordMessage(message.getReferences().getReference().getOrderId(), "validated");
    }

    @SequentialInboxMessageListener
    public void onEvent(JmeOrderShippedEvent message) {
        messageRecorder.recordMessage(message.getReferences().getReference().getOrderId(), "shipped");
    }

    @SequentialInboxMessageListener
    public void onEvent(JmeOrderCreatedEvent message) {
        messageRecorder.recordMessage(message.getReferences().getReference().getOrderId(), "created");
    }
}
