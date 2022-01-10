# Deployment

You can easily deploy the application WAR to any application container, such as Apache Tomcat or JBoss Wildfly.

## Configuration Properties

Use the following properties to define database connectivity:

```
spring.datasource.url=${JDBC_DATABASE_URL}
spring.datasource.username=${JDBC_DATABASE_USERNAME}
spring.datasource.password=${JDBC_DATABASE_PASSWORD}
spring.datasource.driverClassName=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

Alternatively, you can use the JNDI name to configure the database connection:

```
spring.datasource.jndi-name=${JNDI_NAME}
```
