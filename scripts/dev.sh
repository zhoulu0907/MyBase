#!/bin/bash

# OneBase Platform Frontend - 开发启动脚本
# 快速启动开发环境

set -e

echo "🚀 OneBase Platform Frontend 开发环境启动脚本"
echo "================================================"

# 检查是否安装了 pnpm
if ! command -v pnpm &> /dev/null; then
    echo "❌ 错误: 未找到 pnpm，请先安装 pnpm"
    echo "安装命令: npm install -g pnpm"
    exit 1
fi

# 检查是否在项目根目录
if [ ! -f "pnpm-workspace.yaml" ]; then
    echo "❌ 错误: 请在项目根目录运行此脚本"
    exit 1
fi

# 安装依赖
echo "📦 安装依赖..."
pnpm install

# 启动开发模式
echo "🚀 启动开发模式..."
echo "选择启动模式:"
echo "1) 启动所有项目 (推荐)"
echo "2) 仅启动管理控制台"
echo "3) 仅启动公共包"
echo "4) 仅启动UI组件库"
echo "5) 查看帮助"

read -p "请选择 (1-5): " choice

case $choice in
    1)
        echo "启动所有项目..."
        pnpm --parallel dev
        ;;
    2)
        echo "启动管理控制台..."
        cd apps/admin-console && pnpm dev
        ;;
    3)
        echo "启动公共包..."
        cd packages/common && pnpm dev
        ;;
    4)
        echo "启动UI组件库..."
        cd packages/ui-kit && pnpm dev
        ;;
    5)
        echo "显示帮助信息..."
        make help
        ;;
    *)
        echo "❌ 无效选择，启动所有项目..."
        pnpm --parallel dev
        ;;
esac