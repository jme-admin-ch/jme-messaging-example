package ch.admin.bit.jeap.jme.messaging.sender.order.event;

import ch.admin.bit.jeap.domainevent.avro.AvroDomainEventBuilder;
import ch.admin.bit.jeap.jme.messaging.common.declarationcreatedevent.JmeDeclarationCreatedEventBuilder;
import ch.admin.bit.jeap.messaging.avro.AvroMessageBuilderException;
import ch.admin.bit.jme.messaging.OrderReference;
import ch.admin.bit.jme.messaging.event.order.created.JmeOrderCreatedEvent;
import ch.admin.bit.jme.messaging.event.order.created.JmeOrderCreatedPayload;
import ch.admin.bit.jme.messaging.event.order.created.JmeOrderCreatedReferences;
import lombok.Getter;

@Getter
public class JmeOrderCreatedEventBuilder extends AvroDomainEventBuilder<JmeOrderCreatedEventBuilder, JmeOrderCreatedEvent> {
    private final String systemName = "JME";
    private final String eventName = "JmeOrderCreatedEvent";
    private String serviceName = "jme-messaging-sender-service";

    private String orderId;
    private String type;

    private JmeOrderCreatedEventBuilder() {
        super(JmeOrderCreatedEvent::new);
    }

    public static JmeOrderCreatedEventBuilder create() {
        return new JmeOrderCreatedEventBuilder();
    }


    public JmeOrderCreatedEventBuilder orderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public JmeOrderCreatedEventBuilder type(String type) {
        this.type = type;
        return this;
    }

    @Override
    protected JmeOrderCreatedEventBuilder self() {
        return this;
    }

    @Override
    public JmeOrderCreatedEvent build() {
        OrderReference orderReference = OrderReference.newBuilder()
                .setType("order")
                .setOrderId(orderId)
                .build();
        JmeOrderCreatedReferences references = JmeOrderCreatedReferences.newBuilder()
                .setReference(orderReference)
                .build();
        JmeOrderCreatedPayload payload = JmeOrderCreatedPayload.newBuilder()
                .setOrderType(type)
                .build();
        setReferences(references);
        setPayload(payload);
        setProcessId(orderId);
        return super.build();
    }
}
