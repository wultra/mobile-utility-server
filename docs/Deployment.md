# Deployment

You can easily deploy the application WAR to any application container, such as Apache Tomcat or JBoss Wildfly, run it as a standalone Spring Boot application, or via Docker.

## Configuration Properties

Use properties or environment variables to define database connectivity properties.

### Minimal Configuration

To configure the JDBC connectivity, use the following properties or environment variables:

```
spring.datasource.url=${MOBILE_UTILITY_SERVER_DATASOURCE_URL:jdbc:postgresql://host.docker.internal:5432/powerauth}
spring.datasource.username=${MOBILE_UTILITY_SERVER_DATASOURCE_USERNAME:powerauth}
spring.datasource.password=${MOBILE_UTILITY_SERVER_DATASOURCE_PASSWORD:}
```

### Additional Configuration Options

You can customize other frequent database properties using the following properties or environment variables:

```
spring.datasource.driverClassName=${MOBILE_UTILITY_SERVER_DATASOURCE_DRIVER:org.postgresql.Driver}
spring.jpa.properties.hibernate.connection.CharSet=${MOBILE_UTILITY_SERVER_JPA_CHARSET:}
spring.jpa.properties.hibernate.connection.characterEncoding=${MOBILE_UTILITY_SERVER_JPA_CHARACTER_ENCODING:utf8}
spring.jpa.properties.hibernate.connection.useUnicode=${MOBILE_UTILITY_SERVER_JPA_USE_UNICODE:true}
spring.jpa.database-platform=${MOBILE_UTILITY_SERVER_JPA_DATABASE_PLATFORM:org.hibernate.dialect.PostgreSQL10Dialect}
spring.datasource.jndi-name=${MOBILE_UTILITY_SERVER_DATASOURCE_JNDI_NAME:false}
spring.jpa.hibernate.ddl-auto=${MOBILE_UTILITY_SERVER_JPA_DDL_AUTO:none}
```

The application also supports any Spring Boot configuration properties.

## Running in Docker

You can easily deploy the development version of application to your Docker container by running the following commands:

```sh
git clone https://github.com/wultra/mobile-utility-server.git
cd mobile-utility-server
docker build -f Dockerfile . -t mobile-utility-server
docker run --env-file deploy/env.list.tmp -d -it -p 8080:8000 --name=mobile-utility-server mobile-utility-server
```

The Docker image automatically builds the current source codes and creates the database schema using Liquibase. All you need to do to run it successfully is to set the required database properties via environment variables.

## Running the Spring Boot Application

You can also build and run standard Spring Boot application:

```sh
git clone https://github.com/wultra/mobile-utility-server.git
cd mobile-utility-server
mvn spring-boot:run
```

To pass custom application properties, use:

```sh
mvn spring-boot:run \
  -Dspring-boot.run.arguments=--spring.config.location=classpath:application.properties,deploy/conf/application.properties
```