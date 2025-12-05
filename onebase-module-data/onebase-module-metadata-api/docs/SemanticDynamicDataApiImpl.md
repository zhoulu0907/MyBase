# SemanticDynamicDataApiImpl 服务文档

## 概览
- 类名：`SemanticDynamicDataApiImpl`
- 位置：`onebase-module-data/onebase-module-metadata-api/src/main/java/com/cmsr/onebase/module/metadata/api/semantic/impl/SemanticDynamicDataApiImpl.java`
- 职责：面向上层的“语义动态数据”服务接口实现，统一处理实体语义模型构建、数据的查询/详情/新增/更新/删除，封装权限加载、校验、条件构建、结果转换与过程日志。

## 设计与分层
- 分层结构：API实现 → 语义服务层（`SemanticDataCrudService`）→ 数据访问层（`DynamicMetadataRepository`）
- 通用处理流程：
  - 构建 `SemanticRecordDTO`（包含实体模型与上下文）
  - 加载权限上下文与数据完整性校验
  - 构建查询/更新条件（仅条件与排序，不应用数据权限），调用服务层执行
  - 将结果转换为 `SemanticEntityValueDTO` 并记录过程日志

## 依赖组件
- `SemanticMergeRecordAssembler`：构建统一的 `SemanticRecordDTO` 请求载体
- `SemanticPermissionContextLoader`：加载运行时权限上下文
- `SemanticDataIntegrityValidator`：数据完整性与基本校验
- `SemanticPermissionValidator`：功能权限校验
- `SemanticQueryConditionBuilder`：条件与排序的统一构建器
- `SemanticDataCrudService`：CRUD 业务实现（面向 RecordDTO）
- `DynamicMetadataRepository`：底层动态表访问
- `SemanticValueAssembler`：行到语义值对象的转换
- `SemanticProcessLogger`：过程日志记录
- 元数据服务：`MetadataBusinessEntityCoreService`、`MetadataEntityFieldCoreService`

## 请求/响应模型
- `SemanticPageConditionVO`：分页查询（`tableName/pageNo/pageSize/sortBy/semanticConditionDTO`，支持嵌套条件）
- `SemanticTargetBodyVO`：按ID查询/删除（`tableName/id/methodCode/traceId`）
- `SemanicTargetConditionVO`：条件删除/条件更新（`tableName/semanticConditionDTO/updateProperties/methodCode/traceId`）
- `SemanticMergeBodyVO`：合并体创建/更新（顶层键为业务字段或连接器名）
- 返回统一使用 `SemanticEntityValueDTO` 或 `PageResult<SemanticEntityValueDTO>`，删除返回 `Integer`

### SemanticConditionDTO（嵌套条件）
- 字段：
  - `nodeType`: `GROUP` 或 `CONDITION`
  - `combinator`: 组合关系 `AND`/`OR`（仅组节点使用）
  - `children`: 子条件列表（递归嵌套）
  - `fieldUuid`/`fieldName`: 目标字段标识（条件节点）
  - `operator`: 枚举操作符（`EQ/NE/GT/GE/LT/LE/LIKE/IN/NIN`）
  - `fieldValue`: 值列表（`IN/NIN` 使用多值，其余取首值）
  - `conditionType`: `MAIN_CONDITION`/`SUB_CONDITION`（可用于区分来源）

示例（AND/OR 嵌套）：
```json
{
  "nodeType": "GROUP",
  "combinator": "AND",
  "children": [
    {
      "nodeType": "CONDITION",
      "fieldName": "status",
      "operator": "EQ",
      "fieldValue": [1]
    },
    {
      "nodeType": "GROUP",
      "combinator": "OR",
      "children": [
        { "nodeType": "CONDITION", "fieldName": "name", "operator": "LIKE", "fieldValue": ["张"] },
        { "nodeType": "CONDITION", "fieldName": "mobile", "operator": "LIKE", "fieldValue": ["139"] }
      ]
    }
  ]
}
```

## 方法一览（代码参考）
- 构建语义模型：
  - `buildEntitySchemaByUuid` `.../SemanticDynamicDataApiImpl.java:69-72`
  - `buildEntitySchemaByTableName` `.../SemanticDynamicDataApiImpl.java:74-77`
  - `buildEntityValueByName` `.../SemanticDynamicDataApiImpl.java:79-82`
  - `buildEntityValueByUuid` `.../SemanticDynamicDataApiImpl.java:85-103`
- 数据操作：
  - 分页查询：`getDataByCondition` `.../SemanticDynamicDataApiImpl.java:105-123`
  - 详情查询：`getDataById` `.../SemanticDynamicDataApiImpl.java:126-142`
  - 删除（按ID）：`deleteDataById` `.../SemanticDynamicDataApiImpl.java:145-161`
  - 删除（按条件）：`deleteDataByCondition` `.../SemanticDynamicDataApiImpl.java:163-181`
  - 更新（按条件，批量）：`updateDataByCondition` `.../SemanticDynamicDataApiImpl.java:183-216`
  - 插入（合并体）：`insertData` `.../SemanticDynamicDataApiImpl.java:219-240`
  - 更新（按ID，合并体）：`updateDataById` `.../SemanticDynamicDataApiImpl.java:242-265`
- 字段模型获取：
  - `buildEntityFieldsSchemaByTableName` `.../SemanticDynamicDataApiImpl.java:268-291`
- 内部辅助：
  - `buildPageQueryWrapper` `.../SemanticDynamicDataApiImpl.java:293-300`
  - `buildPageRecord` `.../SemanticDynamicDataApiImpl.java:302-310`
  - `buildPageQuery` `.../SemanticDynamicDataApiImpl.java:312-317`
  - `convertToValues` `.../SemanticDynamicDataApiImpl.java:319-333`
  - `buildDeleteRecord` `.../SemanticDynamicDataApiImpl.java:335-342`

## 方法详解（更新后）
- 分页查询 `getDataByCondition`
  - 流程：构建分页 RecordDTO → 加载权限 → 完整性校验 → 构建查询条件（仅过滤+排序，不应用数据权限） → 调服务层分页查询 → 转换语义值 → 过程日志
  - 参考：`onebase-module-data/onebase-module-metadata-api/src/main/java/com/cmsr/onebase/module/metadata/api/semantic/impl/SemanticDynamicDataApiImpl.java:111-129`
- 详情查询 `getDataById`
  - 流程：目标 RecordDTO → 权限 → 完整性校验 → 读取详情 → 过程日志
  - 参考：`.../SemanticDynamicDataApiImpl.java:126-142`
- 按ID删除 `deleteDataById`
  - 流程：目标 RecordDTO → 权限 → 完整性校验 → 服务层删除（软删优先） → 过程日志
  - 参考：`.../SemanticDynamicDataApiImpl.java:145-161`
- 条件删除 `deleteDataByCondition`
  - 流程：删除 RecordDTO（含过滤条件）→ 权限 → 完整性+功能权限校验 → 条件必填校验 → 构建条件（不应用数据权限）→ 服务层执行 → 过程日志
  - 参考：`.../SemanticDynamicDataApiImpl.java:170-200`
- 条件更新 `updateDataByCondition`
  - 流程：参数校验（`tableName/semanticConditionDTO/updateProperties`）→ 目标 RecordDTO（`UPDATE`）→ 权限 → 完整性校验 → 构建条件（不应用数据权限）→ 服务层条件更新 → Map 转语义值 → 过程日志
  - 特性：仅更新实体定义的合法字段；自动填充系统审计字段占位（`updated_time/updater`）；返回结果已做引用解析与字段权限过滤（服务层）
  - 参考：`.../SemanticDynamicDataApiImpl.java:200-237`
- 插入 `insertData`
  - 流程：解析表名 → 合并 RecordDTO（`CREATE`）→ 权限 → 完整性校验 → 服务层创建 → 回读详情 → 过程日志
  - 参考：`.../SemanticDynamicDataApiImpl.java:219-240`
- 按ID更新 `updateDataById`
  - 流程：解析表名 → 合并 RecordDTO（`UPDATE`）→ 权限 → 完整性+功能权限校验 → 服务层更新 → 回读详情 → 过程日志
  - 参考：`.../SemanticDynamicDataApiImpl.java:242-265`
- 字段模型列表 `buildEntityFieldsSchemaByTableName`
  - 行为：逐个将字段 UUID 转换为字段语义模型，未命中跳过
  - 参考：`.../SemanticDynamicDataApiImpl.java:268-291`

## 条件构建
- 条件与排序：`SemanticQueryConditionBuilder.apply` 将 `filters/sortBy` 转换为 `QueryWrapper`
- 嵌套解析：支持 `GROUP` 节点递归解析，依据 `combinator` 组合子条件为 `AND/OR` 表达式
- API 层不应用数据权限过滤；如需数据权限，可在更高层或服务层处理
- 参考：
  - 条件解析入口：`onebase-module-data/onebase-module-metadata-api/src/main/java/com/cmsr/onebase/module/metadata/api/semantic/impl/SemanticDynamicDataApiImpl.java:121-126`
  - 解析实现：`onebase-module-data/onebase-module-metadata-core/src/main/java/com/cmsr/onebase/module/metadata/core/semantic/strategy/SemanticQueryConditionBuilder.java:21-45,47-96`

## 结果转换与富化
- 转换：`convertToValues` 按实体字段将 Map 转为 `Row`，再转为 `SemanticEntityValueDTO`
- 富化与过滤：服务层对更新/查询结果做字典与引用解析，可能进行字段权限过滤
- 参考：
  - 转换：`.../SemanticDynamicDataApiImpl.java:319-333`
  - 服务层更新与富化：`onebase-module-data/onebase-module-metadata-core/src/main/java/com/cmsr/onebase/module/metadata/core/semantic/service/SemanticDataCrudService.java:355-404`

## 底层数据访问（审计字段）
- 条件更新：`DynamicMetadataRepository#updateByQuery` 自动处理 `updated_time/updatetime` 与 `updater`
- 参考：`onebase-module-data/onebase-module-metadata-core/src/main/java/com/cmsr/onebase/module/metadata/core/semantic/dal/DynamicMetadataRepository.java:96-110`

## 错误与边界
- 条件删除：未提供有效条件时抛出异常以避免删除全表
- 条件更新：无条件或无更新内容返回空列表
- 缺失表名：返回 `null` 或空结构，调用方应保证表名有效
- 字段 UUID 未命中：构建字段模型时直接跳过不抛异常

## 安全与合规
- 输入校验：通过 VO 与校验器组合保证结构与必填项
- 权限控制：统一加载权限上下文；API 层不应用数据权限过滤，结果层可能做字段权限过滤
- 输出安全：敏感信息不暴露；结果字段集可能受权限控制过滤
- 操作安全：删除与更新的条件必校验，避免误操作

## 使用示例
- 条件更新（批量）：
```json
{
  "tableName": "customer",
  "semanticConditionDTO": { "fieldName": "status", "operator": "EQ", "fieldValue": [1] },
  "updateProperties": { "mobile": "13900000000", "status": 2 }
}
```
- 条件删除：
```json
{
  "tableName": "customer",
  "semanticConditionDTO": { "fieldName": "deleted", "operator": "EQ", "fieldValue": [0] }
}
```
- 分页查询：
```json
{
  "tableName": "customer",
  "pageNo": 1,
  "pageSize": 20,
  "sortBy": [{ "field": "id", "direction": "DESC" }],
  "semanticConditionDTO": { "fieldName": "status", "operator": "EQ", "fieldValue": [1] }
}
```

## 接口入参/出参示例（逐方法）

### buildEntitySchemaByUuid
- 入参：`entityUuid: "entity-uuid-001"`
- 出参（示例）：
```json
{
  "id": 10,
  "code": "customer",
  "displayName": "客户",
  "tableName": "customer",
  "fields": [
    {"id":101,"fieldUuid":"f-uuid-001","fieldCode":"customer_name","fieldName":"name","displayName":"客户名称","fieldType":"VARCHAR","dataLength":64,"decimalPlaces":0,"isRequired":true,"isUnique":false,"isSystemField":false,"isPrimaryKey":false,"dictTypeId":null},
    {"id":102,"fieldUuid":"f-uuid-002","fieldCode":"customer_status","fieldName":"status","displayName":"状态","fieldType":"INT","dataLength":11,"decimalPlaces":0,"isRequired":true,"isUnique":false,"isSystemField":false,"isPrimaryKey":false,"dictTypeId":2001}
  ],
  "connectors": []
}
```

### buildEntitySchemaByTableName
- 入参：`entityTableName: "customer"`
- 出参：同上（根据表名返回实体模型）

### buildEntityValueByName
- 入参：
```json
{
  "tableName": "customer",
  "fieldNameValues": {
    "name": "张三",
    "status": 1
  }
}
```
- 出参（示例）：
```json
{
  "id": null,
  "tableName": "customer",
  "fields": {
    "name": {"storeValue":"张三","displayValue":"张三"},
    "status": {"storeValue":1,"displayValue":"启用"}
  }
}
```

### buildEntityValueByUuid
- 入参：
```json
{
  "entityUuid": "entity-uuid-001",
  "fieldUuidValues": {
    "f-uuid-001": "张三",
    "f-uuid-002": 1
  }
}
```
- 出参：参考 `buildEntityValueByName`（自动将 UUID 转为字段名）

### getDataByCondition（分页查询）
- 入参：`SemanticPageConditionVO`
```json
{
  "tableName": "customer",
  "pageNo": 1,
  "pageSize": 20,
  "sortBy": [{"field":"id","direction":"DESC"}],
  "semanticConditionDTO": {"fieldName":"status","operator":"EQ","fieldValue":[1]}
}
```
- 出参（示例）：
```json
{
  "list": [
    {
      "id": 123456,
      "name": "张三",
      "status": 1
    },
    {
      "id": 123457,
      "name": "李四",
      "status": 1
    }
  ],
  "total": 2
}
```

### getDataById（详情）
- 入参：`SemanticTargetBodyVO`
```json
{
  "tableName": "customer",
  "id": 123456
}
```
- 出参（示例）：
```json
{
  "id": 123456,
  "name": "张三",
  "mobile": "13800000000",
  "status": 1,
  "connectors": {
    "customer_profile": {"rowValue": {"id": 5001, "fields": {"profile_city":"广州","profile_major":"计算机"}}},
    "customer_tags": {"rowValueList": [{"id": 7001, "fields": {"tag_name":"vip","tag_level":1}}]}
  }
}
```

### deleteDataById（按ID删除）
- 入参：`SemanticTargetBodyVO`
```json
{
  "tableName": "customer",
  "id": 123456
}
```
- 出参：
```json
1
```

### deleteDataByCondition（条件删除）
- 入参：`SemanicTargetConditionVO`
```json
{
  "tableName": "customer",
  "semanticConditionDTO": {"fieldName":"deleted","operator":"EQ","fieldValue":[0]}
}
```
- 出参：
```json
3
```

### updateDataByCondition（条件更新，批量）
- 入参：`SemanicTargetConditionVO`
```json
{
  "tableName": "customer",
  "semanticConditionDTO": {"fieldName":"status","operator":"EQ","fieldValue":[1]},
  "updateProperties": {"mobile":"13900000000","status":2}
}
```
- 出参（示例）：
```json
[
  {"id": 123456, "name":"张三", "mobile":"13900000000", "status":2},
  {"id": 123457, "name":"李四", "mobile":"13900000000", "status":2}
]
```

### insertData（合并体插入）
- 入参：`SemanticMergeBodyVO`
```json
{
  "tableName": "customer",
  "name": "王五",
  "mobile": "13700000000",
  "status": 1,
  "customer_tags": [
    {"tag_name": "new", "tag_level": 1}
  ]
}
```
- 出参（示例）：
```json
{
  "id": 123460,
  "name": "王五",
  "mobile": "13700000000",
  "status": 1,
  "customer_tags": [
    {"id": 8001, "tag_name": "new", "tag_level": 1}
  ]
}
```

### updateDataById（按ID合并体更新）
- 入参：`SemanticMergeBodyVO`
```json
{
  "tableName": "customer",
  "id": 123456,
  "mobile": "13911112222",
  "customer_tags": [
    {"id": 8001, "tag_name": "vip", "tag_level": 2}
  ]
}
```
- 出参（示例）：
```json
{
  "id": 123456,
  "name": "张三",
  "mobile": "13911112222",
  "status": 1,
  "customer_tags": [
    {"id": 8001, "tag_name": "vip", "tag_level": 2}
  ]
}
```

## 约束与说明
- 条件删除：必须传递有效条件，避免误删全表；返回影响行数
- 条件更新：仅更新实体定义的合法字段；自动填充 `updated_time/updater` 占位
- 权限：查询与结果层面均应用数据权限过滤；返回字段集可能受权限影响
- 字典与引用：服务层已做字典翻译与引用富化（展示值可能与存储值不同）

## 单元测试建议
- 成功场景：分页查询、详情、条件更新、条件删除、合并体创建/更新
- 异常场景：缺失 `tableName`、条件删除无条件、条件更新无条件或无更新内容
- 覆盖率目标：≥ 80%
