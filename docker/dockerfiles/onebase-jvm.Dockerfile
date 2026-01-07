FROM alpine:3.23.0

RUN apk update && \
    apk add --no-cache openjdk17 fontconfig ttf-dejavu tzdata icu-libs && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk \
    PATH="/usr/lib/jvm/java-17-openjdk/bin:$PATH" \
    LANG=zh_CN.UTF-8 \
    LANGUAGE=zh_CN:zh \
    LC_ALL=zh_CN.UTF-8