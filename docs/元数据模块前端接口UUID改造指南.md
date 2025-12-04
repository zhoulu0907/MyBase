# 元数据模块前端接口 UUID 改造指南

> **文档版本**: v2.0  
> **更新日期**: 2025-12-03  
> **适用范围**: onebase-v3-fe 前端项目

## 一、改造背景

后端已完成 Metadata 模块的 ID/UUID 兼容改造。

- **VO 层（RequestBody）**: 已支持同时传入 `xxxId`（String 类型）或 `xxxUuid`（String 类型）参数，后端会自动转换
- **Controller 层（RequestParam）**: 已支持同时传入 Long ID（数字字符串）或 UUID 参数

本文档说明各接口当前状态，指导前端开发人员正确使用接口。

## 二、改造原则

1. **VO 层已兼容**: `@RequestBody` 的接口，后端已支持 `xxxId` 和 `xxxUuid` 两种参数
2. **Controller 层已兼容**: `@RequestParam("id") String id` 的接口，后端支持传入 Long ID 或 UUID
3. **保持向后兼容**: 前端可继续使用 Long 类型 ID（以字符串形式传入）

## 三、接口改造状态总览

| 模块 | VO层（RequestBody）| Controller层（RequestParam） |
|------|-------------------|----------------------------|
| 数据源 `/datasource/*` | ✅ 已兼容 | ✅ /get、/delete 已支持 ID/UUID |
| 业务实体 `/business-entity/*` | ✅ 已兼容 | ✅ /get、/delete 已支持 ID/UUID |
| 实体字段 `/entity-field/*` | ✅ 已兼容 | ✅ /delete 已支持 ID/UUID |
| 数据方法 `/data-method/*` | ✅ 已兼容 | ✅ 无需改造 |
| 规则组 `/validation-rule-group/*` | ✅ 已兼容 | ✅ /delete 已支持 String ID |
| 实体关系 `/entity-relationship/*` | ✅ 已兼容 | ✅ /get、/delete 已支持 ID/UUID |
| 校验规则（6类） | ✅ 已兼容 | ✅ /get、/delete 已支持 String ID |
| 自定义验证规则 `/validation-self-defined/*` | ✅ 已兼容 | ✅ /get、/delete 已支持 String ID |

---

## 四、详细接口状态说明

### 4.1 数据源模块 `/metadata/datasource`

#### ✅ 所有接口已支持
| 接口 | 方法 | 说明 |
|------|------|------|
| `POST /page` | `@RequestBody` | 分页查询，无ID参数 |
| `POST /list` | `@RequestBody` | 列表查询，无ID参数 |
| `POST /create` | `@RequestBody` | 创建数据源 |
| `POST /test-connection` | `@RequestBody` | 测试连接 |
| `POST /get-by-code` | `@RequestParam("code")` | 按code查询 |
| `POST /update` | `@RequestBody` | 更新，VO已支持兼容 |
| `POST /get` | `@RequestParam("id") String id` | ✅ 支持 ID 或 UUID |
| `POST /delete` | `@RequestParam("id") String id` | ✅ 支持 ID 或 UUID |

#### ✅ 已改造接口调用示例

**1. 获取数据源详情**
```java
// 后端代码
@PostMapping("/get")
public CommonResult<DatasourceRespVO> getDatasource(@RequestParam("id") String id)
```
```javascript
// 前端调用方式（两种均可）
POST /metadata/datasource/get?id=164329365983232001  // Long ID
POST /metadata/datasource/get?id=ds-xxxx-xxxx-xxxx   // UUID
```

**2. 删除数据源**
```java
// 后端代码
@PostMapping("/delete")
public CommonResult<Boolean> deleteDatasource(@RequestParam("id") String id)
```
```javascript
// 前端调用方式（两种均可）
POST /metadata/datasource/delete?id=164329365983232001  // Long ID
POST /metadata/datasource/delete?id=ds-xxxx-xxxx-xxxx   // UUID
```

---

### 4.2 业务实体模块 `/metadata/business-entity`

#### ✅ 所有接口已支持
| 接口 | 方法 | 说明 |
|------|------|------|
| `POST /create` | `@RequestBody` | VO已支持 `datasourceId`/`datasourceUuid` 兼容 |
| `POST /update` | `@RequestBody` | VO已支持兼容字段 |
| `POST /page` | `@RequestBody` | 分页查询 |
| `POST /list-by-datasource` | `@RequestParam("datasourceUuid")` | ✅ 已使用 UUID |
| `POST /er-diagram` | `@RequestParam("datasourceUuid")` | ✅ 已使用 UUID |
| `POST /list-by-app` | `@RequestParam("appId")` | 应用ID，非实体ID |
| `POST /get` | `@RequestParam("id") String id` | ✅ 支持 ID 或 UUID |
| `POST /delete` | `@RequestParam("id") String id` | ✅ 支持 ID 或 UUID |

#### ✅ 已改造接口调用示例

**1. 获取实体详情**
```java
// 后端代码
@PostMapping("/get")
public CommonResult<BusinessEntityRespVO> getBusinessEntity(@RequestParam("id") String id)
```
```javascript
// 前端调用方式（两种均可）
POST /metadata/business-entity/get?id=164329365983232001  // Long ID
POST /metadata/business-entity/get?id=entity-xxxx-xxxx   // UUID
```

**2. 删除实体**
```java
// 后端代码
@PostMapping("/delete")
public CommonResult<Boolean> deleteBusinessEntity(@RequestParam("id") String id)
```
```javascript
// 前端调用方式（两种均可）
POST /metadata/business-entity/delete?id=164329365983232001  // Long ID
POST /metadata/business-entity/delete?id=entity-xxxx-xxxx   // UUID
```

#### ✅ VO 层已支持兼容（@RequestBody）

**创建/更新实体**
```javascript
// 方案1：推荐 - 使用 datasourceUuid
POST /metadata/business-entity/create
{
  "datasourceUuid": "ds-xxxx-xxxx-xxxx",
  "entityName": "xxx"
}

// 方案2：兼容 - 使用 datasourceId（字符串类型）
{
  "datasourceId": "164329365983232001",
  "entityName": "xxx"
}
```

---

### 4.3 实体字段模块 `/metadata/entity-field`

#### ✅ 所有接口已支持
| 接口 | 方法 | 说明 |
|------|------|------|
| `POST /field-types` | 无参数 | 获取字段类型 |
| `POST /page` | `@RequestBody` | 已使用 `entityUuid` |
| `POST /list` | `@RequestBody` | 已使用 `entityUuid` |
| `POST /get` | `@RequestParam("id") String id` | ✅ 已改为 String 类型 |
| `POST /create` | `@RequestBody` | VO已支持 `entityId`/`entityUuid` 兼容 |
| `POST /update` | `@RequestBody` | VO已支持兼容字段 |
| `POST /batch-save` | `@RequestBody` | 已使用 `entityUuid` |
| `POST /batch-create` | `@RequestBody` | 已使用 UUID |
| `POST /batch-update` | `@RequestBody` | 已使用 UUID |
| `POST /validation-types/query` | `@RequestBody` | 已使用 `fieldUuids` |
| `POST /delete` | `@RequestParam("id") String id` | ✅ 支持 ID 或 UUID |

#### ✅ 已改造接口调用示例

**删除字段**
```java
// 后端代码
@PostMapping("/delete")
public CommonResult<Boolean> deleteEntityField(@RequestParam("id") String id)
```
```javascript
// 前端调用方式（两种均可）
POST /metadata/entity-field/delete?id=164329365983232003  // Long ID
POST /metadata/entity-field/delete?id=field-xxxx-xxxx    // UUID
```

---

### 4.4 实体关系模块 `/metadata/entity-relationship`

#### ✅ 所有接口已支持
| 接口 | 方法 | 说明 |
|------|------|------|
| `POST /page` | `@RequestBody` | 分页查询 |
| `POST /create` | `@RequestBody` | VO已支持 `xxxId`/`xxxUuid` 兼容 |
| `POST /update` | `@RequestBody` | VO已支持兼容字段 |
| `POST /create-parent-child` | `@RequestBody` | VO已支持兼容字段 |
| `POST /relationship-types` | 无参数 | 获取关系类型 |
| `POST /cascade-types` | 无参数 | 获取级联类型 |
| `POST /entity-with-children` | `@RequestParam` | 使用 entityId Long |
| `POST /app-entities` | `@RequestParam` | 使用 appId |
| `POST /get` | `@RequestParam("id") String id` | ✅ 支持 ID 或 UUID |
| `POST /delete` | `@RequestParam("id") String id` | ✅ 支持 ID 或 UUID |

#### ✅ 已改造接口调用示例

**1. 获取关系详情**
```java
// 后端代码
@PostMapping("/get")
public CommonResult<EntityRelationshipRespVO> getEntityRelationship(@RequestParam("id") String id)
```
```javascript
// 前端调用方式（两种均可）
POST /metadata/entity-relationship/get?id=164329365983232001  // Long ID
POST /metadata/entity-relationship/get?id=rel-xxxx-xxxx      // UUID
```

**2. 删除关系**
```java
// 后端代码
@PostMapping("/delete")
public CommonResult<Boolean> deleteEntityRelationship(@RequestParam("id") String id)
```
```javascript
// 前端调用方式（两种均可）
POST /metadata/entity-relationship/delete?id=164329365983232001  // Long ID
POST /metadata/entity-relationship/delete?id=rel-xxxx-xxxx      // UUID
```

#### ✅ VO 层已支持兼容

**创建关系**
```javascript
// 方案1：推荐 - 使用 xxxUuid
POST /metadata/entity-relationship/create
{
  "sourceEntityUuid": "entity-xxxx-xxxx-xxxx",
  "targetEntityUuid": "entity-yyyy-yyyy-yyyy",
  "sourceFieldUuid": "field-xxxx-xxxx-xxxx",
  "targetFieldUuid": "field-yyyy-yyyy-yyyy"
}

// 方案2：兼容 - 使用 xxxId（字符串类型）
{
  "sourceEntityId": "164329365983232001",
  "targetEntityId": "164329365983232002",
  "sourceFieldId": "164329365983232003",
  "targetFieldId": "164329365983232004"
}
```

---

### 4.5 校验规则模块（6类统一说明）

适用于以下 6 类校验规则：
- `/metadata/validation/range` - 范围校验
- `/metadata/validation/format` - 格式校验
- `/metadata/validation/length` - 长度校验
- `/metadata/validation/unique` - 唯一校验
- `/metadata/validation/required` - 必填校验
- `/metadata/validation/child-not-empty` - 子表非空校验

#### ✅ 所有接口已支持
| 接口 | 方法 | 说明 |
|------|------|------|
| `POST /get-by-field` | `@RequestParam("id") String fieldUuid` | ✅ 已使用 UUID |
| `POST /delete-by-field` | `@RequestParam("id") String fieldUuid` | ✅ 已使用 UUID |
| `POST /create` | `@RequestBody` | VO已支持 `fieldId`/`fieldUuid` 兼容 |
| `POST /update` | `@RequestBody` | 更新规则 |
| `GET /get` | `@RequestParam("id") String id` | ✅ 支持 String ID |
| `POST /delete` | `@RequestParam("id") String id` | ✅ 支持 String ID |

#### ✅ 已改造接口调用示例

**1. 获取规则详情（通过规则组ID）**
```java
// 后端代码
@GetMapping("/get")
public CommonResult<ValidationRangeRespVO> get(@RequestParam("id") String id)
```
```javascript
// 前端调用方式
GET /metadata/validation/range/get?id=164329365983232010
```

**2. 删除规则（通过规则组ID）**
```java
// 后端代码
@PostMapping("/delete")
public CommonResult<Boolean> delete(@RequestParam("id") String id)
```
```javascript
// 前端调用方式
POST /metadata/validation/range/delete?id=164329365983232010
```

#### ✅ VO 层已支持兼容

**创建规则**
```javascript
// 方案1：推荐 - 使用 fieldUuid
POST /metadata/validation/range/create
{
  "fieldUuid": "field-xxxx-xxxx-xxxx",
  "rgName": "范围校验规则",
  "minValue": 0,
  "maxValue": 100
}

// 方案2：兼容 - 使用 fieldId（字符串类型）
{
  "fieldId": "164329365983232003",
  "rgName": "范围校验规则",
  "minValue": 0,
  "maxValue": 100
}
```

#### 子表非空校验特殊说明

子表非空校验使用的是 `entityUuid`/`childEntityUuid`：

```javascript
// 方案1：推荐
{
  "entityUuid": "entity-xxxx-xxxx-xxxx",
  "childEntityUuid": "entity-yyyy-yyyy-yyyy",
  "rgName": "子表非空校验"
}

// 方案2：兼容
{
  "entityId": "164329365983232001",
  "childEntityId": "164329365983232002",
  "rgName": "子表非空校验"
}
```

---

### 4.6 自定义验证规则组 `/metadata/validation-self-defined`

#### ✅ 所有接口已支持
| 接口 | 方法 | 说明 |
|------|------|------|
| `POST /create` | `@RequestBody` | VO已支持 `entityUuid` |
| `POST /update` | `@RequestBody` | VO已支持 `entityUuid` |
| `POST /get` | `@RequestParam("id") String id` | ✅ 支持 String ID |
| `POST /delete` | `@RequestParam("id") String id` | ✅ 支持 String ID |

#### ✅ 已改造接口调用示例

**1. 获取规则详情**
```java
// 后端代码
@PostMapping("/get")
public CommonResult<ValidationRuleGroupRespVO> get(@RequestParam("id") String id)
```
```javascript
// 前端调用方式
POST /metadata/validation-self-defined/get?id=164329365983232010
```

**2. 删除规则**
```java
// 后端代码
@PostMapping("/delete")
public CommonResult<Boolean> delete(@RequestParam("id") String id)
```
```javascript
// 前端调用方式
POST /metadata/validation-self-defined/delete?id=164329365983232010
```

---

### 4.7 数据方法模块 `/metadata/data-method`

#### ✅ 所有接口已完成兼容

| 接口 | 说明 |
|------|------|
| `POST /list` | VO已支持 `entityId`/`entityUuid` 兼容 |
| `POST /detail` | VO已支持 `entityId`/`entityUuid` 兼容 |

**调用示例**
```javascript
// 方案1：推荐
POST /metadata/data-method/list
{
  "entityUuid": "entity-xxxx-xxxx-xxxx",
  "methodType": "query"
}

// 方案2：兼容
{
  "entityId": "164329365983232001",
  "methodType": "query"
}
```

---

### 4.8 规则组分页 `/metadata/validation-rule-group`

#### ✅ 所有接口已支持

| 接口 | 说明 |
|------|------|
| `POST /page` | VO已支持 `entityId`/`entityUuid` 兼容 |
| `POST /delete` | ✅ 支持 String ID |

**调用示例**
```javascript
// 方案1：推荐
POST /metadata/validation-rule-group/page
{
  "entityUuid": "entity-xxxx-xxxx-xxxx",
  "pageNo": 1,
  "pageSize": 10
}

// 方案2：兼容
{
  "entityId": "51515658843258880",
  "pageNo": 1,
  "pageSize": 10
}
```

#### ✅ 已改造接口调用示例

**删除规则组**
```java
// 后端代码
@PostMapping("/delete")
public CommonResult<Boolean> unifiedDelete(@RequestParam("id") String id)
```
```javascript
// 前端调用方式
POST /metadata/validation-rule-group/delete?id=164329365983232010
```

---

## 五、总结：改造完成状态

### ✅ 所有接口已完成改造

以下接口现在均支持 `String` 类型参数，前端可传入 Long ID 字符串或 UUID：

| 模块 | 接口 | 当前参数 |
|------|------|---------|
| 数据源 | `POST /datasource/get` | `@RequestParam("id") String id` ✅ |
| 数据源 | `POST /datasource/delete` | `@RequestParam("id") String id` ✅ |
| 业务实体 | `POST /business-entity/get` | `@RequestParam("id") String id` ✅ |
| 业务实体 | `POST /business-entity/delete` | `@RequestParam("id") String id` ✅ |
| 实体字段 | `POST /entity-field/delete` | `@RequestParam("id") String id` ✅ |
| 实体关系 | `POST /entity-relationship/get` | `@RequestParam("id") String id` ✅ |
| 实体关系 | `POST /entity-relationship/delete` | `@RequestParam("id") String id` ✅ |
| 校验规则（6类） | `GET /validation/xxx/get` | `@RequestParam("id") String id` ✅ |
| 校验规则（6类） | `POST /validation/xxx/delete` | `@RequestParam("id") String id` ✅ |
| 自定义验证 | `POST /validation-self-defined/get` | `@RequestParam("id") String id` ✅ |
| 自定义验证 | `POST /validation-self-defined/delete` | `@RequestParam("id") String id` ✅ |
| 规则组 | `POST /validation-rule-group/delete` | `@RequestParam("id") String id` ✅ |

### ✅ 前端可使用的接口

所有接口均支持：

- **@RequestBody 接口**: 支持 `xxxId`（String）和 `xxxUuid` 两种参数
- **@RequestParam 接口**: 支持传入 Long ID（数字字符串）或 UUID

---

## 六、字段类型变更说明

### 6.1 VO 中 ID 字段类型（已完成）

| 字段名 | 原类型 | 新类型 | 说明 |
|--------|-------|--------|------|
| `datasourceId` | `Long` | `String` | 后端自动转换为 UUID |
| `entityId` | `Long` | `String` | 后端自动转换为 UUID |
| `fieldId` | `Long` | `String` | 后端自动转换为 UUID |
| `sourceEntityId` | `Long` | `String` | 后端自动转换为 UUID |
| `targetEntityId` | `Long` | `String` | 后端自动转换为 UUID |
| `sourceFieldId` | `Long` | `String` | 后端自动转换为 UUID |
| `targetFieldId` | `Long` | `String` | 后端自动转换为 UUID |
| `selectFieldId` | `Long` | `String` | 后端自动转换为 UUID |
| `parentEntityId` | `Long` | `String` | 后端自动转换为 UUID |
| `childEntityId` | `Long` | `String` | 后端自动转换为 UUID |

### 6.2 TypeScript 类型定义建议

```typescript
// 针对 @RequestBody 的 VO，使用兼容类型
interface EntityFieldSaveReqVO {
  entityUuid?: string;    // 推荐使用
  entityId?: string;      // 兼容方案（注意是 string 不是 number）
  fieldName: string;
}

// 针对 @RequestParam 的接口，使用 string 类型
interface GetOrDeleteRequest {
  id: string;  // 可传 Long ID 字符串 或 UUID
}
```

---

## 七、常见问题

**Q: 后端返回的数据中既有 `id` 又有 `xxxUuid`，前端应该存储哪个？**  
A: 建议都存储。`/get`、`/delete` 等接口现在支持传入 ID 或 UUID，`/create`、`/update` 等接口推荐使用 `xxxUuid`。

**Q: `@RequestBody` 接口中的 `xxxId` 应该传什么类型？**  
A: 传 **String 类型**，不要传 Number。例如：`"entityId": "164329365983232001"`

**Q: `@RequestParam` 接口中的 `id` 应该传什么类型？**  
A: 传 **String 类型**。支持两种形式：
- Long ID 字符串：`id=164329365983232001`
- UUID：`id=entity-xxxx-xxxx-xxxx`

后端会自动识别：纯数字字符串作为 Long ID 处理，非纯数字字符串作为 UUID 处理并查询数据库转换。

---

## 八、联系方式

如有疑问，请联系后端开发人员。
