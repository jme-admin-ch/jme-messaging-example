package ch.admin.bit.jeap.jme.messaging.sequentialinbox.model;

import ch.admin.bit.jeap.messaging.sequentialinbox.configuration.model.SubTypeResolver;
import ch.admin.bit.jme.messaging.event.order.validated.JmeOrderValidatedEvent;
import ch.admin.bit.jme.messaging.event.order.validated.ValidationType;

public class OrderValidatedEventSubTypeResolver implements SubTypeResolver<JmeOrderValidatedEvent, ValidationType> {

    @Override
    public ValidationType resolveSubType(JmeOrderValidatedEvent event) {
        return event.getPayload().getValidationType();
    }
}
