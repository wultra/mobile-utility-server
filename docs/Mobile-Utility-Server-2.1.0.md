# Migration from 2.0.x to 2.1.0

This guide provides instructions for migrating from PowerAuth Mobile Utility Server version `2.0.x` to version `2.1.0`.

## Database Changes

For convenience, you can use liquibase for your database migration.

For manual changes, use SQL scripts:
- [PostgreSQL script](sql/postgresql/2.1.0-migration.sql)
- [Oracle script](sql/oracle/2.1.0-migration.sql)

### Table mus_certificate

The type of the `expires` column has been changed from `integer` to `bigint` (Postgres) / `NUMBER(38,0)` (Oracle).


## Dependency Updates


### Docker Base Image Upgrade

The Docker base image has been upgraded from `ibm-semeru-runtimes:open-21.0.9_10-jre-noble` (OpenJDK 21) to `ibm-semeru-runtimes:open-jdk-25.0.3.0-jre-noble` (OpenJDK 25).


### Spring Boot 4 and Jackson 3

Mobile Utility Server has been migrated to Spring Boot 4 and Jackson 3.
