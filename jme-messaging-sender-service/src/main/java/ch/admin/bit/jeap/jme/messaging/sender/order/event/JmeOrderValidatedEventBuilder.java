package ch.admin.bit.jeap.jme.messaging.sender.order.event;

import ch.admin.bit.jeap.domainevent.avro.AvroDomainEventBuilder;
import ch.admin.bit.jme.messaging.OrderReference;
import ch.admin.bit.jme.messaging.event.order.validated.JmeOrderValidatedEvent;
import ch.admin.bit.jme.messaging.event.order.validated.JmeOrderValidatedPayload;
import ch.admin.bit.jme.messaging.event.order.validated.JmeOrderValidatedReferences;
import ch.admin.bit.jme.messaging.event.order.validated.ValidationType;
import lombok.Getter;

@Getter
public class JmeOrderValidatedEventBuilder extends AvroDomainEventBuilder<JmeOrderValidatedEventBuilder, JmeOrderValidatedEvent> {
    private final String systemName = "JME";
    private final String eventName = "JmeOrderValidatedEvent";
    private final String serviceName = "jme-messaging-sender-service";

    private String orderId;
    private ValidationType validationType;

    private JmeOrderValidatedEventBuilder() {
        super(JmeOrderValidatedEvent::new);
    }

    public static JmeOrderValidatedEventBuilder create() {
        return new JmeOrderValidatedEventBuilder();
    }

    public JmeOrderValidatedEventBuilder orderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public JmeOrderValidatedEventBuilder validationType(ValidationType validationType) {
        this.validationType = validationType;
        return this;
    }

    @Override
    protected JmeOrderValidatedEventBuilder self() {
        return this;
    }

    @Override
    public JmeOrderValidatedEvent build() {
        setReferences(JmeOrderValidatedReferences.newBuilder()
                .setReference(OrderReference.newBuilder()
                        .setType("order")
                        .setOrderId(orderId)
                        .build())
                .build());
        setPayload(JmeOrderValidatedPayload.newBuilder()
                .setValidationType(validationType)
                .build());
        setProcessId(orderId);
        return super.build();
    }
}
