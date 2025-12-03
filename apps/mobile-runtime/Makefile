# Runtime - Makefile
# 运行时应用的便捷命令

.PHONY: help install dev build clean lint preview

# 默认目标
help:
	@echo "Admin Console - 可用命令:"
	@echo ""
	@echo "安装依赖:"
	@echo "  make install     - 安装依赖"
	@echo ""
	@echo "开发命令:"
	@echo "  make dev         - 启动开发服务器"
	@echo ""
	@echo "构建命令:"
	@echo "  make build       - 构建生产版本"
	@echo ""
	@echo "其他命令:"
	@echo "  make clean       - 清理构建文件"
	@echo "  make lint        - 运行代码检查"
	@echo "  make preview     - 预览构建结果"

# 安装依赖
install:
	@echo "📦 安装依赖..."
	pnpm install

# 开发命令
dev:
	@echo "🚀 启动开发服务器..."
	pnpm dev

# 构建命令
build:
	@echo "🔨 构建生产版本..."
	pnpm build

# 清理命令
clean:
	@echo "🧹 清理构建文件..."
	rm -rf dist
	rm -rf node_modules

# 代码检查
lint:
	@echo "🔍 运行代码检查..."
	pnpm lint

# 预览命令
preview:
	@echo "👀 预览构建结果..."
	pnpm preview
