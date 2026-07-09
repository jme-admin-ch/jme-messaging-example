# JME Audit Example

## Overview

The example service jme-messaging-receiverpublisher-outbox-service demonstrates the use of the jEAP audit libraries for
auditing changes to persisted data. It uses the transactional outbox pattern to ensure reliable delivery of audit
messages even in the presence of failures.

The example demonstrates the following use cases supported directly by the jEAP audit libraries:

- Sending audit messages for data change events triggered by a logged-in user from a UI
- Sending audit messages for data change events triggered by another microservice with a Kafka message
- Sending audit messages for data change events triggered by a scheduled internal task execution

## Example Source Code

The class Audit in the package ch.admin.bit.jeap.jme.messaging.receiverpublisheroutbox contains the core example code
needed for sending audit messages. The three different use cases are implemented in the following methods:

- auditWithUserTrigger
- auditWithMessageTrigger
- auditWithInternalTrigger

## Testing the Example Locally

### Run the jme-messaging-receiverpublisher-outbox-service

Follow the instructions in the [README.md](README.md) to run the jme-messaging-receiverpublisher-outbox-service locally.

### Audit a Data Change Triggered by a User

Navigate to the "Outbox Test and Inspection API" in the [Swagger-UI](http://localhost:8079/jme-messaging-receiverpublisher-outbox-service/swagger-ui/index.html?urls.primaryName=Outbox+Test+and+Inspection+API#/declaration-controller/createDeclaration)
of the jme-messaging-receiverpublisher-outbox-service and execute a POST request on the declaration-controller endpoint
with a test string as input.  

### Audit a Data Change Triggered by a Kafka Message

Send a CreateDeclarationCommand message using the jme-messaging-subscriber-service by opening the following URL in your
browser (or curl): [Send CreateDeclarationCommand](http://localhost:8070/jme-messaging-sender-service/?text=audit-test&idempotenceId=abcd)

### Audit a Data Change Triggered Internally
Just wait for the scheduled task to run every minute. It will log a message "Updated declaration xyz modifiedAt from uvw
to qrs" when it modified a declaration.

### Inspecting the Audit Records

You can see the transactional outbox to check that the audit messages have been sent. Navigate to the "Outbox Test and
Inspection API" in the [Swagger-UI](http://localhost:8079/jme-messaging-receiverpublisher-outbox-service/swagger-ui/index.html?urls.primaryName=Outbox+Test+and+Inspection+API#/outbox-inspection-controller/listTodaysOutboxMessages)
of the jme-messaging-receiverpublisher-outbox-service and execute a GET request on the outbox-inspection-controller endpoint.
This will list all outbox messages created today including the audit messages.

If you want to see the audit messages sent as AVRO, you can use a Kafka consumer tool like Conduktor, Kafdrop or the
Kafka Plugin for IntelliJ IDEA and inspect messages in the topic "jme-audit".
