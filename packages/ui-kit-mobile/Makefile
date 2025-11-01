# UI Kit Package - Makefile
# UI组件库的便捷命令

.PHONY: help install dev build clean

# 默认目标
help:
	@echo "UI Kit Package - 可用命令:"
	@echo ""
	@echo "安装依赖:"
	@echo "  make install     - 安装依赖"
	@echo ""
	@echo "开发命令:"
	@echo "  make dev         - 启动开发模式（监听文件变化）"
	@echo ""
	@echo "构建命令:"
	@echo "  make build       - 构建包"
	@echo ""
	@echo "其他命令:"
	@echo "  make clean       - 清理构建文件"

# 安装依赖
install:
	@echo "📦 安装依赖..."
	pnpm install

# 开发命令
dev:
	@echo "🚀 启动开发模式..."
	pnpm dev

# 构建命令
build:
	@echo "🔨 构建包..."
	pnpm build

# 清理命令
clean:
	@echo "🧹 清理构建文件..."
	rm -rf dist
	rm -rf node_modules
