# OneBase V3 FE 访问指南

## 一、访问地址

### 默认路径模式

网关统一入口：`http://SERVER`（默认端口 80）

| 服务 | 地址 | 说明 |
|------|------|------|
| Admin Console | http://`SERVER`/console/ | 管理控制台（平台管理） |
| App Builder | http://`SERVER`/appbuilder/ | 应用构建器（租户使用） |
| Runtime | http://`SERVER`/runtime/ | 应用运行时 |
| Mobile Editor | http://`SERVER`/mobile-editor/ | 移动端编辑器 |
| Mobile Runtime | http://`SERVER`/mobile-runtime/ | 移动端运行时 |

> `SERVER` 为部署服务器地址，如 `10.0.1.200` 或 `onebase.example.com`
> 网关端口默认为 80，可在 `.env` 中通过 `NGINX_PORT` 修改

---

## 二、首次登录流程

### 步骤 1：登录管理控制台

1. 访问 Admin Console：`http://SERVER/console/`
2. 使用管理员账号登录（由后端服务配置）

### 步骤 2：创建租户

1. 登录后进入平台管理界面
2. 在租户管理中新建租户：
   - 填写租户名称
   - 设置租户标识
   - 配置租户权限
3. 保存租户信息

### 步骤 3：进入 App Builder

1. 访问 App Builder：`http://SERVER/appbuilder/`
2. 选择刚创建的租户
3. 使用租户账号登录

### 步骤 4：创建应用

1. 进入 App Builder 后，点击「新建应用」
2. 填写应用基本信息：
   - 应用名称
   - 应用类型（Web / Mobile）
   - 应用描述
3. 进入应用编辑器开始设计

---

## 三、应用访问

创建应用后，可通过 Runtime 访问：

| 运行时 | 地址格式 | 示例 |
|--------|----------|------|
| Web Runtime | `http://SERVER/runtime/?app=APP_ID` | `http://10.0.1.200/runtime/?app=123` |
| Mobile Runtime | `http://SERVER/mobile-runtime/?app=APP_ID` | `http://10.0.1.200/mobile-runtime/?app=123` |

---

## 四、常见问题

### Q1: 登录后无法创建租户？

检查管理员账号是否有平台管理权限，联系后端服务确认权限配置。

### Q2: App Builder 无法访问？

确认：
1. 租户已正确创建
2. 使用租户账号登录 App Builder（不是管理员账号）
3. 后端服务正常运行

### Q3: 应用无法运行？

确认：
1. 应用已发布
2. Runtime 服务正常启动
3. 应用 ID 正确传递（URL 中的 `app` 参数）

---

## 五、API 访问

### 后端 API 代理

路径模式下，前端网关自动代理后端 API：

| API 路径 | 说明 | 用途 |
|----------|------|------|
| `/build-api/` | 编辑态 API | App Builder 使用 |
| `/runtime-api/` | 运行态 API | Runtime 使用 |
| `/flink-api/` | Flink API | 数据处理（可选） |

**示例**：
```bash
# 编辑态 API
curl http://SERVER/build-api/app/list

# 运行态 API
curl http://SERVER/runtime-api/app/123/run

# Flink API
curl http://SERVER/flink-api/jobs/overview
```

### API 与前端配置对应

前端 `config.js` 中的 `BASE_URL` 配置：
```javascript
BASE_URL: 'http://SERVER/build-api'  // 编辑态
```

---

## 六、快速验证流程

```bash
# 1. 检查服务状态（包含网关）
./deploy.sh ps

# 2. 访问网关健康检查
curl http://SERVER/health

# 3. 访问 Admin Console
curl http://SERVER/console/

# 4. 访问 App Builder
curl http://SERVER/appbuilder/

# 5. 检查配置文件
curl http://SERVER/appbuilder/config.js
# 应返回 window.global_config = {...}
```