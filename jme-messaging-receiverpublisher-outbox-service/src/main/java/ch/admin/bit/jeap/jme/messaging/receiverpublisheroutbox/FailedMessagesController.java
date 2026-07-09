package ch.admin.bit.jeap.jme.messaging.receiverpublisheroutbox;


import ch.admin.bit.jeap.messaging.transactionaloutbox.outbox.FailedMessage;
import ch.admin.bit.jeap.messaging.transactionaloutbox.outbox.TransactionalOutbox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/failedmessage")
@Transactional(readOnly = true)
class FailedMessagesController {

    private final TransactionalOutbox transactionalOutbox;

    @GetMapping
    List<FailedMessage> listTodaysFailedMessages(@RequestParam(name="resend", required=false) Boolean resendParam) {
        boolean resend = Optional.ofNullable(resendParam).orElse(false);
        return transactionalOutbox.findFailedMessages(getTodaysStart(), getTomorrowsStart(), resend, 100);
    }

    @GetMapping("/count")
    int countTodaysFailedMessages(@RequestParam(name="resend", required=false) Boolean resendParam) {
        boolean resend = Optional.ofNullable(resendParam).orElse(false);
        return transactionalOutbox.countFailedMessages(getTodaysStart(), getTomorrowsStart(), resend);
    }

    @PostMapping("/{id}/resend")
    void resendFailed(@PathVariable long id) {
        transactionalOutbox.resendMessageScheduled(id);
    }

    private ZonedDateTime getTodaysStart() {
        return ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS);
    }

    private ZonedDateTime getTomorrowsStart() {
        return getTodaysStart().plusDays(1);
    }

}
