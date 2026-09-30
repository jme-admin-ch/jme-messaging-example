package ch.admin.bit.jeap.jme.messaging.receiver;

import ch.admin.bit.jeap.messaging.avro.errorevent.MessageHandlerExceptionInformation.Temporality;
import ch.admin.bit.jeap.messaging.kafka.errorhandling.DefaultExceptionTemporalityResolver;
import ch.admin.bit.jeap.messaging.kafka.errorhandling.ExceptionTemporalityResolver;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Application-specific {@link ExceptionTemporalityResolver} replacing the jEAP default bean. It classifies
 * {@link TemporaryExampleException} as {@link Temporality#TEMPORARY} and delegates to the
 * {@link DefaultExceptionTemporalityResolver} for all other exceptions to keep the jEAP defaults.
 */
@Component
class ExampleExceptionTemporalityResolver implements ExceptionTemporalityResolver {

    private final DefaultExceptionTemporalityResolver defaultResolver = new DefaultExceptionTemporalityResolver();

    @Override
    public Optional<Temporality> resolve(Throwable exception) {
        for (Throwable current = exception; current != null; current = current.getCause()) {
            if (current instanceof TemporaryExampleException) {
                return Optional.of(Temporality.TEMPORARY);
            }
        }
        return defaultResolver.resolve(exception);
    }
}
