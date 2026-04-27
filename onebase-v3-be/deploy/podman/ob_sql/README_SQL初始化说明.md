# 数据库初始化脚本说明

本目录包含 OneBase 全量数据库初始化脚本，用于 PostgreSQL 容器启动时自动初始化数据库。

## 文件列表

PostgreSQL 容器会按**字母顺序**自动执行 `/docker-entrypoint-initdb.d/` 目录下的 `.sql` 文件：

| 文件 | 说明 | 执行顺序 |
|------|------|----------|
| `01_create_onebase_cloud_v3.sql` | 创建 onebase 用户、onebase_cloud_v3 数据库、授权 | 第1个 |
| `02_init_onebase_cloud_v3.sql` | 初始化 onebase_cloud_v3 表结构、序列、数据 | 第2个 |
| `03_create_onebase_ai.sql` | 创建 onebase_ai 数据库、授权 | 第3个 |
| `04_init_onebase_ai.sql` | 初始化 onebase_ai 表结构、数据 | 第4个 |
| `05_create_onebase_business.sql` | 创建 onebase_business 数据库、授权 | 第5个 |
| `06_create_onebase_dolphinscheduler.sql` | 创建 dolphinscheduler 数据库、授权 | 第6个 |
| `07_init_onebase_dolphinscheduler.sql` | 初始化 dolphinscheduler 表结构、数据 | 第7个 |

## 部署说明

### 自动初始化（推荐）

通过 `docker-compose.middleware.yaml` 自动挂载到 PostgreSQL 容器：

```yaml
volumes:
  - postgresql_data:/var/lib/postgresql/data
  - ../ob_sql:/docker-entrypoint-initdb.d:ro
```

容器首次启动时会自动执行所有 SQL 脚本。

### 手动初始化

如需手动执行，请按文件名字母顺序依次执行：

```bash
# 连接 PostgreSQL（使用管理员账号）
psql -h localhost -p 5432 -U postgres

# 按顺序执行脚本
\i 01_create_onebase_cloud_v3.sql
\i 02_init_onebase_cloud_v3.sql
\i 03_create_onebase_ai.sql
\i 04_init_onebase_ai.sql
\i 05_create_onebase_business.sql
\i 06_create_onebase_dolphinscheduler.sql
\i 07_init_onebase_dolphinscheduler.sql
```

## 数据库说明

| 数据库 | 说明 | 初始化内容 |
|--------|------|------------|
| onebase_cloud_v3 | 主业务数据库 | 完整表结构、序列、初始数据 |
| onebase_ai | AI 服务数据库 | AI 相关表结构、配置数据 |
| onebase_business | 业务运行态数据库 | 仅创建数据库，无初始表 |
| dolphinscheduler | DolphinScheduler 数据库 | 任务调度相关表结构 |

## 注意事项

1. **首次启动**：容器首次启动时会执行初始化脚本，如果数据卷已存在数据则不会重新执行
2. **重置数据库**：如需重新初始化，请删除 PostgreSQL 数据卷：`docker volume rm onebase-postgresql_data`
3. **字符编码**：所有数据库使用 UTF-8 编码
4. **用户权限**：统一使用 `onebase` 用户，默认密码 `onebase123`

## 默认账号

| 账号 | 密码 | 权限 |
|------|------|------|
| onebase | onebase123 | 所有数据库的所有权限 |