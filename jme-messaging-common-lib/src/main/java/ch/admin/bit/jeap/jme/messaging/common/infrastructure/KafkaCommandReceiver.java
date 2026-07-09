package ch.admin.bit.jeap.jme.messaging.common.infrastructure;

import ch.admin.bit.jeap.jme.messaging.common.startdeclarationcommand.JmeCreateDeclarationCommandListener;
import ch.admin.bit.jeap.jme.messaging.common.startdeclarationcommand.JmeCreateDeclarationV2CommandListener;
import ch.admin.bit.jme.declaration.JmeCreateDeclarationCommand;
import ch.admin.bit.jme.declaration.v2.JmeCreateDeclarationV2Command;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;

/**
 * For each command type you want to consume in your application you have to define a receiver.
 * The receiver is linking the listener beans to the topic. You can also do the acknowledgement and
 * error handling here so that the beans do not have to deal with this.
 * <p>
 * For each topic you need to have one consume method annotated with the {@link KafkaListener} annotation. You can
 * either have multiple such methods in one class or define multiple classes (in the 2nd case the
 * {@link ConditionalOnBean} will be way easier).
 * <p>
 * In the KafkaListener annotation the name of the topic must be static. However, you can use SPEL expressions with
 * variables e.g. ${enviroment}, check {@link TopicConfiguration} for details.
 * <p>
 * This bean should only be loaded if there actually is an implementation of a Listener for this
 * Event, therefore the {@link ConditionalOnBean}.
 * <p>
 * NOTE: If you store this in a common jar, make sure this class is on the search path, e.g. by
 * adding it to spring factories.
 */
@RequiredArgsConstructor
@Slf4j
public class KafkaCommandReceiver {

    private final List<JmeCreateDeclarationCommandListener> jmeCreateDeclarationCommandListeners;
    private final List<JmeCreateDeclarationV2CommandListener> jmeCreateDeclarationCommandV2Listeners;

    @PostConstruct
    void info() {
        log.info("Started listening to commands.");
    }

    @KafkaListener(topics = {JmeCreateDeclarationCommand.TypeRef.DEFAULT_TOPIC})
    public void consume(final JmeCreateDeclarationCommand command, Acknowledgment ack) {
        log.debug("Received JmeCreateDeclarationCommand {}", command.getIdentity().getId());
        jmeCreateDeclarationCommandListeners.forEach(l -> l.receive(command));
        // Acknowledge the command. In case of an error, the error handler will take care of acknowledging the command,
        // after having forwarded the command to the error handling service.
        ack.acknowledge();
        log.debug("Acknowledged JmeCreateDeclarationCommand {}", command.getIdentity().getId());
    }

    @KafkaListener(topics = {JmeCreateDeclarationV2Command.TypeRef.DEFAULT_TOPIC})
    public void consume(final JmeCreateDeclarationV2Command command, Acknowledgment ack) {
        log.debug("Received JmeCreateDeclarationV2Command {}", command.getIdentity().getId());
        jmeCreateDeclarationCommandV2Listeners.forEach(l -> l.receive(command));
        // Acknowledge the command. In case of an error, the error handler will take care of acknowledging the command,
        // after having forwarded the command to the error handling service.
        ack.acknowledge();
        log.debug("Acknowledged JmeCreateDeclarationV2Command {}", command.getIdentity().getId());
    }
}
