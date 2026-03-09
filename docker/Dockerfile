FROM powerauth.azurecr.io/wultra-base-image:jre-semeru-21-liquibase_1

LABEL maintainer="Wultra <hello@wultra.com>"

# Prepare environment variables
ENV JAVA_HOME=/opt/java/openjdk \
    LOGBACK_CONF=/opt/logback/conf \
    LQ_ENABLED=true

# Copy new Liquibase data
COPY docs/db/changelog/changesets/mobile-utility-server $LB_HOME/db/changelog

# Deploy and run applications
COPY target/mobile-utility-server.war "$APP_PATH/mobile-utility-server.war"

COPY ./docker/conf/logback/* $LOGBACK_CONF/

ENTRYPOINT ["/docker-entrypoint.sh"]


