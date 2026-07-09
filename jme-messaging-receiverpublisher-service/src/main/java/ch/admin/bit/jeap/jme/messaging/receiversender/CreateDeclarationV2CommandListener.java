package ch.admin.bit.jeap.jme.messaging.receiversender;

import ch.admin.bit.jeap.messaging.api.MessageListener;
import ch.admin.bit.jeap.jme.messaging.common.startdeclarationcommand.JmeCreateDeclarationV2CommandListener;
import ch.admin.bit.jme.declaration.v2.JmeCreateDeclarationV2Command;
import ch.admin.bit.jme.declaration.v2.KeyValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * A bean that needs to act on events only needs to implement the {@link MessageListener} interface
 * and will then be informed about the relevant events.
 * If you need more subtle routing (e.g. only some events should be sent to this consumer) you
 * can also implement a more intelligent event consumer.
 */
@Component
@RequiredArgsConstructor
@Slf4j
class CreateDeclarationV2CommandListener implements JmeCreateDeclarationV2CommandListener {

    @Override
    public void receive(JmeCreateDeclarationV2Command command) {
        String text = command.getPayload().getText();
        KeyValue keyValue = command.getReferences().getReference().getKeyValue();

        // To keep the sample project simple, we just log the received information, no need for a JmeDeclarationCreatedEvent
        log.info("Got JmeCreateDeclarationV2Command {} with text '{}', key '{}' and value '{}' and variant '{}'.",
                command.getIdentity().getId(),
                text,
                keyValue.getKey(),
                keyValue.getValue(),
                command.getType().getVariant());
    }

}
