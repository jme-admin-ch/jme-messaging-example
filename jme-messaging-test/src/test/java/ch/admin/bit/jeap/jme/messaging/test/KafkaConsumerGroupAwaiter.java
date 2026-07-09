package ch.admin.bit.jeap.jme.messaging.test;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ConsumerGroupDescription;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

final class KafkaConsumerGroupAwaiter {

    private static final String BOOTSTRAP_SERVERS = System.getenv("CI") != null ? "broker:29092" : "localhost:9092";

    private KafkaConsumerGroupAwaiter() {
    }

    static void waitForAssignment(String groupId, String topic) {
        try (AdminClient adminClient = AdminClient.create(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS))) {
            await().atMost(Duration.ofSeconds(30)).untilAsserted(() -> {
                boolean assigned;
                try {
                    ConsumerGroupDescription description = adminClient.describeConsumerGroups(List.of(groupId))
                            .describedGroups().get(groupId).get();
                    assigned = description.members().stream()
                            .flatMap(member -> member.assignment().topicPartitions().stream())
                            .anyMatch(topicPartition -> topicPartition.topic().equals(topic));
                } catch (Exception e) {
                    assigned = false;
                }
                assertThat(assigned).as("consumer group %s assigned to topic %s", groupId, topic).isTrue();
            });
        }
    }
}
