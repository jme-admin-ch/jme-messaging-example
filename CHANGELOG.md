# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [8.6.0] - 2026-09-10

### Dependencies
- **ch.admin.bit.jeap:jeap-spring-boot-parent**: 40.11.0 → 40.13.0 (minor)
- **ch.admin.bit.jeap:jeap-error-handling-service**: 23.3.1 → 23.4.0 (minor)
- **ch.admin.bit.jeap.jme:jme-spring-boot-integration-test**: 6.3.0 → 6.4.0 (minor)

## [8.5.0] - 2026-09-09

### Dependencies
- **ch.admin.bit.jeap:jeap-spring-boot-parent**: 40.10.1 → 40.11.0 (minor)
- **ch.admin.bit.jeap:jeap-oauth-mock-server**: 10.9.0 → 10.10.0 (minor)

## [8.4.0] - 2026-09-08

### Dependencies
- **ch.admin.bit.jeap:jeap-spring-boot-parent**: 40.10.0 → 40.10.1 (patch)
- **ch.admin.bit.jeap:jeap-error-handling-service**: 23.2.0 → 23.3.1 (minor)

## [8.3.0] - 2026-09-07

### Dependencies
- **ch.admin.bit.jeap:jeap-spring-boot-parent**: 40.9.2 → 40.10.0 (minor)

## [8.2.0] - 2026-09-06

### Dependencies
- **ch.admin.bit.jeap:jeap-oauth-mock-server**: 10.8.0 → 10.9.0 (minor)
- **ch.admin.bit.jeap.jme:jme-spring-boot-integration-test**: 6.2.2 → 6.3.0 (minor)

## [8.1.1] - 2026-09-04

### Dependencies
- **ch.admin.bit.jeap:jeap-spring-boot-parent**: 40.9.0 → 40.9.2 (patch)
- **ch.admin.bit.jeap.jme:jme-spring-boot-integration-test**: 6.2.1 → 6.2.2 (patch)

## [8.1.0] - 2026-09-03

### Dependencies
- **ch.admin.bit.jeap:jeap-spring-boot-parent**: 40.7.0 → 40.9.0 (minor)
- **ch.admin.bit.jeap:jeap-oauth-mock-server**: 10.7.0 → 10.8.0 (minor)
- **ch.admin.bit.jeap:jeap-error-handling-service**: 23.0.0 → 23.2.0 (minor)
- **ch.admin.bit.jeap.jme:jme-spring-boot-integration-test**: 6.2.0 → 6.2.1 (patch)

## [8.0.0] - 2026-09-02

### Dependencies
- **ch.admin.bit.jeap:jeap-spring-boot-parent**: 40.6.0 → 40.7.0 (minor)
- **ch.admin.bit.jeap:jeap-oauth-mock-server**: 10.5.0 → 10.7.0 (minor)
- **ch.admin.bit.jeap:jeap-error-handling-service**: 22.6.0 → 23.0.0 (major)

## [7.7.0] - 2026-09-01

### Dependencies
- **ch.admin.bit.jeap:jeap-spring-boot-parent**: 40.5.1 → 40.6.0 (minor)
- **ch.admin.bit.jeap:jeap-oauth-mock-server**: 10.4.0 → 10.5.0 (minor)
- **ch.admin.bit.jeap:jeap-error-handling-service**: 22.5.1 → 22.6.0 (minor)

### Fixed
- Removed the test-scoped `jeap-messaging-outbox` dependency from `jme-messaging-error-scs`, which demoted the
  compile-scoped dependency that `jeap-error-handling-service` 22.6.0 newly brings in for the Modulith publication
  error handling and thus kept the service from starting.

## [7.6.0] - 2026-08-31

### Dependencies
- **ch.admin.bit.jeap:jeap-spring-boot-parent**: 40.5.0 → 40.5.1 (patch)
- **ch.admin.bit.jeap:jeap-error-handling-service**: 22.4.0 → 22.5.1 (minor)

## [7.5.0] - 2026-08-30

### Dependencies
- **ch.admin.bit.jeap.jme:jme-spring-boot-integration-test**: 6.1.0 → 6.2.0 (minor)

## [7.4.0] - 2026-08-28

### Dependencies
- **ch.admin.bit.jeap:jeap-oauth-mock-server**: 10.3.0 → 10.4.0 (minor)

## [7.3.0] - 2026-08-28

### Dependencies
- **ch.admin.bit.jeap:jeap-spring-boot-parent**: 40.4.0 → 40.5.0 (minor)
- **ch.admin.bit.jeap:jeap-oauth-mock-server**: 10.2.0 → 10.3.0 (minor)
- **ch.admin.bit.jeap:jeap-error-handling-service**: 22.1.0 → 22.4.0 (minor)
- **ch.admin.bit.jeap.jme:jme-spring-boot-integration-test**: 6.0.1 → 6.1.0 (minor)

## [7.2.0] - 2026-08-27

### Dependencies
- **ch.admin.bit.jeap:jeap-spring-boot-parent**: 40.1.0 → 40.4.0 (minor)
- **ch.admin.bit.jeap:jeap-oauth-mock-server**: 10.1.0 → 10.2.0 (minor)

## [7.1.0] - 2026-08-24

### Dependencies
- **ch.admin.bit.jeap:jeap-spring-boot-parent**: 40.0.0 → 40.1.0 (minor)
- **ch.admin.bit.jeap:jeap-oauth-mock-server**: 10.0.0 → 10.1.0 (minor)
- **ch.admin.bit.jeap:jeap-error-handling-service**: 22.0.1 → 22.1.0 (minor)
- **ch.admin.bit.jeap.jme:jme-spring-boot-integration-test**: 6.0.0 → 6.0.1 (patch)

## [7.0.1] - 2026-08-23

### Dependencies
- **ch.admin.bit.jeap:jeap-error-handling-service**: 22.0.0 → 22.0.1 (patch)

## [7.0.0] - 2026-08-21

### Dependencies
- **ch.admin.bit.jeap:jeap-oauth-mock-server**: 9.1.0 → 10.0.0 (major)
- **ch.admin.bit.jeap:jeap-error-handling-service**: 21.2.0 → 22.0.0 (major)
- **ch.admin.bit.jeap.jme:jme-spring-boot-integration-test**: 5.15.0 → 6.0.0 (major)

## [6.0.0] - 2026-08-20

### Dependencies
- **ch.admin.bit.jeap:jeap-spring-boot-parent**: 39.5.0 → 40.0.0 (major)
- **ch.admin.bit.jeap:jeap-error-handling-service**: 21.0.2 → 21.2.0 (minor)
- **ch.admin.bit.jeap.jme:jme-spring-boot-integration-test**: 5.14.0 → 5.15.0 (minor)

## [5.2.0] - 2026-08-19

### Dependencies
- **ch.admin.bit.jeap:jeap-spring-boot-parent**: 39.3.0 → 39.5.0 (minor)
- **ch.admin.bit.jeap:jeap-oauth-mock-server**: 9.0.0 → 9.1.0 (minor)
- **ch.admin.bit.jeap:jeap-error-handling-service**: 21.0.1 → 21.0.2 (patch)
- **ch.admin.bit.jeap.jme:jme-spring-boot-integration-test**: 5.13.0 → 5.14.0 (minor)

## [5.1.0] - 2026-08-18

### Dependencies
- **ch.admin.bit.jeap:jeap-spring-boot-parent**: 39.0.1 → 39.3.0 (minor)
- **ch.admin.bit.jeap:jeap-error-handling-service**: 21.0.0 → 21.0.1 (patch)
- **ch.admin.bit.jeap.jme:jme-spring-boot-integration-test**: 5.12.0 → 5.13.0 (minor)

## [5.0.0] - 2026-08-16

### Dependencies
- **ch.admin.bit.jeap:jeap-oauth-mock-server**: 8.6.0 → 9.0.0 (major)
- **ch.admin.bit.jeap:jeap-error-handling-service**: 20.2.0 → 21.0.0 (major)

## [4.0.1] - 2026-08-14

### Dependencies
- **ch.admin.bit.jeap:jeap-spring-boot-parent**: 39.0.0 → 39.0.1 (patch)

## [4.0.0] - 2026-08-13

### Dependencies
- **ch.admin.bit.jeap:jeap-spring-boot-parent**: 38.1.0 → 39.0.0 (major)
- **ch.admin.bit.jeap:jeap-oauth-mock-server**: 8.2.0 → 8.6.0 (minor)
- **ch.admin.bit.jeap:jeap-error-handling-service**: 19.0.1 → 20.2.0 (major)
- **ch.admin.bit.jeap.jme:jme-spring-boot-integration-test**: 5.9.0 → 5.12.0 (minor)

## [3.3.0] - 2026-08-09

### Dependencies
- **ch.admin.bit.jeap.jme:jme-spring-boot-integration-test**: 5.8.1 → 5.9.0 (minor)

## [3.2.0] - 2026-08-05

### Dependencies
- Updated dependencies

## [3.1.0] - 2026-08-04

### Dependencies
- **ch.admin.bit.jeap:jeap-oauth-mock-server**: 8.0.1 → 8.1.0 (minor)
- **ch.admin.bit.jeap:jeap-error-handling-service**: 19.0.0 → 19.0.1 (patch)

## [3.0.0] - 2026-08-02

### Dependencies
- **ch.admin.bit.jeap:jeap-oauth-mock-server**: 8.0.0 → 8.0.1 (patch)
- **ch.admin.bit.jeap:jeap-error-handling-service**: 18.8.0 → 19.0.0 (major)
- **ch.admin.bit.jeap.jme:jme-spring-boot-integration-test**: 5.7.5 → 5.8.0 (minor)

## [2.1.0] - 2026-07-30

### Dependencies
- **ch.admin.bit.jeap:jeap-spring-boot-parent**: 38.0.0 → 38.0.1 (patch)
- **ch.admin.bit.jeap:jeap-error-handling-service**: 18.7.1 → 18.8.0 (minor)

## [2.0.0] - 2026-07-29

### Dependencies
- **ch.admin.bit.jeap:jeap-spring-boot-parent**: 37.7.0 → 38.0.0 (major)
- **ch.admin.bit.jeap:jeap-oauth-mock-server**: 7.0.0 → 8.0.0 (major)
- **ch.admin.bit.jeap:jeap-error-handling-service**: 18.6.0 → 18.7.1 (minor)
- **ch.admin.bit.jeap.jme:jme-spring-boot-integration-test**: 5.5.0 → 5.7.5 (minor)

## [1.3.0] - 2026-07-28

### Dependencies
- **ch.admin.bit.jeap:jeap-spring-boot-parent**: 37.0.1 → 37.7.0 

## [1.2.0] - 2026-07-21

### Added
- new `sequential_inbox_idempotence` table

### Changed
- Updated parent version to 37.0.1
- Sequential Inbox idempotence now uses a PostgreSQL-backed atomic claim to prevent concurrent deliveries with the same qualified message type and idempotence ID from being processed or stored more than once.

## [1.1.1] - 2026-07-14

### Change
- Renamed audit topic

### Fixed
- KafkaConsumerGroupAwaiter works on GitHub 

## [1.1.0] - 2026-07-13

### Dependencies
- **ch.admin.bit.jeap:jeap-spring-boot-parent**: 36.3.1 → 36.7.0 (minor)
- **ch.admin.bit.jeap:jeap-oauth-mock-server**: 6.2.0 → 6.3.0 (minor)
- **ch.admin.bit.jeap:jeap-error-handling-service**: 18.5.0 → 18.6.0 (minor)

## [1.0.0] - 2026-07-09

### Changed

- Initial OSS version
