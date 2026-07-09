package ch.admin.bit.jeap.jme.messaging.test;

import ch.admin.bit.jeap.jme.test.BootServiceSpringIntegrationTestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Covers the sequential inbox example (see README_sequential_inbox.md): JmeOrderShippedEvent depends on
 * both JmeOrderPreparedEvent and JmeOrderValidatedEvent (which itself is sent twice, for two different
 * validation types), so the sequence should only close once all of them have arrived, regardless of the
 * order they're sent in.
 */
@Slf4j
class SequentialInboxIT extends BootServiceSpringIntegrationTestBase {

    private static final String SENDER_BASE_URL = "http://localhost:8070/jme-messaging-sender-service";
    private static final String SEQUENTIAL_INBOX_BASE_URL = "http://localhost:8089/jme-messaging-sequential-inbox-service";

    @BeforeAll
    static void startServices() throws Exception {
        startService("jme-messaging-sender-service", SENDER_BASE_URL);
        startService("jme-messaging-sequential-inbox-service", SEQUENTIAL_INBOX_BASE_URL);
    }

    @Test
    void sequenceClosesOnlyAfterAllDependentEventsArrived() {
        String orderId = UUID.randomUUID().toString();

        assertThat(sequenceInstanceStatusCode(orderId)).isEqualTo(404);

        sendOrderEvent("created", orderId);
        await().untilAsserted(() -> assertThat(sequenceInstanceStatusCode(orderId)).isEqualTo(200));
        assertThat(sequenceState(orderId)).isEqualTo("OPEN");

        // Send the dependent events out of order: shipped depends on prepared AND validated, so the
        // sequence must only close once all of them have been received, not just after shipped arrives.
        sendOrderEvent("shipped", orderId);
        sendOrderEvent("prepared", orderId);
        sendOrderEvent("validated", orderId, "validationType", "STOCK_AVAILABLE");
        sendOrderEvent("validated", orderId, "validationType", "CUSTOMER_CREDIT_CHECKED");

        await().untilAsserted(() -> assertThat(sequenceState(orderId)).isEqualTo("CLOSED"));

        List<Object> recordedMessages = given()
                .baseUri(SEQUENTIAL_INBOX_BASE_URL)
                .queryParam("contextId", orderId)
                .when()
                .get("/inspect/recorded-messages")
                .jsonPath().getList("$");
        // created, shipped, prepared, validated x2
        assertThat(recordedMessages).hasSize(5);
    }

    private void sendOrderEvent(String type, String orderId, String... extraQueryParamNameValue) {
        var request = given()
                .baseUri(SENDER_BASE_URL)
                .queryParam("orderId", orderId);
        for (int i = 0; i < extraQueryParamNameValue.length; i += 2) {
            request = request.queryParam(extraQueryParamNameValue[i], extraQueryParamNameValue[i + 1]);
        }
        request.when()
                .get("/send-order-events/" + type)
                .then()
                .statusCode(200);
    }

    private int sequenceInstanceStatusCode(String orderId) {
        return given()
                .baseUri(SEQUENTIAL_INBOX_BASE_URL)
                .queryParam("contextId", orderId)
                .when()
                .get("/inspect/sequence")
                .statusCode();
    }

    private String sequenceState(String orderId) {
        return given()
                .baseUri(SEQUENTIAL_INBOX_BASE_URL)
                .queryParam("contextId", orderId)
                .when()
                .get("/inspect/sequence")
                .jsonPath().getString("state");
    }
}
