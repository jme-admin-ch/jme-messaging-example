package ch.admin.bit.jeap.jme.messaging.receiverpublisheroutbox;

import ch.admin.bit.jeap.messaging.annotations.JeapMessageConsumerContract;
import ch.admin.bit.jeap.messaging.annotations.JeapMessageProducerContract;
import ch.admin.bit.jme.declaration.JmeCreateDeclarationCommand;
import ch.admin.bit.jme.declaration.JmeDeclarationCreatedEvent;
import ch.admin.bit.jme.declaration.v2.JmeCreateDeclarationV2Command;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.core.Ordered;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Slf4j
@EntityScan
@EnableScheduling
@EnableJpaRepositories
// @Transactional has lowest precedence by default. So does @IdempotentMessageHandler. If those two aspects are put on
// the same method the execution order is (by definition) undefined. The @IdempotentMessageHandler aspect must run after
// the @Transactional aspect in order to work. Therefore we configure the @Transactional aspect to have a higher priority
// than the @IdempotentMessageHandler aspect. (For this example microservice @Transactional seems to be executed before
// @IdempotentMessageHandler even when both have the same precedence, but this might very well just be a coincidence).
@EnableTransactionManagement(order = Ordered.LOWEST_PRECEDENCE - 1)
@SpringBootApplication
@JeapMessageConsumerContract(JmeCreateDeclarationCommand.TypeRef.class)
@JeapMessageConsumerContract(JmeCreateDeclarationV2Command.TypeRef.class)
@JeapMessageProducerContract(JmeDeclarationCreatedEvent.TypeRef.class)
public class Application {
    static void main(String[] args) {
        SpringApplication.run(Application.class, args).getEnvironment();
    }
}
