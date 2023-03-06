# Docker Image for Runtime
FROM ibm-semeru-runtimes:open-17.0.5_8-jre

LABEL maintainer="Petr Dvořák <petr@wultra.com>"

# Prepare environment variables
# LIQUIBASE_ prefix must not be used, because this is only supported in PRO version
ENV JAVA_HOME=/opt/java/openjdk \
    LB_HOME=/usr/local/liquibase \
    LB_VERSION=4.19.0 \
    TZ=UTC

ENV PATH=$PATH:$LB_HOME

# Upgrade OS and dependencies
RUN apt-get -y update  \
    && apt-get -y upgrade \
    && apt-get -y install bash wget \
# Install Liquibase, inspired by https://github.com/mobtitude/liquibase/blob/master/Dockerfile
    && set -x \
    && wget -q -O /tmp/liquibase.tar.gz "https://github.com/liquibase/liquibase/releases/download/v$LB_VERSION/liquibase-$LB_VERSION.tar.gz" \
    && [ "2ec24cacf1dc6794cde139de9778854839ee1d3fa9c134fefa92157401e57134  /tmp/liquibase.tar.gz" = "$(sha256sum /tmp/liquibase.tar.gz)" ] \
    && mkdir -p "$LB_HOME" \
    && tar -xzf /tmp/liquibase.tar.gz -C "$LB_HOME" \
    && rm -rf "$LB_HOME/sdk" "$LB_HOME/examples" \
# Uninstall packages which are no longer needed and clean apt caches
    && apt-get -y remove wget gettext-base \
    && apt-get -y purge --auto-remove \
    && rm -rf /tmp/* /var/cache/apt/* \
# Remove default Liquibase data
    && rm -rf $LB_HOME/data \
# Add PowerAuth User
    && groupadd -r powerauth && useradd -r -g powerauth -s /sbin/nologin powerauth

COPY docs/db/changelog $LB_HOME/db/changelog

# Deploy and run applications
COPY deploy/conf/application.properties /application.properties
COPY target/mobile-utility-server.war /mobile-utility-server.war

# Docker configuration
EXPOSE 8000
STOPSIGNAL SIGQUIT

USER powerauth

# Define entry point with mandatory commands (liquibase)
COPY deploy/docker-entrypoint.sh /
ENTRYPOINT ["/docker-entrypoint.sh"]


