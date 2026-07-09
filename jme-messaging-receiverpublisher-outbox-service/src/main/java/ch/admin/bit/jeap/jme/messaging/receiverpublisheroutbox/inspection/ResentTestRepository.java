package ch.admin.bit.jeap.jme.messaging.receiverpublisheroutbox.inspection;

import ch.admin.bit.jeap.jme.messaging.common.MessageContext;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Queue;

@Repository
public class ResentTestRepository {

    private final Queue<MessageContext> messages = new CircularFifoQueue<>(50);

    public void addMessage(MessageContext messageContext) {
        messages.add(messageContext);
    }

    List<MessageContext> getMessages() {
        return List.copyOf(messages);
    }
}
