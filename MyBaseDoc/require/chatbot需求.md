一、 前端工程需求描述 (Claude Code Friendly)

上下文上下文 (Context): 这是一个基于 React + Vite 的 pnpm monorepo 项目。

    Task: 在 packages/ui-kit 中开发一个 Chatbot 工作台组件，并注册到低代码平台。

    1. 组件实现:

        在 packages/ui-kit/src/components/Workbench/Chatbot 下创建组件。

        UI 使用 Arco Design，包含：消息流展示区（支持 Markdown）、底部输入框、发送按钮。

        状态管理: 必须使用 @preact/signals-react 管理 messages 列表，实现流式文本的局部响应式更新，避免大面积 Re-render。

    2. 平台注册:

        在 packages/ui-kit/src/index.ts 中调用 registerComponent。

        Material 配置 (Schema):

            provider: 下拉选框 (OpenAI, DeepSeek, Anthropic)。

            model: 输入框 (如 gpt-4o)。

            apiKey: 密码输入框 (注意：仅用于设计态配置)。

            systemPrompt: 文本域。

    3. 数据交互:

        严禁直连外部 LLM，必须请求后端 /admin-api/ai/chat 接口。

        使用 @onebase/common 中的 httpClient，确保请求头包含租户信息 (X-Tenant-Id)。

        支持 SSE (Server-Sent Events) 流式解析。

二、 后端工程需求描述 (Claude Code Friendly)

上下文上下文 (Context): 这是一个基于 Spring Boot 3.4.5 + JDK 17 的多模块 Maven 项目。

    Task: 在 onebase-module-ai (若无则新建) 中实现 AI 聊天代理路由。

    1. Controller 开发:

        创建 ChatController，暴露 POST /chat 接口。

        使用 SseEmitter 或响应式流返回数据，支持流式输出。

    2. 代理逻辑:

        安全校验: 通过 onebase-spring-boot-starter-biz-tenant 拦截并获取当前租户上下文。

        秘钥解密: 从组件配置实体中读取 apiKey，若存储时已加密，需调用框架内置的 SM2 工具类解密。

        后端转发: 使用 onebase-spring-boot-starter-rpc 封装的 OpenFeign 或 RestTemplate 请求第三方 LLM 供应商。

    3. 框架集成:

        接口需集成 starter-web 的全局异常处理和 API 日志记录。

        考虑到并发，建议使用 starter-protection 对 API 调用频率进行简单限制 (Rate Limiting)。

三、 验证与冒烟测试 (Smoke Test)

给 Claude Code 完成代码后，你可以要求它运行以下检查：
前端验证

    构建检查: pnpm build:packages 是否成功，生成的 dist 是否包含 Chatbot。

    编辑器检查: 启动 app-builder，在“工作台组件”分类下能否看到新图标。

    渲染检查: 拖入画布后，修改 provider 配置，观察 Zustand 或 Signals 中的状态值是否同步更新。

后端验证

    接口文档: 启动后端，访问 http://localhost:48080/doc.html，检查 /ai/chat 接口是否出现在 Knife4j 列表中。

    租户隔离: 模拟不同 X-Tenant-Id 的请求，验证后端能否正确获取对应的租户上下文。

    流式输出: 使用 curl 测试接口：curl -N -X POST http://localhost:48080/admin-api/ai/chat ...，观察是否能逐行接收到 data:  格式的响应。


### V2.0 需求
当前 AI 对话助手 组件已完成 UI 骨架开发并注册至 ui-kit，但在 app-builder 的右侧属性面板中缺少 LLM 相关的配置项，导致组件无法获取 API Key 进行实际对话。

开发任务 (Tasks):

    修改物料定义 (Material Schema):

        定位至 packages/ui-kit/src/components/Workbench/Chatbot 的注册代码或对应的 meta.ts / index.ts 文件。

        在组件的属性定义中增加一个新的配置分组：“LLM 配置” (LLM Configuration)。

    增加具体配置项:

        LLM 供应商 (provider): 使用 Select 组件，选项包括 OpenAI, DeepSeek, Anthropic, 本地私有模型。

        模型名称 (model): 使用 Input 组件，默认值建议设为 gpt-4o 或 deepseek-chat。

        API Key: 使用 Input.Password 组件（增加一键复制和脱敏显示功能）。 注意：该字段需标记为敏感信息。

        系统提示词 (systemPrompt): 使用 TextArea 组件，支持多行输入。

    双向绑定与持久化:

        确保在右侧面板修改上述配置时，能够通过 onChange 事件实时更新组件的 props 或 config 对象。

        在组件预览态（Preview）检测到 apiKey 为空时，保持当前的“请先配置 API Key”提示状态；一旦配置完成，切换为聊天输入状态。

    样式对齐:

        新增加的配置分组样式需与现有的“样式库”、“标题配置”保持一致，遵循 Arco Design 的面板规范。

✅ 验证要点 (Definition of Done)

    配置可见性：选中画布中的 AI 助手后，右侧面板必须出现“LLM 配置”折叠栏。

    数据一致性：在面板输入 API Key 后，切换其他组件再切回，输入的内容必须依然存在。

    代码合规：属性定义的 key 必须与后端 ChatController 接收的字段名（provider, model, apiKey）完全对应。

建议操作步骤：
你可以先让 OpenCode 运行 grep -r "AI 对话助手" . 来精准定位该组件的物料注册文件，然后再执行上述开发任务。