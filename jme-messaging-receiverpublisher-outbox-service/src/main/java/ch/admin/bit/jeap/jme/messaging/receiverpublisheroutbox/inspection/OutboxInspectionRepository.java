package ch.admin.bit.jeap.jme.messaging.receiverpublisheroutbox.inspection;

import ch.admin.bit.jeap.messaging.transactionaloutbox.outbox.DeferredMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;

interface OutboxInspectionRepository extends JpaRepository<DeferredMessage, Long> {

    List<DeferredMessage> findByCreatedAfterOrderByCreatedDesc(ZonedDateTime createdAfter);

}
