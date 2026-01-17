# Huangjie 代码快速参考

**更新日期:** 2025-12-28
**用途:** 一页纸速查表，快速查找常用信息

---

## 📍 核心文件位置

### Flow模块（流程引擎）
```
onebase-module-flow/onebase-module-flow-core/src/main/java/com/cmsr/onebase/module/flow/core/flow/
├── FlowProcessExecutor.java:44          ⭐⭐⭐⭐⭐ 流程执行器（修改44次）
└── FlowProcessExecServiceImpl.java      ⭐⭐⭐⭐ 流程执行服务

onebase-module-flow/onebase-module-flow-context/src/main/java/com/cmsr/onebase/module/flow/context/
├── express/ExpressionExecutor.java      ⭐⭐⭐⭐⭐ 表达式执行器（修改16次）
└── ExecuteContext.java:23              ⭐⭐⭐⭐⭐ 执行上下文（修改23次）

onebase-module-flow/onebase-module-flow-component/src/main/java/com/cmsr/onebase/module/flow/component/
├── data/DataAddNodeComponent.java      ⭐⭐⭐⭐⭐ 数据新增节点（修改22次）
├── data/DataUpdateNodeComponent.java   ⭐⭐⭐⭐ 数据更新节点（修改20次）
├── data/DataQueryNodeComponent.java    ⭐⭐⭐⭐ 数据查询节点（修改19次）
└── logic/IfCaseNodeComponent.java      ⭐⭐⭐⭐ 条件判断节点（修改15次）
```

### ORM框架（数据访问层）
```
onebase-framework/onebase-spring-boot-starter-orm/src/main/java/com/cmsr/onebase/framework/orm/repository/
├── BaseBizRepository.java              ⭐⭐⭐⭐⭐ 业务基础仓储（修改59次）
└── BaseAppRepository.java              ⭐⭐⭐⭐ 应用基础仓储（修改17次）

onebase-framework/onebase-spring-boot-starter-orm/src/main/java/com/cmsr/onebase/framework/orm/util/
└── QueryWrapperUtils.java              ⭐⭐⭐⭐⭐ 查询包装工具（修改65次）
```

### App模块（应用管理）
```
onebase-module-app/onebase-module-app-core/src/main/java/com/cmsr/onebase/module/app/core/dal/database/
└── AppMenuRepository.java              ⭐⭐⭐⭐ 菜单仓储（修改20次）

onebase-module-app/onebase-module-app-runtime/src/main/java/com/cmsr/onebase/module/app/runtime/service/impl/
├── AppAuthPermissionServiceImpl.java   ⭐⭐⭐⭐ 权限服务（修改26次）
├── AppAuthRoleServiceImpl.java         ⭐⭐⭐ 角色服务（修改20次）
└── AppApplicationServiceImpl.java      ⭐⭐⭐ 应用服务（修改17次）
```

---

## 💻 常用命令

### Maven命令
```bash
# 清理编译
mvn clean install -Dmaven.test.skip=true

# 启动项目（本地环境）
mvn spring-boot:run -pl onebase-server -am -Dspring-boot.run.profiles=local

# 运行测试
mvn test

# 查看依赖树
mvn dependency:tree
```

### Git命令
```bash
# 查看Huangjie最近3个月的提交
git log --author="huangjie" --since="3 months ago" --oneline

# 查看某个提交的详细变更
git show <commit-hash> --stat

# 查看某个文件的修改历史
git log --follow --patch -- <file-path>

# 对比两个版本之间的差异
git diff <commit-hash-1> <commit-hash-2> -- <file-path>
```

### 数据库操作
```bash
# 连接PostgreSQL
psql -U postgres -d onebase

# 查看流程执行日志
SELECT * FROM flow_execution_log ORDER BY create_time DESC LIMIT 10;

# 查看应用列表
SELECT * FROM app_application ORDER BY create_time DESC;
```

---

## 📝 Commit Message规范

### 格式
```
<type>(<scope>): <subject>
```

### 类型定义
| 类型 | 说明 | 示例 |
|------|------|------|
| `feat` | 新功能 | `feat(menu): 添加菜单项排序查询功能` |
| `fix` | 修复bug | `fix(flow): 处理表达式执行结果为空的情况` |
| `refactor` | 重构（不改变功能） | `refactor(orm): 移除实体类中的常量定义` |
| `opt` | 优化（性能提升） | `opt(app): 优化版本重复校验逻辑` |
| `style` | 代码格式调整 | `style(flow): 统一代码格式` |
| `test` | 测试相关 | `test(flow): 添加流程执行测试用例` |
| `docs` | 文档更新 | `docs(readme): 更新启动说明` |

### 作用域（scope）
- `flow`: 流程模块
- `orm`: ORM框架
- `app`: 应用模块
- `menu`: 菜单模块
- `auth`: 权限模块
- `etl`: ETL模块

### 示例
```bash
git commit -m "feat(menu): 添加菜单项排序查询功能"
git commit -m "fix(flow): 处理表达式执行结果为空的情况"
git commit -m "refactor(orm): 移除实体类中的常量定义并统一使用工具类"
```

---

## 🎨 代码风格要点

### 1. 租户隔离（自动注入）
```java
// ✅ 正确：无需手动添加租户条件
List<AppMenuDO> menus = QueryWrapperUtils.create()
    .where(AppMenuDO::getApplicationId).eq(appId)
    .and(AppMenuDO::getVersionTag).eq(versionTag)
    .list();
// Repository会自动注入tenant_code

// ❌ 错误：不要手动添加租户条件
List<AppMenuDO> menus = QueryWrapperUtils.create()
    .where(AppMenuDO::getTenantCode).eq(tenantCode)  // 不要这样做
    .where(AppMenuDO::getApplicationId).eq(appId)
    .list();
```

### 2. 版本管理（versionTag）
```java
// 编辑态 -> 0
// 运行态 -> 1
// 历史版 -> 其他数字

// 发布：编辑态转运行态
baseBizRepository.copyEditToRuntime(applicationId);

// 回滚：历史版转运行态
baseBizRepository.copyHistoryToRuntime(applicationId, versionTag);
```

### 3. 异常处理（try-finally）
```java
try {
    // 1. 业务逻辑
    ExecutorResult result = executeFlow(processId, variableContext, executeContext);
    return result;
} catch (Exception e) {
    // 2. 记录异常
    log.error("执行流程异常", e);
    return ExecutorResult.error(processId, "执行流程异常", e);
} finally {
    // 3. 保证资源清理
    executionLog.setLogText(executeContext.getLogText());
    executionLog.setEndTime(LocalDateTime.now());
    flowExecutionLogRepository.save(executionLog);
}
```

### 4. 日志记录（SLF4J）
```java
// ✅ 使用占位符
log.error("执行流程异常, traceId: {}, processId: {}", traceId, processId, e);

// ✅ 使用异常工具类
executionLog.setErrorMessage(ExceptionUtils.getMessage(e));

// ❌ 不要字符串拼接
log.error("执行流程异常, traceId: " + traceId);  // 不要这样做
```

### 5. 空值处理
```java
// ✅ 使用StringUtils
if (StringUtils.isEmpty(traceId)) {
    return ExecutorResult.error("traceId不能为空");
}

// ✅ 使用CollectionUtils
if (CollectionUtils.isNotEmpty(list)) {
    // ...
}

// ✅ 早期返回
if (traceId == null) {
    return error;
}
// 继续处理

// ❌ 避免深层嵌套
if (traceId != null) {
    if (processId != null) {
        // ... 50行业务逻辑
    }
}
```

---

## 🔧 常用工具类

### MyBatis-Flex
```java
// 链式查询
QueryWrapperUtils.create()
    .where(Entity::getField).eq(value)
    .and(Entity::getStatus).eq(1)
    .orderBy(Entity::getCreateTime, false)
    .list();

// UpdateChain更新
UpdateChain.of(mapper)
    .set(Entity::getField, newValue)
    .where(Entity::getId).eq(id)
    .update();
```

### 集合工具
```java
// 判空
CollectionUtils.isEmpty(list)
CollectionUtils.isNotEmpty(list)

// 字符串判空
StringUtils.isEmpty(str)
StringUtils.isNotEmpty(str)

// Java 8 Stream
list.stream()
    .filter(item -> item.isValid())
    .map(Item::getValue)
    .collect(Collectors.toList());
```

### 日期时间
```java
// 当前时间
LocalDateTime.now()

// 时间差
Duration duration = Duration.between(start, end);
long millis = duration.toMillis();

// 格式化
DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
```

---

## 🚀 快速启动步骤

### 1. 启动数据库
```bash
# PostgreSQL
brew services start postgresql
# 或
pg_ctl -D /usr/local/var/postgres start
```

### 2. 启动项目
```bash
# 方式1：Maven命令
mvn spring-boot:run -pl onebase-server -am -Dspring-boot.run.profiles=local

# 方式2：IDEA
# 找到 OneBaseServerApplication.java，右键Run
```

### 3. 验证启动
```bash
curl http://localhost:8080/actuator/health
# 预期输出：{"status":"UP"}
```

---

## 🐛 常见问题排查

### 流程执行失败
1. 检查流程定义是否存在
2. 检查节点配置是否正确
3. 查看执行日志：`flow_execution_log`表
4. 检查表达式语法

### 数据查询为空
1. 检查`applicationId`是否正确
2. 检查`versionTag`是否正确
3. 检查租户隔离是否生效
4. 查看SQL日志

### 权限判断错误
1. 检查用户角色是否正确
2. 检查角色菜单关联是否正确
3. 查看`app_auth_permission`表

---

## 📚 核心概念

### 三个版本状态
```
编辑态(BUILD, 0) -> [发布] -> 运行态(RUNTIME, 1)
运行态(RUNTIME, 1) -> [备份] -> 历史版(2, 3, 4, ...)
历史版(2, 3, 4, ...) -> [回滚] -> 运行态(RUNTIME, 1)
```

### 多租户隔离
- 通过`tenant_code`字段隔离不同租户
- Repository自动注入租户条件
- 无需手动编码

### 应用版本管理
- 通过`version_tag`字段隔离不同版本
- 开发时使用编辑态(0)
- 发布后使用运行态(1)
- 支持版本回滚

---

## 🎯 学习路径速查

### 第1周：框架层基础
- 阅读`BaseBizRepository.java`
- 阅读`QueryWrapperUtils.java`
- 理解租户隔离机制

### 第2周：代码规范
- 阅读`huangjie-code-style-analysis.md`
- 学习命名规范
- 学习异常处理

### 第3周：ORM框架
- 精读`BaseBizRepository.java`源码
- 实现自定义Repository
- 编写单元测试

### 第4-6周：Flow引擎
- 阅读`FlowProcessExecutor.java`
- 阅读`ExpressionExecutor.java`
- 实现自定义节点组件

### 第7周：App模块
- 阅读`AppMenuRepository.java`
- 理解RBAC权限模型
- 实现菜单树查询

### 第8周：实战项目
- 实现审批流程系统
- 综合运用所学知识
- 代码审查和优化

---

## 📞 获取帮助

### 文档资源
- 代码接手指南：`docs/huangjie-code-handover-guide.md`
- 代码风格分析：`docs/huangjie-code-style-analysis.md`
- 学习路径计划：`docs/huangjie-module-learning-path.md`

### 在线资源
- MyBatis-Flex文档：https://mybatis-flex.com/
- LiteFlow文档：https://liteflow.cc/
- Spring Boot文档：https://spring.io/projects/spring-boot

### 团队成员
- Huangjie：原开发者
- 其他开发人员：查看Git log

---

**更新日期:** 2025-12-28
**维护者:** Kanten (Claude Code)

---

> 💡 **提示**: 这是一页纸速查表，涵盖了最常用的信息。如果需要更详细的内容，请参考完整的文档。建议打印出来贴在显示器旁边，方便随时查阅。
