# 连接器环境配置功能实现进度记录

**时间**: 2026-01-23 17:30
**状态**: 调试中 - 环境配置页面显示空白

---

## 当前目标

实现连接器创建向导中的环境配置页面，支持：
1. **创建环境信息模式**: 用户可以输入环境名称、URL 和认证类型
2. **选择已有环境信息模式**: 从现有环境列表中选择（当前禁用，待后端 API 就绪）
3. **动态表单渲染**: 基于 Formily v2 schema 渲染表单字段
4. **认证类型选择**: 支持 7 种认证类型（无认证、Basic、Bearer、API Key、OAuth 2.0、自定义、AWS签名）

---

## 已完成的工作

### Task 1-6: 核心功能实现 ✅

1. **Store 增强** ([`store.ts:34-39`](apps/app-builder/src/pages/CreateApp/pages/IntegratedManagement/pages/connector/createWizard/store.ts))
   - 添加 `envList` 状态管理
   - 实现 `fetchEnvList` action（使用 mock 数据）

2. **DynamicConfigStep 增强** ([`DynamicConfigStep.tsx:37-75`](apps/app-builder/src/pages/CreateApp/pages/IntegratedManagement/pages/connector/createWizard/steps/DynamicConfigStep.tsx))
   - 监听 `envMode` 字段变化
   - 动态加载环境列表
   - 更新 `existingEnvId` 下拉框选项
   - 管理加载状态

3. **环境列表 API 服务** ([`env.ts`](packages/app/src/services/env.ts))
   - 创建 `getEnvList` API 服务
   - 导出到 `@onebase/app` 包

4. **表单数据实时同步** ([`DynamicConfigStep.tsx:27-32`](apps/app-builder/src/pages/CreateApp/pages/IntegratedManagement/pages/connector/createWizard/steps/DynamicConfigStep.tsx))
   - 使用 `onFormValuesChange` 同步表单数据到 store

5. **加载状态优化** ([`DynamicConfigStep.tsx:66-75`](apps/app-builder/src/pages/CreateApp/pages/IntegratedManagement/pages/connector/createWizard/steps/DynamicConfigStep.tsx))
   - 添加 `isEnvListLoading` 状态
   - 设置字段加载状态

### Task 7: Mock Schema 实现 ✅

**文件**: [`store.ts:116-212`](apps/app-builder/src/pages/CreateApp/pages/IntegratedManagement/pages/connector/createWizard/store.ts)

添加了完整的 mock `conn_config` schema，包含：
- `envMode`: 环境信息单选按钮组（创建/选择）
- `existingEnvId`: 环境选择下拉框（已禁用）
- `envName`: 环境名称输入框
- `url`: URL 输入框
- `authType`: 认证类型选择器

---

## 当前问题

### 问题：环境配置页面显示空白 ❌

**现象**: 用户点击"下一步"进入环境配置步骤时，页面显示为空白

**已尝试的修复**:

1. **修复 Schema 覆盖问题** ([`BasicInfoStep.tsx:15`](apps/app-builder/src/pages/CreateApp/pages/IntegratedManagement/pages/connector/createWizard/steps/BasicInfoStep.tsx))
   - 添加 `!schemas.conn_config` 检查，防止重复调用 `fetchSchemas`
   - 提交: `ec7bc160a`

2. **修复 Schema 验证逻辑** ([`store.ts:100-113`](apps/app-builder/src/pages/CreateApp/pages/IntegratedManagement/pages/connector/createWizard/store.ts))
   - 添加 schema 有效性检查：`Object.keys(res.conn_config).length > 0`
   - 只有当后端返回有效 schema 时才使用，否则使用 mock schema
   - 提交: `3bc7d5218`

**问题状态**: 仍未解决，页面仍然空白

---

## 下一步排查方向

### 1. 检查浏览器控制台错误
需要查看：
- JavaScript 错误
- React 组件渲染错误
- Formily schema 解析错误

### 2. 添加调试日志
在以下位置添加 console.log：
- `fetchSchemas` 调用后的 schema 状态
- `DynamicConfigStep` 组件的 schema 值
- Formily 表单创建过程

### 3. 检查 Formily 组件映射
确认 `componentMap` 中是否包含所有需要的组件：
- `Radio.Group`
- `Select`
- `Input`

### 4. 验证 Schema 格式
确认 mock schema 是否符合 Formily v2 规范：
- JSON 结构是否正确
- `x-decorator` 和 `x-component` 映射是否存在
- `x-reactions` 语法是否正确

### 5. 检查组件渲染条件
在 `DynamicConfigStep.tsx:102-110` 中：
```typescript
if (ui.isLoading || !schema) {
  return <Spin loading={true} tip="加载配置中..." />
}
```
检查 `schema` 是否为 `null` 或 `undefined`

---

## 技术栈

- **React 18** + TypeScript
- **Zustand** (状态管理 + persist)
- **Formily v2** (`@formily/core`, `@formily/react`)
- **Arco Design** (UI 组件库)
- **React Router v6**

---

## 关键文件位置

### Store
- `apps/app-builder/src/pages/CreateApp/pages/IntegratedManagement/pages/connector/createWizard/store.ts`

### 组件
- `apps/app-builder/src/pages/CreateApp/pages/IntegratedManagement/pages/connector/createWizard/index.tsx`
- `apps/app-builder/src/pages/CreateApp/pages/IntegratedManagement/pages/connector/createWizard/steps/BasicInfoStep.tsx`
- `apps/app-builder/src/pages/CreateApp/pages/IntegratedManagement/pages/connector/createWizard/steps/DynamicConfigStep.tsx`

### API 服务
- `packages/app/src/services/env.ts`
- `packages/app/src/services/index.ts`

### 计划文档
- `docs/plans/2026-01-23-connector-env-config-implementation.md`

---

## Git 提交历史

```bash
3bc7d5218 - fix: check for valid schema before setting, use mock when backend returns empty schema
ec7bc160a - fix: prevent schema override in BasicInfoStep
632bb1582 - feat: add mock conn_config schema for development testing
4735 - Implemented mock environment list data for connector wizard
4727 - Loading State Added to Environment Selection Dropdown
4721 - Implemented environment list fetching in connector wizard store
4717 - Added environment list API service
4715 - Created environment list API service
4724 - Added isEnvListLoading state to DynamicConfigStep component
4720 - Environment list API integration added to connector wizard store
4706 - DynamicConfigStep component specification issues fixed
4704 - Fixed DynamicConfigStep component code standards
4703 - 修复动态配置步骤表单值变化监听和依赖优化
```

---

## 待完成任务（根据原计划）

- [ ] **Task 8**: 端到端测试和验证（当前正在进行）
- [ ] **Task 9**: 代码审查和优化
- [ ] **Task 10**: 文档更新

---

## 调试命令

```bash
# 启动开发服务器
pnpm --filter ./apps/app-builder dev

# 类型检查
pnpm --filter ./apps/app-builder type-check

# 清除本地存储（用于重置 wizard 状态）
# 浏览器控制台执行：
localStorage.removeItem('connector-wizard-storage')
```

---

## 用户反馈历史

1. "提醒后端还没有开发getEnvList，请先进行mock"
   - 解决：添加 mock 环境列表数据

2. "虽然没有环境配置的信息，但是新建环境配置应该要具备，如图中，选择已有环境信息标灰，但是创建环境信息应该是正常的"
   - 尝试：添加 mock schema，禁用 existingEnvId 字段

3. "页面没有正常显示"（当前问题）
   - 尝试修复 1：防止 schema 被覆盖
   - 尝试修复 2：添加 schema 有效性检查
   - 状态：仍未解决

---

## 下次会话建议

1. **首先检查浏览器控制台**
   - 打开开发者工具
   - 查看 Console 标签页的错误信息
   - 截图或复制错误消息

2. **添加调试日志**
   - 在 `fetchSchemas` 成功后打印 schema
   - 在 `DynamicConfigStep` 渲染前打印 schema 值
   - 检查 `ui.isLoading` 状态

3. **简化测试**
   - 创建最小可复现示例
   - 确认 Formily 组件映射是否正确
   - 验证 schema 格式

4. **参考文档**
   - Formily v2 官方文档: https://formilyjs.org/
   - 项目中的 schema 示例: `docs/connector-env-config-schema.json`

---

**最后更新**: 2026-01-23 17:30
**问题状态**: 🔴 调试中
**优先级**: 高 - 阻塞用户测试
