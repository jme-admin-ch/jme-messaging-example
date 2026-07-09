package ch.admin.bit.jeap.jme.messaging.receiverpublisheroutbox;

import ch.admin.bit.jeap.jme.messaging.common.startdeclarationcommand.JmeCreateDeclarationV2CommandListener;
import ch.admin.bit.jeap.messaging.idempotence.messagehandler.IdempotentMessageHandler;
import ch.admin.bit.jme.declaration.v2.JmeCreateDeclarationV2Command;
import ch.admin.bit.jme.declaration.v2.KeyValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
class CreateDeclarationV2CommandListener implements JmeCreateDeclarationV2CommandListener {

    // An idempotent message handler method execution must be part of a transaction. The transaction could have been started
    // prior to the call to the message handler or as in this example can be started with the method handler execution. If you
    // put the @Transactional and @IdempotentMessageHandler annotation on the same method see the comments in Application.
    @Transactional
    // The receive method will only be executed for a message (here: a command) if the message has not yet been processed
    // by the method in a successful surrounding transaction before. Messages are considered to be the same if they have
    // the same idempotence id and the same message type.
    @IdempotentMessageHandler
    @Override
    public void receive(JmeCreateDeclarationV2Command command) {
        String text = command.getPayload().getText();
        KeyValue keyValue = command.getReferences().getReference().getKeyValue();

        // To keep the sample project simple, we just log the received information, no need for a JmeDeclarationCreatedEvent
        log.info("Got JmeCreateDeclarationV2Command {} with text '{}', key '{}' and value '{}'.", command.getIdentity().getId(), text, keyValue.getKey(), keyValue.getValue());
    }

}
