package ch.admin.bit.jeap.jme.messaging.sender;

import ch.admin.bit.jeap.messaging.annotations.JeapMessageProducerContract;
import ch.admin.bit.jme.declaration.JmeCreateDeclarationCommand;
import ch.admin.bit.jme.declaration.v2.JmeCreateDeclarationV2Command;
import ch.admin.bit.jme.messaging.event.order.created.JmeOrderCreatedEvent;
import ch.admin.bit.jme.messaging.event.order.prepared.JmeOrderPreparedEvent;
import ch.admin.bit.jme.messaging.event.order.shipped.JmeOrderShippedEvent;
import ch.admin.bit.jme.messaging.event.order.validated.JmeOrderValidatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
@JeapMessageProducerContract(JmeCreateDeclarationCommand.TypeRef.class)
@JeapMessageProducerContract(JmeCreateDeclarationV2Command.TypeRef.class)
@JeapMessageProducerContract(JmeOrderCreatedEvent.TypeRef.class)
@JeapMessageProducerContract(JmeOrderPreparedEvent.TypeRef.class)
@JeapMessageProducerContract(JmeOrderValidatedEvent.TypeRef.class)
@JeapMessageProducerContract(JmeOrderShippedEvent.TypeRef.class)
public class SenderApplication {
    public static void main(String[] args) {
        SpringApplication.run(SenderApplication.class, args).getEnvironment();
    }
}
