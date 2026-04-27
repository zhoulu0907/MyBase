# OneBase V3 完整部署手册

## 目录

- [环境要求](#环境要求)
- [代码拉取](#代码拉取)
- [后端部署](#后端部署)
- [前端部署](#前端部署)
- [验证与访问指南](#验证与访问指南)
- [NodeSandbox 服务](#nodesandbox-服务)
- [网络架构](#网络架构)
- [常用命令](#常用命令)
- [故障排查](#故障排查)
- [安全建议](#安全建议)
- [附录](#附录)
- [独立服务部署示例](#独立服务部署示例)
- [打包指南](#打包指南)

---

## 环境要求

### 服务器要求

| 项目 | 要求 |
|------|------|
| 操作系统 | Linux (CentOS 7+, Ubuntu 18.04+) 或 macOS |
| 架构 | x86_64/amd64 或 arm64/aarch64 |
| 内存 | 最低 8GB，推荐 16GB+ |
| 磁盘 | 最低 50GB 可用空间 |
| 容器运行时 | Docker 24.0+ 或 Podman 4.0+ |

### 软件依赖

| 软件 | 版本 | 说明 |
|------|------|------|
| Docker/Podman | 24.0+/4.0+ | 容器运行时 |
| docker-compose/podman-compose | 最新版 | 容器编排 |
| Java | JDK 17+ | 仅构建时需要 |
| Maven | 3.8+ | 仅构建时需要 |

### 端口规划

| 端口 | 服务 | 说明 |
|------|------|------|
| 80 | 前端 Nginx | 统一入口 |
| 8080 | 后端服务 | 容器内部端口 |
| 5432 | PostgreSQL | 数据库 |
| 6379 | Redis | 缓存 |
| 9000 | MinIO API | 对象存储 |
| 9001 | MinIO Console | 对象存储管理 |
| 12345 | DolphinScheduler | 任务调度 |
| 3000 | NodeSandbox | JS 执行环境 |

---

## 代码拉取

### 拉取后端代码

```bash
git clone <onebase-v3-be-repo-url> /path/to/onebase-v3-be
cd /path/to/onebase-v3-be
```

### 拉取前端代码

```bash
git clone <onebase-v3-fe-repo-url> /path/to/onebase-v3-fe
cd /path/to/onebase-v3-fe
```

---

## 后端部署

### 1. 构建产物

在项目根目录执行构建：

```bash
cd /path/to/onebase-v3-be

# 方式一：仅构建 JAR 产物（推荐开发测试）
./deploy/podman/build.sh dist

# 方式二：构建完整镜像（推荐生产部署）
./deploy/podman/build.sh image
```

构建完成后，产物位于 `dist/` 目录。

### 2. 初始化配置

```bash
cd deploy/podman
./deploy.sh init
```

此命令会：
- 显示服务器架构信息
- 创建必要的目录结构
- 从 `.example` 模板文件生成配置文件

**生成的配置文件**：
- `.env` - 构建环境变量
- `config/config.env` - 部署配置
- `middleware/onebase-upstream.conf` - nginx upstream 配置
- `middleware/onebase-location.conf` - nginx location 配置

### 3. 配置说明

**内置中间件模式（默认）**：
- 使用 `deploy.sh middleware-up` 部署的中间件
- 配置已自动填充容器网络地址，**无需手动修改**

**外部中间件模式**：
- 使用已有的外部中间件（PostgreSQL、Redis 等）
- 需要修改 `config/config.env`：

```bash
# 切换到外部中间件模式
USE_BUILTIN_MIDDLEWARE=false

# 修改数据库配置
DB_HOST=your-db-host
DB_PORT=5432
DB_NAME=onebase_cloud_v3
DB_USER=your-username
DB_PASSWORD=your-password

# 修改 Redis 配置
REDIS_HOST=your-redis-host
REDIS_PORT=6379
REDIS_PASSWORD=your-redis-password

# 修改 MinIO 配置
MINIO_ENDPOINT=http://your-minio:9000
MINIO_ACCESS_KEY=your-access-key
MINIO_SECRET_KEY=your-secret-key
```

### 4. 启动中间件

```bash
./deploy.sh middleware-up
```

等待中间件启动完成（约 2-5 分钟），可通过以下地址验证：

| 服务 | 地址 | 默认账号 |
|------|------|----------|
| DolphinScheduler | http://localhost:12345/dolphinscheduler | admin / dolphinscheduler123 |
| MinIO Console | http://localhost:9001 | minioadmin / minioadmin123 |

### 5. 初始化 DolphinScheduler

1. 访问 http://localhost:12345/dolphinscheduler
2. 使用默认账号登录：admin / dolphinscheduler123
3. 进入 **安全中心**：
   - 创建租户：`root`
   - 创建令牌：复制令牌值
   - 创建环境：记录环境 ID
4. 进入 **项目管理**：
   - 创建项目：`自动化工作流`（记录项目 ID）
   - 创建项目：`数据工厂`（记录项目 ID）
5. 更新配置文件：

```bash
vi config/config.env
```

修改以下内容：

```bash
DS_TOKEN=your-token-here
DS_ENV=1
DS_FLOW_PROJECT=1
DS_ETL_PROJECT=2
DS_TENANT=root
```

### 6. 启动后端服务

```bash
# 从 dist 目录启动
./deploy.sh up --dist

# 或从镜像启动
./deploy.sh up --image
```

### 7. 验证后端部署

```bash
# 查看服务状态
./deploy.sh ps

# 查看服务日志
./deploy.sh logs
```

### 8. 记录后端配置文件路径

部署完成后，记录 nginx 配置文件路径供前端使用：

```bash
# nginx 配置文件路径
ls -la middleware/onebase-upstream.conf
ls -la middleware/onebase-location.conf
```

---

## 前端部署

### 1. 配置后端地址

修改前端项目中的后端 API 地址配置：

```bash
cd /path/to/onebase-v3-fe
vi <前端配置文件>
```

配置后端地址：

```bash
# 编辑态 API 地址
BUILD_API_BASE_URL=http://localhost/build-api

# 运行态 API 地址
RUNTIME_API_BASE_URL=http://localhost/runtime-api

# Flink API 地址
FLINK_API_BASE_URL=http://localhost/flink-api
```

### 2. 构建前端产物

```bash
npm install
npm run build
```

构建产物位于 `dist/` 目录。

### 3. Nginx 配置

**步骤 1**：拷贝后端 nginx 配置文件到前端 nginx 配置目录

```bash
# 拷贝 upstream 和 location 配置
cp /path/to/onebase-v3-be/deploy/podman/middleware/onebase-upstream.conf /etc/nginx/conf.d/
cp /path/to/onebase-v3-be/deploy/podman/middleware/onebase-location.conf /etc/nginx/conf.d/
```

**步骤 2**：配置前端 nginx.conf

```nginx
http {
    # 引入 OneBase 后端 upstream 定义
    include /etc/nginx/conf.d/onebase-upstream.conf;

    server {
        listen 80;
        server_name localhost;

        # 前端静态资源
        location / {
            root /usr/share/nginx/html;
            index index.html;
            try_files $uri $uri/ /index.html;
        }

        # 引入 OneBase 后端 API location 配置
        include /etc/nginx/conf.d/onebase-location.conf;
    }
}
```

**步骤 3**：部署前端静态文件

```bash
# 拷贝前端构建产物到 nginx 目录
cp -r /path/to/onebase-v3-fe/dist/* /usr/share/nginx/html/
```

**步骤 4**：启动 nginx

```bash
# 测试配置
nginx -t

# 启动或重启 nginx
nginx
# 或
nginx -s reload
```

### 4. Docker 方式部署前端（可选）

如果前端使用 Docker 部署，创建 `docker-compose.fe.yaml`：

```yaml
version: '3.8'

services:
  onebase-frontend:
    image: nginx:alpine
    container_name: onebase-frontend
    ports:
      - "80:80"
    volumes:
      - ./dist:/usr/share/nginx/html
      - ./nginx.conf:/etc/nginx/nginx.conf
      - /path/to/onebase-v3-be/deploy/podman/middleware/onebase-upstream.conf:/etc/nginx/conf.d/onebase-upstream.conf
      - /path/to/onebase-v3-be/deploy/podman/middleware/onebase-location.conf:/etc/nginx/conf.d/onebase-location.conf
    networks:
      - onebase-middleware

networks:
  onebase-middleware:
    external: true
```

启动前端：

```bash
docker-compose -f docker-compose.fe.yaml up -d
```

---

## 验证与访问指南

### 访问地址汇总

**统一入口**：http://localhost

| 服务 | 路径 | 说明 |
|------|------|------|
| 前端页面 | / | 应用编辑、运行 |
| 编辑态 API | /build-api/ | 后端编辑态服务 |
| 运行态 API | /runtime-api/ | 后端运行态服务 |
| Flink API | /flink-api/ | 数据处理服务 |

### API 示例

#### 基础接口示例

| 接口 | 方法 | 完整地址 |
|------|------|----------|
| 登录 | POST | http://localhost/build-api/admin-api/system/auth/login |
| 登出 | POST | http://localhost/build-api/admin-api/system/auth/logout |
| 用户列表 | GET | http://localhost/build-api/admin-api/system/user/page |
| 角色列表 | GET | http://localhost/build-api/admin-api/system/role/page |
| 页面列表 | GET | http://localhost/build-api/admin-api/app/page/list |
| 应用列表 | GET | http://localhost/build-api/admin-api/app/application/list |

#### 登录接口详细示例

**请求**：
```bash
curl -X POST http://localhost/build-api/admin-api/system/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

**请求体**：
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**响应体**：
```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
    "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
    "userId": 1,
    "username": "admin"
  }
}
```

#### 带认证的接口示例

获取 Token 后，在后续请求中携带 Authorization Header：

```bash
curl -X GET http://localhost/build-api/admin-api/app/page/list \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

#### Dashboard 接口示例

| 接口 | 方法 | 完整地址 |
|------|------|----------|
| Dashboard 项目列表 | GET | http://localhost/build-api/admin-api/dashboard/project/list |
| Dashboard 文件列表 | GET | http://localhost/build-api/admin-api/dashboard/file/list |
| Dashboard 模板列表 | GET | http://localhost/build-api/admin-api/dashboard/template/list |
| 创建 Dashboard 项目 | POST | http://localhost/build-api/admin-api/dashboard/project/create |

**示例请求**：
```bash
# 获取 Dashboard 项目列表
curl -X GET http://localhost/build-api/admin-api/dashboard/project/list \
  -H "Authorization: Bearer {token}"

# 创建 Dashboard 项目
curl -X POST http://localhost/build-api/admin-api/dashboard/project/create \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{"name": "销售分析仪表盘", "description": "展示销售数据统计"}'
```

#### AI 接口示例

| 接口 | 方法 | 完整地址 |
|------|------|----------|
| AI 对话 | POST | http://localhost/build-api/admin-api/ai/chat |
| AI 模型列表 | GET | http://localhost/build-api/admin-api/ai/model/list |
| AI 任务列表 | GET | http://localhost/build-api/admin-api/ai/task/list |

**示例请求**：
```bash
# AI 对话
curl -X POST http://localhost/build-api/admin-api/ai/chat \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{"prompt": "帮我分析这份销售数据", "modelId": 1}'
```

#### 运行态接口示例

| 接口 | 方法 | 完整地址 |
|------|------|----------|
| 获取页面配置 | GET | http://localhost/runtime-api/runtime-api/app/{appId}/page/{pageId} |
| 查询数据 | POST | http://localhost/runtime-api/runtime-api/app/{appId}/data/query |
| 保存数据 | POST | http://localhost/runtime-api/runtime-api/app/{appId}/data/save |
| 删除数据 | POST | http://localhost/runtime-api/runtime-api/app/{appId}/data/delete |

**示例请求**：
```bash
# 查询应用数据
curl -X POST http://localhost/runtime-api/runtime-api/app/1/data/query \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{"tableName": "customer", "conditions": [{"field": "status", "value": "active"}]}'
```

#### WebSocket 接口示例

| 接口 | 地址 | 说明 |
|------|------|------|
| 编辑态 WebSocket | ws://localhost/build-api/ws/endpoint | 实时协作、消息推送 |
| 运行态 WebSocket | ws://localhost/runtime-api/ws/endpoint | 实时数据更新 |

**JavaScript 连接示例**：
```javascript
// 编辑态 WebSocket 连接
const ws = new WebSocket('ws://localhost/build-api/ws/endpoint?token=xxx');
ws.onopen = () => console.log('WebSocket 已连接');
ws.onmessage = (event) => console.log('收到消息:', event.data);
ws.onerror = (error) => console.error('WebSocket 错误:', error);
```

### 中间件服务（直接端口访问）

| 服务 | 地址 | 默认账号 |
|------|------|----------|
| PostgreSQL | localhost:5432 | onebase / onebase123 |
| Redis | localhost:6379 | - / redis123 |
| MinIO Console | http://localhost:9001 | minioadmin / minioadmin123 |
| DolphinScheduler | http://localhost:12345/dolphinscheduler | admin / dolphinscheduler123 |
| NodeSandbox | http://localhost:3000 | - |

### NodeSandbox 服务

**服务说明**：
NodeSandbox 是一个 JavaScript 安全执行环境，用于：
- 执行用户自定义脚本
- 数据转换脚本执行
- 公式计算
- 低代码逻辑处理

**端口**：3000

**健康检查**：
```bash
curl http://localhost:3000/health
# 响应: {"status":"ok"}
```

**使用示例**：
```bash
# 执行 JavaScript 代码
curl -X POST http://localhost:3000/execute \
  -H "Content-Type: application/json" \
  -d '{"code": "const result = 1 + 2; result;"}'

# 响应
# {"success": true, "result": 3}
```

**配置说明**：
NodeSandbox 在中间件启动时自动部署，配置文件位于 `middleware/docker-compose.middleware.yaml`。

**环境变量**：
| 变量 | 说明 | 默认值 |
|------|------|--------|
| NODE_ENV | 运行环境 | production |
| PORT | 服务端口 | 3000 |
| REDIS_HOST | Redis 主机 | redis |
| REDIS_PORT | Redis 端口 | 6379 |

### 健康检查

```bash
# 后端服务（通过 nginx）
curl http://localhost/build-api/actuator/health
curl http://localhost/runtime-api/actuator/health

# 中间件服务
docker exec onebase-postgresql pg_isready -U onebase
docker exec onebase-redis redis-cli -a redis123 ping
curl http://localhost:9000/minio/health/live
```

---

## 网络架构

```
┌─────────────────────────────────────────────────────────────────┐
│                        用户访问                                  │
│                     http://localhost                            │
└─────────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                   前端统一 Nginx                                 │
│                                                                 │
│  ┌──────────────────┐  静态资源: /                              │
│  │   前端静态文件    │  index.html, js, css...                   │
│  └──────────────────┘                                           │
│                                                                 │
│  后端 API 代理:                                                  │
│  ┌──────────────────┬──────────────────┬──────────────────┐    │
│  │   /build-api/    │  /runtime-api/   │   /flink-api/    │    │
│  └────────┬─────────┴────────┬─────────┴────────┬─────────┘    │
└───────────┼──────────────────┼──────────────────┼───────────────┘
            │                  │                  │
            ▼                  ▼                  ▼
    ┌────────────┐     ┌────────────┐     ┌────────────┐
    │   build    │     │  runtime   │     │   flink    │
    │   :8080    │     │   :8080    │     │   :8080    │
    └────────────┘     └────────────┘     └────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                 中间件服务（直接端口访问）                         │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐ │
│  │ PostgreSQL  │  │    Redis    │  │         MinIO           │ │
│  │   :5432     │  │    :6379    │  │   :9000 (API) :9001    │ │
│  └─────────────┘  └─────────────┘  └─────────────────────────┘ │
│  ┌─────────────────────────────┐  ┌─────────────────────────┐  │
│  │     DolphinScheduler        │  │      NodeSandbox        │  │
│  │         :12345              │  │        :3000            │  │
│  └─────────────────────────────┘  └─────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 常用命令

### 后端命令

```bash
cd /path/to/onebase-v3-be/deploy/podman

# 查看服务状态
./deploy.sh ps

# 查看日志
./deploy.sh logs              # 所有服务
./deploy.sh logs build        # 仅 build 服务
./deploy.sh logs runtime      # 仅 runtime 服务

# 重启服务
./deploy.sh restart

# 停止服务
./deploy.sh down

# 停止中间件
./deploy.sh middleware-down

# 清理资源
./deploy.sh clean
```

### 前端命令

```bash
# 查看 nginx 状态
nginx -t

# 重启 nginx
nginx -s reload

# 查看 nginx 日志
tail -f /var/log/nginx/error.log
```

### 中间件命令

```bash
# 查看中间件状态
docker ps | grep onebase

# 查看中间件日志
docker logs onebase-postgresql
docker logs onebase-redis
docker logs onebase-minio
docker logs onebase-dolphinscheduler
```

---

## 故障排查

### Q1: 后端服务启动失败，提示数据库连接错误

**解决方案**：
1. 检查 PostgreSQL 是否正常运行：`docker ps | grep postgresql`
2. 检查数据库连接配置是否正确
3. 确认数据库已创建：`onebase_cloud_v3`, `onebase_business`

### Q2: 前端无法访问后端 API

**排查步骤**：
1. 检查 nginx 配置是否正确：`nginx -t`
2. 检查 upstream 和 location 配置文件是否存在
3. 检查后端服务是否启动：`./deploy.sh ps`
4. 检查 nginx 日志：`tail -f /var/log/nginx/error.log`

### Q3: DolphinScheduler 启动失败

**解决方案**：
1. 等待 PostgreSQL 完全启动后再启动 DolphinScheduler
2. 检查日志：`docker logs onebase-dolphinscheduler`
3. 确认数据库 `dolphinscheduler` 已创建

### Q4: 端口冲突

**解决方案**：
1. 检查端口占用：`netstat -tlnp | grep <port>`
2. 修改 `middleware/middleware.env` 中的端口配置
3. 修改 `config/config.env` 中的端口配置

### Q5: 内存不足

**解决方案**：
1. 调整 JVM 参数：`JAVA_OPTS=-Xms512m -Xmx1g`
2. 减少中间件内存占用
3. 增加服务器内存

### Q6: 文件上传失败

**排查步骤**：
1. 检查 MinIO 服务状态
2. 检查 Bucket 是否存在
3. 检查 nginx 配置中的 `client_max_body_size`

### Q7: WebSocket 连接失败

**排查步骤**：
1. 检查 nginx WebSocket 配置是否正确
2. 检查 nginx 版本是否支持 WebSocket
3. 检查后端服务是否启动

---

## 安全建议

1. **修改默认密码**：部署后立即修改所有默认密码
2. **网络隔离**：将服务部署在内网，通过网关暴露
3. **HTTPS**：生产环境使用 HTTPS
4. **定期备份**：定期备份数据库和文件存储
5. **访问控制**：配置合理的访问权限

---

## 附录

### 配置文件模板

| 文件 | 说明 |
|------|------|
| `.env.example` | 构建环境变量模板 |
| `config.env.example` | 部署配置模板 |
| `onebase-upstream.conf.example` | nginx upstream 模板 |
| `onebase-location.conf.example` | nginx location 模板 |

### JVM 调优建议

```bash
# 编辑态服务（推荐配置）
JAVA_OPTS=-Xms2g -Xmx4g -XX:+UseG1GC

# 运行态服务（推荐配置）
JAVA_OPTS=-Xms2g -Xmx4g -XX:+UseG1GC

# Flink 服务（推荐配置）
JAVA_OPTS=-Xms1g -Xmx2g -XX:+UseG1GC
```

---

## 独立服务部署示例

本章节提供 Dashboard 和 AI 服务独立部署的参考示例。

### Dashboard 服务独立部署示例

Dashboard 模块通常集成在编辑态服务中，如需独立部署可参考以下示例：

**Docker 部署示例**：
```yaml
# docker-compose.dashboard.yaml
version: '3.8'

services:
  onebase-dashboard:
    image: onebase-server-dashboard:v1.0.0
    container_name: onebase-dashboard
    ports:
      - "8081:8080"
    environment:
      TZ: Asia/Shanghai
      JAVA_OPTS: -Xms512m -Xmx1g
      SPRING_DATASOURCE_URL: jdbc:postgresql://onebase-postgresql:5432/onebase_cloud_v3
      SPRING_DATASOURCE_USERNAME: onebase
      SPRING_DATASOURCE_PASSWORD: onebase123
      SPRING_REDIS_HOST: onebase-redis
      SPRING_REDIS_PORT: 6379
      SPRING_REDIS_PASSWORD: redis123
    networks:
      - onebase-middleware
    depends_on:
      - onebase-postgresql
      - onebase-redis

networks:
  onebase-middleware:
    external: true
```

**启动命令**：
```bash
docker-compose -f docker-compose.dashboard.yaml up -d
```

**Nginx 配置示例**（如需独立路径）：
```nginx
# Dashboard API 代理
location /dashboard-api/ {
    rewrite ^/dashboard-api/(.*) /$1 break;
    proxy_pass http://onebase-dashboard:8080;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
}
```

### AI 服务独立部署示例

AI 模块通常集成在编辑态服务中，如需独立部署可参考以下示例：

**Docker 部署示例**：
```yaml
# docker-compose.ai.yaml
version: '3.8'

services:
  onebase-ai:
    image: onebase-server-ai:v1.0.0
    container_name: onebase-ai
    ports:
      - "8082:8080"
    environment:
      TZ: Asia/Shanghai
      JAVA_OPTS: -Xms512m -Xmx2g
      SPRING_DATASOURCE_URL: jdbc:postgresql://onebase-postgresql:5432/onebase_cloud_v3
      SPRING_DATASOURCE_USERNAME: onebase
      SPRING_DATASOURCE_PASSWORD: onebase123
      SPRING_REDIS_HOST: onebase-redis
      SPRING_REDIS_PORT: 6379
      SPRING_REDIS_PASSWORD: redis123
      # AI 服务特有配置
      AI_MODEL_PROVIDER: openai
      AI_MODEL_API_KEY: your-api-key
      AI_MODEL_ENDPOINT: https://api.openai.com/v1
    networks:
      - onebase-middleware
    depends_on:
      - onebase-postgresql
      - onebase-redis

networks:
  onebase-middleware:
    external: true
```

**启动命令**：
```bash
docker-compose -f docker-compose.ai.yaml up -d
```

**Nginx 配置示例**（如需独立路径）：
```nginx
# AI API 代理
location /ai-api/ {
    rewrite ^/ai-api/(.*) /$1 break;
    proxy_pass http://onebase-ai:8080;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
}
```

**AI 服务配置项**：
| 变量 | 说明 | 示例 |
|------|------|------|
| AI_MODEL_PROVIDER | 模型提供商 | openai, azure, local |
| AI_MODEL_API_KEY | API 密钥 | sk-xxx |
| AI_MODEL_ENDPOINT | API 地址 | https://api.openai.com/v1 |

---

## 打包指南

### build.sh 脚本说明

构建脚本位于 `deploy/podman/build.sh`，支持两种构建模式。

#### dist 模式（推荐开发测试）

仅构建 JAR 产物，不构建镜像：

```bash
cd /path/to/onebase-v3-be
./deploy/podman/build.sh dist
```

**产物说明**：
```
deploy/podman/dist/
├── onebase-server.jar          # 编辑态服务 JAR
├── onebase-server-runtime.jar  # 运行态服务 JAR
├── onebase-server-flink.jar    # Flink 服务 JAR
├── config/                     # 配置模板
├── scripts/                    # 部署脚本
└── VERSION                     # 版本信息
```

**适用场景**：
- 开发环境快速测试
- 本地调试
- 无需容器化部署

#### image 模式（推荐生产部署）

构建完整 Docker/Podman 镜像并导出：

```bash
cd /path/to/onebase-v3-be
./deploy/podman/build.sh image
```

**产物说明**：
```
deploy/podman/dist/
├── onebase-jvm-v1.0.0.tar              # JVM 基础镜像
├── onebase-server-build-v1.0.0.tar     # 编辑态服务镜像
├── onebase-server-runtime-v1.0.0.tar   # 运行态服务镜像
├── onebase-server-flink-v1.0.0.tar     # Flink 服务镜像
└── VERSION                             # 版本信息
```

**适用场景**：
- 生产环境部署
- 需要分发镜像
- 离线部署

### 镜像导入与分发

**导入镜像**：
```bash
# 在目标服务器导入镜像
podman load -i onebase-jvm-v1.0.0.tar
podman load -i onebase-server-build-v1.0.0.tar
podman load -i onebase-server-runtime-v1.0.0.tar
podman load -i onebase-server-flink-v1.0.0.tar
```

**或使用 Docker**：
```bash
docker load -i onebase-jvm-v1.0.0.tar
docker load -i onebase-server-build-v1.0.0.tar
docker load -i onebase-server-runtime-v1.0.0.tar
docker load -i onebase-server-flink-v1.0.0.tar
```

### 版本管理

**修改版本号**：
编辑 `deploy/podman/.env` 文件：
```bash
VERSION=v1.0.1
```

或修改 `deploy/podman/build.sh` 中的默认版本：
```bash
VERSION="v1.0.1"
```

**版本信息文件**：
构建后生成 `dist/VERSION` 文件：
```bash
VERSION=v1.0.0
BUILD_DATE=2026-04-10 10:30:00
ARCH=linux/amd64
```

### 架构自动检测

构建脚本自动检测服务器架构：
| 服务器架构 | Docker 架构 |
|------------|-------------|
| x86_64/amd64 | linux/amd64 |
| arm64/aarch64 | linux/arm64 |

**手动指定架构**（可选）：
```bash
# 在 .env 文件中指定
PLATFORM=linux/amd64
```

### 打包完整流程示例

```bash
# 1. 进入项目目录
cd /path/to/onebase-v3-be

# 2. 检查版本配置
cat deploy/podman/.env

# 3. 执行构建
./deploy/podman/build.sh image

# 4. 检查产物
ls -la deploy/podman/dist/

# 5. 分发镜像到目标服务器
scp deploy/podman/dist/*.tar target-server:/opt/onebase/images/

# 6. 在目标服务器导入镜像
ssh target-server
cd /opt/onebase/images
for tar in *.tar; do podman load -i $tar; done
```