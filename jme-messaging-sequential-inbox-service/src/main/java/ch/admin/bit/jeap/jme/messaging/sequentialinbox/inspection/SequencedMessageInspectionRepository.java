package ch.admin.bit.jeap.jme.messaging.sequentialinbox.inspection;

import ch.admin.bit.jeap.messaging.sequentialinbox.persistence.SequencedMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface SequencedMessageInspectionRepository extends JpaRepository<SequencedMessage, Long> {

    List<SequencedMessage> findBySequenceInstanceId(long sequenceInstanceId);
}