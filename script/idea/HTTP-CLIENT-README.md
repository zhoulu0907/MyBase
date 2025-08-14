# HTTP Client 环境配置使用说明

## 概述
本项目使用 IntelliJ IDEA 的 HTTP Client 功能进行接口测试，通过全局环境配置文件 `http-client.env.json` 实现跨文件变量共享。

## 环境配置文件位置
```
/script/idea/http-client.env.json
```

## 环境说明
- **local**: 本地开发环境
- **dev**: 开发环境
- **test**: 测试环境
- **gateway**: 网关环境

## 使用步骤

### 1. 选择环境
在 IntelliJ IDEA 中：
1. 打开任意 `.http` 文件
2. 在编辑器右上角选择环境（local/dev/test/gateway）
3. 所选环境的变量将自动应用到所有 HTTP 请求中

### 2. 获取 Token
1. 首先运行 `AuthController.http` 中的登录接口
2. 登录成功后，token 会自动更新到全局环境变量中
3. 其他 HTTP 文件可直接使用更新后的 token

### 3. 使用其他接口
登录成功并获取 token 后，可以直接运行其他模块的 HTTP 测试文件，如：
- `TenantController.http`
- 其他 Controller 的 HTTP 文件

## 变量说明
- `{{baseUrl}}`: 服务基础地址
- `{{token}}`: 认证令牌，登录后自动更新
- `{{adminTenantId}}`: 管理员租户ID
- `{{tag}}`: 请求标签

## 自动 Token 更新机制
AuthController.http 中的登录接口包含响应脚本，会自动将返回的 accessToken 更新到全局环境变量中：

```javascript
> {%
// 登录成功后自动更新token到环境变量
if (response.status === 200) {
    const responseData = response.body;
    if (responseData.code === 0 && responseData.data && responseData.data.accessToken) {
        client.global.set("token", responseData.data.accessToken);
        console.log("Token已更新: " + responseData.data.accessToken);
    }
}
%}
```

## 优势
1. **跨文件共享**: 所有 HTTP 文件都使用相同的环境变量
2. **环境切换**: 一键切换不同环境，无需修改每个文件
3. **自动更新**: Token 自动更新，无需手动复制粘贴
4. **统一管理**: 所有环境配置集中管理

## 注意事项
1. 确保先在 AuthController.http 中登录获取 token
2. 选择正确的环境对应实际的服务地址
3. Token 有过期时间，过期后需要重新登录
