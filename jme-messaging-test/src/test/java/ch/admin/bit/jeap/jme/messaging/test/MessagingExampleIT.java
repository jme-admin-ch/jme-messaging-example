package ch.admin.bit.jeap.jme.messaging.test;

import ch.admin.bit.jeap.jme.test.BootServiceSpringIntegrationTestBase;
import ch.admin.bit.jme.declaration.JmeCreateDeclarationCommand;
import ch.admin.bit.jme.declaration.JmeDeclarationCreatedEvent;
import io.restassured.path.json.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Slf4j
class MessagingExampleIT extends BootServiceSpringIntegrationTestBase {

    private static final String SENDER_BASE_URL = "http://localhost:8070/jme-messaging-sender-service";
    private static final String RECEIVERPUBLISHER_BASE_URL = "http://localhost:8071/jme-messaging-receiverpublisher-service";
    private static final String SUBSCRIBER_BASE_URL = "http://localhost:8074/jme-messaging-subscriber-service";

    @BeforeAll
    static void startServices() throws Exception {
        startService("jme-messaging-receiverpublisher-service", RECEIVERPUBLISHER_BASE_URL);
        startService("jme-messaging-sender-service", SENDER_BASE_URL);
        startService("jme-messaging-subscriber-service", SUBSCRIBER_BASE_URL);

        KafkaConsumerGroupAwaiter.waitForAssignment("jme-messaging-receiverpublisher-service", JmeCreateDeclarationCommand.TypeRef.DEFAULT_TOPIC);
        KafkaConsumerGroupAwaiter.waitForAssignment("jme-messaging-subscriber-service", JmeDeclarationCreatedEvent.TypeRef.DEFAULT_TOPIC);
    }

    @Test
    void commandSentBySenderIsPublishedAsEventAndReceivedBySubscriber() {
        String idempotenceId = UUID.randomUUID().toString();
        String text = "it-test-" + idempotenceId;

        given()
                .baseUri(SENDER_BASE_URL)
                .queryParam("text", text)
                .queryParam("idempotenceId", idempotenceId)
                .when()
                .get("/")
                .then()
                .statusCode(200);

        await().untilAsserted(() -> assertThat(findEventByIdempotenceId(idempotenceId)).isPresent());

        Map<String, Object> event = findEventByIdempotenceId(idempotenceId).orElseThrow();
        assertThat(event.get("text")).isEqualTo(text);
    }

    private java.util.Optional<Map<String, Object>> findEventByIdempotenceId(String idempotenceId) {
        JsonPath jsonPath = given()
                .baseUri(SUBSCRIBER_BASE_URL)
                .when()
                .get("/events")
                .jsonPath();
        List<Map<String, Object>> events = jsonPath.getList("$");
        return events.stream()
                .filter(e -> idempotenceId.equals(e.get("idempotenceId")))
                .findFirst();
    }
}
