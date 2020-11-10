# Database Structure

The database structure is extremely simple, we provide an example in PostgreSQL to describe it.

## Schema Overview

The following image captures the overview of the tables in the schema:

<img src="./img/mobile_app.png" width="320" alt="Mobile Utility Server DB Schema"/>

## PostgreSQL Schema Scripts

### Table: `mobile_app`

Contains information related to various mobile apps.

```sql
create table mobile_app
(
    id int primary key,
    name varchar(255) null,
    display_name varchar(255) null,
    sign_private_key varchar(255) null,
    sign_public_key varchar(255) null
);
```

| Column | Type | Description |
|---|---|---|
| `id` | `INT` | Primary key for the table, automatically incremented value. |
| `name` | `VARCHAR(255)` | Name of the application, a machine readable value, such as `wlt-demo-app`. |
| `display_name` | `VARCHAR(255)` | Display name of the application, a human readable value, such as `Wultra Demo App`. |
| `sign_private_key` | `VARCHAR(255)` | Base64-encoded private key associated with the application. It is used for signing the data on the server side. |
| `sign_public_key` | `VARCHAR(255)` | Base64-encoded public key associated with the application. It is used by the client applications when verifying data signed on the server side. |

### Table: `mobile_ssl_pinning`

Table with TLS/SSL certificate fingerprints that should be pinned in the mobile app.

```sql
create table mobile_ssl_pinning
(
    id int primary key,
    name varchar(255) not null,
    fingerprint varchar(255) not null,
    expires int null,
    app_id int not null
);
```

| Column | Type | Description |
|---|---|---|
| `id` | `INT` | Primary key for the table, automatically incremented value. |
| `name` | `VARCHAR(255)` | Domain name, such as `mobile.wultra.com`. |
| `fingerprint` | `VARCHAR(255)` | Value of the certificate fingerprint. |
| `expires` | `INT` | Unix timestamp (seconds since Jan 1, 1970) of the certificate expiration. |
| `app_id` | `INT` | Reference to related application in the `mobile_app` table. |

### Sequence `hibernate_sequence`

Sequence responsible for identifier autoincrement.

```sql
create sequence hibernate_sequence minvalue 1 maxvalue 9999999999999 cache 20;
```

### Foreign Indexes

The tables are relatively small and as a result, do not require indexes. To marginally improve the lookup performance, you can create the following foreign index.

```sql
ALTER TABLE mobile_ssl_pinning
  ADD CONSTRAINT mobile_ssl_pinning_app_fk
    FOREIGN KEY (app_id) REFERENCES mobile_app
      ON UPDATE CASCADE ON DELETE CASCADE;
```
