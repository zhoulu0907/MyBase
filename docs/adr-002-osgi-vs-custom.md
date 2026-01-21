# ADR-002: OSGi vs 自研插件框架技术选型

**Status:** ✅ Accepted
**Date:** 2026-01-01
**Decision Maker:** Architecture Team
**Related Documents:** PRD v1.0, Architecture Document v1.0, Tech Spec v1.0

---

## Context（背景）

### 问题陈述

OneBase v3插件系统的核心技术决策：**是否采用OSGi标准框架，还是自研基于ClassLoader的插件框架？**

**当前状态:**
- Architecture文档（第156-158行）：提到"自定义ClassLoader"
- Tech Spec（第42行）：提到"Phase 2 (可选): 根据实际使用情况评估是否迁移到OSGi"
- **缺少正式的技术选型分析**

### 为什么需要决策

1. **技术路径不确定性**: 团队不清楚OSGi是否适合当前场景
2. **重复造轮子风险**: OSGi是成熟标准，自研可能重复工作
3. **架构扩展性影响**: 一旦选定，迁移成本极高
4. **学习曲线**: OSGi学习曲线陡峭，需要评估ROI

### 业务背景

**需求特征:**
- **单一扩展点（MVP）**: 仅LiteFlow组件扩展，非完整OSGi bundles
- **Spring生态深度集成**: 需要与Spring Boot 3.4.5紧密集成
- **团队技能**: Java/Spring经验丰富，但OSGi经验有限
- **交付压力**: 8-12周完成MVP，时间紧迫

**约束条件:**
- 必须支持Spring Bean动态注册
- 必须保证租户隔离（数据、ClassLoader、异常）
- 必须兼容现有OneBase架构（72个Maven模块）

---

## Decision（决策）

### ✅ 最终决策

**OneBase v3.0采用分阶段技术策略:**

#### Phase 1 (MVP): 自研插件框架

**技术栈:**
```
- Spring Boot 3.4.5
- 自定义PluginClassLoader (extends URLClassLoader)
- GenericApplicationContext (父子容器)
- LiteFlow动态组件注册API
```

**核心类设计:**
```java
public class PluginClassLoader extends URLClassLoader {
    // 打破双亲委派，优先加载插件类
    protected Class<?> loadClass(String name, boolean resolve) {
        // 1. 检查是否为SDK类（父类加载）
        // 2. 检查插件JAR是否包含该类
        // 3. 委派给父类加载器
    }
}

public class PluginContainer {
    private PluginClassLoader classLoader;
    private GenericApplicationContext applicationContext;
    private PluginMetadata metadata;

    public void load() {
        // 1. 创建ClassLoader
        // 2. 初始化Spring容器（父子模式）
        // 3. 注册LiteFlow组件
    }
}
```

#### Phase 2: OSGi评估（可选）

**评估触发条件:**
1. 插件数量 > 50个，管理复杂度显著上升
2. 需要支持插件间复杂依赖关系（Import/Export Package）
3. 需要插件版本隔离（同一插件多版本共存）
4. 团队OSGi技能成熟

**技术路径:**
- 如果采用OSGi: **Apache Felix** + **Spring DM**（Spring Dynamic Modules）
- 渐进迁移: 保留Phase 1 SDK，底层容器切换为OSGi

### 决策依据

#### 支持自研的理由（Phase 1）

1. **Spring原生集成** ⭐⭐⭐⭐⭐
   - OSGi与Spring集成复杂（需要Spring DM或Aries）
   - 自研方案直接使用Spring父子容器机制
   - LiteFlow组件注册API无需OSGi适配

   **证据:**
   - Spring DM已废弃，官方推荐Spring Boot而非OSGi
   - OSGi Bundle vs Spring Bean概念映射不直观

2. **学习曲线** ⭐⭐⭐⭐⭐
   - 团队Spring经验丰富，ClassLoader机制可快速掌握
   - OSGi需要学习:
     - Bundle生命周期（ACTIVE、RESOLVED、INSTALLED）
     - Import/Export Package语义
     - OSGi Service Registry（vs Spring Bean）
     - Bundle ClassLoader查找顺序（复杂）

   **预估学习成本:**
   - 自研ClassLoader: **2-3天**
   - OSGi入门: **2-3周**

3. **MVP范围控制** ⭐⭐⭐⭐
   - MVP仅支持**1个扩展点**（LiteFlow流程组件）
   - 无需复杂的Service依赖管理
   - 无需动态安装/卸载（Phase 1无热部署）

   **对比:**
   - OSGi优势在**复杂依赖管理**，但MVP不需要
   - 自研方案**够用即可**，符合敏捷原则

4. **部署简单性** ⭐⭐⭐⭐⭐
   - 自研: 插件JAR + Spring配置，开发者熟悉
   - OSGi: 需要OSGi容器（Felix/Knopflerfish）+ Bundle配置

5. **调试友好性** ⭐⭐⭐⭐
   - 自研: 标准Spring调试，断点直观
   - OSGi: Bundle状态复杂，错误信息晦涩

#### 支持OSGi的理由（Phase 2考虑）

1. **成熟标准** ⭐⭐⭐⭐⭐
   - OSGi是Java模块化标准（JSR 291）
   - 大量成熟工具（Bnd工具、POM元数据生成）
   - 企业级应用验证（Eclipse IDE、Apache Karaf）

2. **依赖管理** ⭐⭐⭐⭐
   - Import/Export Package语义化版本控制
   - 自动依赖解析（uses约束）
   - 多版本共存（同一库不同版本）

   **场景:**
   - 如果插件A需要Spring 5，插件B需要Spring 6 → OSGi可解决
   - 自研方案需要手动处理依赖冲突

3. **动态生命周期** ⭐⭐⭐⭐
   - Bundle的stop/start/uninstall是标准API
   - 自研需要自己实现状态机

4. **生态工具** ⭐⭐⭐⭐
   - Apache Felix Web Console（可视化Bundle管理）
   - Bndtools（Eclipse插件开发IDE）
   - OSGi EnRoute（最佳实践指南）

### 技术方案对比

| 维度 | 自研ClassLoader | OSGi (Apache Felix) | 权重 | 自研评分 | OSGi评分 |
|------|-----------------|---------------------|------|----------|----------|
| **开发成本** | 低（2-3周） | 高（6-8周） | ⭐⭐⭐⭐⭐ | 5 | 2 |
| **学习曲线** | 低（Spring团队熟悉） | 高（新概念多） | ⭐⭐⭐⭐⭐ | 5 | 2 |
| **Spring集成** | 完美（原生支持） | 复杂（需要适配层） | ⭐⭐⭐⭐⭐ | 5 | 2 |
| **依赖管理** | 手动（<scope>provided） | 自动（Import/Export） | ⭐⭐⭐ | 2 | 5 |
| **调试体验** | 好（标准Spring调试） | 差（Bundle状态复杂） | ⭐⭐⭐⭐ | 4 | 2 |
| **动态加载** | 需自实现 | 标准API | ⭐⭐ | 2 | 5 |
| **生态工具** | 少 | 丰富（Felix Web Console） | ⭐⭐ | 2 | 5 |
| **长期维护** | 风险高（自研维护成本） | 低（标准演进） | ⭐⭐⭐ | 2 | 4 |
| **MVP适配性** | 完美（简单场景够用） | 过度设计 | ⭐⭐⭐⭐⭐ | 5 | 2 |
| **总分** | | | | **32** | **29** |

**评分说明:**
- 自研总分**32**，OSGi总分**29**
- MVP场景下，**自研方案胜出**
- Phase 2如需复杂依赖管理，OSGi可能反超

---

## Consequences（后果）

### ✅ 正面影响（自研方案）

1. **快速交付MVP**
   - 预计节省**4-6周开发时间**
   - 团队技能匹配，无需培训
   - 代码简单，易维护

2. **Spring原生体验**
   - 插件开发者无需学习OSGi
   - 使用标准Spring注解（@Component、@Autowired）
   - IDE支持完整（自动补全、重构）

3. **调试和监控友好**
   - 标准Spring Boot Actuator集成
   - 日志清晰（无OSGi Bundle状态噪音）
   - 性能分析工具兼容（JProfiler、VisualVM）

### ⚠️ 负面影响（自研方案）

1. **依赖管理手动化**
   - 开发者需手动标记`<scope>provided</scope>`
   - 依赖冲突需要人工排查
   - 无法实现同一库多版本共存

   **缓解措施:**
   - Maven Enforcer Plugin检测依赖冲突
   - 插件SDK文档明确说明依赖规则
   - Phase 2提供自动依赖分析工具

2. **动态加载需自实现**
   - 如Phase 2需要热部署，需自行实现
   - 内存泄漏风险（ClassLoader泄漏）
   - 状态机管理复杂度

   **缓解措施:**
   - Phase 1无热部署，降低复杂度
   - 使用WeakReference避免内存泄漏
   - 充分的单元测试和集成测试

3. **长期维护成本**
   - 自研框架需要持续维护
   - 不如OSGi标准演进
   - 知识传承风险（团队人员流动）

   **缓解措施:**
   - 详细的架构文档和代码注释
   - Phase 2保留迁移到OSGi的选项
   - 核心代码审查和知识共享

4. **重复造轮子质疑**
   - 技术团队可能被质疑"为什么不使用成熟标准"
   - 需要持续向Stakeholder解释决策依据

   **缓解措施:**
   - 本ADR文档作为正式决策依据
   - 定期回顾OSGi演进，保持开放态度

---

## Implementation Plan（实施计划）

### Phase 1 (MVP): 自研插件框架

#### Week 1: 核心ClassLoader开发

```java
// 目标类结构
onebase-module-plugin-core/
├── classloader/
│   ├── PluginClassLoader.java       // 自定义类加载器
│   └── PluginClassLoaderManager.java // 管理多个ClassLoader
├── container/
│   ├── PluginContainer.java         // 插件容器
│   ├── PluginApplicationContext.java // Spring容器包装
│   └── PluginBeanDefinitionReader.java // Bean定义读取
└── registry/
    └── PluginRegistry.java          // 插件注册表
```

**技术要点:**
1. **PluginClassLoader打破双亲委派**
   ```java
   @Override
   protected Class<?> loadClass(String name, boolean resolve) {
       // 1. 检查是否为Java核心类（java.*、javax.*）
       // 2. 检查是否为OneBase SDK类（com.cmsr.onebase.plugin.sdk.*）
       // 3. 检查插件JAR是否包含该类
       // 4. 委派给父类加载器
   }
   ```

2. **父子ApplicationContext隔离**
   ```java
   // 父容器: OneBase主应用
   ApplicationContext parent = SpringContextUtil.getApplicationContext();

   // 子容器: 插件独立容器
   GenericApplicationContext pluginContext = new GenericApplicationContext(parent);
   pluginContext.setClassLoader(pluginClassLoader);
   ```

3. **LiteFlow组件注册**
   ```java
   // 动态注册组件到LiteFlow
   FlowExecutor flowExecutor = parent.getBean(FlowExecutor.class);
   flowExecutor.reloadRule(pluginComponent);
   ```

#### Week 2: 插件生命周期管理

**状态机设计:**
```
UPLOADED → INSTALLED → STARTED → STOPPED → UNINSTALLED
              ↑           ↓
              └───────────┘ (重启)
```

**关键API:**
```java
public interface PluginManager {
    void install(PluginMetadata metadata);   // 安装插件
    void start(String pluginId);             // 启动插件
    void stop(String pluginId);              // 停止插件
    void uninstall(String pluginId);         // 卸载插件
    PluginStatus getStatus(String pluginId); // 查询状态
}
```

#### Week 3: 隔离机制实现

**三层隔离:**
1. **ClassLoader隔离** (已完成)
2. **Bean命名空间隔离**
   ```java
   // 插件Bean命名: plugin.{pluginId}.{beanName}
   String beanName = "plugin." + pluginId + "." + originalBeanName;
   ```
3. **异常隔离**
   ```java
   try {
       pluginContext.getBean(beanName).execute();
   } catch (Exception e) {
       // 插件异常不传播到平台
       log.error("Plugin execution failed", e);
       throw new PluginExecutionException(pluginId, e);
   }
   ```

#### Week 4: SDK和工具链

**Maven Archetype:**
```bash
mvn archetype:generate \
  -DarchetypeGroupId=com.cmsr.onebase \
  -DarchetypeArtifactId=onebase-plugin-archetype \
  -DgroupId=com.example.plugin \
  -DartifactId=my-first-plugin
```

**生成的插件项目结构:**
```
my-first-plugin/
├── pom.xml (依赖onebase-plugin-sdk)
├── src/main/java/
│   └── com/example/plugin/
│       ├── MyFlowComponent.java (LiteFlow组件)
│       └── PluginApplication.java (可选)
└── src/main/resources/
    └── META-INF/
        └── plugin.yaml (插件元数据)
```

### Phase 2 (可选): OSGi迁移评估

#### 评估触发条件（满足2+即启动）

1. **复杂度需求**
   - [ ] 插件间依赖关系 > 5个
   - [ ] 需要同一库多版本共存（如Spring 5 vs 6）
   - [ ] 插件数量 > 50个

2. **运维需求**
   - [ ] 需要频繁动态加载/卸载（>每周1次）
   - [ ] 需要插件市场生态
   - [ ] 需要可视化Bundle管理

3. **团队能力**
   - [ ] 至少2名OSGi专家
   - [ ] 有充足时间重构（6-8周）

#### 技术预研（2周POC）

**POC目标:**
1. 验证Apache Felix与Spring Boot 3.4.5集成
2. 评估Spring DM（或Aries）兼容性
3. 性能基准测试（启动时间、内存占用）
4. LiteFlow组件在OSGi环境注册

**POC验收标准:**
- [ ] Felix容器成功启动
- [ ] Spring Boot应用在Felix中正常运行
- [ ] LiteFlow组件可动态注册/卸载
- [ ] 性能损失 < 10%
- [ ] 内存泄漏测试通过（100次加载/卸载）

**决策矩阵:**
| POC结果 | 决策 |
|---------|------|
| 所有验收标准通过 | ✅ 迁移到OSGi（启动6周重构） |
| ≥3个验收标准通过 | ⏸️ 延迟到Phase 3 |
| <3个验收标准通过 | ❌ 放弃OSGi，继续自研 |

---

## Risk Management（风险管理）

### 自研方案风险

| 风险 | 概率 | 影响 | 缓解措施 |
|------|------|------|----------|
| ClassLoader内存泄漏 | 中 | 高 | 单元测试100次加载/卸载、VisualVM监控 |
| 依赖冲突难以排查 | 高 | 中 | Maven Enforcer Plugin、详细文档 |
| 动态加载复杂度爆炸 | 低 | 高 | Phase 1不实现热部署 |
| 团队技能不足 | 低 | 中 | 技术分享、代码审查、外部顾问 |
| 长期维护成本高 | 中 | 中 | 保留OSGi迁移选项、文档完善 |

### OSGi方案风险（如果采用）

| 风险 | 概率 | 影响 | 缓解措施 |
|------|------|------|----------|
| Spring集成复杂 | 高 | 高 | 使用Spring DM或Aries、专家顾问 |
| 学习曲线陡峭 | 高 | 中 | 团队培训、POC验证 |
| 调试困难 | 中 | 中 | Felix Web Console、日志增强 |
| 性能下降 | 中 | 低 | 性能测试、优化Bundle加载 |
| 过度设计 | 高 | 中 | MVP先简化、POC评估 |

---

## Open Questions（待解决问题）

1. **ClassLoader内存泄漏**
   - 如何确保插件卸载后ClassLoader被GC？
   - Action: Week 2进行压力测试（100次加载/卸载）

2. **依赖冲突检测**
   - 如何自动化检测插件依赖冲突？
   - Action: Maven Enforcer Plugin配置

3. **Spring Bean动态注册**
   - LiteFlow 2.15.0.2是否支持运行时注册组件？
   - Action: Week 1技术验证

4. **多实例部署**
   - 多实例环境下插件分发策略？
   - Action: 与运维团队确认部署架构

---

## References（参考文档）

### 技术文档

1. **OSGi规范**: https://www.osgi.org/developer/architecture/
2. **Apache Felix**: https://felix.apache.org/
3. **Spring Dynamic Modules**: https://www.springframework.org/osgi/
4. **LiteFlow文档**: https://liteflow.cc/pages/guides/
5. **OneBase Architecture**: 第156-158行（自定义ClassLoader）

### 类似项目

1. **Spring Boot Plugin** (https://github.com/davidafsilva/spring-boot-plugin)
   - 类似自研方案，基于ClassLoader
2. **Apache Karaf** (https://karaf.apache.org/)
   - 基于OSGi的容器，可参考
3. **JPF (Java Plugin Framework)** (https://github.com/jpf/jpf)
   - 自研插件框架参考实现

### 团队知识

- **ClassLoader机制**: 团队已有基础（OneBase多租户隔离使用）
- **Spring容器**: 团队专家级（核心框架）
- **OSGi**: 团队无经验（学习成本）

---

## Revision History（修订历史）

| Date | Version | Author | Changes |
|------|---------|--------|---------|
| 2026-01-01 | 1.0 | Architecture Team | Initial decision: Phase 1自研，Phase 2可选OSGi |

---

## Sign-Off（签字批准）

| Role | Name | Date | Status |
|------|------|------|--------|
| Product Owner | | | ⏳ Pending |
| Tech Lead | | | ⏳ Pending |
| Architecture Team | | | ⏳ Pending |

---

## Appendix: OSGi技术细节（参考资料）

### OSGi Bundle vs Spring Boot Jar

| 特性 | OSGi Bundle | Spring Boot Jar |
|------|-------------|-----------------|
| 依赖声明 | MANIFEST.MF (Import/Export Package) | pom.xml (dependency) |
| 类加载 | Bundle ClassLoader（图结构） | URLClassLoader（树状） |
| 生命周期 | INSTALLED → RESOLVED → ACTIVE | 无状态（JAR扫描即加载） |
| 服务注册 | OSGi Service Registry | Spring ApplicationContext |
| 版本管理 | 语义化版本范围（1.0.0, [1.0,2.0)） | Maven版本号 |

### OSGi与Spring集成方案

| 方案 | 状态 | 说明 |
|------|------|------|
| **Spring DM** | 已废弃 | Spring Dynamic Modules，官方不再维护 |
| **Apache Aries** | 活跃 | OSGi Blueprint实现，Spring集成 |
| **Spring Boot + OSGi** | 实验性 | 需要自定义调整 |

### 典型OSGi Bundle MANIFEST.MF

```manifest
Bundle-SymbolicName: com.example.plugin
Bundle-Version: 1.0.0
Export-Package: com.example.plugin.api;version="1.0.0"
Import-Package: com.cmsr.onebase.plugin.sdk;version="[1.0,2.0)"
Bundle-Activator: com.example.plugin.PluginActivator
```

### Spring Boot插件plugin.yaml（自研方案）

```yaml
plugin:
  id: com.example.plugin
  version: 1.0.0
  name: My First Plugin
  description: Example plugin for OneBase
  dependencies:
    - com.cmsr.onebase:onebase-plugin-sdk:1.0.0
  extensions:
    - type: liteflow-component
      class: com.example.plugin.MyFlowComponent
```
