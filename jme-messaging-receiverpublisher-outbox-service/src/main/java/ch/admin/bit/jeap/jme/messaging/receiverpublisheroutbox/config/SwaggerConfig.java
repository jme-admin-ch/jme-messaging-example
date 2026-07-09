package ch.admin.bit.jeap.jme.messaging.receiverpublisheroutbox.config;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "JME messaging example receiver-publisher-outbox-service API",
                contact = @Contact(
                        email = "jEAP-Community@bit.admin.ch",
                        name = "jEAP",
                        url = "https://confluence.eap.bit.admin.ch/display/BLUE/"
                )
        ),
        externalDocs = @ExternalDocumentation(
                url = "https://confluence.eap.bit.admin.ch/display/JEAP/Blueprint+Microservices",
                description = "Blueprint Microservices in Confluence"),
        security = {@SecurityRequirement(name = "OIDC")}
)
@Configuration
public class SwaggerConfig {

    @Bean
    GroupedOpenApi testAndInspectApi() {
        return GroupedOpenApi.builder()
                .group("Outbox Test and Inspection API")
                .pathsToMatch("/declaration/**", "/inspect/**")
                .build();
    }

}
