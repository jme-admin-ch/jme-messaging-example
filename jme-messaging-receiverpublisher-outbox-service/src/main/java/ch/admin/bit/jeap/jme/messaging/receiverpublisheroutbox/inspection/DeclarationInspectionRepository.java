package ch.admin.bit.jeap.jme.messaging.receiverpublisheroutbox.inspection;

import ch.admin.bit.jeap.jme.messaging.receiverpublisheroutbox.Declaration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

interface DeclarationInspectionRepository extends JpaRepository<Declaration, UUID> {

    List<Declaration> findByCreatedAtAfterOrderByCreatedAtDesc(ZonedDateTime createAtAfter);

}
