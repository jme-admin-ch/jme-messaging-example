package ch.admin.bit.jeap.jme.messaging.common.infrastructure;

import ch.admin.bit.jeap.jme.messaging.common.declarationcreatedevent.JmeDeclarationCreatedEventListener;
import ch.admin.bit.jeap.jme.messaging.common.startdeclarationcommand.JmeCreateDeclarationCommandListener;
import ch.admin.bit.jeap.jme.messaging.common.startdeclarationcommand.JmeCreateDeclarationV2CommandListener;
import ch.admin.bit.jeap.messaging.avro.AvroMessage;
import ch.admin.bit.jeap.messaging.avro.AvroMessageKey;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

@AutoConfiguration
public class InfrastructureAutoConfig {

    @Bean
    public KafkaEventPublisher kafkaEventPublisher(KafkaTemplate<AvroMessageKey, AvroMessage> kafkaTemplate, TopicConfiguration topicConfiguration) {
        return new KafkaEventPublisher(kafkaTemplate, topicConfiguration);
    }

    @Bean
    public KafkaCommandSender kafkaCommandSender(KafkaTemplate<AvroMessageKey, AvroMessage> kafkaTemplate, TopicConfiguration topicConfiguration) {
        return new KafkaCommandSender(kafkaTemplate, topicConfiguration);
    }

    @Bean
    @ConditionalOnBean(JmeDeclarationCreatedEventListener.class)
    public KafkaEventConsumer kafkaEventConsumer(List<JmeDeclarationCreatedEventListener> jmeDeclarationCreatedEventListeners) {
        return new KafkaEventConsumer(jmeDeclarationCreatedEventListeners);
    }

    @Bean
    @ConditionalOnBean({JmeCreateDeclarationCommandListener.class, JmeCreateDeclarationV2CommandListener.class})
    public KafkaCommandReceiver kafkaCommandReceiver(List<JmeCreateDeclarationCommandListener> jmeCreateDeclarationCommandListeners,
                                                     List<JmeCreateDeclarationV2CommandListener> jmeCreateDeclarationCommandV2Listeners) {
        return new KafkaCommandReceiver(jmeCreateDeclarationCommandListeners, jmeCreateDeclarationCommandV2Listeners);
    }

}
