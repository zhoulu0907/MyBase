FROM m.daocloud.io/docker.io/library/debian:bookworm-slim

# 安装基础工具和 JDK17
RUN apt-get update && apt-get install -y --no-install-recommends \
    ca-certificates \
    openjdk-17-jdk \
    locales \
    curl \
    wget \
    tar \
    gzip \
    && rm -rf /var/lib/apt/lists/*

# 配置中文支持
RUN sed -i 's/# zh_CN.UTF-8 UTF-8/zh_CN.UTF-8 UTF-8/' /etc/locale.gen \
    && locale-gen zh_CN.UTF-8

ENV LANG=zh_CN.UTF-8
ENV LANGUAGE=zh_CN:zh
ENV LC_ALL=zh_CN.UTF-8

# 设置 Java 环境变量（自动检测架构）
RUN if [ "$(uname -m)" = "aarch64" ]; then \
        echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-arm64' > /etc/profile.d/java.sh; \
    else \
        echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' > /etc/profile.d/java.sh; \
    fi \
    && echo 'export PATH=$PATH:$JAVA_HOME/bin' >> /etc/profile.d/java.sh

ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk
RUN ln -sf /usr/lib/jvm/java-17-openjdk-* /usr/lib/jvm/java-17-openjdk || true

ENV PATH=$PATH:$JAVA_HOME/bin

# ==================== 安装 Flink 1.20.3（从本地解压目录复制） ====================
ENV FLINK_VERSION=1.20.3
ENV FLINK_HOME=/opt/flink
ENV PATH=$PATH:$FLINK_HOME/bin

# 从本地解压目录复制（构建前解压到 packages/flink-1.20.3/）
COPY packages/flink-${FLINK_VERSION}/ $FLINK_HOME/

# ==================== 安装 DolphinScheduler 3.3.2（从本地解压目录复制） ====================
ENV DOLPHINSCHEDULER_VERSION=3.3.2
ENV DOLPHINSCHEDULER_HOME=/opt/dolphinscheduler
ENV PATH=$PATH:$DOLPHINSCHEDULER_HOME/bin

# 从本地解压目录复制（构建前解压到 packages/apache-dolphinscheduler-3.3.2-bin/）
COPY packages/apache-dolphinscheduler-${DOLPHINSCHEDULER_VERSION}-bin/ $DOLPHINSCHEDULER_HOME/

# 配置阿里云镜像源
RUN echo "deb https://mirrors.aliyun.com/debian sid main contrib non-free non-free-firmware" >> /etc/apt/sources.list

# 更新 apt 缓存并安装安全更新
RUN apt-get update \
    && apt-get install -y --no-install-recommends -t sid zlib1g tar sqlite3 libsqlite3-0 ncurses-bin ncurses-base libncurses6 libncursesw6 \
    && apt-get remove -y libprotobuf-dev \
    && rm -rf /var/lib/apt/lists/* \
    && apt-get purge -y '*-dev' libprotobuf* 2>/dev/null || true \
    && apt-get autoremove -y 2>/dev/null || true

# 暴露端口
# Flink: 8081 (Web UI), 6123 (JobManager RPC), 6124-6125 (TaskManager)
# DolphinScheduler: 12345 (API), 5678 (Master), 1234 (Worker)
EXPOSE 8081 6123 6124 6125 12345 5678 1234 25333

#WORKDIR /opt/dolphinscheduler

CMD ["/bin/bash", "-c", "cd /opt/dolphinscheduler && bash standalone-server/bin/start.sh"]