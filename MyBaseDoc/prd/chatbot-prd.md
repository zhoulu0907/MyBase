# Chatbot 工作台产品��求文档

**版本**：v2.0.0
**日期**：2026-04-25
**状态**：草稿
**作者**：产品团队

**版本历史**：
- v1.0.0 (2026-04-25)：初始版本
- v2.0.0 (2026-04-25)：补充 V2.0 需求 - LLM 配置功能

---

## 1. 文档概述

### 1.1 背景

随着 AI 技术在企业场景的深入应用，聊天机器人已成为提升客户服务效率和用户体验的重要工具。当前系统需要构建一个统一的 Chatbot 工作台，支持多 LLM Provider 切换和流式输出，以满足不同业务场景的需求。

### 1.2 目标

- 构建一套前后端分离的 Chatbot 工作台，支持流式输出
- ���现多 LLM Provider 的灵活切换与代理路由
- 确保租户数据隔离，保障 API 密钥安全
- 提供完整的联调验证流程

### 1.3 范围

| 涉及模块 | 说明 |
|---------|------|
| 前端工作台 | React + Vite pnpm monorepo 中的 Chatbot 组件 |
| 后端代理服务 | Spring Boot 3.4.5 + JDK 17 多模块 Maven 项目 |
| AI 路由层 | 第三方 LLM 接口代理与流式转发 |
| 安全模块 | 租户隔离、SM2 加密、频控、API 密钥管理 |

---

## 2. 用户场景与使用流程

### 2.1 主要用户角色

| 角色 | 职责 |
|-----|------|
| 终端用户 | 通过工作台与 AI 机器人对话，获取信息与服务 |
| 租户管理员 | 配置 AI Provider、管理 API 密钥、设置频控策略 |
| 开发人员 | 集成 Chatbot 组件，联调后端接口 |

### 2.2 核心使用流程

```
┌─────────────────────────────────────────────────────────────────┐
│                        用户对话流程                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  1. 用户登录 → 2. 进入 Chatbot 工作台                            │
│         ↓                                                        │
│  3. 选择/切换 LLM Provider                                       │
│         ↓                                                        │
│  4. 输入消息 → 5. 发送请求                                       │
│         ↓                                                        │
│  6. 后端接收请求 → 7. 鉴权/频控检查 → 8. 代理转发至 LLM           ��
│         ↓                                                        │
│  9. SSE 流式返回 → 10. 前端渲染 Markdown 消息                    │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 2.3 典型场景

**场景一：客户服务对话**
- 用户输入咨询问题
- 系统根据租户配置的 Provider 生成回复
- 支持流式输出，边生成边展示

**场景二：多 Provider 切换**
- 租户管理员配置多个 LLM Provider
- 用户可在工作台切换不同 Provider 对比效果

**场景三：管理员配置**
- 租户管理员新增/编辑 API 密钥
- 设置调用频控阈值

---

## 3. 功能需求详细描述

### 3.1 前端功能

| 功能编号 | 功能名称 | 优先级 | 描述 |
|---------|---------|-------|------|
| F-001 | 消息输入 | P0 | 支持文本输入，支持 Enter 发送、Shift+Enter 换行 |
| F-002 | Markdown 渲染 | P0 | 支持 Markdown 语法解析与展示，包括代码高亮 |
| F-003 | 流式消息展示 | P0 | 实时渲染 SSE 流式输出内容，逐字/逐行展示 |
| F-004 | Provider 切换 | P1 | 下拉选择已配置的 LLM Provider |
| F-005 | 对话历史 | P1 | 展示当前会话的消息列表，支持滚动 |
| F-006 | 消息清空 | P2 | 一键清空当前对话记录 |
| F-007 | 加载状态 | P1 | 发送消息时显示 loading 状态 |

### 3.2 后端功能

| 功能编号 | 功能名称 | 优先级 | 描述 |
|---------|---------|-------|------|
| B-001 | Chat 接口 | P0 | POST /chat，接收消息并返回 SSE 流式响应 |
| B-002 | Provider 代理 | P0 | 代理转发请求至配置的第三方 LLM |
| B-003 | 租户隔离 | P0 | 基于租户 ID 的数据隔离 |
| B-004 | API 密钥管理 | P1 | 密钥的 SM2 加密存储与动态解密 |
| B-005 | 频控 | P1 | 基于租户/用户的调用频率限制 |
| B-006 | Provider 配置查询 | P2 | GET /chat/providers，获取可用 Provider 列表 |

### 3.3 V2.0 新增功能（LLM 配置）

| 功能编号 | 功能名称 | 优先级 | 描述 |
|---------|---------|-------|------|
| V2-F001 | LLM 配置分组 | P0 | 在属性面板中添加"LLM 配置"折叠栏 |
| V2-F002 | Provider 选择 | P0 | 下拉选择 LLM 供应商（OpenAI, DeepSeek, Anthropic, 本地私有模型） |
| V2-F003 | 模型名称配置 | P0 | 输入框配置模型名称，默认 gpt-4o / deepseek-chat |
| V2-F004 | API Key 配置 | P0 | 密码输入框，支持一键复制和脱敏显示，标记为敏感信息 |
| V2-F005 | 系统提示词配置 | P1 | 多行文本��配置系统提示词 |
| V2-F006 | 配置双向绑定 | P0 | 属性面板修改配置实时更新组件 props |
| V2-F007 | 配置持久化 | P0 | 切换组件后再切回，配置内容保持存在 |
| V2-F008 | API Key 空状态提示 | P0 | apiKey 为空时显示"请先配置 API Key"提示 |
| V2-F009 | 自定义 BaseUrl | P1 | 支持配置私有模型或代理服务器地址 |

---

## 4. 前端组件需求规格

### 4.1 技术栈

| 技术 | 版本 | 用途 |
|-----|------|-----|
| React | 18.x | UI 框架 |
| Vite | 5.x | 构建工具 |
| pnpm | 8.x | 包管理器 |
| Arco Design | 2.x | UI 组件库 |
| @preact/signals-react | 1.x | 状态管理 |
| react-markdown | 9.x | Markdown 解析 |
| react-syntax-highlighter | 15.x | 代码高亮 |

### 4.2 项目结构（monorepo）

```
packages/
└── components/
    └── chatbot/
        ├── src/
        │   ├── ChatbotWorkbench.tsx      # 主组件
        │   ├── ChatMessage.tsx            # 消息项组件
        │   ├── ChatInput.tsx              # 输入框组件
        │   ├── ProviderSelector.tsx       # Provider 选择器
        │   ├── hooks/
        │   │   ├── useChat.ts             # 聊天逻辑 hook
        │   │   └── useStream.ts           # 流式读取 hook
        │   └── index.ts
        ├── package.json
        └── tsconfig.json
```

### 4.3 组件 API

**ChatbotWorkbench 组件属性**

| 属性 | 类型 | 必填 | 说明 |
|-----|------|-----|------|
| className | string | 否 | 自定义样式类名 |
| placeholder | string | 否 | 输入框占位文本，默认"输入消息..." |
| providers | Provider[] | 是 | 可用的 LLM Provider 列表 |
| defaultProvider | string | 否 | 默认选中的 Provider ID |
| onSend | (message: string, providerId: string) => void | 是 | 发送消息回调 |
| onProviderChange | (providerId: string) => void | 否 | Provider 切换回调 |

**Provider 数据结构**

```typescript
interface Provider {
  id: string;
  name: string;
  logo?: string;
  endpoint: string;
}
```

**Message 数据结构**

```typescript
interface Message {
  id: string;
  role: 'user' | 'assistant';
  content: string;
  timestamp: number;
  status?: 'loading' | 'error';
}
```

### 4.4 状态管理（@preact/signals-react）

| Signal | 类型 | 用途 |
|--------|-----|------|
| messages | Signal<Message[]> | 当前对话消息列表 |
| currentProvider | Signal<string> | 当前选中的 Provider ID |
| isLoading | Signal<boolean> | 发送中状态 |
| inputValue | Signal<string> | 输入框内容 |

### 4.5 流式输出实现

- 使用 EventSource 或 fetch + ReadableStream 读取 SSE
- 每收到一个数据块，更新对应 Message 的 content 字段
- 使用 `marked` 或 `react-markdown` 实时渲染 Markdown

### 4.6 构建检查

| 检查项 | 工具 | 说明 |
|-------|-----|------|
| TypeScript 类型检查 | tsc --noEmit | 编译前类型校验 |
| ESLint | eslint | 代码规范检查 |
| Prettier | prettier --check | 代码格式化检查 |

---

## 5. 后端接口需求规格

### 5.1 技术栈

| 技术 | 版本 | 用途 |
|-----|------|-----|
| Spring Boot | 3.4.5 | Web 框架 |
| JDK | 17 | 运行环境 |
| Maven | 3.9.x | 构建工具 |

### 5.2 项目结构（多模块）

```
myob-chatbot/
├── chatboot-core/               # 核心模块
│   ├── src/main/java/
│   └── pom.xml
├── chatbot-api/                 # API 模块
│   ├── src/main/java/
│   │   └── com/myob/chatbot/api/
│   │       ├── controller/
│   │       │   └── ChatController.java
│   │       ├── service/
│   │       └── dto/
│   └── src/main/resources/
│       └── application.yml
├── chatbot-security/            # 安全模块
│   ├── src/main/java/
│   └── pom.xml
└── pom.xml                      # 父 pom
```

### 5.3 接口详情

#### 5.3.1 POST /chat

**功能**：发送聊天消息，接收流式响应

**请求头**

| 头信息 | 必填 | 说明 |
|-------|-----|------|
| Content-Type | 是 | application/json |
| Authorization | 是 | Bearer {token}，包含租户信息 |
| X-Tenant-Id | 是 | 租户 ID |

**请求体**

```json
{
  "providerId": "string",
  "message": "string",
  "stream": true
}
```

| 字段 | 类型 | 必填 | 说明 |
|-----|------|-----|------|
| providerId | string | 是 | LLM Provider ID |
| message | string | 是 | 用户消息内容，最大 4000 字符 |
| stream | boolean | 否 | 是否流式返回，默认 true |

**响应**：SSE 流式输出

```
Content-Type: text/event-stream
Cache-Control: no-cache
Connection: keep-alive

event: message
data: {"content": "你好", "done": false}

event: message
data: {"content": "，有什么", "done": false}

event: done
data: {"content": "可以帮助你的吗？", "done": true}
```

**错误响应**

| HTTP 状态码 | 错误码 | 说明 |
|------------|-------|------|
| 400 | INVALID_REQUEST | 请求参数错误 |
| 401 | UNAUTHORIZED | 未授权或 token 失效 |
| 403 | TENANT_FORBIDDEN | 租户无权限访问该 Provider |
| 429 | RATE_LIMITED | 请求频率超限 |
| 500 | INTERNAL_ERROR | 服务器内部错误 |
| 502 | PROVIDER_ERROR | 第三方 LLM 服务异常 |

#### 5.3.2 GET /chat/providers

**功能**：获取当前租户可用的 Provider 列表

**请求头**

| 头信息 | 必填 | 说明 |
|-------|-----|------|
| Authorization | 是 | Bearer {token} |
| X-Tenant-Id | 是 | 租户 ID |

**响应**

```json
{
  "code": 0,
  "data": [
    {
      "id": "provider-1",
      "name": "OpenAI GPT-4",
      "logo": "https://xxx/logo.png",
      "endpoint": "/api/v1/chat/completions"
    }
  ]
}
```

### 5.4 租户隔离

- 所有数据查询必须包含 tenant_id 条件
- 使用 Spring Boot 拦截器统一注入租户上下文
- API 响应数据需校验租户归属

### 5.5 API 密钥管理

| 环节 | 实现方式 |
|-----|---------|
| 存储 | SM2 加密后存入数据库 |
| 解密 | 使用租户私钥动态解密，仅在调用 LLM 时解密 |
| 传输 | HTTPS 全链路加密 |
| 日志 | 屏蔽敏感信息，密钥字段脱敏展示 |

### 5.6 频控策略

| 维度 | 限制 | 窗口 |
|-----|------|-----|
| 租户级别 | 1000 次/分钟 | 按分钟统计 |
| 用户级别 | 60 次/分钟 | 按分钟统计 |
| 单 IP | 100 次/分钟 | 按分钟统计 |

- 使用 Redis 实现分布式计数
- 超限返回 HTTP 429

---

## 6. 非功能性需求

### 6.1 性能

| 指标 | 目标值 | 说明 |
|-----|-------|------|
| 首字延迟 | < 1s | 用户发起请求到收到首字的间隔 |
| TP99 延迟 | < 5s | 99 分位响应延迟 |
| 并发数 | >= 500 | 单机支持的最大并发连接数 |
| 吞吐量 | >= 100 QPS | 单机最大查询吞吐量 |

### 6.2 安全

| 安全项 | 要求 |
|-------|-----|
| 身份认证 | JWT Token，24 小时有效期 |
| 租户隔离 | 数据层强制隔离，不可跨租户访问 |
| 密钥管理 | SM2 加密存储，动态解密 |
| 频控 | 多维度频率控制，防止滥用 |
| 审计日志 | 记录所有 API 调用，包含租户、时间、IP |
| 敏感信息 | 禁止在日志中打印密钥和用户隐私数据 |

### 6.3 兼容性

| 兼容项 | 要求 |
|-------|-----|
| 浏览器 | Chrome >= 90, Firefox >= 88, Safari >= 14, Edge >= 90 |
| 移动端 | iOS Safari >= 14, Android Chrome >= 90 |
| 后端 | Spring Boot 3.4.5，JDK 17 |
| 前端 | React 18.x, Vite 5.x |

### 6.4 可靠性

| 指标 | 目标 |
|-----|-----|
| 可用性 | >= 99.9% |
| 错误率 | < 0.1% |
| 故障恢复 | < 30 分钟 |

---

## 7. 验收标准

### 7.1 前端验收

| 验收项 | 通过条件 |
|-------|---------|
| 组件渲染 | ChatbotWorkbench 组件正常挂载，无 console error |
| 消息发送 | 输入消息后点击发送，触发 onSend 回调 |
| Markdown 渲染 | 包含代码块的响应能正确高亮显示 |
| 流式输出 | SSE 响应能逐字/逐行追加显示 |
| Provider 切换 | 切换后下次请求使用新 Provider |
| 类型检查 | tsc --noEmit 无错误 |
| ESLint | eslint 检查无 error |

### 7.2 后端验收

| 验收项 | 通过条件 |
|-------|---------|
| 接口文档 | Swagger/OpenAPI 文档完整，无缺失字段 |
| 流式响应 | POST /chat 返回标准 SSE 格式 |
| 租户隔离 | 跨租户请求返回 403，数据不泄露 |
| 频控 | 超出限制返回 429，包含 Retry-After 头 |
| 密钥解密 | 实际调用 LLM 时能正确解密密钥 |
| SM2 加密 | 相同内容加密结果不同（随机向量） |

### 7.3 联调验收

| 验收项 | 通过条件 |
|-------|---------|
| 前后端联调 | 前端发送消息，后端流式返回，前端正确展示 |
| 错误处理 | LLM 返回错误时，前端显示友好错误提示 |
| 异常恢复 | 网络中断后重试能正常工作 |

### 7.4 编辑器检查

- [ ] VS Code Settings Sync 正常
- [ ] ESLint/Prettier 插件生效
- [ ] TypeScript 智能提示正常

---

## 8. 风险评估与应对

### 8.1 风险矩阵

| 风险项 | 可能性 | 影响 | 等级 | 应对策略 |
|-------|-------|-----|-----|---------|
| 第三方 LLM 服务不可用 | 中 | 高 | 高 | 接入多个 Provider，支持快速切换；本地做异常捕获与重试 |
| 流式输出中断 | 中 | 中 | 中 | 前端做断线重连；后端设置超时断开 |
| 租户数据泄露 | 低 | 极高 | 高 | 数据层��制租户隔离审计；上线前安全测试 |
| 高并发性能瓶颈 | 中 | 中 | 中 | 水平扩展；引入消息队列；降级策略 |
| 密钥泄露 | 低 | 极高 | 高 | SM2 加密；最小权限原则；日志脱敏 |

### 8.2 降级策略

| 场景 | 降级方案 |
|-----|---------|
| LLM 服务超时 | 返回友好提示"AI 服务响应超时，请稍后重试" |
| 频控触发 | 返回"请求过于频繁，请稍后再试" |
| 系统过载 | 返回 503 Service Unavailable |

---

## 9. 技术选型说明

### 9.1 前端技术选型

| 技术 | 选型 | 理由 |
|-----|------|-----|
| 状态管理 | @preact/signals-react | 轻量级、响应式、对 React 友好，适合流式更新场景 |
| UI 组件库 | Arco Design | 蚂蚁出品，企业级设计规范，组件丰富 |
| Markdown | react-markdown + react-syntax-highlighter | 成熟方案，支持 GFM 和代码高亮 |

### 9.2 后端技术选型

| 技术 | 选型 | 理由 |
|-----|------|-----|
| 加密算法 | SM2 | 国密算法，满足合规要求 |
| 流式响应 | SSE | 轻量级、低延迟、易于前端处理，相比 WebSocket 更适合简单场景 |
| 频控 | Redis | 高性能、分布式支持、原子计数 |

### 9.3 架构决策

| 决策点 | 选型 | 备选方案 |
|-------|------|---------|
| 前后端通信 | SSE 流式 | WebSocket、轮询 |
| 状态传播 | Signal | Redux、Zustand、Context API |
| API 文档 | Swagger/OpenAPI | SpringDoc |

---

## 10. 开发任务分解

### 10.1 前端任务

| 任务 | 负责人 | 预估工时 | 依赖 |
|-----|-------|---------|-----|
| T-F01：搭建 Chatbot 组件工程结构 | 前端团队 | 0.5d | - |
| T-F02：实现 ChatbotWorkbench 主组件 | 前端团队 | 1d | T-F01 |
| T-F03：实现 ChatInput 输入框组件 | 前端团队 | 0.5d | T-F01 |
| T-F04：实现 ChatMessage 消息渲染组件 | 前端团队 | 0.5d | T-F01 |
| T-F05：实现 ProviderSelector 选择器组件 | 前端团队 | 0.5d | T-F01 |
| T-F06：实现 useChat 聊天逻辑 hook | 前端团队 | 1d | T-F02, T-F03, T-F04 |
| T-F07：实现 useStream 流式读取 hook | 前端团队 | 1d | T-F02 |
| T-F08：集成 Markdown 渲染与代码高亮 | 前端团队 | 0.5d | T-F04 |
| T-F09：配置 ESLint + Prettier + TypeScript | 前端团队 | 0.5d | T-F01 |
| T-F10：编写组件单元测试 | 前端团队 | 1d | T-F02~T-F08 |

### 10.2 后端任务

| 任务 | 负责人 | 预估工时 | 依赖 |
|-----|-------|---------|-----|
| T-B01：搭建多模块 Maven 项目结构 | 后端团队 | 0.5d | - |
| T-B02：实现 ChatController 流式接口 | 后端团队 | 1d | T-B01 |
| T-B03：实现 LLM Provider 代理服务 | 后端团队 | 1.5d | T-B01 |
| T-B04：实现租户隔离拦截器 | 后端团队 | 1d | T-B01 |
| T-B05：实现 SM2 加密解密工具 | 后端团队 | 1d | T-B01 |
| T-B06：实现 Redis 频控组件 | 后端团队 | 0.5d | T-B01 |
| T-B07：实现 GET /chat/providers 接口 | 后端团队 | 0.5d | T-B02 |
| T-B08：配置 Swagger/OpenAPI 文档 | 后端团队 | 0.5d | T-B02 |
| T-B09：编写接口单元测试 | 后端团队 | 1d | T-B02~T-B07 |

### 10.3 联调任务

| 任务 | 负责人 | 预估工时 | 依赖 |
|-----|-------|---------|-----|
| T-I01：前后端接口联调 | 全栈 | 1d | T-F10, T-B09 |
| T-I02：流式输出专项测试 | 全栈 | 0.5d | T-I01 |
| T-I03：租户隔离验证 | 安全测试 | 0.5d | T-I01 |
| T-I04：频控功能验证 | 全栈 | 0.5d | T-B06 |
| T-I05：异常场景联调 | 全栈 | 0.5d | T-I01 |
| T-I06：安全扫描与修复 | 安全测试 | 1d | T-I01 |

### 10.4 里程碑

| 里程碑 | 日期 | 交付物 |
|-------|-----|-------|
| M1：工程就绪 | +2d | 前端项目结构、后端多模块结构、代码规范配置 |
| M2：核心功能完成 | +7d | 前端组件、后端接口、基础联调 |
| M3：联调完成 | +10d | 完整功能联调、异常处理、文档完善 |
| M4：测试验收 | +12d | 单元测试、安全测试、验收报告 |

---

## 附录

### 附录 A：术语表

| 术语 | 说明 |
|-----|-----|
| SSE | Server-Sent Events，服务器推送事件 |
| LLM | Large Language Model，大语言模型 |
| Provider | AI 服务提供商（如 OpenAI、Claude 等） |
| SM2 | 国家密码管理局发布的椭圆曲线公钥密码算法 |
| 频控 | 频率控制，对 API 调用频率进行限制 |

### 附录 B：参考文档

- Spring Boot 3.4 官方文档
- React 18 官方文档
- Arco Design 组件库文档
- @preact/signals-react 官方文档

---

## 附录 C：前端数据结构定义

### C.1 核心数据类型

```typescript
// 消息角色类型
export type MessageRole = 'user' | 'assistant' | 'system';

// 消息结构
export interface Message {
  id: string;
  role: MessageRole;
  content: string;
  timestamp: number;
}

// 组件配置属性
export interface ChatbotProps {
  provider: 'OpenAI' | 'DeepSeek' | 'Anthropic';
  model: string;
  apiKey: string;
  systemPrompt?: string;
}

// SSE 流式数据块
export interface StreamChunk {
  delta: string;
  done: boolean;
}

// 聊天请求
export interface ChatRequest {
  provider: string;
  model: string;
  apiKey: string;
  systemPrompt: string;
  messages: Array<{ role: string; content: string }>;
  stream: boolean;
}

// 聊天响应
export interface ChatResponse {
  code: number;
  data: {
    content: string;
    done: boolean;
  };
}
```

### C.2 Signals 状态管理

| Signal | 类型 | 用途 |
|--------|-----|------|
| messagesSignal | Signal<Message[]> | 当前对话消息列表 |
| inputValueSignal | Signal<string> | 输入框内容 |
| isLoadingSignal | Signal<boolean> | 发送中状态 |
| chatMessages | computed<Message[]> | 计算属性，过滤 system 消息 |

### C.3 技术要点

| 要点 | 说明 |
|------|------|
| **Signals 响应式** | 组件内必须调用 `useSignals()` Hook，否则信号变化不会触发重渲染 |
| **流式渲染** | 使用 `appendToMessage` 增量更新，避免整体重渲染 |
| **SSE 解析** | 必须处理 `data: [DONE]` 终止标记和 JSON 解析异常 |
| **租户隔离** | 通过 `httpClient` 自动注入 `X-Tenant-Id`，确保多租户安全 |

### C.4 前端潜在风险

| 风险 | 说明 | 应对方案 |
|------|------|---------|
| 流式中断 | 网络中断时 SSE 连接可能中途断开 | 添加重连机制或状态提示 |
| 大消息体性能 | 消息列表过长时考虑虚拟滚动 | 设置消息数量上限（如 100 条） |
| XSS 风险 | 用户输入的 Markdown 内容需经 sanitize | 防止恶意脚本注入 |
| API Key 安全 | apiKey 仅用于设计态配置 | 生产环境应走后端配置 |

---

## 附录 D：V2.0 LLM 配置需求详细说明

### D.1 需求背景

当前 AI 对话助手（XChatbotAgent）组件已完成 UI 骨架开发并注册至 ui-kit，但在 app-builder 的右侧属性面板中缺少 LLM 相关的配置项，导致组件无法获取 API Key 进行实际对话。

### D.2 配置项定义

| 配置项 | 组件类型 | 默认值 | 说明 |
|-------|---------|-------|------|
| provider | Select | OpenAI | LLM 供应商 |
| model | Input | gpt-4o | 模型名称 |
| apiKey | Input.Password | - | API 密钥（敏感信息） |
| systemPrompt | TextArea | - | 系统提示词 |
| baseUrl | Input | https://api.openai.com | API 地址（支持私有模型） |

### D.3 Provider 选项

| Provider | BaseUrl | 说明 |
|----------|---------|------|
| OpenAI | https://api.openai.com | OpenAI GPT 系列 |
| DeepSeek | https://api.deepseek.com | 深度求索大模型 |
| Anthropic | https://api.anthropic.com | Anthropic Claude 系列 |
| Custom | 用户自定义 | 本地私有模型或代理服务器 |

### D.4 配置界面要求

1. **分组标题**："LLM 配置"
2. **折叠行为**：与其他配置分组（如"样式库"、"标题配置"）保持一致
3. **样式规范**：遵循 Arco Design 的面板规范

### D.5 双向绑定要求

1. 在右侧面板修改配置时，通过 onChange 事件实时更新组件的 props 或 config 对象
2. 切换其他组件再切回，输入的 API Key 内容必须依然存在（持久化）
3. 配置修改后立即生效，无需保存

### D.6 空状态处理

| 状态 | 组件行为 |
|-----|---------|
| apiKey 为空 | 显示"请先配置 API Key"提示，保持当前提示状态 |
| apiKey 已配置 | 切换为正常聊天输入状态 |

### D.7 验证要点（Definition of Done）

| 验证项 | 通过条件 |
|-------|---------|
| 配置可见性 | 选中画布中的 AI 助手后，右侧面板必须出现"LLM 配置"折叠栏 |
| 数据一致性 | 在面板输入 API Key 后，切换其他组件再切回，输入的内容必须依然存在 |
| 代码合规 | 属性定义的 key 必须与后端 ChatController 接收的字段名（provider, model, apiKey, baseUrl）完全对应 |
| 安全合规 | apiKey 必须标记为敏感信息，不在日志中打印 |

---

**文档结束**