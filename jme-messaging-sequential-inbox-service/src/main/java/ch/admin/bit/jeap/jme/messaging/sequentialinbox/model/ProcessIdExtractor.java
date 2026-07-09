package ch.admin.bit.jeap.jme.messaging.sequentialinbox.model;

import ch.admin.bit.jeap.messaging.avro.AvroMessage;
import ch.admin.bit.jeap.messaging.sequentialinbox.configuration.model.ContextIdExtractor;

/**
 * Example of a ContextIdExtractor for the sequential inbox.
 */
public class ProcessIdExtractor implements ContextIdExtractor<AvroMessage> {

    @Override
    public String extractContextId(AvroMessage message) {
        return message.getOptionalProcessId().orElse(null);
    }
}
