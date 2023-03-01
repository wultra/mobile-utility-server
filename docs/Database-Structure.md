# Database Structure

The database structure is extremely simple, we provide an example in PostgreSQL to describe it.

## Schema Overview

The following image captures the overview of the tables in the schema:

<img src="./img/mobile_app.png" width="544" alt="Mobile Utility Server DB Schema"/>

## PostgreSQL Schema Scripts

### Table: `ssl_mobile_app`

Contains information related to various mobile apps.

```sql
CREATE TABLE mobile_app (
    id INT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    sign_private_key VARCHAR(255) NOT NULL,
    sign_public_key VARCHAR(255) NOT NULL
);
```

| Column             | Type           | Description                                                                                                                                      |
|--------------------|----------------|--------------------------------------------------------------------------------------------------------------------------------------------------|
| `id`               | `INT`          | Primary key for the table, automatically incremented value.                                                                                      |
| `name`             | `VARCHAR(255)` | Name of the application, a machine readable value, such as `wlt-demo-app`.                                                                       |
| `display_name`     | `VARCHAR(255)` | Display name of the application, a human readable value, such as `Wultra Demo App`.                                                              |
| `sign_private_key` | `VARCHAR(255)` | Base64-encoded private key associated with the application. It is used for signing the data on the server side.                                  |
| `sign_public_key`  | `VARCHAR(255)` | Base64-encoded public key associated with the application. It is used by the client applications when verifying data signed on the server side.  |

### Table: `ssl_mobile_domain`

Contains information related to pinned domains.

```sql
CREATE TABLE ssl_mobile_domain (
    id INT PRIMARY KEY,
    app_id INT NOT NULL,
    domain VARCHAR(255) NOT NULL
);
```

| Column             | Type           | Description                                                 |
|--------------------|----------------|-------------------------------------------------------------|
| `id`               | `INT`          | Primary key for the table, automatically incremented value. |
| `app_id`           | `INT`          | Reference to related mobile app entity.                     |
| `domain`           | `VARCHAR(255)` | Host name of the domain, such as `mobile.wultra.com`.       |

### Table: `ssl_mobile_fingerprint`

Table with TLS/SSL certificate fingerprints that should be pinned in the mobile app.

```sql
CREATE TABLE ssl_mobile_fingerprint (
    id INT PRIMARY KEY,
    fingerprint VARCHAR(255) NOT NULL,
    expires INT NOT NULL,
    mobile_domain_id INT NOT NULL
);
```

| Column              | Type           | Description                                                               |
|---------------------|----------------|---------------------------------------------------------------------------|
| `id`                | `INT`          | Primary key for the table, automatically incremented value.               |
| `fingerprint`       | `VARCHAR(255)` | Value of the certificate fingerprint.                                     |
| `expires`           | `INT`          | Unix timestamp (seconds since Jan 1, 1970) of the certificate expiration. |
| `mobile_domain_id`  | `INT`          | Reference to related application domain in the `ssl_mobile_domain` table. |

### Sequence `hibernate_sequence`

Sequence responsible for identifier autoincrement.

```sql
CREATE SEQUENCE IF NOT EXISTS ssl_mobile_app_seq MAXVALUE 9999999999999 CACHE 20;
CREATE SEQUENCE IF NOT EXISTS ssl_mobile_domain_seq MAXVALUE 9999999999999 CACHE 20;
CREATE SEQUENCE IF NOT EXISTS ssl_mobile_fingerprint_seq MAXVALUE 9999999999999 CACHE 20;
```

### Foreign Indexes

The tables are relatively small and as a result, do not require indexes. To marginally improve the lookup performance, you can create the following foreign index.

```sql
ALTER TABLE ssl_mobile_fingerprint
    ADD CONSTRAINT mobile_ssl_pinning_app_fk FOREIGN KEY (mobile_domain_id)
        REFERENCES ssl_mobile_domain ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ssl_mobile_domain
    ADD CONSTRAINT ssl_mobile_domain_fk FOREIGN KEY (app_id)
        REFERENCES ssl_mobile_app ON UPDATE CASCADE ON DELETE CASCADE;
```
