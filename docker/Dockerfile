FROM ghcr.io/wultra/wultra-base-images:jre-semeru-25_7@sha256:30d496462a9fa1ead309d9397f23a27f6deadf235733547e7c24a4040a3f98fa

# Prepare environment variables
ENV LOGBACK_CONF=/opt/logback/conf \
    APP_PATH="/app/mobile-utility-server.war" \
    HTTP_PORT=8000 \
    HEALTHCHECK_URL="http://localhost:8000/actuator/health"

# Deploy and run applications
COPY target/mobile-utility-server*.war "${APP_PATH}"

COPY ./docker/conf/logback/* $LOGBACK_CONF/
