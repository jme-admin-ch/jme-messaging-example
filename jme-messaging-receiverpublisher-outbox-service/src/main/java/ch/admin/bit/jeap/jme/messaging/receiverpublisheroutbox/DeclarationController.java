package ch.admin.bit.jeap.jme.messaging.receiverpublisheroutbox;


import ch.admin.bit.jeap.audit.record.create.AuditEventType;
import ch.admin.bit.jeap.audit.record.create.AuditObjectDataRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/declaration")
class DeclarationController {

    private final DeclarationService declarationService;
    private final Audit audit;

    @PostMapping
    @Transactional // Ensure the creation of the declaration and the sending of the audit command happen in the same transaction
    @PreAuthorize("hasRole('declaration', 'write')")
    void createDeclaration(@RequestBody String text) {
        log.info("Got a REST request to create a declaration with text '{}'.",text);
        Declaration declaration = declarationService.createDeclaration(text);
        auditDeclarationCreated(declaration);
    }

    // Audit the creation of a declaration triggered by a user in a UI
    void auditDeclarationCreated(Declaration declaration) {
        // The triggering user will be taken from the current security context automatically
        audit.auditWithUserTrigger(declaration.getCreatedAt().toInstant(), auditBuilder -> {
            // Provide an idempotence id for the audit message. Usually, the idempotence id should be derived from the
            // triggering user action. Here, we just use the declaration id for demonstration purposes.
            auditBuilder.idempotenceId(declaration.getId().toString());
            auditBuilder.setEventType(AuditEventType.CREATED);
            auditBuilder.setAuditObject("Declaration", declaration.getId().toString());
            auditBuilder.addAuditObjectDataValue(AuditObjectDataRole.NEW, "text", declaration.getText());
            auditBuilder.setContext("User creates declaration", UUID.randomUUID().toString());
        });

    }

}
