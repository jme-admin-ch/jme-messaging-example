package ch.admin.bit.jeap.jme.messaging.sequentialinbox;

import ch.admin.bit.jeap.messaging.annotations.JeapMessageConsumerContract;
import ch.admin.bit.jme.messaging.event.order.created.JmeOrderCreatedEvent;
import ch.admin.bit.jme.messaging.event.order.prepared.JmeOrderPreparedEvent;
import ch.admin.bit.jme.messaging.event.order.shipped.JmeOrderShippedEvent;
import ch.admin.bit.jme.messaging.event.order.validated.JmeOrderValidatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@Slf4j
@EnableJpaRepositories
@JeapMessageConsumerContract(JmeOrderCreatedEvent.TypeRef.class)
@JeapMessageConsumerContract(JmeOrderShippedEvent.TypeRef.class)
@JeapMessageConsumerContract(JmeOrderValidatedEvent.TypeRef.class)
@JeapMessageConsumerContract(JmeOrderPreparedEvent.TypeRef.class)
public class SequentialInboxApplication {
    public static void main(String[] args) {
        SpringApplication.run(SequentialInboxApplication.class, args).getEnvironment();
    }
}
