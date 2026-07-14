package ch.admin.bit.jeap.jme.messaging.common.infrastructure;


import ch.admin.bit.jme.declaration.JmeCreateDeclarationCommand;
import ch.admin.bit.jme.declaration.JmeDeclarationCreatedEvent;
import ch.admin.bit.jme.declaration.v2.JmeCreateDeclarationV2Command;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.TopicDescription;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * It is possible to automatically create Kafka topics using a class like this. This should only be done
 * in development environments. Here, we limit the automatic creation of topics to non-cloud environments.
 * For cloud environments we simply check if the topics exist.
 */
@Configuration
@Slf4j
public class TopicConfiguration {

    public static final String AUDIT_TOPIC = "protocol-createauditrecord"; // No default topic on CreateAuditRecordCommand

    @Configuration
    @Profile("!test & !local")
    @RequiredArgsConstructor
    @SuppressWarnings({"unused", "findbugs:RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE"})
    private static class TopicConfigurationCloud {
        private final KafkaAdmin kafkaAdmin;
        private final TopicConfiguration topicConfiguration;

        @PostConstruct
        public void checkIfTopicsExist() throws ExecutionException, InterruptedException {
            try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
                List<String> topics = List.of(
                        JmeDeclarationCreatedEvent.TypeRef.DEFAULT_TOPIC,
                        JmeCreateDeclarationCommand.TypeRef.DEFAULT_TOPIC,
                        JmeCreateDeclarationV2Command.TypeRef.DEFAULT_TOPIC,
                        AUDIT_TOPIC);
                log.info("Checking if topics exist: {}", topics);
                Map<String, TopicDescription> stringTopicDescriptionMap = adminClient.describeTopics(topics).allTopicNames().get();
                stringTopicDescriptionMap.forEach((name, desc) -> log.info("{}: {}", name, desc));
                log.info("All topics exist, good to go");
            }
        }
    }
}
