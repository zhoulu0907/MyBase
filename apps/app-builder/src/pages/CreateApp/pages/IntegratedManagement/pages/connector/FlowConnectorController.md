# FlowConnectorController 接口文档

**Base Path**: `/flow/connector`

## 1. 分页查询连接器实例

*   **Summary**: 分页查询连接器实例，返回精简VO，包含实例名称、类型、环境信息、配置状态、启用状态等列表展示所需字段。
*   **Method**: `GET`
*   **Path**: `/page`

### 请求参数 (Query)

| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| pageNo | Integer | 是 | 页码，默认 1 |
| pageSize | Integer | 是 | 每页条数，默认 10 |
| applicationId | Long | 否 | 应用ID |
| connectorName | String | 否 | 连接器名称 |
| level1Code | String | 否 | Level1 编号 |
| level2Code | String | 否 | Level2 编号 |
| level3Code | String | 否 | Level3 编号 |
| typeCode | String | 否 | 连接器类型编码 |
| activeStatus | Integer | 否 | 启用状态（0-禁用，1-启用） |

### 返回结果

**类型**: `CommonResult<PageResult<FlowConnectorLiteVO>>`

**响应结构 (FlowConnectorLiteVO)**:

| 字段名 | 类型 | 描述 |
| :--- | :--- | :--- |
| id | Long | 主键ID |
| connectorUuid | String | 连接器UUID |
| connectorName | String | 连接器名称 |
| typeCode | String | 连接器类型 |
| description | String | 连接器描述 |
| envName | String | 环境名称 |
| envCode | String | 环境编码 |
| configStatus | String | 配置状态（configured-已配置, unconfigured-未配置） |
| status | String | 配置状态（前端使用，映射到 configStatus） |
| activeStatus | Integer | 启用状态（0-禁用，1-启用） |
| environment | String | 环境信息（显示用） |
| createTime | LocalDateTime | 创建时间 |

---

## 2. 获取连接器详情

*   **Summary**: 获取连接器详情
*   **Method**: `GET`
*   **Path**: `/{id}`

### 请求参数 (Path)

| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| id | Long | 是 | 连接器ID |

### 返回结果

**类型**: `CommonResult<FlowConnectorVO>`

**响应结构 (FlowConnectorVO)**:

| 字段名 | 类型 | 描述 |
| :--- | :--- | :--- |
| id | Long | 主键ID |
| applicationId | Long | 应用ID |
| connectorUuid | String | 连接器UUID |
| typeCode | String | 连接器类型编号 |
| connectorName | String | 连接器名称 |
| description | String | 连接器描述 |
| config | JsonNode | 连接器可选配置 |
| connectorVersion | String | 连接器版本 |
| createTime | LocalDateTime | 连接器创建时间 |
| envName | String | 环境名称 |
| envCode | String | 环境编码（DEV/TEST/PROD） |
| activeStatus | Integer | 启用状态（0-禁用，1-启用） |
| configStatus | String | 配置状态（configured-已配置, unconfigured-未配置） |

---

## 3. 创建连接器

*   **Summary**: 创建连接器
*   **Method**: `POST`
*   **Path**: `/create`

### 请求参数 (Body)

**类型**: `CreateFlowConnectorReqVO`

| 字段名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| applicationId | Long | 是 | 应用ID |
| connectorName | String | 是 | 连接器名称 (max 30 chars) |
| description | String | 否 | 连接器描述 (max 200 chars) |
| typeCode | String | 是 | 连接器类型 |
| config | JsonNode | 否 | 连接器可选配置 |

### 返回结果

**类型**: `CommonResult<CreateFlowConnectorRespVO>`

**响应结构 (CreateFlowConnectorRespVO)**:

| 字段名 | 类型 | 描述 |
| :--- | :--- | :--- |
| id | Long | Connector ID |
| connectorUuid | String | Connector UUID |

---

## 4. 更新连接器

*   **Summary**: 更新连接器
*   **Method**: `POST`
*   **Path**: `/{id}/update`

### 请求参数

**Path 参数**:

| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| id | Long | 是 | 连接器ID |

**Body 参数 (UpdateFlowConnectorReqVO)**:

| 字段名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| id | Long | 否 | 连接器ID (会被 Path 参数覆盖) |
| connectorName | String | 否 | 连接器名称 |
| description | String | 否 | 连接器描述 |
| config | JsonNode | 否 | 连接器可选配置 |

### 返回结果

**类型**: `CommonResult<Boolean>`

---

## 5. 更新连接器基本信息

*   **Summary**: 更新连接器基本信息，只更新描述信息，自动检测变化。
*   **Method**: `POST`
*   **Path**: `/{id}/update-base-info`

### 请求参数

**Path 参数**:

| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| id | Long | 是 | 连接器ID |

**Body 参数 (UpdateFlowConnectorReqVO)**:

同上 "更新连接器" 接口。

### 返回结果

**类型**: `CommonResult<Boolean>`

---

## 6. 删除连接器

*   **Summary**: 删除连接器
*   **Method**: `POST`
*   **Path**: `/{id}/delete`

### 请求参数 (Path)

| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| id | Long | 是 | 连接器ID |

### 返回结果

**类型**: `CommonResult<Boolean>`

---

## 7. 查询所有连接器实例

*   **Summary**: 查询所有连接器实例
*   **Method**: `GET`
*   **Path**: `/list-all`

### 请求参数 (Query)

| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| pageNo | Integer | 是 | 页码 |
| pageSize | Integer | 是 | 每页条数 |

### 返回结果

**类型**: `CommonResult<PageResult<FlowConnectorLiteVO>>`

**响应结构**: 同 "分页查询连接器实例"。

---

## 8. 启用/禁用连接器实例

*   **Summary**: 启用或禁用连接器实例
*   **Method**: `POST`
*   **Path**: `/{id}/update-status`

### 请求参数

**Path 参数**:

| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| id | Long | 是 | 连接器实例ID |

**Query 参数**:

| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| activeStatus | Integer | 是 | 启用状态（0-禁用，1-启用） |

### 返回结果

**类型**: `CommonResult<Boolean>`

---

## 9. 根据连接器类型查询实例列表

*   **Summary**: 返回指定类型的所有连接器实例，不包含分页
*   **Method**: `GET`
*   **Path**: `/by-type/{typeCode}`

### 请求参数 (Path)

| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| typeCode | String | 是 | 连接器类型编码（如 DATABASE_MYSQL、HTTP） |

### 返回结果

**类型**: `CommonResult<List<FlowConnectorVO>>`

**响应结构**: 同 "获取连接器详情" 的 `FlowConnectorVO` 列表。

---

## 10. 查询连接器的环境配置列表

*   **Summary**: 从 flow_connector.config 字段解析环境配置信息
*   **Method**: `GET`
*   **Path**: `/{id}/environments`

### 请求参数 (Path)

| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| id | Long | 是 | 连接器实例ID |

### 返回结果

**类型**: `CommonResult<List<FlowConnectorEnvLiteVO>>`

**响应结构 (FlowConnectorEnvLiteVO)**:

| 字段名 | 类型 | 描述 |
| :--- | :--- | :--- |
| id | Long | 环境配置编号 |
| envUuid | String | 环境配置UUID |
| envName | String | 环境名称 |
| envCode | String | 环境编码 |
| typeCode | String | 连接器类型编号 |
| envUrl | String | 环境URL |
| authType | String | 认证方式 |
| description | String | 描述 |
| activeStatus | Integer | 启用状态 |
| createTime | LocalDateTime | 创建时间 |

---

## 11. 查询连接器的指定环境配置信息

*   **Summary**: 从 flow_connector.config 的 properties 中解析出指定环境的 Formily Schema
*   **Method**: `GET`
*   **Path**: `/{id}/environment-config`

### 请求参数

**Path 参数**:

| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| id | Long | 是 | 连接器实例ID |

**Query 参数**:

| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| envName | String | 是 | 环境名称 |

### 返回结果

**类型**: `CommonResult<EnvironmentConfigVO>`

**响应结构 (EnvironmentConfigVO)**:

| 字段名 | 类型 | 描述 |
| :--- | :--- | :--- |
| schema | JsonNode | 动作的 Formily Schema |
| envCode | String | 环境编码 |
| typeCode | String | 连接器类型编号 |

---

## 12. 获取环境配置模板

*   **Summary**: 获取连接器类型对应的环境配置 Formily Schema 模板，用于创建环境信息
*   **Method**: `GET`
*   **Path**: `/{id}/env-config-template`

### 请求参数 (Path)

| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| id | Long | 是 | 连接器实例ID |

### 返回结果

**类型**: `CommonResult<EnvConfigTemplateVO>`

**响应结构 (EnvConfigTemplateVO)**:

| 字段名 | 类型 | 描述 |
| :--- | :--- | :--- |
| schema | JsonNode | 环境配置的 Formily Schema |

---

## 13. 保存连接器环境配置

*   **Summary**: 保存新的环境配置到 connector.config，如果环境已存在则拒绝
*   **Method**: `POST`
*   **Path**: `/{id}/save-env`

### 请求参数

**Path 参数**:

| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| id | Long | 是 | 连接器实例ID |

**Body 参数 (SaveEnvironmentConfigReqVO)**:

| 字段名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| config | JsonNode | 是 | 环境配置对象，包含 envMode 和 envConfig |

### 返回结果

**类型**: `CommonResult<Boolean>`

---

## 14. 更新连接器环境配置

*   **Summary**: 更新已存在的环境配置，环境必须存在，否则拒绝
*   **Method**: `POST`
*   **Path**: `/{id}/update-env`

### 请求参数

**Path 参数**:

| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| id | Long | 是 | 连接器实例ID |

**Body 参数 (SaveEnvironmentConfigReqVO)**:

同上 "保存连接器环境配置"。

### 返回结果

**类型**: `CommonResult<Boolean>`

---

## 15. 设置启用环境

*   **Summary**: 设置连接器当前启用的环境，环境必须存在。传空表示取消启用。
*   **Method**: `POST`
*   **Path**: `/{id}/enable-env`

### 请求参数

**Path 参数**:

| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| id | Long | 是 | 连接器实例ID |

**Query 参数**:

| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| envName | String | 否 | 环境名称（传空表示取消启用） |

### 返回结果

**类型**: `CommonResult<Boolean>`

---

## 16. 获取启用环境名称

*   **Summary**: 获取连接器当前启用的环境名称，用于编辑回显。未设置则返回null。
*   **Method**: `GET`
*   **Path**: `/{id}/get-enabled-envname`

### 请求参数 (Path)

| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| id | Long | 是 | 连接器实例ID |

### 返回结果

**类型**: `CommonResult<String>`

---

## 17. 获取动作配置模板

*   **Summary**: 获取连接器类型对应的动作配置 Formily Schema 模板，用于创建动作信息
*   **Method**: `GET`
*   **Path**: `/{id}/action-config-template`

### 请求参数 (Path)

| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| id | Long | 是 | 连接器实例ID |

### 返回结果

**类型**: `CommonResult<ActionConfigTemplateVO>`

**响应结构 (ActionConfigTemplateVO)**:

| 字段名 | 类型 | 描述 |
| :--- | :--- | :--- |
| schema | JsonNode | 动作配置的 Formily Schema |

---

## 18. 保存连接器动作配置

*   **Summary**: 保存新的动作配置到 connector.action_config，如果动作已存在则拒绝
*   **Method**: `POST`
*   **Path**: `/{id}/save-action`

### 请求参数

**Path 参数**:

| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| id | Long | 是 | 连接器实例ID |

**Body 参数 (SaveActionConfigReqVO)**:

| 字段名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| actionConfig | JsonNode | 是 | 动作配置（包含 basic, requestHeaders, requestBody, queryParams, pathParams） |

### 返回结果

**类型**: `CommonResult<Boolean>`

---

## 19. 更新连接器动作配置

*   **Summary**: 更新指定动作名称的动作配置，动作必须已存在
*   **Method**: `POST`
*   **Path**: `/{id}/actions/{actionName}/update-config`

### 请求参数

**Path 参数**:

| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| id | Long | 是 | 连接器实例ID |
| actionName | String | 是 | 动作名称 |

**Body 参数 (SaveActionConfigReqVO)**:

同上 "保存连接器动作配置"。

### 返回结果

**类型**: `CommonResult<Boolean>`

---

## 20. 查询连接器动作清单（精简版）

*   **Summary**: 返回动作名称数组。业务规则：必须先设置启用环境（enableEnvName），否则返回错误'未正确配置环境信息'。
*   **Method**: `GET`
*   **Path**: `/{id}/actions`

### 请求参数 (Path)

| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| id | Long | 是 | 连接器实例ID |

### 返回结果

**类型**: `CommonResult<List<String>>`

---

## 21. 获取连接器的动作列表

*   **Summary**: 返回连接器的动作配置列表
*   **Method**: `GET`
*   **Path**: `/{id}/action-infos`

### 请求参数 (Path)

| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| id | Long | 是 | 连接器实例ID |

### 返回结果

**类型**: `CommonResult<List<ConnectorActionLiteVO>>`

**响应结构 (ConnectorActionLiteVO)**:

| 字段名 | 类型 | 描述 |
| :--- | :--- | :--- |
| actionName | String | 动作名称 |
| description | String | 动作描述 |
| status | String | 状态：1-已发布，2-已下架 |
| createTime | String | 创建时间 |
| updateTime | String | 更新时间 |

---

## 22. 获取动作详情

*   **Summary**: 获取动作详情
*   **Method**: `GET`
*   **Path**: `/{connectorId}/actions/{actionName}`

### 请求参数 (Path)

| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| connectorId | Long | 是 | 连接器实例ID |
| actionName | String | 是 | 动作名称 |

### 返回结果

**类型**: `CommonResult<ConnectorActionVO>`

**响应结构 (ConnectorActionVO)**:

| 字段名 | 类型 | 描述 |
| :--- | :--- | :--- |
| actionName | String | 动作名称 |
| description | String | 动作描述 |
| status | String | 状态：1-已发布，2-已下架 |
| createTime | String | 创建时间 |
| updateTime | String | 更新时间 |
| basicInfo | Object | 基础信息配置 |
| inputConfig | Object | 入参配置 |
| outputConfig | Object | 出参配置 |
| debugConfig | Object | 调试配置 |

---

## 23. 保存动作草稿

*   **Summary**: 保存动作草稿
*   **Method**: `POST`
*   **Path**: `/{connectorId}/actions`

### 请求参数

**Path 参数**:

| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| connectorId | Long | 是 | 连接器实例ID |

**Body 参数 (CreateConnectorActionReqVO)**:

| 字段名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| actionName | String | 是 | 动作名称 (max 128 chars) |
| actionCode | String | 是 | 动作编码 (max 64 chars) |
| description | String | 否 | 动作描述 (max 512 chars) |
| basicInfo | JsonNode | 否 | 基础信息配置 |
| inputConfig | JsonNode | 否 | 入参配置 |
| outputConfig | JsonNode | 否 | 出参配置 |
| debugConfig | JsonNode | 否 | 调试配置 |

### 返回结果

**类型**: `CommonResult<String>` (Action Name)

---

## 24. 更新动作草稿

*   **Summary**: 更新动作草稿
*   **Method**: `POST`
*   **Path**: `/{connectorId}/actions/{actionName}/update-draft`

### 请求参数

**Path 参数**:

| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| connectorId | Long | 是 | 连接器实例ID |
| actionName | String | 是 | 动作名称 |

**Body 参数 (UpdateConnectorActionReqVO)**:

| 字段名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| actionName | String | 否 | 动作名称 (max 128 chars) |
| description | String | 否 | 动作描述 (max 512 chars) |
| basicInfo | JsonNode | 否 | 基础信息配置 |
| inputConfig | JsonNode | 否 | 入参配置 |
| outputConfig | JsonNode | 否 | 出参配置 |
| debugConfig | JsonNode | 否 | 调试配置 |

### 返回结果

**类型**: `CommonResult<Boolean>`

---

## 25. 发布动作

*   **Summary**: 发布动作
*   **Method**: `POST`
*   **Path**: `/{connectorId}/actions/{actionName}/publish`

### 请求参数 (Path)

| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| connectorId | Long | 是 | 连接器实例ID |
| actionName | String | 是 | 动作名称 |

### 返回结果

**类型**: `CommonResult<Boolean>`

---

## 26. 下架动作

*   **Summary**: 下架动作
*   **Method**: `POST`
*   **Path**: `/{connectorId}/actions/{actionName}/offline`

### 请求参数 (Path)

| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| connectorId | Long | 是 | 连接器实例ID |
| actionName | String | 是 | 动作名称 |

### 返回结果

**类型**: `CommonResult<Boolean>`

---

## 27. 重新上线动作

*   **Summary**: 重新上线动作
*   **Method**: `POST`
*   **Path**: `/{connectorId}/actions/{actionName}/republish`

### 请求参数 (Path)

| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| connectorId | Long | 是 | 连接器实例ID |
| actionName | String | 是 | 动作名称 |

### 返回结果

**类型**: `CommonResult<Boolean>`

---

## 28. 复制动作

*   **Summary**: 复制动作
*   **Method**: `POST`
*   **Path**: `/{connectorId}/actions/{actionName}/copy`

### 请求参数 (Path)

| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| connectorId | Long | 是 | 连接器实例ID |
| actionName | String | 是 | 动作名称 |

### 返回结果

**类型**: `CommonResult<String>` (New Action Name)
