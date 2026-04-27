# HTTP 动作配置数据结构 - OpenAPI 兼容格式

## 概述

HTTP 连接器动作配置采用兼容 OpenAPI 3.0 Operation 对象的格式保存，额外字段放在 `x-onebase` 扩展字段中。

## 数据结构定义

### 完整类型定义

```typescript
/** HTTP 动作配置 - 兼容 OpenAPI 格式 */
interface HttpActionConfig {
  // ========== OpenAPI 标准字段 ==========
  /** 请求路径（相对于服务器地址） */
  path: string;

  /** HTTP 方法 */
  method: 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH';

  /** 操作摘要 */
  summary?: string;

  /** 操作ID（可作为动作名） */
  operationId?: string;

  /** 描述 */
  description?: string;

  /** 标签 */
  tags?: string[];

  /** 请求参数（包含 header, query, path） */
  parameters?: OpenApiParameter[];

  /** 请求体 */
  requestBody?: OpenApiRequestBody;

  /** 响应定义 */
  responses?: {
    [statusCode: string]: OpenApiResponse;
  };

  // ========== OneBase 扩展字段 ==========
  'x-onebase'?: OneBaseExtension;

  // ========== 调试配置（非 OpenAPI 标准）==========
  debug?: {
    url: string;
    method: string;
    requestHeaders: Array<{ key: string; fieldValue: string }>;
    requestBody: Array<{ key: string; fieldValue: string }>;
    queryParams: Array<{ key: string; fieldValue: string }>;
    pathParams: Array<{ key: string; fieldValue: string }>;
  };
}
```

### 参数对象

```typescript
/** OpenAPI 参数对象 */
interface OpenApiParameter {
  name: string;
  in: 'header' | 'query' | 'path';
  required?: boolean;
  description?: string;
  schema?: {
    type: string;
    default?: string;
    example?: string;
  };
}
```

### 请求体

```typescript
/** OpenAPI 请求体 */
interface OpenApiRequestBody {
  description?: string;
  required?: boolean;
  content: {
    'application/json'?: {
      schema?: object;
      example?: object;
    };
    'multipart/form-data'?: {
      schema?: object;
    };
  };
}
```

### 响应

```typescript
/** OpenAPI 响应 */
interface OpenApiResponse {
  description?: string;
  content?: {
    'application/json'?: {
      schema?: object;
      example?: object;
    };
  };
}
```

### OneBase 扩展

```typescript
/** OneBase 扩展字段 */
interface OneBaseExtension {
  /** 动作名称 */
  actionName: string;

  /** 动作描述 */
  actionDescription?: string;

  /** 成功条件 */
  successCondition?: {
    conditions: Array<{
      field: string;        // 如 "$.success" 或 "statusCode"
      operator: string;     // eq, ne, contains 等
      value: string;        // 期望值
    }>;
    errorMessagePath?: string;  // 错误消息路径，如 "$.message"
  };

  /** 动作入参（对外暴露的参数） */
  inputs?: Array<{
    key: string;
    fieldName: string;
    fieldType: string;
    required: boolean;
    defaultValue?: string;
    description?: string;
    map: {
      in: 'header' | 'query' | 'path' | 'body';
      name: string;
    };
  }>;

  /** 动作出参（对外暴露的响应字段） */
  outputs?: Array<{
    key: string;
    fieldName: string;
    fieldType: string;
    description?: string;
    map: {
      in: 'header' | 'body';
      path: string;  // JSON Path，如 "$.data.id"
    };
  }>;

  /** 调试配置 */
  debug?: {
    paramValues?: Record<string, string>;
  };
}
```

## 示例数据

### 完整示例

```json
{
  "path": "/build/metadata/data-card/query",
  "method": "POST",
  "summary": "数据卡片批量查询",
  "operationId": "dataCardBatchQuery",
  "parameters": [
    {
      "name": "X-Tenant-Id",
      "in": "header",
      "required": true,
      "description": "租户 ID",
      "schema": { "type": "string" }
    },
    {
      "name": "X-Application-Id",
      "in": "header",
      "required": true,
      "description": "应用 ID",
      "schema": { "type": "string" }
    }
  ],
  "requestBody": {
    "required": true,
    "content": {
      "application/json": {
        "schema": {
          "type": "array",
          "items": { "type": "object" }
        },
        "example": [
          { "label": { "text": "本月发布文章数" }, "tableName": "n851_info" }
        ]
      }
    }
  },
  "responses": {
    "200": {
      "description": "成功",
      "content": {
        "application/json": {
          "schema": {
            "type": "array",
            "items": {
              "type": "object",
              "properties": {
                "success": { "type": "boolean" },
                "message": { "type": "string" }
              }
            }
          },
          "example": [
            { "success": true, "message": "success", "value": 128 }
          ]
        }
      }
    }
  },
  "x-onebase": {
    "actionName": "数据卡片批量查询_构建态",
    "successCondition": {
      "conditions": [
        { "field": "$.success", "operator": "eq", "value": "true" },
        { "field": "statusCode", "operator": "eq", "value": "200" }
      ],
      "errorMessagePath": "$.message"
    },
    "inputs": [
      {
        "key": "sender",
        "fieldName": "发送者",
        "fieldType": "string",
        "required": false,
        "description": "发送者用户名",
        "map": {
          "in": "header",
          "name": "X-Sender"
        }
      }
    ],
    "outputs": [
      {
        "key": "success",
        "fieldName": "是否成功",
        "fieldType": "boolean",
        "description": "请求是否成功",
        "map": {
          "in": "body",
          "path": "$.success"
        }
      },
      {
        "key": "message",
        "fieldName": "消息",
        "fieldType": "string",
        "description": "返回消息",
        "map": {
          "in": "body",
          "path": "$.message"
        }
      }
    ]
  },
  "debug": {
    "url": "/build/metadata/data-card/query",
    "method": "POST",
    "requestHeaders": [
      { "key": "Content-Type", "fieldValue": "application/json" },
      { "key": "X-Tenant-Id", "fieldValue": "default" }
    ],
    "requestBody": [
      { "key": "body", "fieldValue": "[{\"label\":{\"text\":\"test\"},\"tableName\":\"test\"}]" }
    ],
    "queryParams": [],
    "pathParams": []
  }
}
```

## 字段映射说明

### OpenAPI 标准字段

| OpenAPI 字段 | 来源 | 说明 |
|-------------|------|------|
| `path` | `url` | 请求路径，相对路径 |
| `method` | `method` | HTTP 方法 |
| `summary` | `basic.actionName` | 操作摘要 |
| `operationId` | `basic.actionName` | 操作ID（自动生成） |
| `description` | `basic.actionDescription` | 详细描述 |
| `parameters` | 请求参数 | 包含 header, query, path |
| `requestBody` | 请求体 | JSON 或 form-data |
| `responses` | 响应定义 | 响应 schema 和 example |

### x-onebase 扩展字段

| 扩展字段 | 来源 | 说明 |
|---------|------|------|
| `actionName` | `basic.actionName` | 动作名称 |
| `actionDescription` | `basic.actionDescription` | 动作描述 |
| `successCondition` | `successCondition` | 成功条件 |
| `inputs` | `inputs` | 动作入参 |
| `outputs` | `outputs` | 动作出参 |

### debug 调试字段

| 调试字段 | 说明 |
|---------|------|
| `url` | 完整请求路径 |
| `method` | 请求方法 |
| `requestHeaders` | 请求头列表（已替换变量） |
| `requestBody` | 请求体列表（已替换变量） |
| `queryParams` | Query 参数列表（已替换变量） |
| `pathParams` | Path 参数列表（已替换变量） |

## 后端接口要求

### 保存接口

**请求体**:
```json
{
  "actionConfig": {
    // HttpActionConfig 完整对象
  }
}
```

### 获取接口

**响应体**:
```json
{
  // HttpActionConfig 完整对象
  // 或者旧格式数据（兼容）
}
```

### 格式检测

前端会根据响应数据是否包含 `path` 和 `method` 字段来判断是新格式还是旧格式：
- 新格式：使用 `openApiConfigToFormValues` 转换
- 旧格式：使用 `actionConfigToFormValues` 转换

## 兼容性说明

1. **旧数据兼容**：编辑已保存的旧格式动作时，前端会自动检测并使用旧的转换函数
2. **新数据格式**：新建或编辑后保存的数据使用新的 OpenAPI 兼容格式
3. **导入 OpenAPI**：导入 OpenAPI 文件时，数据会自动转换为表单格式，保存时再转换为 OpenAPI 格式

## 变更文件

| 文件 | 改动内容 |
|------|----------|
| `types.ts` | 新增 `HttpActionConfig` 类型定义 |
| `transform.ts` | 新增 `formValuesToOpenApiConfig` 和 `openApiConfigToFormValues` 函数 |
| `index.tsx` | 修改保存和加载逻辑，使用新的转换函数 |