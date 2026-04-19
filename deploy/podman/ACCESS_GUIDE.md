# OneBase 访问指南

## 访问地址汇总

### OneBase 后端 API（通过前端统一 nginx）

**网关地址**：http://localhost

| 服务名称 | 路径前缀 | 说明 |
|----------|----------|------|
| 编辑态服务 API | /build-api/ | 应用编辑、页面设计、工作流编排 |
| 运行态服务 API | /runtime-api/ | 应用运行、数据查询、API 调用 |
| Flink 服务 API | /flink-api/ | 数据处理、ETL 任务 |

### API 示例

| 接口 | 完整地址 |
|------|----------|
| 登录 | http://localhost/build-api/admin-api/system/auth/login |
| 页面列表 | http://localhost/build-api/admin-api/app/page/list |
| 运行态数据查询 | http://localhost/runtime-api/runtime-api/app/{appId}/data/query |

### 中间件服务（直接端口访问）

| 服务名称 | 访问地址 | 默认账号 |
|----------|----------|----------|
| PostgreSQL | localhost:5432 | onebase / onebase123 |
| Redis | localhost:6379 | - / redis123 |
| MinIO API | http://localhost:9000 | minioadmin / minioadmin123 |
| MinIO Console | http://localhost:9001 | minioadmin / minioadmin123 |
| DolphinScheduler | http://localhost:12345/dolphinscheduler | admin / dolphinscheduler123 |
| NodeSandbox | http://localhost:3000 | - |

---

## 服务使用流程

### 1. 编辑态服务 (Build)

**访问地址**：http://localhost/build/

**主要功能**：
- 应用管理：创建、编辑、删除应用
- 页面设计：可视化页面编辑器
- 数据建模：数据表、字段定义
- 工作流编排：流程设计、节点配置
- API 管理：接口定义、权限配置

**使用流程**：
1. 访问 http://localhost/build/
2. 登录系统（使用系统管理员账号）
3. 进入「应用管理」创建新应用
4. 进入「页面设计」编辑页面
5. 进入「数据建模」定义数据结构
6. 进入「工作流」编排业务流程
7. 发布应用

### 2. 运行态服务 (Runtime)

**访问地址**：http://localhost/runtime/

**主要功能**：
- 应用访问：运行已发布的应用
- 数据查询：查看业务数据
- API 调用：外部系统集成

**使用流程**：
1. 访问 http://localhost/runtime/
2. 通过应用 ID 访问应用
3. 查询、操作业务数据
4. 调用 API 接口

### 3. DolphinScheduler

**访问地址**：http://localhost:12345/dolphinscheduler

**主要功能**：
- 任务调度：定时任务、依赖任务
- 工作流管理：DAG 编排
- 资源管理：文件、脚本管理

**使用流程**：
1. 访问 http://localhost:12345/dolphinscheduler
2. 登录系统（admin / dolphinscheduler123）
3. 创建项目
4. 编排工作流
5. 配置调度规则
6. 监控任务执行

### 4. MinIO

**访问地址**：http://localhost:9001（Console）

**主要功能**：
- 文件存储：上传、下载、删除文件
- Bucket 管理：创建、配置存储桶
- 权限管理：访问策略配置

**使用流程**：
1. 访问 MinIO Console: http://localhost:9001
2. 登录（minioadmin / minioadmin123）
3. 创建 Bucket
4. 上传/管理文件
5. 配置访问策略

---

## API 接口说明

### 编辑态 API

**路径前缀**：/build-api/admin-api

**主要接口**：

| 接口 | 方法 | 完整地址 |
|------|------|----------|
| 登录 | POST | /build-api/admin-api/system/auth/login |
| 登出 | POST | /build-api/admin-api/system/auth/logout |
| 页面列表 | GET | /build-api/admin-api/app/page/list |
| 创建页面 | POST | /build-api/admin-api/app/page/create |
| 更新页面 | PUT | /build-api/admin-api/app/page/update |
| 删除页面 | DELETE | /build-api/admin-api/app/page/delete/{id} |

### 运行态 API

**路径前缀**：/runtime-api/runtime-api

**主要接口**：

| 接口 | 方法 | 完整地址 |
|------|------|----------|
| 获取页面 | GET | /runtime-api/runtime-api/app/{appId}/page/{pageId} |
| 查询数据 | POST | /runtime-api/runtime-api/app/{appId}/data/query |
| 保存数据 | POST | /runtime-api/runtime-api/app/{appId}/data/save |
| 删除数据 | POST | /runtime-api/runtime-api/app/{appId}/data/delete |

### Flink API

**路径前缀**：/flink-api/

### 认证方式

API 认证使用 Bearer Token：

```bash
curl -H "Authorization: Bearer {token}" http://localhost/build-api/admin-api/app/page/list
```

Token 获取方式：
1. 调用登录接口
2. 从响应中获取 accessToken
3. 在后续请求中携带 Token

### MinIO API

MinIO 通过独立端口访问，不经过 nginx：

```bash
# MinIO S3 API
aws s3 --endpoint-url http://localhost:9000 ls

# 使用 mc 客户端
mc alias set myminio http://localhost:9000 minioadmin minioadmin123
mc ls myminio
```

---

## 健康检查

### 后端服务健康检查（通过 nginx）

```bash
# 编辑态服务
curl http://localhost/build-api/actuator/health

# 运行态服务
curl http://localhost/runtime-api/actuator/health

# 预期响应
{
  "status": "UP"
}
```

### 中间件健康检查

```bash
# PostgreSQL
docker exec onebase-postgresql pg_isready -U onebase

# Redis
docker exec onebase-redis redis-cli -a redis123 ping

# MinIO
curl http://localhost:9000/minio/health/live

# DolphinScheduler
curl http://localhost:12345/dolphinscheduler
```

---

## 常见问题

### Q1: 无法访问服务

**排查步骤**：
1. 检查服务是否启动：`./deploy.sh ps`
2. 检查端口是否正确
3. 检查防火墙设置
4. 检查日志：`./deploy.sh logs`

### Q2: API 返回 401 未授权

**解决方案**：
1. 检查 Token 是否正确
2. 检查 Token 是否过期
3. 重新登录获取新 Token

### Q3: 页面加载缓慢

**可能原因**：
1. 服务器资源不足
2. 网络延迟
3. 数据库查询慢

**解决方案**：
1. 增加 JVM 内存
2. 检查网络连接
3. 优化数据库查询

### Q4: 文件上传失败

**排查步骤**：
1. 检查 MinIO 服务状态
2. 检查 Bucket 是否存在
3. 检查文件大小限制
4. 检查 MinIO 配置

### Q5: 任务调度不执行

**排查步骤**：
1. 检查 DolphinScheduler 服务状态
2. 检查任务配置
3. 检查调度规则
4. 查看任务日志

---

## 日志查看

### 查看服务日志

```bash
# 所有服务日志
./deploy.sh logs

# 指定服务日志
./deploy.sh logs build
./deploy.sh logs runtime
./deploy.sh logs flink
```

### 查看中间件日志

```bash
# PostgreSQL
docker logs onebase-postgresql

# Redis
docker logs onebase-redis

# MinIO
docker logs onebase-minio

# DolphinScheduler
docker logs onebase-dolphinscheduler
```

---

## 性能优化建议

### JVM 调优

```bash
# 编辑态服务（推荐配置）
JAVA_OPTS=-Xms2g -Xmx4g -XX:+UseG1GC

# 运行态服务（推荐配置）
JAVA_OPTS=-Xms2g -Xmx4g -XX:+UseG1GC

# Flink 服务（推荐配置）
JAVA_OPTS=-Xms1g -Xmx2g -XX:+UseG1GC
```

### 数据库优化

1. 调整连接池大小
2. 创建必要的索引
3. 定期清理日志数据

### Redis 优化

1. 设置合理的 maxmemory
2. 选择合适的淘汰策略
3. 监控内存使用

---

## 安全建议

1. **修改默认密码**：部署后立即修改所有默认密码
2. **网络隔离**：将服务部署在内网，通过网关暴露
3. **HTTPS**：生产环境使用 HTTPS
4. **定期备份**：定期备份数据库和文件存储
5. **访问控制**：配置合理的访问权限