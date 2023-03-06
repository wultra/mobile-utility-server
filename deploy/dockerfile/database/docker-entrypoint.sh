#!/usr/bin/env sh

liquibase --headless=true --log-level=INFO --changeLogFile=$LB_HOME/db/changelog/db.changelog-master.xml \
  --username=$MOBILE_UTILITY_SERVER_DATASOURCE_USERNAME \
  --password=$MOBILE_UTILITY_SERVER_DATASOURCE_PASSWORD \
  --url=$MOBILE_UTILITY_SERVER_DATASOURCE_URL \
  update
