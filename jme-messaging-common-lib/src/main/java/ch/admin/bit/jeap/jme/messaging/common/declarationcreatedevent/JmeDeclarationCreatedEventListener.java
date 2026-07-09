package ch.admin.bit.jeap.jme.messaging.common.declarationcreatedevent;

import ch.admin.bit.jeap.jme.messaging.common.infrastructure.KafkaEventConsumer;
import ch.admin.bit.jeap.messaging.api.MessageListener;
import ch.admin.bit.jme.declaration.JmeDeclarationCreatedEvent;

/**
 * Base class for all listeners that are interested in receiving JmeDeclarationCreatedEvent events.
 * Such an interface might not strictly be needed as there is the generic {@link MessageListener} interface, but it
 * makes life easier especially in {@link KafkaEventConsumer} as we do not need to struggle with generic types.
 */

public interface JmeDeclarationCreatedEventListener extends MessageListener<JmeDeclarationCreatedEvent> {
}
