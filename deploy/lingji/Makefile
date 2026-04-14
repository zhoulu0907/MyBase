# Makefile for building Debian 12 + OpenJDK 17 base image

# 镜像配置
IMAGE_NAME ?= debian-security
IMAGE_TAG ?= bookworm-17-jre
IMAGE_FULL ?= $(IMAGE_NAME):$(IMAGE_TAG)

# 默认仓库（内网仓库）
REGISTRY ?= artifact.ec.rdcloud.4c.hq.cmcc:80/onebase

# 完整的镜像路径（带仓库）
IMAGE_WITH_REGISTRY = $(REGISTRY)/$(IMAGE_FULL)

.PHONY: all build tag push run test clean help

# 默认目标
all: build

## help: 显示帮助信息
help:
	@echo "Usage: make [target]"
	@echo ""
	@echo "Targets:"
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)

## build: 构建 Docker 镜像
build:
	@echo "Building $(IMAGE_FULL)..."
	docker build --no-cache -t $(IMAGE_FULL) .
	@echo "Build completed: $(IMAGE_FULL)"

## tag: 给镜像打 registry 标签
tag: build
	docker tag $(IMAGE_FULL) $(IMAGE_WITH_REGISTRY)

## push: 推送镜像到仓库
push: tag
	@echo "Pushing $(IMAGE_WITH_REGISTRY)..."
	docker push $(IMAGE_WITH_REGISTRY)

build-multi: ## 构建多平台基础镜像并推送到 Registry (amd64 + arm64)
	@echo "▶ 构建并推送多平台镜像 $(IMAGE_WITH_REGISTRY)..."
	@docker buildx inspect multiplatform-builder >/dev/null 2>&1 || \
		(docker buildx create --name multiplatform-builder --driver docker-container --config buildkitd.toml --use && docker buildx inspect --bootstrap multiplatform-builder)
	@docker buildx use multiplatform-builder
	docker buildx build -f Dockerfile \
		--platform linux/amd64,linux/arm64 \
		--no-cache \
		--pull=false \
		--push \
		--progress=auto \
		--provenance=false \
		-t $(IMAGE_WITH_REGISTRY) \
		.
	@echo "✓ 推送完成: $(IMAGE_WITH_REGISTRY)"

## run: 运行容器进行测试
run: build
	docker run --rm -it $(IMAGE_FULL) /bin/bash

## test: 测试镜像
test: build
	@docker run --rm $(IMAGE_FULL) java -version
	@docker run --rm $(IMAGE_FULL) cat /etc/os-release | grep PRETTY

## clean: 清理本地镜像
clean:
	docker rmi -f $(IMAGE_FULL) $(IMAGE_WITH_REGISTRY) 2>/dev/null || true

## info: 显示镜像信息
info:
	@echo "Image: $(IMAGE_WITH_REGISTRY)"
	@docker images $(IMAGE_NAME) --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}" 2>/dev/null || echo "Not found"
