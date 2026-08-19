# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Changed

- Upgraded Docker base image to `ibm-semeru-runtimes:open-jdk-25.0.3.0-jre-noble` (OpenJDK 25) [(#504)](https://github.com/wultra/mobile-utility-server/issues/504)
- Migrated to Spring Boot 4 and Jackson 3 [(#503)](https://github.com/wultra/mobile-utility-server/issues/503)
- Removed ENV variable override for `spring.jpa.hibernate.ddl-auto` in `application.properties` [(#512)](https://github.com/wultra/mobile-utility-server/issues/512)
- Changed Docker images to be based on the Wultra base image [(#538)](https://github.com/wultra/mobile-utility-server/issues/538)

[unreleased]: https://github.com/wultra/mobile-utility-server/compare/2.0.0...HEAD
