# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
