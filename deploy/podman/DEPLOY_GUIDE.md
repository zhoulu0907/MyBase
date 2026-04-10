# OneBase 后端部署指南

## 目录

- [部署前准备](#部署前准备)
- [快速开始](#快速开始)
- [详细部署步骤](#详细部署步骤)
- [配置说明](#配置说明)
- [常见问题](#常见问题)

---

## 部署前准备

### 系统要求

| 项目 | 要求 |
|------|------|
| 操作系统 | Linux (CentOS 7+, Ubuntu 18.04+) 或 macOS |
| 架构 | x86_64/amd64 或 arm64/aarch64 |
| 内存 | 最低 8GB，推荐 16GB+ |
| 磁盘 | 最低 50GB 可用空间 |
| 容器运行时 | Docker 24.0+ 或 Podman 4.0+ |

### 软件依赖

- **容器运行时**：Docker 或 Podman
- **容器编排**：docker-compose 或 podman-compose
- **Java**：JDK 17+ (仅构建时需要)
- **Maven**：3.8+ (仅构建时需要)


### 安装 Podman (如未安装)

```bash
# CentOS/RHEL
sudo yum install -y podman
pip install podman-compose

# Ubuntu/Debian
sudo apt-get update
sudo apt-get install -y podman
pip install podman-compose
```

---

## 快速开始

### 一键部署

```bash
# 1. 初始化配置
./deploy.sh init

# 2. 修改配置文件
vi config/config.env

# 3. 启动中间件
./deploy.sh middleware-up

# 4. 等待中间件启动完成 (约 2-5 分钟)
# 查看 DolphinScheduler 是否就绪: http://localhost:12345/dolphinscheduler

# 5. 启动 OneBase 服务
./deploy.sh up --dist
```

### 访问地址

**后端 API（通过前端统一 nginx 代理）**：

| 服务 | 路径前缀 | 说明 |
|------|----------|------|
| 编辑态 API | /build-api/ | 应用编辑、页面设计 |
| 运行态 API | /runtime-api/ | 应用运行、数据查询 |
| Flink API | /flink-api/ | 数据处理 |

**完整 API 示例**：
- 登录接口: `http://localhost/build-api/admin-api/system/auth/login`
- 页面列表: `http://localhost/build-api/admin-api/app/page/list`
- 数据查询: `http://localhost/runtime-api/runtime-api/app/{appId}/data/query`

**中间件服务（直接端口访问）**：

| 服务 | 地址 | 说明 |
|------|------|------|
| DolphinScheduler | http://localhost:12345/dolphinscheduler | 任务调度管理 |
| MinIO Console | http://localhost:9001 | 对象存储管理 |

---

## 详细部署步骤

### 步骤 1：构建产物

在项目根目录执行构建：

```bash
# 方式一：仅构建 JAR 产物（推荐开发测试）
./build.sh dist

# 方式二：构建完整镜像（推荐生产部署）
./build.sh image
```

构建完成后，产物位于 `dist/` 目录。

### 步骤 2：初始化配置

```bash
./deploy.sh init
```

此命令会：
- 显示服务器架构信息
- 创建必要的目录结构
- 生成默认配置文件

### 步骤 3：修改配置

**内置中间件模式（默认）**：
- 使用 `deploy.sh middleware-up` 部署的中间件
- 配置已自动填充容器网络地址，**无需手动修改**
- 直接执行下一步启动中间件

**外部中间件模式**：
- 使用已有的外部中间件（PostgreSQL、Redis 等）
- 需要修改配置文件：

```bash
vi config/config.env
```

修改以下内容：

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

# 修改 MinIO 配置（如使用）
MINIO_ENDPOINT=http://your-minio:9000
MINIO_ACCESS_KEY=your-access-key
MINIO_SECRET_KEY=your-secret-key
```

**关键配置项说明**：

```bash
# 数据库配置
DB_HOST=localhost
DB_PORT=5432
DB_NAME=onebase_cloud_v3
DB_USER=onebase
DB_PASSWORD=your_password  # 请修改

# Redis 配置
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=your_redis_password  # 请修改

# DolphinScheduler 配置
DS_ADDRESS=http://localhost:12345/dolphinscheduler
DS_TOKEN=your_token  # 首次启动后获取

# MinIO 配置
MINIO_ENDPOINT=http://localhost:9000
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=your_secret_key  # 请修改
```

### 步骤 4：启动中间件

```bash
./deploy.sh middleware-up
```

中间件服务列表：

| 服务 | 端口 | 默认账号 |
|------|------|----------|
| PostgreSQL | 5432 | onebase / onebase123 |
| Redis | 6379 | - / redis123 |
| MinIO | 9000/9001 | minioadmin / minioadmin123 |
| DolphinScheduler | 12345 | admin / dolphinscheduler123 |
| NodeSandbox | 3000 | - |

### 步骤 5：初始化 DolphinScheduler

1. 访问 DolphinScheduler：http://localhost:12345/dolphinscheduler
2. 使用默认账号登录：admin / dolphinscheduler123
3. 进入 **安全中心**：
   - 创建租户：`root`
   - 创建令牌：复制令牌值
   - 创建环境：记录环境 ID
4. 进入 **项目管理**：
   - 创建项目：`自动化工作流`
   - 创建项目：`数据工厂`
5. 更新配置文件中的 DolphinScheduler 相关配置

### 步骤 6：启动 OneBase 服务

```bash
# 从 dist 目录启动
./deploy.sh up --dist

# 或从镜像启动
./deploy.sh up --image
```

### 步骤 7：验证部署

检查服务状态：

```bash
./deploy.sh ps
```

检查服务健康：

```bash
# 编辑态服务
curl http://localhost/build-api/actuator/health

# 运行态服务
curl http://localhost/runtime-api/actuator/health
```

---

## 前端 Nginx 配置

### 配置文件说明

| 文件 | 用途 | 引用位置 |
|------|------|----------|
| onebase-upstream.conf | upstream 定义 | http 块 |
| onebase-location.conf | location 配置 | server 块 |

### 前端 nginx.conf 示例

```nginx
http {
    # 引入 OneBase 后端 upstream 定义
    include /path/to/onebase-upstream.conf;

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
        include /path/to/onebase-location.conf;
    }
}
```

### 配置文件路径

部署时将配置文件拷贝到前端 nginx 可访问的位置：

```bash
# 拷贝配置文件到前端目录
cp middleware/onebase-upstream.conf /etc/nginx/conf.d/
cp middleware/onebase-location.conf /etc/nginx/conf.d/

# 重新加载 nginx
nginx -s reload
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

**nginx 配置说明**：
- 前端 nginx 配置文件: 参考 `middleware/nginx.conf`
- 将 upstream 和 location 配置拷贝到前端统一的 nginx.conf 中
- 前端静态资源直接服务，后端 API 通过 location 代理

---

## 配置说明

### 目录结构

```
deploy/podman/
├── build.sh              # 构建脚本
├── deploy.sh             # 部署脚本
├── config/               # 配置文件
│   └── config.env        # 主配置文件
├── middleware/           # 中间件配置
│   ├── docker-compose.middleware.yaml
│   └── middleware.env
├── images/               # Dockerfile
├── lib/                  # JAR 文件
└── data/                 # 运行数据
    ├── conf/             # 运行时配置
    └── logs/             # 日志文件
```

### 配置文件优先级

1. `data/conf/application-*.yaml` - 运行时配置（最高优先级）
2. `config/config.env` - 环境变量配置
3. 默认配置

### JVM 调优

在 `config/config.env` 中配置：

```bash
# 堆内存设置
JAVA_OPTS=-Xms2g -Xmx4g -XX:+UseG1GC

# 完整 JVM 参数示例
JAVA_OPTS=-Xms2g -Xmx4g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+HeapDumpOnOutOfMemoryError
```

---

## 常用命令

```bash
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

---

## 常见问题

### Q1: 服务启动失败，提示数据库连接错误

**解决方案**：
1. 检查 PostgreSQL 是否正常运行：`docker ps | grep postgresql`
2. 检查数据库连接配置是否正确
3. 确认数据库已创建：`onebase_cloud_v3`, `onebase_business`

### Q2: DolphinScheduler 启动失败

**解决方案**：
1. 等待 PostgreSQL 完全启动后再启动 DolphinScheduler
2. 检查日志：`docker logs onebase-dolphinscheduler`
3. 确认数据库 `dolphinscheduler` 已创建

### Q3: 内存不足

**解决方案**：
1. 调整 JVM 参数：`JAVA_OPTS=-Xms512m -Xmx1g`
2. 减少中间件内存占用
3. 增加服务器内存

### Q4: 端口冲突

**解决方案**：
1. 检查端口占用：`netstat -tlnp | grep <port>`
2. 修改 `middleware/middleware.env` 中的端口配置
3. 修改 `config/config.env` 中的端口配置

### Q5: 如何更新配置

**解决方案**：
1. 修改 `config/config.env`
2. 执行 `./deploy.sh config` 重新生成配置
3. 执行 `./deploy.sh restart` 重启服务

---

## 技术支持

如遇问题，请联系技术支持团队。