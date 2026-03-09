FROM powerauth.azurecr.io/wultra-base-image:jre-semeru-21-liquibase_1

LABEL maintainer="Wultra <hello@wultra.com>"

ENV LIQUIBASE_DATASOURCE_URL="$MOBILE_UTILITY_SERVER_DATASOURCE_URL" \
    LIQUIBASE_DATASOURCE_USERNAME="$MOBILE_UTILITY_SERVER_DATASOURCE_USERNAME" \
    LIQUIBASE_DATASOURCE_PASSWORD="$MOBILE_UTILITY_SERVER_DATASOURCE_PASSWORD" \

COPY docs/db/changelog/changesets/mobile-utility-server $LB_HOME/db/changelog
