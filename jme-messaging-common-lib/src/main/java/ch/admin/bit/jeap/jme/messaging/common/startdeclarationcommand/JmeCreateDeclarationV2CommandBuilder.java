package ch.admin.bit.jeap.jme.messaging.common.startdeclarationcommand;

import ch.admin.bit.jeap.command.avro.AvroCommandBuilder;
import ch.admin.bit.jeap.domainevent.avro.AvroDomainEventBuilder;
import ch.admin.bit.jeap.messaging.avro.AvroMessageBuilderException;
import ch.admin.bit.jme.declaration.v2.CreateDeclarationPayload;
import ch.admin.bit.jme.declaration.v2.CreateDeclarationReferences;
import ch.admin.bit.jme.declaration.v2.JmeCreateDeclarationV2Command;
import ch.admin.bit.jme.declaration.v2.KeyValueReference;
import lombok.Getter;

/**
 * For each custom command a builder class should be created to make it easy to create command instances.
 * You can extend {@link AvroCommandBuilder} to set up such a builder in a simple way:
 * - Define the "static" data like the command's name etc. This data has to be provided by implementing the
 * corresponding abstract methods of {@link AvroDomainEventBuilder}. Here we do this using "private final" fields
 * and the Getter Lombok annotation. Sonar will complain that the data fields could be static,
 * we suppress this rule here.
 * - Define additional builder methods (here e.g. text()) for providing the data that will populate the
 * payload and references of the command.
 * - Implement the self() method that returns this builder instance
 * - Overwrite the build method to create the payload and references objects from the data provided by
 * the builder methods. Call setReferences() and setPayload() to hand them over to the base class and
 * then call the build() method of the base class.
 */
@Getter
@SuppressWarnings("findbugs:SS_SHOULD_BE_STATIC")
public class JmeCreateDeclarationV2CommandBuilder extends AvroCommandBuilder<JmeCreateDeclarationV2CommandBuilder, JmeCreateDeclarationV2Command> {
    private final String systemName = "JME";
    private final String serviceName = "jme-messaging-sender-service";
    private final String commandName = "JmeCreateDeclarationV2Command";

    // Additional data needed to build the command message.
    // This data should correspond to the builder methods (see below).
    private String text;

    private KeyValueReference keyValueReference;

    //We do not want a client of this class to use the constructor but rather a static factory
    //method. But this is really up to taste and not necessarily needed.
    private JmeCreateDeclarationV2CommandBuilder() {
        super(JmeCreateDeclarationV2Command::new);
    }
    public static JmeCreateDeclarationV2CommandBuilder create() {
        return new JmeCreateDeclarationV2CommandBuilder();
    }

    // Define the additional builder methods.
    public JmeCreateDeclarationV2CommandBuilder text(String text) {
        this.text = text;
        return this;
    }

    public JmeCreateDeclarationV2CommandBuilder keyValueReference(KeyValueReference keyValueReference) {
        this.keyValueReference = keyValueReference;
        return this;
    }

    @Override
    protected JmeCreateDeclarationV2CommandBuilder self() {
        return this;
    }

    // The build method has to assemble all the data from the builder methods into the payload
    // and references objects.
    @Override
    public JmeCreateDeclarationV2Command build() {
        if (this.text == null) {
            throw AvroMessageBuilderException.propertyNull("JmeCreateDeclarationV2Command.text");
        }
        if (this.keyValueReference != null) {
            CreateDeclarationReferences references = CreateDeclarationReferences.newBuilder()
                    .setReference(keyValueReference)
                    .build();
            setReferences(references);
        }
        CreateDeclarationPayload payload = CreateDeclarationPayload.newBuilder()
                .setText(text)
                .build();
        setPayload(payload);
        return super.build();
    }
}
