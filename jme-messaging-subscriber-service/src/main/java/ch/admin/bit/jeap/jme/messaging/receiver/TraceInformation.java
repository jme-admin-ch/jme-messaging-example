package ch.admin.bit.jeap.jme.messaging.receiver;

import lombok.Value;

import java.time.ZonedDateTime;

@Value
public class TraceInformation {

    String message;
    String idempotenceId;
    long traceId;
    String traceIdString;
    ZonedDateTime creationDateTime;

}
