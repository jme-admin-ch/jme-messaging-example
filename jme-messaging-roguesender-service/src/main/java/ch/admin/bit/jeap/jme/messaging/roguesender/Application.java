package ch.admin.bit.jeap.jme.messaging.roguesender;

import ch.admin.bit.jeap.messaging.annotations.JeapMessageProducerContract;
import ch.admin.bit.jme.declaration.JmeDeclarationCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
@JeapMessageProducerContract(value = JmeDeclarationCreatedEvent.TypeRef.class, topic = {"jme-messageprocessing-failed", JmeDeclarationCreatedEvent.TypeRef.DEFAULT_TOPIC})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args).getEnvironment();
    }
}
