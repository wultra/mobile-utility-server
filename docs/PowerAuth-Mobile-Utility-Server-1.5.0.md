# Migration from 1.x.x to 1.5.x

This guide provides step-by-step instructions for migrating from PowerAuth Mobile Utility Server version 1.x.x to
version 1.5.x.

## Prerequisites

1. **SQL Scripts & Liquibase Tool**: Ensure you have the necessary SQL scripts and the Liquibase tool available.
   Depending on your database type, the scripts can be found at:

    - Oracle: `${repo_root}/docs/sql/oracle`
    - PostgreSQL: `${repo_root}/docs/sql/postgresql`

2. **Docker DB Update Script**: Before running the script `docker-db-update.sh`, ensure that you have set up the
   required environment variables. For a list of the required environment variables or additional details, refer to the
   associated documentation [Deployment.md](Deployment.md).

## Migration Steps

1. **Stop the Application**: Ensure that the PowerAuth Mobile Utility Server application is not running. Shut it down if
   it's currently active.

2. **Execute Pre-Migration Script**: Navigate to the appropriate SQL scripts directory based on your database type. Run
   the `1.5.x-migration-before.sql` script.

3. **Run Liquibase Commands with Docker**: To apply the database changes, execute the `docker-db-update.sh` script.

4. **Execute Post-Migration Script**: After applying the Liquibase changes, run the `1.5.x-migration-after.sql` script
   located in the same directory as the pre-migration script.

5. **Start the PowerAuth MUS Application**: Once all the database changes are successfully applied, restart the
   PowerAuth Mobile Utility Server application.

## Database Changes - detailed description of changes above

The steps detailed above have taken care of migrating the database structure. The following is an overview of the major
changes:

### Resetting Liquibase Tracking Tables

Liquibase uses specific tables to track which changesets have been applied to the database. Occasionally, to ensure a fresh start or to reset its state, these tables might need to be dropped. The `1.5.x-migration-before.sql` script takes care of this by removing the following Liquibase-specific tables:

```sql
-- Drop liquibase tracking tables
DROP TABLE databasechangelog CASCADE;
DROP TABLE databasechangeloglock CASCADE;
```

### Data Migration between Tables

The migration involves transferring data from old tables to new ones. Post data transfer, the old tables are discarded.

```sql
-- Migrate data
-- TABLES

-- For table mobile_app
INSERT INTO mus_mobile_app SELECT * FROM ssl_mobile_app;

-- For table mobile_app_version
INSERT INTO mus_mobile_app_version SELECT * FROM ssl_mobile_app_version;

-- For table mobile_domain
INSERT INTO mus_mobile_domain SELECT * FROM ssl_mobile_domain;

-- For table user
INSERT INTO mus_user SELECT * FROM ssl_user;

-- For table user_authority
INSERT INTO mus_user_authority SELECT * FROM ssl_user_authority;

-- For table certificate
INSERT INTO mus_certificate SELECT * FROM ssl_certificate;

-- For table localized_text
INSERT INTO mus_localized_text SELECT * FROM ssl_localized_text;
```

### Removal of Deprecated Tables and Sequences

After the data migration is successfully completed, the old tables and their associated sequences, which are no longer
in use, have been removed.

```sql
-- Drop old tables
DROP TABLE ssl_certificate CASCADE;
DROP TABLE ssl_localized_text CASCADE;
DROP TABLE ssl_mobile_app CASCADE;
DROP TABLE ssl_mobile_app_version CASCADE;
DROP TABLE ssl_mobile_domain CASCADE;
DROP TABLE ssl_user CASCADE;
DROP TABLE ssl_user_authority CASCADE;

-- Drop old sequences
DROP SEQUENCE ssl_certificate_seq CASCADE;
DROP SEQUENCE ssl_mobile_app_seq CASCADE;
DROP SEQUENCE ssl_mobile_app_version_seq CASCADE;
DROP SEQUENCE ssl_mobile_domain_seq CASCADE;
DROP SEQUENCE ssl_user_authority_seq CASCADE;
DROP SEQUENCE ssl_user_seq CASCADE;

```

### Conclusion

Upon successfully executing the provided SQL commands and scripts, the migration process is complete. Ensure to verify
and test the updated database to confirm the successful migration from version 1.x.x to 1.5.x.