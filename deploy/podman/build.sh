#!/bin/bash
# OneBase Server 构建脚本
# 支持 image 和 dist 两种构建模式

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

# 显示架构信息
show_arch() {
    local host_arch=$(uname -m)
    local docker_arch=$(detect_arch)
    log_info "检测到服务器架构: ${host_arch} -> ${docker_arch}"
}

# 检查容器运行环境
check_container_runtime() {
    if command -v podman &> /dev/null; then
        CONTAINER_RUNTIME="podman"
        log_info "使用 Podman 作为容器运行环境"
    elif command -v docker &> /dev/null; then
        CONTAINER_RUNTIME="docker"
        log_info "使用 Docker 作为容器运行环境"
    else
        log_error "未找到 Podman 或 Docker，请先安装容器运行环境"
        exit 1
    fi
}

# Maven 打包
maven_package() {
    log_info "开始 Maven 打包..."
    cd "$PROJECT_ROOT"

    # 单体模式打包
    mvn clean package -DskipTests -am -pl onebase-server -pl onebase-server-runtime -pl onebase-server-flink

    log_success "Maven 打包完成"
}

# 复制 JAR 文件到 lib 目录
copy_jars() {
    log_info "复制 JAR 文件到 lib 目录..."

    mkdir -p "$SCRIPT_DIR/lib"

    # 复制 onebase-server.jar
    if [ -f "$PROJECT_ROOT/onebase-server/target/onebase-server.jar" ]; then
        cp "$PROJECT_ROOT/onebase-server/target/onebase-server.jar" "$SCRIPT_DIR/lib/"
        log_success "复制 onebase-server.jar 完成"
    else
        log_error "未找到 onebase-server.jar"
        exit 1
    fi

    # 复制 onebase-server-runtime.jar
    if [ -f "$PROJECT_ROOT/onebase-server-runtime/target/onebase-server-runtime.jar" ]; then
        cp "$PROJECT_ROOT/onebase-server-runtime/target/onebase-server-runtime.jar" "$SCRIPT_DIR/lib/"
        log_success "复制 onebase-server-runtime.jar 完成"
    else
        log_warn "未找到 onebase-server-runtime.jar，跳过"
    fi

    # 复制 onebase-server-flink.jar
    if [ -f "$PROJECT_ROOT/onebase-server-flink/target/onebase-server-flink.jar" ]; then
        cp "$PROJECT_ROOT/onebase-server-flink/target/onebase-server-flink.jar" "$SCRIPT_DIR/lib/"
        log_success "复制 onebase-server-flink.jar 完成"
    else
        log_warn "未找到 onebase-server-flink.jar，跳过"
    fi
}

# 构建 dist 模式（仅构建产物到本地 dist 目录）
build_dist() {
    log_info "开始构建 dist 产物..."

    show_arch

    # Maven 打包
    maven_package

    # 创建 dist 目录结构
    mkdir -p "$SCRIPT_DIR/dist/config"
    mkdir -p "$SCRIPT_DIR/dist/scripts"

    # 复制 JAR 文件
    copy_jars

    # 复制 JAR 到 dist 目录
    cp "$SCRIPT_DIR/lib/*.jar" "$SCRIPT_DIR/dist/" 2>/dev/null || true

    # 复制配置模板
    if [ -d "$SCRIPT_DIR/config" ]; then
        cp -r "$SCRIPT_DIR/config/*" "$SCRIPT_DIR/dist/config/" 2>/dev/null || true
    fi

    # 复制部署脚本
    cp "$SCRIPT_DIR/deploy.sh" "$SCRIPT_DIR/dist/scripts/"
    cp "$SCRIPT_DIR/middleware/docker-compose.middleware.yaml" "$SCRIPT_DIR/dist/scripts/" 2>/dev/null || true

    # 创建版本信息文件
    echo "VERSION=$VERSION" > "$SCRIPT_DIR/dist/VERSION"
    echo "BUILD_DATE=$(date '+%Y-%m-%d %H:%M:%S')" >> "$SCRIPT_DIR/dist/VERSION"
    echo "ARCH=$(detect_arch)" >> "$SCRIPT_DIR/dist/VERSION"

    log_success "dist 构建完成！产物位于: $SCRIPT_DIR/dist/"
    log_info "JAR 文件: onebase-server.jar, onebase-server-runtime.jar, onebase-server-flink.jar"
    log_info "配置模板: config/"
    log_info "部署脚本: scripts/"
}

# 构建 JVM 基础镜像
build_jvm_image() {
    log_info "构建 JVM 基础镜像..."

    local arch=$(detect_arch)
    local image_name="onebase-jvm:${VERSION}"

    # 检查镜像是否已存在
    if $CONTAINER_RUNTIME images --filter "reference=onebase-jvm" --format "{{.Repository}}" | grep -q "onebase-jvm"; then
        log_warn "JVM 镜像已存在，跳过构建"
        return
    fi

    cd "$SCRIPT_DIR/images"
    $CONTAINER_RUNTIME build --platform "$arch" -t "$image_name" -f onebase-jvm.Dockerfile .

    log_success "JVM 基础镜像构建完成: $image_name"
}

# 构建服务镜像
build_service_image() {
    local service_name=$1
    local jar_name=$2
    local port=$3

    log_info "构建 ${service_name} 镜像..."

    local arch=$(detect_arch)
    local image_name="onebase-server-${service_name}:${VERSION}"

    cd "$SCRIPT_DIR/images"

    # 创建临时目录
    local tmp_dir="$SCRIPT_DIR/images/tmp_${service_name}"
    mkdir -p "$tmp_dir/lib"
    mkdir -p "$tmp_dir/conf"

    # 复制 JAR 文件
    if [ -f "$SCRIPT_DIR/lib/${jar_name}" ]; then
        cp "$SCRIPT_DIR/lib/${jar_name}" "$tmp_dir/lib/"
    else
        log_error "未找到 JAR 文件: ${jar_name}"
        exit 1
    fi

    # 复制配置模板（如果存在）
    if [ -f "$SCRIPT_DIR/config/application-${service_name}.yaml.template" ]; then
        cp "$SCRIPT_DIR/config/application-${service_name}.yaml.template" "$tmp_dir/conf/application.yaml"
    fi

    # 构建镜像
    $CONTAINER_RUNTIME build --platform "$arch" \
        -t "$image_name" \
        -f "onebase-server-${service_name}.Dockerfile" \
        --build-arg SERVICE_NAME="${service_name}" \
        --build-arg JAR_NAME="${jar_name}" \
        --build-arg PORT="${port}" \
        "$tmp_dir"

    # 清理临时目录
    rm -rf "$tmp_dir"

    log_success "${service_name} 镜像构建完成: $image_name"
}

# 构建所有镜像
build_all_images() {
    log_info "开始构建所有镜像..."

    show_arch
    check_container_runtime

    # Maven 打包
    maven_package

    # 复制 JAR 文件
    copy_jars

    # 构建 JVM 基础镜像
    build_jvm_image

    # 构建各服务镜像
    build_service_image "build" "onebase-server.jar" "28080"
    build_service_image "runtime" "onebase-server-runtime.jar" "38080"
    build_service_image "flink" "onebase-server-flink.jar" "8080"

    log_success "所有镜像构建完成！"

    # 显示镜像列表
    $CONTAINER_RUNTIME images --filter "reference=onebase" --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}"
}

# 导出镜像到 dist 目录
export_images() {
    log_info "导出镜像到 dist 目录..."

    mkdir -p "$SCRIPT_DIR/dist"

    # 导出各镜像
    $CONTAINER_RUNTIME save onebase-jvm:${VERSION} > "$SCRIPT_DIR/dist/onebase-jvm-${VERSION}.tar"
    $CONTAINER_RUNTIME save onebase-server-build:${VERSION} > "$SCRIPT_DIR/dist/onebase-server-build-${VERSION}.tar"
    $CONTAINER_RUNTIME save onebase-server-runtime:${VERSION} > "$SCRIPT_DIR/dist/onebase-server-runtime-${VERSION}.tar"
    $CONTAINER_RUNTIME save onebase-server-flink:${VERSION} > "$SCRIPT_DIR/dist/onebase-server-flink-${VERSION}.tar"

    log_success "镜像导出完成！位于: $SCRIPT_DIR/dist/"
}

# 构建镜像模式（构建完整镜像并导出）
build_image() {
    build_all_images
    export_images
}

# 使用帮助
usage() {
    echo "OneBase Server 构建脚本"
    echo ""
    echo "用法: build.sh <命令>"
    echo ""
    echo "命令:"
    echo "  image     构建完整 Docker/Podman 镜像并导出到 dist 目录"
    echo "  dist      仅构建产物到本地 dist 目录（不构建镜像）"
    echo ""
    echo "示例:"
    echo "  build.sh image    # 构建镜像"
    echo "  build.sh dist     # 构建产物"
    echo ""
}

# 主函数
main() {
    cd "$SCRIPT_DIR"

    case "$1" in
        image)
            build_image
            ;;
        dist)
            build_dist
            ;;
        *)
            usage
            exit 1
            ;;
    esac
}

main "$@"