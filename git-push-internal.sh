#!/bin/bash
# onebase-v3-be 项目专用推送脚本
# 用途：推送到内网Git仓库时自动处理代理配置
# 日期：2026-01-17

set -e  # 遇到错误立即退出

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}========================================${NC}"
echo -e "${YELLOW}  OneBase v3 内网仓库推送工具${NC}"
echo -e "${YELLOW}========================================${NC}"
echo ""

# 1. 取消代理
echo -e "${YELLOW}🔄 步骤 1/4: 取消代理配置...${NC}"
git config --global --unset http.proxy 2>/dev/null || true
git config --global --unset https.proxy 2>/dev/null || true
echo -e "${GREEN}✓ 代理已取消${NC}"
echo ""

# 2. 检查是否有待推送的提交
echo -e "${YELLOW}🔄 步骤 2/4: 检查状态...${NC}"
git status -sb
echo ""

# 3. 推送代码
echo -e "${YELLOW}🔄 步骤 3/4: 推送到远程仓库...${NC}"
if git push origin dev; then
    echo -e "${GREEN}✓ 推送成功${NC}"
    PUSH_STATUS=0
else
    echo -e "${RED}✗ 推送失败${NC}"
    PUSH_STATUS=1
fi
echo ""

# 4. 恢复代理
echo -e "${YELLOW}🔄 步骤 4/4: 恢复代理配置...${NC}"
git config --global http.proxy http://127.0.0.1:7890
git config --global https.proxy http://127.0.0.1:7890
echo -e "${GREEN}✓ 代理已恢复: http://127.0.0.1:7890${NC}"
echo ""

# 结果总结
echo -e "${YELLOW}========================================${NC}"
if [ $PUSH_STATUS -eq 0 ]; then
    echo -e "${GREEN}✅ 推送完成！${NC}"
    echo ""
    echo "最近的提交："
    git log --oneline -3
else
    echo -e "${RED}❌ 推送失败，请检查网络或仓库状态${NC}"
    exit 1
fi
echo -e "${YELLOW}========================================${NC}"
