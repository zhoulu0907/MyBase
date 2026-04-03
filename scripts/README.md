# 配置脚本使用说明

## 文件说明

| 文件 | 说明 |
|-----|------|
| `env-config.ts` | 环境配置文件，定义各环境的服务地址 |
| `encrypt-config.ts` | 配置加密工具，生成加密后的配置文件 |

## 环境配置

在 `env-config.ts` 中定义了以下环境：

| 环境键 | 名称 | OneBase 地址 |
|-------|------|-------------|
| `lingji-dev` | 灵畿开发环境 | `http://onebase.4c-uat.hq.cmcc:20011` |
| `lingji-sit` | 灵畿 SIT 环境 | `http://onebase.4c-uat3.hq.cmcc:20018` |
| `tiangong-dev` | 天工开发环境 | `https://onebase-sit.artifex-cmcc.com.cn` |
| `tiangong-sit` | 天工 SIT 环境 | `https://onebase-sit.artifex-cmcc.com.cn` |

## 使用方法

### 查看帮助

```bash
npx tsx scripts/encrypt-config.ts --help
```

### 加密并验证单个配置

```bash
# 语法: npx tsx scripts/encrypt-config.ts [环境] [configKey]

npx tsx scripts/encrypt-config.ts lingji-dev app-builder
npx tsx scripts/encrypt-config.ts lingji-sit app-builder
npx tsx scripts/encrypt-config.ts tiangong-dev app-builder
```

### 生成配置文件

```bash
# 语法: npx tsx scripts/encrypt-config.ts emit [输出目录] [环境] [configKey1] [configKey2]...

# 生成单个配置
npx tsx scripts/encrypt-config.ts emit deploy/config lingji-dev app-builder

# 生成指定配置
npx tsx scripts/encrypt-config.ts emit deploy/config/lingji-sit lingji-sit app-builder runtime

# 生成所有配置
npx tsx scripts/encrypt-config.ts emit deploy/config/lingji-dev lingji-dev
npx tsx scripts/encrypt-config.ts emit deploy/config/lingji-sit lingji-sit
```

### 生成明文配置（调试用）

```bash
npx tsx scripts/encrypt-config.ts emit-plain deploy/config lingji-dev
```

## 配置项说明

### CONFIG（主配置）

生成的 `builder_config.js` 包含加密的主配置：

- `THEME` - 主题
- `ENVIRONMENT` - 环境类型
- `BASE_URL` - API 基础地址
- `PLATFORM_BASE_URL` - 平台 API 地址
- `RUNTIME_BASE_URL` - 运行时 API 地址
- `RESOURCE_URL` - 资源下载地址
- `SUPERVISION_PLUGIN` - 监督插件配置

### AI_CONFIG（AI 配置）

生成的 `builder_config.js` 还包含加密的 AI 配置：

- `GENAPP` - AI GenApp 服务地址
- `COPILOT` - AI Copilot 服务地址

## 输出文件

| 配置键 | 输出文件 |
|-------|---------|
| `app-builder` | `builder_config.js`（包含 CONFIG 和 AI_CONFIG） |
| `admin-console` | `console_config.js` |
| `runtime` | `runtime_config.js` |
| `mobile-runtime` | `mobile_runtime_config.js` |
| `mobile-editor` | `mobile_editor_config.js` |
| `app-dashboard` | `dashboard_config.js` |

**app-builder 额外配置文件：**

| 文件 | 说明 |
|-----|------|
| `sso.config.js` | 灵畿 SSO 配置（仅灵畿环境生成） |
| `supervision.config.js` | 监督插件配置 |

## 新增环境

在 `env-config.ts` 的 `ENVIRONMENTS` 对象中添加新环境配置：

```typescript
'new-env': {
  name: '新环境名称',
  ONEBASESERVER_BASE_URL: 'http://xxx/observerbuilder',
  ONEBASERUNTIMESERVER_BASE_URL: 'http://xxx/observerruntime',
  // ... 其他配置
  AI_GENAPP_URL: 'http://xxx/aigenapp/',
  AI_COPILOT_URL: 'http://xxx/aicopilot/',
  SUPERVISION_ENABLE: true,
  SUPERVISION_PLATFORM: '01',
  SUPERVISION_URL: 'http://xxx/supervision',
  SUPERVISION_SSO_URL: 'http://xxx/sso',
},
```

然后在 `EnvironmentKey` 类型中添加新环境的键名。