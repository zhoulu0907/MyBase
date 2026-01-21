# Tech-Spec: OneBase 自开发管理模块（插件系统）

**Created:** 2025-12-06
**Status:** Ready for Development
**Project:** OneBase v3.0 Backend
**Module:** onebase-module-plugin

---

## 📋 Overview

### Problem Statement

OneBase 低代码平台通过可视化组件和配置化方式帮助用户快速构建业务应用,但在实际使用中存在以下痛点:

1. **功能覆盖不完整** - 低代码平台的标准组件无法覆盖所有业务场景
2. **特殊业务需求** - 某些业务逻辑需要与外部系统集成(如ERP、第三方API)
3. **定制化开发困难** - 当前平台是封闭的,缺乏扩展机制
4. **ISV生态受限** - 第三方独立软件开发商(ISV)无法为平台贡献功能扩展
5. **业务迭代缓慢** - 每个新需求都需要平台核心团队开发,响应周期长

**典型场景:**
- 企业需要对接特定的ERP系统进行数据同步
- 业务流程中需要执行复杂的数据转换逻辑
- 需要集成第三方支付、消息推送等服务
- 需要实现特定行业的业务规则和计算逻辑

### Solution

构建一个 **自开发管理模块(Plugin System)**,允许开发者通过标准化的Spring Boot插件包扩展平台能力:

**核心能力:**
1. **插件开发SDK** - 提供标准化的插件开发脚手架和API
2. **插件生命周期管理** - 上传、安装、启用、版本管理、热部署、卸载
3. **多扩展点支持** - 流程、数据钩子、API、定时任务、事件、公式、UI
4. **多租户隔离** - 租户级插件严格隔离,平台级插件全局共享
5. **热部署能力** - 插件加载/卸载无需重启服务器(关键需求)
6. **安全可控** - 插件注册、审核、监控、日志

**技术方案:**
- **Phase 1 (MVP):** Spring Boot + 自定义ClassLoader + GenericApplicationContext
- **Phase 2 (可选):** 根据实际使用情况评估是否迁移到OSGi

### Scope

#### ✅ In Scope (第一期实现)

**核心框架:**
- [ ] 插件加载引擎(ClassLoader + ApplicationContext)
- [ ] 插件生命周期管理(上传/安装/启用/卸载)
- [ ] 热部署机制(不重启服务器)
- [ ] 租户级和平台级插件隔离

**扩展点实现(分阶段):**
- [ ] 流程扩展(LiteFlow组件 + Flowable服务任务)
- [ ] API扩展(自定义RestController)
- [ ] 数据钩子(beforeSave/afterSave)
- [ ] 定时任务(Cron表达式)
- [ ] 事件监听(系统事件 + 业务事件)
- [ ] 公式函数扩展
- [ ] UI扩展(前端组件注册)

**管理能力:**
- [ ] 插件上传(Web界面 + Maven私服拉取)
- [ ] 插件安装配置
- [ ] 版本管理(多版本共存、升级、回滚)
- [ ] 插件状态监控
- [ ] 插件日志查看

**开发者工具:**
- [ ] onebase-plugin-sdk开发包
- [ ] 插件开发脚手架(Maven Archetype)
- [ ] 本地调试工具
- [ ] 开发文档和示例

#### ❌ Out of Scope (第一期不包含)

- ❌ 跨租户插件授权共享
- ❌ 插件级别的权限控制(字段权限、操作权限)
- ❌ 插件沙箱隔离(JVM安全管理器、资源限制)
- ❌ 插件市场(插件商店、评分、评论)
- ❌ 灰度发布(A/B测试、流量分配)
- ❌ 插件费用计费
- ❌ OSGi框架(Phase 2再评估)

---

## 🔍 Context for Development

### Codebase Patterns

#### 1. 模块化架构

OneBase v3.0 采用Maven多模块架构,新插件模块应遵循相同模式:

```
onebase-module-plugin/
├── onebase-module-plugin-api/          # 插件API接口(供外部调用)
├── onebase-module-plugin-core/         # 插件核心逻辑
├── onebase-module-plugin-sdk/          # 插件开发SDK(供开发者使用)
├── onebase-module-plugin-build/        # 插件管理后台
└── onebase-module-plugin-runtime/      # 插件运行时引擎
```

**参考现有模块:**
- `onebase-module-flow` - 流程编排模块
- `onebase-module-metadata` - 元数据模块
- `onebase-module-bpm` - BPM工作流模块

#### 2. 多租户模式

**关键约定:**
- 所有业务表必须包含 `tenant_id` 字段
- 使用 MyBatis-Flex 租户插件自动注入租户条件
- 平台级数据使用 `tenant_id = 0`
- 租户级数据使用 `tenant_id > 0`

**参考文件:**
- `onebase-framework-tenant` - 租户框架实现
- `com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore` - 租户忽略注解

#### 3. Spring Boot集成模式

**自动配置约定:**
```java
// 参考: onebase-module-flow-core/src/main/resources/META-INF/spring.factories
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.cmsr.onebase.module.plugin.config.PluginAutoConfiguration
```

**Bean注册模式:**
```java
@Configuration
@ConditionalOnProperty(prefix = "onebase.plugin", name = "enabled", havingValue = "true", matchIfMissing = true)
public class PluginAutoConfiguration {
    // 插件核心Bean注册
}
```

#### 4. 数据库设计模式

**通用字段约定:**
```sql
-- 所有业务表标准字段
id BIGINT PRIMARY KEY,
creator BIGINT,
create_time TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
updater BIGINT,
update_time TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
deleted SMALLINT DEFAULT 0,  -- 逻辑删除
tenant_id BIGINT,  -- 租户ID
lock_version BIGINT DEFAULT 0  -- 乐观锁
```

**参考:** `docs/data-models-backend.md`

#### 5. API设计模式

**RESTful API约定:**
```java
@RestController
@RequestMapping("/admin-api/plugin")
@Tag(name = "管理后台 - 插件管理")
public class PluginController {

    @PostMapping("/upload")
    @Operation(summary = "上传插件")
    public CommonResult<Long> uploadPlugin(@RequestBody PluginUploadReqVO reqVO) {
        // ...
    }
}
```

**参考:** `docs/api-contracts-backend.md`

### Files to Reference

#### 核心集成参考

**1. 流程扩展集成:**
```
onebase-module-flow/onebase-module-flow-core/
├── src/main/java/com/cmsr/onebase/module/flow/core/graph/FlowGraphBuilder.java
└── src/main/java/com/cmsr/onebase/module/flow/component/utils/FieldTypeProviderImpl.java
```

**2. BPM工作流集成:**
```
onebase-module-bpm/onebase-module-bpm-core/
└── src/main/java/com/cmsr/onebase/module/bpm/service/
```

**3. 元数据钩子集成:**
```
onebase-module-metadata/onebase-module-metadata-core/
└── src/main/java/com/cmsr/onebase/module/metadata/service/
```

**4. 租户隔离实现:**
```
onebase-framework/onebase-framework-tenant/
├── src/main/java/com/cmsr/onebase/framework/tenant/core/aop/TenantIgnore.java
└── src/main/java/com/cmsr/onebase/framework/tenant/core/context/TenantContextHolder.java
```

**5. 文件上传服务:**
```
onebase-module-infra/onebase-module-infra-core/
└── src/main/java/com/cmsr/onebase/module/infra/service/file/
```

**6. 定时任务框架:**
```
onebase-module-infra/onebase-module-infra-core/
└── src/main/java/com/cmsr/onebase/module/infra/service/job/
```

#### 配置参考

**应用配置:**
```
onebase-server/src/main/resources/
├── application.yaml  # 主配置
└── application-local.yaml  # 本地环境配置
```

**数据库DDL:**
```
sql/postgresql/onsbase_v3_be_ddl.sql
```

### Technical Decisions

#### 决策1: 热部署技术方案

**选择:** Spring Boot + 自定义ClassLoader (Phase 1)

**理由:**
- ✅ 快速实现(4-6周)
- ✅ 团队熟悉Spring Boot生态
- ✅ 学习曲线低
- ✅ 务实且高性价比
- ⚠️ 依赖隔离能力有限(可接受)

**替代方案(Phase 2可选):**
- OSGi (Apache Felix/Eclipse Equinox) - 当插件数量多、依赖冲突严重时考虑

**实现要点:**
```java
public class PluginClassLoader extends URLClassLoader {
    private final ClassLoader parent;

    public PluginClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, null);  // 破坏双亲委派,优先加载插件类
        this.parent = parent;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) {
        // 1. 已加载检查
        // 2. 插件类优先
        // 3. 系统类委托给parent
        // 4. 未找到抛异常
    }
}
```

#### 决策2: 租户隔离策略

**选择:** 严格租户隔离 + 平台级插件

**隔离级别:**
- **租户级插件:** `tenant_id > 0`,租户A的插件对租户B完全不可见
- **平台级插件:** `tenant_id = 0`,管理员安装,所有租户可用

**不支持(第一期):**
- ❌ 跨租户插件授权共享(租户A不能授权插件给租户B使用)

**数据隔离实现:**
```sql
-- 插件安装表
SELECT * FROM plugin_installation
WHERE tenant_id = #{currentTenantId} AND deleted = 0;

-- 平台级插件(所有租户可用)
SELECT * FROM plugin_installation
WHERE tenant_id = 0 AND deleted = 0;
```

#### 决策3: 插件生命周期管理

**选择:** 全生命周期管理(A+B+C+D+E)

**阶段:**
1. **上传与注册** - Web上传/Maven私服拉取/API上传
2. **安装与启用** - 租户级/平台级安装,启用开关
3. **版本管理** - 多版本共存,升级/回滚
4. **运行时管理** - 热部署,状态监控,日志查看
5. **卸载与清理** - 卸载插件,清理数据

**关键需求: 热部署(非常重要)**
- 插件加载/卸载不重启服务器
- 使用 GenericApplicationContext 管理插件Bean
- 插件卸载时销毁ApplicationContext

#### 决策4: 扩展点设计

**选择:** 7个扩展点,分阶段实现

**优先级排序:**
1. **P0 (核心):**
   - 流程扩展(LiteFlow + Flowable) - 最高优先级
   - API扩展(自定义Controller) - 最高优先级
2. **P1 (重要):**
   - 数据钩子(beforeSave/afterSave)
   - 定时任务(Cron)
3. **P2 (次要):**
   - 事件监听
   - 公式函数
   - UI扩展

**扩展点接口设计:**
```java
// 流程扩展点
public interface FlowExtensionPoint {
    String getComponentName();  // LiteFlow组件名称
    void execute(NodeComponent component);
}

// API扩展点
@RestController
public class PluginApiController {
    // 插件注册的自定义API端点
}

// 数据钩子扩展点
public interface DataHookExtensionPoint {
    void beforeSave(DataHookContext context);
    void afterSave(DataHookContext context);
}
```

#### 决策5: 模块架构

**选择:** 独立新模块 `onebase-module-plugin`

**子模块职责:**
- **onebase-module-plugin-api:** 对外API接口(供其他模块调用)
- **onebase-module-plugin-core:** 核心逻辑(生命周期管理、热部署引擎)
- **onebase-module-plugin-sdk:** 开发SDK(供插件开发者使用)
- **onebase-module-plugin-build:** 管理后台(上传、安装、监控)
- **onebase-module-plugin-runtime:** 运行时引擎(ClassLoader、扩展点注册)

**集成关系:**
```
onebase-module-plugin (新模块)
├── 依赖 → onebase-module-flow (流程扩展)
├── 依赖 → onebase-module-bpm (BPM扩展)
├── 依赖 → onebase-module-metadata (数据钩子)
├── 依赖 → onebase-module-formula (公式函数)
├── 依赖 → onebase-module-app (UI组件)
├── 依赖 → onebase-module-system (租户隔离)
└── 依赖 → onebase-module-infra (文件上传、定时任务)
```

---

## 🚀 Implementation Plan

### Phase 1: 核心框架 (4-6周)

**目标:** 构建插件加载引擎和基础生命周期管理能力

#### Task 1.1: 创建模块结构

**目标:** 搭建 `onebase-module-plugin` 模块骨架

**步骤:**
```bash
# 创建模块目录
mkdir -p onebase-module-plugin/onebase-module-plugin-api/src/main/java
mkdir -p onebase-module-plugin/onebase-module-plugin-core/src/main/java
mkdir -p onebase-module-plugin/onebase-module-plugin-sdk/src/main/java
mkdir -p onebase-module-plugin/onebase-module-plugin-build/src/main/java
mkdir -p onebase-module-plugin/onebase-module-plugin-runtime/src/main/java

# 创建pom.xml文件
```

**验收标准:**
- [ ] 5个子模块创建完成
- [ ] Maven依赖关系配置正确
- [ ] `mvn clean install` 编译成功

#### Task 1.2: 设计数据库Schema

**目标:** 创建插件管理的5张核心表

**DDL:**
```sql
-- 1. 插件定义表
CREATE TABLE plugin_definition (
    id BIGINT PRIMARY KEY,
    plugin_code VARCHAR(64) NOT NULL,  -- 插件编码(唯一)
    plugin_name VARCHAR(128) NOT NULL,  -- 插件名称
    plugin_version VARCHAR(32) NOT NULL,  -- 插件版本
    description TEXT,  -- 插件描述
    author VARCHAR(64),  -- 作者
    homepage_url VARCHAR(256),  -- 主页URL
    main_class VARCHAR(256) NOT NULL,  -- 插件主类
    enabled BOOLEAN DEFAULT TRUE,  -- 是否启用
    creator BIGINT,
    create_time TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    updater BIGINT,
    update_time TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT DEFAULT 0,
    tenant_id BIGINT,  -- 租户ID(0=平台级)
    lock_version BIGINT DEFAULT 0,
    UNIQUE (plugin_code, plugin_version, tenant_id, deleted)
);

-- 2. 插件包表
CREATE TABLE plugin_package (
    id BIGINT PRIMARY KEY,
    definition_id BIGINT NOT NULL,  -- 关联plugin_definition.id
    file_id BIGINT,  -- 关联infra_file.id
    file_name VARCHAR(256),  -- JAR文件名
    file_size BIGINT,  -- 文件大小(字节)
    file_md5 VARCHAR(32),  -- MD5校验
    maven_group_id VARCHAR(128),  -- Maven GroupId
    maven_artifact_id VARCHAR(128),  -- Maven ArtifactId
    maven_version VARCHAR(64),  -- Maven版本
    creator BIGINT,
    create_time TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    updater BIGINT,
    update_time TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT DEFAULT 0,
    tenant_id BIGINT,
    lock_version BIGINT DEFAULT 0
);

-- 3. 插件安装表
CREATE TABLE plugin_installation (
    id BIGINT PRIMARY KEY,
    definition_id BIGINT NOT NULL,  -- 插件定义ID
    package_id BIGINT NOT NULL,  -- 插件包ID
    install_type SMALLINT NOT NULL,  -- 安装类型(0=平台级,1=租户级,2=应用级)
    application_id BIGINT,  -- 应用ID(仅应用级安装)
    status SMALLINT NOT NULL,  -- 状态(0=已安装,1=已启用,2=已禁用)
    installed_at TIMESTAMP(6),  -- 安装时间
    enabled_at TIMESTAMP(6),  -- 启用时间
    creator BIGINT,
    create_time TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    updater BIGINT,
    update_time TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT DEFAULT 0,
    tenant_id BIGINT,  -- 租户ID
    lock_version BIGINT DEFAULT 0
);

-- 4. 插件扩展点表
CREATE TABLE plugin_extension_point (
    id BIGINT PRIMARY KEY,
    definition_id BIGINT NOT NULL,  -- 插件定义ID
    extension_type VARCHAR(64) NOT NULL,  -- 扩展点类型(flow/api/data_hook/job/event/formula/ui)
    extension_name VARCHAR(128) NOT NULL,  -- 扩展点名称
    bean_class VARCHAR(256) NOT NULL,  -- 实现类
    config_json TEXT,  -- 配置JSON
    enabled BOOLEAN DEFAULT TRUE,
    creator BIGINT,
    create_time TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    updater BIGINT,
    update_time TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT DEFAULT 0,
    tenant_id BIGINT,
    lock_version BIGINT DEFAULT 0
);

-- 5. 插件配置表
CREATE TABLE plugin_config (
    id BIGINT PRIMARY KEY,
    installation_id BIGINT NOT NULL,  -- 安装记录ID
    config_key VARCHAR(128) NOT NULL,  -- 配置键
    config_value TEXT,  -- 配置值
    value_type VARCHAR(32),  -- 值类型(string/number/boolean/json)
    description VARCHAR(256),
    creator BIGINT,
    create_time TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    updater BIGINT,
    update_time TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT DEFAULT 0,
    tenant_id BIGINT,
    lock_version BIGINT DEFAULT 0,
    UNIQUE (installation_id, config_key, deleted)
);

-- 索引
CREATE INDEX idx_plugin_definition_code ON plugin_definition(plugin_code);
CREATE INDEX idx_plugin_definition_tenant ON plugin_definition(tenant_id, deleted);
CREATE INDEX idx_plugin_package_definition ON plugin_package(definition_id);
CREATE INDEX idx_plugin_installation_definition ON plugin_installation(definition_id);
CREATE INDEX idx_plugin_installation_tenant ON plugin_installation(tenant_id, status, deleted);
CREATE INDEX idx_plugin_extension_point_definition ON plugin_extension_point(definition_id);
CREATE INDEX idx_plugin_extension_point_type ON plugin_extension_point(extension_type, enabled, deleted);
CREATE INDEX idx_plugin_config_installation ON plugin_config(installation_id);
```

**验收标准:**
- [ ] 5张表创建成功
- [ ] 索引创建完成
- [ ] MyBatis-Flex实体类生成
- [ ] 通过单元测试验证CRUD

#### Task 1.3: 实现PluginClassLoader

**目标:** 自定义类加载器,支持插件隔离加载

**核心代码:**
```java
package com.cmsr.onebase.module.plugin.runtime.classloader;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * 插件类加载器
 * 实现插件与平台的类隔离
 */
public class PluginClassLoader extends URLClassLoader {

    private final ClassLoader parent;
    private final String pluginId;

    public PluginClassLoader(String pluginId, URL[] urls, ClassLoader parent) {
        super(urls, null);  // 破坏双亲委派
        this.pluginId = pluginId;
        this.parent = parent;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            // 1. 检查是否已加载
            Class<?> c = findLoadedClass(name);
            if (c != null) {
                return c;
            }

            // 2. 系统类委托给parent(如java.*, javax.*, org.springframework.*)
            if (isSystemClass(name)) {
                return parent.loadClass(name);
            }

            // 3. 插件类优先从当前ClassLoader加载
            try {
                c = findClass(name);
                if (resolve) {
                    resolveClass(c);
                }
                return c;
            } catch (ClassNotFoundException e) {
                // 4. 插件类未找到,委托给parent
                return parent.loadClass(name);
            }
        }
    }

    private boolean isSystemClass(String name) {
        return name.startsWith("java.") ||
               name.startsWith("javax.") ||
               name.startsWith("org.springframework.") ||
               name.startsWith("com.cmsr.onebase.framework.");
    }

    @Override
    public void close() throws IOException {
        super.close();
        // 清理资源
    }
}
```

**验收标准:**
- [ ] PluginClassLoader实现完成
- [ ] 能够加载插件JAR中的类
- [ ] 系统类正确委托给父ClassLoader
- [ ] 插件类优先从插件JAR加载
- [ ] 单元测试覆盖率 > 80%

#### Task 1.4: 实现PluginApplicationContext

**目标:** 为每个插件创建独立的Spring ApplicationContext

**核心代码:**
```java
package com.cmsr.onebase.module.plugin.runtime.context;

import org.springframework.context.support.GenericApplicationContext;

/**
 * 插件应用上下文
 * 每个插件拥有独立的Spring ApplicationContext
 */
public class PluginApplicationContext extends GenericApplicationContext {

    private final String pluginId;
    private final PluginClassLoader classLoader;

    public PluginApplicationContext(String pluginId, PluginClassLoader classLoader) {
        this.pluginId = pluginId;
        this.classLoader = classLoader;
        setClassLoader(classLoader);
        setParent(getMainApplicationContext());  // 设置父上下文
    }

    /**
     * 获取主应用上下文(OneBase平台)
     */
    private ApplicationContext getMainApplicationContext() {
        return ApplicationContextHolder.getApplicationContext();
    }

    /**
     * 销毁插件上下文
     */
    public void destroy() {
        close();
        try {
            classLoader.close();
        } catch (IOException e) {
            // 记录日志
        }
    }
}
```

**验收标准:**
- [ ] PluginApplicationContext实现完成
- [ ] 能够注册插件Bean
- [ ] 插件Bean能够注入平台Bean(通过父上下文)
- [ ] 上下文销毁时能清理所有资源
- [ ] 单元测试通过

#### Task 1.5: 实现PluginLoader

**目标:** 插件加载核心引擎

**核心代码:**
```java
package com.cmsr.onebase.module.plugin.runtime.loader;

import com.cmsr.onebase.module.plugin.api.dto.PluginDefinitionDTO;
import com.cmsr.onebase.module.plugin.runtime.classloader.PluginClassLoader;
import com.cmsr.onebase.module.plugin.runtime.context.PluginApplicationContext;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 插件加载器
 */
@Service
public class PluginLoader {

    private final Map<String, PluginApplicationContext> pluginContexts = new ConcurrentHashMap<>();
    private final FileService fileService;

    /**
     * 加载插件
     */
    public void loadPlugin(PluginDefinitionDTO plugin) {
        String pluginId = plugin.getPluginCode() + ":" + plugin.getPluginVersion();

        // 1. 下载插件JAR文件
        File jarFile = fileService.downloadFile(plugin.getFileId());

        // 2. 创建PluginClassLoader
        URL[] urls = new URL[] { jarFile.toURI().toURL() };
        PluginClassLoader classLoader = new PluginClassLoader(
            pluginId,
            urls,
            Thread.currentThread().getContextClassLoader()
        );

        // 3. 创建PluginApplicationContext
        PluginApplicationContext context = new PluginApplicationContext(pluginId, classLoader);

        // 4. 加载插件主类
        Class<?> mainClass = classLoader.loadClass(plugin.getMainClass());

        // 5. 扫描插件中的Bean(@Component, @Service, @Configuration等)
        context.scan(mainClass.getPackage().getName());

        // 6. 刷新上下文
        context.refresh();

        // 7. 注册扩展点
        registerExtensionPoints(context, plugin);

        // 8. 缓存上下文
        pluginContexts.put(pluginId, context);
    }

    /**
     * 卸载插件
     */
    public void unloadPlugin(String pluginId) {
        PluginApplicationContext context = pluginContexts.remove(pluginId);
        if (context != null) {
            // 1. 注销扩展点
            unregisterExtensionPoints(pluginId);

            // 2. 销毁上下文
            context.destroy();
        }
    }

    /**
     * 注册扩展点
     */
    private void registerExtensionPoints(PluginApplicationContext context, PluginDefinitionDTO plugin) {
        // 扫描插件中的扩展点Bean,注册到平台
        Map<String, Object> extensionBeans = context.getBeansWithAnnotation(ExtensionPoint.class);
        for (Map.Entry<String, Object> entry : extensionBeans.entrySet()) {
            // 注册到ExtensionPointRegistry
        }
    }
}
```

**验收标准:**
- [ ] 能够成功加载插件JAR
- [ ] 插件Bean正确注册到ApplicationContext
- [ ] 插件扩展点自动注册
- [ ] 卸载插件时资源完全清理
- [ ] 内存泄漏检测通过
- [ ] 集成测试通过

#### Task 1.6: 实现插件上传服务

**目标:** 提供插件上传和注册API

**核心代码:**
```java
@RestController
@RequestMapping("/admin-api/plugin")
@Tag(name = "管理后台 - 插件管理")
public class PluginController {

    @Autowired
    private PluginService pluginService;

    @PostMapping("/upload")
    @Operation(summary = "上传插件")
    public CommonResult<Long> uploadPlugin(@RequestParam("file") MultipartFile file) {
        // 1. 验证文件(JAR格式、大小限制)
        // 2. 解析插件元数据(plugin.yml)
        // 3. 保存文件到infra_file
        // 4. 创建plugin_definition和plugin_package记录
        // 5. 返回插件ID
        return success(pluginService.uploadPlugin(file));
    }

    @PostMapping("/install")
    @Operation(summary = "安装插件")
    public CommonResult<Boolean> installPlugin(@RequestBody PluginInstallReqVO reqVO) {
        pluginService.installPlugin(reqVO);
        return success(true);
    }

    @PostMapping("/enable/{id}")
    @Operation(summary = "启用插件")
    public CommonResult<Boolean> enablePlugin(@PathVariable Long id) {
        pluginService.enablePlugin(id);
        return success(true);
    }

    @PostMapping("/disable/{id}")
    @Operation(summary = "禁用插件")
    public CommonResult<Boolean> disablePlugin(@PathVariable Long id) {
        pluginService.disablePlugin(id);
        return success(true);
    }

    @DeleteMapping("/uninstall/{id}")
    @Operation(summary = "卸载插件")
    public CommonResult<Boolean> uninstallPlugin(@PathVariable Long id) {
        pluginService.uninstallPlugin(id);
        return success(true);
    }
}
```

**验收标准:**
- [ ] 上传接口实现完成
- [ ] 元数据解析正确(plugin.yml)
- [ ] 安装/启用/禁用/卸载接口正常工作
- [ ] 租户隔离验证通过
- [ ] API文档生成(Swagger)
- [ ] 接口测试通过

### Phase 2: 扩展点实现 (6-8周,分批实现)

#### Task 2.1: 流程扩展点 (P0 - 最高优先级)

**目标:** 支持插件扩展LiteFlow和Flowable流程

**LiteFlow组件扩展:**
```java
// 插件SDK接口
@ExtensionPoint(type = "flow")
public interface FlowComponentExtension {
    String getComponentName();  // LiteFlow组件名称
    void execute(NodeComponent component);  // 组件执行逻辑
}

// 插件开发者实现示例
@Component
public class MyCustomFlowComponent implements FlowComponentExtension {
    @Override
    public String getComponentName() {
        return "customNode1";
    }

    @Override
    public void execute(NodeComponent component) {
        // 自定义流程逻辑
    }
}
```

**Flowable服务任务扩展:**
```java
@ExtensionPoint(type = "bpm_service_task")
public interface BpmServiceTaskExtension {
    String getTaskType();
    void execute(DelegateExecution execution);
}
```

**验收标准:**
- [ ] 插件能注册LiteFlow组件
- [ ] 插件能注册Flowable服务任务
- [ ] 流程能调用插件组件
- [ ] 插件卸载时组件自动注销
- [ ] 集成测试通过

#### Task 2.2: API扩展点 (P0 - 最高优先级)

**目标:** 插件能注册自定义REST API端点

**实现方案:**
```java
// 插件中的自定义Controller
@RestController
@RequestMapping("/plugin-api/my-plugin")
public class MyPluginApiController {

    @GetMapping("/custom-endpoint")
    public CommonResult<String> customEndpoint() {
        return success("Hello from plugin!");
    }
}
```

**动态路由注册:**
```java
@Service
public class PluginApiRegistry {

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    /**
     * 注册插件API
     */
    public void registerPluginApi(PluginApplicationContext context) {
        Map<String, Object> controllers = context.getBeansWithAnnotation(RestController.class);
        for (Object controller : controllers.values()) {
            // 动态注册RequestMapping
            handlerMapping.registerMapping(...);
        }
    }

    /**
     * 注销插件API
     */
    public void unregisterPluginApi(String pluginId) {
        // 注销所有该插件的RequestMapping
    }
}
```

**验收标准:**
- [ ] 插件能注册自定义API端点
- [ ] API路径使用 `/plugin-api/{plugin-code}/` 前缀
- [ ] 支持认证鉴权
- [ ] 插件卸载时API自动注销
- [ ] Swagger文档自动更新
- [ ] API测试通过

#### Task 2.3: 数据钩子扩展点 (P1)

**目标:** 插件能监听业务实体的CRUD操作

**SDK接口:**
```java
@ExtensionPoint(type = "data_hook")
public interface DataHookExtension {

    /**
     * 支持的实体编码
     */
    String getEntityCode();

    /**
     * 保存前钩子
     */
    default void beforeSave(DataHookContext context) {}

    /**
     * 保存后钩子
     */
    default void afterSave(DataHookContext context) {}

    /**
     * 删除前钩子
     */
    default void beforeDelete(DataHookContext context) {}

    /**
     * 删除后钩子
     */
    default void afterDelete(DataHookContext context) {}
}

// DataHookContext
public class DataHookContext {
    private String entityCode;
    private Map<String, Object> data;  // 实体数据
    private Long userId;  // 当前用户
    private Long tenantId;  // 当前租户
    // ... getters/setters
}
```

**验收标准:**
- [ ] 插件能注册数据钩子
- [ ] beforeSave/afterSave正确触发
- [ ] beforeDelete/afterDelete正确触发
- [ ] 支持多个插件监听同一实体
- [ ] 钩子执行顺序可配置
- [ ] 测试通过

#### Task 2.4: 定时任务扩展点 (P1)

**目标:** 插件能注册Cron定时任务

**SDK接口:**
```java
@ExtensionPoint(type = "scheduled_job")
public interface ScheduledJobExtension {

    /**
     * 任务名称
     */
    String getJobName();

    /**
     * Cron表达式
     */
    String getCronExpression();

    /**
     * 任务执行逻辑
     */
    void execute(JobExecutionContext context);
}
```

**验收标准:**
- [ ] 插件能注册定时任务
- [ ] 任务按Cron表达式执行
- [ ] 插件卸载时任务自动停止
- [ ] 任务执行历史记录
- [ ] 测试通过

#### Task 2.5: 事件监听扩展点 (P2)

**目标:** 插件能监听系统事件和业务事件

**SDK接口:**
```java
@ExtensionPoint(type = "event_listener")
public interface EventListenerExtension {

    /**
     * 支持的事件类型
     */
    Class<? extends ApplicationEvent> getEventType();

    /**
     * 事件处理
     */
    void onEvent(ApplicationEvent event);
}
```

**验收标准:**
- [ ] 插件能监听Spring ApplicationEvent
- [ ] 支持自定义业务事件
- [ ] 异步事件处理
- [ ] 测试通过

#### Task 2.6: 公式函数扩展点 (P2)

**目标:** 插件能扩展Formula引擎的函数

**SDK接口:**
```java
@ExtensionPoint(type = "formula_function")
public interface FormulaFunctionExtension {

    /**
     * 函数名称
     */
    String getFunctionName();

    /**
     * 函数执行
     */
    Object execute(Object... args);

    /**
     * 参数定义
     */
    FunctionParameter[] getParameters();
}
```

**验收标准:**
- [ ] 插件能注册公式函数
- [ ] 函数能在低代码表单中使用
- [ ] 参数验证正确
- [ ] 测试通过

#### Task 2.7: UI扩展点 (P2)

**目标:** 插件能注册前端组件

**实现方案:**
```java
@ExtensionPoint(type = "ui_component")
public interface UiComponentExtension {

    /**
     * 组件名称
     */
    String getComponentName();

    /**
     * 组件前端资源URL
     */
    String getComponentUrl();  // Vue/React组件JS文件

    /**
     * 组件配置Schema
     */
    String getConfigSchema();  // JSON Schema
}
```

**验收标准:**
- [ ] 插件能注册前端组件
- [ ] 组件能在低代码页面中使用
- [ ] 测试通过

### Phase 3: 管理与监控 (2-3周)

#### Task 3.1: 插件管理界面

**功能:**
- [ ] 插件列表(分页、搜索、筛选)
- [ ] 插件上传
- [ ] 插件安装/卸载
- [ ] 插件启用/禁用
- [ ] 插件配置
- [ ] 插件详情查看

#### Task 3.2: 插件监控

**功能:**
- [ ] 插件状态监控(运行中/已停止/异常)
- [ ] 插件资源占用(内存、CPU)
- [ ] 插件API调用统计
- [ ] 插件事件统计

#### Task 3.3: 插件日志

**功能:**
- [ ] 插件日志查看(实时/历史)
- [ ] 日志级别过滤
- [ ] 日志搜索
- [ ] 日志导出

---

## 🗄️ Database Schema

详见 **Task 1.2** 的DDL设计,包含5张核心表:

1. **plugin_definition** - 插件定义表
2. **plugin_package** - 插件包表
3. **plugin_installation** - 插件安装表
4. **plugin_extension_point** - 插件扩展点表
5. **plugin_config** - 插件配置表

---

## 🔌 API Design

### 插件管理API

**Base Path:** `/admin-api/plugin`

#### 1. 上传插件

```http
POST /admin-api/plugin/upload
Content-Type: multipart/form-data

file: <plugin.jar>
```

**Response:**
```json
{
  "code": 0,
  "data": 123,  // 插件ID
  "msg": "success"
}
```

#### 2. 安装插件

```http
POST /admin-api/plugin/install
Content-Type: application/json

{
  "definitionId": 123,
  "installType": 1,  // 0=平台级,1=租户级,2=应用级
  "applicationId": 456  // 仅应用级安装时需要
}
```

#### 3. 启用插件

```http
POST /admin-api/plugin/enable/{id}
```

#### 4. 禁用插件

```http
POST /admin-api/plugin/disable/{id}
```

#### 5. 卸载插件

```http
DELETE /admin-api/plugin/uninstall/{id}
```

#### 6. 查询插件列表

```http
GET /admin-api/plugin/list?pageNo=1&pageSize=10&pluginName=xxx
```

**Response:**
```json
{
  "code": 0,
  "data": {
    "list": [
      {
        "id": 123,
        "pluginCode": "my-plugin",
        "pluginName": "我的插件",
        "pluginVersion": "1.0.0",
        "status": 1,  // 0=已安装,1=已启用,2=已禁用
        "installType": 1,
        "installedAt": "2025-12-06T10:00:00"
      }
    ],
    "total": 100
  }
}
```

---

## 📦 SDK Specification

### onebase-plugin-sdk结构

```
onebase-plugin-sdk/
├── src/main/java/
│   └── com/cmsr/onebase/plugin/sdk/
│       ├── annotation/
│       │   ├── @ExtensionPoint  # 扩展点注解
│       │   └── @PluginConfiguration  # 插件配置注解
│       ├── api/
│       │   ├── FlowComponentExtension  # 流程扩展接口
│       │   ├── DataHookExtension  # 数据钩子接口
│       │   ├── ScheduledJobExtension  # 定时任务接口
│       │   └── ...  # 其他扩展点接口
│       ├── context/
│       │   ├── PluginContext  # 插件上下文
│       │   └── DataHookContext  # 数据钩子上下文
│       └── util/
│           └── PluginUtils  # 插件工具类
└── src/main/resources/
    └── META-INF/
        └── plugin.yml  # 插件元数据模板
```

### plugin.yml示例

```yaml
# 插件元数据
plugin:
  code: my-custom-plugin
  name: 我的自定义插件
  version: 1.0.0
  description: 这是一个示例插件
  author: John Doe
  homepage: https://example.com
  main-class: com.example.myplugin.MyPluginMain

# 依赖
dependencies:
  - groupId: org.springframework.boot
    artifactId: spring-boot-starter-web
    version: 3.4.5

# 扩展点
extensions:
  - type: flow
    name: customFlowNode1
    class: com.example.myplugin.flow.CustomFlowComponent
  - type: api
    class: com.example.myplugin.api.MyPluginApiController
```

### Maven Archetype(插件脚手架)

```bash
mvn archetype:generate \
  -DarchetypeGroupId=com.cmsr.onebase \
  -DarchetypeArtifactId=onebase-plugin-archetype \
  -DarchetypeVersion=1.0.0 \
  -DgroupId=com.example \
  -DartifactId=my-plugin \
  -Dversion=1.0.0
```

生成的项目结构:
```
my-plugin/
├── pom.xml
├── src/main/java/
│   └── com/example/myplugin/
│       ├── MyPluginMain.java  # 插件主类
│       ├── flow/
│       │   └── CustomFlowComponent.java
│       ├── api/
│       │   └── MyPluginApiController.java
│       └── config/
│           └── PluginConfiguration.java
└── src/main/resources/
    ├── META-INF/
    │   └── plugin.yml
    └── application-plugin.yml
```

---

## 🧪 Testing Strategy

### 单元测试

**覆盖率目标:** > 80%

**关键测试:**
- [ ] PluginClassLoader类加载测试
- [ ] PluginApplicationContext生命周期测试
- [ ] PluginLoader加载/卸载测试
- [ ] 扩展点注册/注销测试

### 集成测试

**关键场景:**
- [ ] 完整插件生命周期测试(上传→安装→启用→禁用→卸载)
- [ ] 多租户隔离测试
- [ ] 插件热部署测试(加载/卸载不重启服务器)
- [ ] 扩展点调用测试

### 性能测试

**指标:**
- [ ] 插件加载时间 < 5秒
- [ ] 插件卸载时间 < 2秒
- [ ] 插件API响应时间 < 500ms (P99)
- [ ] 支持同时加载 >= 50个插件

### 安全测试

**验证项:**
- [ ] 租户隔离验证(租户A无法访问租户B的插件)
- [ ] 插件权限验证
- [ ] 恶意插件防护(文件大小、类加载限制)

---

## ✅ Acceptance Criteria

### 功能验收

**必须满足(MUST):**
- [ ] AC-1: **插件上传** - 开发者能通过管理界面上传JAR包,系统自动解析plugin.yml并创建插件定义
- [ ] AC-2: **插件安装** - 平台管理员能安装插件到平台级(tenant_id=0),租户管理员能安装插件到租户级
- [ ] AC-3: **插件启用/禁用** - 插件安装后能启用/禁用,禁用后扩展点不生效
- [ ] AC-4: **插件卸载** - 卸载插件时清理所有数据、Bean、扩展点,不影响平台运行
- [ ] AC-5: **热部署** - 插件加载/卸载不需要重启OneBase服务器,已有用户会话不中断
- [ ] AC-6: **流程扩展** - 插件能注册LiteFlow组件,流程能调用插件组件
- [ ] AC-7: **API扩展** - 插件能注册自定义REST API,外部能调用插件API
- [ ] AC-8: **租户隔离** - 租户A的插件对租户B完全不可见,插件数据严格隔离

**应该满足(SHOULD):**
- [ ] AC-9: **数据钩子** - 插件能监听业务实体的beforeSave/afterSave事件
- [ ] AC-10: **定时任务** - 插件能注册Cron定时任务
- [ ] AC-11: **版本管理** - 同一插件的多个版本能共存,支持版本升级/回滚

**可以满足(COULD):**
- [ ] AC-12: **事件监听** - 插件能监听系统事件和业务事件
- [ ] AC-13: **公式函数** - 插件能扩展Formula引擎的函数
- [ ] AC-14: **UI组件** - 插件能注册前端组件

### 性能验收

- [ ] 插件加载时间 < 5秒
- [ ] 插件卸载时间 < 2秒
- [ ] 插件API P99响应时间 < 500ms
- [ ] 支持 >= 50个插件同时运行
- [ ] 内存泄漏检测通过(卸载插件后内存能回收)

### 安全验收

- [ ] 租户隔离测试通过(100%隔离)
- [ ] 插件文件大小限制(< 100MB)
- [ ] 恶意代码扫描通过

---

## 🔗 Dependencies & Risks

### 技术依赖

**必需依赖:**
- Spring Boot 3.4.5
- MyBatis-Flex
- onebase-module-flow (流程扩展)
- onebase-module-metadata (数据钩子)
- onebase-module-infra (文件上传、定时任务)
- onebase-framework-tenant (多租户)

**可选依赖:**
- Apache Felix/Equinox (Phase 2,如果选择OSGi)

### 潜在风险

#### 风险1: 类加载冲突

**描述:** 插件依赖的第三方库版本与平台冲突

**影响:** 高

**缓解措施:**
- 插件ClassLoader优先加载插件JAR中的类
- 强制插件使用 `provided` scope依赖平台已有的库(Spring Boot等)
- 提供依赖冲突检测工具

#### 风险2: 内存泄漏

**描述:** 插件卸载后ClassLoader未被GC回收

**影响:** 高

**缓解措施:**
- 严格清理ThreadLocal
- 关闭所有资源(File、Socket、Thread)
- 使用WeakReference
- 定期内存泄漏检测

#### 风险3: 插件恶意代码

**描述:** 插件包含恶意代码(删除文件、网络攻击等)

**影响:** 中

**缓解措施:**
- 插件上传时进行代码扫描(病毒扫描、敏感API检测)
- 插件审核机制(平台级插件需管理员审核)
- 限制插件能调用的API(黑名单)
- Phase 2考虑引入沙箱隔离

#### 风险4: 性能影响

**描述:** 大量插件影响平台性能

**影响:** 中

**缓解措施:**
- 限制插件数量(租户级 < 20,平台级 < 10)
- 插件性能监控和告警
- 异步加载插件
- 延迟初始化(Lazy Initialization)

#### 风险5: 学习曲线

**描述:** 开发者不熟悉插件开发

**影响:** 低

**缓解措施:**
- 提供详细的开发文档
- 提供示例插件
- 提供Maven Archetype脚手架
- 提供开发视频教程

---

## 📚 References

**项目文档:**
- `docs/project-scan-report.json` - 项目扫描报告
- `docs/api-contracts-backend.md` - API合约文档
- `docs/data-models-backend.md` - 数据模型文档
- `docs/deployment-configuration-backend.md` - 部署配置文档
- `docs/session-2025-12-06-plugin-system-techspec.md` - 需求澄清会话记录

**技术参考:**
- Spring Framework Documentation: https://docs.spring.io/spring-framework/reference/
- MyBatis-Flex Documentation: https://mybatis-flex.com/
- LiteFlow Documentation: https://liteflow.cc/
- Flowable Documentation: https://www.flowable.com/open-source/docs/

**类加载参考:**
- Java ClassLoader机制详解
- OSGi规范: https://www.osgi.org/
- Apache Felix: https://felix.apache.org/

---

**文档版本:** 1.0
**最后更新:** 2025-12-06
**维护者:** OneBase Team
**审核状态:** Ready for Development

---

## 📝 Change Log

| 日期 | 版本 | 变更说明 | 作者 |
|------|------|----------|------|
| 2025-12-06 | 1.0 | 初始版本 | Claude (Analyst) |

---

## ✅ Next Steps

1. **技术评审** - 组织团队评审本Tech-Spec
2. **任务分解** - 将Implementation Plan分解为具体Story
3. **Sprint规划** - 安排开发Sprint
4. **原型开发** - 优先实现核心框架(Task 1.1-1.6)
5. **迭代开发** - 按Phase 1→2→3逐步实现

---

**Ready to Start Development! 🚀**
