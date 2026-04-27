FROM m.daocloud.io/docker.io/library/node:22-bookworm-slim

# 安装基础工具（包含 curl 用于 NodeSource）
RUN apt-get update && apt-get install -y --no-install-recommends ca-certificates locales

# 配置中文支持
RUN sed -i 's/# zh_CN.UTF-8 UTF-8/zh_CN.UTF-8 UTF-8/' /etc/locale.gen \
    && locale-gen zh_CN.UTF-8

ENV LANG=zh_CN.UTF-8
ENV LANGUAGE=zh_CN:zh
ENV LC_ALL=zh_CN.UTF-8

# 配置阿里云镜像源（包含 sid 用于安装新版系统工具）
RUN echo "deb https://mirrors.aliyun.com/debian sid main contrib non-free non-free-firmware" >> /etc/apt/sources.list


# 更新 apt 缓存
RUN apt-get update

# 从 sid 安装新版系统工具（修复安全漏洞）
RUN apt-get install -y --no-install-recommends -t sid zlib1g tar sqlite3 libsqlite3-0 ncurses-bin ncurses-base libncurses6 libncursesw6

# 清理 apt 缓存，删除开发包
RUN rm -rf /var/lib/apt/lists/* \
    && apt-get purge -y '*-dev' libprotobuf* 2>/dev/null || true \
    && apt-get autoremove -y 2>/dev/null || true

# 强制移除 npm（保留 node）
RUN rm -rf /usr/local/lib/node_modules/npm \
    && rm -f /usr/local/bin/npm \
    && rm -f /usr/local/bin/npx \
    && node --version