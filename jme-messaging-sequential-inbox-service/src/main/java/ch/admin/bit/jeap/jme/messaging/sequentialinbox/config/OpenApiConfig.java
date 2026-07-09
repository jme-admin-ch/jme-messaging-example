package ch.admin.bit.jeap.jme.messaging.sequentialinbox.config;

import ch.admin.bit.jeap.messaging.sequentialinbox.api.SequentialInboxController;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "JME Messaging Example - Sequential Inbox Service API",
                contact = @Contact(
                        email = "jEAP-Community@bit.admin.ch",
                        name = "jEAP",
                        url = "https://confluence.eap.bit.admin.ch/display/BLUE/"
                )
        ),
        security = {@SecurityRequirement(name = "OIDC")}
)
@Configuration
public class OpenApiConfig {

    @Bean
    GroupedOpenApi externalApi() {
        return GroupedOpenApi.builder()
                .group("Sequential Inbox")
                .pathsToMatch("/api/**")
                .packagesToScan(SequentialInboxController.class.getPackageName())
                .build();
    }
}
