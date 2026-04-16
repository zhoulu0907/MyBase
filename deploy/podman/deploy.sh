#!/bin/bash
# ===========================================
# OneBase V3 FE 部署脚本
# ===========================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="${ENV_FILE:-$SCRIPT_DIR/.env}"
CONFIG_FILE="${CONFIG_FILE:-$SCRIPT_DIR/config.env}"
DIST_DIR="${DIST_DIR:-$SCRIPT_DIR/dist}"
COMPOSE_PROJECT="${COMPOSE_PROJECT:-onebase-v3-fe}"

# 颜色
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

log_info() { echo "${GREEN}[INFO]${NC} $1"; }
log_warn() { echo "${YELLOW}[WARN]${NC} $1"; }
log_error() { echo "${RED}[ERROR]${NC} $1"; }
log_step() { echo "${BLUE}[STEP]${NC} $1"; }

# 自动检测架构
detect_platform() {
    local arch=$(uname -m)
    case "$arch" in
        x86_64|amd64)
            echo "linux/amd64"
            ;;
        aarch64|arm64)
            echo "linux/arm64"
            ;;
        *)
            log_warn "未知架构: $arch，使用默认 linux/arm64"
            echo "linux/arm64"
            ;;
    esac
}

# 解析 URL 获取 host 和 port
parse_url_host() {
    local url="$1"
    # 移除协议前缀
    url="${url#http://}"
    url="${url#https://}"
    # 移除路径
    url="${url%%/*}"
    # 提取 host（去掉端口）
    echo "${url%%:*}"
}

parse_url_port() {
    local url="$1"
    local default_port="$2"
    # 移除协议前缀
    url="${url#http://}"
    url="${url#https://}"
    # 移除路径
    url="${url%%/*}"
    # 如果有端口则提取，否则返回默认端口
    if [[ "$url" == *:* ]]; then
        echo "${url##*:}"
    else
        echo "$default_port"
    fi
}

# 内置默认值
DEFAULT_APP_KEY="onebase"
DEFAULT_APP_SECRET="ac47af767231f0d08e3787b7d032443a2c7baedaeee07d596cff4525b94ce6a7"
DEFAULT_PUBLIC_KEY="045efee7520c3ed4b3c6bb75424a3ae25039e25bd859731a1f6464cb7e5f7dfb419bcba55cc6adfb7f3e224a6e8949709a3664ff2dc4b822f50ee77bbd64ce3946"

# 端口默认值
DEFAULT_NGINX_PORT=80
DEFAULT_APP_BUILDER_PORT=4399
DEFAULT_ADMIN_CONSOLE_PORT=4402
DEFAULT_RUNTIME_PORT=9527
DEFAULT_MOBILE_RUNTIME_PORT=9528
DEFAULT_MOBILE_EDITOR_PORT=4401

show_help() {
    echo "OneBase V3 FE 部署脚本 (Podman)"
    echo ""
    echo "用法: $0 <命令> [选项]"
    echo ""
    echo "命令:"
    echo "  init        初始化配置文件"
    echo "  config      生成配置文件到 dist"
    echo "  up          启动服务"
    echo "  down        停止服务"
    echo "  ps          查看服务状态"
    echo "  logs        查看日志"
    echo "  restart     重启服务"
    echo "  clean       清理所有资源"
    echo "  help        显示帮助"
    echo ""
    echo "部署模式:"
    echo "  --image     从镜像部署（需要先 build.sh image）"
    echo "  --dist      从本地 dist 目录部署（需要先 build.sh dist）"
    echo ""
    echo "示例:"
    echo "  $0 init"
    echo "  $0 config"
    echo "  $0 up --dist"
    echo "  $0 up --image"
}

# 加载配置
load_config() {
    # 加载 .env
    if [ -f "$ENV_FILE" ]; then
        source "$ENV_FILE"
    fi

    # 加载 config.env
    if [ -f "$CONFIG_FILE" ]; then
        source "$CONFIG_FILE"
    fi

    # 设置默认值
    DEPLOY_MODE="${DEPLOY_MODE:-path}"
    THEME="${THEME:-default}"
    BACKEND_URL="${BACKEND_URL:-http://localhost:8080}"
    FRONTEND_BASE_URL="${FRONTEND_BASE_URL:-http://localhost}"

    # 端口
    NGINX_PORT="${NGINX_PORT:-$DEFAULT_NGINX_PORT}"
    APP_BUILDER_PORT="${APP_BUILDER_PORT:-$DEFAULT_APP_BUILDER_PORT}"
    ADMIN_CONSOLE_PORT="${ADMIN_CONSOLE_PORT:-$DEFAULT_ADMIN_CONSOLE_PORT}"
    RUNTIME_PORT="${RUNTIME_PORT:-$DEFAULT_RUNTIME_PORT}"
    MOBILE_RUNTIME_PORT="${MOBILE_RUNTIME_PORT:-$DEFAULT_MOBILE_RUNTIME_PORT}"
    MOBILE_EDITOR_PORT="${MOBILE_EDITOR_PORT:-$DEFAULT_MOBILE_EDITOR_PORT}"

    # 路径
    APP_BUILDER_PATH="${APP_BUILDER_PATH:-/appbuilder}"
    ADMIN_CONSOLE_PATH="${ADMIN_CONSOLE_PATH:-/console}"
    RUNTIME_PATH="${RUNTIME_PATH:-/runtime}"
    MOBILE_RUNTIME_PATH="${MOBILE_RUNTIME_PATH:-/mobile-runtime}"
    MOBILE_EDITOR_PATH="${MOBILE_EDITOR_PATH:-/mobile-editor}"

    # 扩展配置
    DATASET_URL="${DATASET_URL:-$BACKEND_URL}"
    DASHBOARD_URL="${DASHBOARD_URL:-$FRONTEND_BASE_URL}"

    # AI 服务基础地址（自动拼接 GENAPP 和 COPILOT）
    AI_BASE_URL="${AI_BASE_URL:-}"
    if [ -n "$AI_BASE_URL" ]; then
        GENAPP="${GENAPP:-${AI_BASE_URL%/}/aigenapp/}"
        COPILOT="${COPILOT:-${AI_BASE_URL%/}/aicopilot/}"
    fi

    # SSO
    SSO_HOME="${SSO_HOME:-}"
    SSO_SOURCE_ID="${SSO_SOURCE_ID:-}"

    # Flink 服务地址（可选）
    FLINK_URL="${FLINK_URL:-}"

    # 解析后端地址
    BACKEND_HOST=$(parse_url_host "$BACKEND_URL")
    BACKEND_PORT=$(parse_url_port "$BACKEND_URL" "8080")

    # 解析 Flink 地址
    if [ -n "$FLINK_URL" ]; then
        FLINK_HOST=$(parse_url_host "$FLINK_URL")
        FLINK_PORT=$(parse_url_port "$FLINK_URL" "8081")
    else
        FLINK_HOST=""
        FLINK_PORT=""
    fi

    # 镜像配置
    REGISTRY="${REGISTRY:-}"
    IMAGE_PREFIX="${IMAGE_PREFIX:-onebase-v3-fe}"
    VERSION="${VERSION:-v3.0.0}"
}

# 初始化配置
init_config() {
    if [ ! -f "$CONFIG_FILE" ]; then
        cp "$SCRIPT_DIR/config.env.example" "$CONFIG_FILE"
        log_info "创建配置文件: $CONFIG_FILE"
    else
        log_warn "配置文件已存在: $CONFIG_FILE"
    fi

    if [ ! -f "$ENV_FILE" ]; then
        cp "$SCRIPT_DIR/.env.example" "$ENV_FILE"
        log_info "创建环境文件: $ENV_FILE"
    else
        log_warn "环境文件已存在: $ENV_FILE"
    fi

    # 检测架构并提示
    local detected_platform=$(detect_platform)
    echo ""
    echo "检测到服务器架构: $detected_platform"
    echo ""
    echo "请修改以下配置文件："
    echo "  1. $CONFIG_FILE - 部署配置"
    echo "  2. $ENV_FILE - 环境变量（确认 PLATFORM=$detected_platform）"
    echo ""
    echo "修改完成后运行："
    echo "  $0 config    # 生成配置文件"
    echo "  $0 up --dist # 从本地 dist 部署"
}

# 生成配置文件
generate_configs() {
    load_config

    log_step "生成配置文件"

    local config_dir="$DIST_DIR/config"
    mkdir -p "$config_dir"

    # 生成 App Builder 配置
    cat > "$config_dir/app-builder.js" << EOF
window.global_config = {
  THEME: '${THEME}',
  ENVIRONMENT: 'builder',
  APP_KEY: '${DEFAULT_APP_KEY}',
  APP_SECRET: '${DEFAULT_APP_SECRET}',
  BASE_URL: '${BACKEND_URL}/admin-api',
  PLATFORM_BASE_URL: '${BACKEND_URL}/platform',
  RUNTIME_BASE_URL: '${BACKEND_URL}/runtime',
  RESOURCE_URL: '${BACKEND_URL}/admin-api/infra/file/download',
  $(get_service_urls "app-builder")
  PLUGIN_URL: '${BACKEND_URL}/plugins',
  APP_BUILDER_DATASET_URL: '${DATASET_URL}',
  APP_BUILDER_DASHBOARD_URL: '${DASHBOARD_URL}/#/',
  PUBLIC_KEY: '${DEFAULT_PUBLIC_KEY}'$(if [ -n "$GENAPP" ]; then echo ",
  GENAPP: '${GENAPP}'"; fi)$(if [ -n "$COPILOT" ]; then echo ",
  COPILOT: '${COPILOT}'"; fi)
};
EOF

    # 生成 Admin Console 配置
    cat > "$config_dir/admin-console.js" << EOF
window.global_config = {
  THEME: '${THEME}',
  ENVIRONMENT: 'platform',
  APP_KEY: '${DEFAULT_APP_KEY}',
  APP_SECRET: '${DEFAULT_APP_SECRET}',
  BASE_URL: '${BACKEND_URL}/admin-api',
  PLATFORM_BASE_URL: '${BACKEND_URL}/platform',
  RESOURCE_URL: '${BACKEND_URL}/platform/infra/file/download',
  PLATFORM_FE_URL: '$(get_service_url "app-builder")'
};
EOF

    # 生成 Runtime 配置
    cat > "$config_dir/runtime.js" << EOF
window.global_config = {
  THEME: '${THEME}',
  ENVIRONMENT: 'runtime',
  APP_KEY: '${DEFAULT_APP_KEY}',
  APP_SECRET: '${DEFAULT_APP_SECRET}',
  BASE_URL: '${BACKEND_URL}/admin-api',
  RUNTIME_BASE_URL: '${BACKEND_URL}/runtime',
  RESOURCE_URL: '${BACKEND_URL}/runtime/infra/file/download',
  CORP_RESOURCE_URL: '${BACKEND_URL}/runtime/infra/file/corp/download',
  PLUGIN_URL: '${BACKEND_URL}/plugins',
  APP_BUILDER_DASHBOARD_URL: '${DASHBOARD_URL}/#/',
  PUBLIC_KEY: '${DEFAULT_PUBLIC_KEY}'
};
EOF

    # 生成 Mobile Editor 配置
    cat > "$config_dir/mobile-editor.js" << EOF
window.global_config = {
  THEME: '${THEME}',
  ENVIRONMENT: 'mobile-editor',
  APP_KEY: '${DEFAULT_APP_KEY}',
  APP_SECRET: '${DEFAULT_APP_SECRET}',
  BASE_URL: '${BACKEND_URL}/admin-api',
  PLATFORM_BASE_URL: '${BACKEND_URL}/platform',
  RUNTIME_BASE_URL: '${BACKEND_URL}/runtime',
  RESOURCE_URL: '${BACKEND_URL}/admin-api/infra/file/download'
};
EOF

    # 生成 Mobile Runtime 配置
    cat > "$config_dir/mobile-runtime.js" << EOF
window.global_config = {
  THEME: '${THEME}',
  ENVIRONMENT: 'mobile-runtime',
  APP_KEY: '${DEFAULT_APP_KEY}',
  APP_SECRET: '${DEFAULT_APP_SECRET}',
  BASE_URL: '${BACKEND_URL}/admin-api',
  RUNTIME_BASE_URL: '${BACKEND_URL}/runtime',
  RESOURCE_URL: '${BACKEND_URL}/runtime/infra/file/download'
};
EOF

    log_info "配置文件已生成: $config_dir"
}

# 获取服务 URL（根据部署模式）
get_service_url() {
    local service="$1"
    local url=""

    case "$service" in
        app-builder)
            if [ "$DEPLOY_MODE" = "port" ]; then
                url="${FRONTEND_BASE_URL}:${APP_BUILDER_PORT}"
            else
                if [ "$NGINX_PORT" = "80" ]; then
                    url="${FRONTEND_BASE_URL}${APP_BUILDER_PATH}"
                else
                    url="${FRONTEND_BASE_URL}:${NGINX_PORT}${APP_BUILDER_PATH}"
                fi
            fi
            ;;
        admin-console)
            if [ "$DEPLOY_MODE" = "port" ]; then
                url="${FRONTEND_BASE_URL}:${ADMIN_CONSOLE_PORT}"
            else
                if [ "$NGINX_PORT" = "80" ]; then
                    url="${FRONTEND_BASE_URL}${ADMIN_CONSOLE_PATH}"
                else
                    url="${FRONTEND_BASE_URL}:${NGINX_PORT}${ADMIN_CONSOLE_PATH}"
                fi
            fi
            ;;
        runtime)
            if [ "$DEPLOY_MODE" = "port" ]; then
                url="${FRONTEND_BASE_URL}:${RUNTIME_PORT}"
            else
                if [ "$NGINX_PORT" = "80" ]; then
                    url="${FRONTEND_BASE_URL}${RUNTIME_PATH}"
                else
                    url="${FRONTEND_BASE_URL}:${NGINX_PORT}${RUNTIME_PATH}"
                fi
            fi
            ;;
        mobile-runtime)
            if [ "$DEPLOY_MODE" = "port" ]; then
                url="${FRONTEND_BASE_URL}:${MOBILE_RUNTIME_PORT}"
            else
                if [ "$NGINX_PORT" = "80" ]; then
                    url="${FRONTEND_BASE_URL}${MOBILE_RUNTIME_PATH}"
                else
                    url="${FRONTEND_BASE_URL}:${NGINX_PORT}${MOBILE_RUNTIME_PATH}"
                fi
            fi
            ;;
        mobile-editor)
            if [ "$DEPLOY_MODE" = "port" ]; then
                url="${FRONTEND_BASE_URL}:${MOBILE_EDITOR_PORT}"
            else
                if [ "$NGINX_PORT" = "80" ]; then
                    url="${FRONTEND_BASE_URL}${MOBILE_EDITOR_PATH}"
                else
                    url="${FRONTEND_BASE_URL}:${NGINX_PORT}${MOBILE_EDITOR_PATH}"
                fi
            fi
            ;;
    esac

    echo "$url"
}

# 获取所有服务 URL（用于 app-builder 配置）
get_service_urls() {
    cat << EOF
MOBILE_EDITOR_URL: '$(get_service_url "mobile-editor" | sed 's|http://|//|')',
  RUNTIME_URL: '$(get_service_url "runtime")',
  RUNTIME_MOBILE_URL: '$(get_service_url "mobile-runtime")',
EOF
}

# 启动服务
up() {
    local deploy_type="dist"

    while [ $# -gt 0 ]; do
        case "$1" in
            --image) deploy_type="image" ;;
            --dist) deploy_type="dist" ;;
        esac
        shift
    done

    load_config

    log_step "启动服务 (模式: $deploy_type)"
    log_info "部署模式: $DEPLOY_MODE"
    log_info "主题: $THEME"
    log_info "后端地址: $BACKEND_URL"

    # 创建网络
    podman network create onebase-network 2>/dev/null || true

    if [ "$deploy_type" = "image" ]; then
        up_from_image
    else
        up_from_dist
    fi

    show_access_info
}

# 从镜像部署
up_from_image() {
    log_info "从镜像部署"

    # 生成配置文件
    generate_configs

    local image_prefix="${IMAGE_PREFIX}"
    [ -n "$REGISTRY" ] && image_prefix="${REGISTRY}/${IMAGE_PREFIX}"

    if [ "$DEPLOY_MODE" = "path" ]; then
        # 路径模式：各服务不暴露外部端口，由网关统一入口
        up_services_from_image_no_port "$image_prefix"
        up_gateway
    else
        # 端口模式：各服务独立端口
        up_services_from_image_with_port "$image_prefix"
    fi
}

# 从镜像启动服务（端口模式）
up_services_from_image_with_port() {
    local image_prefix="$1"

    podman run -d --name onebase-app-builder --network onebase-network \
        -p "${APP_BUILDER_PORT}:80" \
        -v "$DIST_DIR/config/app-builder.js:/usr/share/nginx/html/config.js:ro" \
        --restart unless-stopped "${image_prefix}/app-builder:${VERSION}"

    podman run -d --name onebase-admin-console --network onebase-network \
        -p "${ADMIN_CONSOLE_PORT}:80" \
        -v "$DIST_DIR/config/admin-console.js:/usr/share/nginx/html/config.js:ro" \
        --restart unless-stopped "${image_prefix}/admin-console:${VERSION}"

    podman run -d --name onebase-runtime --network onebase-network \
        -p "${RUNTIME_PORT}:80" \
        -v "$DIST_DIR/config/runtime.js:/usr/share/nginx/html/config.js:ro" \
        --restart unless-stopped "${image_prefix}/runtime:${VERSION}"

    podman run -d --name onebase-mobile-runtime --network onebase-network \
        -p "${MOBILE_RUNTIME_PORT}:80" \
        -v "$DIST_DIR/config/mobile-runtime.js:/usr/share/nginx/html/config.js:ro" \
        --restart unless-stopped "${image_prefix}/mobile-runtime:${VERSION}"

    podman run -d --name onebase-mobile-editor --network onebase-network \
        -p "${MOBILE_EDITOR_PORT}:80" \
        -v "$DIST_DIR/config/mobile-editor.js:/usr/share/nginx/html/config.js:ro" \
        --restart unless-stopped "${image_prefix}/mobile-editor:${VERSION}"
}

# 从镜像启动服务（路径模式 - 不暴露外部端口）
up_services_from_image_no_port() {
    local image_prefix="$1"

    podman run -d --name onebase-app-builder --network onebase-network \
        -v "$DIST_DIR/config/app-builder.js:/usr/share/nginx/html/config.js:ro" \
        --restart unless-stopped "${image_prefix}/app-builder:${VERSION}"

    podman run -d --name onebase-admin-console --network onebase-network \
        -v "$DIST_DIR/config/admin-console.js:/usr/share/nginx/html/config.js:ro" \
        --restart unless-stopped "${image_prefix}/admin-console:${VERSION}"

    podman run -d --name onebase-runtime --network onebase-network \
        -v "$DIST_DIR/config/runtime.js:/usr/share/nginx/html/config.js:ro" \
        --restart unless-stopped "${image_prefix}/runtime:${VERSION}"

    podman run -d --name onebase-mobile-runtime --network onebase-network \
        -v "$DIST_DIR/config/mobile-runtime.js:/usr/share/nginx/html/config.js:ro" \
        --restart unless-stopped "${image_prefix}/mobile-runtime:${VERSION}"

    podman run -d --name onebase-mobile-editor --network onebase-network \
        -v "$DIST_DIR/config/mobile-editor.js:/usr/share/nginx/html/config.js:ro" \
        --restart unless-stopped "${image_prefix}/mobile-editor:${VERSION}"
}

# 从本地 dist 部署
up_from_dist() {
    log_info "从本地 dist 部署"

    # 检查 dist 目录
    if [ ! -d "$DIST_DIR/app-builder" ]; then
        log_error "dist 目录不存在，请先运行: ./build.sh dist"
        exit 1
    fi

    # 生成配置
    generate_configs

    if [ "$DEPLOY_MODE" = "path" ]; then
        # 路径模式：各服务不暴露外部端口，由网关统一入口
        up_services_no_port
        up_gateway
    else
        # 端口模式：各服务独立端口
        up_services_with_port
    fi
}

# 启动服务（端口模式 - 各服务独立端口）
up_services_with_port() {
    podman run -d --name onebase-app-builder --network onebase-network \
        -p "${APP_BUILDER_PORT}:80" \
        -v "$DIST_DIR/app-builder:/usr/share/nginx/html:ro" \
        -v "$DIST_DIR/config/app-builder.js:/usr/share/nginx/html/config.js:ro" \
        --restart unless-stopped nginx:1.28-alpine3.21

    podman run -d --name onebase-admin-console --network onebase-network \
        -p "${ADMIN_CONSOLE_PORT}:80" \
        -v "$DIST_DIR/admin-console:/usr/share/nginx/html:ro" \
        -v "$DIST_DIR/config/admin-console.js:/usr/share/nginx/html/config.js:ro" \
        --restart unless-stopped nginx:1.28-alpine3.21

    podman run -d --name onebase-runtime --network onebase-network \
        -p "${RUNTIME_PORT}:80" \
        -v "$DIST_DIR/runtime:/usr/share/nginx/html:ro" \
        -v "$DIST_DIR/config/runtime.js:/usr/share/nginx/html/config.js:ro" \
        --restart unless-stopped nginx:1.28-alpine3.21

    podman run -d --name onebase-mobile-runtime --network onebase-network \
        -p "${MOBILE_RUNTIME_PORT}:80" \
        -v "$DIST_DIR/mobile-runtime:/usr/share/nginx/html:ro" \
        -v "$DIST_DIR/config/mobile-runtime.js:/usr/share/nginx/html/config.js:ro" \
        --restart unless-stopped nginx:1.28-alpine3.21

    podman run -d --name onebase-mobile-editor --network onebase-network \
        -p "${MOBILE_EDITOR_PORT}:80" \
        -v "$DIST_DIR/mobile-editor:/usr/share/nginx/html:ro" \
        -v "$DIST_DIR/config/mobile-editor.js:/usr/share/nginx/html/config.js:ro" \
        --restart unless-stopped nginx:1.28-alpine3.21
}

# 启动服务（路径模式 - 不暴露外部端口）
up_services_no_port() {
    podman run -d --name onebase-app-builder --network onebase-network \
        -v "$DIST_DIR/app-builder:/usr/share/nginx/html:ro" \
        -v "$DIST_DIR/config/app-builder.js:/usr/share/nginx/html/config.js:ro" \
        --restart unless-stopped nginx:1.28-alpine3.21

    podman run -d --name onebase-admin-console --network onebase-network \
        -v "$DIST_DIR/admin-console:/usr/share/nginx/html:ro" \
        -v "$DIST_DIR/config/admin-console.js:/usr/share/nginx/html/config.js:ro" \
        --restart unless-stopped nginx:1.28-alpine3.21

    podman run -d --name onebase-runtime --network onebase-network \
        -v "$DIST_DIR/runtime:/usr/share/nginx/html:ro" \
        -v "$DIST_DIR/config/runtime.js:/usr/share/nginx/html/config.js:ro" \
        --restart unless-stopped nginx:1.28-alpine3.21

    podman run -d --name onebase-mobile-runtime --network onebase-network \
        -v "$DIST_DIR/mobile-runtime:/usr/share/nginx/html:ro" \
        -v "$DIST_DIR/config/mobile-runtime.js:/usr/share/nginx/html/config.js:ro" \
        --restart unless-stopped nginx:1.28-alpine3.21

    podman run -d --name onebase-mobile-editor --network onebase-network \
        -v "$DIST_DIR/mobile-editor:/usr/share/nginx/html:ro" \
        -v "$DIST_DIR/config/mobile-editor.js:/usr/share/nginx/html/config.js:ro" \
        --restart unless-stopped nginx:1.28-alpine3.21
}

# 启动网关（路径模式）
up_gateway() {
    log_info "启动网关 (端口: $NGINX_PORT)"

    # 检查 middleware 配置文件
    check_middleware_config

    podman run -d --name onebase-gateway --network onebase-network \
        -p "${NGINX_PORT}:80" \
        -v "$SCRIPT_DIR/nginx/gateway.conf:/etc/nginx/conf.d/default.conf:ro" \
        -v "$SCRIPT_DIR/middleware:/etc/nginx/middleware:ro" \
        --restart unless-stopped nginx:1.28-alpine3.21
}

# 检查 middleware 配置文件
check_middleware_config() {
    local middleware_dir="$SCRIPT_DIR/middleware"

    if [ ! -f "$middleware_dir/onebase-upstream.conf" ]; then
        log_warn "缺少后端配置: $middleware_dir/onebase-upstream.conf"
        log_warn "请参考 $middleware_dir/onebase-upstream.conf.example 创建配置文件"
        log_warn "后端 API 代理将无法正常工作"
    fi

    if [ ! -f "$middleware_dir/onebase-location.conf" ]; then
        log_warn "缺少后端配置: $middleware_dir/onebase-location.conf"
        log_warn "请参考 $middleware_dir/onebase-location.conf.example 创建配置文件"
        log_warn "后端 API 代理将无法正常工作"
    fi
}

# 显示访问信息
show_access_info() {
    echo ""
    log_info "服务启动完成"
    echo ""

    if [ "$DEPLOY_MODE" = "path" ]; then
        echo "网关入口: ${FRONTEND_BASE_URL}:${NGINX_PORT}"
        echo ""
        echo "访问地址:"
        echo "  App Builder:    $(get_service_url "app-builder")/"
        echo "  Admin Console:  $(get_service_url "admin-console")/"
        echo "  Runtime:        $(get_service_url "runtime")/"
        echo "  Mobile Editor:  $(get_service_url "mobile-editor")/"
        echo "  Mobile Runtime: $(get_service_url "mobile-runtime")/"
    else
        echo "访问地址:"
        echo "  App Builder:    $(get_service_url "app-builder")"
        echo "  Admin Console:  $(get_service_url "admin-console")"
        echo "  Runtime:        $(get_service_url "runtime")"
        echo "  Mobile Editor:  $(get_service_url "mobile-editor")"
        echo "  Mobile Runtime: $(get_service_url "mobile-runtime")"
    fi
}

# 停止服务
down() {
    log_step "停止服务"

    podman stop onebase-app-builder onebase-admin-console onebase-runtime \
        onebase-mobile-runtime onebase-mobile-editor onebase-gateway 2>/dev/null || true
    podman rm onebase-app-builder onebase-admin-console onebase-runtime \
        onebase-mobile-runtime onebase-mobile-editor onebase-gateway 2>/dev/null || true

    log_info "服务已停止"
}

# 查看状态
ps() {
    podman ps --filter name=onebase-
}

# 查看日志
logs() {
    local service="${1:-}"
    if [ -n "$service" ]; then
        podman logs -f "onebase-$service"
    else
        podman logs -f onebase-app-builder onebase-admin-console onebase-runtime \
            onebase-mobile-runtime onebase-mobile-editor onebase-gateway 2>/dev/null
    fi
}

# 重启服务
restart() {
    log_step "重启服务"
    podman restart onebase-app-builder onebase-admin-console onebase-runtime \
        onebase-mobile-runtime onebase-mobile-editor onebase-gateway 2>/dev/null || true
    log_info "服务已重启"
}

# 清理
clean() {
    log_step "清理所有资源"

    down
    podman network rm onebase-network 2>/dev/null || true
    rm -rf "$DIST_DIR/config" 2>/dev/null || true

    log_info "清理完成"
}

# 主入口
case "${1:-help}" in
    init)     init_config ;;
    config)   generate_configs ;;
    up)       up "${@:2}" ;;
    down)     down ;;
    ps)       ps ;;
    logs)     logs "${2:-}" ;;
    restart)  restart ;;
    clean)    clean ;;
    help|--help|-h) show_help ;;
    *)
        log_error "未知命令: $1"
        show_help
        exit 1
        ;;
esac