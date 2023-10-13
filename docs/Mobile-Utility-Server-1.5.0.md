# Migration from 1.1.x to 1.5.0

This guide provides step-by-step instructions for migrating from PowerAuth Mobile Utility Server version 1.1.x to version 1.5.0.


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

2. **Backup the Database**: Before making any changes, it's crucial to **back up your database**. This step ensures you have
   a fallback in case of any unforeseen issues.

   > **IMPORTANT**: Ensure that you've backed up your entire database before proceeding

3. **Run Liquibase update**:  Execute [liquibase](https://www.liquibase.com/download) scripts located in `docs\db\changelog\db.changelog-master` 
- For convenience, you can use supplied Dockerfiles and apply the database changes by execute the `docker-db-update.sh` script. 
- If direct update via Liquibase is not possible `liquibase update` command can generate required SQL script.

   Please take a look at a list of necessary environmental variables listed
   here [env.list.tmp](../deploy/env.list.tmp).

4. **Execute Migration Script**: After applying the Liquibase changes, run the `1.5.0-migration.sql` script
   located in the sql directory.

5. **Start the PowerAuth MUS Application**: Once all the database changes are successfully applied, restart the
   PowerAuth Mobile Utility Server application.

6. **Import Certificates**: Certificates are not possible to migrate automatically.
It is needed to [Importing Certificate in PEM Format](Configuration.md#importing-certificate-in-pem-format) via API.


## Database Changes - detailed description of changes above

The steps detailed above have taken care of migrating the database structure. The following is an overview of the major
changes:


### Data Migration between Tables

The migration involves transferring data from old tables to new ones. Post data transfer, the old tables are discarded.

```sql
-- Migrate data
-- TABLES

-- For table mobile_app
INSERT INTO mus_mobile_app SELECT * FROM mobile_app;

-- There is no migration for table certificate
```


### Removal of Deprecated Tables and Sequences

After the data migration is successfully completed, the old tables and their associated sequences, which are no longer
in use, have been removed.

```sql
-- Drop old tables
DROP TABLE mobile_ssl_pinning CASCADE;
DROP TABLE mobile_app CASCADE;

-- Drop old sequences
DROP SEQUENCE mobile_ssl_pinning_seq CASCADE;
DROP SEQUENCE mobile_app_seq CASCADE;
```


### Conclusion

Upon successfully executing the provided SQL commands and scripts, the migration process is complete.
Ensure to verify and test the updated database to confirm the successful migration from version 1.1.0 to 1.5.0.