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

