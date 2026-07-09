package ch.admin.bit.jeap.jme.messaging.selfmessaging;

import ch.admin.bit.jeap.messaging.annotations.JeapMessageConsumerContract;
import ch.admin.bit.jeap.messaging.annotations.JeapMessageProducerContract;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
@JeapMessageConsumerContract(ch.admin.bit.jme.test.v2.JmeBackwardSchemaEvolutionTestEvent.TypeRef.class)
@JeapMessageProducerContract(ch.admin.bit.jme.test.JmeBackwardSchemaEvolutionTestEvent.TypeRef.class)
@JeapMessageProducerContract(ch.admin.bit.jme.test.v2.JmeBackwardSchemaEvolutionTestEvent.TypeRef.class)
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args).getEnvironment();
    }
}
