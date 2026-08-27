# Installation

Mobile Utility Server is distributed as two Docker images:

- `powerauth/mobile-utility-server` – the application runtime
- `powerauth/mobile-utility-server-init` – a lightweight init image that runs Liquibase database migrations

Use these images to deploy locally or on any cloud provider (Azure, AWS, ...).

## Pull the Docker Images

```sh
docker pull powerauth/mobile-utility-server:${VERSION}
docker pull powerauth/mobile-utility-server-init:${VERSION}
```

## Configure the Docker Images

Prepare an `env.list` file with the environment variables required for the container launch. See [Deployment](./Deployment.md) for the full list of supported properties.

<!-- begin box warning -->
**Set The Right Database URL**<br/>
The datasource URL for our Docker container follows the structure of the JDBC connectivity. Make sure to provide a valid JDBC URL to the configuration (starting with `jdbc:` prefix). Be especially careful when working on `localhost`! From the Docker container perspective, `localhost` is in the internal network. To connect to your host's localhost, use `host.docker.internal` host name.
<!-- end -->

You need to set at least the properties below:

```
MOBILE_UTILITY_SERVER_DATASOURCE_URL=jdbc:postgresql://db-server:5432/powerauth
MOBILE_UTILITY_SERVER_DATASOURCE_USERNAME=$USERNAME$
MOBILE_UTILITY_SERVER_DATASOURCE_PASSWORD=$PASSWORD$
```

Database schema creation and upgrades are handled by the separate init image via [Liquibase](https://www.liquibase.org/). The main application container does not perform database migrations.

The init image reuses the same datasource properties, so no additional variables need to be configured for it:

```
MOBILE_UTILITY_SERVER_DATASOURCE_URL
MOBILE_UTILITY_SERVER_DATASOURCE_USERNAME
MOBILE_UTILITY_SERVER_DATASOURCE_PASSWORD
```

## Initialize the Database Schema

Run the init image once to create or upgrade the database schema:

```sh
docker run --rm --env-file env.list \
    --name=mobile-utility-server-init powerauth/mobile-utility-server-init:${VERSION}
```

The command above runs Liquibase migrations and exits on success. If there are any issues connecting to the database or applying the migrations, the command will fail with an error message.

To keep the init container alive after the migration finishes (e.g. for Kubernetes init container health checks), set `KEEP_RUNNING=true` (optionally with `KEEP_RUNNING_PORT`, default `8080`).

## Start the Docker Container

After you prepare the configuration file, run the application container using `docker run`:

```sh
docker run --env-file env.list -d -it -p 8080:8000 \
    --name=mobile-utility-server powerauth/mobile-utility-server:${VERSION}
```

This will launch the application container with the properties you specified. Make sure you ran the init image first to prepare the database schema.

<!-- begin box info -->
The Docker containers use the standard UTC timezone.
<!-- end -->