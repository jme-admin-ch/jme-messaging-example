package ch.admin.bit.jeap.jme.messaging.receiverpublisheroutbox;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface DeclarationRepository extends JpaRepository<Declaration, UUID> {
    Optional<Declaration> findFirstByOrderByCreatedAtAsc();
}
