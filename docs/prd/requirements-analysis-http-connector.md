# HTTP连接器需求分析文档

**作者**: Claude (AI Assistant)
**日期**: 2026-01-16
**版本**: 1.0

---

## 目录

1. [产品定位与目标](#第一章产品定位与目标)
2. [功能需求](#第二章功能需求)
3. [非功能需求](#第三章非功能需求)
4. [数据模型设计](#第四章数据模型设计)
5. [流程执行设计](#第五章流程执行设计)
6. [API接口设计](#第六章api接口设计)
7. [前端界面设计](#第七章前端界面设计)
8. [技术实现方案](#第八章技术实现方案)
9. [测试计划](#第九章测试计划)
10. [实施计划与里程碑](#第十章实施计划与里程碑)

---

## 第一章：产品定位与目标

### 1.1 产品定位

HTTP连接器是OneBase v3.0 Flow流程引擎的核心外部能力集成组件，为流程提供标准化的REST API调用能力。它与Script连接器并列，共同构成连接器体系的第一批实现。

### 1.2 核心价值

1. **零代码集成外部API**：业务人员通过可视化配置即可对接任何REST API，无需编写代码
2. **配置复用与统一管理**：企业内常见的API端点（如支付服务、用户服务等）可以配置为连接器模板，在多个流程中复用
3. **安全可控**：内置SSRF防护、认证管理、敏感数据加密等企业级安全特性
4. **开发提效**：开发人员无需为每个API调用编写重复代码，专注于业务逻辑

### 1.3 目标用户

- **流程设计师**：在流程中快速集成外部API服务
- **平台管理员**：统一管理企业所有API连接器，监控使用情况
- **安全审计员**：确保外部API调用符合企业安全规范

### 1.4 与现有系统的关系

- 继承自统一的连接器框架（与Script连接器同级）
- 使用FlowConnectorController进行管理
- 通过HttpNodeComponent在流程中执行
- 数据存储在flow_connector和flow_connector_http表

---

## 第二章：功能需求

### 2.1 核心功能

#### 2.1.1 HTTP请求支持

- 支持GET、POST、PUT、PATCH、DELETE五种HTTP方法
- 支持Path、Query、Header、Body四种参数传递位置
- 支持JSON和Form两种请求体编码格式
- 支持自定义请求头（如Content-Type、Accept、User-Agent等）

#### 2.1.2 变量替换能力

- 使用`${variableName}`语法从流程上下文获取值
- 支持URL、参数、请求头、请求体中的变量替换
- 支持嵌套变量（如`${user.profile.id}`）
- 变量不存在时保持原样（便于调试）

#### 2.1.3 认证方式支持

1. **无认证**：直接调用API
2. **Basic Auth**：用户名密码认证
3. **Token认证**：Bearer Token方式
4. **自定义签名**：支持自定义签名算法和头部
5. **OAuth 2.0**：支持授权码、客户端凭证等模式

#### 2.1.4 响应处理

- 自动解析JSON响应为对象
- 非JSON响应保留原始字符串
- 返回完整的响应信息（状态码、响应头、响应体、请求耗时）
- HTTP错误状态码（4xx/5xx）自动中断流程并抛出异常

---

## 第三章：非功能需求

### 3.1 性能要求

- **响应时间**：单个HTTP请求执行时间不超过配置的超时时间（默认5秒，可配置）
- **并发能力**：支持同一流程内的多个HTTP节点并发执行
- **连接复用**：使用HttpClient连接池，复用TCP连接
- **内存效率**：大量并发请求时不应该出现内存泄漏

### 3.2 可靠性要求

#### 3.2.1 重试机制

- 网络异常（连接超时、读取超时）自动重试
- HTTP 5xx服务器错误自动重试
- HTTP 429（请求过多）自动重试
- 使用指数退避策略（100ms、200ms、400ms，最大5000ms）
- 可配置重试次数（默认0次，即不重试）

#### 3.2.2 超时控制

- 可配置连接超时时间（默认5秒）
- 可配置读取超时时间（默认10秒）
- 超时后自动中断请求并抛出异常

### 3.3 安全要求

#### 3.3.1 SSRF防护（可配置）

- 禁止访问localhost（127.0.0.1、::1）
- 禁止访问内网地址（10.x.x.x、172.16-31.x.x、192.168.x.x）
- 只允许http://和https://协议
- 提供管理员配置开关

#### 3.3.2 敏感数据保护

- 认证信息（密码、Token）加密存储
- 日志自动脱敏敏感字段
- 支持租户级隔离的连接器配置

---

## 第四章：数据模型设计

### 4.1 架构设计原则

HTTP连接器采用与Script连接器相同的架构模式：
- 使用独立的动作表（flow_connector_http）存储HTTP特有的配置
- 与flow_connector表关联，获取全局配置
- 流程定义中引用httpUuid，运行时动态加载配置

### 4.2 数据表关系图

```
flow_connector (连接器定义)
├── connector_uuid
├── connector_name: "用户服务API"
├── type_code: "HTTP"
├── config: {baseUrl, auth...}
└── 流向 ↓

flow_connector_http (HTTP动作)
├── connector_uuid (外键)
├── http_uuid
├── http_name: "获取用户信息"
├── request_method: "GET"
├── request_path: "/user/${userId}"
└── 流向 ↓

flow_process (流程定义)
└── processDefinition (JSON)
    └── nodes[].data
        ├── connectorUuid: "..."
        └── httpUuid: "..."
```

### 4.3 flow_connector 表（已有）

```sql
CREATE TABLE flow_connector (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    application_id BIGINT NOT NULL,
    connector_uuid VARCHAR(64) NOT NULL UNIQUE,
    connector_name VARCHAR(100) NOT NULL,
    code VARCHAR(100),
    type_code VARCHAR(50) NOT NULL COMMENT '连接器类型: HTTP/SCRIPT/EMAIL/SMS/DATABASE',
    description VARCHAR(500),
    config TEXT COMMENT '连接器配置(JSON)',
    config_json TEXT COMMENT '连接器配置(JSON格式)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_type_code (type_code),
    INDEX idx_application_id (application_id)
) COMMENT '连接器定义表';
```

**示例记录**：
```json
{
  "connector_uuid": "uuid-http-user-service-001",
  "connector_name": "用户服务API",
  "type_code": "HTTP",
  "description": "企业用户管理系统的REST API",
  "config": {
    "baseUrl": "https://api.company.com",
    "auth": {
      "type": "OAuth2",
      "tokenUrl": "https://api.company.com/oauth/token",
      "clientId": "onebase",
      "clientSecret": "encrypted_secret",
      "scope": "user.read user.write"
    },
    "timeout": 5000,
    "retry": 3,
    "ssrfProtection": true,
    "headers": {
      "User-Agent": "OneBase-HTTP-Connector/1.0",
      "Accept": "application/json"
    }
  }
}
```

### 4.4 flow_connector_http 表（新增）

```sql
CREATE TABLE flow_connector_http (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    application_id BIGINT NOT NULL COMMENT '应用ID',

    -- 关联连接器
    connector_uuid VARCHAR(64) NOT NULL COMMENT '所属连接器UUID',

    -- HTTP动作基本信息
    http_uuid VARCHAR(64) NOT NULL UNIQUE COMMENT 'HTTP动作UUID',
    http_name VARCHAR(100) NOT NULL COMMENT 'HTTP动作名称',
    http_code VARCHAR(100) COMMENT 'HTTP动作编码',
    description VARCHAR(500) COMMENT '动作描述',

    -- HTTP请求配置
    request_method VARCHAR(10) NOT NULL COMMENT 'HTTP方法: GET/POST/PUT/PATCH/DELETE',
    request_path VARCHAR(500) NOT NULL COMMENT '请求路径，支持变量替换如/api/users/${userId}',
    request_query TEXT COMMENT 'Query参数定义(JSON)',
    request_headers TEXT COMMENT '请求头定义(JSON)',
    request_body_type VARCHAR(20) COMMENT '请求体类型: JSON/FORM/RAW/NONE',
    request_body_template TEXT COMMENT '请求体模板，支持变量替换',

    -- 认证配置（可覆盖连接器级别的认证）
    auth_type VARCHAR(50) COMMENT '认证方式: NONE/BASIC/TOKEN/OAUTH2/CUSTOM_SIGNATURE/INHERIT',
    auth_config TEXT COMMENT '认证配置(JSON)',

    -- 响应处理
    response_mapping TEXT COMMENT '响应字段映射定义(JSON)',
    success_condition TEXT COMMENT '成功条件表达式(JSON)',

    -- 输入输出Schema（供前端生成表单）
    input_schema TEXT COMMENT '输入参数Schema(JSON)',
    output_schema TEXT COMMENT '输出参数Schema(JSON)',

    -- 高级配置
    timeout INT COMMENT '超时时间(ms)，覆盖连接器配置',
    retry_count INT COMMENT '重试次数，覆盖连接器配置',
    mock_response TEXT COMMENT 'Mock响应（用于测试）',

    -- 状态管理
    active_status INT DEFAULT 1 COMMENT '启用状态: 0-禁用, 1-启用',
    sort_order INT DEFAULT 0 COMMENT '排序',

    -- 审计字段
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by VARCHAR(100),
    update_by VARCHAR(100),

    INDEX idx_connector_uuid (connector_uuid),
    INDEX idx_application_id (application_id),
    INDEX idx_http_code (http_code),
    INDEX idx_active_status (active_status)
) COMMENT 'HTTP连接器动作配置表';
```

**示例记录**：
```json
{
  "http_uuid": "uuid-http-get-user-001",
  "connector_uuid": "uuid-http-user-service-001",
  "http_name": "获取用户信息",
  "http_code": "GET_USER_INFO",
  "description": "根据用户ID获取用户详细信息",
  "request_method": "GET",
  "request_path": "/api/v1/users/${userId}",
  "request_query": [
    {
      "name": "includeProfile",
      "type": "BOOLEAN",
      "required": false,
      "defaultValue": false
    }
  ],
  "request_headers": [
    {
      "name": "Accept",
      "value": "application/json"
    }
  ],
  "request_body_type": "NONE",
  "auth_type": "INHERIT",
  "response_mapping": {
    "userId": "$.data.id",
    "userName": "$.data.name",
    "userEmail": "$.data.email"
  },
  "success_condition": {
    "expression": "$.code == 200",
    "expectStatusCode": 200
  },
  "input_schema": [
    {
      "fieldName": "userId",
      "fieldType": "STRING",
      "fieldDesc": "用户ID",
      "required": true,
      "example": "12345"
    },
    {
      "fieldName": "includeProfile",
      "fieldType": "BOOLEAN",
      "fieldDesc": "是否包含详细资料",
      "required": false,
      "defaultValue": false
    }
  ],
  "output_schema": [
    {
      "fieldName": "userId",
      "fieldType": "STRING",
      "fieldDesc": "用户ID"
    },
    {
      "fieldName": "userName",
      "fieldType": "STRING",
      "fieldDesc": "用户姓名"
    },
    {
      "fieldName": "userEmail",
      "fieldType": "STRING",
      "fieldDesc": "用户邮箱"
    },
    {
      "fieldName": "statusCode",
      "fieldType": "INTEGER",
      "fieldDesc": "HTTP状态码"
    }
  ]
}
```

### 4.5 Java数据对象

#### FlowConnectorHttpDO

```java
package com.cmsr.onebase.module.flow.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseAppEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "flow_connector_http")
public class FlowConnectorHttpDO extends BaseAppEntity {

    @Column(value = "connector_uuid")
    private String connectorUuid;

    @Column(value = "http_uuid")
    private String httpUuid;

    @Column(value = "http_name")
    private String httpName;

    @Column(value = "http_code")
    private String httpCode;

    @Column(value = "description")
    private String description;

    @Column(value = "request_method")
    private String requestMethod;

    @Column(value = "request_path")
    private String requestPath;

    @Column(value = "request_query")
    private String requestQuery;  // JSON: List<HttpParameter>

    @Column(value = "request_headers")
    private String requestHeaders; // JSON: List<HttpHeader>

    @Column(value = "request_body_type")
    private String requestBodyType;

    @Column(value = "request_body_template")
    private String requestBodyTemplate;

    @Column(value = "auth_type")
    private String authType;

    @Column(value = "auth_config")
    private String authConfig; // JSON

    @Column(value = "response_mapping")
    private String responseMapping; // JSON

    @Column(value = "success_condition")
    private String successCondition; // JSON

    @Column(value = "input_schema")
    private String inputSchema; // JSON

    @Column(value = "output_schema")
    private String outputSchema; // JSON

    @Column(value = "timeout")
    private Integer timeout;

    @Column(value = "retry_count")
    private Integer retryCount;

    @Column(value = "mock_response")
    private String mockResponse;

    @Column(value = "active_status")
    private Integer activeStatus;

    @Column(value = "sort_order")
    private Integer sortOrder;
}
```

#### 辅助数据类

```java
// HTTP参数定义
@Data
public class HttpParameter {
    private String name;           // 参数名
    private String type;           // 类型: STRING/NUMBER/BOOLEAN/OBJECT/ARRAY
    private String location;       // 位置: PATH/QUERY/HEADER/BODY
    private Boolean required;      // 是否必填
    private Object defaultValue;   // 默认值
    private String description;    // 描述
    private String example;        // 示例值
}

// HTTP请求头定义
@Data
public class HttpHeader {
    private String name;           // Header名
    private String value;          // Header值（支持变量）
    private String description;    // 描述
    private Boolean required;      // 是否必填
}

// 响应映射定义
@Data
public class ResponseMapping {
    private String field;          // 输出字段名
    private String jsonPath;       // JSONPath表达式
    private String type;           // 字段类型
    private String description;    // 描述
}

// 参数Schema定义
@Data
public class ParameterSchema {
    private String fieldName;      // 字段名
    private String fieldType;      // 字段类型
    private String fieldDesc;      // 字段描述
    private Boolean required;      // 是否必填
    private Object defaultValue;   // 默认值
    private String example;        // 示例值
    private List<ParameterSchema> nestedFields; // 嵌套字段
}

// 成功条件定义
@Data
public class SuccessCondition {
    private String expression;     // 成功条件表达式（JSONPath）
    private Integer expectStatusCode; // 期望的HTTP状态码
    private String responseBodyPath; // 响应体路径
    private String description;    // 描述
}
```

---

## 第五章：流程执行设计

### 5.1 执行流程

```
1. 流程引擎触发HttpNodeComponent
   ↓
2. 从流程定义中获取节点配置
   - connectorUuid: 连接器UUID
   - httpUuid: HTTP动作UUID
   - runtimeData: 运行时数据（变量值）
   ↓
3. FlowGraphBuilder加载数据
   - 从flow_connector表加载连接器配置
   - 从flow_connector_http表加载HTTP动作配置
   - 合并配置到HttpNodeData
   ↓
4. 构建HTTP请求
   - 合并baseUrl + path = 完整URL
   - 解析并替换变量：${userId} → "12345"
   - 设置请求方法、请求头、请求体
   ↓
5. 执行前检查
   - SSRF防护检查（如果启用）
   - 认证信息准备（Token获取、签名计算等）
   ↓
6. 执行HTTP请求
   - 使用HttpClient发送请求
   - 超时控制
   - 失败重试（如果配置）
   ↓
7. 处理响应
   - 检查HTTP状态码
   - 4xx/5xx抛出异常，中断流程
   - 成功则解析JSON响应
   ↓
8. 输出结果
   - 将响应数据存入VariableContext
   - 后续节点可通过${节点标签.字段名}访问
```

### 5.2 变量上下文示例

```javascript
// 假设上游节点输出：
// {
//   "currentUserId": "12345",
//   "apiKey": "abc-def-ghi"
// }

// HTTP节点配置：
{
  "connectorUuid": "user-service-api",
  "httpUuid": "get-user-info",
  "runtimeData": {
    "userId": "${currentUserId}",
    "apiKey": "${apiKey}"
  }
}

// 实际发送的请求：
GET https://api.company.com/api/v1/users/12345?apiKey=abc-def-ghi
```

### 5.3 输出数据结构

```json
{
  "statusCode": 200,
  "headers": {
    "Content-Type": ["application/json"],
    "Content-Length": ["1024"]
  },
  "body": {
    "userId": "12345",
    "userName": "张三",
    "userEmail": "zhangsan@example.com"
  },
  "rawBody": "{\"userId\":\"12345\",\"userName\":\"张三\"}",
  "duration": 523
}
```

### 5.4 FlowGraphBuilder扩展

```java
private void traverseNodeAndEnrichData(Long applicationId, JsonGraphNode node) {
    // 1. ScriptNodeData 加载
    if (node.getData() instanceof ScriptNodeData scriptNodeData) {
        FlowConnectorScriptDO connectorScriptDO =
            connectorScriptRepository.findByApplicationAndUuid(...);
        scriptNodeData.setScript(connectorScriptDO.getRawScript());
        // ...
    }

    // 2. CommonNodeData 加载（用于通用连接器）
    if (node.getData() instanceof CommonNodeData commonNodeData) {
        // 加载连接器配置和节点配置
        // ...
    }

    // 3. HttpNodeData 加载（新增）
    if (node.getData() instanceof HttpNodeData httpNodeData) {
        // 从数据库加载HTTP动作配置
        FlowConnectorHttpDO httpActionDO =
            connectorHttpRepository.findByApplicationAndUuid(
                applicationId,
                httpNodeData.getHttpUuid()
            );

        // 加载连接器配置
        FlowConnectorDO connectorDO =
            connectorRepository.findByApplicationAndUuid(
                applicationId,
                httpActionDO.getConnectorUuid()
            );

        // 合并配置到节点数据
        httpNodeData.setHttpActionConfig(parseHttpAction(httpActionDO));
        httpNodeData.setConnectorConfig(parseConnector(connectorDO));
    }

    // 递归处理子节点
    if (CollectionUtils.isNotEmpty(node.getBlocks())) {
        for (JsonGraphNode child : node.getBlocks()) {
            traverseNodeAndEnrichData(applicationId, child);
        }
    }
}
```

---

## 第六章：API接口设计

### 6.1 连接器管理接口（扩展FlowConnectorController）

```
POST /flow/connector/http/create
创建HTTP连接器
Request: {
  "connectorName": "支付服务API",
  "baseUrl": "https://pay.example.com",
  "authType": "OAuth2",
  "authConfig": {...},
  "timeout": 5000,
  "retry": 3
}
Response: {"connectorId": 123, "connectorUuid": "..."}

GET /flow/connector/http/page
分页查询HTTP连接器
Query:?pageNo=1&pageSize=10&keyword=支付
Response: {
  "total": 5,
  "list": [...]
}

GET /flow/connector/http/get
获取连接器详情
Query:?id=123
Response: {
  "id": 123,
  "connectorName": "支付服务API",
  "config": {...},
  "actionCount": 8
}

POST /flow/connector/http/update
更新连接器配置
Request: {
  "id": 123,
  "authConfig": {...}
}

DELETE /flow/connector/http/delete
删除连接器
Query:?id=123
```

### 6.2 动作管理接口（新增FlowConnectorHttpController）

```
POST /flow/connector/http/action/create
创建HTTP动作
Request: {
  "connectorUuid": "...",
  "httpName": "创建订单",
  "httpCode": "CREATE_ORDER",
  "requestMethod": "POST",
  "requestPath": "/orders",
  "requestBodyType": "JSON",
  "requestBodyTemplate": "{\\"amount\\": \\"${amount}\\", \\"userId\\": \\"${userId}\\"}"
}

GET /flow/connector/http/action/page
分页查询动作列表
Query:?connectorUuid=...&pageNo=1&pageSize=10

GET /flow/connector/http/action/get
获取动作详情
Query:?id=456

POST /flow/connector/http/action/update
更新HTTP动作
Request: {
  "id": 456,
  "requestPath": "/v2/orders"
}

DELETE /flow/connector/http/action/delete
删除HTTP动作
Query:?id=456

POST /flow/connector/http/action/test
测试HTTP动作
Request: {
  "id": 456,
  "testData": {
    "userId": "12345",
    "amount": 100
  }
}
Response: {
  "success": true,
  "statusCode": 200,
  "response": {...},
  "duration": 523
}
```

---

## 第七章：前端界面设计

### 7.1 连接器管理页面

```
┌─────────────────────────────────────────────────────────────┐
│ 连接器管理                                          [+ 新建] │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│ 筛选: [全部类型▼] [搜索连接器名称____________] [刷新]        │
│                                                              │
│ ┌─────────────────────────────────────────────────────────┐ │
│ │ 连接器列表                         共 12 个连接器         │ │
│ ├─────────────────────────────────────────────────────────┤ │
│ │ ┌─────────────────────────────────────────────────────┐ │ │
│ │ │ 用户服务API                    HTTP | v1.0          │ │ │
│ │ │ api.company.com                   5个动作 | 编辑|删除│ │ │
│ │ │ OAuth2 认证                       被使用8次          │ │ │
│ │ └─────────────────────────────────────────────────────┘ │ │
│ │ ┌─────────────────────────────────────────────────────┐ │ │
│ │ │ 支付服务API                    HTTP | v1.2          │ │ │
│ │ │ pay.example.com                  3个动作 | 编辑|删除│ │ │
│ │ │ Token 认证                        被使用23次         │ │ │
│ │ └─────────────────────────────────────────────────────┘ │ │
│ └─────────────────────────────────────────────────────────┘ │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

### 7.2 连接器创建/编辑表单

```
┌─────────────────────────────────────────────────────────────┐
│ 创建HTTP连接器                                    [保存][取消]│
├─────────────────────────────────────────────────────────────┤
│                                                              │
│ 基本信息 *                                                   │
│ ┌────────────────────────────────────────────────────────┐ │
│ │ 连接器名称: [_________________________]                 │ │
│ │ 连接器编码: [user-api_____________] 自动生成            │ │
│ │ 描述:       [_________________________]                 │ │
│ └────────────────────────────────────────────────────────┘ │
│                                                              │
│ 服务配置 *                                                   │
│ ┌────────────────────────────────────────────────────────┐ │
│ │ Base URL:  [https://api.company.com_________]          │ │
│ │ 超时时间:  [5000____] ms                               │ │
│ │ 重试次数:  [3____] 次                                  │ │
│ └────────────────────────────────────────────────────────┘ │
│                                                              │
│ 认证配置 *                                                   │
│ ┌────────────────────────────────────────────────────────┐ │
│ │ 认证方式: [OAuth2.0 ▼]                                 │ │
│ │                                                          │ │
│ │ Token URL: [https://api.company.com/oauth/token____]   │ │
│ │ Client ID: [onebase_________________________________]   │ │
│ │ Client Secret: [••••••••••••________________]           │ │
│ │ Scope: [user.read user.write_______________]           │ │
│ └────────────────────────────────────────────────────────┘ │
│                                                              │
│ 安全配置                                                     │
│ ┌────────────────────────────────────────────────────────┐ │
│ │ ☑ 启用SSRF防护                                         │ │
│ │ ☐ 只允许HTTPS                                         │ │
│ └────────────────────────────────────────────────────────┘ │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

### 7.3 动作管理页面

```
┌─────────────────────────────────────────────────────────────┐
│ 用户服务API - 动作管理                        [+ 新建动作]  │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│ ┌─────────────────────────────────────────────────────────┐ │
│ │ ┌─────────────────────────────────────────────────────┐ │ │
│ │ │ 获取用户信息         GET | /user/${userId}           │ │ │
│ │ │                                   [编辑][删除][测试]│ │ │
│ │ └─────────────────────────────────────────────────────┘ │ │
│ │ ┌─────────────────────────────────────────────────────┐ │ │
│ │ │ 创建用户           POST | /users                    │ │ │
│ │ │                                   [编辑][删除][测试]│ │ │
│ │ └─────────────────────────────────────────────────────┘ │ │
│ │ ┌─────────────────────────────────────────────────────┐ │ │
│ │ │ 更新用户           PUT | /user/${userId}           │ │ │
│ │ │                                   [编辑][删除][测试]│ │ │
│ │ └─────────────────────────────────────────────────────┘ │ │
│ └─────────────────────────────────────────────────────────┘ │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

### 7.4 动作创建/编辑表单

```
┌─────────────────────────────────────────────────────────────┐
│ 创建HTTP动作                                      [保存][取消]│
├─────────────────────────────────────────────────────────────┤
│                                                              │
│ 基本信息 *                                                   │
│ ┌────────────────────────────────────────────────────────┐ │
│ │ 动作名称: [获取用户信息_____________]                   │ │
│ │ 动作编码: [GET_USER_INFO____________]                   │ │
│ │ 描述:     [根据用户ID获取用户详细信息_________]         │ │
│ └────────────────────────────────────────────────────────┘ │
│                                                              │
│ 请求配置 *                                                   │
│ ┌────────────────────────────────────────────────────────┐ │
│ │ 请求方法: [GET ▼]                                      │ │
│ │ 请求路径: [/api/v1/users/${userId}_______________]     │ │
│ │ 请求体类型: [NONE ▼]                                   │ │
│ └────────────────────────────────────────────────────────┘ │
│                                                              │
│ 参数配置                                                     │
│ ┌────────────────────────────────────────────────────────┐ │
│ │ [+ 添加Query参数] [+ 添加Header] [+ 添加Body参数]      │ │
│ │                                                          │ │
│ │ Query参数:                                              │ │
│ │ ┌────────────────────────────────────────────────────┐ │ │
│ │ │ 参数名: [includeProfile___] 类型: [BOOLEAN▼]       │ │ │
│ │ │ 必填: ☐ 默认值: [false____]         [删除]          │ │ │
│ │ └────────────────────────────────────────────────────┘ │ │
│ └────────────────────────────────────────────────────────┘ │
│                                                              │
│ 响应配置                                                     │
│ ┌────────────────────────────────────────────────────────┐ │
│ │ 成功条件: HTTP状态码 [200____] 等于                   │ │
│ │ 响应映射: [+ 添加映射]                                │ │
│ │ ┌────────────────────────────────────────────────────┐ │ │
│ │ │ 输出字段: [userId___]  ←  JSONPath: [$.data.id_] │ │ │
│ │ │                                    [删除]          │ │ │
│ │ └────────────────────────────────────────────────────┘ │ │
│ └────────────────────────────────────────────────────────┘ │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## 第八章：技术实现方案

### 8.1 核心组件设计

#### 8.1.1 HttpNodeData（节点数据模型）

```java
@Data
@EqualsAndHashCode(callSuper = true)
@NodeType("api_http")
public class HttpNodeData extends NodeData implements Serializable {

    // 流程定义中的配置
    private String connectorUuid;      // 连接器UUID
    private String httpUuid;           // HTTP动作UUID
    private Map<String, Object> runtimeData;  // 运行时数据

    // 运行时从数据库加载（transient）
    private transient HttpConnectorConfig connectorConfig;
    private transient HttpActionConfig actionConfig;
}
```

#### 8.1.2 HttpNodeComponent（执行组件）

```java
@Slf4j
@Setter
@LiteflowComponent("api_http")
public class HttpNodeComponent extends SkippableNodeComponent {

    @Autowired
    private HttpExecuteService httpExecuteService;

    @Autowired
    private FlowConnectorHttpRepository connectorHttpRepository;

    @Autowired
    private FlowConnectorRepository connectorRepository;

    @Override
    public void process() throws Exception {
        // 1. 获取上下文
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        executeContext.addLog("HTTP连接器节点开始执行");
        VariableContext variableContext = this.getContextBean(VariableContext.class);

        // 2. 获取节点配置
        HttpNodeData nodeData = (HttpNodeData) executeContext.getNodeData(this.getTag());

        // 3. 加载连接器和动作配置（如果未加载）
        loadConfigIfNeeded(nodeData);

        // 4. 构建HTTP请求
        HttpRequest request = buildRequest(nodeData, variableContext);

        // 5. 执行请求（含重试逻辑）
        HttpServiceResponse response = httpExecuteService.execute(request);

        // 6. 输出结果
        Map<String, Object> output = buildOutput(response, nodeData.getActionConfig());
        variableContext.putNodeVariables(this.getTag(), output);

        executeContext.addLog(String.format(
            "HTTP请求执行成功 - 方法: %s, URL: %s, 状态码: %d, 耗时: %dms",
            request.getMethod(), request.getUrl(),
            response.getStatusCode(), response.getDuration()
        ));
    }

    private void loadConfigIfNeeded(HttpNodeData nodeData) {
        if (nodeData.getConnectorConfig() == null) {
            FlowConnectorDO connectorDO = connectorRepository.findByUuid(
                nodeData.getConnectorUuid()
            );
            nodeData.setConnectorConfig(parseConnectorConfig(connectorDO));
        }

        if (nodeData.getActionConfig() == null) {
            FlowConnectorHttpDO httpActionDO = connectorHttpRepository.findByUuid(
                nodeData.getHttpUuid()
            );
            nodeData.setActionConfig(parseActionConfig(httpActionDO));
        }
    }
}
```

#### 8.1.3 HttpExecuteService（执行服务）

```java
@Service
public class HttpExecuteService {

    private final HttpClient httpClient;
    private final Map<String, AuthHandler> authHandlers;

    public HttpServiceResponse execute(HttpRequest request) {
        // 1. SSRF防护检查
        validateUrl(request.getUrl());

        // 2. 认证处理
        applyAuthentication(request);

        // 3. 发送请求（含重试）
        return executeWithRetry(request);
    }

    private void validateUrl(String url) {
        // SSRF防护逻辑
        // ...
    }

    private void applyAuthentication(HttpRequest request) {
        AuthHandler handler = authHandlers.get(request.getAuthType());
        if (handler != null) {
            handler.apply(request, request.getAuthConfig());
        }
    }

    private HttpServiceResponse executeWithRetry(HttpRequest request) {
        int maxRetries = request.getRetryCount() != null ? request.getRetryCount() : 0;
        int retryDelay = 100; // 初始重试延迟100ms

        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                return doExecute(request);
            } catch (Exception e) {
                if (attempt == maxRetries || !shouldRetry(e)) {
                    throw e;
                }
                try {
                    Thread.sleep(retryDelay);
                    retryDelay = Math.min(retryDelay * 2, 5000); // 指数退避，最大5秒
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("重试被中断", ie);
                }
            }
        }
    }
}
```

### 8.2 认证处理器

```java
public interface AuthHandler {
    void applyAuthentication(HttpRequest request, HttpAuthConfig config);
}

@Component
public class BasicAuthHandler implements AuthHandler {
    @Override
    public void applyAuthentication(HttpRequest request, HttpAuthConfig config) {
        String credentials = config.getUsername() + ":" + config.getPassword();
        String encoded = Base64.getEncoder().encodeToString(
            credentials.getBytes(StandardCharsets.UTF_8)
        );
        request.addHeader("Authorization", "Basic " + encoded);
    }
}

@Component
public class TokenAuthHandler implements AuthHandler {
    @Override
    public void applyAuthentication(HttpRequest request, HttpAuthConfig config) {
        request.addHeader("Authorization", "Bearer " + config.getToken());
    }
}

@Component
public class OAuth2Handler implements AuthHandler {
    @Autowired
    private OAuth2TokenService tokenService;

    @Override
    public void applyAuthentication(HttpRequest request, HttpAuthConfig config) {
        String token = tokenService.getAccessToken(config);
        request.addHeader("Authorization", "Bearer " + token);
    }
}

@Component
public class CustomSignatureHandler implements AuthHandler {
    @Override
    public void applyAuthentication(HttpRequest request, HttpAuthConfig config) {
        // 自定义签名算法
        String signature = calculateSignature(request, config);
        request.addHeader(config.getSignatureHeader(), signature);
    }
}
```

---

## 第九章：测试计划

### 9.1 单元测试

```java
@Tag("unit")
class HttpExecuteServiceTest {

    @Test
    void shouldExecuteGETRequestSuccessfully() {
        // Given
        HttpRequest request = HttpRequest.builder()
            .url("https://api.example.com/users")
            .method("GET")
            .build();

        // When
        HttpServiceResponse response = httpExecuteService.execute(request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldBlockSSRFAttack() {
        // Given
        HttpRequest request = HttpRequest.builder()
            .url("http://127.0.0.1:6379")
            .method("GET")
            .build();

        // When & Then
        assertThatThrownBy(() -> httpExecuteService.execute(request))
            .isInstanceOf(SSRFSecurityException.class)
            .hasMessageContaining("SSRF protection");
    }

    @Test
    void shouldRetryOnTimeout() {
        // Given
        HttpRequest request = HttpRequest.builder()
            .url("https://slow-api.example.com/timeout")
            .retry(3)
            .timeout(1000)
            .build();

        // When
        HttpServiceResponse response = httpExecuteService.execute(request);

        // Then
        assertThat(response.getRetryCount()).isEqualTo(3);
    }
}
```

### 9.2 集成测试

```java
@Tag("integration")
class HttpConnectorIntegrationTest {

    @Test
    void shouldExecuteHttpNodeInFlow() {
        // Given: 创建包含HTTP节点的流程
        String flowDefinition = """
        {
          "nodes": [
            {
              "id": "http1",
              "type": "api_http",
              "tag": "userApi",
              "data": {
                "connectorUuid": "user-service",
                "httpUuid": "get-user-info",
                "runtimeData": {
                  "userId": "12345"
                }
              }
            }
          ]
        }
        """;

        // When: 执行流程
        FlowExecutionResult result = flowExecutor.execute(flowDefinition);

        // Then: 验证结果
        assertThat(result.getStatus()).isEqualTo(ExecutionStatus.SUCCESS);
        Map<String, Object> output = result.getOutput("userApi");
        assertThat(output.get("statusCode")).isEqualTo(200);
        assertThat(output.get("body")).isInstanceOf(Map.class);
    }
}
```

### 9.3 性能测试

```java
@Tag("performance")
class HttpConnectorPerformanceTest {

    @Test
    void shouldHandleConcurrentRequests() {
        // Given: 100个并发请求
        int threadCount = 100;
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        // When: 并发执行
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    httpExecuteService.execute(createRequest());
                    successCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await(30, TimeUnit.SECONDS);

        // Then: 验证成功率
        assertThat(successCount.get()).isGreaterThan(95);
    }
}
```

---

## 第十章：实施计划与里程碑

### 10.1 分阶段实施

#### 第一阶段：数据模型（3天）

**目标**：建立数据模型和数据库表

**交付物**：
- flow_connector_http表创建脚本
- FlowConnectorHttpDO数据对象
- 辅助数据类（HttpParameter、HttpHeader等）
- 数据库Repository接口

**验收标准**：能够通过JPA操作flow_connector_http表

#### 第二阶段：API接口（3天）

**目标**：实现连接器和动作的CRUD接口

**交付物**：
- FlowConnectorHttpController（HTTP动作管理）
- FlowConnectorHttpService（业务逻辑）
- 相关VO类（CreateHttpActionReqVO、HttpActionVO等）
- API文档

**验收标准**：能够通过API创建、查询、更新、删除HTTP连接器和动作

#### 第三阶段：核心执行（5天）

**目标**：实现HTTP请求执行核心能力

**交付物**：
- HttpNodeData节点数据模型
- HttpNodeComponent执行组件
- HttpExecuteService执行服务
- 支持GET/POST/PUT/DELETE方法
- 基础认证（无认证、Basic Auth、Token）
- 变量替换功能

**验收标准**：能在流程中成功调用HTTP API

#### 第四阶段：高级特性（5天）

**目标**：完善企业级特性

**交付物**：
- OAuth 2.0认证
- 自定义签名认证
- 重试机制和超时控制
- SSRF防护
- JSON智能解析
- 错误处理和日志

**验收标准**：通过所有单元测试和集成测试

#### 第五阶段：前端界面（5天）

**目标**：提供可视化管理界面

**交付物**：
- 连接器管理页面
- 连接器创建/编辑表单
- 动作管理页面
- 动作创建/编辑表单
- API测试工具

**验收标准**：前端能够完整管理HTTP连接器

#### 第六阶段：测试与优化（3天）

**目标**：确保系统稳定性和性能

**交付物**：
- 完整的测试套件
- 性能测试报告
- 用户使用文档
- API文档

**验收标准**：所有测试通过，文档完整

### 10.2 里程碑

| 里程碑 | 时间 | 交付物 |
|--------|------|--------|
| M1: 数据模型 | 第1周结束 | 数据库表和DO对象 |
| M2: API接口 | 第2周结束 | CRUD接口完成 |
| M3: 核心功能 | 第3-4周结束 | 可执行的HTTP连接器 |
| M4: 高级特性 | 第5周结束 | 所有功能特性完成 |
| M5: 前端就绪 | 第6周结束 | 可视化管理界面 |
| M6: 发布准备 | 第7周结束 | 测试通过，文档完整 |

### 10.3 风险与应对

| 风险 | 影响 | 概率 | 应对措施 |
|------|------|------|----------|
| OAuth 2.0实现复杂 | 高 | 中 | 使用成熟库（如Spring Security OAuth） |
| SSRF防护误杀 | 中 | 低 | 提供白名单配置 |
| 性能瓶颈 | 中 | 低 | 连接池优化，异步处理 |
| 前端工作量超预期 | 中 | 中 | 使用现有UI组件库（Ant Design Pro） |

### 10.4 成功标准

#### 功能完整性

- ✅ 支持所有指定的认证方式
- ✅ 支持所有HTTP方法和参数类型
- ✅ 变量替换功能正常工作
- ✅ 错误处理和重试机制符合预期

#### 质量标准

- ✅ 单元测试覆盖率 ≥ 80%
- ✅ 所有集成测试通过
- ✅ 无已知严重bug

#### 性能标准

- ✅ 单个请求执行时间 < 配置超时时间
- ✅ 支持100并发请求
- ✅ 内存使用稳定，无泄漏

#### 用户体验

- ✅ 界面简洁直观
- ✅ 错误提示友好清晰
- ✅ 文档完整易懂

---

## 总结

本需求分析文档涵盖了HTTP连接器的完整设计，从产品定位、功能需求、技术实现到测试计划，为后续开发提供了清晰的指导。

### 核心设计决策

1. **数据模型**：采用独立的flow_connector_http表，与Script连接器保持一致的架构模式
2. **认证方式**：支持无认证、Basic Auth、Token、OAuth 2.0、自定义签名五种方式
3. **变量替换**：使用`${variableName}`语法，支持URL、参数、请求头、请求体中的变量替换
4. **安全防护**：提供可配置的SSRF防护，保护内网安全
5. **架构一致性**：与现有连接器（Script、Email、SMS）保持统一的管理和执行模式

### 与现有系统的关系

HTTP连接器将与Script连接器并列，共同构成OneBase连接器体系的第一批实现：

```
连接器体系
├── Script连接器（已有）
│   └── flow_connector_script表
├── HTTP连接器（本设计）
│   └── flow_connector_http表
├── Email连接器（已有）
│   └── flow_node_config表
├── SMS连接器（已有）
│   └── flow_node_config表
└── Database连接器（已有）
    └── flow_node_config表
```

通过统一连接器架构，企业可以实现：
1. API配置的复用和管理
2. 安全的认证和权限控制
3. 标准化的外部系统对接
4. 可扩展的连接器生态

预计实施周期为7周，分6个阶段逐步交付。
