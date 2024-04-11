# Database Structure

<!-- TEMPLATE database -->

You can download DDL scripts for supported databases:

- [Oracle - Create Database Schema](./sql/oracle/create_schema.sql)
- [PostgreSQL - Create Database Schema](./sql/postgresql/create_schema.sql)

See the overall database schema:

<img src="./img/mobile_app.png" width="888" alt="Mobile Utility Server DB Schema"/>

## Database Tables

<!-- begin database table mus_mobile_app -->
### Mobile Applications

Contains information related to various mobile apps.

#### Columns

| Column             | Type           | Description                                                                                                                                      |
|--------------------|----------------|--------------------------------------------------------------------------------------------------------------------------------------------------|
| `id`               | `INTEGER`      | Primary key for the table, automatically incremented value.                                                                                      |
| `name`             | `VARCHAR(255)` | Name of the application, a machine readable value, such as `wlt-demo-app`.                                                                       |
| `display_name`     | `VARCHAR(255)` | Display name of the application, a human readable value, such as `Wultra Demo App`.                                                              |
| `sign_private_key` | `VARCHAR(255)` | Base64-encoded private key associated with the application. It is used for signing the data on the server side.                                  |
| `sign_public_key`  | `VARCHAR(255)` | Base64-encoded public key associated with the application. It is used by the client applications when verifying data signed on the server side.  |

#### Sequence

Sequence `mus_mobile_app_seq` responsible for mobile app autoincrements.

<!-- end -->

<!-- begin database table mus_mobile_domain -->
### Mobile App Domains

Contains information related to pinned domains.

#### Columns

| Column             | Type           | Description                                                 |
|--------------------|----------------|-------------------------------------------------------------|
| `id`               | `INTEGER`      | Primary key for the table, automatically incremented value. |
| `app_id`           | `INTEGER`      | Reference to related mobile app entity.                     |
| `domain`           | `VARCHAR(255)` | Host name of the domain, such as `mobile.wultra.com`.       |

#### Sequence

Sequence `mus_mobile_domain_seq` responsible for mobile domain autoincrements.

#### Indexes

The tables are relatively small and as a result, do not require indexes. To marginally improve the lookup performance, you can create a foreign index to map the domain to mobile app.

<!-- end -->

<!-- begin database table mus_certificate -->
### SSL Certificate 

Table with TLS/SSL certificate and fingerprints that should be pinned in the mobile app.

#### Columns

| Column             | Type           | Description                                                               |
|--------------------|----------------|---------------------------------------------------------------------------|
| `id`               | `INTEGER`      | Primary key for the table, automatically incremented value.               |
| `pem`              | `TEXT`         | Original certificate value in PEM format.                                 |
| `fingerprint`      | `VARCHAR(255)` | Value of the certificate fingerprint.                                     |
| `expires`          | `INTEGER`      | Unix timestamp (seconds since Jan 1, 1970) of the certificate expiration. |
| `mobile_domain_id` | `INTEGER`      | Reference to related application domain in the `mus_mobile_domain` table. |

#### Sequence

Sequence `mus_certificate_seq` responsible for SSL certificates and fingerprints autoincrements.

#### Indexes

The tables are relatively small and as a result, do not require indexes. To marginally improve the lookup performance, you can create a foreign index for mapping the fingerprint to domain.

<!-- end -->

<!-- begin database table mus_user -->
### Administrative User

Table with users for basic HTTP authentication.

#### Columns

| Column             | Type           | Description                                                                      |
|--------------------|----------------|----------------------------------------------------------------------------------|
| `id`               | `INTEGER`      | Primary key for the table, automatically incremented value.                      |
| `username`         | `VARCHAR(255)` | Username of the user.                                                            |
| `password`         | `VARCHAR(255)` | Password of the user (`bcrypt` by default, or `{SHA-256}` prefix for `SHA-256`). |
| `enabled`          | `BOOLEAN`      | Indication if the user is enabled or not.                                        |
<!-- end -->

<!-- begin database table mus_user_authority -->
### Administrative User Authorities

Table with users authorities.

#### Columns

| Column      | Type           | Description                                                          |
|-------------|----------------|----------------------------------------------------------------------|
| `id`        | `INTEGER`      | Primary key for the table, automatically incremented value.          |
| `user_id`   | `INTEGER`      | Foreign key column referencing users in `mus_user` table.            |
| `authority` | `VARCHAR(255)` | Name of authority for the user prefixed with `ROLE_` (`ROLE_ADMIN`). |

#### Indexes

The tables are relatively small and as a result, do not require indexes. To marginally improve the lookup performance, you can create a foreign index to map the user authority to the user.

<!-- end -->

<!-- begin database table mus_mobile_app_version -->
### Mobile Application Version

Table to force or suggest update of mobile application version.

#### Columns

| Column              | Type           | Description                                                                                                            |
|---------------------|----------------|------------------------------------------------------------------------------------------------------------------------|
| `id`                | `INTEGER`      | Primary key for the table, automatically incremented value.                                                            |
| `app_id`            | `INTEGER`      | Reference to related mobile app entity.                                                                                |
| `platform`          | `VARCHAR(10)`  | `ANDROID`, `IOS`                                                                                                       |
| `major_os_version`  | `INTEGER`      | For iOS e.g. 12.4.2 it is 12. For Android, it is API level e.g. 29. When `null`, the rule is applied for all versions. |
| `suggested_version` | `VARCHAR(24)`  | If the application version is lower, update is suggested.                                                              |
| `required_version`  | `VARCHAR(24)`  | If the application version is lower, update is required.                                                               |
| `message_key`       | `VARCHAR(255)` | Together with language identifies row in `mus_localized_text`                                                          |

#### Sequence

Sequence `mus_mobile_app_version_seq` responsible for mobile application version autoincrements.

<!-- end -->


<!-- begin database table mus_localized_text -->
### Localized Text

Table with localized texts.

#### Columns

| Column        | Type           | Description                                                              |
|---------------|----------------|--------------------------------------------------------------------------|
| `message_key` | `VARCHAR(255)` | Primary composite key for the table.                                     |
| `language`    | `VARCHAR(2)`   | Primary composite key for the table. ISO 639-1 two-letter language code. |
| `text`        | `TEXT`         | Localized text.                                                          |
<!-- end -->
