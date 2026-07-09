package ch.admin.bit.jeap.jme.messaging.receiverpublisheroutbox.inspection;

import ch.admin.bit.jeap.messaging.transactionaloutbox.outbox.DeferredMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/inspect/outbox")
@Transactional(readOnly = true)
class OutboxInspectionController {

    private final OutboxInspectionRepository outboxInspectionRepository;

    @GetMapping
    List<DeferredMessage> listTodaysOutboxMessages() {
        return outboxInspectionRepository.
                findByCreatedAfterOrderByCreatedDesc(ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)).stream()
                .toList();
    }

}
