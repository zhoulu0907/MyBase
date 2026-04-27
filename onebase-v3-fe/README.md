# OneBase V3 Frontend

OneBase 平台前端项目，基于 React + TypeScript + Vite 构建的现代化前端应用。

## 项目结构

```
onebase-v3-fe/
├── apps/
│   └── admin-console/     # 管理控制台应用
│   └── app-builder/       # 管理应用
├── packages/
│   ├── common/           # 公共包
│   ├── ui-kit/          # UI组件库
│   └── platform-center/  # 用户中心服务包
├── scripts/              # 脚本文件
└── pnpm-workspace.yaml   # pnpm workspace 配置
```

## 快速开始

### 前置要求

- Node.js >= v22.17.0
- pnpm >= 9.15.9

### 首次使用

如果是第一次使用项目，需要先初始化：

```bash
# 克隆项目
git clone https://xxx.git

# 初始化项目（下载子模块并切换到dev分支）
make init

# 安装依赖
make install
```

### 安装依赖

```bash
# 初始化项目（首次使用）
make init

# 安装所有依赖
make install

# 或者使用脚本
./scripts/dev.sh
```

### 开发模式

```bash
# 启动所有项目的开发模式
make dev

# 或者使用脚本（交互式选择）
./scripts/dev.sh

# 仅启动特定项目
make dev-admin      # 仅管理控制台
make dev-common     # 仅公共包
make dev-ui         # 仅UI组件库
```

### 构建项目

#### 克隆仓库

```bash
git clone https://xxx.git

# 初始化项目（下载子模块并切换到dev分支）
make init

# 或者手动执行
git submodule update --init
# 进入对应的子项目，切换分支
cd ${TARGET_SUB_MUDULE}
git checkout ${TARGET_BRANCH}
```

#### 构建所有项目

```bash
make build

# 或者使用脚本（交互式选择）
./scripts/build.sh

# 仅构建特定项目
make build-admin    # 仅管理控制台
make build-packages # 仅所有包
```

## 可用命令

### 根目录命令 (Makefile)

| 命令                  | 描述                                    |
| --------------------- | --------------------------------------- |
| `make help`           | 显示所有可用命令                        |
| `make init`           | 初始化项目（下载子模块并切换到dev分支） |
| `make install`        | 安装所有依赖                            |
| `make dev`            | 启动所有项目的开发模式                  |
| `make dev-admin`      | 仅启动管理控制台开发模式                |
| `make dev-common`     | 仅启动公共包开发模式                    |
| `make dev-ui`         | 仅启动UI组件库开发模式                  |
| `make build`          | 构建所有项目                            |
| `make build-admin`    | 仅构建管理控制台                        |
| `make build-packages` | 仅构建所有包                            |
| `make clean`          | 清理所有构建文件                        |
| `make lint`           | 运行代码检查                            |
| `make preview`        | 预览构建结果                            |

#### npm 脚本命令

| 命令                  | 描述                 |
| --------------------- | -------------------- |
| `pnpm encrypt:config` | 运行配置文件加密工具 |

### 脚本命令

| 脚本                 | 描述               |
| -------------------- | ------------------ |
| `./scripts/dev.sh`   | 交互式开发启动脚本 |
| `./scripts/build.sh` | 交互式构建脚本     |

### 配置文件加密工具

项目提供了配置文件加密工具，用于生成生产环境的加密配置。

#### 使用方法

```bash
# 使用 npm 脚本运行（推荐）
pnpm encrypt:config

# 或直接使用 tsx 运行
npx tsx scripts/encrypt-config.ts
```

#### 使用步骤

1. **编辑配置对象**

   打开 `scripts/encrypt-config.ts` 文件，修改 `obj` 对象中的配置项：

   ```typescript
   const obj = {
     ENVIRONMENT: 'runtime',
     APP_KEY: 'onebase',
     APP_SECRET: 'xxx',
     BASE_URL: 'xxx',
     RUNTIME_BASE_URL: 'xxx',
     FILE_DETAIL_URL: 'xxx',
     PUBLIC_KEY: 'xxx'
   };
   ```

2. **运行加密脚本**

   执行加密命令后，脚本会：
   - 显示原始配置对象
   - 使用 SM2 算法加密配置
   - 输出加密后的字符串
   - 自动解密验证配置正确性

3. **使用加密结果**

   将输出的加密字符串复制到生产环境的 `config.js` 文件中：

   ```javascript
   window.global_config = {
     CONFIG: '加密后的字符串'
   };
   ```

#### 注意事项

- 加密使用的密钥定义在 `packages/common/src/utils/crypto.ts` 中
- 生产环境会自动使用私钥解密配置
- 请妥善保管私钥，不要泄露到公共仓库

### 子项目命令

每个子项目都有自己的 Makefile，可以在对应目录下使用：

```bash
# 在 apps/admin-console/ 目录下
make dev      # 启动开发服务器
make build    # 构建应用
make preview  # 预览构建结果

# 在 packages/common/ 目录下
make dev      # 启动开发模式
make build    # 构建包

# 在 packages/ui-kit/ 目录下
make dev      # 启动开发模式
make build    # 构建包

# 在 packages/platform-center/ 目录下
make dev      # 启动开发模式
make build    # 构建包
```

## 开发指南

### 管理控制台 (apps/admin-console)

基于 React + Vite 的管理控制台应用，使用 Arco Design 组件库。

- 开发服务器: http://localhost:5173
- 构建输出: `dist/` 目录

### 公共包 (packages/common)

包含项目中的公共工具函数、类型定义等。

- 开发模式: 监听文件变化并自动重新构建
- 构建输出: `dist/` 目录

### UI组件库 (packages/ui-kit)

可复用的UI组件库。

- 开发模式: 监听文件变化并自动重新构建
- 构建输出: `dist/` 目录

### 接口文档

链接: https://apifox.com/apidoc/shared-2faf1349-8c61-4d4f-bf66-f264dbc9dd81?pwd=UUkEVsvp

访问密码: UUkEVsvp

## 技术栈

- **框架**: React 18
- **构建工具**: Vite
- **语言**: TypeScript
- **包管理**: pnpm
- **UI组件**: Arco Design
- **路由**: React Router DOM
- **代码检查**: ESLint

## 贡献指南

1. Clone 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 作者

- [Mickey.Zhou](http://git.virtueit.net/zhoumingji)
