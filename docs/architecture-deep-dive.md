# OneBase v3 架构深入分析

> 最后更新: 2026-01-11
> 分析工具: Claude Code Explore Agent
> 文档版本: 1.0

---

## 文档概述

本文档基于对 OneBase v3 项目的深度代码扫描和架构分析，提供完整的技术架构、模块设计、技术实现细节。适合架构师、技术负责人和高级开发者阅读。

---

## 1. 项目定位与设计理念

### 1.1 项目定位

OneBase v3 是一个**企业级低代码开发平台**，旨在帮助企业和开发者快速构建业务应用，无需从零开始编写大量代码。

**核心价值主张:**
- **快速开发**: 通过可视化设计器和拖拽式配置，大幅减少手写代码量
- **灵活扩展**: 插件化架构 + 连接器机制，支持自定义扩展
- **企业就绪**: 多租户、权限控制、审计日志等企业级特性内置
- **技术先进**: 基于 Spring Boot 3.x + Java 17，采用现代化技术栈

### 1.2 架构设计理念

#### 模块化分层架构
```
┌─────────────────────────────────────────────────────────────┐
│                    Server Layer (服务层)                      │
│  onebase-server | onebase-server-platform | onebase-server-runtime │
├─────────────────────────────────────────────────────────────┤
│                    Module Layer (模块层)                      │
│  system | infra | app | data | flow | formula | bpm | dashboard │
├─────────────────────────────────────────────────────────────┤
│                   Framework Layer (框架层)                    │
│  web | security | orm | tenant | cache | mq | plugin          │
├─────────────────────────────────────────────────────────────┤
│                   Dependencies Layer (依赖层)                 │
│  onebase-dependencies (统一BOM)                              │
└─────────────────────────────────────────────────────────────┘
```

#### 分层职责

| 层级 | 职责 | 核心能力 |
|------|------|----------|
| **服务层** | 应用入口、配置管理、服务编排 | 4种部署模式、端口隔离 |
| **模块层** | 业务能力、领域逻辑 | 8大业务模块、API解耦 |
| **框架层** | 通用能力、技术中间件 | 15+ Starter、多租户 |
| **依赖层** | 版本管理、依赖控制 | 统一BOM |

#### Build/Runtime 分离设计

OneBase v3 创新性地采用了 **构建态/运行态分离** 架构：

```
┌─────────────────────────────────────────────────────────────┐
│                  构建态 (Platform)                            │
│  应用设计器 | 流程设计器 | 数据建模 | 权限配置                  │
│  onebase-server-platform (端口: 48081)                       │
├─────────────────────────────────────────────────────────────┤
│                  运行态 (Runtime)                             │
│  应用执行 | 流程执行 | 数据查询 | 用户交互                      │
│  onebase-server-runtime                                      │
└─────────────────────────────────────────────────────────────┘
```

**设计优势:**
- **职责分离**: 设计时与运行时解耦，降低复杂度
- **性能优化**: 运行态服务可独立扩展，提升用户体验
- **权限隔离**: 设计者权限与用户权限分离，增强安全性
- **灵活部署**: 可独立部署 Platform 或 Runtime，适应不同场景

---

## 2. 核心架构模式

### 2.1 模块化单体架构 (Modular Monolith)

OneBase v3 采用模块化单体架构，这是介于单体应用和微服务之间的架构模式。

**架构特点:**
```
onebase-cloud/
├── onebase-module-system/        # 系统模块 (内聚)
│   ├── system-api/               # API接口 (模块边界)
│   ├── system-core/              # 核心逻辑
│   ├── system-build/             # 构建态
│   └── system-runtime/           # 运行态
├── onebase-module-app/           # 应用模块 (内聚)
│   ├── app-api/                  # API接口 (模块边界)
│   ├── app-core/
│   ├── app-build/
│   └── app-runtime/
└── ...                           # 其他模块
```

**模块间通信规则:**
1. **只能依赖 API 层**: 模块间通过 `-api` 接口通信，避免直接依赖实现
2. **禁止循环依赖**: 严格依赖方向，下层不能依赖上层
3. **事件驱动通信**: 通过 Spring Events 实现跨模块事件通知
4. **API Gateway**: 所有外部请求统一通过 Controller 层

**演进路径:**
```
第一阶段: 模块化单体 (当前)
    ↓
第二阶段: 服务化拆分 (可选)
    将核心模块拆分为独立微服务
    ↓
第三阶段: 云原生部署
    Kubernetes + Service Mesh
```

### 2.2 多租户架构 (Multi-Tenancy)

OneBase v3 实现了**框架级多租户支持**，租户隔离贯穿整个技术栈。

#### 租户隔离层级

```
┌─────────────────────────────────────────────────────────────┐
│                    应用层隔离                                 │
│  租户感知的 Controller | 租户上下文自动传播                  │
├─────────────────────────────────────────────────────────────┤
│                    业务层隔离                                 │
│  租户独立的 Service 实例 | 租户级别的业务规则                │
├─────────────────────────────────────────────────────────────┤
│                    数据层隔离                                 │
│  Row-Level Security | 租户ID自动注入 | SQL 过滤              │
├─────────────────────────────────────────────────────────────┤
│                    缓存层隔离                                 │
│  租户隔离的缓存命名空间 | 租户级别的缓存键                   │
└─────────────────────────────────────────────────────────────┘
```

#### 技术实现

**1. 租户上下文传播**
```java
// TenantContext - 租户上下文持有者
public class TenantContext {
    private static final ThreadLocal<Long> TENANT_ID = new TransmittableThreadLocal<>();

    public static void setTenantId(Long tenantId) {
        TENANT_ID.set(tenantId);
    }

    public static Long getTenantId() {
        return TENANT_ID.get();
    }
}
```

**2. ORM 层租户过滤**
```java
// MyBatis-Flex 租户拦截器
@Table(tenantId = "tenant_id")
public class BaseEntity {
    private Long tenantId;
}

// 自动添加租户过滤条件
SELECT * FROM table_name WHERE tenant_id = ? AND deleted = 0
```

**3. 缓存租户隔离**
```java
@Cache(name = "user_info", key = "#userId", tenant = "#tenantId")
public User getUserById(Long userId, Long tenantId) {
    // 缓存键自动包含租户ID: user_info:{tenantId}:{userId}
}
```

### 2.3 插件化架构 (Plugin Architecture)

基于 **PF4J** 实现的插件化架构，支持运行时动态扩展。

#### 插件生命周期

```
┌──────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐
│  加载    → → │  解析    → → │  启动    → → │  运行    │
│  Load    │   │  Resolve │   │  Start   │   │  Run     │
└──────────┘   └──────────┘   └──────────┘   └──────────┘
     ↑                                           ↓
     └───────────────────────────────────────────┘
                    卸载 (Stop)
```

#### 插件扩展点

| 扩展点 | 说明 | 示例 |
|--------|------|------|
| **节点扩展** | 自定义流程节点 | 自定义审批节点、数据校验节点 |
| **连接器扩展** | 外部系统集成 | ERP、CRM、第三方API |
| **组件扩展** | UI组件库 | 自定义表单控件、图表组件 |
| **函数扩展** | 公式引擎函数 | 行业特定计算函数 |

#### 插件开发示例

```java
// 1. 定义扩展点
@ExtensionPoint
public interface FlowNodeProcessor {
    void process(FlowContext context);
}

// 2. 实现插件扩展
@Extension
public class CustomApprovalNode implements FlowNodeProcessor {
    @Override
    public void process(FlowContext context) {
        // 自定义审批逻辑
    }
}

// 3. 插件打包
// pom.xml
<plugin>
    <groupId>com.cmsr.onebase</groupId>
    <artifactId>onebase-plugin-maven-plugin</artifactId>
</plugin>
```

---

## 3. 核心业务架构

### 3.1 流程引擎架构 (Flow Engine)

OneBase v3 实现了基于**组件化流程编排**的流程引擎。

#### 流程架构设计

```
┌─────────────────────────────────────────────────────────────┐
│                   流程设计层 (Build)                          │
│  可视化设计器 | 节点配置 | 连线编辑 | 流程验证                 │
├─────────────────────────────────────────────────────────────┤
│                   流程核心层 (Core)                           │
│  流程定义 | 流程实例 | 上下文管理 | 状态机                    │
├─────────────────────────────────────────────────────────────┤
│                   流程组件层 (Component)                      │
│  开始/结束 | 任务节点 | 条件分支 | 并行网关 | 子流程           │
│  脚本节点 | 数据查询 | 连接器节点 | ...                        │
├─────────────────────────────────────────────────────────────┤
│                   流程执行层 (Runtime)                        │
│  流程调度 | 异步执行 | 事件触发 | 监控统计                    │
└─────────────────────────────────────────────────────────────┘
```

#### 通用连接器架构

连接器是流程引擎的核心扩展机制，用于集成外部系统。

**连接器接口定义:**
```java
public interface Connector {
    /**
     * 连接器标识
     */
    String getType();

    /**
     * 配置验证
     */
    void validateConfig(ConnectorConfig config) throws ConnectorException;

    /**
     * 执行连接器逻辑
     */
    ConnectorResult execute(ConnectorContext context) throws ConnectorException;
}
```

**内置连接器:**

| 连接器类型 | 支持的系统 | 功能 |
|-----------|-----------|------|
| **数据库连接器** | MySQL, PostgreSQL, Oracle, DM8, KingBase | 数据查询、数据更新 |
| **短信连接器** | 阿里云短信 | 短信发送、模板短信 |
| **邮箱连接器** | SMTP/POP3/IMAP | 邮件发送、邮件读取 |
| **HTTP连接器** | RESTful API | HTTP请求、API调用 |

**连接器执行流程:**
```
1. 配置验证 (validateConfig)
    ↓
2. 参数解析 (parseParams)
    ↓
3. 连接建立 (establishConnection)
    ↓
4. 业务执行 (executeBusiness)
    ↓
5. 结果封装 (buildResult)
    ↓
6. 资源清理 (cleanup)
```

### 3.2 元数据管理架构 (Metadata)

元数据是 OneBase v3 数据管理模块的核心，支持动态表结构定义。

#### 元数据层次结构

```
数据源 (DataSource)
    ↓
实体 (Entity)
    ↓
字段 (Field)
    ↓
视图 (View)
```

#### 元数据类型系统

| 字段类型 | Java类型 | 数据库类型 | 验证规则 |
|---------|---------|-----------|----------|
| STRING | String | VARCHAR | 长度、正则 |
| INTEGER | Integer | INT | 范围 |
| DECIMAL | BigDecimal | DECIMAL | 精度、小数位 |
| DATE | LocalDate | DATE | 日期格式 |
| DATETIME | LocalDateTime | TIMESTAMP | 日期时间格式 |
| REFERENCE | Long | BIGINT | 外键引用 |
| ENUM | String | VARCHAR | 枚举值 |

#### 动态数据访问

```java
// 基于元数据的动态查询
public class DynamicDataService {
    public PageResult<Map<String, Object>> query(
        String entityCode,
        QueryCondition condition,
        PageParam pageParam
    ) {
        // 1. 根据实体代码获取元数据定义
        EntityMetadata metadata = metadataService.getByCode(entityCode);

        // 2. 构建动态SQL
        String sql = buildSql(metadata, condition);

        // 3. 执行查询
        List<Map<String, Object>> data = jdbcTemplate.queryForList(sql);

        // 4. 数据转换
        return convert(metadata, data, pageParam);
    }
}
```

### 3.3 公式引擎架构 (Formula Engine)

OneBase v3 支持多引擎的公式计算能力。

#### 支持的公式引擎

| 引擎 | 优势 | 适用场景 |
|------|------|----------|
| **GraalVM** | 高性能、支持JS语法 | 复杂计算、JS函数库 |
| **JEXL3** | 轻量级、表达式语法 | 简单条件判断 |
| **MVEL2** | 强大的表达式语言 | 业务规则引擎 |

#### 内置函数库

```java
// 数学函数
Math.abs(-1)          // 1
Math.round(1.5)       // 2
Math.max(1, 2)        // 2

// 字符串函数
String.substring("abc", 0, 2)  // "ab"
String.length("abc")           // 3

// 日期函数
Date.now()            // 当前时间
Date.format(date, "yyyy-MM-dd")  // 格式化日期

// 聚合函数
Agg.sum(list, "field")    // 求和
Agg.avg(list, "field")    // 平均值
```

---

## 4. 技术栈深度解析

### 4.1 ORM 技术选型

OneBase v3 采用 **MyBatis-Flex + Anyline 双 ORM** 架构。

#### MyBatis-Flex (主要 ORM)

**选型原因:**
- **MyBatis 增强**: 兼容 MyBatis 生态，学习成本低
- **联表查询**: 支持多表关联查询
- **读写分离**: 内置主从数据库支持
- **多租户**: 原生支持租户隔离

**核心特性:**
```java
// 1. 实体定义
@Table(value = "system_user", tenantId = "tenant_id")
public class UserDO {
    @Id(keyType = KeyType.Auto)
    private Long id;

    private String username;
    private String password;

    @TableField(insertStrategy = FieldStrategy.NOT_NULL)
    private String email;
}

// 2. 查询构建
QueryWrapper query = QueryWrapper.create()
    .select()
    .from(SYSTEM_USER)
    .where(SYSTEM_USER.USERNAME.eq("admin"))
    .and(SYSTEM_USER.DELETED.eq(0));

List<UserDO> users = mapper.selectListByQuery(query);
```

#### Anyline (多数据库 ORM)

**选型原因:**
- **数据库无关**: 自动适配不同数据库方言
- **国产数据库**: 支持达梦、人大金仓等
- **时序数据库**: 支持 TDengine

**使用场景:**
- 需要支持多种数据库的功能
- 数据库无关的元数据管理
- 跨数据库的数据同步

### 4.2 缓存架构

采用 **Redisson + JetCache 双层缓存** 架构。

#### 缓存层级

```
┌─────────────────────────────────────────────────────────────┐
│                    应用层缓存 (本地)                          │
│  Caffeine | Guava Cache | ConcurrentHashMap                │
├─────────────────────────────────────────────────────────────┤
│                    缓存抽象层 (JetCache)                      │
│  统一缓存API | 多级缓存 | 缓存刷新 | 缓存监控                │
├─────────────────────────────────────────────────────────────┤
│                    分布式缓存 (Redis)                         │
│  Redisson (Redis Client) | 分布式锁 | 发布订阅               │
└─────────────────────────────────────────────────────────────┘
```

#### JetCache 使用示例

```java
// 缓存配置
@Cached(name = "user:", key = "#userId", expire = 3600, cacheType = CacheType.BOTH)
public User getUserById(Long userId) {
    return userMapper.selectById(userId);
}

// 缓存刷新
@CacheInvalidate(name = "user:", key = "#userId")
public void updateUser(Long userId, User user) {
    userMapper.updateById(user);
}
```

### 4.3 工作流引擎

OneBase v3 集成 **Flowable** 作为 BPM 工作流引擎。

#### Flowable 集成架构

```
┌─────────────────────────────────────────────────────────────┐
│                    BPM 流程设计层                             │
│  BPMN 建模器 | 流程定义 | 表单设计                            │
├─────────────────────────────────────────────────────────────┤
│                    Flowable 引擎层                           │
│  流程引擎 | 任务引擎 | 身份引擎 | 表单引擎                    │
├─────────────────────────────────────────────────────────────┤
│                    OneBase 集成层                            │
│  BPM API | 任务服务 | 流程监控 | 权限集成                     │
└─────────────────────────────────────────────────────────────┘
```

#### 核心功能

| 功能 | 说明 |
|------|------|
| **流程定义** | BPMN 2.0 标准、可视化建模 |
| **流程实例** | 流程启动、流程流转、流程撤回 |
| **任务管理** | 待办任务、已办任务、任务委派 |
| **流程监控** | 流程跟踪、流程统计、性能分析 |

---

## 5. 数据库设计规范

### 5.1 表命名规范

```sql
-- 格式: {模块}_{业务实体}
-- 示例:
system_user          -- 系统模块用户表
system_role          -- 系统模块角色表
app_application      -- 应用模块应用表
flow_definition      -- 流程模块流程定义表
metadata_entity      -- 元数据模块实体表
```

### 5.2 通用字段设计

```sql
-- 所有业务表包含以下通用字段
CREATE TABLE example_table (
    id              BIGINT          PRIMARY KEY,
    creator         BIGINT          NOT NULL,                -- 创建人
    create_time     TIMESTAMP       NOT NULL DEFAULT NOW(),  -- 创建时间
    updater         BIGINT          NOT NULL,                -- 更新人
    update_time     TIMESTAMP       NOT NULL DEFAULT NOW(),  -- 更新时间
    deleted         SMALLINT        NOT NULL DEFAULT 0,      -- 软删除标记
    tenant_id       BIGINT          NOT NULL,                -- 租户ID
    -- 业务字段
    ...
);

-- 索引
CREATE INDEX idx_tenant_id ON example_table(tenant_id);
CREATE INDEX idx_deleted ON example_table(deleted);
CREATE INDEX idx_create_time ON example_table(create_time);
```

### 5.3 软删除机制

```sql
-- 软删除查询
SELECT * FROM example_table WHERE deleted = 0;

-- 软删除更新
UPDATE example_table SET deleted = 1 WHERE id = ?;

-- 唯一索引处理 (包含 deleted)
CREATE UNIQUE INDEX idx_unique_name
ON example_table(name, deleted)
WHERE deleted = 0;
```

### 5.4 多租户隔离

```sql
-- 租户隔离查询
SELECT * FROM example_table
WHERE tenant_id = ? AND deleted = 0;

-- 租户隔离约束
ALTER TABLE example_table
ADD CONSTRAINT fk_tenant_id
FOREIGN KEY (tenant_id) REFERENCES system_tenant(id);
```

---

## 6. 安全架构

### 6.1 认证授权架构

```
┌─────────────────────────────────────────────────────────────┐
│                    认证层 (Authentication)                    │
│  OAuth2 | JWT | Social Login (微信/钉钉)                      │
├─────────────────────────────────────────────────────────────┤
│                    授权层 (Authorization)                     │
│  RBAC (角色权限) | ABAC (属性权限) | 数据权限                 │
├─────────────────────────────────────────────────────────────┤
│                    审计层 (Audit)                             │
│  操作日志 | 登录日志 | 数据变更追踪                           │
└─────────────────────────────────────────────────────────────┘
```

### 6.2 数据安全

| 安全措施 | 技术实现 |
|---------|----------|
| **数据脱敏** | Jackson 注解、自定义序列化器 |
| **加密存储** | 敏感字段 AES 加密 |
| **SQL 注入防护** | MyBatis 参数化查询 |
| **XSS 防护** | 输入校验、输出转义 |
| **CSRF 防护** | Token 验证 |

---

## 7. 监控与运维

### 7.1 日志管理

```
┌─────────────────────────────────────────────────────────────┐
│                    日志采集层                                 │
│  Logback | Log4j2 | Structured Logging                       │
├─────────────────────────────────────────────────────────────┤
│                    日志处理层                                 │
│  日志分类 | 日志脱敏 | 日志聚合                               │
├─────────────────────────────────────────────────────────────┤
│                    日志存储层                                 │
│  API访问日志 | 错误日志 | 业务操作日志                        │
└─────────────────────────────────────────────────────────────┘
```

### 7.2 健康检查

```java
// Spring Boot Actuator 健康检查
@Component
public class CustomHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        // 检查数据库连接
        if (databaseHealthCheck()) {
            return Health.up()
                .withDetail("database", "UP")
                .build();
        }
        return Health.down()
            .withDetail("database", "DOWN")
            .build();
    }
}
```

---

## 8. 性能优化

### 8.1 数据库优化

- **索引优化**: 合理创建索引、覆盖索引
- **查询优化**: 避免 N+1 查询、使用 JOIN
- **分页优化**: 使用游标分页替代 OFFSET
- **读写分离**: 主从数据库、读写分离

### 8.2 缓存优化

- **多级缓存**: 本地缓存 + 分布式缓存
- **缓存预热**: 系统启动时加载热点数据
- **缓存更新**: 定时刷新、事件驱动更新
- **缓存监控**: 缓存命中率、缓存大小监控

### 8.3 JVM 优化

```bash
# JVM 参数配置
JAVA_OPTS="
-Xms2g -Xmx4g                    # 堆内存
-XX:+UseG1GC                     # G1垃圾回收器
-XX:MaxGCPauseMillis=200         # 最大GC暂停时间
-XX:+HeapDumpOnOutOfMemoryError  # OOM时生成堆转储
-XX:HeapDumpPath=/logs/heap.hprof
"
```

---

## 9. 扩展指南

### 9.1 添加新模块

```bash
# 1. 创建模块目录
mkdir -p onebase-module-{name}/{name}-api,{name}-core,{name}-build,{name}-runtime

# 2. 定义 pom.xml
cd onebase-module-{name}
# 创建各子模块的 pom.xml

# 3. 在根 pom.xml 注册
<modules>
    <module>onebase-module-{name}</module>
</modules>

# 4. 实现模块功能
# -api: 定义接口和DTO
# -core: 实现核心业务逻辑
# -build: 提供构建态API
# -runtime: 提供运行态API
```

### 9.2 添加自定义连接器

```java
// 1. 实现Connector接口
public class CustomConnector extends AbstractConnector {
    @Override
    public String getType() {
        return "custom";
    }

    @Override
    public void executeInternal(ConnectorContext context) {
        // 自定义连接器逻辑
    }
}

// 2. 注册连接器
@Component
public class CustomConnectorRegistrar {
    @PostConstruct
    public void register() {
        ConnectorRegistry.register(new CustomConnector());
    }
}
```

### 9.3 添加公式函数

```java
// 1. 实现函数接口
@FunctionInfo(name = "CUSTOM_FUNC", description = "自定义函数")
public class CustomFunction extends AbstractFunction {
    @Override
    public Object execute(Expression expression, Object... args) {
        // 函数实现
        return result;
    }
}

// 2. 注册函数
FunctionRegistry.register(new CustomFunction());
```

---

## 10. 最佳实践

### 10.1 代码组织

- **分层清晰**: Controller → Service → Repository → DO
- **职责单一**: 每个类只做一件事
- **接口隔离**: 模块间通过API接口通信
- **依赖倒置**: 面向接口编程

### 10.2 事务管理

```java
// 事务传播特性
@Transactional(propagation = Propagation.REQUIRED)  // 默认
@Transactional(propagation = Propagation.REQUIRES_NEW)  // 新事务
@Transactional(propagation = Propagation.NOT_SUPPORTED)  // 非事务
@Transactional(readonly = true)  // 只读事务
```

### 10.3 异常处理

```java
// 统一异常处理
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public CommonResult handleBusinessException(BusinessException ex) {
        return CommonResult.error(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public CommonResult handleException(Exception ex) {
        log.error("系统异常", ex);
        return CommonResult.error(500, "系统异常");
    }
}
```

---

## 附录

### A. 技术栈版本总览

| 类别 | 技术 | 版本 |
|------|------|------|
| **Java** | JDK | 17+ |
| **Spring** | Spring Boot | 3.4.5 |
| **Spring** | Spring Cloud | 2024.0.1 |
| **ORM** | MyBatis-Flex | 1.11.4 |
| **ORM** | Anyline | 8.7.2 |
| **数据库** | PostgreSQL | 42.7.4 |
| **缓存** | Redisson | 3.52.0 |
| **缓存** | JetCache | 2.7.8 |
| **工作流** | Flowable | 7.0.1 |
| **规则引擎** | LiteFlow | 2.15.2 |
| **插件** | PF4J | 3.14.0 |

### B. 相关文档链接

- [项目概述](./project-overview.md)
- [模块参考](./modules-reference.md)
- [技术栈详解](./technology-stack.md)
- [开发指南](./development-guide.md)

---

**文档维护**: 本文档应随项目架构演进持续更新
**更新频率**: 每次重大架构变更后更新
**责任人**: 架构师团队
