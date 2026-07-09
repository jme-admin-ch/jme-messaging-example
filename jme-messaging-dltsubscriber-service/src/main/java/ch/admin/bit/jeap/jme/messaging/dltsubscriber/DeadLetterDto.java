package ch.admin.bit.jeap.jme.messaging.dltsubscriber;

import ch.admin.bit.jeap.messaging.avro.errorevent.MessageProcessingFailedEvent;
import lombok.Value;

import java.util.List;

@Value
public class DeadLetterDto {

    int count;

    List<String> events;

}
