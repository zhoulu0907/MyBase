# OneBase JVM 基础镜像
# 基于 Eclipse Temurin JDK 17

FROM eclipse-temurin:17-jre

LABEL maintainer="OneBase Team"
LABEL description="OneBase JVM Base Image"

# 设置时区
ENV TZ=Asia/Shanghai

# 创建应用目录
RUN mkdir -p /app/lib /app/conf /app/logs

# 设置工作目录
WORKDIR /app

# 设置 JAVA_HOME
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"