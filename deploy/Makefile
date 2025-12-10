# Docker 镜像配置
REGISTRY ?=
IMAGE_PREFIX ?= onebase-v3-fe
VERSION ?= v3.0.1
SAVE_DIR ?= ./images
PLATFORM ?= linux/arm64

# 应用名称
ADMIN_CONSOLE_IMAGE = $(if $(REGISTRY),$(REGISTRY)/)$(IMAGE_PREFIX)/admin-console:$(VERSION)
APP_BUILDER_IMAGE = $(if $(REGISTRY),$(REGISTRY)/)$(IMAGE_PREFIX)/app-builder:$(VERSION)
RUNTIME_IMAGE = $(if $(REGISTRY),$(REGISTRY)/)$(IMAGE_PREFIX)/runtime:$(VERSION)

# Docker build 平台参数
DOCKER_BUILD_PLATFORM = $(if $(PLATFORM),--platform $(PLATFORM),)

# 镜像文件名
ADMIN_CONSOLE_TAR = $(SAVE_DIR)/admin-console-$(VERSION).tar
APP_BUILDER_TAR = $(SAVE_DIR)/app-builder-$(VERSION).tar
RUNTIME_TAR = $(SAVE_DIR)/runtime-$(VERSION).tar

# Dockerfile 路径
DOCKERFILE_DIR = $(shell pwd)
PROJECT_ROOT = $(shell dirname $(DOCKERFILE_DIR))

.PHONY: help build build-admin-console build-app-builder build-runtime save save-all clean compose-up compose-down compose-ps compose-logs

help: ## 显示帮助信息
	@echo "可用命令:"
	@echo "  make build                    - 构建所有应用的 Docker 镜像"
	@echo "  make build-admin-console      - 构建 admin-console 镜像"
	@echo "  make build-app-builder        - 构建 app-builder 镜像"
	@echo "  make build-runtime            - 构建 runtime 镜像"
	@echo "  make save                     - 保存所有镜像为 tar 文件"
	@echo "  make save-admin-console       - 保存 admin-console 镜像"
	@echo "  make save-app-builder         - 保存 app-builder 镜像"
	@echo "  make save-runtime             - 保存 runtime 镜像"
	@echo "  make compose-up               - 启动所有服务 (docker-compose up)"
	@echo "  make compose-down             - 停止所有服务 (docker-compose down)"
	@echo "  make compose-ps               - 查看服务状态 (docker-compose ps)"
	@echo "  make compose-logs             - 查看服务日志 (docker-compose logs)"
	@echo "  make clean                    - 清理本地构建的镜像"
	@echo ""
	@echo "环境变量:"
	@echo "  REGISTRY      - Docker 镜像仓库地址 (默认: 空)"
	@echo "  IMAGE_PREFIX  - 镜像前缀 (默认: onebase-v3-fe)"
	@echo "  VERSION       - 镜像版本标签 (默认: latest)"
	@echo "  SAVE_DIR      - 镜像保存目录 (默认: ./images)"
	@echo "  PLATFORM      - 构建平台 (默认: 空, 示例: linux/amd64)"

build: build-admin-console build-app-builder build-runtime ## 构建所有应用的 Docker 镜像

build-admin-console: ## 构建 admin-console 镜像
	@echo "构建 admin-console 镜像: $(ADMIN_CONSOLE_IMAGE)$(if $(PLATFORM), [平台: $(PLATFORM)],)"
	docker build $(DOCKER_BUILD_PLATFORM) -f $(DOCKERFILE_DIR)/console.Dockerfile -t $(ADMIN_CONSOLE_IMAGE) $(PROJECT_ROOT)
	@echo "构建完成: $(ADMIN_CONSOLE_IMAGE)"

build-app-builder: ## 构建 app-builder 镜像
	@echo "构建 app-builder 镜像: $(APP_BUILDER_IMAGE)$(if $(PLATFORM), [平台: $(PLATFORM)],)"
	docker build $(DOCKER_BUILD_PLATFORM) -f $(DOCKERFILE_DIR)/builder.Dockerfile -t $(APP_BUILDER_IMAGE) $(PROJECT_ROOT)
	@echo "构建完成: $(APP_BUILDER_IMAGE)"

build-runtime: ## 构建 runtime 镜像
	@echo "构建 runtime 镜像: $(RUNTIME_IMAGE)$(if $(PLATFORM), [平台: $(PLATFORM)],)"
	docker build $(DOCKER_BUILD_PLATFORM) -f $(DOCKERFILE_DIR)/runtime.Dockerfile -t $(RUNTIME_IMAGE) $(PROJECT_ROOT)
	@echo "构建完成: $(RUNTIME_IMAGE)"

save: save-admin-console save-app-builder save-runtime ## 保存所有镜像为 tar 文件

save-admin-console: build-admin-console ## 保存 admin-console 镜像
	@echo "保存 admin-console 镜像: $(ADMIN_CONSOLE_IMAGE) -> $(ADMIN_CONSOLE_TAR)"
	@mkdir -p $(SAVE_DIR)
	docker save -o $(ADMIN_CONSOLE_TAR) $(ADMIN_CONSOLE_IMAGE)
	@echo "保存完成: $(ADMIN_CONSOLE_TAR)"

save-app-builder: build-app-builder ## 保存 app-builder 镜像
	@echo "保存 app-builder 镜像: $(APP_BUILDER_IMAGE) -> $(APP_BUILDER_TAR)"
	@mkdir -p $(SAVE_DIR)
	docker save -o $(APP_BUILDER_TAR) $(APP_BUILDER_IMAGE)
	@echo "保存完成: $(APP_BUILDER_TAR)"

save-runtime: build-runtime ## 保存 runtime 镜像
	@echo "保存 runtime 镜像: $(RUNTIME_IMAGE) -> $(RUNTIME_TAR)"
	@mkdir -p $(SAVE_DIR)
	docker save -o $(RUNTIME_TAR) $(RUNTIME_IMAGE)
	@echo "保存完成: $(RUNTIME_TAR)"

compose-up: build ## 启动所有服务
	@echo "启动 docker-compose 服务..."
	IMAGE_NAME_ADMIN_CONSOLE=$(ADMIN_CONSOLE_IMAGE) \
	IMAGE_NAME_APP_BUILDER=$(APP_BUILDER_IMAGE) \
	IMAGE_NAME_RUNTIME=$(RUNTIME_IMAGE) \
	docker-compose -f $(DOCKERFILE_DIR)/docker-compose.yml up -d
	@echo "服务启动完成"

compose-down: ## 停止所有服务
	@echo "停止 docker-compose 服务..."
	docker-compose -f $(DOCKERFILE_DIR)/docker-compose.yml down
	@echo "服务已停止"

compose-ps: ## 查看服务状态
	docker-compose -f $(DOCKERFILE_DIR)/docker-compose.yml ps

compose-logs: ## 查看服务日志
	docker-compose -f $(DOCKERFILE_DIR)/docker-compose.yml logs -f

clean: ## 清理本地构建的镜像
	@echo "清理本地镜像..."
	docker rmi $(ADMIN_CONSOLE_IMAGE) $(APP_BUILDER_IMAGE) $(RUNTIME_IMAGE) 2>/dev/null || true
	@echo "清理完成"
