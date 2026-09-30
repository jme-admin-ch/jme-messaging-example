package ch.admin.bit.jeap.jme.messaging.receiver;

import ch.admin.bit.jeap.messaging.avro.errorevent.MessageHandlerExceptionInformation.Temporality;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.ResourceAccessException;

import static org.assertj.core.api.Assertions.assertThat;

class ExampleExceptionTemporalityResolverTest {

    private final ExampleExceptionTemporalityResolver resolver = new ExampleExceptionTemporalityResolver();

    @Test
    void resolve_temporaryExampleException_isTemporary() {
        assertThat(resolver.resolve(new TemporaryExampleException("temporary-example")))
                .contains(Temporality.TEMPORARY);
    }

    @Test
    void resolve_wrappedTemporaryExampleException_isTemporary() {
        assertThat(resolver.resolve(new IllegalStateException("wrapper", new TemporaryExampleException("temporary-example"))))
                .contains(Temporality.TEMPORARY);
    }

    @Test
    void resolve_exceptionCoveredByJeapDefaults_isTemporary() {
        assertThat(resolver.resolve(new ResourceAccessException("I/O error")))
                .contains(Temporality.TEMPORARY);
    }

    @Test
    void resolve_unknownException_isEmpty() {
        assertThat(resolver.resolve(new NullPointerException()))
                .isEmpty();
    }
}
