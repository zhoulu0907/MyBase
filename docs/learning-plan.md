# OneBase v3.0 后端学习计划

**制定日期**: 2025-12-30  
**学习周期**: 10天  
**目标水平**: 能够对外介绍项目、承接需求  
**适用人群**: 新加入的开发者、需要快速上线的团队成员

---

## 一、学习目标

### 总体目标
通过10天的系统学习，达到以下能力：
- ✅ 能够清晰介绍OneBase v3.0的架构、技术栈和核心功能
- ✅ 能够理解并解释多租户、版本管理、流程编排等核心概念
- ✅ 能够独立进行简单的功能开发和Bug修复
- ✅ 能够承接新需求并进行技术方案设计
- ✅ 能够进行代码审查和技术分享

### 能力维度

| 能力维度 | 学习前 | 学习后 |
|---------|--------|--------|
| **项目理解** | 不了解 | 能清晰介绍项目架构和核心功能 |
| **代码阅读** | 困难 | 能快速定位代码、理解业务逻辑 |
| **功能开发** | 无法独立 | 能独立开发简单功能 |
| **需求承接** | 无法评估 | 能评估需求、设计方案 |
| **技术分享** | 无法进行 | 能进行技术分享和代码审查 |

---

## 二、学习时间安排

### 10天学习路线图

```
Day 1-2:  项目概览与环境搭建
Day 3-4:  核心框架深入（ORM + 多租户）
Day 5-6:  流程引擎核心（Flow模块）
Day 7:    应用管理模块（App模块）
Day 8:    插件系统架构（当前重点）
Day 9:    实战开发（小功能实现）
Day 10:   综合考核与分享
```

### 每日时间分配

| 时间段 | 内容 | 时长 |
|--------|------|------|
| 上午 (9:00-12:00) | 理论学习 + 代码阅读 | 3小时 |
| 下午 (14:00-17:00) | 实践操作 + 编码练习 | 3小时 |
| 晚上 (19:00-21:00) | 复习总结 + 笔记整理 | 2小时 |

**每日总学习时间**: 8小时

---

## 三、详细学习计划

### Day 1: 项目概览与环境搭建

#### 学习目标
- 了解OneBase v3.0的整体架构和业务定位
- 搭建本地开发环境
- 理解项目目录结构和模块划分

#### 学习内容

**上午 (3小时)**

1. **项目概览** (1小时)
   - 阅读 [`README.md`](../README.md)
   - 阅读 [`docs/index.md`](index.md)
   - 阅读 [`docs/project-analysis-summary.md`](project-analysis-summary.md)
   - 理解项目定位：企业级低代码平台后端

2. **技术栈学习** (1小时)
   - Spring Boot 3.4.5 基础
   - MyBatis-Flex ORM框架
   - LiteFlow 流程编排
   - Flowable BPM工作流

3. **模块架构理解** (1小时)
   - 理解9大核心模块组
   - 理解分层架构（api/core/build/runtime/platform）
   - 绘制模块依赖关系图

**下午 (3小时)**

1. **环境搭建** (1.5小时)
   ```bash
   # 1. 安装依赖
   mvn clean install -Dmaven.test.skip=true
   
   # 2. 配置数据库
   # 修改 onebase-server/src/main/resources/application-local.yaml
   
   # 3. 启动项目
   mvn spring-boot:run -pl onebase-server -am -Dspring-boot.run.profiles=local
   ```

2. **项目结构探索** (1.5小时)
   - 使用IDEA打开项目
   - 浏览各个模块的目录结构
   - 找到主启动类：`OneBaseServerApplication.java`
   - 理解配置文件：`application.yaml`

**晚上 (2小时)**

1. **笔记整理** (1小时)
   - 整理项目架构笔记
   - 绘制模块关系图
   - 记录关键配置项

2. **预习准备** (1小时)
   - 预习ORM框架基础
   - 预习多租户概念

#### 验收标准
- [ ] 能口述OneBase v3.0的定位和核心功能
- [ ] 本地环境成功启动项目
- [ ] 能画出9大模块的关系图
- [ ] 能说出每个模块后缀的含义（api/core/build/runtime/platform）

#### 学习资源
- [`README.md`](../README.md)
- [`docs/index.md`](index.md)
- [`docs/project-analysis-summary.md`](project-analysis-summary.md)
- Spring Boot官方文档：https://spring.io/projects/spring-boot
- MyBatis-Flex文档：https://mybatis-flex.com/

---

### Day 2: 数据模型与数据库设计

#### 学习目标
- 理解OneBase的数据模型设计
- 掌握通用字段约定
- 理解多租户隔离机制

#### 学习内容

**上午 (3小时)**

1. **数据模型概览** (1小时)
   - 阅读 [`docs/data-models-backend.md`](data-models-backend.md)
   - 理解100+张表的分类
   - 理解通用字段约定

2. **核心表结构学习** (2小时)
   - App模块表：`app_application`、`app_menu`、`app_auth_role`
   - System模块表：`system_users`、`system_role`、`system_tenant`
   - Flow模块表：`flow_process`、`flow_process_exec`、`flow_execution_log`
   - Metadata模块表：`metadata_business_entity`、`metadata_entity_field`

**下午 (3小时)**

1. **数据库实践** (2小时)
   ```bash
   # 1. 连接数据库
   psql -U postgres -d onebase
   
   # 2. 查看表结构
   \d app_application
   \d system_users
   \d flow_process
   
   # 3. 查询数据
   SELECT * FROM app_application ORDER BY create_time DESC LIMIT 10;
   SELECT * FROM system_users WHERE deleted = 0;
   ```

2. **ER图绘制** (1小时)
   - 使用DBeaver或DbDiagram.io
   - 绘制App模块ER图
   - 绘制System模块ER图

**晚上 (2小时)**

1. **多租户机制深入** (1.5小时)
   - 理解`tenant_id`字段的作用
   - 理解平台级数据（tenant_id = 0）
   - 理解租户级数据（tenant_id > 0）

2. **版本管理机制** (0.5小时)
   - 理解`version_tag`字段
   - 理解编辑态(0)、运行态(1)、历史版(2,3,4...)

#### 验收标准
- [ ] 能说出通用字段的7个字段及其作用
- [ ] 能解释多租户隔离的实现机制
- [ ] 能解释版本管理的三种状态
- [ ] 能画出App模块的ER图
- [ ] 能独立查询数据库并理解表结构

#### 学习资源
- [`docs/data-models-backend.md`](data-models-backend.md)
- PostgreSQL文档：https://www.postgresql.org/docs/
- DBeaver工具：https://dbeaver.io/

---

### Day 3: ORM框架深入

#### 学习目标
- 掌握BaseBizRepository的核心功能
- 理解QueryWrapperUtils的使用
- 掌握租户隔离的自动注入机制

#### 学习内容

**上午 (3小时)**

1. **BaseBizRepository源码阅读** (2小时)
   - 阅读 [`BaseBizRepository.java`](../onebase-framework/onebase-spring-boot-starter-orm/src/main/java/com/cmsr/onebase/framework/orm/repository/BaseBizRepository.java)
   - 理解自动注入租户条件的实现
   - 理解版本管理的方法（copyEditToRuntime、copyHistoryToRuntime）
   - 理解UpdateChain链式更新

2. **QueryWrapperUtils学习** (1小时)
   - 阅读 [`QueryWrapperUtils.java`](../onebase-framework/onebase-spring-boot-starter-orm/src/main/java/com/cmsr/onebase/framework/orm/util/QueryWrapperUtils.java)
   - 理解链式查询的用法
   - 理解自动创建applicationId和versionTag列

**下午 (3小时)**

1. **ORM实践** (2小时)
   ```java
   // 练习1：基础查询
   List<AppMenuDO> menus = QueryWrapperUtils.create()
       .where(AppMenuDO::getApplicationId).eq(appId)
       .and(AppMenuDO::getVersionTag).eq(versionTag)
       .list();
   
   // 练习2：链式更新
   UpdateChain.of(mapper)
       .set(AppMenuDO::getMenuName, "新名称")
       .where(AppMenuDO::getId).eq(id)
       .update();
   
   // 练习3：版本管理
   baseBizRepository.copyEditToRuntime(applicationId);
   ```

2. **租户隔离验证** (1小时)
   - 编写测试代码验证租户隔离
   - 查看生成的SQL，确认自动注入了tenant_id条件

**晚上 (2小时)**

1. **源码深入分析** (1.5小时)
   - 分析BaseBizRepository的AOP实现
   - 分析租户条件注入的时机
   - 分析版本管理的实现逻辑

2. **笔记整理** (0.5小时)
   - 整理ORM框架的核心方法
   - 整理常用查询和更新模式

#### 验收标准
- [ ] 能解释BaseBizRepository的3大核心功能
- [ ] 能熟练使用QueryWrapperUtils进行查询
- [ ] 能使用UpdateChain进行链式更新
- [ ] 能解释租户隔离的自动注入机制
- [ ] 能独立编写CRUD代码

#### 学习资源
- [`BaseBizRepository.java`](../onebase-framework/onebase-spring-boot-starter-orm/src/main/java/com/cmsr/onebase/framework/orm/repository/BaseBizRepository.java)
- [`QueryWrapperUtils.java`](../onebase-framework/onebase-spring-boot-starter-orm/src/main/java/com/cmsr/onebase/framework/orm/util/QueryWrapperUtils.java)
- MyBatis-Flex文档：https://mybatis-flex.com/

---

### Day 4: 开发规范与代码风格

#### 学习目标
- 掌握OneBase的开发规范
- 理解代码风格要求
- 掌握异常处理和日志记录规范

#### 学习内容

**上午 (3小时)**

1. **代码规范学习** (1.5小时)
   - 阅读 [`docs/huangjie-code-style-analysis.md`](huangjie-code-style-analysis.md)
   - 阅读 [`docs/huangjie-quick-reference.md`](huangjie-quick-reference.md)
   - 理解命名规范、注释规范、格式规范

2. **Commit Message规范** (1.5小时)
   - 学习Commit Message格式：`<type>(<scope>): <subject>`
   - 学习类型定义：feat、fix、refactor、opt、style、test、docs
   - 学习作用域：flow、orm、app、menu、auth

**下午 (3小时)**

1. **异常处理实践** (1.5小时)
   ```java
   // 练习：try-finally模式
   try {
       // 1. 业务逻辑
       ExecutorResult result = executeFlow(processId, variableContext, executeContext);
       return result;
   } catch (Exception e) {
       // 2. 记录异常
       log.error("执行流程异常, traceId: {}, processId: {}", traceId, processId, e);
       return ExecutorResult.error(processId, "执行流程异常", e);
   } finally {
       // 3. 保证资源清理
       executionLog.setLogText(executeContext.getLogText());
       executionLog.setEndTime(LocalDateTime.now());
       flowExecutionLogRepository.save(executionLog);
   }
   ```

2. **日志记录实践** (1.5小时)
   ```java
   // 练习：使用占位符
   log.error("执行流程异常, traceId: {}, processId: {}", traceId, processId, e);
   
   // 练习：使用异常工具类
   executionLog.setErrorMessage(ExceptionUtils.getMessage(e));
   
   // 练习：空值处理
   if (StringUtils.isEmpty(traceId)) {
       return ExecutorResult.error("traceId不能为空");
   }
   ```

**晚上 (2小时)**

1. **代码审查练习** (1小时)
   - 阅读Huangjie的代码提交记录
   - 分析代码风格和规范
   - 总结优秀代码的特点

2. **Git实践** (1小时)
   ```bash
   # 练习：查看提交历史
   git log --author="huangjie" --since="3 months ago" --oneline
   
   # 练习：查看某个提交的详细变更
   git show <commit-hash> --stat
   
   # 练习：提交代码
   git add .
   git commit -m "feat(menu): 添加菜单项排序查询功能"
   ```

#### 验收标准
- [ ] 能说出Commit Message的7种类型
- [ ] 能写出符合规范的Commit Message
- [ ] 能使用try-finally模式处理异常
- [ ] 能正确使用日志占位符
- [ ] 能进行简单的代码审查

#### 学习资源
- [`docs/huangjie-code-style-analysis.md`](huangjie-code-style-analysis.md)
- [`docs/huangjie-quick-reference.md`](huangjie-quick-reference.md)
- Git文档：https://git-scm.com/doc

---

### Day 5: Flow流程引擎（上）

#### 学习目标
- 理解Flow模块的整体架构
- 掌握流程执行器的核心逻辑
- 理解执行上下文的管理

#### 学习内容

**上午 (3小时)**

1. **Flow模块概览** (1小时)
   - 阅读 [`onebase-module-flow/README.md`](../onebase-module-flow/README.md)
   - 理解Flow模块的子模块划分
   - 理解流程编排的核心概念

2. **FlowProcessExecutor源码阅读** (2小时)
   - 阅读 [`FlowProcessExecutor.java`](../onebase-module-flow/onebase-module-flow-core/src/main/java/com/cmsr/onebase/module/flow/core/flow/FlowProcessExecutor.java:44)
   - 理解流程执行的核心逻辑
   - 理解异常处理和日志记录
   - 理解执行结果的返回

**下午 (3小时)**

1. **ExecuteContext学习** (1.5小时)
   - 阅读 [`ExecuteContext.java`](../onebase-module-flow/onebase-module-flow-context/src/main/java/com/cmsr/onebase/module/flow/context/ExecuteContext.java:23)
   - 理解执行上下文的作用
   - 理解变量管理、节点结果管理、日志管理

2. **ExpressionExecutor学习** (1.5小时)
   - 阅读 [`ExpressionExecutor.java`](../onebase-module-flow/onebase-module-flow-context/src/main/java/com/cmsr/onebase/module/flow/context/express/ExpressionExecutor.java)
   - 理解表达式执行器的作用
   - 理解MVEL和JEXL引擎的使用

**晚上 (2小时)**

1. **流程执行流程图绘制** (1小时)
   - 绘制FlowProcessExecutor的执行流程图
   - 标注关键步骤和决策点

2. **笔记整理** (1小时)
   - 整理Flow模块的核心类
   - 整理流程执行的关键步骤

#### 验收标准
- [ ] 能解释Flow模块的作用和核心功能
- [ ] 能画出FlowProcessExecutor的执行流程图
- [ ] 能解释ExecuteContext的作用
- [ ] 能解释ExpressionExecutor的作用
- [ ] 能说出流程执行的3个阶段

#### 学习资源
- [`FlowProcessExecutor.java`](../onebase-module-flow/onebase-module-flow-core/src/main/java/com/cmsr/onebase/module/flow/core/flow/FlowProcessExecutor.java:44)
- [`ExecuteContext.java`](../onebase-module-flow/onebase-module-flow-context/src/main/java/com/cmsr/onebase/module/flow/context/ExecuteContext.java:23)
- [`ExpressionExecutor.java`](../onebase-module-flow/onebase-module-flow-context/src/main/java/com/cmsr/onebase/module/flow/context/express/ExpressionExecutor.java)
- LiteFlow文档：https://liteflow.cc/

---

### Day 6: Flow流程引擎（下）

#### 学习目标
- 掌握节点组件的实现
- 理解数据节点、逻辑节点、交互节点的区别
- 能够实现自定义节点组件

#### 学习内容

**上午 (3小时)**

1. **节点组件基类学习** (1小时)
   - 阅读 [`SkippableNodeComponent.java`](../onebase-module-flow/onebase-module-flow-component/src/main/java/com/cmsr/onebase/module/flow/component/SkippableNodeComponent.java)
   - 理解节点组件的基类
   - 理解可跳过节点的实现

2. **数据节点学习** (2小时)
   - 阅读 [`DataAddNodeComponent.java`](../onebase-module-flow/onebase-module-flow-component/src/main/java/com/cmsr/onebase/module/flow/component/data/DataAddNodeComponent.java)
   - 阅读 [`DataUpdateNodeComponent.java`](../onebase-module-flow/onebase-module-flow-component/src/main/java/com/cmsr/onebase/module/flow/component/data/DataUpdateNodeComponent.java)
   - 阅读 [`DataQueryNodeComponent.java`](../onebase-module-flow/onebase-module-flow-component/src/main/java/com/cmsr/onebase/module/flow/component/data/DataQueryNodeComponent.java)
   - 理解数据节点的实现模式

**下午 (3小时)**

1. **逻辑节点学习** (1.5小时)
   - 阅读 [`IfCaseNodeComponent.java`](../onebase-module-flow/onebase-module-flow-component/src/main/java/com/cmsr/onebase/module/flow/component/logic/IfCaseNodeComponent.java)
   - 阅读 [`SwitchCaseNodeComponent.java`](../onebase-module-flow/onebase-module-flow-component/src/main/java/com/cmsr/onebase/module/flow/component/logic/SwitchCaseNodeComponent.java)
   - 理解条件判断的实现

2. **交互节点学习** (1.5小时)
   - 阅读 [`ModalNodeComponent.java`](../onebase-module-flow/onebase-module-flow-component/src/main/java/com/cmsr/onebase/module/flow/component/interact/ModalNodeComponent.java)
   - 阅读 [`NavigateNodeComponent.java`](../onebase-module-flow/onebase-module-flow-component/src/main/java/com/cmsr/onebase/module/flow/component/interact/NavigateNodeComponent.java)
   - 理解交互节点的实现

**晚上 (2小时)**

1. **自定义节点组件实践** (1.5小时)
   ```java
   // 练习：实现一个自定义数据节点
   @Component("customDataNode")
   public class CustomDataNodeComponent extends SkippableNodeComponent {
       
       @Override
       public void execute(NodeComponent component) {
           // 1. 获取输入参数
           // 2. 执行业务逻辑
           // 3. 设置输出结果
       }
   }
   ```

2. **节点组件对比总结** (0.5小时)
   - 对比数据节点、逻辑节点、交互节点的区别
   - 总结节点组件的实现模式

#### 验收标准
- [ ] 能说出节点组件的4种类型
- [ ] 能解释数据节点的实现模式
- [ ] 能解释逻辑节点的实现模式
- [ ] 能实现一个简单的自定义节点组件
- [ ] 能说出节点组件的基类

#### 学习资源
- [`SkippableNodeComponent.java`](../onebase-module-flow/onebase-module-flow-component/src/main/java/com/cmsr/onebase/module/flow/component/SkippableNodeComponent.java)
- [`DataAddNodeComponent.java`](../onebase-module-flow/onebase-module-flow-component/src/main/java/com/cmsr/onebase/module/flow/component/data/DataAddNodeComponent.java)
- [`IfCaseNodeComponent.java`](../onebase-module-flow/onebase-module-flow-component/src/main/java/com/cmsr/onebase/module/flow/component/logic/IfCaseNodeComponent.java)
- [`ModalNodeComponent.java`](../onebase-module-flow/onebase-module-flow-component/src/main/java/com/cmsr/onebase/module/flow/component/interact/ModalNodeComponent.java)

---

### Day 7: App应用管理模块

#### 学习目标
- 理解App模块的整体架构
- 掌握菜单管理的实现
- 理解RBAC权限模型

#### 学习内容

**上午 (3小时)**

1. **App模块概览** (1小时)
   - 理解App模块的子模块划分
   - 理解应用、菜单、权限的关系
   - 绘制App模块的架构图

2. **菜单管理学习** (2小时)
   - 阅读 [`AppMenuRepository.java`](../onebase-module-app/onebase-module-app-core/src/main/java/com/cmsr/onebase/module/app/core/dal/database/AppMenuRepository.java)
   - 理解菜单树形结构的构建
   - 理解菜单的排序查询

**下午 (3小时)**

1. **权限管理学习** (2小时)
   - 阅读 [`AppAuthPermissionServiceImpl.java`](../onebase-module-app/onebase-module-app-runtime/src/main/java/com/cmsr/onebase/module/app/runtime/service/impl/AppAuthPermissionServiceImpl.java)
   - 阅读 [`AppAuthRoleServiceImpl.java`](../onebase-module-app/onebase-module-app-runtime/src/main/java/com/cmsr/onebase/module/app/runtime/service/impl/AppAuthRoleServiceImpl.java)
   - 理解RBAC权限模型的实现

2. **应用管理学习** (1小时)
   - 阅读 [`AppApplicationServiceImpl.java`](../onebase-module-app/onebase-module-app-runtime/src/main/java/com/cmsr/onebase/module/app/runtime/service/impl/AppApplicationServiceImpl.java)
   - 理解应用版本管理

**晚上 (2小时)**

1. **菜单树构建实践** (1小时)
   ```java
   // 练习：构建菜单树
   List<AppMenuDO> menus = appMenuRepository.getMenuTree(applicationId, versionTag);
   // 理解树形结构的构建逻辑
   ```

2. **权限判断实践** (1小时)
   ```java
   // 练习：权限判断
   boolean hasPermission = appAuthPermissionService.hasPermission(userId, menuId, operationType);
   // 理解权限判断的逻辑
   ```

#### 验收标准
- [ ] 能解释App模块的作用和核心功能
- [ ] 能解释菜单树形结构的构建逻辑
- [ ] 能解释RBAC权限模型的实现
- [ ] 能说出应用版本管理的3种状态
- [ ] 能独立进行菜单查询和权限判断

#### 学习资源
- [`AppMenuRepository.java`](../onebase-module-app/onebase-module-app-core/src/main/java/com/cmsr/onebase/module/app/core/dal/database/AppMenuRepository.java)
- [`AppAuthPermissionServiceImpl.java`](../onebase-module-app/onebase-module-app-runtime/src/main/java/com/cmsr/onebase/module/app/runtime/service/impl/AppAuthPermissionServiceImpl.java)
- [`AppAuthRoleServiceImpl.java`](../onebase-module-app/onebase-module-app-runtime/src/main/java/com/cmsr/onebase/module/app/runtime/service/impl/AppAuthRoleServiceImpl.java)

---

### Day 8: 插件系统架构

#### 学习目标
- 理解插件系统的背景和目标
- 掌握插件系统的核心架构
- 理解7个扩展点的设计

#### 学习内容

**上午 (3小时)**

1. **插件系统PRD学习** (1.5小时)
   - 阅读 [`docs/prd.md`](prd.md)
   - 理解插件系统的背景和痛点
   - 理解插件系统的核心价值

2. **插件系统架构学习** (1.5小时)
   - 阅读 [`docs/architecture-plugin-system.md`](architecture-plugin-system.md)
   - 理解插件系统的技术方案
   - 理解热部署机制

**下午 (3小时)**

1. **插件系统技术规格学习** (2小时)
   - 阅读 [`docs/sprint-artifacts/tech-spec-plugin-system.md`](sprint-artifacts/tech-spec-plugin-system.md)
   - 理解插件系统的数据库设计
   - 理解插件系统的开发计划

2. **7个扩展点学习** (1小时)
   - 理解流程扩展（P0）
   - 理解API扩展（P0）
   - 理解数据钩子（P1）
   - 理解定时任务（P1）
   - 理解事件监听（P2）
   - 理解公式函数（P2）
   - 理解UI扩展（P2）

**晚上 (2小时)**

1. **插件系统架构图绘制** (1小时)
   - 绘制插件系统的架构图
   - 标注核心组件和扩展点

2. **插件开发流程梳理** (1小时)
   - 梳理插件开发的完整流程
   - 梳理插件的生命周期管理

#### 验收标准
- [ ] 能解释插件系统的背景和目标
- [ ] 能说出插件系统的7个扩展点
- [ ] 能解释热部署机制的实现
- [ ] 能说出插件系统的5张核心表
- [ ] 能画出插件系统的架构图

#### 学习资源
- [`docs/prd.md`](prd.md)
- [`docs/architecture-plugin-system.md`](architecture-plugin-system.md)
- [`docs/sprint-artifacts/tech-spec-plugin-system.md`](sprint-artifacts/tech-spec-plugin-system.md)

---

### Day 9: 实战开发

#### 学习目标
- 通过实际开发巩固所学知识
- 掌握完整的功能开发流程
- 能够独立承接简单需求

#### 学习内容

**上午 (3小时)**

1. **需求分析** (1小时)
   - 选择一个简单的需求进行开发
   - 示例需求：为App模块添加菜单搜索功能
   - 分析需求的技术方案

2. **技术方案设计** (2小时)
   - 设计数据库表结构（如需要）
   - 设计API接口
   - 设计Service层逻辑
   - 设计Controller层接口

**下午 (3小时)**

1. **代码实现** (2.5小时)
   ```java
   // 1. 创建DO实体类
   // 2. 创建Mapper接口
   // 3. 创建Service接口和实现
   // 4. 创建Controller接口
   // 5. 编写单元测试
   ```

2. **测试验证** (0.5小时)
   - 启动项目
   - 使用Postman测试API
   - 验证功能正确性

**晚上 (2小时)**

1. **代码审查** (1小时)
   - 自我审查代码
   - 检查是否符合开发规范
   - 检查是否有潜在Bug

2. **文档编写** (1小时)
   - 编写API文档
   - 编写功能说明文档
   - 编写使用示例

#### 验收标准
- [ ] 能独立完成一个简单功能的开发
- [ ] 能进行需求分析和技术方案设计
- [ ] 能编写符合规范的代码
- [ ] 能进行自我代码审查
- [ ] 能编写完整的功能文档

#### 学习资源
- [`docs/huangjie-code-index.md`](huangjie-code-index.md)
- [`docs/huangjie-quick-reference.md`](huangjie-quick-reference.md)

---

### Day 10: 综合考核与分享

#### 学习目标
- 综合考核学习成果
- 进行技术分享
- 制定后续学习计划

#### 学习内容

**上午 (3小时)**

1. **综合考核** (2小时)
   - 理论考核：口述项目架构、核心概念
   - 实践考核：现场开发一个小功能
   - 代码审查：审查一段代码

2. **考核评分** (1小时)
   - 根据考核标准进行评分
   - 识别薄弱环节
   - 制定改进计划

**下午 (3小时)**

1. **技术分享准备** (1.5小时)
   - 准备技术分享PPT
   - 整理学习心得
   - 准备演示Demo

2. **技术分享** (1.5小时)
   - 进行技术分享
   - 回答问题
   - 收集反馈

**晚上 (2小时)**

1. **学习总结** (1小时)
   - 总结10天的学习成果
   - 总结学习方法和经验
   - 总结遇到的问题和解决方案

2. **后续学习计划** (1小时)
   - 制定后续深入学习计划
   - 制定技能提升计划
   - 制定项目实践计划

#### 验收标准
- [ ] 能清晰介绍OneBase v3.0的架构和核心功能
- [ ] 能独立承接简单需求
- [ ] 能进行技术分享
- [ ] 能制定后续学习计划
- [ ] 达到对外介绍、承接需求的水平

#### 学习资源
- [`docs/project-analysis-summary.md`](project-analysis-summary.md)
- [`docs/learning-plan.md`](learning-plan.md)

---

## 四、学习资源汇总

### 核心文档
| 文档 | 路径 | 用途 |
|------|------|------|
| 项目索引 | [`docs/index.md`](index.md) | 项目文档总索引 |
| 项目分析总结 | [`docs/project-analysis-summary.md`](project-analysis-summary.md) | 项目分析报告 |
| 产品需求 | [`docs/prd.md`](prd.md) | 插件系统PRD |
| 架构文档 | [`docs/architecture-plugin-system.md`](architecture-plugin-system.md) | 插件系统架构 |
| 技术规格 | [`docs/sprint-artifacts/tech-spec-plugin-system.md`](sprint-artifacts/tech-spec-plugin-system.md) | 插件系统技术规格 |
| 数据模型 | [`docs/data-models-backend.md`](data-models-backend.md) | 数据库设计 |
| 代码索引 | [`docs/huangjie-code-index.md`](huangjie-code-index.md) | 代码位置索引 |
| 快速参考 | [`docs/huangjie-quick-reference.md`](huangjie-quick-reference.md) | 一页纸速查表 |
| 代码风格分析 | [`docs/huangjie-code-style-analysis.md`](huangjie-code-style-analysis.md) | 代码风格规范 |

### 核心代码文件
| 文件 | 路径 | 重要性 |
|------|------|--------|
| FlowProcessExecutor | [`onebase-module-flow/.../FlowProcessExecutor.java`](../onebase-module-flow/onebase-module-flow-core/src/main/java/com/cmsr/onebase/module/flow/core/flow/FlowProcessExecutor.java:44) | ⭐⭐⭐⭐⭐ |
| ExecuteContext | [`onebase-module-flow/.../ExecuteContext.java`](../onebase-module-flow/onebase-module-flow-context/src/main/java/com/cmsr/onebase/module/flow/context/ExecuteContext.java:23) | ⭐⭐⭐⭐⭐ |
| BaseBizRepository | [`onebase-framework/.../BaseBizRepository.java`](../onebase-framework/onebase-spring-boot-starter-orm/src/main/java/com/cmsr/onebase/framework/orm/repository/BaseBizRepository.java) | ⭐⭐⭐⭐⭐ |
| QueryWrapperUtils | [`onebase-framework/.../QueryWrapperUtils.java`](../onebase-framework/onebase-spring-boot-starter-orm/src/main/java/com/cmsr/onebase/framework/orm/util/QueryWrapperUtils.java) | ⭐⭐⭐⭐⭐ |
| AppMenuRepository | [`onebase-module-app/.../AppMenuRepository.java`](../onebase-module-app/onebase-module-app-core/src/main/java/com/cmsr/onebase/module/app/core/dal/database/AppMenuRepository.java) | ⭐⭐⭐⭐ |
| AppAuthPermissionServiceImpl | [`onebase-module-app/.../AppAuthPermissionServiceImpl.java`](../onebase-module-app/onebase-module-app-runtime/src/main/java/com/cmsr/onebase/module/app/runtime/service/impl/AppAuthPermissionServiceImpl.java) | ⭐⭐⭐⭐ |

### 在线资源
- Spring Boot文档：https://spring.io/projects/spring-boot
- MyBatis-Flex文档：https://mybatis-flex.com/
- LiteFlow文档：https://liteflow.cc/
- Flowable文档：https://www.flowable.com/open-source/docs/
- PostgreSQL文档：https://www.postgresql.org/docs/
- Git文档：https://git-scm.com/doc

---

## 五、考核标准

### 理论考核（40分）

| 考核项 | 分值 | 考核内容 |
|--------|------|----------|
| 项目架构 | 10分 | 能清晰介绍OneBase v3.0的架构、技术栈和核心功能 |
| 核心概念 | 10分 | 能解释多租户、版本管理、流程编排等核心概念 |
| 模块功能 | 10分 | 能说出9大模块的作用和核心功能 |
| 插件系统 | 10分 | 能解释插件系统的背景、目标和7个扩展点 |

### 实践考核（40分）

| 考核项 | 分值 | 考核内容 |
|--------|------|----------|
| 环境搭建 | 5分 | 能独立搭建本地开发环境并启动项目 |
| 数据库操作 | 10分 | 能独立查询数据库并理解表结构 |
| 代码开发 | 15分 | 能独立开发一个简单功能（菜单搜索、权限判断等） |
| 测试验证 | 10分 | 能进行功能测试并验证正确性 |

### 代码审查（20分）

| 考核项 | 分值 | 考核内容 |
|--------|------|----------|
| 代码规范 | 10分 | 代码符合开发规范（命名、注释、格式） |
| 代码质量 | 10分 | 代码质量良好（无明显Bug、逻辑清晰） |

### 总分：100分
- **90分以上**: 优秀，达到对外介绍、承接需求的水平
- **80-89分**: 良好，基本达到要求，需要加强薄弱环节
- **70-79分**: 及格，需要继续学习
- **70分以下**: 不及格，需要重新学习

---

## 六、后续学习计划

### 深入学习方向

1. **插件系统开发** (2周)
   - 深入学习插件系统的实现
   - 开发一个完整的插件
   - 掌握7个扩展点的使用

2. **Flow引擎深入** (1周)
   - 深入学习LiteFlow的使用
   - 开发自定义节点组件
   - 掌握流程编排的高级用法

3. **性能优化** (1周)
   - 学习性能优化技巧
   - 学习SQL优化
   - 学习缓存策略

4. **分布式架构** (2周)
   - 学习分布式事务
   - 学习分布式锁
   - 学习消息队列的使用

### 技能提升计划

1. **技术分享** (每月1次)
   - 进行技术分享
   - 编写技术博客
   - 参与技术社区

2. **代码审查** (每周1次)
   - 参与代码审查
   - 学习优秀代码
   - 提升代码质量

3. **项目实践** (持续)
   - 参与项目开发
   - 承接实际需求
   - 积累项目经验

---

## 七、学习建议

### 学习方法

1. **理论与实践结合**
   - 先学习理论知识
   - 再进行实践操作
   - 最后总结经验

2. **循序渐进**
   - 从简单到复杂
   - 从基础到高级
   - 不要急于求成

3. **多问多思考**
   - 遇到问题及时提问
   - 多思考为什么这样做
   - 不要死记硬背

4. **做好笔记**
   - 记录学习心得
   - 记录遇到的问题
   - 记录解决方案

### 学习心态

1. **保持耐心**
   - 学习是一个过程
   - 不要急于求成
   - 持续学习

2. **保持好奇**
   - 对技术保持好奇
   - 主动探索未知
   - 不断学习新知识

3. **保持自信**
   - 相信自己能学会
   - 不要害怕犯错
   - 从错误中学习

---

## 八、常见问题

### Q1: 学习时间不够怎么办？
A: 可以延长学习周期，每天学习4-6小时，总共需要15-20天。

### Q2: 某个知识点学不会怎么办？
A: 可以先跳过，继续学习其他知识点，等整体学习完成后再回头学习。

### Q3: 如何验证学习效果？
A: 可以通过以下方式验证：
- 进行自我测试
- 进行代码实践
- 进行技术分享
- 参与代码审查

### Q4: 学习完成后如何进一步提升？
A: 可以通过以下方式提升：
- 参与项目开发
- 承接实际需求
- 进行技术分享
- 学习新技术

---

**文档版本**: 1.0  
**最后更新**: 2025-12-30  
**维护者**: Kanten (Claude Code)