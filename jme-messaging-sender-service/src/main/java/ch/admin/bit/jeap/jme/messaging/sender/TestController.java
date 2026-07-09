package ch.admin.bit.jeap.jme.messaging.sender;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.health.actuate.endpoint.HealthDescriptor;
import org.springframework.boot.health.actuate.endpoint.HealthEndpoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final HealthEndpoint healthEndpoint;

    /**
     * The standard actuator health endpoint (`/actuator/health`) may not be reachable from outside the cluster.
     * To make the health information accessible for after-deployment smoke tests, this controller exposes it at
     * `/health-test` as a regular REST endpoint delegating to Spring Boot's `HealthEndpoint`.
     */
    @GetMapping("/health-test")

    public HealthDescriptor health() {
        return healthEndpoint.health();
    }
}
