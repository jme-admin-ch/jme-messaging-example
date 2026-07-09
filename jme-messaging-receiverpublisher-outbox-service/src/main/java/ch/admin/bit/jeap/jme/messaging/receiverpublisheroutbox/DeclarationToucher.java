package ch.admin.bit.jeap.jme.messaging.receiverpublisheroutbox;

import ch.admin.bit.jeap.audit.record.create.AuditEventType;
import ch.admin.bit.jeap.audit.record.create.AuditObjectDataRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

/**
 * Scheduled task that "touches" (updates the modifiedAt timestamp) of the oldest declaration
 * in the database to demonstrate the auditing of an internally triggered (scheduled) event.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeclarationToucher {

    private final DeclarationRepository declarationRepository;
    private final Audit audit;

    @Scheduled(cron = "${declaration.touch.schedule}")
    @Transactional
    public void touchOldestDeclaration() {
        log.info("Starting scheduled task to touch oldest declaration");

        declarationRepository.findFirstByOrderByCreatedAtAsc().ifPresentOrElse(
            declaration -> {
                ZonedDateTime oldModifiedAt = declaration.getModifiedAt();
                ZonedDateTime newModifiedAt = ZonedDateTime.now();

                declaration.setModifiedAt(newModifiedAt);
                declarationRepository.save(declaration);

                log.info("Updated declaration {} modifiedAt from {} to {}",
                    declaration.getId(), oldModifiedAt, newModifiedAt);

                auditDeclarationTouched(declaration, oldModifiedAt, newModifiedAt);
            },
            () -> log.debug("No declarations found to touch")
        );
    }

    private void auditDeclarationTouched(Declaration declaration, ZonedDateTime oldModifiedAt, ZonedDateTime newModifiedAt) {
        audit.auditWithInternalTrigger(newModifiedAt.toInstant(), auditBuilder -> {
            // Provide an idempotence id for the audit message. Usually, the idempotence id should be derived from the
            // triggering internal action. Here, we just use the declaration id and the modification timestamp for
            // demonstration purposes.
            auditBuilder.idempotenceId(declaration.getId().toString() + "-" + newModifiedAt);
            auditBuilder.setEventType(AuditEventType.MODIFIED);
            auditBuilder.setAuditObject("Declaration", declaration.getId().toString());
            auditBuilder.addAuditObjectDataValue(AuditObjectDataRole.OLD, "modifiedAt", oldModifiedAt.toString());
            auditBuilder.addAuditObjectDataValue(AuditObjectDataRole.NEW, "modifiedAt", newModifiedAt.toString());
            auditBuilder.setContext("Service touches oldest declaration", null);
        });
    }

}
