package ch.admin.bit.jeap.jme.messaging.receiver;

import ch.admin.bit.jeap.messaging.avro.errorevent.MessageHandlerExceptionInformation;
import lombok.Getter;

@Getter
class TemporaryProcessingException extends RuntimeException implements MessageHandlerExceptionInformation {
    private final String errorCode;
    private final Temporality temporality = Temporality.TEMPORARY;
    private final String message;
    private final String description;
    private final String stackTraceAsString;

    TemporaryProcessingException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
        this.stackTraceAsString = null;
        this.description = null;
    }

}
