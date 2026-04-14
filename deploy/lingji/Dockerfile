FROM m.daocloud.io/docker.io/library/debian:bookworm-slim

RUN apt-get update && apt-get install -y --no-install-recommends ca-certificates openjdk-17-jdk

# 备份并删除原源配置，创建传统格式源列表（使用阿里云镜像）
RUN mv /etc/apt/sources.list.d/debian.sources /etc/apt/sources.list.d/debian.sources.bak 2>/dev/null || true \
    && echo "deb https://mirrors.aliyun.com/debian bookworm main contrib non-free non-free-firmware" >> /etc/apt/sources.list \
    && echo "deb https://mirrors.aliyun.com/debian-security bookworm-security main contrib non-free non-free-firmware" >> /etc/apt/sources.list \
    && echo "deb https://mirrors.aliyun.com/debian sid main contrib non-free non-free-firmware" >> /etc/apt/sources.list

# 更新 apt 缓存
RUN apt-get update

# 从 sid/unstable 安装最新 zlib1g、tar、sqlite3、ncurses（修复安全漏洞）
RUN apt-get install -y --no-install-recommends -t sid zlib1g tar sqlite3 libsqlite3-0 ncurses-bin ncurses-base libncurses6 libncursesw6

RUN apt-get remove -y libprotobuf-dev

# 清理 apt 缓存，删除开发包
RUN rm -rf /var/lib/apt/lists/* \
    && apt-get purge -y '*-dev' libprotobuf* 2>/dev/null || true \
    && apt-get autoremove -y 2>/dev/null || trued