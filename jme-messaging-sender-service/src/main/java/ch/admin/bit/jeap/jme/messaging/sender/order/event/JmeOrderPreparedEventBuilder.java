package ch.admin.bit.jeap.jme.messaging.sender.order.event;

import ch.admin.bit.jeap.domainevent.avro.AvroDomainEventBuilder;
import ch.admin.bit.jme.messaging.OrderReference;
import ch.admin.bit.jme.messaging.event.order.prepared.JmeOrderPreparedEvent;
import ch.admin.bit.jme.messaging.event.order.prepared.JmeOrderPreparedReferences;
import lombok.Getter;

@Getter
public class JmeOrderPreparedEventBuilder extends AvroDomainEventBuilder<JmeOrderPreparedEventBuilder, JmeOrderPreparedEvent> {
    private final String systemName = "JME";
    private final String eventName = "JmeOrderPreparedEvent";
    private String serviceName = "jme-messaging-sender-service";

    private String orderId;
    private String type;

    private JmeOrderPreparedEventBuilder() {
        super(JmeOrderPreparedEvent::new);
    }

    public static JmeOrderPreparedEventBuilder create() {
        return new JmeOrderPreparedEventBuilder();
    }


    public JmeOrderPreparedEventBuilder orderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    @Override
    protected JmeOrderPreparedEventBuilder self() {
        return this;
    }

    @Override
    public JmeOrderPreparedEvent build() {
        OrderReference orderReference = OrderReference.newBuilder()
                .setType("order")
                .setOrderId(orderId)
                .build();
        JmeOrderPreparedReferences references = JmeOrderPreparedReferences.newBuilder()
                .setReference(orderReference)
                .build();
        setReferences(references);
        setProcessId(orderId);
        return super.build();
    }
}
