# ===========================================
# OneBase V3 FE 多阶段构建 Dockerfile
# 支持两种构建模式：
# 1. 完整镜像：包含构建产物，可直接运行
# 2. 运行时镜像：需要挂载外部构建产物
# ===========================================

# ==================== 构建阶段 ====================
FROM node:20-alpine AS builder

WORKDIR /app

# 安装 pnpm
RUN npm install -g pnpm@9.15.9

# 复制依赖配置（利用缓存）
COPY package.json pnpm-lock.yaml pnpm-workspace.yaml ./
COPY apps/*/package.json ./apps/
COPY packages/*/package.json ./packages/
COPY sdks/*/package.json ./sdks/

# 安装依赖
RUN pnpm install --frozen-lockfile

# 复制源代码
COPY . .

# 构建所有应用
RUN pnpm build:packages && \
    pnpm build:plugins && \
    pnpm build:admin-console && \
    pnpm build:app-builder && \
    pnpm build:runtime && \
    pnpm build:mobile-runtime && \
    pnpm build:mobile-editor

# ==================== App Builder 镜像 ====================
FROM nginx:1.28-alpine3.21 AS app-builder

COPY --from=builder /app/apps/app-builder/dist /usr/share/nginx/html
COPY deploy/podman/nginx/app-builder.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

# ==================== Admin Console 镜像 ====================
FROM nginx:1.28-alpine3.21 AS admin-console

COPY --from=builder /app/apps/admin-console/dist /usr/share/nginx/html
COPY deploy/podman/nginx/admin-console.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

# ==================== Runtime 镜像 ====================
FROM nginx:1.28-alpine3.21 AS runtime

COPY --from=builder /app/apps/runtime/dist /usr/share/nginx/html
COPY deploy/podman/nginx/runtime.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

# ==================== Mobile Runtime 镜像 ====================
FROM nginx:1.28-alpine3.21 AS mobile-runtime

COPY --from=builder /app/apps/mobile-runtime/dist /usr/share/nginx/html
COPY deploy/podman/nginx/mobile-runtime.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

# ==================== Mobile Editor 镜像 ====================
FROM nginx:1.28-alpine3.21 AS mobile-editor

COPY --from=builder /app/apps/mobile-editor/dist /usr/share/nginx/html
COPY deploy/podman/nginx/mobile-editor.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

# ==================== 网关镜像 ====================
FROM nginx:1.28-alpine3.21 AS gateway

COPY deploy/podman/nginx/gateway.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]