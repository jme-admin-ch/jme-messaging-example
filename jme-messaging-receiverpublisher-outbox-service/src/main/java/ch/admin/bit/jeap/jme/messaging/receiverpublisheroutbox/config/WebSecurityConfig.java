package ch.admin.bit.jeap.jme.messaging.receiverpublisheroutbox.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;

@Configuration
public class WebSecurityConfig {

    @Bean
    @Order(100)
    SecurityFilterChain securityFilterChainNoAuth(HttpSecurity http) throws Exception {
        // Only request authentication for the '/api/declaration' endpoint, but leave other endpoints unprotected.
        http.securityMatchers(matchers -> matchers.requestMatchers(
                new NegatedRequestMatcher(
                        PathPatternRequestMatcher.withDefaults().matcher("/declaration"))))
            .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                    .anyRequest()
                    .permitAll());
        return http.build();
    }

}
