# OneBase Platform Frontend - 根目录 Makefile
# 提供整个项目的便捷命令

.PHONY: help install dev build clean lint test init

# 默认目标
help:
	@echo "OneBase Platform Frontend - 可用命令:"
	@echo ""
	@echo "初始化子项目:"
	@echo "  make init        - 下载所有子项目"
	@echo "安装依赖:"
	@echo "  make install     - 安装所有依赖"
	@echo "  make install-dev - 仅安装开发依赖"
	@echo ""
	@echo "开发命令:"
	@echo "  make dev         - 启动所有项目的开发模式"
	@echo "  make dev-admin   - 仅启动管理控制台开发模式"
	@echo "  make dev-common  - 仅启动公共包开发模式"
	@echo "  make dev-ui      - 仅启动UI组件库开发模式"
	@echo "  make dev-app     - 仅启动应用包开发模式"
	@echo ""
	@echo "构建命令:"
	@echo "  make build       - 构建所有项目"
	@echo "  make build-admin - 仅构建管理控制台"
	@echo "  make build-packages - 仅构建所有包"
	@echo ""
	@echo "其他命令:"
	@echo "  make clean       - 清理所有构建文件"
	@echo "  make lint        - 运行代码检查"
	@echo "  make test        - 运行测试"
	@echo "  make preview     - 预览构建结果"
	@echo "  make init        - 初始化所有 git 子模块"

# 安装依赖
install:
	@echo "📦 安装所有依赖..."
	pnpm install

install-dev:
	@echo "📦 安装开发依赖..."
	pnpm install --only=dev

# 开发命令
dev:
	@echo "🚀 启动所有项目的开发模式..."
	pnpm --parallel dev

dev-admin:
	@echo "🚀 启动管理控制台开发模式..."
	cd apps/admin-console && pnpm dev

dev-common:
	@echo "🚀 启动公共包开发模式..."
	cd packages/common && pnpm dev

dev-ui:
	@echo "🚀 启动UI组件库开发模式..."
	cd packages/ui-kit && pnpm dev

dev-app:
	@echo "🚀 启动应用包开发模式..."
	cd packages/app && pnpm dev

# 构建命令
build:
	@echo "🔨 构建所有项目..."
	pnpm --recursive build

build-admin:
	@echo "🔨 构建管理控制台..."
	cd apps/admin-console && pnpm build

build-packages:
	@echo "🔨 构建所有包..."
	cd packages/common && pnpm build
	cd packages/ui-kit && pnpm build
	cd packages/app && pnpm build

# 清理命令
clean:
	@echo "🧹 清理所有构建文件..."
	rm -rf apps/admin-console/dist
	rm -rf packages/common/dist
	rm -rf packages/ui-kit/dist
	rm -rf packages/app/dist
	rm -rf node_modules
	rm -rf apps/admin-console/node_modules
	rm -rf packages/common/node_modules
	rm -rf packages/ui-kit/node_modules

# 代码检查
lint:
	@echo "🔍 运行代码检查..."
	pnpm --recursive lint

# 测试命令
test:
	@echo "🧪 运行测试..."
	pnpm --recursive test

# 预览命令
preview:
	@echo "👀 预览构建结果..."
	cd apps/admin-console && pnpm preview

# 初始化 git 子模块
init:
	@echo "🔄 初始化所有 git 子模块..."
	git submodule update --init

	@echo "🔄 切换 apps 和 packages 下所有子项目到 dev 分支..."
	@for dir in apps/* packages/*; do \
		if [ -f "$$dir/.git" ] || [ -d "$$dir/.git" ]; then \
			echo "切换到 $$dir 并执行 git checkout dev"; \
			cd "$$dir" && git checkout dev && cd - > /dev/null; \
		fi \
	done
