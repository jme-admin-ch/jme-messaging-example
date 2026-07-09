package ch.admin.bit.jeap.jme.messaging.test;

import ch.admin.bit.jeap.jme.test.BootServiceSpringIntegrationTestBase;
import ch.admin.bit.jme.declaration.JmeCreateDeclarationCommand;
import ch.admin.bit.jme.declaration.JmeDeclarationCreatedEvent;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Covers the error-handling example: a NullPointerException thrown by the subscriber while consuming
 * the published event is picked up by jme-messaging-error-scs, which stores it and exposes it through
 * its (OAuth-protected) error query API.
 */
@Slf4j
class ErrorHandlingIT extends BootServiceSpringIntegrationTestBase {

    private static final String AUTH_BASE_URL = "http://localhost:8073/jme-messaging-auth-scs";
    private static final String ERROR_SCS_BASE_URL = "http://localhost:8072/error-handling";
    private static final String RECEIVERPUBLISHER_BASE_URL = "http://localhost:8071/jme-messaging-receiverpublisher-service";
    private static final String SENDER_BASE_URL = "http://localhost:8070/jme-messaging-sender-service";
    private static final String SUBSCRIBER_BASE_URL = "http://localhost:8074/jme-messaging-subscriber-service";

    @BeforeAll
    static void startServices() throws Exception {
        startService("jme-messaging-auth-scs", AUTH_BASE_URL);
        startService("jme-messaging-error-scs", ERROR_SCS_BASE_URL);
        startService("jme-messaging-receiverpublisher-service", RECEIVERPUBLISHER_BASE_URL);
        startService("jme-messaging-sender-service", SENDER_BASE_URL);
        startService("jme-messaging-subscriber-service", SUBSCRIBER_BASE_URL);

        KafkaConsumerGroupAwaiter.waitForAssignment("jme-messaging-error-scs", "jme-messageprocessing-failed");
        KafkaConsumerGroupAwaiter.waitForAssignment("jme-messaging-receiverpublisher-service", JmeCreateDeclarationCommand.TypeRef.DEFAULT_TOPIC);
        KafkaConsumerGroupAwaiter.waitForAssignment("jme-messaging-subscriber-service", JmeDeclarationCreatedEvent.TypeRef.DEFAULT_TOPIC);
    }

    @Test
    void nullPointerExceptionDuringConsumptionShowsUpInErrorHandling() {
        String idempotenceId = UUID.randomUUID().toString();

        String traceId = given()
                .baseUri(SENDER_BASE_URL)
                .queryParam("text", "npe")
                .queryParam("idempotenceId", idempotenceId)
                .when()
                .get("/")
                .then()
                .statusCode(200)
                .extract().jsonPath().getString("traceId");

        String accessToken = retrieveAccessToken();

        await().untilAsserted(() -> assertThat(errorCountForTrace(accessToken, traceId)).isEqualTo(1));
    }

    private String retrieveAccessToken() {
        // Client defined in jme-messaging-auth-scs's application-local.yml with role jme_@error_#view
        return RestAssured.given()
                .config(RestAssured.config().encoderConfig(
                        EncoderConfig.encoderConfig().encodeContentTypeAs("x-www-form-urlencoded", ContentType.URLENC)))
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("grant_type", "client_credentials")
                .formParam("client_id", "jme-messaging-it-client")
                .formParam("client_secret", "secret")
                .post(AUTH_BASE_URL + "/oauth2/token")
                .jsonPath().get("access_token");
    }

    private int errorCountForTrace(String accessToken, String traceId) {
        String body = """
                {"dateFrom":"","dateTo":"","eventName":"","traceId":"%s","eventId":"",\
                "stacktracePattern":"","states":null,"sortField":"created","sortOrder":"desc","closingReason":""}\
                """.formatted(traceId);
        return given()
                .baseUri(ERROR_SCS_BASE_URL)
                .auth().oauth2(accessToken)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/error/?pageIndex=0&pageSize=20")
                .jsonPath().getInt("totalErrorCount");
    }
}
