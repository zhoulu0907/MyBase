# 配置加密脚本使用说明

本脚本用于生成各环境的加密配置文件，支持多环境管理和 SM2 加密。

## 文件说明

| 文件 | 说明 |
|-----|------|
| `env-config.ts` | 环境配置文件，定义各环境的服务地址变量 |
| `encrypt-config.ts` | 配置加密工具，读取环境配置并生成加密后的配置文件 |

## 加密原理

使用 SM2 国密算法对配置进行加密：

1. **加密流程**：将配置对象 JSON 序列化后，使用 SM2 公钥加密
2. **解密流程**：前端使用 SM2 私钥解密（私钥存储在前端代码中）
3. **密钥管理**：公钥/私钥定义在 `packages/common/src/utils/crypto.ts`

生成的配置文件格式：
```javascript
window.global_config = {
  CONFIG: "加密后的字符串",
  AI_CONFIG: "加密后的字符串"
};
```

## 环境配置

### 当前环境列表

| 环境键 | 名称 | OneBase 地址 | 监督插件 | SSO |
|-------|------|-------------|---------|-----|
| `lingji-dev` | 灵畿开发环境 | `http://onebase.4c-uat.hq.cmcc:20011` | ✓ | ✓ |
| `lingji-sit` | 灵畿 SIT 环境 | `http://onebase.4c-uat3.hq.cmcc:20018` | ✓ | ✓ |
| `tiangong-dev` | 天工开发环境 | `https://onebase-sit.artifex-cmcc.com.cn` | - | - |
| `tiangong-sit` | 天工 SIT 环境 | `https://onebase-sit.artifex-cmcc.com.cn` | - | - |

### 环境变量说明

在 `env-config.ts` 的 `EnvironmentConfig` 接口中定义了以下变量：

#### 基础服务地址

| 变量名 | 说明 | 示例 |
|-------|------|------|
| `ONEBASESERVER_BASE_URL` | OneBase 服务端地址（构建器） | `http://xxx/observerbuilder` |
| `ONEBASERUNTIMESERVER_BASE_URL` | OneBase 运行时服务端地址 | `http://xxx/observerruntime` |

#### 前端应用地址

| 变量名 | 说明 | 示例 |
|-------|------|------|
| `APP_BUILDER_FE_URL` | 应用构建器前端地址 | `http://xxx/appbuilder` |
| `APP_RUNTIME_FE_URL` | 应用运行时前端地址 | `http://xxx/appruntime` |
| `APP_MOBILE_BUILDER_FE_URL` | 移动端构建器前端地址 | `http://xxx/mobilebuilder` |
| `APP_MOBILE_RUNTIME_FE_URL` | 移动端运行时前端地址 | `http://xxx/mobileruntime` |

#### 数据服务地址

| 变量名 | 说明 | 示例 |
|-------|------|------|
| `APP_BUILDER_DATASET_URL` | 数据集服务地址 | `http://xxx/observerbuilder` |
| `APP_BUILDER_DASHBOARD_URL` | 仪表板服务地址 | `http://xxx/appdashboard/#/` |
| `CHATBOT_BASE_URL` | Chatbot 服务地址 | `` |
| `DASHBOARD_URL` | Dashboard 配置地址 | `http://xxx/observerbuilder` |
| `PREVIEW_URL` | 预览地址 | `http://xxx/appdashboard/#/chart/preview` |
| `DATASET_URL` | 数据集 API 地址 | `http://xxx/de2api` |

#### AI 配置

| 变量名 | 说明 | 示例 |
|-------|------|------|
| `AI_GENAPP_URL` | AI GenApp 服务地址 | `http://xxx/aigenapp/` |
| `AI_COPILOT_URL` | AI Copilot 服务地址 | `http://xxx/aicopilot/` |

#### 监督插件配置

| 变量名 | 说明 | 示例 |
|-------|------|------|
| `SUPERVISION_ENABLE` | 是否启用监督插件 | `true` / `false` |
| `SUPERVISION_PLATFORM` | 平台版本（01=IT公司α, 02=互联网公司β） | `'01'` |
| `SUPERVISION_URL` | 监督平台访问地址 | `http://xxx/supervision` |
| `SUPERVISION_SSO_URL` | 监督平台单点登录地址 | `http://xxx/sso` |

#### SSO 配置（灵畿专属）

| 变量名 | 说明 | 示例 |
|-------|------|------|
| `LINGJI_HOME_URL` | 灵畿前端首页地址 | `https://xxx/home` |
| `LINGJI_SSO_SOURCE_ID` | 灵畿 SSO 应用ID | `'5570132830'` |

## 使用方法

### 查看帮助

```bash
npx tsx scripts/encrypt-config.ts --help
```

### 加密并验证单个配置

用于调试，输出加密前后的配置内容：

```bash
# 语法: npx tsx scripts/encrypt-config.ts [环境] [configKey]

npx tsx scripts/encrypt-config.ts lingji-dev app-builder
npx tsx scripts/encrypt-config.ts lingji-sit runtime
```

### 生成配置文件

```bash
# 语法: npx tsx scripts/encrypt-config.ts emit [输出目录] [环境] [configKey1] [configKey2]...

# 生成 app-builder 配置（包含 builder_config.js、sso.config.js、supervision.config.js）
npx tsx scripts/encrypt-config.ts emit deploy/config/lingji-dev lingji-dev app-builder

# 生成多个配置
npx tsx scripts/encrypt-config.ts emit deploy/config/lingji-sit lingji-sit app-builder runtime

# 生成所有配置
npx tsx scripts/encrypt-config.ts emit deploy/config/lingji-sit lingji-sit
```

### 生成明文配置（调试用）

```bash
npx tsx scripts/encrypt-config.ts emit-plain deploy/config lingji-dev
```

## 配置项与输出文件映射

### 主配置输出文件

| 配置键 (configKey) | 输出文件 | 加密 | 说明 |
|-------------------|---------|-----|------|
| `app-builder` | `builder_config.js` | ✓ | 包含 CONFIG 和 AI_CONFIG |
| `admin-console` | `console_config.js` | ✓ | 管理控制台配置 |
| `runtime` | `runtime_config.js` | ✓ | 运行时配置 |
| `mobile-runtime` | `mobile_runtime_config.js` | ✓ | 移动端运行时配置 |
| `mobile-editor` | `mobile_editor_config.js` | ✓ | 移动端编辑器配置 |
| `app-dashboard` | `dashboard_config.js` | - | 明文配置 |

### app-builder 额外配置文件

| 文件 | 生成条件 | 说明 |
|-----|---------|------|
| `sso.config.js` | 灵畿环境（LINGJI_HOME_URL 有值） | 灵畿 SSO 登录配置 |
| `supervision.config.js` | SUPERVISION_ENABLE=true | 监督插件配置 |

### 各环境生成的文件差异

| 环境 | builder_config.js | sso.config.js | supervision.config.js |
|------|-------------------|---------------|----------------------|
| lingji-dev | ✓ | ✓ | ✓ |
| lingji-sit | ✓ | ✓ | ✓ |
| tiangong-dev | ✓ | - | - |
| tiangong-sit | ✓ | - | - |

## 配置内容说明

### builder_config.js - CONFIG 部分

| 字段 | 说明 | 来源变量 |
|-----|------|---------|
| `THEME` | 主题 | 固定值 `lingji` |
| `ENVIRONMENT` | 环境类型 | 固定值 `builder` |
| `APP_KEY` | 应用标识 | 固定值 |
| `APP_SECRET` | 应用密钥 | 固定值 |
| `BASE_URL` | API 基础地址 | `ONEBASESERVER_BASE_URL` + `/admin-api` |
| `PLATFORM_BASE_URL` | 平台 API 地址 | `ONEBASESERVER_BASE_URL` + `/platform` |
| `RUNTIME_BASE_URL` | 运行时 API 地址 | `ONEBASERUNTIMESERVER_BASE_URL` + `/runtime` |
| `RESOURCE_URL` | 资源下载地址 | `ONEBASESERVER_BASE_URL` + `/admin-api/infra/file/download` |
| `MOBILE_EDITOR_URL` | 移动端编辑器地址 | `APP_MOBILE_BUILDER_FE_URL` |
| `RUNTIME_URL` | 运行时前端地址 | `APP_RUNTIME_FE_URL` |
| `RUNTIME_MOBILE_URL` | 移动端运行时地址 | `APP_MOBILE_RUNTIME_FE_URL` |
| `PLUGIN_URL` | 插件地址 | `ONEBASESERVER_BASE_URL` |
| `APP_BUILDER_DATASET_URL` | 数据集地址 | `APP_BUILDER_DATASET_URL` |
| `APP_BUILDER_DASHBOARD_URL` | 仪表板地址 | `APP_BUILDER_DASHBOARD_URL` |
| `PUBLIC_KEY` | 前端公钥 | 固定值 |

### builder_config.js - AI_CONFIG 部分

| 字段 | 说明 | 来源变量 |
|-----|------|---------|
| `GENAPP` | AI GenApp 服务地址 | `AI_GENAPP_URL` |
| `COPILOT` | AI Copilot 服务地址 | `AI_COPILOT_URL` |

### sso.config.js（灵畿环境）

| 字段 | 说明 | 来源变量 |
|-----|------|---------|
| `lingjiHome` | 灵畿前端首页地址 | `LINGJI_HOME_URL` |
| `sourceid` | 灵畿 SSO 应用ID | `LINGJI_SSO_SOURCE_ID` |
| `ssoLoginApi` | 后端 SSO 登录接口 | `ONEBASESERVER_BASE_URL` + `/admin-api/system/lingji-sso/login` |
| `successRedirectBase` | 登录成功跳转地址 | `APP_BUILDER_FE_URL` |

### supervision.config.js

| 字段 | 说明 | 来源变量 |
|-----|------|---------|
| `ENABLE` | 是否启用监督插件 | `SUPERVISION_ENABLE` |
| `PLATFORM` | 平台版本 | `SUPERVISION_PLATFORM` |
| `SUPERVISION_URL` | 监督平台访问地址 | `SUPERVISION_URL` |
| `SSO_URL` | 单点登录地址 | `SUPERVISION_SSO_URL` |

## 新增环境

### 步骤 1：添加环境配置

在 `env-config.ts` 中添加：

```typescript
// 1. 在 EnvironmentKey 类型中添加新环境键名
export type EnvironmentKey = 'lingji-dev' | 'lingji-sit' | 'tiangong-dev' | 'tiangong-sit' | 'new-env';

// 2. 在 ENVIRONMENTS 对象中添加新环境配置
'new-env': {
  name: '新环境名称',
  // 基础服务地址
  ONEBASESERVER_BASE_URL: 'http://xxx/observerbuilder',
  ONEBASERUNTIMESERVER_BASE_URL: 'http://xxx/observerruntime',
  // 前端应用地址
  APP_BUILDER_FE_URL: 'http://xxx/appbuilder',
  APP_RUNTIME_FE_URL: 'http://xxx/appruntime',
  APP_MOBILE_BUILDER_FE_URL: 'http://xxx/mobilebuilder',
  APP_MOBILE_RUNTIME_FE_URL: 'http://xxx/mobileruntime',
  // 数据服务地址
  APP_BUILDER_DATASET_URL: 'http://xxx/observerbuilder',
  APP_BUILDER_DASHBOARD_URL: 'http://xxx/appdashboard/#/',
  CHATBOT_BASE_URL: '',
  DASHBOARD_URL: 'http://xxx/observerbuilder',
  PREVIEW_URL: 'http://xxx/appdashboard/#/chart/preview',
  DATASET_URL: 'http://xxx/de2api',
  // AI 配置
  AI_GENAPP_URL: 'http://xxx/aigenapp/',
  AI_COPILOT_URL: 'http://xxx/aicopilot/',
  // 监督插件配置（如不需要设为 false）
  SUPERVISION_ENABLE: true,
  SUPERVISION_PLATFORM: '01',
  SUPERVISION_URL: 'http://xxx/supervision',
  SUPERVISION_SSO_URL: 'http://xxx/sso',
  // SSO 配置（如不需要设为空字符串）
  LINGJI_HOME_URL: '',
  LINGJI_SSO_SOURCE_ID: '',
},
```

### 步骤 2：生成配置文件

```bash
npx tsx scripts/encrypt-config.ts emit deploy/config/new-env new-env app-builder
```

## 新增配置项

### 步骤 1：添加环境变量

在 `env-config.ts` 的 `EnvironmentConfig` 接口中添加新变量：

```typescript
export interface EnvironmentConfig {
  // ... 现有变量
  NEW_CONFIG_URL: string;  // 新增变量
}
```

然后在各环境配置中添加对应的值。

### 步骤 2：添加到配置输出

在 `encrypt-config.ts` 的 `buildConfigSources` 函数中添加：

```typescript
'app-builder': {
  // ... 现有配置
  NEW_CONFIG_URL: envConfig.NEW_CONFIG_URL,  // 新增配置项
},
```

### 步骤 3：重新生成配置文件

```bash
npx tsx scripts/encrypt-config.ts emit deploy/config/lingji-dev lingji-dev app-builder
```

## 常见问题

### Q: 如何查看加密前的配置内容？

```bash
npx tsx scripts/encrypt-config.ts emit-plain deploy/config lingji-dev
```

### Q: 天工环境为什么没有 sso.config.js 和 supervision.config.js？

天工环境 `SUPERVISION_ENABLE=false` 且 `LINGJI_HOME_URL=''`，脚本会自动跳过这些文件的生成。

### Q: 如何验证加密是否正确？

```bash
# 加密并验证会输出加密前后的配置内容进行对比
npx tsx scripts/encrypt-config.ts lingji-dev app-builder
```