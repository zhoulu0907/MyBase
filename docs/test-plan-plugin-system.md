# OneBase v3 插件系统测试计划

**项目:** onebase-v3-be 插件系统
**文档版本:** 1.0
**创建日期:** 2026-01-01
**作者:** Kanten
**测试范围:** MVP 验证型 (5个Epic, 29个功能需求)

---

## 📋 测试概览

### 测试目标

本测试计划覆盖 OneBase v3 插件系统的5个 Epic,验证以下核心能力:

1. **插件框架基础** - ClassLoader隔离和生命周期管理
2. **流程扩展能力** - LiteFlow组件动态注册和调用
3. **插件管理平台** - REST API管理插件生命周期
4. **插件开发工具链** - SDK和脚手架工具
5. **插件元数据与自动发现** - 元数据解析和依赖验证

### 测试策略

- **测试驱动开发 (TDD)**: 优先编写测试,再实现功能
- **单元测试覆盖率目标**: ≥ 80%
- **集成测试覆盖**: 所有关键用户旅程
- **性能测试**: 插件加载、API响应时间
- **安全测试**: 租户隔离、异常隔离

### 测试环境

| 环境 | 用途 | URL |
|------|------|-----|
| local | 本地开发测试 | http://localhost:8080 |
| dev | 开发集成测试 | http://dev.example.com |
| sit | 系统集成测试 | http://sit.example.com |

---

## 🎯 Epic 1: 插件框架基础测试

**Epic目标**: 证明 ClassLoader 隔离和生命周期管理技术可行性
**Sprint**: Sprint 1 (Week 1-2)
**功能需求**: FR1.1, FR1.4, FR6.1, FR6.3, FR6.4

### 1.1 ClassLoader 隔离测试 (FR6.1)

#### 测试用例 TC-E1-001: 插件类加载隔离

**优先级**: P0 (Critical)
**测试类型**: 单元测试
**测试目标**: 验证插件使用独立的 ClassLoader,避免类冲突

**前置条件**:
- OneBase 平台启动
- 准备两个测试插件 JAR (plugin-a.jar, plugin-b.jar)

**测试步骤**:
1. 加载 plugin-a.jar,包含 `com.example.ConflictingClass` 版本 1.0
2. 加载 plugin-b.jar,包含 `com.example.ConflictingClass` 版本 2.0
3. 从 plugin-a 的 ClassLoader 加载 `ConflictingClass`
4. 从 plugin-b 的 ClassLoader 加载 `ConflictingClass`
5. 验证两个类的 ClassLoader 不同
6. 验证两个类的版本不同

**预期结果**:
- 两个插件能同时加载,不抛出 `LinkageError`
- `classA.getClassLoader() != classB.getClassLoader()`
- 两个类的字段/方法版本不同

**清理**: 卸载所有测试插件

---

#### 测试用例 TC-E1-002: 系统类委托加载

**优先级**: P0 (Critical)
**测试类型**: 单元测试
**测试目标**: 验证系统类 (java.*, javax.*, org.springframework.*) 委托给父 ClassLoader

**测试步骤**:
1. 加载测试插件
2. 从插件 ClassLoader 加载 `java.util.ArrayList`
3. 从插件 ClassLoader 加载 `org.springframework.context.ApplicationContext`
4. 验证加载的类与应用主 ClassLoader 加载的是同一个类

**预期结果**:
- 系统类由父 ClassLoader 加载
- `pluginClassLoader.loadClass("java.util.ArrayList") == mainClassLoader.loadClass("java.util.ArrayList")`

---

### 1.2 插件加载测试 (FR1.1)

#### 测试用例 TC-E1-003: 插件 JAR 加载

**优先级**: P0 (Critical)
**测试类型**: 集成测试
**测试目标**: 验证能加载插件 JAR 文件到运行环境
**性能要求**: < 10秒

**前置条件**:
- 准备符合规范的测试插件 JAR (helloworld-plugin.jar)
- JAR 包含正确的 plugin.yml 和主类

**测试步骤**:
1. 调用 `PluginLoader.loadPlugin(jarFile)`
2. 记录加载开始时间和结束时间
3. 验证插件加载成功
4. 验证插件状态为 `LOADED`

**预期结果**:
- 插件成功加载
- 加载时间 < 10秒
- 插件元数据正确解析

---

#### 测试用例 TC-E1-004: 插件 Bean 注册

**优先级**: P0 (Critical)
**测试类型**: 集成测试
**测试目标**: 验证插件中的 @Component/@Service 能注册到插件的 ApplicationContext

**测试步骤**:
1. 加载包含 Spring Bean 的测试插件
2. 获取插件的 ApplicationContext
3. 查询插件中的 Bean
4. 验证 Bean 能正常调用

**预期结果**:
- 插件 Bean 正确注册
- `context.getBean("customService") != null`
- Bean 方法能正常执行

---

### 1.3 插件重启加载测试 (FR1.4)

#### 测试用例 TC-E1-005: 启动时自动加载已安装插件

**优先级**: P0 (Critical)
**测试类型**: 集成测试
**测试目标**: 验证平台重启后自动加载状态为 INSTALLED 的插件
**性能要求**: 启动时间增加 < 20%

**测试步骤**:
1. 安装测试插件,状态设为 INSTALLED
2. 停止 OneBase 平台
3. 记录平台启动时间 (无插件)
4. 重启 OneBase 平台
5. 记录平台启动时间 (有插件)
6. 验证插件自动加载
7. 计算启动时间增加比例

**预期结果**:
- 插件自动加载成功
- 启动时间增加 < 20%
- 插件功能正常

---

### 1.4 异常隔离测试 (FR6.3)

#### 测试用例 TC-E1-006: 插件异常不中断平台

**优先级**: P0 (Critical)
**测试类型**: 集成测试
**测试目标**: 验证插件抛出异常不会导致平台崩溃

**测试步骤**:
1. 加载包含异常代码的测试插件 (故意抛出 RuntimeException)
2. 调用插件的异常方法
3. 验证平台继续运行
4. 验证其他插件正常工作

**预期结果**:
- 插件异常被捕获并记录日志
- OneBase 平台继续运行
- 其他插件功能不受影响

---

### 1.5 内存泄漏防护测试 (FR6.4)

#### 测试用例 TC-E1-007: 插件卸载后内存回收

**优先级**: P0 (Critical)
**测试类型**: 性能测试
**测试目标**: 验证插件卸载后 ClassLoader 能被 GC 回收
**性能要求**: 内存泄漏 < 50MB (可接受)

**测试步骤**:
1. 记录初始堆内存 (M1)
2. 加载 10 个测试插件
3. 记录加载后内存 (M2)
4. 卸载所有插件
5. 调用 `System.gc()`
6. 记录卸载后内存 (M3)
7. 计算内存泄漏: M3 - M1

**预期结果**:
- 内存泄漏 < 50MB
- 插件 ClassLoader 被 GC 回收
- 无 `OutOfMemoryError`

---

#### 测试用例 TC-E1-008: WeakReference 清理机制

**优先级**: P1 (High)
**测试类型**: 单元测试
**测试目标**: 验证使用 WeakReference 管理插件资源

**测试步骤**:
1. 加载测试插件
2. 创建插件的 WeakReference
3. 卸载插件
4. 调用 `System.gc()`
5. 验证 WeakReference.get() == null

**预期结果**:
- WeakReference 被清理
- 插件资源被正确释放

---

## 🎯 Epic 2: 流程扩展能力测试

**Epic目标**: 最终用户在 OneBase 流程中无缝使用插件自定义组件
**Sprint**: Sprint 2 (Week 3-4)
**功能需求**: FR2.1, FR2.2, FR2.3, FR2.4

### 2.1 流程组件扩展点定义测试 (FR2.1)

#### 测试用例 TC-E2-001: FlowComponentExtension 接口实现

**优先级**: P0 (Critical)
**测试类型**: 单元测试
**测试目标**: 验证插件能实现 FlowComponentExtension 接口

**前置条件**:
- 插件 SDK 提供 `FlowComponentExtension` 接口

**测试步骤**:
1. 创建测试插件,实现 `FlowComponentExtension`
2. 实现 `getComponentName()` 返回 "customNode1"
3. 实现 `execute(NodeComponent)` 方法
4. 验证编译成功

**预期结果**:
- 接口实现正确
- 无编译错误

---

### 2.2 流程组件动态注册测试 (FR2.2)

#### 测试用例 TC-E2-002: LiteFlow 组件自动注册

**优先级**: P0 (Critical)
**测试类型**: 集成测试
**测试目标**: 验证插件扩展点自动注册到 LiteFlow

**测试步骤**:
1. 加载包含 FlowComponentExtension 的插件
2. 查询 LiteFlow 的组件注册表
3. 验证自定义组件已注册
4. 验证组件命名格式: `plugin.{pluginId}.{componentName}`

**预期结果**:
- 组件自动注册到 LiteFlow
- 组件名称符合命名规范
- 组件在流程设计器中可见

---

#### 测试用例 TC-E2-003: 命名空间隔离

**优先级**: P0 (Critical)
**测试类型**: 集成测试
**测试目标**: 验证不同插件的组件不会冲突

**测试步骤**:
1. 加载 plugin-a,组件名 "customNode"
2. 加载 plugin-b,组件名 "customNode"
3. 验证两个组件都注册成功
4. 验证组件名称分别为:
   - `plugin.plugin-a.customNode`
   - `plugin.plugin-b.customNode`

**预期结果**:
- 两个组件都能正常注册
- 组件完全隔离,不冲突

---

### 2.3 流程组件运行时调用测试 (FR2.3)

#### 测试用例 TC-E2-004: 流程中调用插件组件

**优先级**: P0 (Critical)
**测试类型**: 集成测试
**测试目标**: 验证 LiteFlow 流程能调用插件组件
**性能要求**: P95 响应时间 < 500ms

**前置条件**:
- 插件组件已注册

**测试步骤**:
1. 创建包含插件组件的 LiteFlow 流程定义
2. 执行流程 100 次
3. 记录每次执行时间
4. 计算 P95 响应时间

**预期结果**:
- 流程成功执行
- 插件组件被正确调用
- P95 响应时间 < 500ms

---

#### 测试用例 TC-E2-005: 组件参数传递

**优先级**: P1 (High)
**测试类型**: 集成测试
**测试目标**: 验证流程数据能正确传递给插件组件

**测试步骤**:
1. 创建流程,向插件组件传递参数
2. 在插件组件中读取参数
3. 修改参数值
4. 验证后续组件能获取修改后的值

**预期结果**:
- 参数正确传递到插件组件
- 插件组件能修改流程数据
- 数据流转正确

---

### 2.4 流程组件故障隔离测试 (FR2.4)

#### 测试用例 TC-E2-006: 组件异常不中断流程

**优先级**: P0 (Critical)
**测试类型**: 集成测试
**测试目标**: 验证插件组件异常不影响流程后续步骤

**测试步骤**:
1. 创建测试插件,组件故意抛出异常
2. 创建流程: step1 → customComponent (异常) → step3
3. 执行流程
4. 验证异常被捕获
5. 验证 step3 继续执行 (如果配置了异常处理)

**预期结果**:
- 异常被记录日志
- 流程不崩溃
- 平台核心功能正常

---

#### 测试用例 TC-E2-007: 组件超时处理

**优先级**: P1 (High)
**测试类型**: 集成测试
**测试目标**: 验证组件执行超时能被正确处理

**测试步骤**:
1. 创建测试插件,组件执行超时 (sleep 10秒)
2. 配置组件超时时间为 3 秒
3. 执行流程
4. 验证超时异常被抛出
5. 验证流程继续执行

**预期结果**:
- 超时异常被正确抛出
- 流程不阻塞

---

## 🎯 Epic 3: 插件管理平台测试

**Epic目标**: 平台管理员通过 REST API 便捷管理插件完整生命周期
**Sprint**: Sprint 3 (Week 5-6)
**功能需求**: FR1.2, FR1.3, FR1.5, FR4.1, FR4.2, FR4.3, FR4.4, FR4.5

### 3.1 插件上传 API 测试 (FR4.1)

#### 测试用例 TC-E3-001: 上传插件 JAR

**优先级**: P0 (Critical)
**测试类型**: API 测试
**测试目标**: 验证 `POST /api/plugin/upload` 上传插件成功

**API规格**:
```
POST /admin-api/plugin/upload
Content-Type: multipart/form-data
```

**测试步骤**:
1. 准备符合规范的插件 JAR 文件
2. 调用上传 API
3. 验证响应状态码 200
4. 验证返回插件 ID
5. 验证 JAR 文件保存到文件系统
6. 验证 `plugin_definition` 表创建记录

**预期结果**:
```json
{
  "code": 0,
  "data": 12345,
  "msg": "success"
}
```

---

#### 测试用例 TC-E3-002: 文件大小限制验证

**优先级**: P1 (High)
**测试类型**: API 测试
**测试目标**: 验证上传文件超过 50MB 被拒绝

**测试步骤**:
1. 准备 > 50MB 的 JAR 文件
2. 调用上传 API
3. 验证响应状态码 400
4. 验证错误消息: "File size exceeds limit (50MB)"

**预期结果**:
- 上传被拒绝
- 返回明确的错误消息

---

#### 测试用例 TC-E3-003: plugin.yml 解析验证

**优先级**: P0 (Critical)
**测试类型**: API 测试
**测试目标**: 验证上传时自动解析 plugin.yml

**测试步骤**:
1. 准备包含 plugin.yml 的插件 JAR
2. 调用上传 API
3. 验证元数据正确解析:
   - plugin_code
   - plugin_name
   - plugin_version
   - author
   - main_class

**预期结果**:
- 元数据 100% 正确解析
- 数据保存到 `plugin_definition` 表

---

### 3.2 插件安装 API 测试 (FR4.2, FR1.2)

#### 测试用例 TC-E3-004: 安装插件到平台级

**优先级**: P0 (Critical)
**测试类型**: API 测试
**测试目标**: 验证 `POST /api/plugin/install` 安装插件成功

**API规格**:
```
POST /admin-api/plugin/install
Content-Type: application/json

{
  "definitionId": 123,
  "installType": 0,  // 0=平台级
  "applicationId": null
}
```

**测试步骤**:
1. 调用安装 API,installType=0
2. 验证响应状态码 200
3. 验证 `plugin_installation` 表创建记录
4. 验证 tenant_id=0 (平台级)
5. 验证状态=INSTALLED

**预期结果**:
- 插件安装成功
- 租户 ID = 0
- 状态正确

---

#### 测试用例 TC-E3-005: 安装插件到租户级

**优先级**: P0 (Critical)
**测试类型**: API 测试
**测试目标**: 验证租户管理员能安装插件到租户级

**前置条件**:
- 切换到租户上下文 (tenant_id=100)

**测试步骤**:
1. 调用安装 API,installType=1
2. 验证 tenant_id=100
3. 验证租户隔离: 其他租户看不到此插件

**预期结果**:
- 插件仅对当前租户可见
- 租户隔离生效

---

### 3.3 插件卸载 API 测试 (FR4.3, FR1.3)

#### 测试用例 TC-E3-006: 卸载插件

**优先级**: P0 (Critical)
**测试类型**: API 测试
**测试目标**: 验证 `DELETE /api/plugin/uninstall/{id}` 卸载插件成功

**API规格**:
```
DELETE /admin-api/plugin/uninstall/123
```

**测试步骤**:
1. 安装测试插件
2. 调用卸载 API
3. 验证响应状态码 200
4. 验证 `plugin_installation` 记录逻辑删除 (deleted=1)
5. 验证插件 Bean 已销毁
6. 验证扩展点已注销

**预期结果**:
- 插件卸载成功
- 资源完全清理

---

### 3.4 插件状态查询测试 (FR4.4, FR1.5)

#### 测试用例 TC-E3-007: 查询插件列表

**优先级**: P0 (Critical)
**测试类型**: API 测试
**测试目标**: 验证 `GET /api/plugin/list` 返回插件列表
**性能要求**: < 500ms

**API规格**:
```
GET /admin-api/plugin/list?pageNo=1&pageSize=10&status=1
```

**测试步骤**:
1. 安装 5 个测试插件
2. 调用列表 API
3. 验证分页参数正确
4. 验证状态过滤生效
5. 记录响应时间

**预期结果**:
```json
{
  "code": 0,
  "data": {
    "list": [
      {
        "id": 123,
        "pluginCode": "my-plugin",
        "pluginName": "我的插件",
        "status": 1
      }
    ],
    "total": 5
  }
}
```
- 响应时间 < 500ms

---

#### 测试用例 TC-E3-008: 查询插件详情

**优先级**: P0 (Critical)
**测试类型**: API 测试
**测试目标**: 验证 `GET /api/plugin/{id}` 返回插件详情
**性能要求**: < 300ms

**API规格**:
```
GET /admin-api/plugin/123
```

**测试步骤**:
1. 调用详情 API
2. 验证返回完整的插件元数据
3. 记录响应时间

**预期结果**:
- 返回完整的插件信息
- 响应时间 < 300ms

---

### 3.5 租户隔离测试

#### 测试用例 TC-E3-009: 租户 A 无法访问租户 B 的插件

**优先级**: P0 (Critical)
**测试类型**: 集成测试
**测试目标**: 验证严格的租户隔离

**测试步骤**:
1. 切换到租户 A (tenant_id=100),安装插件 P1
2. 切换到租户 B (tenant_id=200)
3. 调用 `GET /api/plugin/list`
4. 验证看不到 P1
5. 尝试访问 `GET /api/plugin/{P1.id}`
6. 验证返回 403 或 404

**预期结果**:
- 租户完全隔离
- 跨租户访问被拒绝

---

## 🎯 Epic 4: 插件开发工具链测试

**Epic目标**: ISV 开发者 30 分钟完成 HelloWorld 插件从开发到部署
**Sprint**: Sprint 2-4 (Week 3-8)
**功能需求**: FR3.1, FR3.2, FR3.3, FR3.4, FR5.1

### 4.1 插件 SDK 测试 (FR3.1)

#### 测试用例 TC-E4-001: SDK API 完整性

**优先级**: P0 (Critical)
**测试类型**: 单元测试
**测试目标**: 验证 onebase-plugin-sdk 提供完整的 API

**测试步骤**:
1. 检查 SDK JAR 包结构
2. 验证核心接口存在:
   - `FlowComponentExtension`
   - `DataHookExtension`
   - `ScheduledJobExtension`
3. 验证注解存在:
   - `@ExtensionPoint`
   - `@PluginConfiguration`
4. 验证 Javadoc 覆盖率 100%

**预期结果**:
- 所有 API 接口存在
- Javadoc 完整

---

#### 测试用例 TC-E4-002: SDK 依赖正确性

**优先级**: P1 (High)
**测试类型**: 单元测试
**测试目标**: 验证 SDK 不会引入版本冲突

**测试步骤**:
1. 检查 SDK 的 pom.xml
2. 验证 OneBase 平台依赖使用 `provided` scope
3. 验证无冲突的传递依赖

**预期结果**:
- 依赖配置正确
- 无版本冲突

---

### 4.2 Maven 脚手架测试 (FR3.2)

#### 测试用例 TC-E4-003: 一键生成项目骨架

**优先级**: P0 (Critical)
**测试类型**: 集成测试
**测试目标**: 验证 Maven Archetype 能生成插件项目
**性能要求**: < 30秒

**测试步骤**:
1. 执行 Maven 命令:
```bash
mvn archetype:generate \
  -DarchetypeGroupId=com.cmsr.onebase \
  -DarchetypeArtifactId=onebase-plugin-archetype \
  -DarchetypeVersion=1.0.0 \
  -DgroupId=com.example \
  -DartifactId=my-plugin \
  -Dversion=1.0.0
```
2. 记录生成时间
3. 验证项目结构正确

**预期结果**:
- 项目生成成功
- 生成时间 < 30秒
- 项目结构符合规范

---

#### 测试用例 TC-E4-004: 生成的项目可编译

**优先级**: P0 (Critical)
**测试类型**: 集成测试
**测试目标**: 验证生成的项目能编译打包

**测试步骤**:
1. 进入生成的项目目录
2. 执行 `mvn clean package`
3. 验证编译成功
4. 验证生成 JAR 文件

**预期结果**:
- 编译成功,无错误
- 生成可部署的 JAR

---

### 4.3 HelloWorld 示例测试 (FR3.3)

#### 测试用例 TC-E4-005: 示例插件能运行

**优先级**: P0 (Critical)
**测试类型**: 集成测试
**测试目标**: 验证 HelloWorld 示例插件能加载和运行

**测试步骤**:
1. 上传 HelloWorld 示例插件
2. 安装插件
3. 启用插件
4. 调用插件 API
5. 验证响应正确

**预期结果**:
- 插件运行成功
- 返回预期结果: "Hello from HelloWorld Plugin!"

---

#### 测试用例 TC-E4-006: 示例插件测试覆盖率

**优先级**: P1 (High)
**测试类型**: 单元测试
**测试目标**: 验证示例插件测试覆盖率 ≥ 80%

**测试步骤**:
1. 进入示例插件目录
2. 执行 `mvn test`
3. 查看覆盖率报告
4. 验证覆盖率 ≥ 80%

**预期结果**:
- 测试覆盖率 ≥ 80%
- 所有测试通过

---

### 4.4 本地测试测试 (FR3.4)

#### 测试用例 TC-E4-007: Mock OneBase 环境测试

**优先级**: P1 (High)
**测试类型**: 单元测试
**测试目标**: 验证开发者能在 IDE 中运行单元测试

**测试步骤**:
1. 创建插件单元测试
2. 使用 `@OneBasePluginTest` 注解
3. Mock OneBase 核心组件
4. 在 IDE 中运行测试
5. 验证测试通过

**预期结果**:
- Mock 环境正常工作
- IDE 中测试通过

---

## 🎯 Epic 5: 插件元数据与自动发现测试

**Epic目标**: 系统自动处理元数据解析和依赖验证,减少人工错误
**Sprint**: Sprint 3-4 (Week 5-8)
**功能需求**: FR5.2, FR5.3, FR5.4, FR6.2, FR6.5

### 5.1 元数据解析测试 (FR5.2)

#### 测试用例 TC-E5-001: plugin.yml 自动解析

**优先级**: P0 (Critical)
**测试类型**: 单元测试
**测试目标**: 验证上传时自动解析 plugin.yml

**测试步骤**:
1. 准备包含 plugin.yml 的插件 JAR
2. 上传插件
3. 验证元数据解析正确:
   - plugin_code
   - plugin_name
   - plugin_version
   - author
   - main_class
   - dependencies
   - extensions

**预期结果**:
- 所有元数据正确解析
- 数据保存到数据库

---

#### 测试用例 TC-E5-002: 元数据验证失败处理

**优先级**: P0 (Critical)
**测试类型**: API 测试
**测试目标**: 验证 plugin.yml 格式错误时拒绝上传

**测试步骤**:
1. 准备包含错误 plugin.yml 的插件 JAR:
   - 缺少必需字段 plugin_code
2. 上传插件
3. 验证返回 400 错误
4. 验证错误消息: "Missing required field: plugin_code"

**预期结果**:
- 上传被拒绝
- 错误消息清晰

---

### 5.2 依赖验证测试 (FR5.3)

#### 测试用例 TC-E5-003: 版本兼容性检查

**优先级**: P0 (Critical)
**测试类型**: 单元测试
**测试目标**: 验证插件依赖版本与平台兼容

**测试步骤**:
1. 准备插件,依赖 Spring Boot 2.x (平台是 3.x)
2. 上传插件
3. 验证依赖验证失败
4. 验证错误消息: "Incompatible dependency: Spring Boot 2.x"

**预期结果**:
- 上传被拒绝
- 版本冲突被检测

---

#### 测试用例 TC-E5-004: SemVer 版本比较

**优先级**: P1 (High)
**测试类型**: 单元测试
**测试目标**: 验证使用 SemVer 规范比较版本

**测试步骤**:
1. 测试版本比较: 1.2.3 vs 1.2.4
2. 测试版本比较: 2.0.0 vs 1.9.9
3. 验证结果符合 SemVer 规范

**预期结果**:
- 1.2.4 > 1.2.3
- 2.0.0 > 1.9.9

---

### 5.3 扩展点发现测试 (FR5.4)

#### 测试用例 TC-E5-005: 自动扫描扩展点

**优先级**: P0 (Critical)
**测试类型**: 集成测试
**测试目标**: 验证插件加载时自动扫描和注册扩展点

**测试步骤**:
1. 准备插件,包含多个扩展点:
   - FlowComponentExtension
   - DataHookExtension
2. 加载插件
3. 验证扩展点自动注册
4. 验证 `plugin_extension_point` 表创建记录

**预期结果**:
- 所有扩展点自动注册
- 元数据保存正确

---

#### 测试用例 TC-E5-006: 反射扫描性能

**优先级**: P1 (High)
**测试类型**: 性能测试
**测试目标**: 验证扩展点扫描不影响加载性能

**测试步骤**:
1. 准备包含 100 个类的插件
2. 加载插件
3. 记录扫描时间
4. 验证扫描时间 < 1 秒

**预期结果**:
- 扫描时间 < 1 秒
- 加载性能不受影响

---

### 5.4 命名空间隔离测试 (FR6.2)

#### 测试用例 TC-E5-007: Bean ID 命名格式

**优先级**: P0 (Critical)
**测试类型**: 集成测试
**测试目标**: 验证插件 Bean ID 格式: `plugin.{pluginId}.{beanName}`

**测试步骤**:
1. 加载插件 plugin-a,包含 Bean "customService"
2. 查询 Spring 容器中的 Bean 名称
3. 验证 Bean ID 为: `plugin.plugin-a.customService`

**预期结果**:
- Bean ID 符合命名规范
- 不同插件的同名 Bean 不冲突

---

### 5.5 平台级作用域测试 (FR6.5)

#### 测试用例 TC-E5-008: 平台级插件全局可见

**优先级**: P0 (Critical)
**测试类型**: 集成测试
**测试目标**: 验证平台级插件 (tenant_id=0) 对所有租户可见

**测试步骤**:
1. 切换到租户 A (tenant_id=100)
2. 查询插件列表
3. 验证能看到平台级插件
4. 切换到租户 B (tenant_id=200)
5. 查询插件列表
6. 验证能看到平台级插件

**预期结果**:
- 平台级插件对所有租户可见
- 所有租户都能使用

---

## 📊 测试覆盖率目标

### 单元测试覆盖率

| 模块 | 覆盖率目标 | 测试类数估算 |
|------|-----------|------------|
| PluginClassLoader | ≥ 90% | 8 |
| PluginApplicationContext | ≥ 85% | 6 |
| PluginLoader | ≥ 85% | 10 |
| ExtensionPointRegistry | ≥ 80% | 7 |
| PluginService | ≥ 80% | 12 |
| MetadataParser | ≥ 85% | 8 |
| DependencyValidator | ≥ 80% | 5 |
| **总计** | **≥ 80%** | **56+** |

### 集成测试场景

| Epic | 关键场景数 |
|------|----------|
| Epic 1 | 8 |
| Epic 2 | 7 |
| Epic 3 | 9 |
| Epic 4 | 7 |
| Epic 5 | 8 |
| **总计** | **39** |

### API 测试覆盖

| API 端点 | 测试用例数 |
|---------|----------|
| POST /api/plugin/upload | 3 |
| POST /api/plugin/install | 2 |
| DELETE /api/plugin/uninstall/{id} | 1 |
| GET /api/plugin/list | 2 |
| GET /api/plugin/{id} | 1 |
| POST /api/plugin/enable/{id} | 1 |
| POST /api/plugin/disable/{id} | 1 |
| **总计** | **11** |

---

## ⚡ 性能测试基准

### 关键性能指标 (KPI)

| 指标 | 目标值 | 测试方法 |
|------|--------|---------|
| 插件加载时间 | < 10 秒 | TC-E1-003 |
| 插件卸载时间 | < 2 秒 | TC-E1-007 |
| 平台启动时间增加 | < 20% | TC-E1-005 |
| 插件 API P95 响应时间 | < 500ms | TC-E2-004 |
| 插件列表查询 | < 500ms | TC-E3-007 |
| 插件详情查询 | < 300ms | TC-E3-008 |
| Maven 项目生成 | < 30 秒 | TC-E4-003 |
| 内存泄漏 (单插件) | < 50MB | TC-E1-007 |
| 并发插件数 | ≥ 50 | 压力测试 |

### 压力测试场景

#### 场景 1: 多插件并发加载

**测试步骤**:
1. 准备 50 个测试插件
2. 并发加载所有插件
3. 验证加载成功率 100%
4. 记录总加载时间
5. 验证内存使用正常

**预期结果**:
- 所有插件加载成功
- 无 `OutOfMemoryError`
- 总加载时间 < 5 分钟

---

#### 场景 2: 高频 API 调用

**测试步骤**:
1. 安装测试插件
2. 使用 JMeter 并发调用插件 API
3. 并发用户: 100
4. 持续时间: 10 分钟
5. 验证成功率 ≥ 99.9%
6. 验证 P95 响应时间 < 500ms

**预期结果**:
- API 稳定运行
- 无性能降级

---

## 🔒 安全测试用例

### 租户隔离验证

| 测试用例 | 描述 |
|---------|------|
| TC-SEC-001 | 租户 A 无法查询租户 B 的插件 |
| TC-SEC-002 | 租户 A 无法启用/禁用租户 B 的插件 |
| TC-SEC-003 | 租户 A 无法卸载租户 B 的插件 |
| TC-SEC-004 | 平台管理员能看到所有租户的插件 |
| TC-SEC-005 | 租户级插件数据严格隔离 |

### 异常隔离验证

| 测试用例 | 描述 |
|---------|------|
| TC-SEC-006 | 插件抛出 `OutOfMemoryError` 不影响平台 |
| TC-SEC-007 | 插件抛出 `StackOverflowError` 不影响平台 |
| TC-SEC-008 | 插件死线程不影响平台 |
| TC-SEC-009 | 插件恶意删除文件被阻止 |

---

## 📦 测试数据管理

### 测试插件清单

| 插件名 | 用途 | 测试场景 |
|-------|------|---------|
| helloworld-plugin.jar | 基础加载测试 | TC-E1-003, TC-E4-005 |
| class-conflict-a.jar | 类冲突测试 | TC-E1-001 |
| class-conflict-b.jar | 类冲突测试 | TC-E1-001 |
| exception-plugin.jar | 异常隔离测试 | TC-E1-006, TC-E2-006 |
| flow-extension-plugin.jar | 流程扩展测试 | TC-E2-002, TC-E2-004 |
| leak-plugin.jar | 内存泄漏测试 | TC-E1-007 |
| invalid-metadata-plugin.jar | 元数据验证测试 | TC-E5-002 |

### 测试数据准备

**数据库测试数据**:
```sql
-- 测试租户
INSERT INTO sys_tenant (id, name) VALUES (100, '测试租户A');
INSERT INTO sys_tenant (id, name) VALUES (200, '测试租户B');

-- 测试用户
INSERT INTO sys_user (id, username, tenant_id) VALUES (1001, 'admin', 0);
INSERT INTO sys_user (id, username, tenant_id) VALUES (1002, 'user_a', 100);
INSERT INTO sys_user (id, username, tenant_id) VALUES (1003, 'user_b', 200);
```

---

## 🛠️ 测试工具与环境

### 单元测试框架

- **JUnit 5** - 测试运行器
- **Mockito** - Mock 框架
- **AssertJ** - 断言库
- **JaCoCo** - 覆盖率工具

### 集成测试工具

- **RestAssured** - API 测试
- **TestContainers** - 数据库集成测试
- **WireMock** - 服务 Mock

### 性能测试工具

- **JMeter** - API 压力测试
- **JProfiler** - 内存分析
- **VisualVM** - JVM 监控

### 测试环境配置

**application-test.yml**:
```yaml
onebase:
  plugin:
    enabled: true
    upload-path: /tmp/test-plugins
    max-file-size: 50MB

spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver

logging:
  level:
    com.cmsr.onebase.module.plugin: DEBUG
```

---

## ✅ 测试执行计划

### Sprint 1 测试 (Week 1-2)

**重点**: Epic 1 - 插件框架基础

| 测试套件 | 用例数 | 执行时间 |
|---------|--------|---------|
| ClassLoader 隔离测试 | 2 | Day 1 |
| 插件加载测试 | 2 | Day 2 |
| 插件重启加载测试 | 1 | Day 3 |
| 异常隔离测试 | 1 | Day 3 |
| 内存泄漏防护测试 | 2 | Day 4-5 |

### Sprint 2 测试 (Week 3-4)

**重点**: Epic 2 - 流程扩展能力 + Epic 4 部分

| 测试套件 | 用例数 | 执行时间 |
|---------|--------|---------|
| 流程组件扩展点测试 | 1 | Day 1 |
| 流程组件动态注册测试 | 2 | Day 1-2 |
| 流程组件运行时测试 | 2 | Day 2-3 |
| 流程组件故障隔离测试 | 2 | Day 3-4 |
| SDK 测试 | 2 | Day 5 |
| HelloWorld 示例测试 | 2 | Day 5 |

### Sprint 3 测试 (Week 5-6)

**重点**: Epic 3 - 插件管理平台 + Epic 5 部分

| 测试套件 | 用例数 | 执行时间 |
|---------|--------|---------|
| 插件上传 API 测试 | 3 | Day 1-2 |
| 插件安装 API 测试 | 2 | Day 2 |
| 插件卸载 API 测试 | 1 | Day 3 |
| 插件状态查询测试 | 2 | Day 3-4 |
| 租户隔离测试 | 1 | Day 4 |
| 元数据解析测试 | 2 | Day 5 |
| 依赖验证测试 | 2 | Day 5-6 |

### Sprint 4 测试 (Week 7-8)

**重点**: Epic 4 剩余 + Epic 5 剩余 + 回归测试

| 测试套件 | 用例数 | 执行时间 |
|---------|--------|---------|
| Maven 脚手架测试 | 2 | Day 1 |
| 本地测试测试 | 1 | Day 1 |
| 扩展点发现测试 | 2 | Day 2 |
| 命名空间隔离测试 | 1 | Day 2 |
| 平台级作用域测试 | 1 | Day 3 |
| 完整回归测试 | 39 | Day 4-8 |

---

## 📝 测试报告模板

### 日报模板

```markdown
## 测试日报 - YYYY-MM-DD

**测试人员**: xxx
**测试 Epic**: Epic X - XXX

### 今日完成
- [x] TC-EXXX-001: XXX 测试 - PASS
- [x] TC-EXXX-002: XXX 测试 - FAIL

### 发现的 Bug
| Bug ID | 标题 | 严重程度 | 状态 |
|--------|------|---------|------|
| BUG-001 | 插件加载时 NPE | P0 | OPEN |

### 明日计划
- [ ] TC-EXXX-003: XXX 测试
- [ ] BUG-001 复测

### 阻塞问题
- 无
```

### Epic 测试总结报告模板

```markdown
## Epic X 测试总结报告

**Epic 名称**: XXX
**测试周期**: YYYY-MM-DD ~ YYYY-MM-DD
**测试人员**: xxx

### 测试执行情况
- 计划用例数: X
- 执行用例数: Y
- 通过用例数: Z
- 失败用例数: W
- 用例通过率: Z/Y * 100%

### Bug 统计
| 严重程度 | 数量 |
|---------|------|
| P0 | 0 |
| P1 | 2 |
| P2 | 5 |
| P3 | 10 |

### 测试覆盖
- 单元测试覆盖率: XX%
- 集成测试覆盖: YY%
- API 测试覆盖: ZZ%

### 风险与建议
- 风险: XXX
- 建议: YYY

### 结论
[ ] 通过 - 可以发布
[ ] 有条件通过 - 需修复 P0/P1 Bug
[ ] 不通过 - 需要重新测试
```

---

## 🎯 验收标准

### Epic 验收标准

每个 Epic 必须满足以下条件才能通过验收:

1. **功能完整性**
   - [ ] 所有 P0 测试用例 100% 通过
   - [ ] P1 测试用例通过率 ≥ 95%
   - [ ] P2/P3 测试用例通过率 ≥ 80%

2. **质量指标**
   - [ ] 单元测试覆盖率 ≥ 80%
   - [ ] 无 P0/P1 级别的 Bug
   - [ ] P2 Bug ≤ 5 个

3. **性能指标**
   - [ ] 所有性能测试用例通过
   - [ ] 无内存泄漏
   - [ ] 无性能降级

4. **安全指标**
   - [ ] 所有安全测试用例通过
   - [ ] 租户隔离 100% 生效
   - [ ] 异常隔离验证通过

### MVP 验收标准

整个 MVP 必须满足以下条件才能发布:

1. **5 个 Epic 全部通过验收**
2. **端到端测试通过**
   - [ ] ISV 开发者能使用 SDK 开发 HelloWorld 插件 (30 分钟)
   - [ ] 平台管理员能通过 API 管理插件生命周期
   - [ ] 最终用户能在流程中使用插件组件

3. **非功能需求满足**
   - [ ] 插件加载时间 < 10 秒
   - [ ] 支持 ≥ 50 个插件同时运行
   - [ ] 内存泄漏 < 50MB
   - [ ] 租户隔离 100%

---

**文档版本**: 1.0
**最后更新**: 2026-01-01
**维护者**: Kanten
**审核状态**: 待审核

---

## 附录

### A. 测试用例编号规范

- **TC-EXXX-NNN**:
  - E = Epic 编号 (1-5)
  - NNN = 用例序号 (001-999)

例如:
- TC-E1-001: Epic 1 的第 1 个测试用例
- TC-E3-015: Epic 3 的第 15 个测试用例

### B. 优先级定义

- **P0 (Critical)**: 阻塞发布,必须修复
- **P1 (High)**: 严重影响,优先修复
- **P2 (Medium)**: 一般问题,计划修复
- **P3 (Low)**: 轻微问题,可选修复

### C. 测试环境准备指南

参见: `docs/test-environment-setup.md` (待创建)
