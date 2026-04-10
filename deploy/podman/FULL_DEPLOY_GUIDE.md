# OneBase V3 完整部署手册

本文档描述 OneBase V3 平台的完整部署流程，包含后端和前端服务。

---

## 一、环境要求

### 1. 服务器要求

| 项目 | 要求 |
|------|------|
| CPU 架构 | x86_64 (amd64) 或 arm64 (aarch64) ≥ 32C | 
| 内存 | ≥ 64GB |
| 磁盘 | ≥ 300GB |

### 2. 软件依赖

| 软件 | 版本要求 | 用途 |
|------|---------|------|
| Podman | ≥ 4.0 | 容器运行时 |
| Git | 任意版本 | 代码拉取 |

### 3. 端口规划

**后端服务**（后端团队补充）：

| 服务 | 端口 | 说明 |
|------|------|------|
| 后端 API | 8080 | 后端主服务 |
| Flink | 8081 | Flink 服务（可选） |

**前端服务**：

| 服务 | 端口 | 说明 |
|------|------|------|
| 网关 | 80 | 统一入口（路径模式） |
| App Builder | 4399 | 应用构建器（端口模式） |
| Admin Console | 4402 | 管理控制台（端口模式） |
| Runtime | 9527 | 应用运行时（端口模式） |
| Mobile Editor | 4401 | 移动端编辑器（端口模式） |
| Mobile Runtime | 9528 | 移动端运行时（端口模式） |

### 4. 查看服务器架构

```bash
# Linux
uname -m
# 输出：x86_64 或 aarch64（即 arm64）

# macOS
arch
# 输出：arm64 或 i386
```

---

## 二、代码拉取

### 1. 创建部署目录

```bash
mkdir -p /opt/onebase
cd /opt/onebase
```

### 2. 拉取代码

**后端代码**：
```bash
git clone <后端仓库地址> backend
```

**前端代码**：
```bash
git clone <前端仓库地址> frontend
```

### 3. 目录结构

```
/opt/onebase/
├── backend/          # 后端代码
│   └── deploy/       # 后端部署脚本（后端团队提供）
└── frontend/         # 前端代码
    └── deploy/
        └── podman/   # 前端部署脚本
            ├── build.sh
            ├── deploy.sh
            ├── middleware/
            ├── nginx/
            └── ...
```

---

## 三、后端部署

> **注意**：后端服务必须先于前端部署，前端网关需要代理后端 API。

### 1. 部署后端服务

```bash
cd /opt/onebase/backend

# 后端团队补充具体部署步骤
# ...
```

### 2. 验证后端服务

```bash
# 检查后端服务状态
curl http://localhost:8080/actuator/health

```

### 3. 记录后端服务地址

部署完成后，记录后端服务地址供前端配置使用：

| 配置项 | 说明 | 示例 |
|--------|------|------|
| 后端 API 地址 | 后端服务地址 | `<后端服务器IP>:8080`（示例：`10.0.1.100:8080`） |

---

## 四、前端部署

### 1. 进入前端部署目录

```bash
cd /opt/onebase/frontend/deploy/podman
```

### 2. 初始化配置

```bash
./deploy.sh init
```

输出示例：
```
[INFO] 创建配置文件: /opt/onebase/frontend/deploy/podman/config.env
[INFO] 创建环境文件: /opt/onebase/frontend/deploy/podman/.env

检测到服务器架构: linux/amd64

请修改以下配置文件：
  1. /opt/onebase/frontend/deploy/podman/config.env - 部署配置
  2. /opt/onebase/frontend/deploy/podman/.env - 环境变量（确认 PLATFORM=linux/amd64）
```

### 3. 编辑配置文件

**编辑 config.env**：

```bash
vim config.env
```

必填配置：
```bash
# 主题（default / tiangong / lingji）
THEME=default

# 后端 API 地址
# 格式：http://<后端服务器IP>:<端口>
BACKEND_URL=http://<后端服务器IP>:8080
# 示例：BACKEND_URL=http://10.0.1.100:8080

# 前端访问地址
# 格式：http://<前端服务器IP或域名>
FRONTEND_BASE_URL=http://<前端服务器IP>
# 示例：FRONTEND_BASE_URL=http://10.0.1.200
```

可选配置：
```bash
# AI 服务地址（自动生成 /aigenapp/ 和 /aicopilot/ 路径）
AI_BASE_URL=https://<AI服务域名>
# 示例：AI_BASE_URL=https://ai.example.com

# SSO 配置
SSO_HOME=https://<SSO服务域名>/home
# 示例：SSO_HOME=https://sso.example.com/home
SSO_SOURCE_ID=onebase_prod
```

**确认 .env 中的架构**：

```bash
vim .env
```

确认 `PLATFORM` 配置：
```bash
# x86_64 服务器
PLATFORM=linux/amd64

# arm64 服务器
PLATFORM=linux/arm64
```

### 4. 配置后端 API 代理

**创建 middleware 配置文件**：

```bash
cd /opt/onebase/frontend/deploy/podman

# 复制模板
cp middleware/onebase-upstream.conf.example middleware/onebase-upstream.conf
cp middleware/onebase-location.conf.example middleware/onebase-location.conf
```

**编辑 upstream 配置**：

```bash
vim middleware/onebase-upstream.conf
```

修改后端服务地址：
```nginx
upstream onebase-backend {
    # 修改为实际后端地址
    server <后端服务器IP>:8080;
    # 示例：server 10.0.1.100:8080;
}

upstream onebase-flink {
    # 修改为实际 Flink 地址（如不使用可删除此块）
    server <Flink服务器IP>:8081;
    # 示例：server 10.0.1.101:8081;
}
```

### 5. 构建前端

**方式一：本地构建**

```bash
./build.sh dist
```

构建产物输出到 `deploy/podman/dist/` 目录。

**方式二：构建镜像**

```bash
./build.sh image
```

### 6. 启动服务

```bash
# 从本地 dist 启动
./deploy.sh up --dist

# 或从镜像启动
./deploy.sh up --image
```

输出示例：
```
[STEP] 启动服务 (模式: dist)
[INFO] 部署模式: path
[INFO] 主题: default
[INFO] 后端地址: http://<后端服务器IP>:8080
[INFO] 启动网关 (端口: 80)
[INFO] 服务启动完成

网关入口: http://<前端服务器IP>

访问地址:
  App Builder:    http://<前端服务器IP>/appbuilder/
  Admin Console:  http://<前端服务器IP>/console/
  Runtime:        http://<前端服务器IP>/runtime/
  Mobile Editor:  http://<前端服务器IP>/mobile-editor/
  Mobile Runtime: http://<前端服务器IP>/mobile-runtime/
```

---

## 五、验证部署

### 1. 检查服务状态

```bash
cd /opt/onebase/frontend/deploy/podman
./deploy.sh ps
```

### 2. 检查前端访问

```bash
# 访问 App Builder
curl http://localhost/appbuilder/

# 检查前端配置
curl http://localhost/appbuilder/config.js
```

### 3. 检查 API 代理

```bash
# 检查后端 API 代理
curl http://localhost/build-api/health
curl http://localhost/runtime-api/health
```

### 4. 浏览器访问

打开浏览器访问：
- App Builder: `http://<前端服务器IP>/appbuilder/`
- Admin Console: `http://<前端服务器IP>/console/`

---

## 六、访问指南

### 1. 服务访问地址

| 服务 | 地址格式 | 示例 |
|------|----------|------|
| App Builder | `http://<前端服务器IP>/appbuilder/` | `http://10.0.1.200/appbuilder/` |
| Admin Console | `http://<前端服务器IP>/console/` | `http://10.0.1.200/console/` |
| Runtime | `http://<前端服务器IP>/runtime/` | `http://10.0.1.200/runtime/` |
| Mobile Editor | `http://<前端服务器IP>/mobile-editor/` | `http://10.0.1.200/mobile-editor/` |
| Mobile Runtime | `http://<前端服务器IP>/mobile-runtime/` | `http://10.0.1.200/mobile-runtime/` |

### 2. 首次使用流程

1. **登录管理控制台**
   - 访问 `http://<前端服务器IP>/console/`
   - 使用管理员账号登录

2. **创建租户**
   - 进入平台管理
   - 新建租户并配置权限

3. **进入应用构建器**
   - 访问 `http://<前端服务器IP>/appbuilder/`
   - 选择租户登录
   - 创建应用

4. **运行应用**
   - 访问 `http://<前端服务器IP>/runtime/?app=<应用ID>`

---

## 七、常用命令

### 前端服务管理

```bash
cd /opt/onebase/frontend/deploy/podman

# 查看服务状态
./deploy.sh ps

# 查看日志
./deploy.sh logs              # 所有服务
./deploy.sh logs gateway      # 网关日志
./deploy.sh logs app-builder  # App Builder 日志

# 重启服务
./deploy.sh restart

# 停止服务
./deploy.sh down

# 清理所有资源
./deploy.sh clean
```

### 后端服务管理

```bash
# 后端团队补充
```

---

## 八、配置文件说明

### 前端配置文件

| 文件 | 位置 | 说明 |
|------|------|------|
| config.env | deploy/podman/config.env | 前端部署配置 |
| .env | deploy/podman/.env | 构建环境变量 |
| onebase-upstream.conf | deploy/podman/middleware/ | 后端 upstream 定义 |
| onebase-location.conf | deploy/podman/middleware/ | 后端 API location 配置 |

### 后端配置文件

| 文件 | 位置 | 说明 |
|------|------|------|
| （后端团队补充） | | |

---

## 九、部署模式说明

### 路径模式（默认）

- 网关统一入口，端口 80
- 各服务通过路径区分：`/appbuilder/`、`/console/`、`/runtime/` 等
- 后端 API 通过网关代理：`/build-api/`、`/runtime-api/`、`/flink-api/`
- 适合生产环境

### 端口模式

- 各服务独立端口
- 需单独配置后端 API 代理
- 适合开发/测试环境

切换方式：
```bash
# 在 .env 中设置
DEPLOY_MODE=path   # 路径模式
DEPLOY_MODE=port   # 端口模式
```

---

## 十、故障排查

### 1. 前端无法访问

```bash
# 检查服务状态
./deploy.sh ps

# 检查网关日志
./deploy.sh logs gateway

# 检查端口占用
ss -tlnp | grep :80
```

### 2. API 代理失败

```bash
# 检查后端服务是否正常
curl http://<后端服务器IP>:8080/actuator/health

# 检查 middleware 配置
cat middleware/onebase-upstream.conf

# 进入容器检查 nginx 配置
podman exec onebase-gateway nginx -t
```

### 3. 架构不匹配

```bash
# 查看当前架构
uname -m

# 确认 .env 中的 PLATFORM
cat .env | grep PLATFORM
```

---

## 十一、附录

### 1. 完整目录结构

```
/opt/onebase/
├── backend/                              # 后端代码
│   ├── deploy/                           # 后端部署脚本
│   └── ...
└── frontend/                             # 前端代码
    ├── apps/                             # 前端应用
    ├── packages/                         # 共享包
    └── deploy/
        └── podman/                       # 前端部署目录
            ├── .env                      # 构建环境变量
            ├── .env.example              # 环境变量模板
            ├── config.env                # 部署配置
            ├── config.env.example        # 配置模板
            ├── build.sh                  # 构建脚本
            ├── deploy.sh                 # 部署脚本
            ├── Dockerfile                # 镜像构建文件
            ├── dist/                     # 构建产物
            ├── middleware/               # 后端 API 配置
            │   ├── onebase-upstream.conf
            │   ├── onebase-upstream.conf.example
            │   ├── onebase-location.conf
            │   ├── onebase-location.conf.example
            │   └── README.md
            └── nginx/                    # Nginx 配置
                ├── gateway.conf
                ├── app-builder.conf
                ├── admin-console.conf
                ├── runtime.conf
                ├── mobile-editor.conf
                └── mobile-runtime.conf
```

### 2. 环境变量速查

| 变量 | 文件 | 说明 | 示例 |
|------|------|------|------|
| PLATFORM | .env | 构建架构 | `linux/amd64` |
| DEPLOY_MODE | .env | 部署模式 | `path` |
| NGINX_PORT | .env | 网关端口 | `80` |
| THEME | config.env | 主题 | `default` |
| BACKEND_URL | config.env | 后端地址 | `http://<后端服务器IP>:8080` |
| FRONTEND_BASE_URL | config.env | 前端地址 | `http://<前端服务器IP>` |
| AI_BASE_URL | config.env | AI 服务地址 | `https://<AI服务域名>` |

### 3. 示例配置速查

| 场景 | 配置项 | 格式 |
|------|--------|------|
| 后端地址 | BACKEND_URL | `http://<后端服务器IP>:8080` |
| 前端地址 | FRONTEND_BASE_URL | `http://<前端服务器IP>` |
| AI 服务 | AI_BASE_URL | `https://<AI服务域名>` |
| SSO 服务 | SSO_HOME | `https://<SSO服务域名>/home` |
| Flink 地址 | middleware/onebase-upstream.conf | `server <Flink服务器IP>:8081;` |

---

## 十二、更新日志

| 日期 | 版本 | 说明 |
|------|------|------|
| 2026-04-10 | v1.0 | 初始版本 |