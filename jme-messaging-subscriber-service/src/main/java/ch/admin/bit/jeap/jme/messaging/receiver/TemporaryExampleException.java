package ch.admin.bit.jeap.jme.messaging.receiver;

/**
 * Example of an exception that does not implement
 * {@link ch.admin.bit.jeap.messaging.avro.errorevent.MessageHandlerExceptionInformation} and therefore does not
 * provide a temporality by itself. Its temporality is resolved by the {@link ExampleExceptionTemporalityResolver}.
 */
class TemporaryExampleException extends RuntimeException {

    TemporaryExampleException(String message) {
        super("Received an event with message '" + message + "' which simulates a temporary error");
    }
}
