FROM --platform=linux/arm64/v8 onebase-jvm:v1.0.0

WORKDIR /app
ENV APP_HOME=/app
RUN mkdir -p ${APP_HOME}/lib ${APP_HOME}/conf ${APP_HOME}/logs
COPY ./onebase-server-build/conf ${APP_HOME}/conf
COPY ./onebase-server-build/lib ${APP_HOME}/lib

EXPOSE 8080 38080/TCP

ENTRYPOINT ${JAVA_HOME}/bin/java -jar /app/lib/onebase-server.jar com.cmsr.onebase.server.OneBaseServerApplication --spring.profiles.active=prod --spring.config.import=file:${APP_HOME}/conf/application.yaml,file:${APP_HOME}/conf/application-prod.yaml --logging.config=file:${APP_HOME}/conf/logback.xml