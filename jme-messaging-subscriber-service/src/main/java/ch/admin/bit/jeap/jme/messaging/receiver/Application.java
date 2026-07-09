package ch.admin.bit.jeap.jme.messaging.receiver;

import ch.admin.bit.jeap.messaging.annotations.JeapMessageConsumerContract;
import ch.admin.bit.jme.declaration.JmeDeclarationCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
@JeapMessageConsumerContract(JmeDeclarationCreatedEvent.TypeRef.class)
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args).getEnvironment();
    }
}
