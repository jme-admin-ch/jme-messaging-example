package ch.admin.bit.jeap.jme.messaging.receiver;

import ch.admin.bit.jme.declaration.DeclarationPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Poor man's in-memory repository implementation
 */
@Component
@Slf4j
class DeclarationCreatedRepository {
    private final Map<String, DeclarationPayload> declarationsByIdempotenceId = new HashMap<>();

    synchronized boolean declarationExistsByIdempotenceId(String idempotenceId) {
        return declarationsByIdempotenceId.containsKey(idempotenceId);
    }

    synchronized void save(String idempotenceId, DeclarationPayload declarationPayload) {
        declarationsByIdempotenceId.put(idempotenceId, declarationPayload);
    }

    synchronized List<DeclarationPayload> getDeclarations() {
        return List.copyOf(declarationsByIdempotenceId.values());
    }
}
