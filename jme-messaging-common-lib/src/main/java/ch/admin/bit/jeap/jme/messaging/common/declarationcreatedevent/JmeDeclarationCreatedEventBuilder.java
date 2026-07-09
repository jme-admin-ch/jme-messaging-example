package ch.admin.bit.jeap.jme.messaging.common.declarationcreatedevent;


import ch.admin.bit.jeap.domainevent.avro.AvroDomainEventBuilder;
import ch.admin.bit.jeap.messaging.avro.AvroMessageBuilderException;
import ch.admin.bit.jme.declaration.DeclarationPayload;
import ch.admin.bit.jme.declaration.DeclarationReferences;
import ch.admin.bit.jme.declaration.JmeDeclarationCreatedEvent;
import lombok.Getter;
import lombok.Setter;

/**
 * For each custom event a builder class should be created to make it easy to create event instances.
 * You can extend {@link AvroDomainEventBuilder} to set up such a builder in a simple way:
 * - Define the "static" data like the event's name etc. This data has to be provided by implementing the
 * corresponding abstract methods of {@link AvroDomainEventBuilder}. Here we do this using "private final" fields
 * and the Getter Lombok annotation. Sonar will complain that the data fields could be static,
 * we suppress this rule here.
 * - Define additional builder methods (here e.g. message()) for providing the data that will populate the
 * payload and references of the event.
 * - Implement the self() method that returns this builder instance
 * - Overwrite the build method to create the payload and references objects from the data provided by
 * the builder methods. Call setReferences() and setPayload() to hand them over to the base class and
 * then call the build() method of the base class.
 */
@Getter
@SuppressWarnings("findbugs:SS_SHOULD_BE_STATIC")
public class JmeDeclarationCreatedEventBuilder extends AvroDomainEventBuilder<JmeDeclarationCreatedEventBuilder, JmeDeclarationCreatedEvent> {
    private final String systemName = "JME";
    private final String eventName = "JmeDeclarationCreatedEvent";
    private String serviceName = "jeap-microservice-examples-kafka";

    // Additional data needed to build the event message.
    // This data should correspond to the builder methods (see below).
    private String message;

    // We do not want a client of this class to use the constructor but rather a static factory
    // method. But this is really up to taste and not necessarily needed
    private JmeDeclarationCreatedEventBuilder() {
        super(JmeDeclarationCreatedEvent::new);
    }

    public static JmeDeclarationCreatedEventBuilder create() {
        return new JmeDeclarationCreatedEventBuilder();
    }

    //Define the additional builder methods. The interface of those methods should really be
    //driven by the producer, e.g put only the data here is the producer will have it, and not driven
    //by the implementation in the event. E.g. here the event is defined to contain the namnespace and
    //the name of the sending bean. For the produces this distinction makes no sense, its only a Java-Class
    //therefore we have a builder method that takes a java class, not a name and a namespace. This way we
    //can hide the actual implementation of the event from the producer.
    public JmeDeclarationCreatedEventBuilder message(String message) {
        this.message = message;
        return this;
    }

    public JmeDeclarationCreatedEventBuilder serviceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    @Override
    protected JmeDeclarationCreatedEventBuilder self() {
        return this;
    }

    // The build method has to assemble all the data from the builder methods into the payload
    // and references objects.
    @Override
    public JmeDeclarationCreatedEvent build() {
        if (this.message == null) {
            throw AvroMessageBuilderException.propertyNull("jmeDeclarationCreatedEventReferences.message");
        }
        DeclarationReferences declarationReferences = DeclarationReferences.newBuilder()
                .build();
        DeclarationPayload declarationPayload = DeclarationPayload.newBuilder()
                .setMessage(message)
                .build();
        setReferences(declarationReferences);
        setPayload(declarationPayload);
        return super.build();
    }
}
