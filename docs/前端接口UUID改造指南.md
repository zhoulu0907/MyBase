# 前端接口UUID改造指南

> **更新日期**：2025年12月2日  
> **更新内容**：后端Metadata模块完成UUID改造，部分接口参数字段名称发生变化

---

## 一、总体变化说明

### 1.1 核心变化

本次改造将原有的 **Long类型ID** 标识符全面升级为 **String类型UUID**，主要影响：

| 旧字段名 | 新字段名 | 说明 |
|---------|---------|------|
| `datasourceId` | `datasourceUuid` | 数据源标识 |
| `entityId` | `entityUuid` | 业务实体标识 |
| `fieldId` | `fieldUuid` | 字段标识 |
| `sourceEntityId` | `sourceEntityUuid` | 源实体标识（关系） |
| `targetEntityId` | `targetEntityUuid` | 目标实体标识（关系） |
| `sourceFieldId` | `sourceFieldUuid` | 源字段标识（关系） |
| `targetFieldId` | `targetFieldUuid` | 目标字段标识（关系） |
| `selectFieldId` | `selectFieldUuid` | 选择字段标识（关系） |
| `groupId` | `groupUuid` | 规则组标识 |
| `parentRuleId` | `parentRuleUuid` | 父规则标识 |

### 1.2 向后兼容说明

- **请求参数**：部分接口仍支持传入旧字段名（如 `id`），但建议尽快切换到UUID字段
- **响应数据**：返回数据中同时包含 `id`（Long）和 `xxxUuid`（String）字段，建议前端优先使用UUID字段

---

## 二、数据源接口 `/metadata/datasource`

### 2.1 接口清单

| 接口路径 | 请求方式 | 参数变化 |
|---------|---------|---------|
| `/datasource/page` | POST | 无变化 |
| `/datasource/get` | POST | `id` 参数保持Long类型 |
| `/datasource/delete` | POST | `id` 参数保持Long类型 |
| `/datasource/create` | POST | 返回值变为String类型UUID |
| `/datasource/update` | POST | 可使用 `datasourceUuid` 定位记录 |
| `/datasource/test-connection` | POST | 无变化 |
| `/datasource/list` | POST | 无变化 |

### 2.2 响应数据变化

```json
// 响应示例
{
  "id": 1024,
  "datasourceUuid": "01onal1s-0000-0000-0000-000000000001",
  "datasourceName": "主数据源",
  ...
}
```

**建议**：后续关联其他对象时，优先使用 `datasourceUuid` 字段。

---

## 三、业务实体接口 `/metadata/business-entity`

### 3.1 接口清单

| 接口路径 | 请求方式 | 参数变化 |
|---------|---------|---------|
| `/business-entity/create` | POST | 使用 `datasourceUuid` 关联数据源 |
| `/business-entity/update` | POST | 可使用 `entityUuid` 定位记录 |
| `/business-entity/delete` | POST | `id` 参数保持Long类型 |
| `/business-entity/list-by-datasource` | POST | **改为 `datasourceUuid` 参数** |
| `/business-entity/er-diagram` | POST | **改为 `datasourceUuid` 参数** |

### 3.2 创建/更新请求参数

```json
// BusinessEntitySaveReqVO
{
  "id": "1024",                    // 更新时必填（String类型）
  "entityUuid": "01onal1s-...",    // 更新时可用于定位记录
  "displayName": "用户实体",
  "datasourceUuid": "01onal1s-...", // 【重要】使用UUID关联数据源
  "tableName": "sys_user",
  ...
}
```

### 3.3 响应数据变化

```json
{
  "id": 1024,
  "entityUuid": "01onal1s-0000-0000-0000-000000000002",
  "datasourceUuid": "01onal1s-0000-0000-0000-000000000001",
  "displayName": "用户实体",
  ...
}
```

---

## 四、字段接口 `/metadata/entity-field`

### 4.1 接口清单

| 接口路径 | 请求方式 | 参数变化 |
|---------|---------|---------|
| `/entity-field/page` | POST | 使用 `entityId`(实际为entityUuid) 查询 |
| `/entity-field/field-types` | POST | 无变化 |
| `/entity-field/list` | POST | 使用 `entityId`(实际为entityUuid) 查询 |
| `/entity-field/create` | POST | **使用 `entityUuid` 关联实体** |
| `/entity-field/update` | POST | 可使用 `fieldUuid` 定位记录 |
| `/entity-field/get` | POST | `id` 参数为String类型（可传UUID） |
| `/entity-field/delete` | POST | `id` 参数保持Long类型 |
| `/entity-field/batch-save` | POST | **使用 `entityUuid`** |
| `/entity-field/validation-types/query` | POST | 使用 `fieldUuids` 批量查询 |

### 4.2 创建/更新请求参数

```json
// EntityFieldSaveReqVO
{
  "id": "3001",                      // 更新时必填
  "fieldUuid": "01onal1s-...",       // 更新时可用于定位记录
  "entityUuid": "01onal1s-...",      // 【必填】关联实体UUID
  "fieldName": "username",
  "displayName": "用户名",
  "fieldType": "VARCHAR",
  ...
}
```

### 4.3 响应数据变化

```json
{
  "id": 3001,
  "fieldUuid": "01onal1s-0000-0000-0000-000000000003",
  "entityUuid": "01onal1s-0000-0000-0000-000000000002",
  "fieldName": "username",
  ...
}
```

---

## 五、实体关系接口 `/metadata/entity-relationship`

### 5.1 接口清单

| 接口路径 | 请求方式 | 参数变化 |
|---------|---------|---------|
| `/entity-relationship/page` | POST | 使用 `entityId`(实际为entityUuid) 查询 |
| `/entity-relationship/create` | POST | **全部使用UUID关联** |
| `/entity-relationship/update` | POST | **全部使用UUID关联** |
| `/entity-relationship/delete` | POST | `id` 参数保持Long类型 |
| `/entity-relationship/create-parent-child` | POST | **使用UUID关联** |
| `/entity-relationship/entity-with-children` | POST | `entityId` 参数保持Long类型 |

### 5.2 创建/更新请求参数（重要变化）

```json
// EntityRelationshipSaveReqVO
{
  "id": "5001",                       // 更新时必填
  "relationshipUuid": "01onal1s-...", // 更新时可用于定位记录
  "relationName": "用户订单关系",
  "sourceEntityUuid": "01onal1s-...", // 【必填】源实体UUID
  "targetEntityUuid": "01onal1s-...", // 【必填】目标实体UUID
  "relationshipType": "ONE_TO_MANY",
  "sourceFieldUuid": "01onal1s-...",  // 【必填】源字段UUID
  "targetFieldUuid": "01onal1s-...",  // 【必填】目标字段UUID
  "selectFieldUuid": "01onal1s-...",  // 可选，选择字段UUID
  "cascadeType": "READ",
  ...
}
```

### 5.3 主子关系创建

```json
// ParentChildRelationshipSaveReqVO
{
  "parentEntityUuid": "01onal1s-...", // 父实体UUID
  "childEntityUuid": "01onal1s-...",  // 子实体UUID
  ...
}
```

---

## 六、数据方法接口 `/metadata/data-method`

### 6.1 接口清单

| 接口路径 | 请求方式 | 参数变化 |
|---------|---------|---------|
| `/data-method/list` | POST | `entityId` 参数可传UUID |
| `/data-method/detail` | POST | `entityId` 参数可传UUID |

### 6.2 请求参数

```json
// DataMethodQueryReqVO
{
  "entityId": "01onal1s-...", // 实体UUID
  "methodType": "QUERY",
  "keyword": ""
}
```

---

## 七、校验规则分组接口 `/metadata/validation-rule-group`

### 7.1 接口清单

| 接口路径 | 请求方式 | 参数变化 |
|---------|---------|---------|
| `/validation-rule-group/page` | POST | 无变化 |

---

## 八、校验规则接口

### 8.1 范围校验 `/metadata/validation/range`

| 接口路径 | 请求方式 | 参数变化 |
|---------|---------|---------|
| `/range/get-by-field` | POST | **参数名为 `id`，值为 `fieldUuid`** |
| `/range/create` | POST | **使用 `fieldUuid`** |
| `/range/update` | POST | 使用 `id`（规则组ID，Long类型） |
| `/range/delete-by-field` | POST | **参数名为 `id`，值为 `fieldUuid`** |
| `/range/get` | GET | `id` 参数为Long类型（规则组ID） |
| `/range/delete` | POST | `id` 参数为Long类型（规则组ID） |

**创建请求参数**：
```json
// ValidationRangeSaveReqVO
{
  "fieldUuid": "01onal1s-...",   // 【必填】字段UUID
  "rgName": "金额范围校验",       // 【必填】规则组名称
  "rangeType": "NUMBER",
  "minValue": 0,
  "maxValue": 10000,
  ...
}
```

### 8.2 格式校验 `/metadata/validation/format`

| 接口路径 | 请求方式 | 参数变化 |
|---------|---------|---------|
| `/format/get-regex-by-field` | POST | **参数名为 `id`，值为 `fieldUuid`** |
| `/format/get` | GET | `id` 参数为Long类型 |
| `/format/create` | POST | **使用 `fieldUuid`** |
| `/format/update` | POST | 使用 `id`（Long类型） |
| `/format/delete-by-field` | POST | **参数名为 `id`，值为 `fieldUuid`** |
| `/format/delete` | POST | `id` 参数为Long类型 |

### 8.3 长度校验 `/metadata/validation/length`

| 接口路径 | 请求方式 | 参数变化 |
|---------|---------|---------|
| `/length/get-by-field` | POST | **参数名为 `id`，值为 `fieldUuid`** |
| `/length/create` | POST | **使用 `fieldUuid`** |
| `/length/update` | POST | 使用 `id`（Long类型） |
| `/length/delete-by-field` | POST | **参数名为 `id`，值为 `fieldUuid`** |
| `/length/get` | GET | `id` 参数为Long类型 |
| `/length/delete` | POST | `id` 参数为Long类型 |

### 8.4 唯一校验 `/metadata/validation/unique`

| 接口路径 | 请求方式 | 参数变化 |
|---------|---------|---------|
| `/unique/get-by-field` | POST | **参数名为 `id`，值为 `fieldUuid`** |
| `/unique/create` | POST | **使用 `fieldUuid`** |
| `/unique/update` | POST | 使用 `id`（Long类型） |
| `/unique/delete-by-field` | POST | **参数名为 `id`，值为 `fieldUuid`** |
| `/unique/get` | GET | `id` 参数为Long类型 |
| `/unique/delete` | POST | `id` 参数为Long类型 |

### 8.5 必填校验 `/metadata/validation/required`

| 接口路径 | 请求方式 | 参数变化 |
|---------|---------|---------|
| `/required/get-by-field` | POST | **参数名为 `id`，值为 `fieldUuid`** |
| `/required/create` | POST | **使用 `fieldUuid` 和 `entityUuid`** |
| `/required/update` | POST | 使用 `id`（Long类型） |
| `/required/delete-by-field` | POST | **参数名为 `id`，值为 `fieldUuid`** |
| `/required/get` | GET | `id` 参数为Long类型 |
| `/required/delete` | POST | `id` 参数为Long类型 |

**创建请求参数**：
```json
// ValidationRequiredSaveReqVO
{
  "entityUuid": "01onal1s-...",   // 【必填】业务实体UUID
  "fieldUuid": "01onal1s-...",    // 【必填】字段UUID
  "rgName": "必填校验",           // 【必填】规则组名称
  "isEnabled": 1,
  ...
}
```

### 8.6 子表非空校验 `/metadata/validation/child-not-empty`

| 接口路径 | 请求方式 | 参数变化 |
|---------|---------|---------|
| `/child-not-empty/get-by-field` | POST | **参数名为 `id`，值为 `fieldUuid`** |
| `/child-not-empty/get` | GET | `id` 参数为Long类型 |
| `/child-not-empty/create` | POST | **使用 `fieldUuid`** |
| `/child-not-empty/update` | POST | 使用 `id`（Long类型） |
| `/child-not-empty/delete-by-field` | POST | **参数名为 `id`，值为 `fieldUuid`** |
| `/child-not-empty/delete` | POST | `id` 参数为Long类型 |

---

## 九、自定义验证规则组 `/metadata/validation-self-defined`

### 9.1 接口清单

| 接口路径 | 请求方式 | 参数变化 |
|---------|---------|---------|
| `/validation-self-defined/create` | POST | 可使用 `entityUuid` |
| `/validation-self-defined/update` | POST | 可使用 `groupUuid` 定位记录 |
| `/validation-self-defined/delete` | POST | `id` 参数为Long类型 |
| `/validation-self-defined/get` | POST | `id` 参数为Long类型 |

### 9.2 创建/更新请求参数

```json
// ValidationRuleGroupSaveReqVO
{
  "id": 1024,                       // 更新时必填
  "groupUuid": "01onal1s-...",      // 更新时可用于定位记录
  "rgName": "客户信用评级规则",
  "rgDesc": "用于识别高价值潜力的客户",
  "rgStatus": 1,
  "entityUuid": "01onal1s-...",     // 业务实体UUID
  "validationType": "SELF_DEFINED",
  "valueRules": [[...], [...]]
}
```

---

## 十、前端修改清单

### 10.1 高优先级修改（必须修改）

1. **实体关系创建/更新**：将 `sourceEntityId`、`targetEntityId`、`sourceFieldId`、`targetFieldId`、`selectFieldId` 改为对应的UUID字段
2. **字段创建/更新**：将 `entityId` 改为 `entityUuid`
3. **业务实体创建/更新**：将 `datasourceId` 改为 `datasourceUuid`
4. **所有校验规则创建**：确保使用 `fieldUuid` 而非 `fieldId`

### 10.2 建议修改（兼容但建议修改）

1. **存储标识符**：前端存储的实体标识建议从 `id` 切换到 `xxxUuid`
2. **组件间传参**：组件间传递标识符时建议使用UUID

### 10.3 无需修改

1. **删除操作**：大部分删除操作的 `id` 参数仍为Long类型
2. **分页查询**：分页接口参数基本无变化

---

## 十一、UUID格式说明

本系统使用的UUID格式示例：
```
01onal1s-0000-0000-0000-000000000001
```

- 总长度：36个字符（包含4个连字符）
- 格式：8-4-4-4-12
- 类型：String

---

## 十二、联系方式

如有疑问，请联系后端开发人员。
