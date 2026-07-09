package ch.admin.bit.jeap.jme.messaging.common;

import java.time.ZonedDateTime;

public record MessageContext(String identityId,
                             String traceId,
                             String publishingService,
                             String idempotenceId,
                             String type,
                             String version,
                             ZonedDateTime created,
                             String text,
                             String userId) {

}
