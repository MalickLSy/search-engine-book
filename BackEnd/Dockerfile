FROM openjdk:17-jdk

ARG TOPIC_SUBSCRIPTION_VERSION=1.0.0
ENV APPDIR=/usr/src/myapp

RUN mkdir -p ${APPDIR}
WORKDIR ${APPDIR}

ARG JAR_FILE=target/search-engine-${TOPIC_SUBSCRIPTION_VERSION}.jar
COPY ${JAR_FILE} ${APPDIR}/app.jar


ENTRYPOINT  exec java -jar ${APPDIR}/app.jar

