package ch.admin.bit.jeap.jme.messaging.roguesender;

import ch.admin.bit.jeap.domainevent.avro.AvroDomainEventUser;
import ch.admin.bit.jeap.jme.messaging.common.MessageContext;
import ch.admin.bit.jeap.jme.messaging.common.declarationcreatedevent.JmeDeclarationCreatedEventBuilder;
import ch.admin.bit.jeap.messaging.avro.AvroMessage;
import ch.admin.bit.jeap.messaging.avro.AvroMessageKey;
import ch.admin.bit.jeap.messaging.avro.errorevent.MessageHandlerException;
import ch.admin.bit.jeap.messaging.avro.errorevent.MessageHandlerExceptionInformation;
import ch.admin.bit.jeap.messaging.avro.errorevent.MessageProcessingFailedEvent;
import ch.admin.bit.jeap.messaging.avro.errorevent.MessageProcessingFailedEventBuilder;
import ch.admin.bit.jeap.messaging.kafka.KafkaConfiguration;
import ch.admin.bit.jeap.messaging.kafka.properties.KafkaProperties;
import ch.admin.bit.jeap.messaging.kafka.serde.KafkaAvroSerdeProvider;
import ch.admin.bit.jeap.messaging.kafka.spring.JeapKafkaBeanNames;
import ch.admin.bit.jeap.messaging.kafka.tracing.TraceContextProvider;
import ch.admin.bit.jeap.messaging.model.MessageUser;
import ch.admin.bit.jme.declaration.JmeDeclarationCreatedEvent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping
@Slf4j
class RogueSenderController {

    public static final String TOPIC = "jme-messageprocessing-failed";

    public static final String DLT_TOPIC = "jme-messageprocessing-deadletter";

    private final KafkaConfiguration kafkaConfiguration;
    private final Producer<String, String> kafkaStringProducer;
    private final KafkaTemplate<AvroMessageKey, AvroMessage> kafkaTemplate;
    private final String defaultClusterName;
    private final BeanFactory beanFactory;
    private final KafkaProperties kafkaProperties;
    private final String applicationName;
    private final TraceContextProvider traceContextProvider;

    public RogueSenderController(KafkaConfiguration kafkaConfiguration,
                                 KafkaTemplate<AvroMessageKey, AvroMessage> kafkaTemplate,
                                 KafkaProperties kafkaProperties,
                                 BeanFactory beanFactory, KafkaProperties kafkaProperties1,
                                 @Value("${spring.application.name}") String applicationName,
                                 TraceContextProvider traceContextProvider) {
        this.kafkaConfiguration = kafkaConfiguration;
        this.kafkaTemplate = kafkaTemplate;
        this.defaultClusterName = kafkaProperties.getDefaultClusterName();
        this.kafkaProperties = kafkaProperties1;
        this.applicationName = applicationName;
        this.kafkaStringProducer = createKafkaStringProducer();
        this.beanFactory = beanFactory;
        this.traceContextProvider = traceContextProvider;
    }

    @GetMapping(path = "text")
    public String sendTextMessage() throws ExecutionException, InterruptedException {
        kafkaStringProducer.send(new ProducerRecord<>(TOPIC, "Fake TextMessage")).get();
        final String result = "TextMessage sent to topic " + TOPIC;
        log.info("{}", result);
        return result;
    }

    @GetMapping(path = "avro")
    public String sendAvroMessage() throws ExecutionException, InterruptedException {
        JmeDeclarationCreatedEvent jmeDeclarationCreatedEvent = JmeDeclarationCreatedEventBuilder.create().
                serviceName(applicationName).
                idempotenceId(UUID.randomUUID().toString()).
                message("Fake AvroMessage").
                build();

        kafkaTemplate.send(TOPIC, jmeDeclarationCreatedEvent).get();
        final String result = "AvroMessage sent to topic " + TOPIC;
        log.info("{}", result);
        return result;
    }

    @GetMapping(path = "mpfe")
    public String sendMessageProcessingFailedEvent() throws ExecutionException, InterruptedException {
        MessageHandlerException eventHandleException = MessageHandlerException.builder()
                .description(null)
                .errorCode(MessageHandlerExceptionInformation.StandardErrorCodes.UNKNOWN_EXCEPTION.name())
                .temporality(MessageHandlerExceptionInformation.Temporality.UNKNOWN)
                .build();
        MessageProcessingFailedEvent messageProcessingFailedEvent = MessageProcessingFailedEventBuilder.create()
                .eventHandleException(eventHandleException)
                .serviceName(applicationName)
                .systemName("system")
                .originalMessage(new ConsumerRecord<>("Topic", 1, 2, new byte[]{0, 1}, new byte[]{1, 1}), null)
                .build();

        kafkaTemplate.send(TOPIC, messageProcessingFailedEvent).get();
        final String result = "AvroMessage sent to topic " + TOPIC;
        log.info("{}", result);
        return result;
    }

    @GetMapping(path = "mpfe-valid")
    public String sendValidMessageProcessingFailedEvent() throws ExecutionException, InterruptedException {

        MessageProcessingFailedEvent errorEvent = createEventProcessingFailedEvent("Fake AvroMessage");

        // when
        kafkaTemplate.send(DLT_TOPIC, errorEvent).get();

        final String result = "AvroMessage sent to topic " + DLT_TOPIC;
        log.info("{}", result);
        return result;
    }

    @GetMapping(path = "mpfe-valid/{nbMessages}")
    @SneakyThrows
    public String sendLargeValidMessagesProcessingFailedEvent(@PathVariable("nbMessages") int nbMessages) {

        String content = new String(Files.readAllBytes(Paths.get(ResourceUtils.getFile("classpath:avro-message-content.txt").getPath())));
        // when
        int count = 0;
        while (count < nbMessages) {
            kafkaTemplate.send(DLT_TOPIC, createEventProcessingFailedEvent(content)).get();
            count++;
        }

        final String result = count + " AvroMessages sent to topic " + DLT_TOPIC;
        log.info("{}", result);
        return result;
    }

    @GetMapping(path = "not-signed")
    public MessageContext sendNotSignedDeclarationCreatedEvent() throws ExecutionException, InterruptedException {
        JmeDeclarationCreatedEvent event = JmeDeclarationCreatedEventBuilder.create()
                .serviceName(applicationName)
                .idempotenceId(UUID.randomUUID().toString())
                .message("Not signed example")
                .user(AvroDomainEventUser.newBuilder().setId("rogue").build())
                .build();

        kafkaTemplate.send(JmeDeclarationCreatedEvent.TypeRef.DEFAULT_TOPIC, event).get();

        return new MessageContext(event.getIdentity().getId(),
                traceContextProvider.getTraceContext().getTraceIdString(),
                event.getPublisher().getService(),
                event.getIdentity().getIdempotenceId(),
                event.getType().getName(),
                event.getDomainEventVersion(),
                event.getIdentity().getCreatedZoned(),
                "Not signed example",
                event.getOptionalUser().map(MessageUser::getId).orElse(null));
    }

    private Producer<String, String> createKafkaStringProducer() {
        Map<String, Object> config = kafkaConfiguration.producerConfig(defaultClusterName);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, List.of());
        return new KafkaProducer<>(config);
    }

    private MessageProcessingFailedEvent createEventProcessingFailedEvent(String content) {
        Serializer<Object> avroSerializer = getSerializer(defaultClusterName);
        JmeDeclarationCreatedEvent domainEvent = JmeDeclarationCreatedEventBuilder.create().
                serviceName(applicationName).
                idempotenceId(UUID.randomUUID().toString()).
                message(content).
                build();

        domainEvent.setSerializedMessage(avroSerializer.serialize("Topic", domainEvent));
        ConsumerRecord<?, ?> originalMessage = new ConsumerRecord<>("Topic", 1, 1, null, domainEvent);

        MessageHandlerException eventHandleException = MessageHandlerException.builder()
                .description("description")
                .errorCode(MessageHandlerExceptionInformation.StandardErrorCodes.UNKNOWN_EXCEPTION.name())
                .temporality(MessageHandlerExceptionInformation.Temporality.UNKNOWN)
                .build();

        return MessageProcessingFailedEventBuilder.create()
                .eventHandleException(eventHandleException)
                .serviceName("service")
                .systemName("system")
                .originalMessage(originalMessage, domainEvent)
                .build();
    }

    public Serializer<Object> getSerializer(String clusterName) {
        JeapKafkaBeanNames jeapKafkaBeanNames = new JeapKafkaBeanNames(kafkaProperties.getDefaultClusterName());
        KafkaAvroSerdeProvider kafkaAvroSerdeProvider = (KafkaAvroSerdeProvider)
                beanFactory.getBean(jeapKafkaBeanNames.getKafkaAvroSerdeProviderBeanName(clusterName));
        return kafkaAvroSerdeProvider.getValueSerializer();
    }

}
