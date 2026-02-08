#!/usr/bin/env bash
set -euo pipefail

if [ "${LQ_ENABLED}" = true ]; then
  liquibase --headless=true --log-level=INFO --changeLogFile="${LB_HOME}/db/changelog/db.changelog-master.xml" \
    --username="${MOBILE_UTILITY_SERVER_DATASOURCE_USERNAME:-}" \
    --password="${MOBILE_UTILITY_SERVER_DATASOURCE_PASSWORD:-}" \
    --url="${MOBILE_UTILITY_SERVER_DATASOURCE_URL}" \
    update
fi

java -Dserver.port=8000 ${JAVA_OPTS:-} -cp "${APP_PATH}:${EXTLIB_PATH}/*" -jar /mobile-utility-server.war
