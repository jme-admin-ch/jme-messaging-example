package ch.admin.bit.jeap.jme.messaging.sequentialinbox.inspection;

import ch.admin.bit.jeap.messaging.sequentialinbox.persistence.SequenceInstance;
import ch.admin.bit.jeap.messaging.sequentialinbox.persistence.SequencedMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/inspect")
@Transactional(readOnly = true)
public class SequentialInboxInspectionController {

    private final MessageRecorder messageRecorder;
    private final SequenceInstanceInspectionRepository sequenceInstanceInspectionRepository;
    private final SequencedMessageInspectionRepository sequencedMessageInspectionRepository;

    @GetMapping(path = "/reset-recorded-messages")
    void resetRecordedMessages(@RequestParam String contextId) {
        messageRecorder.reset(contextId);
    }

    @GetMapping(path = "/recorded-messages", produces = "application/json")
    List<String> getRecordedMessages(@RequestParam String contextId) {
        return messageRecorder.getRecordedMessages(contextId);
    }

    @GetMapping(path = "/sequence", produces = "application/json")
    ResponseEntity<SequenceInstanceDto> getSequenceByContextId(@RequestParam String contextId) {

        Optional<SequenceInstance> sequenceInstanceOptional = sequenceInstanceInspectionRepository
                .findByNameAndContextId("OrderSequence", contextId);

        if (sequenceInstanceOptional.isPresent()) {
            SequenceInstance sequenceInstance = sequenceInstanceOptional.get();
            List<SequencedMessage> messages = sequencedMessageInspectionRepository.findBySequenceInstanceId(sequenceInstance.getId());

            SequenceInstanceDto dto = new SequenceInstanceDto(
                    sequenceInstance.getId(),
                    sequenceInstance.getName(),
                    sequenceInstance.getContextId(),
                    sequenceInstance.getState(),
                    sequenceInstance.getCreatedAt(),
                    sequenceInstance.getClosedAt(),
                    sequenceInstance.getRetainUntil(),
                    sequenceInstance.getRemoveAfter(),
                    messages
            );

            return ResponseEntity.ok(dto);
        }

        return ResponseEntity.notFound().build();
    }
}
