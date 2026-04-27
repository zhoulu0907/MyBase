#!/bin/bash
# OneBase Server 部署脚本
# 支持 init、config、up、down、ps、logs、restart、clean 等命令

set -e

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
PROJECT_ROOT=$(cd "$SCRIPT_DIR/../.." && pwd)

# 版本信息
VERSION="v1.0.0"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

# 日志函数
log_info() {
    echo "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo "${GREEN}[SUCCESS]${NC} $1"
}

log_warn() {
    echo "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo "${RED}[ERROR]${NC} $1"
}

# 检测服务器架构
detect_arch() {
    local arch=$(uname -m)
    case "$arch" in
        x86_64|amd64)
            echo "linux/amd64"
            ;;
        arm64|aarch64)
            echo "linux/arm64"
            ;;
        *)
            log_error "不支持的架构: $arch"
            exit 1
            ;;
    esac
}

# 检查容器运行环境
check_container_runtime() {
    if command -v podman &> /dev/null; then
        CONTAINER_RUNTIME="podman"
        COMPOSE_CMD="podman-compose"
        log_info "使用 Podman 作为容器运行环境"
    elif command -v docker &> /dev/null; then
        CONTAINER_RUNTIME="docker"
        COMPOSE_CMD="docker compose"
        log_info "使用 Docker 作为容器运行环境"
    else
        log_error "未找到 Podman 或 Docker，请先安装容器运行环境"
        exit 1
    fi

    # 检查 compose 命令
    if [ "$CONTAINER_RUNTIME" = "podman" ]; then
        if ! command -v podman-compose &> /dev/null; then
            log_error "未找到 podman-compose，请先安装: pip install podman-compose"
            exit 1
        fi
    fi
}

# 默认配置
DEFAULT_CONFIG() {
    cat << EOF
# OneBase 配置文件
#
# 部署模式说明：
# - 内置中间件模式（默认）：使用 deploy.sh middleware-up 部署的中间件，配置自动填充，无需修改
# - 外部中间件模式：使用已有的外部中间件，需要修改相应的连接配置
#
# 如何切换模式：修改 USE_BUILTIN_MIDDLEWARE=true（内置）或 false（外部）

# ====================
# 部署模式
# ====================
# true: 使用内置中间件（默认，配置自动填充）
# false: 使用外部中间件（需要配置下面的连接信息）
USE_BUILTIN_MIDDLEWARE=true

# ====================
# 数据库配置（外部中间件模式时需要修改）
# ====================
# 内置模式自动使用: onebase-postgresql:5432
DB_HOST=onebase-postgresql
DB_PORT=5432
DB_NAME=onebase_cloud_v3
DB_USER=onebase
DB_PASSWORD=onebase123
DB_TYPE=PostgreSQL

# ====================
# Redis 配置（外部中间件模式时需要修改）
# ====================
# 内置模式自动使用: onebase-redis:6379
REDIS_HOST=onebase-redis
REDIS_PORT=6379
REDIS_PASSWORD=redis123
REDIS_DATABASE=3

# ====================
# DolphinScheduler 配置
# ====================
# 内置模式自动使用: http://onebase-dolphinscheduler:12345/dolphinscheduler
# 注意：首次部署需要登录 DolphinScheduler 获取 Token 和项目 ID
DS_ADDRESS=http://onebase-dolphinscheduler:12345/dolphinscheduler
DS_TOKEN=dolphinscheduler123
DS_ENV=1
DS_FLOW_PROJECT=1
DS_ETL_PROJECT=2
DS_TENANT=root

# ====================
# MinIO 配置（外部中间件模式时需要修改）
# ====================
# 内置模式自动使用: http://onebase-minio:9000
MINIO_ENDPOINT=http://onebase-minio:9000
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin123

# ====================
# Flink 配置
# ====================
FLINK_ADDRESS=http://onebase-server-flink:8080

# ====================
# NodeSandbox 配置
# ====================
JS_SERVER_ADDRESS=http://onebase-nodesandbox:3000

# ====================
# JVM 配置
# ====================
JAVA_OPTS=-Xms512m -Xmx2g
EOF
}

# 初始化配置文件
init_config() {
    log_info "初始化部署配置..."

    # 显示架构信息
    local host_arch=$(uname -m)
    local docker_arch=$(detect_arch)
    echo ""
    echo "${CYAN}========================================${NC}"
    echo "${CYAN}  服务器架构信息${NC}"
    echo "${CYAN}========================================${NC}"
    echo "  主机架构: ${host_arch}"
    echo "  容器架构: ${docker_arch}"
    echo "${CYAN}========================================${NC}"
    echo ""

    # 创建必要的目录
    mkdir -p "$SCRIPT_DIR/config"
    mkdir -p "$SCRIPT_DIR/data/logs"
    mkdir -p "$SCRIPT_DIR/data/conf"
    mkdir -p "$SCRIPT_DIR/data/plugins"

    # 从模板文件生成配置
    # .env 文件
    if [ ! -f "$SCRIPT_DIR/.env" ]; then
        if [ -f "$SCRIPT_DIR/.env.example" ]; then
            cp "$SCRIPT_DIR/.env.example" "$SCRIPT_DIR/.env"
            # 写入检测到的架构
            echo "PLATFORM=${docker_arch}" >> "$SCRIPT_DIR/.env"
            log_success "配置文件已生成: .env"
        else
            echo "PLATFORM=${docker_arch}" > "$SCRIPT_DIR/.env"
            echo "VERSION=v1.0.0" >> "$SCRIPT_DIR/.env"
            log_success "配置文件已生成: .env"
        fi
    else
        log_warn "配置文件已存在: .env"
    fi

    # config.env 文件
    if [ ! -f "$SCRIPT_DIR/config/config.env" ]; then
        if [ -f "$SCRIPT_DIR/config/config.env.example" ]; then
            cp "$SCRIPT_DIR/config/config.env.example" "$SCRIPT_DIR/config/config.env"
            log_success "配置文件已生成: config/config.env"
        else
            DEFAULT_CONFIG > "$SCRIPT_DIR/config/config.env"
            log_success "配置文件已生成: config/config.env"
        fi
    else
        log_warn "配置文件已存在: config/config.env"
    fi

    # 中间件配置文件
    if [ ! -f "$SCRIPT_DIR/middleware/middleware.env" ]; then
        cat << EOF > "$SCRIPT_DIR/middleware/middleware.env"
# PostgreSQL 配置
POSTGRES_PORT=5432
POSTGRES_USER=onebase
POSTGRES_PASSWORD=onebase123
POSTGRES_DB=onebase_cloud_v3

# Redis 配置
REDIS_PORT=6379
REDIS_PASSWORD=redis123

# MinIO 配置
MINIO_API_PORT=9000
MINIO_CONSOLE_PORT=9001
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin123

# DolphinScheduler 配置
DS_PORT=12345
DS_ADMIN_PASSWORD=dolphinscheduler123

# NodeSandbox 配置
JS_PORT=3000
EOF
        log_success "中间件配置文件已生成: middleware/middleware.env"
    else
        log_warn "中间件配置文件已存在: middleware/middleware.env"
    fi

    # nginx upstream 配置
    if [ ! -f "$SCRIPT_DIR/middleware/onebase-upstream.conf" ]; then
        if [ -f "$SCRIPT_DIR/middleware/onebase-upstream.conf.example" ]; then
            cp "$SCRIPT_DIR/middleware/onebase-upstream.conf.example" "$SCRIPT_DIR/middleware/onebase-upstream.conf"
            log_success "Nginx upstream 配置已生成: middleware/onebase-upstream.conf"
        fi
    else
        log_warn "Nginx upstream 配置已存在: middleware/onebase-upstream.conf"
    fi

    # nginx location 配置
    if [ ! -f "$SCRIPT_DIR/middleware/onebase-location.conf" ]; then
        if [ -f "$SCRIPT_DIR/middleware/onebase-location.conf.example" ]; then
            cp "$SCRIPT_DIR/middleware/onebase-location.conf.example" "$SCRIPT_DIR/middleware/onebase-location.conf"
            log_success "Nginx location 配置已生成: middleware/onebase-location.conf"
        fi
    else
        log_warn "Nginx location 配置已存在: middleware/onebase-location.conf"
    fi

    log_success "初始化完成！"
    echo ""
    echo "${CYAN}========================================${NC}"
    echo "${CYAN}  配置说明${NC}"
    echo "${CYAN}========================================${NC}"
    echo ""
    echo "  当前模式: ${GREEN}内置中间件模式${NC}（USE_BUILTIN_MIDDLEWARE=true）"
    echo ""
    echo "  内置中间件模式下，以下配置已自动填充，无需修改："
    echo "  - 数据库地址: onebase-postgresql:5432"
    echo "  - Redis 地址: onebase-redis:6379"
    echo "  - MinIO 地址: onebase-minio:9000"
    echo "  - DolphinScheduler: onebase-dolphinscheduler:12345"
    echo ""
    echo "  ${YELLOW}如使用外部中间件，请修改:${NC}"
    echo "  1. 修改 USE_BUILTIN_MIDDLEWARE=false"
    echo "  2. 修改相应的连接地址、用户名、密码"
    echo ""
    echo "${CYAN}========================================${NC}"
    echo ""
    echo "${CYAN}下一步操作:${NC}"
    echo "  1. 启动中间件: deploy.sh middleware-up"
    echo "  2. 等待中间件启动完成"
    echo "  3. 启动服务: deploy.sh up --dist 或 deploy.sh up --image"
    echo ""
    echo "${YELLOW}注意：DolphinScheduler 的 Token 和项目 ID 需要首次启动后获取${NC}"
}

# 生成运行配置（从环境变量注入到 YAML）
generate_config() {
    log_info "生成运行配置..."

    # 加载配置
    if [ -f "$SCRIPT_DIR/config/config.env" ]; then
        source "$SCRIPT_DIR/config/config.env"
    else
        log_error "未找到配置文件，请先执行: deploy.sh init"
        exit 1
    fi

    # 生成 build 配置
    cat << EOF > "$SCRIPT_DIR/data/conf/application-build.yaml"
server:
  port: 8080

spring:
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
    username: ${DB_USER}
    password: ${DB_PASSWORD}
    type: com.zaxxer.hikari.HikariDataSource
    hikari:
      minimum-idle: 1
      maximum-pool-size: 20
  redis:
    redisson:
      config: |
        singleServerConfig:
          address: "redis://${REDIS_HOST}:${REDIS_PORT}"
          password: "${REDIS_PASSWORD}"
          subscriptionConnectionMinimumIdleSize: 1
          subscriptionConnectionPoolSize: 5
          connectionMinimumIdleSize: 2
          connectionPoolSize: 5
          database: ${REDIS_DATABASE}
pagehelper:
  helperDialect: postgresql
  reasonable: false
  defaultCount: true

onebase:
  metadata:
    default-datasource:
      type: PostgreSQL
      host: ${DB_HOST}
      port: ${DB_PORT}
      username: ${DB_USER}
      password: ${DB_PASSWORD}
      database: onebase_business
      description: Onebase业务数据库
  scheduler:
    address: ${DS_ADDRESS}
    token: ${DS_TOKEN}
    env: ${DS_ENV}
    flow-project: ${DS_FLOW_PROJECT}
    etl-project: ${DS_ETL_PROJECT}
    tenant: ${DS_TENANT}
    flow-url: http://onebase-server-runtime:8080/runtime/flow/remote-call/trigger
  flink:
    address: ${FLINK_ADDRESS}
  file:
    storage-type: MINIO
    minio:
      endpoint: ${MINIO_ENDPOINT}
      access-key: ${MINIO_ACCESS_KEY}
      secret-key: ${MINIO_SECRET_KEY}

justauth:
  enabled: true
  cache:
    type: REDIS
    prefix: 'social_auth_state:'
    timeout: 24h

liteflow:
  enable: true
  version-tag: build
  js-server-address: ${JS_SERVER_ADDRESS}
EOF

    # 生成 runtime 配置
    cat << EOF > "$SCRIPT_DIR/data/conf/application-runtime.yaml"
server:
  port: 8080

spring:
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
    username: ${DB_USER}
    password: ${DB_PASSWORD}
    type: com.zaxxer.hikari.HikariDataSource
    hikari:
      minimum-idle: 1
      maximum-pool-size: 20
  redis:
    redisson:
      config: |
        singleServerConfig:
          address: "redis://${REDIS_HOST}:${REDIS_PORT}"
          password: "${REDIS_PASSWORD}"
          subscriptionConnectionMinimumIdleSize: 1
          subscriptionConnectionPoolSize: 5
          connectionMinimumIdleSize: 2
          connectionPoolSize: 5
          database: ${REDIS_DATABASE}
pagehelper:
  helperDialect: postgresql
  reasonable: false
  defaultCount: true

onebase:
  metadata:
    default-datasource:
      type: PostgreSQL
      host: ${DB_HOST}
      port: ${DB_PORT}
      username: ${DB_USER}
      password: ${DB_PASSWORD}
      database: onebase_business
      description: Onebase业务数据库
  scheduler:
    address: ${DS_ADDRESS}
    token: ${DS_TOKEN}
    env: ${DS_ENV}
    flow-project: ${DS_FLOW_PROJECT}
    etl-project: ${DS_ETL_PROJECT}
    tenant: ${DS_TENANT}
    flow-url: http://onebase-server-runtime:8080/runtime/flow/remote-call/trigger
  flink:
    address: ${FLINK_ADDRESS}
  file:
    storage-type: MINIO
    minio:
      endpoint: ${MINIO_ENDPOINT}
      access-key: ${MINIO_ACCESS_KEY}
      secret-key: ${MINIO_SECRET_KEY}

justauth:
  enabled: true
  cache:
    type: REDIS
    prefix: 'social_auth_state:'
    timeout: 24h

liteflow:
  enable: false
  version-tag: runtime
  js-server-address: ${JS_SERVER_ADDRESS}

metadata:
  runtime:
    enable-auth-check: false
EOF

    # 生成 flink 配置
    cat << EOF > "$SCRIPT_DIR/data/conf/application-flink.yaml"
server:
  port: 8080

spring:
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
    username: ${DB_USER}
    password: ${DB_PASSWORD}
EOF

    log_success "配置文件已生成:"
    log_info "  - data/conf/application-build.yaml"
    log_info "  - data/conf/application-runtime.yaml"
    log_info "  - data/conf/application-flink.yaml"
}

# 创建 docker-compose.yaml（动态生成）
create_compose_file() {
    local mode=$1

    log_info "创建 docker-compose 配置..."

    # 加载配置
    if [ -f "$SCRIPT_DIR/config/config.env" ]; then
        source "$SCRIPT_DIR/config/config.env"
    fi

    cat << EOF > "$SCRIPT_DIR/docker-compose.yaml"
services:
  onebase-server-build:
    container_name: onebase-server-build
    image: onebase-server-build:${VERSION}
    restart: unless-stopped
    environment:
      - TZ=Asia/Shanghai
      - JAVA_OPTS=${JAVA_OPTS:-"-Xms512m -Xmx2g"}
    ports:
      - "28080:8080"
    volumes:
      - ./data/conf/application-build.yaml:/app/conf/application.yaml:ro
      - ./data/logs:/app/logs
    networks:
      - onebase
      - onebase-middleware
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 5
      start_period: 60s

  onebase-server-runtime:
    container_name: onebase-server-runtime
    image: onebase-server-runtime:${VERSION}
    restart: unless-stopped
    environment:
      - TZ=Asia/Shanghai
      - JAVA_OPTS=${JAVA_OPTS:-"-Xms512m -Xmx2g"}
    ports:
      - "38080:8080"
    volumes:
      - ./data/conf/application-runtime.yaml:/app/conf/application.yaml:ro
      - ./data/logs:/app/logs
    networks:
      - onebase
      - onebase-middleware
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 5
      start_period: 60s
    depends_on:
      onebase-server-build:
        condition: service_healthy

  onebase-server-flink:
    container_name: onebase-server-flink
    image: onebase-server-flink:${VERSION}
    restart: unless-stopped
    environment:
      - TZ=Asia/Shanghai
      - JAVA_OPTS=${JAVA_OPTS:-"-Xms512m -Xmx2g"}
    volumes:
      - ./data/conf/application-flink.yaml:/app/conf/application.yaml:ro
      - ./data/logs:/app/logs
    networks:
      - onebase
      - onebase-middleware
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 5
      start_period: 60s

networks:
  onebase:
    driver: bridge
  onebase-middleware:
    external: true
EOF

    log_success "docker-compose.yaml 已生成"
}

# 确保中间件网络存在
ensure_middleware_network() {
    if ! $CONTAINER_RUNTIME network ls | grep -q "onebase-middleware"; then
        log_info "创建中间件网络: onebase-middleware"
        $CONTAINER_RUNTIME network create onebase-middleware
    fi
}

# 启动中间件
middleware_up() {
    log_info "启动中间件服务..."

    check_container_runtime

    cd "$SCRIPT_DIR/middleware"

    # 加载中间件配置
    if [ -f "$SCRIPT_DIR/middleware/middleware.env" ]; then
        source "$SCRIPT_DIR/middleware/middleware.env"
    fi

    # 检查 compose 文件
    if [ ! -f "$SCRIPT_DIR/middleware/docker-compose.middleware.yaml" ]; then
        log_error "未找到中间件 compose 文件: middleware/docker-compose.middleware.yaml"
        exit 1
    fi

    $COMPOSE_CMD -f docker-compose.middleware.yaml up -d

    log_success "中间件服务已启动"
    echo ""
    echo "${CYAN}========================================${NC}"
    echo "${CYAN}  中间件访问地址（直接端口访问）${NC}"
    echo "${CYAN}========================================${NC}"
    echo ""
    echo "  PostgreSQL: localhost:${POSTGRES_PORT:-5432}"
    echo "  Redis: localhost:${REDIS_PORT:-6379}"
    echo "  MinIO API: localhost:${MINIO_API_PORT:-9000}"
    echo "  MinIO Console: http://localhost:${MINIO_CONSOLE_PORT:-9001}"
    echo "  DolphinScheduler: http://localhost:${DS_PORT:-12345}/dolphinscheduler"
    echo "  NodeSandbox: http://localhost:${JS_PORT:-3000}"
    echo ""
    echo "${CYAN}========================================${NC}"
    echo ""
    log_warn "请等待中间件完全启动后再启动 OneBase 服务"
}

# 停止中间件
middleware_down() {
    log_info "停止中间件服务..."

    check_container_runtime

    cd "$SCRIPT_DIR/middleware"
    $COMPOSE_CMD -f docker-compose.middleware.yaml down

    log_success "中间件服务已停止"
}

# 从 dist 启动服务
up_from_dist() {
    log_info "从 dist 目录启动服务..."

    check_container_runtime

    # 检查 dist 目录
    if [ ! -d "$SCRIPT_DIR/dist" ]; then
        log_error "未找到 dist 目录，请先执行: build.sh dist"
        exit 1
    fi

    # 检查 JAR 文件
    local jars_found=0
    for jar in onebase-server.jar onebase-server-runtime.jar onebase-server-flink.jar; do
        if [ -f "$SCRIPT_DIR/lib/$jar" ] || [ -f "$SCRIPT_DIR/dist/$jar" ]; then
            jars_found=$((jars_found + 1))
        fi
    done

    if [ $jars_found -eq 0 ]; then
        log_error "未找到 JAR 文件，请先执行: build.sh dist"
        exit 1
    fi

    # 确保中间件网络存在
    ensure_middleware_network

    # 生成配置
    generate_config

    # 创建 compose 文件
    create_compose_file "dist"

    # 启动服务
    cd "$SCRIPT_DIR"
    $COMPOSE_CMD up -d

    show_access_info
}

# 从镜像启动服务
up_from_image() {
    log_info "从镜像启动服务..."

    check_container_runtime

    # 检查镜像是否存在
    local images_ok=true
    for img in onebase-server-build onebase-server-runtime onebase-server-flink; do
        if ! $CONTAINER_RUNTIME images --filter "reference=${img}:${VERSION}" --format "{{.Repository}}" | grep -q "${img}"; then
            log_error "未找到镜像: ${img}:${VERSION}"
            images_ok=false
        fi
    done

    if [ "$images_ok" = false ]; then
        log_error "请先执行: build.sh image 或加载镜像"
        exit 1
    fi

    # 确保中间件网络存在
    ensure_middleware_network

    # 生成配置
    generate_config

    # 创建 compose 文件
    create_compose_file "image"

    # 启动服务
    cd "$SCRIPT_DIR"
    $COMPOSE_CMD up -d

    show_access_info
}

# 显示访问信息
show_access_info() {
    log_success "服务启动完成！"
    echo ""
    echo "${CYAN}========================================${NC}"
    echo "${CYAN}  OneBase 后端 API 访问地址${NC}"
    echo "${CYAN}========================================${NC}"
    echo ""
    echo "  ${GREEN}前端统一入口: http://localhost${NC}"
    echo ""
    echo "  后端 API 路径（通过前端 nginx 代理）:"
    echo "    编辑态 API: /build-api/admin-api/..."
    echo "    运行态 API: /runtime-api/runtime-api/..."
    echo "    Flink API: /flink-api/..."
    echo ""
    echo "  示例:"
    echo "    登录接口: http://localhost/build-api/admin-api/system/auth/login"
    echo "    页面列表: http://localhost/build-api/admin-api/app/page/list"
    echo ""
    echo "  ${YELLOW}中间件直接端口访问:${NC}"
    echo "    DolphinScheduler: http://localhost:12345/dolphinscheduler"
    echo "    MinIO Console: http://localhost:9001"
    echo ""
    echo "${CYAN}========================================${NC}"
    echo ""
    log_info "nginx 配置请参考: middleware/nginx.conf"
    log_info "查看服务状态: deploy.sh ps"
    log_info "查看日志: deploy.sh logs [service]"
}

# 停止服务
down_services() {
    log_info "停止 OneBase 服务..."

    check_container_runtime

    cd "$SCRIPT_DIR"
    $COMPOSE_CMD down

    log_success "服务已停止"
}

# 查看服务状态
show_status() {
    check_container_runtime

    cd "$SCRIPT_DIR"

    if [ -f "$SCRIPT_DIR/docker-compose.yaml" ]; then
        $COMPOSE_CMD ps
    else
        log_warn "未找到 docker-compose.yaml"
        $CONTAINER_RUNTIME ps --filter "name=onebase"
    fi
}

# 查看日志
show_logs() {
    check_container_runtime

    local service=$1

    cd "$SCRIPT_DIR"

    if [ -n "$service" ]; then
        $CONTAINER_RUNTIME logs -f onebase-server-${service}
    else
        $COMPOSE_CMD logs -f
    fi
}

# 重启服务
restart_services() {
    log_info "重启 OneBase 服务..."

    check_container_runtime

    cd "$SCRIPT_DIR"
    $COMPOSE_CMD restart

    log_success "服务已重启"
    show_access_info
}

# 清理资源
clean_resources() {
    log_info "清理部署资源..."

    check_container_runtime

    # 停止服务
    cd "$SCRIPT_DIR"
    $COMPOSE_CMD down -v --rmi local 2>/dev/null || true

    # 清理临时文件
    rm -rf "$SCRIPT_DIR/data/conf/*.yaml" 2>/dev/null || true
    rm -rf "$SCRIPT_DIR/docker-compose.yaml" 2>/dev/null || true

    log_success "清理完成"
}

# 使用帮助
usage() {
    echo "OneBase Server 部署脚本"
    echo ""
    echo "用法: deploy.sh <命令> [选项]"
    echo ""
    echo "命令:"
    echo "  init                初始化配置文件，显示服务器架构"
    echo "  config              生成运行配置文件"
    echo "  middleware-up       启动中间件服务"
    echo "  middleware-down     停止中间件服务"
    echo "  up --dist           从本地 dist 目录启动服务"
    echo "  up --image          从镜像启动服务"
    echo "  down                停止 OneBase 服务"
    echo "  ps                  查看服务状态"
    echo "  logs [service]      查看服务日志 (build/runtime/flink)"
    echo "  restart             重启服务"
    echo "  clean               清理部署资源"
    echo ""
    echo "示例:"
    echo "  deploy.sh init              # 初始化配置"
    echo "  deploy.sh middleware-up     # 启动中间件"
    echo "  deploy.sh up --dist         # 从 dist 启动"
    echo "  deploy.sh logs build        # 查看 build 服务日志"
    echo ""
}

# 主函数
main() {
    cd "$SCRIPT_DIR"

    case "$1" in
        init)
            init_config
            ;;
        config)
            generate_config
            ;;
        middleware-up)
            middleware_up
            ;;
        middleware-down)
            middleware_down
            ;;
        up)
            if [ "$2" = "--dist" ]; then
                up_from_dist
            elif [ "$2" = "--image" ]; then
                up_from_image
            else
                log_error "请指定启动模式: --dist 或 --image"
                usage
                exit 1
            fi
            ;;
        down)
            down_services
            ;;
        ps)
            show_status
            ;;
        logs)
            show_logs "$2"
            ;;
        restart)
            restart_services
            ;;
        clean)
            clean_resources
            ;;
        *)
            usage
            exit 1
            ;;
    esac
}

main "$@"