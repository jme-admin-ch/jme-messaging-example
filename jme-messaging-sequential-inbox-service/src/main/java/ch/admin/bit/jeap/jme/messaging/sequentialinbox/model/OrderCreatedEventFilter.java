package ch.admin.bit.jeap.jme.messaging.sequentialinbox.model;

import ch.admin.bit.jeap.messaging.sequentialinbox.configuration.model.MessageFilter;
import ch.admin.bit.jme.messaging.event.order.created.JmeOrderCreatedEvent;

/**
 * Example of a MessageFilter for the sequential inbox. Messages with OrderType = NOT_SEQUENCED won't be sequenced.
 */
public class OrderCreatedEventFilter implements MessageFilter<JmeOrderCreatedEvent> {

    @Override
    public boolean shouldSequence(JmeOrderCreatedEvent message) {
        return !"NOT_SEQUENCED".equals(message.getPayload().getOrderType());
    }
}
