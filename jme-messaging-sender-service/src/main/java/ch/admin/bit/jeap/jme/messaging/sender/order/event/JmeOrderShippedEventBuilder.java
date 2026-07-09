package ch.admin.bit.jeap.jme.messaging.sender.order.event;

import ch.admin.bit.jeap.domainevent.avro.AvroDomainEventBuilder;
import ch.admin.bit.jme.messaging.OrderReference;
import ch.admin.bit.jme.messaging.event.order.shipped.JmeOrderShippedEvent;
import ch.admin.bit.jme.messaging.event.order.shipped.JmeOrderShippedReferences;
import lombok.Getter;

@Getter
public class JmeOrderShippedEventBuilder extends AvroDomainEventBuilder<JmeOrderShippedEventBuilder, JmeOrderShippedEvent> {
    private final String systemName = "JME";
    private final String eventName = "JmeOrderShippedEvent";
    private String serviceName = "jme-messaging-sender-service";

    private String orderId;
    private String type;

    private JmeOrderShippedEventBuilder() {
        super(JmeOrderShippedEvent::new);
    }

    public static JmeOrderShippedEventBuilder create() {
        return new JmeOrderShippedEventBuilder();
    }


    public JmeOrderShippedEventBuilder orderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    @Override
    protected JmeOrderShippedEventBuilder self() {
        return this;
    }

    @Override
    public JmeOrderShippedEvent build() {
        OrderReference orderReference = OrderReference.newBuilder()
                .setType("order")
                .setOrderId(orderId)
                .build();
        JmeOrderShippedReferences references = JmeOrderShippedReferences.newBuilder()
                .setReference(orderReference)
                .build();
        setReferences(references);
        setProcessId(orderId);
        return super.build();
    }
}
