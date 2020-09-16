# Mobile Utility Server

A utility server with various features suitable for mobile apps.

## Database Structure

### MySQL

#### Table: `mobile_app`

```sql
create table mobile_app
(
    id int auto_increment primary key,
    name varchar(255) null,
    display_name varchar(255) null,
    sign_private_key varchar(255) null
);
```

#### Table: `mobile_ssl_pinning`

```sql
create table mobile_ssl_pinning
(
    id int auto_increment primary key,
    name varchar(255) not null,
    fingerprint varchar(255) not null,
    expires int null,
    signature varchar(255) not null,
    app_id int not null
);
```

### Configuration

Use the following properties to define database connectivity:

```
spring.datasource.url=jdbc:mysql://localhost:3306/powerauth?autoReconnect=true&useSSL=false
spring.datasource.username=powerauth
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
```

### Deployment

Deploy the app to any application container, such as Apache Tomcat.

### API Documentation

Application publishes a Swagger UI documentation at the `/swagger-ui.html` path. 

