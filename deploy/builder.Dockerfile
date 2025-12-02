# # 构建阶段
# FROM node:20-alpine3.22 AS builder

# # 设置工作目录
# WORKDIR /app

# # 安装 pnpm
# RUN npm install -g pnpm

# # 复制 package 文件
# COPY package.json pnpm-lock.yaml pnpm-workspace.yaml ./
# COPY apps/app-builder/package.json ./apps/app-builder/
# COPY packages/common/package.json ./packages/common/
# COPY packages/app/package.json ./packages/app/
# COPY packages/platform-center/package.json ./packages/platform-center/
# COPY packages/ui-kit/package.json ./packages/ui-kit/

# # 安装依赖
# RUN pnpm install --frozen-lockfile

# # 复制源代码
# COPY . .

# # 构建应用
# RUN pnpm build:app-builder

# 运行阶段
FROM nginx:1.28-alpine3.21

# 复制构建产物到 nginx
# COPY --from=builder /app/apps/app-builder/dist /usr/share/nginx/html

COPY  apps/app-builder/dist /usr/share/nginx/html

# 复制 nginx 配置文件（如果需要自定义配置）
# COPY nginx.conf /etc/nginx/conf.d/default.conf

# 暴露端口
EXPOSE 80

# 启动 nginx
CMD ["nginx", "-g", "daemon off;"]
