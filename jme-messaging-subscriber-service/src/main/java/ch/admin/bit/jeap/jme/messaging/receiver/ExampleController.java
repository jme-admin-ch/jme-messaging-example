package ch.admin.bit.jeap.jme.messaging.receiver;

import ch.admin.bit.jeap.jme.messaging.common.MessageContext;
import ch.admin.bit.jeap.jme.messaging.common.declarationcreatedevent.JmeDeclarationCreatedEventListener;
import ch.admin.bit.jeap.messaging.kafka.tracing.TraceContext;
import ch.admin.bit.jeap.messaging.kafka.tracing.TraceContextProvider;
import ch.admin.bit.jeap.messaging.model.MessageUser;
import ch.admin.bit.jme.declaration.DeclarationPayload;
import ch.admin.bit.jme.declaration.JmeDeclarationCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * A bean that needs to act on events only needs to implement the corresponding listener interface
 * and will then be informed about the relevant events.
 * If you need more subtle routing (e.g. only some events should be sent to this consumer) you
 * can also implement a more intelligent event consumer.
 * <p>
 * This example simple buffers the last 50 events and return them via a rest interface.
 */

@RestController
@RequiredArgsConstructor
@Slf4j
class ExampleController implements JmeDeclarationCreatedEventListener {
    private final Queue<MessageContext> events = new CircularFifoQueue<>(50);
    private final Queue<TraceInformation> traceInformation = new CircularFifoQueue<>(50);
    private final DeclarationCreatedRepository repository;
    private final TraceContextProvider traceContextProvider;

    @Override
    public void receive(JmeDeclarationCreatedEvent event) {
        final String message = event.getPayload().getMessage();
        TraceContext traceContext = traceContextProvider.getTraceContext();
        log.info("Got test event '{}' from '{}' with message '{}', traceId '{}', traceId string '{}' and user '{}'.",
                event.getIdentity().getEventId(), event.getPublisher().getService(), message,
                traceContext.getTraceId(), traceContext.getTraceIdString(), event.getUser());
        synchronized (traceInformation) {
            traceInformation.add(new TraceInformation(message, event.getIdentity().getIdempotenceId(), traceContext.getTraceId(), traceContext.getTraceIdString(), ZonedDateTime.now()));
        }

        switch (message) {
            case "fail" -> throw new FailedMessageException();
            case "npe" -> throw new NullPointerException();
            case "temp100" -> {
                log.info("Processing of {} failed.", message);
                throw new TemporaryProcessingException("999", "Something went wrong.");
            }
            case "temp" -> {
                if (processingFailed()) {
                    log.info("Processing of {} failed.", message);
                    throw new TemporaryProcessingException("999", "Something went wrong.");
                } else {
                    log.info("Processing of {} succeeded.", message);
                }
            }
            default -> {
                log.info("Consuming test message without error: {}", message);
                if (!repository.declarationExistsByIdempotenceId(event.getIdentity().getIdempotenceId())) {
                    log.info("Saving new declaration for idempotence ID {}", event.getIdentity().getIdempotenceId());
                    repository.save(event.getIdentity().getIdempotenceId(), event.getPayload());
                } else {
                    log.info("Declaration for idempotence ID {} already exists", event.getIdentity().getIdempotenceId());
                    // To achieve idempotent behaviour, the persistent state is not altered here as this has already
                    // been done by a previous attempt to consume the event.
                }
            }

            // Follow-up events resulting from the received event would be published regardless of whether the entity
            // exists in the repository or not, to make sure they are produced and haven't been skipped due to errors
            // by the previous attempt to consume the event. As downstream consumers will be idempotent as well it
            // is no issue if the event should be produced twice.
        }

        // Note: This is not idempotent and only serves the purpose of tracing all received events, even if consumed
        // multiple times
        synchronized (events) {
            MessageContext messageContext = new MessageContext(
                    event.getIdentity().getId(), traceContext.getTraceIdString(), event.getPublisher().getService(),
                    event.getIdentity().getIdempotenceId(), event.getType().getName(), event.getType().getVersion(),
                    event.getIdentity().getCreatedZoned(), event.getPayload().getMessage(),
                    event.getOptionalUser().map(MessageUser::getId).orElse(null));
            log.info("Saving new message context {}", messageContext);
            events.add(messageContext);
        }
    }

    @GetMapping("/events")
    public List<MessageContext> getEvents() {
        synchronized (events) {
            return new ArrayList<>(events);
        }
    }

    @GetMapping("/traceInformation")
    public String getTraceInformation() {
        StringBuilder sb = new StringBuilder();
        synchronized (traceInformation) {
            for (TraceInformation traceInfo : traceInformation) {
                sb.append(traceInfo);
                sb.append("<br>");
            }
        }
        return sb.toString();
    }

    @GetMapping("/payloads")
    public List<String> getPayloads() {
        return repository.getDeclarations().stream()
                .map(DeclarationPayload::getMessage)
                .toList();
    }

    private boolean processingFailed() {
        // let about 66% of the executions fail
        return (LocalTime.now().toNanoOfDay() % 3) <= 1;
    }
}
