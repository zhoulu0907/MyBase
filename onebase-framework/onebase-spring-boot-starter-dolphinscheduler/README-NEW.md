# DolphinScheduler Spring Boot Starter

DolphinScheduler 客户端 Spring Boot Starter，用于在 Spring Boot 应用中便捷地调用 DolphinScheduler API。

## 功能特性

- ✅ 基于 Retrofit2 实现的类型安全的 HTTP 客户端
- ✅ Spring Boot 自动配置，开箱即用
- ✅ 统一的异常处理机制
- ✅ 支持请求重试
- ✅ 支持日志级别配置
- ✅ 完整的类型定义（DTO、枚举）
- ✅ 全面的连通性测试

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.cmsr.onebase</groupId>
    <artifactId>onebase-spring-boot-starter-dolphinscheduler</artifactId>
    <version>${revision}</version>
</dependency>
```

### 2. 配置参数

在 `application.yml` 中添加配置：

```yaml
onebase:
  dolphinscheduler:
    # DolphinScheduler 服务基础 URL（必填）
    baseUrl: http://10.0.104.33:12345/dolphinscheduler/v2/
    # 访问认证 token（必填）
    token: your-access-token
    # 连接超时时间（可选，默认 5 秒）
    connectTimeout: 5s
    # 读取超时时间（可选，默认 30 秒）
    readTimeout: 30s
    # 写入超时时间（可选，默认 30 秒）
    writeTimeout: 30s
    # 日志级别（可选，默认 BASIC）：NONE/BASIC/HEADERS/BODY
    logLevel: BASIC
    # 是否启用重试（可选，默认 true）
    retryEnabled: true
    # 最大重试次数（可选，默认 3 次）
    maxRetries: 3
```

### 3. 使用示例

#### 注入 API 接口

```java
@Service
public class ProjectService {

    @Resource
    private ProjectApi projectApi;

    /**
     * 创建项目
     */
    public ProjectDTO createProject(String name, String description) {
        try {
            ProjectCreateRequestDTO request = new ProjectCreateRequestDTO();
            request.setProjectName(name);
            request.setDescription(description);
            
            Response<ResultDTO<ProjectDTO>> response = projectApi.createProject(request).execute();
            
            if (response.isSuccessful() && response.body() != null) {
                ResultDTO<ProjectDTO> result = response.body();
                if (result.getSuccess()) {
                    return result.getData();
                }
            }
            
            throw new RuntimeException("创建项目失败");
            
        } catch (IOException e) {
            throw new RuntimeException("API 调用异常", e);
        }
    }

    /**
     * 查询项目列表
     */
    public List<ProjectDTO> listProjects(String searchVal, int pageNo, int pageSize) {
        try {
            Response<ResultDTO<PageInfoDTO<ProjectDTO>>> response = 
                    projectApi.queryProjectListPaging(searchVal, pageNo, pageSize).execute();
            
            if (response.isSuccessful() && response.body() != null) {
                ResultDTO<PageInfoDTO<ProjectDTO>> result = response.body();
                if (result.getSuccess() && result.getData() != null) {
                    return result.getData().getTotalList();
                }
            }
            
            return Collections.emptyList();
            
        } catch (IOException e) {
            throw new RuntimeException("查询项目列表失败", e);
        }
    }
}
```

## API 接口说明

### ProjectApi（项目管理）

| 方法 | 说明 | 请求方式 |
|------|------|----------|
| `createProject` | 创建项目 | POST |
| `queryProjectByCode` | 通过编码查询项目 | GET |
| `updateProject` | 更新项目 | PUT |
| `deleteProject` | 删除项目 | DELETE |
| `queryProjectListPaging` | 分页查询项目列表 | GET |
| `queryAllProjectList` | 查询所有项目 | GET |
| `queryAllProjectListForDependent` | 查询 Dependent 节点所有项目 | GET |
| `queryProjectCreatedAndAuthorizedByUser` | 查询授权和用户创建的项目 | GET |
| `queryAuthorizedProject` | 查询授权项目 | GET |
| `queryUnauthorizedProject` | 查询未授权的项目 | GET |
| `queryAuthorizedUser` | 查询拥有项目授权的用户 | GET |

## 异常处理

Starter 提供了统一的异常处理机制：

- `DolphinSchedulerException`：统一异常类
- `DolphinSchedulerErrorCode`：错误码枚举

### 常见错误码

| 错误码 | 说明 |
|--------|------|
| `DS_UNKNOWN` | 未知错误 |
| `DS_NETWORK` | 网络异常 |
| `DS_TIMEOUT` | 请求超时 |
| `DS_AUTH` | 认证失败 |
| `DS_TOKEN_INVALID` | Token 无效或已过期 |
| `DS_PERMISSION_DENIED` | 权限不足 |
| `DS_PROJECT_NOT_FOUND` | 项目不存在 |
| `DS_API_FAILED` | API 调用失败 |
| `DS_PARAM_INVALID` | 参数校验失败 |

### 异常处理示例

```java
try {
    Response<ResultDTO<ProjectDTO>> response = projectApi.queryProjectByCode(projectCode).execute();
    // 处理响应
} catch (DolphinSchedulerException e) {
    log.error("DolphinScheduler 调用失败: code={}, msg={}", e.getErrorCode(), e.getErrorMsg());
    // 业务处理
} catch (IOException e) {
    log.error("网络异常", e);
    // 网络异常处理
}
```

## DTO 说明

### 请求 DTO

- `ProjectCreateRequestDTO`：项目创建请求
- `ProjectUpdateRequestDTO`：项目更新请求
- `ProjectQueryDTO`：项目查询请求

### 响应 DTO

- `ResultDTO<T>`：通用响应结果
- `PageInfoDTO<T>`：分页信息

### 实体 DTO

- `ProjectDTO`：项目信息
- `UserDTO`：用户信息

## 测试

项目提供了完整的连通性测试：

```bash
mvn test -Dtest=ProjectApiTest
```

测试类位于：`src/test/java/com/cmsr/onebase/framework/dolphins/ProjectApiTest.java`

## 注意事项

1. **Token 管理**：请妥善保管 DolphinScheduler 的访问 Token，建议使用配置中心管理
2. **超时配置**：根据实际网络环境调整超时时间
3. **重试策略**：根据业务需求调整重试次数和策略
4. **日志级别**：生产环境建议使用 `BASIC` 或 `NONE`，避免记录敏感信息

## 技术栈

- Spring Boot 3.4.5
- Retrofit 2.11.0
- OkHttp 4.12.0
- Jackson
- Lombok

## 作者

matianyu

## 更新日期

2025-01-17
