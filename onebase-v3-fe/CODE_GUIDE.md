# OneBase V3 前端 - 代码指南

本文档描述 OneBase 平台前端项目的代码架构、目录结构、核心机制与开发规范，帮助新成员快速理解项目并开展开发工作。

**作者**：Mickey.Zhou
**最后更新**：2026-04-19

---

## 一、项目概述

OneBase 平台前端是基于 **React 18 + TypeScript + Vite** 构建的现代化 monorepo 应用，采用 **pnpm workspace** 管理多包结构，包含管理控制台、低代码应用搭建、PC/移动端运行时等多个子应用及共享包。

### 技术栈

| 类别       | 技术                          | 说明                             |
| ---------- | ----------------------------- | -------------------------------- |
| 框架       | React 18                      | 函数组件 + Hooks                 |
| 构建工具   | Vite                          | 所有应用统一使用                  |
| 语言       | TypeScript 5.8                | 严格类型检查                      |
| 包管理     | pnpm                          | workspace monorepo               |
| UI 组件    | Arco Design / Semi Design     | admin-console/app-builder 用 Arco，runtime 用 Semi |
| 路由       | React Router DOM v6           |                                  |
| 状态管理   | @preact/signals + Zustand     | Signals 全局响应式，Zustand 局部状态 |
| 动态表单   | Formily v2                    | JSON Schema 驱动的低代码表单      |
| 图形编辑   | AntV G6/X6                    | 流程图、ETL 图编辑               |
| 代码编辑   | CodeMirror / Monaco Editor    | 脚本编辑器                       |
| 样式       | Less / CSS Modules            |                                  |
| 加密       | SM2（国密）                    | 配置加密、传输签名                |

---

## 二、Monorepo 结构

```
onebase-v3-fe/
├── apps/                      # 应用层
│   ├── admin-console/         # 管理控制台（端口 4400）
│   ├── app-builder/           # 应用搭建 / 低代码编辑器（端口 4399）
│   ├── mobile-editor/         # 移动端编辑器
│   ├── mobile-runtime/        # 移动端运行时
│   └── runtime/               # PC 端运行时
├── packages/                  # 公共包
│   ├── common/                # 基础工具、HTTP、Token、加密、Signals
│   ├── app/                   # 应用领域服务与 API 封装
│   ├── platform-center/       # 平台中心服务（用户、租户、组织等）
│   ├── ui-kit/                # PC 端可复用 UI 组件库
│   ├── ui-kit-mobile/         # 移动端 UI 组件库
│   ├── product-lingji/        # 灵畿平台差异化包
│   └── product-tiangong/      # 天工平台差异化包
├── sdks/                      # SDK
│   ├── ob-plugin/             # 插件 SDK（宿主 + 开发侧）
│   └── ob-plugin-template/    # 插件开发模板
├── scripts/                   # 构建/开发辅助脚本
├── docs/                      # 设计文档与实现计划
└── Makefile                   # 统一命令入口
```

### 包依赖关系

```
                    ┌──────────────┐
                    │   packages/  │
                    │    common    │  ← 最底层，被所有包引用
                    └──────┬───────┘
                           │
          ┌────────────────┼──────────────────┐
          │                │                  │
   ┌──────▼──────┐  ┌──────▼──────┐   ┌──────▼──────┐
   │  ui-kit /   │  │  platform-  │   │    app      │
   │ ui-kit-mob  │  │   center    │   │             │
   └──────┬──────┘  └──────┬──────┘   └──────┬──────┘
          │                │                  │
          │         ┌──────▼──────┐           │
          │         │  product-   │           │
          │         │ lingji /    │           │
          │         │ tiangong    │           │
          │         └──────┬──────┘           │
          │                │                  │
          └────────────────┼──────────────────┘
                           │
                    ┌──────▼──────┐
                    │    apps/*    │  ← 所有应用
                    └─────────────┘
```

- `packages/common` 为最底层基础包
- `packages/ui-kit`、`packages/app`、`packages/platform-center` 依赖 `common`
- `product-lingji`、`product-tiangong` 依赖 `common` + `platform-center`
- 各应用依赖对应的 packages

---

## 三、应用说明

### 3.1 admin-console（管理控制台）

- **用途**：平台管理端，负责租户、组织、用户、License 等配置
- **端口**：开发模式 `http://localhost:4400`
- **主要模块**：
  - `Login`：登录页（支持多主题：default/lingji/tiangong）
  - `Home`：首页
  - `Tenant`：租户管理（创建/编辑/列表）
  - `PlatformInfo`：平台信息
  - `Administrator`：管理员管理
- **特色**：多主题登录页，通过 `config.js` 的 `THEME` 字段切换

### 3.2 app-builder（应用搭建）

- **用途**：低代码应用搭建平台核心应用
- **端口**：开发模式 `http://localhost:4399`
- **核心模块**：
  - `CreateApp`：应用创建与配置
  - `Editor`：页面/表单/列表/工作台/流程/ETL 编辑器
  - `Runtime`：搭建预览
  - `Setting`：应用/实体/流程/角色/用户等设置（支持多平台差异化 UI）
  - `store/signals/`：编辑器全局状态
  - `plugin/`：插件加载与运行
  - `products/`：**多平台动态加载机制**
- **平台分发机制**（详见第六节）：
  - 通过 `products/index.ts` 根据 config.js 动态加载 `product-lingji` 或 `product-tiangong`
  - 每个平台提供独立的布局、侧边栏、Logo、应用卡片等组件

### 3.3 runtime（PC 运行时）

- **用途**：应用发布后的实际运行环境
- **UI 库**：Semi Design（与其他应用的 Arco Design 不同）
- **功能**：登录、我的应用、页面渲染、设置、第三方登录等
- **依赖**：`ob-plugin` 实现组件与页面动态渲染
- **特色**：支持 JEXL 表达式计算

### 3.4 mobile-editor / mobile-runtime

- **用途**：移动端应用的编辑与运行
- **依赖**：`packages/ui-kit-mobile`
- **结构**：FormEditor、ListEditor、WorkbenchEditor 等对应 PC 端能力
- **UI 库**：Arco Design Mobile

---

## 四、公共包说明

### 4.1 packages/common

提供全局共享能力，是整个项目的基础层：

```
common/src/
├── utils/
│   ├── http.ts          # HTTP 客户端封装（Axios + 签名 + Token）
│   ├── token.ts         # Token 管理（session/local 双存储策略）
│   ├── crypto.ts        # SM2 加密/解密工具
│   ├── signature.ts     # API 请求签名（SHA256 + nonce + timestamp）
│   ├── env.ts           # 环境检测与配置解密
│   ├── router.ts        # 路由工具
│   └── tree.ts          # 树形数据工具
├── components/          # 通用组件（Captcha、Upload、DynamicIcon 等）
├── signals/             # 全局响应式信号
│   ├── etl_editor.tsx   # ETL 编辑器状态
│   ├── page_runtime.tsx # 页面运行时状态
│   └── menu_premission.tsx  # 菜单权限
├── constants/           # 常量（field、permission、security 等）
├── types/               # 通用类型定义
└── pages/               # ErrorPage、NotFoundPage 等通用页面
```

**HTTP 客户端特性**（`http.ts`）：
- 自动 Bearer Token 注入
- 自动租户 ID（`Tenant-ID`）注入
- 自动应用 ID（`X-Application-Id`）注入
- SHA256 请求签名（防重放攻击）
- 401 自动重定向到登录页（支持 iframe 环境通过 postMessage 通知父页面）

**Token 管理特性**（`token.ts`）：
- 支持 sessionStorage（默认）和 localStorage（记住我）双策略
- 环境隔离（通过 `addEnv` 前缀区分 builder/platform/runtime）
- 自动过期检测

**加密特性**（`crypto.ts`）：
- SM2 国密算法加密/解密
- 生产环境配置加密存储
- `env.ts` 中运行时自动解密 `window.global_config.CONFIG`

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

- PC/移动端可复用 UI 组件库
- 编辑器渲染层（EditRender / PreviewRender）
- 物料、导航、表格等业务组件
- 使用 Zustand 管理组件内部状态

### 4.5 packages/product-lingji / product-tiangong

**多平台差异化包**，通过统一的 `PlatformExports` 接口为不同平台提供定制化实现：

| 差异点       | 灵畿 (lingji)          | 天工 (tiangong)          |
| ------------ | ---------------------- | ------------------------ |
| SSO 登录     | 支持                   | 不支持                   |
| OAuth 登录   | 不支持                 | 支持                     |
| 监督插件     | 支持                   | 不支持                   |
| 回调路由     | `/lingji-callback`     | `/oauth/obbuilder/:name` |
| 布局/侧边栏  | 独立 UI                | 独立 UI                  |
| 应用卡片     | 独立样式               | 独立样式                 |

构建工具均为 tsup。

---

## 五、状态管理

项目采用 **@preact/signals** 做全局响应式状态管理，部分模块使用 **Zustand**。

### Signals 模式

```typescript
import { signal, computed } from '@preact/signals-react';

// 创建信号
const curNode = signal<any>({});

// 派生状态
const canCreate = computed(() => menuPermission.value?.operationPermission.canCreate ?? true);

// 更新
curNode.value = newNode;
```

### 信号清单

**全局信号**（`packages/common/src/signals/`）：

| 信号文件               | 用途                          |
| ---------------------- | ----------------------------- |
| `etl_editor.tsx`       | ETL 编辑器：当前节点、节点数据、图形数据 |
| `page_runtime.tsx`     | 页面运行时：当前页、抽屉状态、表格行、流程 |
| `menu_premission.tsx`  | 菜单权限：CRUD 权限计算属性    |

**UI Kit 信号**（`packages/ui-kit/src/signals/`）：

| 信号文件                | 用途                |
| ----------------------- | ------------------- |
| `page_layout.ts`        | 页面布局状态        |
| `page_editor.tsx`       | 页面编辑器状态      |
| `pageview_editor.tsx`   | 页面视图编辑器      |
| `page_setting.tsx`      | 页面设置状态        |
| `page_schema_validate.tsx` | Schema 验证状态  |
| `flowpage_editor.tsx`   | 流程页面编辑器      |
| `workbench_editor.tsx`  | 工作台编辑器        |
| `store_entity.tsx`      | 实体存储            |
| `menu_dict.tsx`         | 菜单字典            |
| `current_editor.tsx`    | 当前编辑器标识      |

---

## 六、多平台动态加载机制

这是项目最核心的架构特色之一，实现了同一套代码支持多个平台（灵畿/天工）的差异化。

### 平台识别策略

```
优先级从高到低：
1. config.js 中的 PLATFORM 字段
2. config.js 中的 THEME 字段（lingji/tiangong）
3. 域名判断（包含 'artifex-cmcc' → 天工）
4. 兜底 → default
```

### 加载流程

```
config.js (THEME/PLATFORM)
    │
    ▼
products/index.ts (getPlatformExports)
    │
    ├── lingji → import('@onebase/product-lingji')
    ├── tiangong → import('@onebase/product-tiangong')
    └── default → products/default/ (空实现)
    │
    ▼
PlatformExports 接口（统一出口）
    ├── config          - 平台配置
    ├── usePlatformInit - 平台初始化 Hook
    ├── Layout/Sider    - 布局组件
    ├── AppCard         - 应用卡片
    ├── Callback        - 登录回调
    └── PlatformRoutes  - 平台路由
```

### 设置页面多平台架构

`app-builder/src/pages/Setting/` 下的多平台实现策略：

```
Setting/
├── components/
│   ├── layout/
│   │   ├── lingji/     # 灵畿布局
│   │   └── tiangong/   # 天工布局
│   └── sider/
│       ├── lingji/     # 灵畿侧边栏
│       └── tiangong/   # 天工侧边栏
├── pages/
│   └── Application/
│       ├── lingji.tsx          # 灵畿应用管理页
│       ├── tiangong.tsx        # 天工应用管理页
│       └── components/AppCard/
│           ├── lingji.tsx      # 灵畿卡片
│           └── tiangong.tsx    # 天工卡片
└── utils/
    └── menuData.tsx            # 菜单数据（按平台差异化）
```

---

## 七、插件系统

### 架构概览

```
sdks/ob-plugin/
├── src/
│   ├── host/           # 宿主侧（app-builder / runtime 使用）
│   │   ├── manager.ts  # 插件管理器（注册/加载/生命周期）
│   │   ├── resource.ts # 资源管理
│   │   ├── validator.ts# 插件验证
│   │   └── registry.ts # 插件注册表
│   ├── sdk/            # 插件开发侧（插件开发者使用）
│   │   ├── context/    # 上下文管理（create/ui/events/entity/request）
│   │   └── register/   # 插件注册（definePlugin / PluginBuilder）
│   └── types.ts        # 类型定义（PluginMeta/PluginPage/PluginComponent/PluginMethod）
└── ob-plugin-template/ # 插件开发模板
```

### 插件加载流程

```
1. 检查 ENABLE_PLUGINS 配置
2. 注入全局变量（React/ReactDOM/ReactRouterDOM/ArcoDesign）
3. 获取插件清单（优先服务端 API，回退本地 envConfig.PLUGINS）
4. 注册插件（registerPlugin）
5. 加载并集成插件（loadPlugin + integratePlugin）
```

### 插件集成方式

宿主应用通过全局变量注入实现插件沙箱：
```typescript
(window as any).React = React;
(window as any).ReactDOM = ReactDOM;
(window as any).ReactRouterDOM = ReactRouterDOM;
(window as any).ArcoDesign = ArcoDesign;
```

插件通过 `definePlugin` 注册自己的页面、组件和方法：
```typescript
export default definePlugin({
  name: 'my-plugin',
  version: '1.0.0',
  pages: [...],
  components: [...],
  methods: [...],
});
```

---

## 八、安全与鉴权

### 8.1 API 签名机制

每次 HTTP 请求自动附加签名头，防重放攻击：

```
签名算法：
1. Query 参数按 key 排序拼接
2. Body JSON 序列化
3. Header 拼接（appKey + nonce + timestamp）
4. 签名基础字符串 = Query + Body + Header + appSecret
5. SHA256 生成最终签名
```

自动添加的请求头：
- `X-App-Key`：应用标识
- `X-Nonce`：16位随机字符串
- `X-Timestamp`：时间戳
- `X-Sign`：签名值

### 8.2 配置加密

- **算法**：SM2（国密）
- **生产环境**：配置加密后存储在 `window.global_config.CONFIG`
- **运行时解密**：`env.ts` 中自动解密
- **加密脚本**：`scripts/encrypt-config.ts`

### 8.3 Token 管理

- 双存储策略：sessionStorage（默认） / localStorage（记住我）
- 环境隔离：builder / platform / runtime 各自独立
- 401 自动处理：跳转登录页或 postMessage 通知父页面

---

## 九、构建与开发

### 9.1 常用命令

| 命令                  | 说明                          |
| --------------------- | ----------------------------- |
| `make init`           | 初始化 git 子模块             |
| `make install`        | 安装所有依赖                  |
| `make dev`            | 启动所有项目开发模式          |
| `make dev-admin`      | 仅管理控制台                  |
| `make dev-common`     | 仅公共包（watch 模式）        |
| `make dev-ui`         | 仅 UI 组件库                  |
| `make build`          | 构建所有项目                  |
| `make build-packages` | 仅构建所有包                  |
| `make build-admin`    | 仅构建管理控制台              |
| `make clean`          | 清理所有构建文件              |
| `make lint`           | 运行代码检查                  |
| `pnpm encrypt:config` | 运行配置加密脚本              |

### 9.2 构建顺序

```
1. packages/common          （基础层）
2. packages/app             （应用服务层）
3. packages/platform-center （平台服务层）
4. packages/ui-kit          （UI 组件层）
5. sdks/ob-plugin           （插件 SDK，app-builder/runtime 需要）
6. packages/product-lingji / product-tiangong （平台差异化包）
7. apps/*                   （各应用）
```

### 9.3 Vite 配置特点

- 所有应用统一使用 Vite
- 启用 gzip 压缩（`vite-plugin-compression`）
- 统一路径别名（`@` → `src/`）
- app-builder 配置了存储服务代理（`/storage_area`、`/share-app`）
- 支持 Babel 装饰器插件（`@babel/plugin-proposal-decorators`）
- Less 预处理配置（CSS 变量前缀）

---

## 十、目录与文件规范

### 10.1 应用内目录

各应用通常遵循：

```
src/
├── assets/        # 静态资源（图片、SVG、字体）
├── components/    # 通用组件
├── hooks/         # 自定义 Hooks
├── i18n/          # 国际化（JSON + 配置）
├── pages/         # 页面（按功能分子目录）
├── plugin/        # 插件加载（app-builder）
├── products/      # 平台分发（app-builder）
├── store/         # 状态管理（含 signals/）
├── themes/        # 主题文件（theme_lingji.less 等）
├── types/         # 类型定义
└── utils/         # 工具函数
```

### 10.2 样式规范

- 使用 Less 和 CSS Modules（`.module.less`）
- 主题文件：`theme.less`、`theme_lingji.less`、`theme_tiangong.less`
- CSS 变量前缀通过 Vite 配置注入

### 10.3 国际化

- `i18n/` 下的 JSON 文件管理翻译
- 使用 i18next 库
- 支持中英文切换

### 10.4 环境配置

各应用通过 `public/config.js` 注入运行时配置：

```javascript
window.global_config = {
  THEME: 'lingji',           // 主题/平台
  ENVIRONMENT: 'builder',    // 环境标识
  APP_KEY: 'onebase',        // 应用标识
  APP_SECRET: '...',         // 应用密钥
  BASE_URL: '...',           // API 基础地址
  PLATFORM_BASE_URL: '...',  // 平台服务地址
  RUNTIME_BASE_URL: '...',   // 运行时地址
  RESOURCE_URL: '...',       // 资源下载地址
  PUBLIC_KEY: '...',         // SM2 公钥
};
```

---

## 十一、核心设计模式

### 11.1 多平台策略模式

通过 `PlatformExports` 接口实现平台差异化的策略模式：
- 默认包提供空实现
- 各平台包覆盖需要差异化的组件
- 运行时通过动态 `import()` 加载对应平台包

### 11.2 插件化架构

- 宿主应用通过 `ob-plugin` 的 host 层管理插件生命周期
- 插件通过 SDK 层注册页面、组件、方法
- 全局变量注入实现沙箱隔离

### 11.3 信号驱动的状态管理

- `@preact/signals` 提供细粒度响应式更新
- `computed` 自动派生状态，避免手动计算
- 与 React 组件无缝集成（`@preact/signals-react`）

### 11.4 JSON Schema 驱动的动态表单

- 使用 Formily v2 实现 JSON Schema → UI 的动态渲染
- 连接器配置、环境配置等场景通过后端返回 Schema 驱动前端表单
- 消除条件渲染逻辑，实现 "固定外壳 + 动态内核" 架构

---

## 十二、接口与文档

- 接口文档：见 Apifox 链接（README.md）
- 详细设计文档：`docs/` 目录
- 连接器相关设计：`docs/design/`、`docs/plan/`、`docs/prd/`

---

_本文档随项目演进持续更新，如有遗漏或变更请补充修正。_
