## Migration from 1.4.0 to 1.5.x

This guide contains instructions for migration from Mobile Utility Server version 1.4.0 to version 1.5.x.

### Prerequisites

Before performing the migration, users are advised to run the associated Liquibase scripts. Once the Liquibase scripts
have been executed successfully, proceed with the steps in the "Database Changes" section below and run
the `1.5.x-migration.sql` script.

### Database Changes

#### Data Migration between Tables

Migration involves copying data from old tables to new tables and subsequently deleting the old tables. Below are the
SQL commands used for this process:

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

#### Drop Old Tables

After successful data migration, the old tables are no longer required and can be dropped:

```sql
-- Drop old tables
DROP TABLE ssl_certificate CASCADE;
DROP TABLE ssl_localized_text CASCADE;
DROP TABLE ssl_mobile_app CASCADE;
DROP TABLE ssl_mobile_app_version CASCADE;
DROP TABLE ssl_mobile_domain CASCADE;
DROP TABLE ssl_user CASCADE;
DROP TABLE ssl_user_authority CASCADE;
```

#### Drop Old Sequences

Similarly, old sequences associated with the dropped tables can be removed:

```sql
-- Drop old sequences
DROP SEQUENCE ssl_certificate_seq CASCADE;
DROP SEQUENCE ssl_mobile_app_seq CASCADE;
DROP SEQUENCE ssl_mobile_app_version_seq CASCADE;
DROP SEQUENCE ssl_mobile_domain_seq CASCADE;
DROP SEQUENCE ssl_user_authority_seq CASCADE;
DROP SEQUENCE ssl_user_seq CASCADE;
```

### Conclusion

After executing the above SQL commands successfully, the migration process is complete. Ensure to verify and test the
updated database to confirm the successful migration from version 1.4.0 to 1.5.x.