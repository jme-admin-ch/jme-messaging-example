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

This branch overrides the parent-managed inbox version with released version `22.0.0` until a released
jEAP parent manages that version.

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
