# OneBase V3 FE 部署指南

## 一、部署前准备

### 1. 需要收集的信息

| 信息 | 说明 | 示例 |
|------|------|------|
| `服务器架构` | CPU 架构类型 | `x86_64` / `arm64` |
| `BACKEND_URL` | 后端 API 地址 | `http://10.0.1.100:8080` 或 `https://api.example.com` |
| `FRONTEND_BASE_URL` | 前端访问地址（部署服务器地址） | `http://10.0.1.200` 或 `https://onebase.example.com` |
| `THEME` | 主题（根据部署环境选择） | `default` / `tiangong` / `lingji` |

> **说明**：`FRONTEND_BASE_URL` 是用户在浏览器访问前端的地址，即部署服务器的 IP 或域名。 |

### 2. 可选信息

| 信息 | 说明 | 默认值 |
|------|------|--------|
| `AI_BASE_URL` | AI 服务地址 | 空（不启用） |
| `FLINK_URL` | Flink 服务地址 | 空（不启用） |
| `DATASET_URL` | 数据集服务地址 | 使用 BACKEND_URL |
| `DASHBOARD_URL` | Dashboard 地址 | 使用 FRONTEND_BASE_URL |
| `SSO_HOME` | SSO 首页地址 | 空（不启用） |
| `SSO_SOURCE_ID` | SSO 应用ID | 空 |

### 3. 端口规划

**路径模式（默认）**：使用标准 HTTP 端口

| 服务 | 默认端口 | 说明 |
|------|---------|------|
| Gateway | 80 | 统一入口网关 |

各内部服务不暴露外部端口，通过网关访问。

**端口模式**：各服务独立端口

| 服务 | 默认端口 | 说明 |
|------|---------|------|
| App Builder | 4399 | 应用构建器 |
| Admin Console | 4402 | 管理控制台 |
| Runtime | 9527 | 应用运行时 |
| Mobile Editor | 4401 | 移动端编辑器 |
| Mobile Runtime | 9528 | 移动端运行时 |

---

## 二、部署步骤

### 方式一：从源码部署（推荐开发/测试）

```bash
# 1. 进入部署目录
cd deploy/podman

# 2. 初始化配置文件
./deploy.sh init
# 会生成：.env 和 config.env

# 3. 编辑配置文件
vim config.env
# 修改以下必填项：
#   BACKEND_URL=http://your-backend-server
#   FRONTEND_BASE_URL=http://your-server
#   THEME=default

# 4. 配置后端 API 代理（路径模式）
cp middleware/onebase-upstream.conf.example middleware/onebase-upstream.conf
cp middleware/onebase-location.conf.example middleware/onebase-location.conf
vim middleware/onebase-upstream.conf
# 修改 server 地址为实际后端地址

# 5. 构建前端产物
./build.sh dist
# 产物输出到：deploy/podman/dist/

# 6. 启动服务
./deploy.sh up --dist
```

### 方式二：从镜像部署（推荐生产）

```bash
# 1. 进入部署目录
cd deploy/podman

# 2. 初始化配置
./deploy.sh init

# 3. 编辑配置
vim config.env  # 部署配置
vim .env        # 构建配置（修改 PLATFORM 为服务器架构）
# .env 中 PLATFORM 配置：
#   x86_64 服务器 → linux/amd64
#   arm64 服务器 → linux/arm64

# 4. 配置后端 API 代理（路径模式）
cp middleware/onebase-upstream.conf.example middleware/onebase-upstream.conf
cp middleware/onebase-location.conf.example middleware/onebase-location.conf
vim middleware/onebase-upstream.conf
# 修改 server 地址为实际后端地址

# 5. 构建镜像
./build.sh image

# 6. 推送镜像到仓库（可选）
./build.sh image --push

# 7. 启动服务
./deploy.sh up --image
```

### 方式三：复制 dist 到服务器运行

**本地机器：**
```bash
# 1. 构建产物
./build.sh dist

# 2. 打包（包含 middleware 目录）
tar -czf onebase-fe.tar.gz dist/ deploy.sh .env.example config.env.example nginx/ middleware/

# 3. 上传到服务器
scp onebase-fe.tar.gz user@server:/opt/onebase/
```

**服务器：**
```bash
# 1. 解压
cd /opt/onebase
tar -xzf onebase-fe.tar.gz

# 2. 配置
cp .env.example .env
cp config.env.example config.env
vim config.env  # 修改 BACKEND_URL 等

# 3. 配置后端 API 代理
cp middleware/onebase-upstream.conf.example middleware/onebase-upstream.conf
cp middleware/onebase-location.conf.example middleware/onebase-location.conf
vim middleware/onebase-upstream.conf
# 修改 server 地址为实际后端地址

# 4. 运行
./deploy.sh up --dist
```

---

## 三、配置文件注入流程

### 流程说明

配置文件通过以下流程注入容器：

1. `./deploy.sh init` → 创建 config.env 和 .env
2. 用户编辑 config.env 设置 BACKEND_URL 等
3. `./deploy.sh up --dist` 或 `./deploy.sh up --image`：
   - 自动调用 `generate_configs()`
   - 从 config.env 读取配置
   - 生成 `dist/config/*.js` 文件
   - 启动容器时通过 `-v` 挂载到 `/usr/share/nginx/html/config.js`

### 挂载方式

| 服务 | 挂载源 | 挂载目标 |
|------|--------|----------|
| app-builder | dist/config/app-builder.js | /usr/share/nginx/html/config.js |
| admin-console | dist/config/admin-console.js | /usr/share/nginx/html/config.js |
| runtime | dist/config/runtime.js | /usr/share/nginx/html/config.js |
| mobile-editor | dist/config/mobile-editor.js | /usr/share/nginx/html/config.js |
| mobile-runtime | dist/config/mobile-runtime.js | /usr/share/nginx/html/config.js |

### 配置文件内容

生成的 config.js 格式示例：

```javascript
window.global_config = {
  THEME: 'lingji',
  ENVIRONMENT: 'builder',
  APP_KEY: 'onebase',
  APP_SECRET: 'xxx',
  BASE_URL: 'http://backend:8080/admin-api',
  RUNTIME_URL: 'http://frontend:9527',
  ...
};
```

### 后端 API 代理（路径模式）

路径模式下，网关通过 include 引用后端提供的配置文件代理 API：

| API 路径 | 说明 | 后端服务 |
|----------|------|----------|
| `/build-api/` | 编辑态 API | onebase-backend |
| `/runtime-api/` | 运行态 API | onebase-backend |
| `/flink-api/` | Flink API | onebase-flink |

**配置文件位置**：

```
deploy/podman/middleware/
├── onebase-upstream.conf.example  # upstream 定义模板
├── onebase-location.conf.example  # location 配置模板
└── README.md                      # 使用说明
```

**配置步骤**：

```bash
# 1. 进入部署目录
cd deploy/podman

# 2. 创建配置文件（去掉 .example 后缀）
cp middleware/onebase-upstream.conf.example middleware/onebase-upstream.conf
cp middleware/onebase-location.conf.example middleware/onebase-location.conf

# 3. 修改 upstream 地址
vim middleware/onebase-upstream.conf
# 修改 server 地址为实际后端服务地址：
#   upstream onebase-backend {
#       server 10.0.1.100:8080;  # 后端服务 IP:端口
#   }

# 4. 启动前端服务（网关会自动挂载 middleware 目录）
./deploy.sh up --dist
```

**后端独立部署时**：

后端团队只需提供配置文件：

```bash
# 后端提供的配置文件内容示例
# onebase-upstream.conf:
upstream onebase-backend {
    server backend-service:8080;
}

upstream onebase-flink {
    server flink-service:8081;
}

# 部署到前端网关
cp onebase-upstream.conf deploy/podman/middleware/
cp onebase-location.conf deploy/podman/middleware/
``

---

## 四、访问地址

### 路径模式（默认，DEPLOY_MODE=path）

统一入口，按路径访问：

| 服务 | 地址 |
|------|------|
| App Builder | http://`FRONTEND_BASE_URL`/appbuilder/ |
| Admin Console | http://`FRONTEND_BASE_URL`/console/ |
| Runtime | http://`FRONTEND_BASE_URL`/runtime/ |
| Mobile Editor | http://`FRONTEND_BASE_URL`/mobile-editor/ |
| Mobile Runtime | http://`FRONTEND_BASE_URL`/mobile-runtime/ |

**示例**（FRONTEND_BASE_URL=http://10.0.1.200）：
- App Builder: http://10.0.1.200/appbuilder/
- Admin Console: http://10.0.1.200/console/
- Runtime: http://10.0.1.200/runtime/

### 端口模式（DEPLOY_MODE=port）

服务独立端口访问：

| 服务 | 地址 |
|------|------|
| App Builder | http://`FRONTEND_BASE_URL`:4399 |
| Admin Console | http://`FRONTEND_BASE_URL`:4402 |
| Runtime | http://`FRONTEND_BASE_URL`:9527 |
| Mobile Editor | http://`FRONTEND_BASE_URL`:4401 |
| Mobile Runtime | http://`FRONTEND_BASE_URL`:9528 |

**示例**（FRONTEND_BASE_URL=http://10.0.1.200）：
- App Builder: http://10.0.1.200:4399
- Admin Console: http://10.0.1.200:4402
- Runtime: http://10.0.1.200:9527

---

## 五、常用命令

```bash
# 查看服务状态
./deploy.sh ps

# 查看日志
./deploy.sh logs              # 所有服务
./deploy.sh logs app-builder  # 指定服务

# 重启服务
./deploy.sh restart

# 停止服务
./deploy.sh down

# 清理所有资源
./deploy.sh clean
```

---

## 六、配置示例

### 开发环境
```bash
# config.env
THEME=default
BACKEND_URL=http://dev-backend:8080
FRONTEND_BASE_URL=http://dev-server
```

### 测试环境
```bash
# config.env
THEME=lingji
BACKEND_URL=http://test-api.example.com
FRONTEND_BASE_URL=http://test.example.com
AI_BASE_URL=https://test-ai.example.com
```

### 生产环境
```bash
# config.env
THEME=lingji
BACKEND_URL=https://api.example.com
FRONTEND_BASE_URL=https://onebase.example.com
AI_BASE_URL=https://ai.example.com
SSO_HOME=https://sso.example.com/home
SSO_SOURCE_ID=onebase_prod
```

---

## 七、检查清单

部署前确认：
- [ ] 确认服务器 CPU 架构（x86_64 或 arm64）
- [ ] 后端服务已启动并可访问
- [ ] 服务器端口已开放：
  - 路径模式：80（网关端口）
  - 端口模式：4399, 4402, 9527, 9528, 4401
- [ ] Podman 已安装
- [ ] config.env 已正确配置
- [ ] middleware 配置文件已创建（路径模式）：
  - middleware/onebase-upstream.conf
  - middleware/onebase-location.conf

查看服务器架构：
```bash
# Linux
uname -m
# 输出：x86_64 或 aarch64（即 arm64）

# macOS
arch
# 输出：arm64 或 i386
```