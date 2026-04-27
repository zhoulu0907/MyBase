# SQL 文件管理规范

## 目录结构

```
sql/
├── legacy/              # 历史文件（归档）
│   ├── postgresql/      # PostgreSQL 历史文件
│   ├── dameng/          # 达梦历史文件
│   ├── kingbase/        # 人大金仓历史文件
│   └── alter/           # 历史修改脚本
│
├── migration/           # 增量迁移脚本
│   ├── postgresql/
│   ├── mysql/
│   ├── dameng/
│   └── kingbase/
│
├── init/                # 系统初始化数据
├── tools/               # 运维工具脚本
└── test/                # 测试数据脚本
```

## 后续新增 SQL 规则

### 1. 增量迁移（migration/）

**命名格式**：
```
V{YYYY.MM.DD.NN}__{operation}_{table}.sql
```

| 部分 | 说明 | 示例 |
|------|------|------|
| `V` | 固定前缀 | `V` |
| `YYYY.MM.DD` | 日期 | `2026.03.19` |
| `NN` | 当日序号 | `01`, `02` |
| `__` | 双下划线 | `__` |
| `operation` | 操作类型 | `create`, `alter`, `drop`, `insert` |
| `table` | 目标表名 | `flow_connector_action` |

**示例**：
```
migration/postgresql/V2026.03.19.01__create_flow_connector_action.sql
migration/mysql/V2026.03.19.01__create_flow_connector_action.sql
```

### 2. 操作类型前缀

| 前缀 | 含义 |
|------|------|
| `create` | 创建表 |
| `alter` | 修改表结构 |
| `drop` | 删除表 |
| `insert` | 插入数据 |
| `update` | 更新数据 |
| `delete` | 删除数据 |
| `create_idx` | 创建索引 |

### 3. 存放位置规则

| 场景 | 存放位置 |
|------|---------|
| 新建表/修改表结构 | `migration/{db_type}/V{日期}__{操作}_{表名}.sql` |
| 初始化数据 | `init/{模块}_init.sql` |
| 运维工具脚本 | `tools/{用途}.sql` |
| 测试数据 | `test/{模块}_test.sql` |

## 注意事项

1. **迁移文件需同时维护所有目标数据库类型**
2. **迁移文件一旦执行，不可修改**
3. **同一天的多个迁移文件，序号递增（01, 02, 03...）**
4. **历史文件保持不动，新文件按此规范执行**