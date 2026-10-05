# JME Sequential Inbox Example

## Context

This example (jme-messaging-sequential-inbox-service) demonstrates the use of the sequential inbox.

The configuration is as follows:
- JmeOrderCreatedEvent is the base event and starts the sequence
- JmeOrderPreparedEvent depends on the JmeOrderCreatedEvent
- JmeOrderValidatedEvent depends on the JmeOrderCreatedEvent
- JmeOrderShippedEvent depends on JmeOrderPreparedEvent AND JmeOrderValidatedEvent

These events can be sent from the jme-messaging-sender-service. 

In this example, the contextId is an OrderId (test1 for the example below).

## Introducing a sequence on live topics

Set `sequencingStartTimestamp` on the relevant sequence in
`jme-messaging-sequential-inbox-service/src/main/resources/messaging/jeap-sequential-inbox.yml`.
Before that local timestamp, messages are handled immediately and their processing history is recorded.
Other sequences continue to enforce their release conditions. Leave the global
`jeap.messaging.sequential-inbox.sequencing-start-timestamp` unset: a future global timestamp still
enables recording for **all** sequences, even if a sequence has an elapsed local timestamp.

The default example leaves recording disabled so out-of-order events demonstrate buffering.
For a manual recording demonstration, set a future timestamp, restart the service and send a shipped
event for a fresh order ID before its predecessors. It is processed immediately instead of waiting.
Allow enough recording time for the business process's in-flight instances before activating sequencing.

Migration `V9__add-created-in-recording-mode-to-sequence-instance.sql` adds the persistent
`created_in_recording_mode` flag. Apply it before upgrading the inbox and complete the rolling
deployment before enabling recording. Expired instances created in recording mode are deleted after
the usual housekeeping delay without forwarding buffered messages to EHS. Normal instances retain
the existing EHS behavior. The provenance flag survives timestamp changes and restarts.

## Consuming one message type from two topics

The example uses Sequential Inbox 22.8.0, managed by jEAP parent 41.15.0. The shipped event is
consumed from both its original topic and a migration topic:

```yaml
- type: JmeOrderShippedEvent
  topics:
    - jme-order-shipped
    - jme-order-shipped-v2
  contextIdExtractor: ch.admin.bit.jeap.jme.messaging.sequentialinbox.model.ProcessIdExtractor
  releaseCondition:
    and:
      - predecessor: JmeOrderValidatedEvent.STOCK_AVAILABLE
      - predecessor: JmeOrderValidatedEvent.CUSTOMER_CREDIT_CHECKED
      - predecessor: JmeOrderPreparedEvent
```

Both consumers invoke the same `@SequentialInboxMessageListener` and feed the same order sequence.
The sender and inbox declare contracts for both topics. `topic` and `topics` are mutually exclusive;
omitting both still selects the message type's default topic. Each extra topic adds a consumer
container, so check the database connection pool and consumer concurrency when enabling it.

For a fresh order ID, first send `/send-order-events/shipped?orderId=multi-topic-1&newTopic=true`.
The sequence is `OPEN`, the shipped message is `WAITING` on `jme-order-shipped-v2`, and no business
message has been recorded. Then send created, prepared and both validated events as below: the
sequence closes and five messages are recorded. The sequence inspection response includes each
message's actual `topic`.

Repeat with a fresh order ID and `bothTopics=true` instead of `newTopic=true` to send the **same
event** to both topics. Only one shipped message is handled, so the final count remains five.
Without either parameter, shipped events still go to the original topic. The new topic does not
require a new message schema or a second handler.

### RHOS and Nivel

The platform wrappers inherit this descriptor, sender endpoint and contracts from the shared
example artifact. Run the same walkthrough using these base URLs:

| Platform | Sender | Inbox inspection |
|----------|--------|------------------|
| Local | `http://localhost:8070/jme-messaging-sender-service` | `http://localhost:8089/jme-messaging-sequential-inbox-service` |
| RHOS DEV | `https://bit-jme-d.apps.p-szb-ros-shrd-npr-01.cloud.admin.ch/jme-messaging-sender-service` | `https://bit-jme-d.apps.p-szb-ros-shrd-npr-01.cloud.admin.ch/jme-messaging-sequential-inbox-service` |
| Nivel DEV | `https://jme-dev.ingress.nivel.bazg.admin.ch/jme-nivel-messaging-sender-service` | `https://jme-dev.ingress.nivel.bazg.admin.ch/jme-nivel-messaging-sequential-inbox-service` |

Before deploying the wrappers, provision `jme-order-shipped-v2` on each platform's Kafka cluster
and grant the sender write access and the inbox read access (plus schema-registry access).
Topics are ordered from the cluster operator; they are not provisioned by the example's service
GitOps files. The local Docker broker permits automatic topic creation. Deploy the upgraded inbox
before publishing to the new topic; an existing topic's initial offset policy must be chosen
explicitly if records were already published before subscription. After a migration, remove the
old topic from the descriptor and contracts once it has been drained.

## Test this example

### Localhost
#### Send Order Events
- http://localhost:8070/jme-messaging-sender-service/send-order-events/created?orderId=test1
- http://localhost:8070/jme-messaging-sender-service/send-order-events/validated?orderId=test1&validationType=STOCK_AVAILABLE
- http://localhost:8070/jme-messaging-sender-service/send-order-events/validated?orderId=test1&validationType=CUSTOMER_CREDIT_CHECKED
- http://localhost:8070/jme-messaging-sender-service/send-order-events/prepared?orderId=test1
- http://localhost:8070/jme-messaging-sender-service/send-order-events/shipped?orderId=test1

#### Inspect Sequence Instance
- http://localhost:8089/jme-messaging-sequential-inbox-service/inspect/sequence?contextId=test1
- http://localhost:8089/jme-messaging-sequential-inbox-service/inspect/recorded-messages?contextId=test1

#### DevOps Swagger
- http://localhost:8089/jme-messaging-sequential-inbox-service/swagger-ui/index.html?urls.primaryName=Sequential+Inbox
