# Admin Console

OneBase 平台管理控制台前端应用，基于 React + TypeScript + Vite + Arco Design 构建的现代化管理后台。

## 项目概述

管理控制台是 OneBase 平台的核心管理界面，提供用户管理、系统配置、数据监控等功能。

## 技术栈

- **框架**: React 18
- **构建工具**: Vite
- **语言**: TypeScript
- **UI组件**: Arco Design
- **路由**: React Router DOM
- **状态管理**: React Hooks
- **样式**: Less + CSS Modules
- **代码检查**: ESLint

## 项目结构

```
admin-console/
├── public/              # 静态资源
│   ├── js/             # 第三方 JS 库
│   └── tac/            # 验证码相关资源
├── src/
│   ├── components/     # 公共组件
│   ├── pages/         # 页面组件
│   │   ├── Home/      # 首页
│   │   ├── Login/     # 登录页
│   │   └── NotFound/  # 404页面
│   ├── types/         # TypeScript 类型定义
│   ├── App.tsx        # 应用入口
│   └── main.tsx       # 应用启动
├── package.json        # 项目配置
├── tsconfig.json      # TypeScript 配置
├── vite.config.ts     # Vite 配置
└── Makefile          # 构建脚本
```

## 快速开始

### 前置要求

- Node.js >= v22.17.0
- pnpm >= 9.15.9

### 安装依赖

```bash
# 在项目根目录
make install

# 或在当前目录
pnpm install
```

### 开发模式

```bash
# 在项目根目录
make dev-admin

# 或在当前目录
pnpm dev
```

开发服务器将在 http://localhost:5173 启动

### 构建项目

```bash
# 在项目根目录
make build-admin

# 或在当前目录
pnpm build
```

构建输出将生成在 `dist/` 目录

### 预览构建结果

```bash
pnpm preview
```

## 功能特性

### 用户认证

- 账号密码登录
- 手机号验证码登录
- 验证码集成 (TAC)
- 记住登录状态

### 页面路由

- `/login` - 登录页面
- `/onebase` - 首页
- `/*` - 404 页面

### 组件特性

- 响应式设计
- 表单验证
- 错误处理
- 加载状态

## 开发指南

### 添加新页面

1. 在 `src/pages/` 目录下创建新页面组件
2. 在 `src/App.tsx` 中添加路由配置
3. 更新导航菜单（如需要）

### 使用公共组件

项目使用 monorepo 结构，可以引用其他包的组件：

```typescript
import { Captcha } from '@onebase/common';
import { Input } from '@onebase/ui-kit';
```

### 样式开发

使用 Less + CSS Modules：

```less
// index.module.less
.container {
  padding: 20px;
}
```

```typescript
import styles from './index.module.less';

<div className={styles.container}>
```

## 可用命令

| 命令           | 描述           |
| -------------- | -------------- |
| `pnpm dev`     | 启动开发服务器 |
| `pnpm build`   | 构建生产版本   |
| `pnpm preview` | 预览构建结果   |
| `pnpm lint`    | 运行代码检查   |
| `make dev`     | 启动开发模式   |
| `make build`   | 构建项目       |
| `make preview` | 预览构建结果   |

## 环境配置

### 开发环境

- 开发服务器: http://localhost:5173
- API 服务器: http://localhost:9524

### 环境变量

创建 `.env` 文件配置环境变量：

```env
VITE_API_BASE_URL=http://localhost:9524
VITE_APP_TITLE=OneBase Admin Console
```

## 部署

### 构建部署

```bash
pnpm build
```

构建完成后，将 `dist/` 目录的内容部署到 Web 服务器。

### Docker 部署

```dockerfile
FROM nginx:1.28-alpine3.21
COPY dist/ /usr/share/nginx/html/
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

## 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 作者

- [Mickey.Zhou](http://git.virtueit.net/zhoumingji)
