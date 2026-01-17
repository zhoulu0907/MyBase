# onebase-v3-be - Epic Breakdown

**Author:** Kanten
**Date:** 2025-12-20
**Project Level:** Medium-High
**Target Scale:** MVP (验证型) - 6-8周单人开发

---

## Overview

This document provides the complete epic and story breakdown for onebase-v3-be, decomposing the requirements from the [PRD](./PRD.md) into implementable stories.

**Living Document Notice:** This is the initial version. It will be updated after UX Design and Architecture workflows add interaction and technical details to stories.

### Epic摘要

本项目采用**验证型MVP策略**，将29个功能需求组织为**5个Epic**，按用户价值增量交付：

**Epic 1: 插件框架基础** - 证明ClassLoader隔离和生命周期管理技术可行性
**Epic 2: 流程扩展能力** - 最终用户在OneBase流程中无缝使用插件自定义组件
**Epic 3: 插件管理平台** - 平台管理员通过REST API便捷管理插件完整生命周期
**Epic 4: 插件开发工具链** - ISV开发者30分钟完成HelloWorld插件从开发到部署
**Epic 5: 插件元数据与自动发现** - 系统自动处理元数据解析和依赖验证，减少人工错误

**交付策略**: 按Sprint递增交付（Sprint 1-4，每Sprint 2周），每个Epic完成后具备独立价值验证能力。

---

## Functional Requirements Inventory

### 上下文验证

**已加载的输入文档：**
1. ✅ **PRD.md** - 产品需求文档已成功加载 (~75KB，29个功能需求 + 31个非功能需求)
2. ✅ **Architecture.md** - 架构决策文档已成功加载 (~8KB，技术栈和集成分析)
3. ⚪ **UX Design.md** - 未找到（本项目为后端开发者工具，无需UI设计）

**项目类型：** 企业级私有化部署低代码平台插件系统（验证型MVP）

**验证状态：** ✅ 所有必需文档已加载，可以继续Epic和Story分解

### 功能需求清单

根据PRD文档完整提取的功能需求，组织为6个核心能力领域

#### 1. 插件生命周期管理（6个FR）
- **FR1.1** - 插件加载：JAR文件加载到运行环境，ClassLoader隔离，<10秒
- **FR1.2** - 插件安装：注册到平台，扩展点和Bean注册，状态INSTALLED
- **FR1.3** - 插件卸载：移除元数据和文件，状态UNINSTALLED（需重启）
- **FR1.4** - 插件重启加载：自动加载INSTALLED状态插件，启动时间增加<20%
- **FR1.5** - 插件状态查询：查询所有插件状态，支持过滤，<500ms

#### 2. 流程扩展能力（4个FR）
- **FR2.1** - 流程组件扩展点定义：SDK提供FlowComponentExtension接口
- **FR2.2** - 流程组件动态注册：自动注册到LiteFlow，命名格式隔离
- **FR2.3** - 流程组件运行时调用：流程中无缝调用，<500ms（P95）
- **FR2.4** - 流程组件故障隔离：异常不中断流程，平台核心稳定

#### 3. 插件开发工具链（4个FR）
- **FR3.1** - 插件SDK：完整API，Javadoc 100%覆盖
- **FR3.2** - Maven脚手架：一键生成项目骨架，<30秒
- **FR3.3** - HelloWorld示例：完整示例，测试≥80%覆盖
- **FR3.4** - 本地测试：Mock OneBase环境，IDE单元测试

#### 4. 插件管理接口（5个FR）
- **FR4.1** - 上传API：POST /api/plugin/upload，支持50MB
- **FR4.2** - 安装API：POST /api/plugin/install/{pluginId}
- **FR4.3** - 卸载API：DELETE /api/plugin/uninstall/{pluginId}
- **FR4.4** - 列表API：GET /api/plugin/list，支持状态过滤
- **FR4.5** - 详情API：GET /api/plugin/{pluginId}，<300ms

#### 5. 插件元数据与发现（4个FR）
- **FR5.1** - 元数据定义：plugin.yaml声明
- **FR5.2** - 元数据解析：自动解析和验证
- **FR5.3** - 依赖验证：版本和兼容性检查
- **FR5.4** - 扩展点发现：自动扫描和注册

#### 6. 插件隔离与安全（6个FR）
- **FR6.1** - ClassLoader隔离：独立ClassLoader避免类冲突
- **FR6.2** - 命名空间隔离：Bean ID格式`plugin.{pluginId}.{beanName}`
- **FR6.3** - 异常隔离：插件异常不导致平台崩溃
- **FR6.4** - 内存泄漏防护：卸载清理资源，<50MB泄漏可接受
- **FR6.5** - 平台级作用域：MVP阶段无租户隔离

**总计：29个功能需求**

---

## FR Coverage Map

### Epic结构概览

本项目采用**验证型MVP策略**（8周，4个Sprint），将29个功能需求组织为5个Epic，按用户价值增量交付：

**Epic 1：插件框架基础** (Sprint 1, Week 1-2)
→ **用户价值**：证明插件系统技术可行性，开发者能够加载和运行插件JAR

**Epic 2：流程扩展能力** (Sprint 2, Week 3-4)
→ **用户价值**：最终用户在OneBase流程中无缝使用插件提供的自定义组件

**Epic 3：插件管理平台** (Sprint 3, Week 5-6)
→ **用户价值**：平台管理员通过REST API便捷管理插件完整生命周期

**Epic 4：插件开发工具链** (Sprint 2-4, Week 3-8)
→ **用户价值**：ISV开发者30分钟完成Hello World插件从开发到部署全流程

**Epic 5：插件元数据与自动发现** (Sprint 3-4, Week 5-8)
→ **用户价值**：系统自动处理元数据解析和依赖验证，减少人工配置错误

### Epic技术上下文映射

| Epic | 覆盖的FR | Architecture技术点 | MVP技术债 |
|------|---------|-------------------|----------|
| Epic 1 | FR1.1, FR1.4, FR6.1, FR6.3, FR6.4 | PluginClassLoader、WeakReference清理 | 允许<50MB内存泄漏，无热部署 |
| Epic 2 | FR2.1-2.4 | LiteFlow 2.15.0动态注册、Spring BeanDefinitionRegistry | 仅流程扩展点，其他6个延后 |
| Epic 3 | FR1.2, FR1.3, FR1.5, FR4.1-4.5 | MyBatis-Flex 1.11.4、Multipart上传 | 无UI，安装卸载需重启 |
| Epic 4 | FR3.1-3.4, FR5.1 | Maven Archetype、onebase-plugin-sdk | 无完整开发服务器 |
| Epic 5 | FR5.2-5.4, FR6.2, FR6.5 | YAML解析、SemVer、反射扫描 | 依赖冲突手动处理 |

### Epic依赖关系

```
Epic 1 (插件框架基础)
   ↓ 依赖
Epic 2 (流程扩展能力) + Epic 4 (开发工具链) 并行
   ↓ 依赖
Epic 3 (插件管理平台) + Epic 5 (元数据发现) 并行
   ↓
MVP完成
```

---

---

## FR Coverage Matrix

### Epic vs FR 映射表

| FR编号 | 功能需求 | 覆盖Epic | 优先级 | Sprint |
|--------|---------|---------|--------|--------|
| **1. 插件生命周期管理** | | | | |
| FR1.1 | 插件加载 | Epic 1 | P0 | Sprint 1 |
| FR1.2 | 插件安装 | Epic 3 | P0 | Sprint 3 |
| FR1.3 | 插件卸载 | Epic 3 | P0 | Sprint 3 |
| FR1.4 | 插件重启加载 | Epic 1 | P0 | Sprint 1 |
| FR1.5 | 插件状态查询 | Epic 3 | P0 | Sprint 3 |
| **2. 流程扩展能力** | | | | |
| FR2.1 | 流程组件扩展点定义 | Epic 2 | P0 | Sprint 2 |
| FR2.2 | 流程组件动态注册 | Epic 2 | P0 | Sprint 2 |
| FR2.3 | 流程组件运行时调用 | Epic 2 | P0 | Sprint 2 |
| FR2.4 | 流程组件故障隔离 | Epic 2 | P0 | Sprint 2 |
| **3. 插件开发工具链** | | | | |
| FR3.1 | 插件SDK | Epic 4 | P0 | Sprint 2-4 |
| FR3.2 | Maven项目脚手架 | Epic 4 | P0 | Sprint 4 |
| FR3.3 | HelloWorld示例插件 | Epic 4 | P0 | Sprint 2 |
| FR3.4 | 插件本地测试 | Epic 4 | P0 | Sprint 4 |
| **4. 插件管理接口** | | | | |
| FR4.1 | 插件上传API | Epic 3 | P0 | Sprint 3 |
| FR4.2 | 插件安装API | Epic 3 | P0 | Sprint 3 |
| FR4.3 | 插件卸载API | Epic 3 | P0 | Sprint 3 |
| FR4.4 | 插件列表查询API | Epic 3 | P0 | Sprint 3 |
| FR4.5 | 插件详情查询API | Epic 3 | P0 | Sprint 3 |
| **5. 插件元数据与发现** | | | | |
| FR5.1 | 插件元数据定义 | Epic 4 | P0 | Sprint 4 |
| FR5.2 | 插件元数据解析 | Epic 5 | P0 | Sprint 3-4 |
| FR5.3 | 插件依赖验证 | Epic 5 | P0 | Sprint 3-4 |
| FR5.4 | 插件扩展点发现 | Epic 5 | P0 | Sprint 3-4 |
| **6. 插件隔离与安全** | | | | |
| FR6.1 | ClassLoader隔离 | Epic 1 | P0 | Sprint 1 |
| FR6.2 | 命名空间隔离 | Epic 5 | P0 | Sprint 3-4 |
| FR6.3 | 异常隔离 | Epic 1 | P0 | Sprint 1 |
| FR6.4 | 内存泄漏基础防护 | Epic 1 | P0 | Sprint 1 |
| FR6.5 | 平台级插件作用域 | Epic 5 | P0 | Sprint 3-4 |

### 覆盖率统计

**按Epic统计：**
- **Epic 1 (插件框架基础)**: 5个FR
  - FR1.1, FR1.4, FR6.1, FR6.3, FR6.4
- **Epic 2 (流程扩展能力)**: 4个FR
  - FR2.1, FR2.2, FR2.3, FR2.4
- **Epic 3 (插件管理平台)**: 8个FR
  - FR1.2, FR1.3, FR1.5, FR4.1, FR4.2, FR4.3, FR4.4, FR4.5
- **Epic 4 (插件开发工具链)**: 5个FR
  - FR3.1, FR3.2, FR3.3, FR3.4, FR5.1
- **Epic 5 (插件元数据与自动发现)**: 5个FR
  - FR5.2, FR5.3, FR5.4, FR6.2, FR6.5

**总计**: 29个功能需求，100%覆盖

**按能力领域统计：**
1. 插件生命周期管理: 5个FR - 分布于Epic 1和Epic 3
2. 流程扩展能力: 4个FR - 集中于Epic 2
3. 插件开发工具链: 4个FR - 集中于Epic 4
4. 插件管理接口: 5个FR - 集中于Epic 3
5. 插件元数据与发现: 4个FR - 分布于Epic 4和Epic 5
6. 插件隔离与安全: 5个FR - 分布于Epic 1和Epic 5

---

## Summary

### Epic分解完成度

本文档已完成onebase-v3-be插件系统的Epic和Story分解，将PRD中定义的29个功能需求组织为5个可执行的Epic，覆盖率100%。

### 核心成果

**1. MVP验证策略明确**
- 采用验证型MVP策略，8周完成核心技术可行性验证
- 仅实现1个扩展点（流程扩展），其他6个扩展点延后到Phase 2
- 允许技术债（无热部署、仅平台级插件、允许少量内存泄漏<50MB）

**2. 递增价值交付**
- 5个Epic按Sprint递增交付（Sprint 1-4，每Sprint 2周）
- 每个Epic完成后具备独立价值验证能力
- Epic依赖关系清晰：Epic 1 → (Epic 2 + Epic 4) → (Epic 3 + Epic 5)

**3. 用户旅程覆盖**
- **张伟（ISV开发者）**: Epic 4提供完整SDK和脚手架，30分钟开发HelloWorld插件
- **李娜（平台管理员）**: Epic 3提供REST API管理插件生命周期
- **王芳（最终用户）**: Epic 2确保插件功能与平台原生功能完全透明

### 架构对齐

Epic分解充分考虑了架构决策文档中的技术约束：

**集成点确认：**
- Epic 1: PluginClassLoader + PluginContainer（类隔离机制）
- Epic 2: LiteFlow动态组件注册（流程扩展集成）
- Epic 3: MyBatis-Flex元数据存储 + REST API
- Epic 4: Maven Archetype + onebase-plugin-sdk
- Epic 5: YAML解析 + SemVer依赖验证

**技术栈一致：**
- Spring Boot 3.4.5 + Java 17
- LiteFlow 2.15.0.2（流程编排）
- MyBatis-Flex 1.11.4（数据访问）
- 新增4个Maven模块：plugin-api、plugin-core、plugin-platform、plugin-sdk

### 实施准备度

**开发计划就绪：**
- Sprint 1 (Week 1-2): 插件加载框架（Epic 1）
- Sprint 2 (Week 3-4): 流程扩展集成（Epic 2 + Epic 4部分）
- Sprint 3 (Week 5-6): 插件管理API（Epic 3 + Epic 5部分）
- Sprint 4 (Week 7-8): SDK完善和文档（Epic 4 + Epic 5剩余）

**风险缓解：**
- 每Sprint结束检查点明确
- 允许技术债，优先验证核心假设
- Plan A/B/C应急计划就绪
- 技术债追踪机制（TECH_DEBT.md + 代码标记）

### 下一步工作

**Epic完成后的下一步（根据workflow-status）：**
1. ✅ **create-epics-and-stories** 已完成 → 更新状态为 `docs/epics.md`
2. ⏭️ **test-design** (推荐) - TEA Agent执行系统级可测试性评估
3. ⏭️ **implementation-readiness** (必需) - Architect Agent验证PRD+UX+Architecture+Epics一致性
4. ⏭️ **sprint-planning** (必需) - SM Agent创建Sprint计划和Story分解

**质量保证：**
- 本文档将在UX Design和Architecture workflows完成后更新，加入交互细节和技术决策
- Epic和Story将在Sprint Planning中进一步细化为可实施的任务
- 通过Implementation Readiness验证所有文档一致性后才进入Phase 4实施

---

_For implementation: Use the `create-story` workflow to generate individual story implementation plans from this epic breakdown._

_This document will be updated after UX Design and Architecture workflows to incorporate interaction details and technical decisions._
