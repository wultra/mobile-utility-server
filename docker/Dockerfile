FROM ghcr.io/wultra/wultra-base-images:jre-semeru-25_3

# Prepare environment variables
ENV LOGBACK_CONF=/opt/logback/conf \
    APP_PATH="/app/mobile-utility-server.war" \
    HTTP_PORT=8000 \
    HEALTHCHECK_URL="http://localhost:8000/actuator/health"

# Deploy and run applications
COPY target/mobile-utility-server*.war "${APP_PATH}"

COPY ./docker/conf/logback/* $LOGBACK_CONF/
