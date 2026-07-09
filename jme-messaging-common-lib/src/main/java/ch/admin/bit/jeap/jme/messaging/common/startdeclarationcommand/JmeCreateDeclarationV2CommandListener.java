package ch.admin.bit.jeap.jme.messaging.common.startdeclarationcommand;

import ch.admin.bit.jeap.jme.messaging.common.infrastructure.KafkaCommandReceiver;
import ch.admin.bit.jeap.messaging.api.MessageListener;
import ch.admin.bit.jme.declaration.v2.JmeCreateDeclarationV2Command;

/**
 * Base class for all listeners that are interested in receiving JmeCreateDeclarationV2Command commands.
 * Such an interface might not strictly be needed as there is the generic {@link MessageListener} interface, but it
 * makes life easier especially in {@link KafkaCommandReceiver} as we do not need to struggle with generic types.
 */
public interface JmeCreateDeclarationV2CommandListener extends MessageListener<JmeCreateDeclarationV2Command> {
}
