package ch.admin.bit.jeap.jme.messaging.sequentialinbox.inspection;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageRecorder {

    private final Map<String, List<String>> recordedMessages = new HashMap<>();

    public void reset(String contextId) {
        recordedMessages.remove(contextId);
    }

    public synchronized void recordMessage(String contextId, String type) {
        log.debug("Message from type {} and contextId {} handled in listener", type, contextId);

        if (!recordedMessages.containsKey(contextId)) {
            recordedMessages.put(contextId, new ArrayList<>());
        }

        recordedMessages.get(contextId).add(type);
    }

    public List<String> getRecordedMessages(String contextId) {
        if (!recordedMessages.containsKey(contextId)) {
            return Collections.emptyList();
        }
        return recordedMessages.get(contextId);
    }

}
