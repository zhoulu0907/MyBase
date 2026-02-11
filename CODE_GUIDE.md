# OneBase V3 前端 - 代码说明文档

本文档描述 OneBase 平台前端项目的代码架构、目录结构、开发规范与核心实现，帮助新成员快速理解项目并开展开发工作。

**作者**：Mickey.Zhou

## 一、项目概述

OneBase 平台前端是基于 **React 18 + TypeScript + Vite** 构建的现代化 monorepo 应用，采用 **pnpm workspace** 管理多包结构，包含管理控制台、应用搭建、运行时等多个子应用及公共包。

### 技术栈

| 类别     | 技术               |
| -------- | ------------------ |
| 框架     | React 18           |
| 构建工具 | Vite               |
| 语言     | TypeScript         |
| 包管理   | pnpm               |
| UI 组件  | Arco Design        |
| 路由     | React Router DOM   |
| 状态管理 | @preact/signals    |
| 代码检查 | ESLint             |
| 样式     | Less / CSS Modules |

## 二、Monorepo 结构

项目采用 pnpm workspace 组织为以下部分：

```
onebase-v3-fe/
├── apps/                 # 应用层
│   ├── admin-console/    # 管理控制台
│   ├── app-builder/      # 应用搭建（低代码编辑器）
│   ├── mobile-editor/    # 移动端编辑器
│   ├── mobile-runtime/   # 移动端运行时
│   └── runtime/          # PC 端运行时
├── packages/             # 公共包
│   ├── common/           # 公共工具、组件、类型、信号
│   ├── ui-kit/           # PC 端 UI 组件库
│   ├── ui-kit-mobile/    # 移动端 UI 组件库
│   ├── app/              # 应用相关服务与类型
│   └── platform-center/  # 平台中心服务（用户、租户、组织等）
├── sdks/                 # SDK
│   ├── ob-plugin/        # 插件 SDK
│   └── ob-plugin-template/  # 插件开发模板
├── scripts/              # 脚本
└── deploy/               # 部署相关
```

### 包依赖关系

- 各 `apps` 应用依赖 `packages` 中的公共包
- `packages/common` 为最底层，被所有应用和包引用
- `packages/ui-kit` / `packages/ui-kit-mobile` 依赖 `common`
- `packages/app`、`packages/platform-center` 提供领域服务和 API 封装
- `sdks/ob-plugin` 为应用运行时提供插件能力

## 三、应用说明

### 3.1 admin-console（管理控制台）

- **用途**：平台管理端，负责租户、组织、用户、License 等配置
- **端口**：开发模式默认 http://localhost:5173
- **主要模块**：Administrator、PlatformInfo、Tenant、Login、Home

### 3.2 app-builder（应用搭建）

- **用途**：低代码应用搭建，支持表单、列表、工作台、流程、ETL 等编辑
- **结构**：
  - `CreateApp`：应用创建与配置
  - `Editor`：页面/表单/列表/工作台/流程/ETL 编辑器
  - `Runtime`：搭建预览
  - `Setting`：应用/实体/流程等设置
  - `store/`：编辑器状态（Signals）
  - `plugin/`：插件加载与运行

### 3.3 runtime（PC 运行时）

- **用途**：应用发布后的实际运行环境
- **功能**：登录、我的应用、页面渲染、设置、第三方登录等
- **依赖**：`ob-plugin` 实现组件与页面动态渲染

### 3.4 mobile-editor / mobile-runtime

- **用途**：移动端编辑与运行
- **依赖**：`packages/ui-kit-mobile`
- **结构**：FormEditor、ListEditor、WorkbenchEditor 等对应 PC 端能力

## 四、公共包说明

### 4.1 packages/common

提供全局共享能力：

- **utils/**：工具函数（http、token、env、router、crypto、tree 等）
- **components/**：通用组件（Captcha、Upload、DynamicIcon、Loading 等）
- **signals/**：基于 @preact/signals 的全局信号（ETL 编辑、页面运行时、菜单权限等）
- **constants/**：常量（field、permission、security 等）
- **types/**：通用类型定义
- **pages/**：ErrorPage、NotFoundPage 等通用页面

### 4.2 packages/app

应用领域相关服务与类型：

- **services/**：应用、实体、流程、ETL、方法、权限、BPM 等 API 封装
- **types/**：应用相关类型
- **signals/**：菜单编辑等应用内信号

### 4.3 packages/platform-center

平台中心服务（用户、租户、组织等）：

- **services/**：auth、user、tenant、dept、role、dict、plugin 等
- **types/**：平台相关类型

### 4.4 packages/ui-kit / ui-kit-mobile

- 可复用的 PC / 移动端 UI 组件
- 编辑器渲染层（EditRender / PreviewRender）
- 物料、导航等业务组件

## 五、状态管理（Signals）

项目使用 **@preact/signals** 做响应式状态管理，典型用法：

```typescript
// 创建信号
const curNode = signal<any>({});

// 派生状态
const canCreate = computed(() => menuPermission.value?.operationPermission.canCreate ?? true);

// 更新
curNode.value = newNode;
```

常见信号模块：

- `etl_editor.tsx`：ETL 编辑器状态
- `page_runtime.tsx`：页面运行时（当前页、抽屉、表格行、流程等）
- `menu_premission.tsx`：菜单/操作权限
- 各应用内 `store/singals/`：编辑器、流程、字段等状态

## 六、插件与 SDK

### ob-plugin（插件 SDK）

- **host/**：宿主侧接口
- **sdk/**：插件侧 API、上下文、注册机制
- **register/**：组件、页面、方法等注册类型与实现

### ob-plugin-template

- 插件开发模板
- 含 mock、调试工具、示例组件与页面

## 七、目录与文件规范

### 7.1 应用内目录

各应用通常遵循：

```
src/
├── assets/        # 静态资源
├── components/    # 组件
├── hooks/         # 自定义 Hooks
├── i18n/          # 国际化
├── pages/         # 页面（按功能分子目录）
├── plugin/        # 插件相关（如有）
├── store/         # 状态管理（含 singals）
├── themes/        # 主题
├── types/         # 类型
└── utils/         # 工具
```

### 7.2 样式

- 使用 Less 和 CSS Modules（`.module.less`）
- 主题文件：`theme.less`、`theme_lingji.less`、`theme_tiangong.less`

### 7.3 国际化

- `i18n/` 下的 JSON 与配置
- 支持多语言切换

## 八、配置与安全

### 8.1 配置加密

- 脚本：`scripts/encrypt-config.ts`，用于生成生产环境加密配置
- 算法：SM2
- 密钥定义在 `packages/common/src/utils/crypto.ts`
- 生产环境从 `window.global_config.CONFIG` 读取加密字符串并解密

### 8.2 HTTP 与鉴权

- `packages/common` 提供 `HttpClient`、`TokenManager`、签名工具
- 统一处理请求头、token 刷新、错误与权限

## 九、构建与开发

### 9.1 常用命令

| 命令                  | 说明                          |
| --------------------- | ----------------------------- |
| `make init`           | 初始化子模块并切换到 dev 分支 |
| `make install`        | 安装依赖                      |
| `make dev`            | 启动所有项目开发模式          |
| `make dev-admin`      | 仅管理控制台                  |
| `make build`          | 构建所有项目                  |
| `make build-packages` | 构建所有包                    |
| `pnpm encrypt:config` | 运行配置加密脚本              |

### 9.2 构建顺序

1. 构建 `packages`：common → app → platform-center → ui-kit
2. 构建 `sdks/ob-plugin`（app-builder、runtime 需要）
3. 构建各应用

## 十、接口与文档

- 接口文档：见 README 中的 Apifox 链接
- 详细使用说明：参见根目录 `README.md`

---

_本文档随项目演进持续更新，如有遗漏或变更请补充修正。_
