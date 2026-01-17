# OneBase v3.0 Backend - 配置与部署文档

> **生成日期：** 2025-12-06
> **项目：** onebase-v3-be
> **Spring Boot版本：** 3.4.5
> **Java版本：** 17+

---

## 📋 目录

1. [概述](#概述)
2. [核心技术栈](#核心技术栈)
3. [配置文件结构](#配置文件结构)
4. [多环境配置](#多环境配置)
5. [核心配置项](#核心配置项)
6. [数据库配置](#数据库配置)
7. [认证与安全](#认证与安全)
8. [部署架构](#部署架构)
9. [运维监控](#运维监控)

---

## 概述

OneBase v3.0 Backend 采用 Spring Boot 3.4.5 + Maven 多模块架构，支持多环境部署和多数据库（PostgreSQL、DM达梦、KingBase人大金仓）。

### 服务器模块

项目包含3个独立的服务器模块：

1. **onebase-server** - 标准服务器（Build模式）
2. **onebase-server-runtime** - 运行时服务器（Runtime模式）
3. **onebase-server-platform** - 平台管理服务器（Platform模式）
4. **onebase-server-flink** - Flink ETL执行服务器

---

## 核心技术栈

### 基础框架

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.4.5 | 核心框架 |
| Java | 17+ | 运行环境 |
| Maven | 3.8+ | 构建工具 |
| PostgreSQL | 10+ | 主数据库 |
| Redis | 6.0+ | 缓存和会话 |

### ORM 与数据访问

| 技术 | 说明 |
|------|------|
| MyBatis-Flex | 主ORM框架 |
| Anyline | 多数据库适配层 |
| PageHelper | 分页插件 |

### 工作流引擎

| 技术 | 说明 |
|------|------|
| Flowable | BPM工作流引擎 |
| Warm-Flow | 自研轻量级工作流 |
| LiteFlow | 规则引擎 |

### 其他核心组件

| 技术 | 说明 |
|------|------|
| Spring Cloud (可选) | Nacos 注册中心（默认禁用） |
| Swagger/Knife4j | API 文档 |
| AJ-Captcha | 滑动验证码 |
| EasyTrans | 分布式事务（可选） |
| Apache Flink | 实时计算 |

---

## 配置文件结构

### 1. 主配置文件

```
onebase-server/src/main/resources/
├── application.yaml               # 主配置文件
├── application-local.yaml         # 本地开发环境
├── application-sit.yaml           # 集成测试环境
├── application-offline.yaml       # 离线环境
├── application-dameng.yaml        # 达梦数据库配置
└── application-kingbase.yaml      # 人大金仓数据库配置
```

### 2. 模块级配置

每个核心模块都有自己的 `application.yaml`：

- **onebase-module-system-core** - 系统模块配置
- **onebase-module-infra-core** - 基础设施配置
- **onebase-module-metadata-runtime** - 元数据运行时配置

### 3. 配置优先级

```
application-{profile}.yaml > application.yaml > 模块配置
```

---

## 多环境配置

### 1. 环境说明

| 环境 | Profile | 说明 | 配置文件 |
|------|---------|------|----------|
| 本地开发 | local | 开发人员本地环境 | application-local.yaml |
| 集成测试 | sit | 测试环境 | application-sit.yaml |
| 离线环境 | offline | 内网离线环境 | application-offline.yaml |

### 2. 数据库环境

| 数据库 | Profile | 说明 |
|--------|---------|------|
| PostgreSQL | 默认 | 主要支持数据库 |
| DM达梦 | dameng | 国产数据库 |
| KingBase人大金仓 | kingbase | 国产数据库 |

### 3. 环境切换

**方式1：配置文件**
```yaml
spring:
  profiles:
    active: local  # 切换为：sit, offline, dameng, kingbase
```

**方式2：启动参数**
```bash
java -jar onebase-server.jar --spring.profiles.active=sit
```

**方式3：Maven参数**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

---

## 核心配置项

### 1. 应用基础配置

```yaml
spring:
  application:
    name: onebase-v3

  profiles:
    active: local

  main:
    allow-circular-references: true  # 允许循环依赖（三层架构）
```

### 2. 文件上传配置

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 16MB      # 单个文件大小
      max-request-size: 32MB   # 总上传大小
```

### 3. Jackson 序列化配置

```yaml
spring:
  jackson:
    serialization:
      write-dates-as-timestamps: true              # Date 使用时间戳
      write-date-timestamps-as-nanoseconds: false  # 不使用纳秒格式
      write-durations-as-timestamps: true          # Duration 使用时间戳
      fail-on-empty-beans: false                   # 允许空Bean序列化
```

### 4. 缓存配置

```yaml
spring:
  cache:
    type: REDIS
    redis:
      time-to-live: 1h  # 过期时间1小时
```

### 5. 编码配置

```yaml
server:
  servlet:
    encoding:
      enabled: true
      charset: UTF-8
      force: true  # 避免 WebFlux 流式返回乱码
```

---

## 数据库配置

### 1. MyBatis-Flex 配置

```yaml
mybatis-flex:
  configuration:
    mapUnderscoreToCamelCase: true  # 下划线转驼峰
  print-sql: true  # 打印SQL（生产环境建议关闭）
```

### 2. PageHelper 配置

```yaml
pagehelper:
  helperDialect: postgresql  # 数据库方言
  reasonable: false
  defaultCount: true
```

### 3. Anyline 配置

```yaml
anyline:
  is-debug: false
  is-log-sql: false          # 是否打印SQL
  is-log-slow-sql: false     # 是否打印慢SQL
  is-log-sql-time: false
  is-log-sql-warn: false
  is-log-sql-param: false
  is-log-sql-when-error: true         # 错误时打印SQL
  is-log-sql-param-when-error: true   # 错误时打印参数
```

### 4. 动态数据源配置

项目支持多数据源，通过 `spring.datasource.dynamic` 配置。

---

## 认证与安全

### 1. 多租户配置

```yaml
onebase:
  tenant:
    enable: true  # 启用多租户

    # 忽略租户过滤的URL
    ignore-urls:
      - /jmreport/*  # 积木报表
      - /admin-api/app/application/get

    # 访问时忽略租户检查的URL
    ignore-visit-urls:
      - /admin-api/system/user/profile/**
      - /admin-api/system/auth/**

    # 忽略多租户的缓存
    ignore-caches:
      - user_role_ids
      - permission_menu_ids
      - oauth_client
      - notify_template
```

### 2. 验证码配置（AJ-Captcha）

```yaml
aj:
  captcha:
    type: blockPuzzle          # 验证码类型：滑块拼图
    jigsaw: classpath:images/jigsaw
    pic-click: classpath:images/pic-click
    cache-type: redis          # 缓存类型
    cache-number: 1000
    water-mark: OneBase        # 水印文字
    interference-options: 0    # 滑动干扰项
    req-frequency-limit-enable: false
    req-get-lock-limit: 5      # 失败5次锁定
    req-get-lock-seconds: 10   # 锁定10秒
```

### 3. 短信验证码配置

```yaml
onebase:
  sms-code:
    expire-times: 10m                       # 过期时间10分钟
    send-frequency: 1m                      # 发送频率1分钟
    send-maximum-quantity-per-day: 10       # 每天最多10条
    begin-code: 9999  # 测试环境固定验证码
    end-code: 9999
```

---

## API 文档配置

### SpringDoc / Swagger 配置

```yaml
springdoc:
  api-docs:
    enabled: true
    path: /v3/api-docs
  swagger-ui:
    enabled: true
    path: /swagger-ui
  default-flat-param-object: true

onebase:
  swagger:
    title: OneBase快速开发平台
    description: 提供管理后台、用户 App 的所有功能
    version: 1.0.0
    email: xingyu4j@vip.qq.com
    license: MIT
```

**访问地址：**
- Swagger UI: `http://localhost:8080/swagger-ui`
- API Docs: `http://localhost:8080/v3/api-docs`

---

## 线程池配置

```yaml
spring:
  task:
    execution:
      pool:
        core-size: 10                  # 核心线程数
        max-size: 30                   # 最大线程数
        queue-capacity: 10             # 队列容量
        keep-alive: 300                # 空闲线程存活时间（秒）
        thread-name-prefix: onebase-executor-
```

---

## Spring Cloud 配置（可选）

默认情况下，Nacos 注册中心和配置中心是禁用的：

```yaml
spring:
  cloud:
    nacos:
      discovery:
        enabled: false  # 禁用服务注册发现
      config:
        enabled: false  # 禁用配置中心
```

如需启用微服务架构，设置为 `true` 并配置 Nacos 地址。

---

## 部署架构

### 1. 单机部署

#### 最低要求

- **CPU：** 4核
- **内存：** 8GB
- **磁盘：** 100GB SSD
- **Java：** JDK 17+
- **PostgreSQL：** 10+
- **Redis：** 6.0+

#### 部署步骤

```bash
# 1. 构建项目
mvn clean package -DskipTests

# 2. 启动服务
cd onebase-server/target
java -jar onebase-server-{version}.jar --spring.profiles.active=local

# 3. 验证
curl http://localhost:8080/actuator/health
```

### 2. 集群部署

#### 架构图

```
┌─────────────┐
│   Nginx     │  负载均衡
└──────┬──────┘
       │
       ├──────────────────────────┐
       │                          │
┌──────▼──────┐           ┌───────▼──────┐
│  Server 1   │           │   Server 2   │
│  (Build)    │           │  (Runtime)   │
└─────────────┘           └──────────────┘
       │                          │
       └──────────┬───────────────┘
                  │
          ┌───────▼────────┐
          │   PostgreSQL   │  主数据库
          │    (Master)    │
          └────────────────┘
                  │
          ┌───────▼────────┐
          │   PostgreSQL   │  从数据库（只读）
          │    (Slave)     │
          └────────────────┘
                  │
          ┌───────▼────────┐
          │     Redis      │  缓存集群
          │   (Cluster)    │
          └────────────────┘
```

#### 服务器配置

| 组件 | 数量 | 配置 |
|------|------|------|
| 应用服务器 | 2+ | 8核16GB |
| PostgreSQL主库 | 1 | 8核32GB, SSD |
| PostgreSQL从库 | 1+ | 8核32GB, SSD |
| Redis集群 | 3+ | 4核8GB |
| Nginx | 1+ | 4核8GB |

---

## 运维监控

### 1. 健康检查

```bash
# Spring Boot Actuator
curl http://localhost:8080/actuator/health

# 数据库连接检查
curl http://localhost:8080/actuator/health/db

# Redis连接检查
curl http://localhost:8080/actuator/health/redis
```

### 2. 日志管理

#### 日志级别

- **生产环境：** INFO
- **测试环境：** DEBUG
- **开发环境：** DEBUG

#### 日志路径

```
logs/
├── onebase-v3.log          # 主日志
├── onebase-v3-error.log    # 错误日志
└── onebase-v3-access.log   # 访问日志
```

### 3. 性能监控

#### JVM 参数建议（生产环境）

```bash
java -server \
  -Xms4g -Xmx4g \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/var/logs/heapdump.hprof \
  -Dspring.profiles.active=sit \
  -jar onebase-server.jar
```

### 4. 数据库备份

#### 自动备份脚本

```bash
#!/bin/bash
# PostgreSQL 备份脚本

BACKUP_DIR="/backup/postgres"
DATE=$(date +%Y%m%d_%H%M%S)
DB_NAME="onebase_cloud_v3"

# 全量备份
pg_dump -h localhost -U postgres $DB_NAME | gzip > $BACKUP_DIR/onebase_$DATE.sql.gz

# 保留最近7天的备份
find $BACKUP_DIR -name "onebase_*.sql.gz" -mtime +7 -delete
```

---

## 故障排查

### 1. 启动失败

**问题：** 循环依赖错误

**解决：** 确保 `spring.main.allow-circular-references=true`

---

**问题：** 数据库连接失败

**检查：**
```bash
# 测试数据库连接
psql -h 10.0.104.38 -U postgres -d onebase_cloud_v3
```

---

### 2. 性能问题

**慢SQL分析：**
- 启用 `mybatis-flex.print-sql=true`
- 查看 PostgreSQL 慢查询日志

**内存溢出：**
- 分析 heapdump 文件
- 调整 JVM 参数

---

## 下一步

- **Docker化**：创建 Dockerfile 和 docker-compose.yml
- **Kubernetes**：K8s 部署清单
- **CI/CD**：Jenkins/GitLab CI 流水线配置

---

**文档版本：** 1.0
**最后更新：** 2025-12-06
**维护者：** OneBase Team
