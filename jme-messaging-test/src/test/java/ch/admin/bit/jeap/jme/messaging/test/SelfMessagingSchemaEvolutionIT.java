package ch.admin.bit.jeap.jme.messaging.test;

import ch.admin.bit.jeap.jme.test.BootServiceSpringIntegrationTestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Covers the message schema evolution example: jme-messaging-self-messaging-service sends and receives
 * both a V1 and a V2 version of the same event, demonstrating backward-compatible schema evolution for a
 * service that sends messages to itself (see README, "Some notes on jme-messaging-self-messaging-service").
 */
@Slf4j
class SelfMessagingSchemaEvolutionIT extends BootServiceSpringIntegrationTestBase {

    private static final String SELF_MESSAGING_BASE_URL = "http://localhost:8076/jme-messaging-self-messaging-service";

    @BeforeAll
    static void startServices() throws Exception {
        startService("jme-messaging-self-messaging-service", SELF_MESSAGING_BASE_URL);

        KafkaConsumerGroupAwaiter.waitForAssignment("jme-messaging-self-messaging-service", "jme-backward-schema-evolution-test-event");
    }

    @Test
    void bothV1AndV2EventsAreConsumedAsV2() {
        String idempotenceIdV1 = UUID.randomUUID().toString();
        String textV1 = "v1-" + idempotenceIdV1;
        String idempotenceIdV2 = UUID.randomUUID().toString();
        String textV2 = "v2-" + idempotenceIdV2;

        sendEvent("v1", textV1, idempotenceIdV1);
        sendEvent("v2", textV2, idempotenceIdV2);

        await().untilAsserted(() -> {
            List<Map<String, Object>> received = receivedEvents();
            assertThat(received).anySatisfy(e -> assertThat(e).containsEntry("idempotenceId", idempotenceIdV1).containsEntry("text", textV1));
            assertThat(received).anySatisfy(e -> assertThat(e).containsEntry("idempotenceId", idempotenceIdV2).containsEntry("text", textV2));
        });
    }

    private void sendEvent(String version, String text, String idempotenceId) {
        given()
                .baseUri(SELF_MESSAGING_BASE_URL)
                .queryParam("text", text)
                .queryParam("idempotenceId", idempotenceId)
                .when()
                .get("/" + version)
                .then()
                .statusCode(200);
    }

    private List<Map<String, Object>> receivedEvents() {
        return given()
                .baseUri(SELF_MESSAGING_BASE_URL)
                .when()
                .get("/list-received-events")
                .jsonPath().getList("$");
    }
}
