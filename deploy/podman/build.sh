#!/bin/bash
# ===========================================
# OneBase V3 FE 构建脚本
# ===========================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$(dirname "$SCRIPT_DIR")")"
ENV_FILE="${ENV_FILE:-$SCRIPT_DIR/.env}"

# 默认值
REGISTRY="${REGISTRY:-}"
IMAGE_PREFIX="${IMAGE_PREFIX:-onebase-v3-fe}"
VERSION="${VERSION:-v3.0.0}"
PLATFORM="${PLATFORM:-linux/arm64}"

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

show_help() {
    echo "OneBase V3 FE 构建脚本"
    echo ""
    echo "用法: $0 <命令> [选项]"
    echo ""
    echo "命令:"
    echo "  image       构建完整 Docker 镜像（包含构建产物）"
    echo "  dist        只构建产物到本地 dist 目录"
    echo "  clean       清理构建产物和镜像"
    echo "  help        显示帮助"
    echo ""
    echo "选项:"
    echo "  --push      构建镜像后推送到仓库"
    echo "  --save      保存镜像为 tar 文件"
    echo ""
    echo "环境变量:"
    echo "  REGISTRY      镜像仓库地址"
    echo "  IMAGE_PREFIX  镜像前缀 (默认: onebase-v3-fe)"
    echo "  VERSION       镜像版本 (默认: v3.0.0)"
    echo "  PLATFORM      构建平台 (默认: linux/arm64)"
    echo ""
    echo "示例:"
    echo "  $0 image"
    echo "  $0 image --push"
    echo "  $0 dist"
    echo "  VERSION=v3.1.0 $0 image --save"
}

# 加载环境变量
load_env() {
    if [ -f "$ENV_FILE" ]; then
        log_info "加载配置: $ENV_FILE"
        source "$ENV_FILE"
    fi

    # 如果未设置 PLATFORM，自动检测
    if [ -z "$PLATFORM" ]; then
        PLATFORM=$(detect_platform)
        log_info "自动检测架构: $PLATFORM"
    fi
}

# 构建完整镜像
build_image() {
    local push_flag=""
    local save_flag=""

    while [ $# -gt 0 ]; do
        case "$1" in
            --push) push_flag="--push" ;;
            --save) save_flag="true" ;;
        esac
        shift
    done

    load_env

    log_step "构建完整 Docker 镜像"
    log_info "镜像前缀: $IMAGE_PREFIX"
    log_info "版本: $VERSION"
    log_info "平台: $PLATFORM"

    local image_name
    local target
    local targets=("app-builder" "admin-console" "runtime" "mobile-runtime" "mobile-editor" "gateway")

    for target in "${targets[@]}"; do
        image_name="${IMAGE_PREFIX}/${target}:${VERSION}"
        [ -n "$REGISTRY" ] && image_name="${REGISTRY}/${image_name}"

        log_info "构建: $image_name"

        podman build \
            --platform "$PLATFORM" \
            --target "$target" \
            -t "$image_name" \
            -f "$SCRIPT_DIR/Dockerfile" \
            "$PROJECT_ROOT"

        if [ -n "$push_flag" ]; then
            log_info "推送: $image_name"
            podman push "$image_name"
        fi

        if [ -n "$save_flag" ]; then
            local save_dir="$SCRIPT_DIR/dist/images"
            mkdir -p "$save_dir"
            local tar_file="$save_dir/${target}-${VERSION}.tar"
            log_info "保存: $tar_file"
            podman save -o "$tar_file" "$image_name"
        fi
    done

    log_info "镜像构建完成"
}

# 只构建产物到本地
build_dist() {
    load_env

    log_step "构建前端产物到本地"
    log_info "项目目录: $PROJECT_ROOT"

    cd "$PROJECT_ROOT"

    # 检查依赖
    if [ ! -d "node_modules" ]; then
        log_info "安装依赖..."
        pnpm install --frozen-lockfile
    fi

    # 构建
    log_info "构建 packages..."
    pnpm build:packages

    log_info "构建 plugins..."
    pnpm build:plugins

    log_info "构建应用..."

    for app in admin-console app-builder runtime mobile-runtime mobile-editor; do
        log_info "构建: $app"
        pnpm --filter "./apps/$app" devops_build
    done

    # 复制产物到 deploy/podman/dist
    local dist_dir="$SCRIPT_DIR/dist"
    mkdir -p "$dist_dir"

    for app in admin-console app-builder runtime mobile-runtime mobile-editor; do
        if [ -d "$PROJECT_ROOT/apps/$app/dist" ]; then
            cp -r "$PROJECT_ROOT/apps/$app/dist" "$dist_dir/$app"
            log_info "复制: $app -> $dist_dir/$app"
        fi
    done

    # 生成配置模板
    generate_config_template "$dist_dir"

    log_info "产物构建完成: $dist_dir"
}

# 生成配置模板
generate_config_template() {
    local dist_dir="$1"

    cat > "$dist_dir/config.env" << 'EOF'
# 部署时修改此配置文件
# 复制此文件到 config.env 并修改

# 主题
THEME=default

# 后端 API 地址
BACKEND_URL=http://your-backend-server

# 前端访问地址
FRONTEND_BASE_URL=http://your-server

# AI 服务基础地址（可选）
# AI_BASE_URL=https://ai-server
EOF

    log_info "生成配置模板: $dist_dir/config.env"
}

# 清理
clean() {
    log_step "清理构建产物"

    # 清理本地 dist
    rm -rf "$SCRIPT_DIR/dist" 2>/dev/null || true
    log_info "清理本地 dist"

    # 清理镜像
    local targets=("app-builder" "admin-console" "runtime" "mobile-runtime" "mobile-editor" "gateway")
    for target in "${targets[@]}"; do
        podman rmi "${IMAGE_PREFIX}/${target}:${VERSION}" 2>/dev/null || true
    done
    log_info "清理镜像"

    log_info "清理完成"
}

# 主入口
case "${1:-help}" in
    image)
        build_image "${@:2}"
        ;;
    dist)
        build_dist
        ;;
    clean)
        clean
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        log_error "未知命令: $1"
        show_help
        exit 1
        ;;
esac