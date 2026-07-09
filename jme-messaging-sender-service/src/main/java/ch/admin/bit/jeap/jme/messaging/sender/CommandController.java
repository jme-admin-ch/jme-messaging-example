package ch.admin.bit.jeap.jme.messaging.sender;

import ch.admin.bit.jeap.jme.messaging.common.MessageContext;
import ch.admin.bit.jeap.jme.messaging.common.infrastructure.KafkaCommandSender;
import ch.admin.bit.jeap.jme.messaging.common.startdeclarationcommand.JmeCreateDeclarationCommandBuilder;
import ch.admin.bit.jeap.jme.messaging.common.startdeclarationcommand.JmeCreateDeclarationV2CommandBuilder;
import ch.admin.bit.jeap.messaging.avro.AvroMessageUser;
import ch.admin.bit.jeap.messaging.kafka.tracing.TraceContextProvider;
import ch.admin.bit.jeap.messaging.model.MessageUser;
import ch.admin.bit.jme.declaration.JmeCreateDeclarationCommand;
import ch.admin.bit.jme.declaration.v2.JmeCreateDeclarationV2Command;
import ch.admin.bit.jme.declaration.v2.KeyValue;
import ch.admin.bit.jme.declaration.v2.KeyValueReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * A bean that needs to send commands only needs an injected bean of type {@link KafkaCommandSender}.
 * <p>
 * This example sends a JmeCreateDeclarationCommand when triggered on the rest interface.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
class CommandController {
    private final KafkaCommandSender commandSender;
    private final TraceContextProvider traceContextProvider;

    @GetMapping(path = "/")
    // Should be PUT/POST but GET used to enable easy access of the example in browsers. Don't do this in real services!
    public MessageContext sendCommand(@RequestParam String text, @RequestParam String idempotenceId) {
        JmeCreateDeclarationCommand command = JmeCreateDeclarationCommandBuilder.create()
                .idempotenceId(idempotenceId)
                .user(createUserWithFullData())
                .text(text)
                .build();
        commandSender.send(command);
        return new MessageContext(command.getIdentity().getId(),
                traceContextProvider.getTraceContext().getTraceIdString(),
                command.getPublisher().getService(),
                command.getIdentity().getIdempotenceId(),
                command.getType().getName(),
                command.getCommandVersion(),
                command.getIdentity().getCreatedZoned(),
                text,
                command.getOptionalUser().map(MessageUser::getId).orElse(null));
    }

    private AvroMessageUser createUserWithFullData() {
        return AvroMessageUser.newBuilder()
                .setId("abcd")
                .setGivenName("Hans")
                .setFamilyName("Muster")
                .setBusinessPartnerId("1234")
                .setBusinessPartnerName("ACME GmbH")
                .setPropertiesMap(Map.of("key1", "value1", "key2", "value2"))
                .build();
    }

    @GetMapping(path = "/v2")
    // Should be PUT/POST but GET used to enable easy access of the example in browsers. Don't do this in real services!
    public MessageContext sendV2Command(@RequestParam String text, @RequestParam String idempotenceId) {
        KeyValue keyValue = KeyValue.newBuilder().setKey("foo").setValue("bar").build();
        KeyValueReference keyValueReference = KeyValueReference.newBuilder()
                .setKeyValue(keyValue)
                .setType("keyValue")
                .setText("some text")
                .build();
        JmeCreateDeclarationV2Command command = JmeCreateDeclarationV2CommandBuilder.create()
                .idempotenceId(idempotenceId)
                .text(text)
                .keyValueReference(keyValueReference)
                .build();
        commandSender.send(command);
        return new MessageContext(command.getIdentity().getId(),
                traceContextProvider.getTraceContext().getTraceIdString(),
                command.getPublisher().getService(),
                command.getIdentity().getIdempotenceId(),
                command.getType().getName(),
                command.getCommandVersion(),
                command.getIdentity().getCreatedZoned(),
                text,
                command.getOptionalUser().map(MessageUser::getId).orElse(null));
    }

    @GetMapping(path = "/v1-with-variant")
    // Should be PUT/POST but GET used to enable easy access of the example in browsers. Don't do this in real services!
    public MessageContext sendCommandWithVariant(@RequestParam String text, @RequestParam String idempotenceId) {
        JmeCreateDeclarationCommand command = JmeCreateDeclarationCommandBuilder.create()
                .idempotenceId(idempotenceId)
                .variant("test-variant")
                .user(createUserWithFullData())
                .text(text)
                .build();
        commandSender.send(command);
        return new MessageContext(command.getIdentity().getId(),
                traceContextProvider.getTraceContext().getTraceIdString(),
                command.getPublisher().getService(),
                command.getIdentity().getIdempotenceId(),
                command.getType().getName(),
                command.getCommandVersion(),
                command.getIdentity().getCreatedZoned(),
                text,
                command.getOptionalUser().map(MessageUser::getId).orElse(null));
    }

    @GetMapping(path = "/v2-with-variant")
    // Should be PUT/POST but GET used to enable easy access of the example in browsers. Don't do this in real services!
    public MessageContext sendV2CommandWithVariant(@RequestParam String text, @RequestParam String idempotenceId) {
        KeyValue keyValue = KeyValue.newBuilder().setKey("foo").setValue("bar").build();
        KeyValueReference keyValueReference = KeyValueReference.newBuilder()
                .setKeyValue(keyValue)
                .setType("keyValue")
                .setText("some text")
                .build();
        JmeCreateDeclarationV2Command command = JmeCreateDeclarationV2CommandBuilder.create()
                .idempotenceId(idempotenceId)
                .variant("test-variant")
                .text(text)
                .keyValueReference(keyValueReference)
                .build();
        commandSender.send(command);
        log.info("Send JmeCreateDeclarationV2Command with variant '{}'", command.getType().getVariant());
        return new MessageContext(command.getIdentity().getId(),
                traceContextProvider.getTraceContext().getTraceIdString(),
                command.getPublisher().getService(),
                command.getIdentity().getIdempotenceId(),
                command.getType().getName(),
                command.getCommandVersion(),
                command.getIdentity().getCreatedZoned(),
                text,
                command.getOptionalUser().map(MessageUser::getId).orElse(null));
    }
}
