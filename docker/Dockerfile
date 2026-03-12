FROM powerauth.azurecr.io/wultra-base-image:jre-semeru-21-liquibase_1

LABEL maintainer="Wultra <hello@wultra.com>"

# Prepare environment variables
ENV LOGBACK_CONF=/opt/logback/conf \
    APP_PATH="/app/mobile-utility-server.war" \
    LQ_ENABLED=true

# Copy new Liquibase data
COPY docs/db/changelog/changesets/mobile-utility-server ${LB_HOME}/db/changelog

# Deploy and run applications
COPY target/mobile-utility-server.war "$APP_PATH"

COPY ./docker/conf/logback/* $LOGBACK_CONF/

ENTRYPOINT ["/docker-entrypoint.sh"]


