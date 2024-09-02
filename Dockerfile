# Docker Image for Build
FROM maven:3.9.6-ibm-semeru-21-jammy AS builder

WORKDIR /workspace

COPY pom.xml .
COPY lombok.config .
COPY src src

RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests


FROM ibm-semeru-runtimes:open-21.0.2_13-jre
LABEL maintainer="Petr Dvořák <petr@wultra.com>"

ENV JAVA_HOME=/opt/java/openjdk \
    LB_HOME=/usr/local/liquibase \
    LB_VERSION=4.23.2 \
    LOGBACK_CONF=/opt/logback/conf \
    TZ=UTC
ENV PATH=$PATH:$LB_HOME

RUN apt-get -y update  \
    && apt-get -y upgrade \
    && apt-get -y install bash wget \
    && set -x \
    && wget -q -O /tmp/liquibase.tar.gz "https://github.com/liquibase/liquibase/releases/download/v${LB_VERSION}/liquibase-${LB_VERSION}.tar.gz" \
    && [ "fc7d2a9fa97d91203d639b664715d40953c6c9155a5225a0ddc4c8079b9a3641  /tmp/liquibase.tar.gz" = "$(sha256sum /tmp/liquibase.tar.gz)" ] \
    && mkdir -p "${LB_HOME}" \
    && tar -xzf /tmp/liquibase.tar.gz -C "${LB_HOME}" \
    && rm -rf "${LB_HOME}/sdk" "${LB_HOME}/examples" \
    && apt-get -y remove wget gettext-base \
    && apt-get -y purge --auto-remove \
    && rm -rf /tmp/* /var/cache/apt/* \
    && rm -rf ${LB_HOME}/data \
    && groupadd -r powerauth && useradd -r -g powerauth -s /sbin/nologin powerauth


COPY docs/db/changelog ${LB_HOME}/db/changelog
COPY deploy/conf/application.properties /application.properties
COPY --from=builder /workspace/target/mobile-utility-server.war /mobile-utility-server.war

EXPOSE 8000
STOPSIGNAL SIGQUIT

USER powerauth

COPY deploy/conf/logback/* $LOGBACK_CONF/

COPY docker-entrypoint.sh /
ENTRYPOINT ["/docker-entrypoint.sh"]
