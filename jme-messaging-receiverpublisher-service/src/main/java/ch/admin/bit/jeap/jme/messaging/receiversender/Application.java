package ch.admin.bit.jeap.jme.messaging.receiversender;

import ch.admin.bit.jeap.messaging.annotations.JeapMessageConsumerContract;
import ch.admin.bit.jeap.messaging.annotations.JeapMessageProducerContract;
import ch.admin.bit.jme.declaration.JmeCreateDeclarationCommand;
import ch.admin.bit.jme.declaration.JmeDeclarationCreatedEvent;
import ch.admin.bit.jme.declaration.v2.JmeCreateDeclarationV2Command;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
@JeapMessageConsumerContract(JmeCreateDeclarationCommand.TypeRef.class)
@JeapMessageConsumerContract(JmeCreateDeclarationV2Command.TypeRef.class)
@JeapMessageProducerContract(JmeDeclarationCreatedEvent.TypeRef.class)
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
