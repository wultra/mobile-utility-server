# Docker Image for Build
FROM maven:3.9.0-ibm-semeru-17-focal as build
WORKDIR /workspace

COPY pom.xml .
COPY lombok.config .
COPY src src

RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests

# Docker Image for Runtime
FROM ibm-semeru-runtimes:open-17.0.5_8-jre

LABEL maintainer="Petr Dvořák <petr@wultra.com>"

# Prepare environment variables
# LIQUIBASE_ prefix must not be used, because this is only supported in PRO version
ENV JAVA_HOME=/opt/java/openjdk \
    LB_HOME=/usr/local/liquibase \
    LB_VERSION=4.18.0 \
    TZ=UTC

ENV PATH=$PATH:$LB_HOME

# Init
RUN apt-get -y update  \
    && apt-get -y upgrade \
    && apt-get -y install bash curl wget

# Install Liquibase
# This setup was inspired by https://github.com/mobtitude/liquibase/blob/master/Dockerfile
RUN set -x \
    && wget -q -O /tmp/liquibase.tar.gz "https://github.com/liquibase/liquibase/releases/download/v$LB_VERSION/liquibase-$LB_VERSION.tar.gz" \
    && [ "6113f652d06a71556d6ed4a8bb371ab2d843010cb0365379e83df8b4564a6a76  /tmp/liquibase.tar.gz" = "$(sha256sum /tmp/liquibase.tar.gz)" ] \
    && mkdir -p "$LB_HOME" \
    && tar -xzf /tmp/liquibase.tar.gz -C "$LB_HOME" \
    && rm -rf "$LB_HOME/sdk" \
# Uninstall packages which are no longer needed and clean apt caches
    && apt-get -y remove wget curl gettext-base \
    && apt-get -y purge --auto-remove \
    && rm -rf /tmp/* /var/cache/apt/*

COPY deploy/lib/postgresql*.jar $LB_HOME/lib/

# Liquibase - changesets
RUN rm -rf $LB_HOME/data
COPY docs/db/changelog $LB_HOME/db/changelog

# Deploy and run applications
RUN mkdir /app
COPY deploy/conf/application.properties /application.properties
COPY --from=build /workspace/target/*.war /mobile-utility-server.war

# Docker configuration
EXPOSE 8000
STOPSIGNAL SIGQUIT

# Add PowerAuth User
RUN groupadd -r powerauth && useradd -r -g powerauth -s /sbin/nologin powerauth
USER powerauth

# Define entry point with mandatory commands (liquibase, nginx)
COPY deploy/docker-entrypoint.sh /
ENTRYPOINT ["/docker-entrypoint.sh"]


