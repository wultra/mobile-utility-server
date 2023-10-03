# Docker Image for Runtime
FROM ibm-semeru-runtimes:open-17.0.8_7-jre

LABEL maintainer="Petr Dvořák <petr@wultra.com>"

# Prepare environment variables
# LIQUIBASE_ prefix must not be used, because this is only supported in PRO version
ENV JAVA_HOME=/opt/java/openjdk \
    LB_HOME=/usr/local/liquibase \
    LB_VERSION=4.23.2 \
    TZ=UTC

ENV PATH=$PATH:$LB_HOME

# Upgrade OS and dependencies
RUN apt-get -y update  \
    && apt-get -y upgrade \
    && apt-get -y install bash wget \
# Install Liquibase, inspired by https://github.com/mobtitude/liquibase/blob/master/Dockerfile
    && set -x \
    && wget -q -O /tmp/liquibase.tar.gz "https://github.com/liquibase/liquibase/releases/download/v$LB_VERSION/liquibase-$LB_VERSION.tar.gz" \
    && [ "fc7d2a9fa97d91203d639b664715d40953c6c9155a5225a0ddc4c8079b9a3641  /tmp/liquibase.tar.gz" = "$(sha256sum /tmp/liquibase.tar.gz)" ] \
    && mkdir -p "$LB_HOME" \
    && tar -xzf /tmp/liquibase.tar.gz -C "$LB_HOME" \
    && rm -rf "$LB_HOME/sdk" "$LB_HOME/examples" \
# Uninstall packages which are no longer needed and clean apt caches
    && apt-get -y remove wget gettext-base \
    && apt-get -y purge --auto-remove \
    && rm -rf /tmp/* /var/cache/apt/* \
# Remove default Liquibase data
    && rm -rf $LB_HOME/data \
# Add PowerAuth user
    && groupadd -r powerauth && useradd -r -g powerauth -s /sbin/nologin powerauth

# Copy new Liquibase data
COPY docs/db/changelog $LB_HOME/db/changelog

# Set non-root PowerAuth user
USER powerauth

# Define entry point with mandatory commands (liquibase)
COPY deploy/dockerfile/database/docker-entrypoint.sh /
ENTRYPOINT ["/docker-entrypoint.sh"]


