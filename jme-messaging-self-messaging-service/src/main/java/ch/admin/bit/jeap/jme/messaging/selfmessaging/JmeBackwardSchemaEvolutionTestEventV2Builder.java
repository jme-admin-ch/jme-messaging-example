package ch.admin.bit.jeap.jme.messaging.selfmessaging;


import ch.admin.bit.jeap.domainevent.avro.AvroDomainEventBuilder;
import ch.admin.bit.jeap.messaging.avro.AvroMessageBuilderException;
import ch.admin.bit.jme.test.v2.JmeBackwardSchemaEvolutionTestEvent;
import ch.admin.bit.jme.test.v2.JmeBackwardSchemaEvolutionTestEventPayload;
import ch.admin.bit.jme.test.v2.JmeBackwardSchemaEvolutionTestEventReferences;

public class JmeBackwardSchemaEvolutionTestEventV2Builder extends AvroDomainEventBuilder<JmeBackwardSchemaEvolutionTestEventV2Builder, JmeBackwardSchemaEvolutionTestEvent> {
    private String message;

    private JmeBackwardSchemaEvolutionTestEventV2Builder() {
        super(JmeBackwardSchemaEvolutionTestEvent::new);
    }

    public static JmeBackwardSchemaEvolutionTestEventV2Builder create() {
        return new JmeBackwardSchemaEvolutionTestEventV2Builder();
    }

    public JmeBackwardSchemaEvolutionTestEventV2Builder message(String message) {
        this.message = message;
        return this;
    }

    @Override
    protected String getServiceName() {
        return "jme-self-messaging-service";
    }

    @Override
    protected String getSystemName() {
        return "JME";
    }

    @Override
    protected JmeBackwardSchemaEvolutionTestEventV2Builder self() {
        return this;
    }

    @Override
    public JmeBackwardSchemaEvolutionTestEvent build() {
        if (this.message == null) {
            throw AvroMessageBuilderException.propertyNull("jmeBackwardSchemaEvolutionTestEventPayload.message");
        }
        JmeBackwardSchemaEvolutionTestEventReferences references = JmeBackwardSchemaEvolutionTestEventReferences.newBuilder()
                .build();
        JmeBackwardSchemaEvolutionTestEventPayload payload = JmeBackwardSchemaEvolutionTestEventPayload.newBuilder()
                .setMessage(message)
                .build();
        setReferences(references);
        setPayload(payload);
        return super.build();
    }
}
