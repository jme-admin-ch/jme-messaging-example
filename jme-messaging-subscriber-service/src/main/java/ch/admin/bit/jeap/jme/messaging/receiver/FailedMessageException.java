package ch.admin.bit.jeap.jme.messaging.receiver;

import ch.admin.bit.jeap.messaging.avro.errorevent.MessageHandlerExceptionInformation;
import lombok.Getter;

import java.io.PrintWriter;
import java.io.StringWriter;

class FailedMessageException extends RuntimeException implements MessageHandlerExceptionInformation {
    private static final String ERROR_CODE = "FAILED_MESSAGE";

    @Getter
    private final String errorCode;
    @Getter
    private final Temporality temporality;
    @Getter
    private final String description;

    FailedMessageException() {
        super("Received a message with text 'failed'");
        this.errorCode = ERROR_CODE;
        this.temporality = Temporality.PERMANENT;
        this.description = "Events with a message 'failed' cannot be consumed";
    }

    public String getStackTraceAsString() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        this.printStackTrace(pw);
        return sw.toString();
    }
}
