# OneBase Server Flink 服务镜像
# Flink 处理服务

ARG PLATFORM=linux/amd64
FROM --platform=${PLATFORM} onebase-jvm:v1.0.0

LABEL maintainer="OneBase Team"
LABEL description="OneBase Server Flink Service"
LABEL service=flink

# 服务名称
ARG SERVICE_NAME=flink
ARG JAR_NAME=onebase-server-flink.jar
ARG PORT=8080

# 设置环境变量
ENV APP_HOME=/app
ENV SERVICE_NAME=${SERVICE_NAME}
ENV TZ=Asia/Shanghai

# 创建目录
RUN mkdir -p ${APP_HOME}/lib ${APP_HOME}/conf ${APP_HOME}/logs

# 复制 JAR 文件
COPY lib/${JAR_NAME} ${APP_HOME}/lib/

# 暴露端口（内部通信）
EXPOSE ${PORT}

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=5 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 启动命令
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar ${APP_HOME}/lib/onebase-server-flink.jar --spring.profiles.active=prod --spring.config.import=file:${APP_HOME}/conf/application.yaml --logging.config=file:${APP_HOME}/conf/logback.xml"]