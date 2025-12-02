FROM liquibase:4.33.0-alpine

LABEL maintainer="Wultra <hello@wultra.com>"

COPY docker-init/liquibase/data /liquibase/changelog
COPY docker-init/docker-entrypoint.sh /docker-entrypoint.sh

USER liquibase
ENTRYPOINT ["/docker-entrypoint.sh"]
