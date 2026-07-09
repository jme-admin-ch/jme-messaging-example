package ch.admin.bit.jeap.jme.messaging.sequentialinbox.inspection;

import ch.admin.bit.jeap.messaging.sequentialinbox.persistence.SequenceInstanceState;
import ch.admin.bit.jeap.messaging.sequentialinbox.persistence.SequencedMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class SequenceInstanceDto {

    private Long id;

    private String name;

    private String contextId;

    private SequenceInstanceState state;

    private ZonedDateTime createdAt;

    private ZonedDateTime closedAt;

    private ZonedDateTime retainUntil;

    private ZonedDateTime removeAfter;

    private List<SequencedMessage> messages;

}
