# OneBase V3 FE 部署方案

基于 Podman 的前端部署方案，支持端口模式和路径模式两种部署方式。

## 目录结构

```
deploy/podman/
├── Dockerfile              # 多阶段构建 Dockerfile
├── build.sh                # 构建脚本
├── deploy.sh               # 部署脚本
├── .env.example            # 环境变量示例
├── config.env.example      # 配置变量示例
├── nginx/                  # Nginx 配置
│   ├── app-builder.conf
│   ├── admin-console.conf
│   ├── runtime.conf
│   ├── mobile-runtime.conf
│   ├── mobile-editor.conf
│   └── gateway.conf
└── dist/                   # 构建产物（运行时生成）
    ├── app-builder/
    ├── admin-console/
    ├── runtime/
    ├── mobile-runtime/
    ├── mobile-editor/
    └── config/
```

## 快速开始

### 方式一：从本地 dist 部署（推荐开发/测试）

```bash
cd deploy/podman

# 1. 初始化配置
./deploy.sh init

# 2. 修改配置
vim config.env

# 3. 构建产物
./build.sh dist

# 4. 启动服务
./deploy.sh up --dist
```

### 方式二：从镜像部署（推荐生产）

```bash
cd deploy/podman

# 1. 初始化配置
./deploy.sh init

# 2. 修改配置
vim config.env
vim .env

# 3. 构建镜像
./build.sh image

# 4. 推送镜像（可选）
./build.sh image --push

# 5. 启动服务
./deploy.sh up --image
```

## 配置说明

### config.env（部署配置）

```bash
# 主题
THEME=default

# 后端 API 地址
BACKEND_URL=http://your-backend-server

# 前端访问地址
FRONTEND_BASE_URL=http://your-server

# AI 服务基础地址（可选，自动拼接 /aigenapp/ 和 /aicopilot/）
AI_BASE_URL=https://ai-server
```

### .env（构建配置）

```bash
# 部署模式：port | path
DEPLOY_MODE=port

# 镜像仓库
REGISTRY=
IMAGE_PREFIX=onebase-v3-fe
VERSION=v3.0.0
```

## 部署模式

### 端口模式（DEPLOY_MODE=port）

每个服务独立端口：
- App Builder: `http://server:4399`
- Admin Console: `http://server:4402`
- Runtime: `http://server:9527`
- Mobile Editor: `http://server:4401`
- Mobile Runtime: `http://server:9528`

### 路径模式（DEPLOY_MODE=path）

统一入口，按路径区分：
- App Builder: `http://server/appbuilder/`
- Admin Console: `http://server/console/`
- Runtime: `http://server/runtime/`
- Mobile Editor: `http://server/mobile-editor/`
- Mobile Runtime: `http://server/mobile-runtime/`

## 命令说明

### build.sh

| 命令 | 说明 |
|------|------|
| `image` | 构建 Docker 镜像 |
| `image --push` | 构建并推送镜像 |
| `image --save` | 构建并保存为 tar 文件 |
| `dist` | 构建产物到本地 dist 目录 |
| `clean` | 清理构建产物 |

### deploy.sh

| 命令 | 说明 |
|------|------|
| `init` | 初始化配置文件 |
| `config` | 生成配置文件 |
| `up --dist` | 从本地 dist 启动服务 |
| `up --image` | 从镜像启动服务 |
| `down` | 停止服务 |
| `ps` | 查看服务状态 |
| `logs [service]` | 查看日志 |
| `restart` | 重启服务 |
| `clean` | 清理所有资源 |

## 部署到服务器

### 方式一：复制 dist 目录

```bash
# 本地构建
./build.sh dist

# 打包
tar -czf onebase-dist.tar.gz dist/ config.env

# 上传到服务器
scp onebase-dist.tar.gz user@server:/opt/onebase/

# 服务器上解压并启动
ssh user@server
cd /opt/onebase
tar -xzf onebase-dist.tar.gz
./deploy.sh up --dist
```

### 方式二：使用镜像

```bash
# 构建并推送镜像
./build.sh image --push

# 服务器上拉取并运行
# 复制 config.env 到服务器
scp config.env user@server:/opt/onebase/

# 服务器上启动
ssh user@server
cd /opt/onebase
./deploy.sh up --image
```

## 安装 Podman

```bash
# macOS
brew install podman
podman machine init
podman machine start

# Linux (Ubuntu/Debian)
sudo apt-get update && sudo apt-get install -y podman

# Linux (RHEL/CentOS/Fedora)
sudo dnf install -y podman
```