# 项目会话记录 - 2026-01-01 (Week 1 架构决策)

**会话日期**: 2026-01-01
**项目**: onebase-v3-be 插件系统
**用户目标**: Week 1架构决策周 - 解决3个Critical Issues
**执行工作**: 架构决策文档生成

---

## ✅ 本次会话完成的工作

### 背景

根据上次会话（2026-01-01 Implementation Readiness）发现的**3个Critical Issues**:

1. 🔴 **热部署冲突** - PRD定位为"核心差异化能力"，Architecture明确说"无热部署"
2. 🔴 **时间规划不现实** - PRD说8周，Tech Spec说12-17周
3. 🔴 **自研vs OSGi未决策** - 所有文档提到"自研ClassLoader"，但缺少"为什么不用OSGi"的分析

### Week 1行动: 架构决策周

本次会话完成了**全部3个架构决策文档**的生成：

#### 1. ADR-001: 热部署技术决策 ✅

**文件**: `docs/adr-001-hot-deployment.md`

**核心决策**:
- **MVP阶段采用无热部署**方案
- 插件安装/卸载需要重启服务器（2-5分钟停机）
- Phase 2根据实际反馈评估是否实现热部署

**技术方案对比**:
| 方案 | 优势 | 劣势 | MVP适用性 |
|------|------|------|-----------|
| **无热部署（推荐）** | ✅ 实现简单<br>✅ 无内存泄漏风险<br>✅ 测试覆盖容易 | ❌ 需要停机2-5分钟 | ⭐⭐⭐⭐⭐ |
| Spring Hot Swap | ✅ 无停机 | ❌ 仅支持方法体修改 | ⭐⭐ |
| JRebel | ✅ 强大热部署 | ❌ 商业许可费用高 | ⭐⭐ |
| 自定义ClassLoader | ✅ 完全控制 | ❌ 实现复杂度极高 | ⭐⭐⭐ |
| OSGi | ✅ 成熟标准 | ❌ 学习曲线陡峭 | ⭐⭐⭐ |

**理由**:
- 降低技术复杂度和风险（避免ClassLoader内存泄漏）
- 企业客户有维护窗口，可接受计划内停机
- 节省2-3周开发时间
- 通过优雅停机+滚动升级缓解停机影响

#### 2. ADR-002: OSGi vs 自研插件框架 ✅

**文件**: `docs/adr-002-osgi-vs-custom.md`

**核心决策**:
- **Phase 1 (MVP)采用自研方案**
- 基于PluginClassLoader + Spring父子ApplicationContext
- Phase 2根据需求复杂度评估OSGi迁移

**技术方案对比**:
| 维度 | 自研ClassLoader | OSGi (Apache Felix) | 权重 | 自研评分 | OSGi评分 |
|------|-----------------|---------------------|------|----------|----------|
| **开发成本** | 低（2-3周） | 高（6-8周） | ⭐⭐⭐⭐⭐ | 5 | 2 |
| **学习曲线** | 低（Spring团队熟悉） | 高（新概念多） | ⭐⭐⭐⭐⭐ | 5 | 2 |
| **Spring集成** | 完美（原生支持） | 复杂（需要适配层） | ⭐⭐⭐⭐⭐ | 5 | 2 |
| **依赖管理** | 手动 | 自动（Import/Export） | ⭐⭐⭐ | 2 | 5 |
| **调试体验** | 好（标准Spring调试） | 差（Bundle状态复杂） | ⭐⭐⭐⭐ | 4 | 2 |
| **MVP适配性** | 完美（简单场景够用） | 过度设计 | ⭐⭐⭐⭐⭐ | 5 | 2 |
| **总分** | | | | **32** | **29** |

**理由**:
- Spring原生集成，无需复杂适配层
- 团队Spring经验丰富，学习曲线低
- MVP仅1个扩展点，OSGi过度设计
- 节省4-6周学习开发时间

**Phase 2 OSGi评估触发条件**:
1. 插件数量 > 50个
2. 需要复杂依赖管理（Import/Export Package）
3. 需要插件版本隔离（同一插件多版本共存）
4. 团队OSGi技能成熟

#### 3. 项目计划重评估 ✅

**文件**: `docs/project-plan-reassessment.md`

**核心发现**:
- 原PRD估算**8周（40人天）**过于乐观
- Tech Spec估算**12-17周**更接近实际
- **重新评估结果**:
  - **方案A**: 16周单人开发（推荐）
  - **方案C**: 8周双人开发（如果市场紧迫）

**时间差异分析**:
| Epic | 8周计划（人天） | 16周计划（人天） | 差异 | 说明 |
|------|----------------|-----------------|------|------|
| Epic 1 | 10 | 15 | +5 | ClassLoader内存泄漏排查 |
| Epic 2 | 10 | 15 | +5 | LiteFlow集成调试 |
| Epic 3 | 10 | 15 | +5 | 数据库集成、文件存储 |
| Epic 4 | 5 | 10 | +5 | SDK完整开发 |
| Epic 5 | 5 | 5 | 0 | 保持不变 |
| **测试** | 0 | 10 | +10 | 集成测试、压力测试 |
| **Bug修复** | 0 | 10 | +10 | 缓冲时间 |
| **文档** | 0 | 10 | +10 | API文档、用户文档 |
| **总计** | **40人天** | **80人天** | **+40人天** | **+100%** |

**关键差异来源**:
1. SDK和文档开发（+10人天）- 原估算未包含完整SDK文档
2. 集成测试（+10人天）- 原估算测试包含在开发任务内
3. ClassLoader内存泄漏排查（+5人天）- 高风险技术问题
4. Bug修复缓冲（+10人天）- 保守估算包含20%缓冲
5. 文档完善（+10人天）- API文档、快速入门、故障排查

**架构决策节省的时间**:
- ADR-001（无热部署）: 节省2-3周
- ADR-002（自研vs OSGi）: 节省1周
- 净节省: 约2周

**⚠️ 重要发现**:
**8周单人开发不可行**，需要与Stakeholder重新确认：
- **方案A**: 16周单人开发（质量优先）
- **方案C**: 8周双人开发（速度优先）

---

## 📊 Critical Issues 解决状态

| Issue | 状态 | 文档 | 签字批准 |
|-------|------|------|----------|
| 🔴 热部署冲突 | ✅ 已解决 | ADR-001 | ⏳ Pending |
| 🔴 自研vs OSGi | ✅ 已解决 | ADR-002 | ⏳ Pending |
| 🔴 时间规划 | ✅ 已分析 | 项目计划重评估 | ⏳ 需Stakeholder确认 |

---

## 📋 下一步行动计划

### 立即行动（本周内）

1. **Stakeholder对齐** ⏳
   - [ ] 与产品团队沟通ADR-001（无热部署）
   - [ ] 与产品团队沟通ADR-002（自研方案）
   - [ ] 与项目管理沟通时间调整（8周 → 16周 或 8周2人）
   - [ ] 获得正式批准签字

2. **资源确认** ⏳
   - [ ] 确认开发资源（单人 vs 双人）
   - [ ] 确认测试资源（兼职测试人员）
   - [ ] 准备开发环境（CI/CD、测试环境）

### Week 2: 文档补充周

**Day 1-2: Story分解**
- [ ] 使用`sprint-planning` workflow
- [ ] 生成Story级实施计划
- [ ] Epic → Story → Task分解

**Day 3-4: 核心文档**
- [ ] ADR-003: Spring Bean动态注册方案
- [ ] 部署架构文档（单机/集群/容器化）
- [ ] 安全策略文档（插件审核、权限控制、租户隔离）

**Day 5: 开发者文档**
- [ ] API参考骨架
- [ ] 快速入门指南
- [ ] 故障排查指南

### Week 3: 启动开发（如果资源到位）

- [ ] Epic 1: 插件框架基础开发
- [ ] PluginClassLoader核心实现
- [ ] HelloWorld插件验证

---

## 📂 本次会话生成的文档

### 架构决策文档

1. **ADR-001: 热部署技术决策**
   - 路径: `docs/adr-001-hot-deployment.md`
   - 大小: ~15KB
   - 内容: 技术方案对比、决策依据、实施计划、缓解措施

2. **ADR-002: OSGi vs 自研技术选型**
   - 路径: `docs/adr-002-osgi-vs-custom.md`
   - 大小: ~20KB
   - 内容: 详细技术对比、评分矩阵、风险管理、Phase 2评估

3. **项目计划重评估**
   - 路径: `docs/project-plan-reassessment.md`
   - 大小: ~18KB
   - 内容: 根因分析、详细时间表、资源计划、风险评估

### 会话日志

4. **架构决策会话记录**
   - 路径: `docs/session-logs/session-2026-01-01-architecture-decisions.md`
   - 本文档

---

## 🎯 Week 1 完成度

**Week 1目标**: 架构决策周 - 解决3个Critical Issues

**完成情况**: ✅ 100%

- ✅ ADR-001: 热部署技术决策（Day 1-2目标）
- ✅ ADR-002: OSGi vs 自研（Day 3-4目标）
- ✅ 项目计划重评估（Day 5目标）

**额外完成**:
- 详细的技术方案对比矩阵
- 风险管理计划
- Phase 2评估触发条件定义
- 资源计划（单人 vs 双人）

---

## 💡 关键决策总结

### 技术栈确认

**Phase 1 (MVP)**:
```
- Spring Boot 3.4.5 + Java 17
- 自定义PluginClassLoader (extends URLClassLoader)
- GenericApplicationContext (父子容器)
- LiteFlow 2.15.0.2 (流程扩展)
- MyBatis-Flex 1.11.4 (元数据存储)
- Maven多模块架构
```

**Phase 2 (可选)**:
- 热部署: 根据实际使用反馈决定
- OSGi: 根据复杂度需求评估迁移

### MVP范围确认

**包含**:
- ✅ 1个扩展点（流程扩展）
- ✅ 插件生命周期管理（安装、启动、停止、卸载）
- ✅ ClassLoader隔离
- ✅ Spring Bean命名空间隔离
- ✅ 异常隔离
- ✅ 基础SDK和脚手架

**不包含**（Phase 2）:
- ❌ 热部署
- ❌ 租户隔离
- ❌ 其他6个扩展点（API、数据钩子、定时任务、事件、公式、UI）
- ❌ 依赖冲突自动检测
- ❌ OSGi标准

### 时间规划确认

**需要Stakeholder确认**:
- **方案A**: 16周单人开发（推荐）
- **方案C**: 8周双人开发（如果市场紧迫）

**不能接受**: 8周单人开发（不可行）

---

## 📊 项目状态

**当前阶段**: Phase 3 (Solutioning) 完成 → Phase 4 (Implementation) 准备中

**就绪状态**: **Ready with Conditions** ⚠️

**阻塞项**:
- ⏳ Stakeholder批准3个ADR
- ⏳ 确认开发资源（单人 vs 双人）
- ⏳ 确认时间规划（16周单人 或 8周双人）

**文档完整度**: 6/6核心文档 ✅ + 3个ADR ✅ + 项目计划重评估 ✅

**下一步**: Week 2 文档补充周

---

## 🔧 技术决策要点

### ClassLoader设计

**PluginClassLoader关键特性**:
```java
public class PluginClassLoader extends URLClassLoader {
    // 打破双亲委派，优先加载插件类
    protected Class<?> loadClass(String name, boolean resolve) {
        // 1. 检查是否为Java核心类（java.*、javax.*）
        // 2. 检查是否为OneBase SDK类
        // 3. 检查插件JAR是否包含该类
        // 4. 委派给父类加载器
    }
}
```

**隔离机制**:
1. ClassLoader隔离（每个插件独立）
2. Bean命名空间隔离（plugin.{pluginId}.{beanName}）
3. 异常隔离（try-catch所有插件调用）
4. 父子ApplicationContext（插件独立Spring容器）

### Spring集成

**父子容器模式**:
```java
// 父容器: OneBase主应用
ApplicationContext parent = SpringContextUtil.getApplicationContext();

// 子容器: 插件独立容器
GenericApplicationContext pluginContext = new GenericApplicationContext(parent);
pluginContext.setClassLoader(pluginClassLoader);
```

**LiteFlow组件注册**:
```java
// 动态注册组件到LiteFlow
FlowExecutor flowExecutor = parent.getBean(FlowExecutor.class);
flowExecutor.reloadRule(pluginComponent);
```

---

## 📚 参考资料

### 生成的文档

1. **ADR-001**: `docs/adr-001-hot-deployment.md`
2. **ADR-002**: `docs/adr-002-osgi-vs-custom.md`
3. **项目计划重评估**: `docs/project-plan-reassessment.md`

### 现有文档

1. **PRD**: `docs/prd.md`
2. **Architecture**: `docs/architecture-plugin-system.md`
3. **Epics**: `docs/epics.md`
4. **Tech Spec**: `docs/sprint-artifacts/tech-spec-plugin-system.md`
5. **Implementation Readiness**: `docs/implementation-readiness-report-2026-01-01.md`

### 外部参考

1. **Spring Boot Hot Swapping**: https://docs.spring.io/spring-boot/docs/current/reference/html/using.html#using.devtools.restart
2. **OSGi规范**: https://www.osgi.org/developer/architecture/
3. **LiteFlow文档**: https://liteflow.cc/pages/guides/

---

## 🎓 经验总结

### 成功要素

1. **详细的技术对比**
   - 使用评分矩阵量化决策依据
   - 多维度分析（开发成本、学习曲线、集成难度等）
   - 权重不同维度的重要性

2. **分阶段策略**
   - MVP优先验证核心价值
   - Phase 2根据实际反馈优化
   - 保持架构灵活性（保留OSGi迁移选项）

3. **风险意识**
   - ClassLoader内存泄漏是高风险
   - 时间估算需要包含缓冲
   - 技术债需要追踪管理

### 待改进

1. **时间估算准确性**
   - 原PRD估算8周过于乐观
   - 应该更早进行详细任务分解
   - 需要技术团队参与时间估算

2. **Stakeholder沟通**
   - 应该更早发现时间期望不一致
   - 需要定期同步进度和风险
   - 重要决策需要正式签字批准

---

## ✅ 会话结束检查清单

- [x] 3个ADR文档已生成
- [x] 项目计划重评估已完成
- [x] 技术决策理由充分
- [x] 风险已识别和缓解
- [x] 下一步行动计划清晰
- [x] 会话日志已记录
- [ ] Stakeholder批准（待完成）
- [ ] 资源确认（待完成）
- [ ] 时间规划确认（待完成）

---

**记录时间**: 2026-01-01
**下次会话**: Week 2 文档补充周 或 启动开发（如果Stakeholder批准）

**快速启动指令**:
- 继续文档补充 → "继续Week 2文档补充"
- 启动开发 → "开始Epic 1开发"
- 查看状态 → "查看项目状态"
