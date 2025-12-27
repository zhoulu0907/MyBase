---
description: OneBase V3 项目上下文和开发规范
---

# OneBase V3 Backend - 项目上下文及交互偏好

## 项目概述
- **项目名称**: OneBase V3 Backend
- **核心功能**: 企业级低代码平台后端系统
- **技术栈**: Spring Boot 3.x, Java 17, Maven, PostgreSQL, Redis
- **开发环境**: Windows, IntelliJ IDEA
- **项目定位**: 提供灵活、可扩展的低代码平台基础设施

## AI 交互偏好

### 语言设置
- **响应语言**: 中文（简体中文）
- **代码注释**: 中文
- **文档说明**: 中文
- **异常信息**: 保持英文原文，但提供中文解释

### 交互风格
- **技术深度**: 提供详细的技术说明和原理解释
- **代码示例**: 包含完整的、可运行的代码示例
- **最佳实践**: 主动提供相关的最佳实践建议
- **问题排查**: 提供系统化的排查步骤和解决方案

## 项目架构

### 整体架构
```
onebase-v3-be/
├── onebase-dependencies/          # 依赖版本统一管理
├── onebase-framework/             # 核心框架层
│   ├── 通用工具类
│   ├── 基础设施组件
│   └── 框架扩展
├── onebase-plugin/                # 插件系统
│   └── 支持动态扩展和热加载
├── onebase-module-*/              # 业务模块层
│   ├── onebase-module-system      # 系统管理
│   ├── onebase-module-data        # 数据管理
│   ├── onebase-module-bpm         # 工作流引擎
│   ├── onebase-module-ai          # AI 能力
│   ├── onebase-module-app         # 应用管理
│   ├── onebase-module-flow        # 流程编排
│   ├── onebase-module-formula     # 公式引擎
│   ├── onebase-module-dashboard   # 仪表盘
│   └── onebase-module-infra       # 基础设施
└── onebase-server*/               # 服务器层
    ├── onebase-server             # 编辑态服务
    ├── onebase-server-platform    # 平台服务
    ├── onebase-server-runtime     # 运行态服务
    └── onebase-server-flink       # 流处理服务
```

### 模块职责

#### 框架层 (onebase-framework)
- 提供通用工具类和基础组件
- 统一异常处理和日志框架
- 数据库访问和缓存抽象
- 安全认证和权限控制

#### 业务模块层 (onebase-module-*)
- 各业务领域的独立模块
- 遵循领域驱动设计 (DDD)
- 模块间通过接口解耦
- 支持独立开发和测试

#### 服务器层 (onebase-server-*)
- 应用启动和配置
- 模块组装和编排
- 对外提供 REST API
- 处理跨模块协调

## 编码规范

### Java 代码风格
- **包命名**: `com.cmsr.onebase.<module>.<submodule>`
- **类命名**: 大驼峰命名法 (PascalCase)
  - Controller: `*Controller`
  - Service: `*Service` / `*ServiceImpl`
  - Repository: `*Repository` / `*Mapper`
  - DTO: `*DTO` / `*VO` / `*Request` / `*Response`
- **方法命名**: 小驼峰命名法 (camelCase)
  - 查询: `get*` / `find*` / `query*` / `list*`
  - 修改: `update*` / `modify*` / `set*`
  - 删除: `delete*` / `remove*`
  - 创建: `create*` / `add*` / `save*`
- **常量命名**: 全大写下划线分隔 (UPPER_SNAKE_CASE)
- **缩进**: 4 个空格
- **编码**: UTF-8 (已配置 `lombok.config`)

### 分层架构规范
```
Controller 层  → 处理 HTTP 请求，参数验证
    ↓
Service 层     → 业务逻辑，事务控制
    ↓
Repository 层  → 数据访问，SQL 操作
```

**规则**:
- Controller 不直接调用 Repository
- Service 层方法使用 `@Transactional` 控制事务
- 跨模块调用通过接口，避免直接依赖

### 测试规范
- **测试类命名**: `*Test.java`
- **测试方法命名**: `test<功能描述>` 或使用 `@DisplayName`
- **测试位置**: `src/test/java` 对应包路径
- **测试覆盖率**: 核心业务逻辑分支覆盖率 > 85%
- **测试隔离**: 使用 `@DirtiesContext` 或 `@Transactional(rollback=true)`

### 日志规范
- **日志框架**: SLF4J + Logback
- **日志级别**:
  - `DEBUG`: 详细的调试信息（仅开发环境）
  - `INFO`: 重要的业务流程节点
  - `WARN`: 潜在问题，但不影响运行
  - `ERROR`: 错误异常，需要关注
- **日志格式**: `[模块名] 操作描述 - 详细信息`
- **敏感信息**: 不记录密码、token 等敏感数据

### API 设计规范
- **RESTful 风格**: 使用标准 HTTP 方法
  - `GET`: 查询资源
  - `POST`: 创建资源
  - `PUT`: 完整更新资源
  - `PATCH`: 部分更新资源
  - `DELETE`: 删除资源
- **URL 命名**: 小写字母，单词用连字符分隔
  - ✅ `/api/user-profiles`
  - ❌ `/api/userProfiles`
- **响应格式**: 统一使用 JSON
- **错误处理**: 统一的错误响应结构

## 开发原则

### 通用开发原则
1. **单一职责**: 每个类/方法只做一件事
2. **开闭原则**: 对扩展开放，对修改关闭
3. **依赖倒置**: 依赖抽象而非具体实现
4. **最小知识**: 减少模块间的耦合
5. **DRY 原则**: 不重复代码，提取公共逻辑
6. **KISS 原则**: Keep It Simple, Stupid
7. **YAGNI 原则**: 避免无用的功能

### 性能优化原则
- 合理使用缓存（Redis）
- 避免 N+1 查询问题
- 使用分页查询大数据集
- 异步处理耗时操作
- 数据库索引优化

### 安全原则
- 所有输入必须验证
- 防止 SQL 注入（使用参数化查询）
- 防止 XSS 攻击（输出转义）
- 敏感操作需要权限验证
- 使用 HTTPS 传输敏感数据

## 开发工具配置

### Maven
- **编码**: `-Dfile.encoding=UTF-8`
- **Java 版本**: Java 17
- **构建命令**: `mvn clean install -DskipTests`
- **依赖管理**: 所有版本在 `onebase-dependencies` 中统一管理

### IDE 配置
- **文件编码**: UTF-8
- **Lombok**: 已启用 (`lombok.config`)
- **代码格式化**: 遵循项目规范
- **自动导入**: 优化 import 顺序

### Git 配置
- **换行符**: LF (Unix 风格)
- **忽略文件**: 参考 `.gitignore`
- **提交信息**: 清晰描述变更内容

### 代码审查
- 关注代码质量和规范
- 确保测试覆盖
- 验证功能正确性
- 检查性能和安全问题
- 提供建设性反馈

## 跨模块协作

### 模块依赖原则
- 业务模块不直接依赖其他业务模块
- 通过接口和事件进行模块间通信
- 共享代码放在 `onebase-framework`
- 避免循环依赖

### 接口设计
- 定义清晰的接口契约
- 使用 DTO 传递数据
- 版本化 API 接口
- 向后兼容

## 参考文档
- 项目 README: `/README.md`
- 文档目录: `/docs/`
- 各模块文档: `<module>/README.md`
