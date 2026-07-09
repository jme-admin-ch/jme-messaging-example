package ch.admin.bit.jeap.jme.messaging.sequentialinbox.inspection;

import ch.admin.bit.jeap.messaging.sequentialinbox.persistence.SequenceInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface SequenceInstanceInspectionRepository extends JpaRepository<SequenceInstance, Long> {

    Optional<SequenceInstance> findByNameAndContextId(String name, String contextId);
}