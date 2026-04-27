#!/bin/bash

# OneBase Platform Frontend - 构建脚本
# 快速构建项目

set -e

echo "🔨 OneBase Platform Frontend 构建脚本"
echo "======================================"

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

# 构建选择
echo "🔨 选择构建模式:"
echo "1) 构建所有项目 (推荐)"
echo "2) 仅构建管理控制台"
echo "3) 仅构建所有包"
echo "4) 清理后构建所有项目"

read -p "请选择 (1-4): " choice

case $choice in
    1)
        echo "构建所有项目..."
        pnpm --recursive build
        ;;
    2)
        echo "构建管理控制台..."
        cd apps/admin-console && pnpm build
        ;;
    3)
        echo "构建所有包..."
        cd packages/common && pnpm build
        cd ../ui-kit && pnpm build
        ;;
    4)
        echo "清理后构建所有项目..."
        make clean
        pnpm install
        pnpm --recursive build
        ;;
    *)
        echo "❌ 无效选择，构建所有项目..."
        pnpm --recursive build
        ;;
esac

echo "✅ 构建完成！"