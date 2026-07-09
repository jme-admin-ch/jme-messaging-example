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
import java.util.Optional;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Covers the transactional outbox example: jme-messaging-receiverpublisher-outbox-service publishes the
 * same event as jme-messaging-receiverpublisher-service, but via the outbox, and skips re-publishing a
 * message it has already handled for a given idempotence id (see CommandListener.receive(Message)).
 */
@Slf4j
class OutboxAndIdempotenceIT extends BootServiceSpringIntegrationTestBase {

    private static final String SENDER_BASE_URL = "http://localhost:8070/jme-messaging-sender-service";
    private static final String RECEIVERPUBLISHER_BASE_URL = "http://localhost:8071/jme-messaging-receiverpublisher-service";
    private static final String RECEIVERPUBLISHER_OUTBOX_BASE_URL = "http://localhost:8079/jme-messaging-receiverpublisher-outbox-service";
    private static final String SUBSCRIBER_BASE_URL = "http://localhost:8074/jme-messaging-subscriber-service";

    @BeforeAll
    static void startServices() throws Exception {
        startService("jme-messaging-receiverpublisher-service", RECEIVERPUBLISHER_BASE_URL);
        startService("jme-messaging-receiverpublisher-outbox-service", RECEIVERPUBLISHER_OUTBOX_BASE_URL);
        startService("jme-messaging-sender-service", SENDER_BASE_URL);
        startService("jme-messaging-subscriber-service", SUBSCRIBER_BASE_URL);

        KafkaConsumerGroupAwaiter.waitForAssignment("jme-messaging-receiverpublisher-service", JmeCreateDeclarationCommand.TypeRef.DEFAULT_TOPIC);
        KafkaConsumerGroupAwaiter.waitForAssignment("jme-messaging-receiverpublisher-outbox-service", JmeCreateDeclarationCommand.TypeRef.DEFAULT_TOPIC);
        KafkaConsumerGroupAwaiter.waitForAssignment("jme-messaging-subscriber-service", JmeDeclarationCreatedEvent.TypeRef.DEFAULT_TOPIC);
    }

    @Test
    void commandIsPublishedViaOutboxAndReceivedBySubscriber() {
        String idempotenceId = UUID.randomUUID().toString();
        String text = "outbox-it-" + idempotenceId;

        String traceId = sendCommand(text, idempotenceId).getString("traceId");

        await().untilAsserted(() -> assertThat(outboxMessageExists(traceId)).isTrue());
        await().untilAsserted(() -> assertThat(outboxMessageStatus(traceId))
                .as("outbox row for traceId %s", traceId)
                .isEqualTo("sent"));

        await().untilAsserted(() -> assertThat(eventsWithTraceId(traceId)).hasSize(2));
        List<Map<String, Object>> events = eventsWithTraceId(traceId);
        assertThat(events).allSatisfy(event -> assertThat(event.get("text")).isEqualTo(text));
    }

    @Test
    void resendingSameIdempotenceIdDoesNotDuplicateOutboxMessage() {
        String idempotenceId = UUID.randomUUID().toString();
        String text1 = "outbox-it-" + idempotenceId + "-first";
        String text2 = "outbox-it-" + idempotenceId + "-second";

        String traceId1 = sendCommand(text1, idempotenceId).getString("traceId");
        await().untilAsserted(() -> assertThat(outboxMessageExists(traceId1)).isTrue());

        String traceId2 = sendCommand(text2, idempotenceId).getString("traceId");
        // The receiverpublisher-outbox-service's CommandListener is annotated @IdempotentMessageHandler,
        // so a second command with the same idempotence id must not create a second outbox entry.
        assertThat(outboxMessageExists(traceId2)).isFalse();

        // The plain (non-idempotent) receiverpublisher-service still republishes on every command, so the
        // subscriber ends up with 2 events for the first command's trace and 1 for the second's.
        await().untilAsserted(() -> assertThat(eventsWithTraceId(traceId1)).hasSize(2));
        assertThat(eventsWithTraceId(traceId2)).hasSize(1);
    }

    private JsonPath sendCommand(String text, String idempotenceId) {
        return given()
                .baseUri(SENDER_BASE_URL)
                .queryParam("text", text)
                .queryParam("idempotenceId", idempotenceId)
                .when()
                .get("/")
                .then()
                .statusCode(200)
                .extract().jsonPath();
    }

    @SuppressWarnings("unchecked")
    private boolean outboxMessageExists(String traceId) {
        return findOutboxMessage(traceId).isPresent();
    }

    @SuppressWarnings("unchecked")
    private String outboxMessageStatus(String traceId) {
        Map<String, Object> message = findOutboxMessage(traceId).orElseThrow();
        if (message.get("failed") != null) {
            return "failed: " + message.get("failReason");
        }
        if (message.get("sentImmediately") != null || message.get("sentScheduled") != null) {
            return "sent";
        }
        return "pending";
    }

    @SuppressWarnings("unchecked")
    private Optional<Map<String, Object>> findOutboxMessage(String traceId) {
        List<Map<String, Object>> outboxMessages = given()
                .baseUri(RECEIVERPUBLISHER_OUTBOX_BASE_URL)
                .when()
                .get("/inspect/outbox")
                .jsonPath().getList("$");
        return outboxMessages.stream().filter(m -> {
            Map<String, Object> traceContext = (Map<String, Object>) m.get("traceContext");
            return traceContext != null && traceId.equals(traceContext.get("traceIdString"));
        }).findFirst();
    }

    private List<Map<String, Object>> eventsWithTraceId(String traceId) {
        return allSubscriberEvents().stream()
                .filter(e -> traceId.equals(e.get("traceId")))
                .toList();
    }

    private List<Map<String, Object>> allSubscriberEvents() {
        return given()
                .baseUri(SUBSCRIBER_BASE_URL)
                .when()
                .get("/events")
                .jsonPath().getList("$");
    }
}
