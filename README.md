# Mobile Utility Server

A utility server with various features suitable for mobile apps.

## Database Structure

### PostgreSQL

#### Table: `mobile_app`

```sql
create table mobile_app
(
    id int primary key,
    name varchar(255) null,
    display_name varchar(255) null,
    sign_private_key varchar(255) null
);
```

#### Table: `mobile_ssl_pinning`

```sql
create table mobile_ssl_pinning
(
    id int primary key,
    name varchar(255) not null,
    fingerprint varchar(255) not null,
    expires int null,
    signature varchar(255) not null,
    app_id int not null
);
```

#### Sequence `hibernate_sequence`

```sql
create sequence hibernate_sequence minvalue 1 maxvalue 9999999999999 cache 20;
```

### Configuration

Use the following properties to define database connectivity:

```
spring.datasource.url=${JDBC_DATABASE_URL}
spring.datasource.username=${JDBC_DATABASE_USERNAME}
spring.datasource.password=${JDBC_DATABASE_PASSWORD}
spring.datasource.driverClassName=org.postgresql.Driver
```

### Deployment

Deploy the app to any application container, such as Apache Tomcat.

### API Documentation

Application publishes a Swagger UI documentation at the `/swagger-ui.html` path. 

