#!/usr/bin/env bash
set -euo pipefail

if [ "${MOBILE_UTILITY_SERVER_DATABASE_MIGRATIONS}" = true ];then
  liquibase --headless=true --log-level=INFO --changeLogFile="${LB_HOME}/db/changelog/db.changelog-master.xml" \
    --username="${MOBILE_UTILITY_SERVER_DATASOURCE_USERNAME:-}" \
    --password="${MOBILE_UTILITY_SERVER_DATASOURCE_PASSWORD:-}" \
    --url="${MOBILE_UTILITY_SERVER_DATASOURCE_URL}" \
    update
else
    echo "Database migrations are disabled. Skipping Liquibase update."
fi

java -Dserver.port=8000 -jar /mobile-utility-server.war
