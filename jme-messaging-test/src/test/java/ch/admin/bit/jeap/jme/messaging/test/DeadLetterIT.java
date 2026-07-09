package ch.admin.bit.jeap.jme.messaging.test;

import ch.admin.bit.jeap.jme.test.BootServiceSpringIntegrationTestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Covers the dead-letter topic example: jme-messaging-roguesender-service can put a message directly on
 * the dead-letter topic, and jme-messaging-dltsubscriber-service lists everything it has consumed from it.
 */
@Slf4j
class DeadLetterIT extends BootServiceSpringIntegrationTestBase {

    private static final String ROGUESENDER_BASE_URL = "http://localhost:8077/jme-messaging-roguesender-service";
    private static final String DLTSUBSCRIBER_BASE_URL = "http://localhost:8078/jme-messaging-dltsubscriber-service";

    @BeforeAll
    static void startServices() throws Exception {
        startService("jme-messaging-roguesender-service", ROGUESENDER_BASE_URL);
        startService("jme-messaging-dltsubscriber-service", DLTSUBSCRIBER_BASE_URL);
    }

    @Test
    void messageSentDirectlyToDeadLetterTopicIsListedByDltSubscriber() {
        int countBefore = deadLetterCount();

        given()
                .baseUri(ROGUESENDER_BASE_URL)
                .when()
                .get("/mpfe-valid")
                .then()
                .statusCode(200);

        await().untilAsserted(() -> assertThat(deadLetterCount()).isGreaterThan(countBefore));
    }

    private int deadLetterCount() {
        return given()
                .baseUri(DLTSUBSCRIBER_BASE_URL)
                .when()
                .get("/dead-letter")
                .jsonPath().getInt("count");
    }
}
