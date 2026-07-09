# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-07-09

### Changed

- Split the former combined RHOS/AWS repository into three repositories: this OSS base
  (`jme-messaging-example`), `jme-rhos-messaging-example` and `jme-nivel-messaging-example`. The
  platform-specific service modules now depend on the artifacts published from this repository instead of
  containing duplicated source.
- Removed platform-specific configuration (AWS starters, `jeap-spring-boot-tls-starter`,
  `jeap-spring-boot-db-migration-starter`, Dockerfiles, `application-rhos*.yml`, `application-aws*.yml`) from
  all modules; this repository is now platform-agnostic and buildable/runnable locally without any RHOS or
  AWS-specific dependency.
- Added OSS project scaffolding (`publiccode.yml`, `SECURITY.md`, `CONTRIBUTING.md`, `LICENSE`,
  `THIRD-PARTY-LICENSES.md`, `renovate.json`, `.trivyignore`, GitHub Actions build workflow) and an
  integration test module (`jme-messaging-test`).

### Dependencies

- **ch.admin.bit.jeap:jeap-spring-boot-parent**: 36.3.1
- **ch.admin.bit.jeap:jeap-oauth-mock-server**: 6.2.0
- **ch.admin.bit.jeap:jeap-error-handling-service**: 18.5.0
- **ch.admin.bit.jeap.jme:jme-spring-boot-integration-test**: 5.5.0
