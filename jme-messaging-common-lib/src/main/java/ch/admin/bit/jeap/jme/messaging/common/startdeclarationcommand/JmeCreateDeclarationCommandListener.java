package ch.admin.bit.jeap.jme.messaging.common.startdeclarationcommand;

import ch.admin.bit.jeap.jme.messaging.common.infrastructure.KafkaCommandReceiver;
import ch.admin.bit.jeap.messaging.api.MessageListener;
import ch.admin.bit.jme.declaration.JmeCreateDeclarationCommand;

/**
 * Base class for all listeners that are interested in receiving JmeCreateDeclarationCommand commands.
 * Such an interface might not strictly be needed as there is the generic {@link MessageListener} interface, but it
 * makes life easier especially in {@link KafkaCommandReceiver} as we do not need to struggle with generic types.
 */
public interface JmeCreateDeclarationCommandListener extends MessageListener<JmeCreateDeclarationCommand> {
}
