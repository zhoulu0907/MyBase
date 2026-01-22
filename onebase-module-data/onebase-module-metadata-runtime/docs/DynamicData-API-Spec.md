# 动态数据接口设计方案

## 目标与原则
- 保持接口简洁、统一，贴近主流 REST/JSON 设计实践
- 明确职责分离：业务数据仅在 `data` 中，控制参数由路径/查询参数/请求头承载
- 与现有服务层兼容：控制器完成新结构到旧 VO 的转换，服务层无需改动
- 安全与校验遵循 OWASP 最佳实践：严格输入验证、类型与结构校验、统一错误码

## 路由与承载
- 路径资源：`/runtime/metadata/{tableName}` 表示主实体表名，由后端解析实体模型
- 查询参数：`menuId` 必填，用于权限检测
- 请求头：`X-Trace-Id` 可选，用于链路追踪
- 响应头：`X-Trace-Id` 返回，用于链路追踪
- 请求体：仅保留 `data`，承载主字段、关系与子表数据

参考控制器位置：
- 创建：`onebase-module-data/onebase-module-metadata-runtime/src/main/java/com/cmsr/onebase/module/metadata/runtime/semantic/controller/SemanticDynamicDataController.java:42`
- 更新：`onebase-module-data/onebase-module-metadata-runtime/src/main/java/com/cmsr/onebase/module/metadata/runtime/semantic/controller/SemanticDynamicDataController.java:70`
- 删除：`onebase-module-data/onebase-module-metadata-runtime/src/main/java/com/cmsr/onebase/module/metadata/runtime/semantic/controller/SemanticDynamicDataController.java:96`
- 详情：`onebase-module-data/onebase-module-metadata-runtime/src/main/java/com/cmsr/onebase/module/metadata/runtime/semantic/controller/SemanticDynamicDataController.java:122`
- 分页：`onebase-module-data/onebase-module-metadata-runtime/src/main/java/com/cmsr/onebase/module/metadata/runtime/semantic/controller/SemanticDynamicDataController.java:148`

## 请求体结构
- 顶层仅保留：
  - `data`: `Map<String, Object>`
    - 业务字段键（如 `name`、`mobile`、`status`）→ 主实体字段
    - 语义化键（如 `customer_profile`、`customer_tags`、`orders`）→ 关系或子表
- 行通用语义字段：
  - `id`：更新/识别目标
  - `deleted`：软删除标记（必要时由控制器映射为删除动作）

## 关系与子表识别规则
- 后端基于实体元数据自动识别，无需前端传 `descriptor`
- 关系类型：
  - 一对一关系：值为对象 `Map<Long,Object>`
  - 一对多关系：值为数组 `List<Map<Long,Object>>`
- 子表：值为数组 `List<Map<Long,Object>>`
- 未知键拒绝：
  - 业务字段键必须存在于主实体字段定义中
  - 语义化键必须匹配已定义的关系或子表名

## 操作语义
- 新增：不传 `id`，不传 `deleted`
- 更新：`data.id` 必传
- 删除：`data.deleted=true` 且携带 `data.id`
- 删除执行顺序优先于更新/新增，避免唯一约束冲突

## 接口文档

### 创建
- 方法：`POST /runtime/metadata/{tableName}/create?menuId={menuId}`
- 头：`X-Trace-Id` 可选, `Content-Type: application/json`
- 体：
```json
{
  "name": "张三",
  "mobile": "13800000000",
  "status": 1,
  "customer_profile": { 
    "profile_city": "广州", 
    "profile_major": "计算机" 
  },
  "customer_tags": [
    { "tag_name": "vip", "tag_level": 1 },
    { "tag_name": "new", "tag_level": 1 }
  ],
  "orders": [
    { "order_no": "SO2024-0001", "amount": 100.00 },
    { "order_no": "SO2024-0002", "amount": 88.00 }
  ]
}
```
- 响应：`CommonResult<DynamicDataRespVO>`（返回主实体 `id` 与回显）

### 更新
- 方法：`POST /runtime/metadata/{tableName}/update?menuId={menuId}`
- 头：`X-Trace-Id` 可选
- 体：
```json
{
    "id": 123456,
    "mobile": "13900000000",
    "xxx01_customer_tags": [
      { "id": 789, "tag_name": "vip", "tag_level": 1 },
      { "tag_name": "svip", "tag_level": 1 }
    ],
    "xxx01_orders": [
      { "id": 555, "deleted": true }
    ]
}
```
- 响应：`CommonResult<DynamicDataRespVO>`

- 方法：`POST /runtime/metadata/{tableName}/delete?menuId={menuId}`
- 头：`X-Trace-Id` 可选
- 体：
```json
{
  "id": 123456,
  "methodCode": "SOFT_DELETE"
}
```
- 响应：`CommonResult<Boolean>`

### 详情
- 方法：`POST /runtime/metadata/{tableName}/detail?menuId={menuId}`
- 头：`X-Trace-Id` 可选
- 体：
```json
{
  "id": 123456,
  "containSubTable": true,
  "containRelation": true
}
```
- 响应：`CommonResult<DynamicDataRespVO>`

### 分页
- 方法：`POST /runtime/metadata/{tableName}/page?menuId={menuId}`
- 头：`X-Trace-Id` 可选
- 体（兼容现有分页VO字段传体内）：
```json
{
  "data": {},
  "pageNo": 1,
  "pageSize": 20,
  "sortBy": [ { "field": "name", "direction": "DESC" } ],
  "filters": {
    "nodeType": "GROUP",
    "combinator": "AND",
    "children": [
      { "nodeType": "CONDITION", "fieldName": "status", "operator": "EQUALS", "fieldValue": [1] },
      { "nodeType": "CONDITION", "fieldName": "name", "operator": "CONTAINS", "fieldValue": ["张"] }
    ]
  }
}
```
- 响应：`CommonResult<PageResult<DynamicDataRespVO>>`

#### 过滤配置
- 支持两种结构：
  - 简单键值：`{"status": 1, "name": "张"}`（等价于 `EQUALS` 与默认 `CONTAINS` 的退化处理，字符串默认模糊匹配）
  - 条件对象：`{"cond1": {"fieldName":"status","operator":"EQUALS","value":1}}`
- 操作符可选值：
  - 比较：`EQUALS`、`NOT_EQUALS`、`GREATER_THAN`、`GREATER_EQUALS`、`LESS_THAN`、`LESS_EQUALS`
  - 文本：`CONTAINS`、`NOT_CONTAINS`
  - 时间：`EARLIER_THAN`、`LATER_THAN`、`RANGE`（`{"start":...,"end":...}` 任意一端可省略）
  - 集合：`EXISTS_IN`、`NOT_EXISTS_IN`
  - 空值：`IS_EMPTY`、`IS_NOT_EMPTY`
- 字段与默认：
  - `fieldName` 必须为主实体已定义字段；未知字段忽略
  - 未指定 `operator` 时默认 `CONTAINS`
  - `deleted`、`tenant_id` 自动忽略，系统会追加 `deleted=0`
- 示例：
```json
{
  "filters": {
    "f1": { "fieldName": "status", "operator": "CONTAINS", "value": 1 },
    "f2": { "fieldName": "name", "operator": "CONTAINS", "value": "张" },
    "f3": { "fieldName": "amount", "operator": "RANGE", "value": { "start": 50, "end": 200 } },
    "f4": { "fieldName": "created_at", "operator": "EARLIER_THAN", "value": "2025-12-01 00:00:00" },
    "f5": { "fieldName": "category", "operator": "EXISTS_IN", "value": ["A","B"] }
  }
}
```

参考实现：
- 条件解析：`onebase-module-data/onebase-module-metadata-runtime/src/main/java/com/cmsr/onebase/module/metadata/runtime/controller/app/datamethod/datamethodImpl/MetadataDataMethodQueryImpl.java:236`
- 计数与列表过滤：`onebase-module-data/onebase-module-metadata-runtime/src/main/java/com/cmsr/onebase/module/metadata/runtime/controller/app/datamethod/datamethodImpl/MetadataDataMethodQueryImpl.java:285`
- 操作符映射：`onebase-module-data/onebase-module-metadata-runtime/src/main/java/com/cmsr/onebase/module/metadata/runtime/controller/app/datamethod/datamethodImpl/MetadataDataMethodQueryImpl.java:349`

### 条件配置设计
- 条件模型：`SemanticConditionDTO`（树形结构）
  - 组节点：`{"nodeType":"GROUP","combinator":"AND|OR","children":[...]}`
  - 条件节点：`{"nodeType":"CONDITION","fieldName":"xxx"|"fieldUuid":"...","operator":"...","fieldValue":[...],"conditionType":"MAIN_CONDITION|SUB_CONDITION"}`
  - 字段解析优先级：先用 `fieldName`，缺失时按 `fieldUuid` 解析为字段名。实现位置 `onebase-module-data/onebase-module-metadata-core/src/main/java/com/cmsr/onebase/module/metadata/core/semantic/strategy/SemanticQueryConditionBuilder.java:175`
- 操作符语义（核心映射）：
  - 空操作符：字符串走 `LIKE`，其他类型走 `=`。实现位置 `onebase-module-data/onebase-module-metadata-core/src/main/java/com/cmsr/onebase/module/metadata/core/semantic/strategy/SemanticQueryConditionBuilder.java:79`
  - 比较：`EQUALS(EQ)`、`NOT_EQUALS(NE)`、`GREATER_THAN(GT)`、`GREATER_EQUALS(GE)`、`LESS_THAN(LT)`、`LESS_EQUALS(LE)`（`QueryColumn.eq/ne/gt/ge/lt/le`）
  - 文本：`CONTAINS`→`LIKE`，`NOT_CONTAINS`→`NOT LIKE`
  - 集合：`EXISTS_IN`→`IN`，`NOT_EXISTS_IN`→`NOT IN`
  - 组合包含：`CONTAINS_ALL`（所有值 AND 组合 LIKE）、`CONTAINS_ANY`（任一值 OR 组合 LIKE）、`NOT_CONTAINS_ALL`（任一值 OR 组合 NOT LIKE）、`NOT_CONTAINS_ANY`（所有值 AND 组合 NOT LIKE）。实现位置 `.../SemanticQueryConditionBuilder.java:123`
  - 时间：`LATER_THAN`→`>`，`EARLIER_THAN`→`<`，`RANGE`→`>= start AND <= end`（`fieldValue` 顺序为 `[start,end]`，可缺省任一端）。实现位置 `.../SemanticQueryConditionBuilder.java:109`, `...:113`, `...:114`
  - 空值：`IS_EMPTY`（`IS NULL OR = ''`）、`IS_NOT_EMPTY`（`IS NOT NULL AND != ''`）。实现位置 `.../SemanticQueryConditionBuilder.java:159`
- 值类型与转换：
  - 后端按字段类型强制转换（数值、日期/时间、布尔等）。字符串缺省按 `LIKE`；集合运算按列表元素转换。
  - 空字符串规范：业务值归一为 `null`；示例实现 `onebase-module-data/onebase-module-metadata-common/.../SemanticFieldValueDTO.java:296`。
- 嵌套示例：
```json
{
  "filters": {
    "nodeType": "GROUP",
    "combinator": "AND",
    "children": [
      { "nodeType": "GROUP", "combinator": "OR", "children": [
        { "nodeType": "CONDITION", "fieldName": "name", "operator": "CONTAINS_ANY", "fieldValue": ["张","李"] },
        { "nodeType": "CONDITION", "fieldName": "mobile", "operator": "CONTAINS", "fieldValue": ["138"] }
      ]},
      { "nodeType": "CONDITION", "fieldName": "status", "operator": "EXISTS_IN", "fieldValue": [1,2] },
      { "nodeType": "CONDITION", "fieldName": "created_at", "operator": "RANGE", "fieldValue": ["2025-01-01 00:00:00","2025-12-31 23:59:59"] },
      { "nodeType": "CONDITION", "fieldName": "amount", "operator": "GREATER_EQUALS", "fieldValue": [100] },
      { "nodeType": "CONDITION", "fieldName": "remark", "operator": "IS_NOT_EMPTY", "fieldValue": [] }
    ]
  },
  "sortBy": [
    { "field": "created_at", "direction": "DESC" },
    { "field": "id", "direction": "DESC" }
  ]
}
```
- 约束与默认：
  - 未识别字段直接忽略；系统字段如 `deleted`、`tenant_id` 不可作为条件，系统会默认追加 `deleted=0`（分页实现位置 `onebase-module-data/onebase-module-metadata-runtime/.../MetadataDataMethodQueryImpl.java:266`）
  - `fieldValue` 为数组：`IN/NOT_IN` 使用全量列表；`RANGE` `[start,end]`；其他取首值。
  - 排序缺省按主键降序（实现位置 `.../SemanticQueryConditionBuilder.java:42`）

## 控制器传输逻辑
- 路由解析：`{tableName}` 由服务层解析实体模型
- 上下文：查询参数 `menuId` 必填；请求头 `X-Trace-Id` 若缺失则由控制器生成并在响应头返回
- 委派执行：控制器薄层，业务语义装配、权限与校验在服务层完成

## 校验与错误码
- 必填：`menuId`；删除/详情需 `id`
- 类型：一对一为对象，一对多/子表为数组；业务字段键必须为合法字段
- 未知键：拒绝并返回业务错误码
- 典型错误：
  - `BUSINESS_ENTITY_NOT_EXISTS`（实体不存在）
  - 参数校验失败（空 `data`、未知键、类型不匹配、非法 `menuId`）

## 安全与日志
- 输入验证：数字键整型校验、字符串键白名单匹配、值类型与长度限制
- 敏感信息脱敏；禁止输出敏感字段到日志
- 链路追踪：`X-Trace-Id` 贯穿请求处理与响应头（不在响应体中返回）

## 响应结构（示例）
```json
{
  "code": 0,
  "msg": "OK",
  "data": {
    "id": 123456,
    "name": "张三",
    "mobile": "13800000000",
    "status": 1,
    "customer_profile": { "profile_city": "广州", "profile_major": "计算机" },
    "customer_tags": [
      { "id": 789, "tag_name": "vip", "tag_level": 1 },
      { "tag_name": "new", "tag_level": 1 }
    ],
    "orders": [
      { "id": 555, "order_no": "SO2024-0001", "amount": 100.00, "status": "paid" }
    ]
  }
}
```
说明：响应头包含 `X-Trace-Id`。

## 完整返回结构示例

- 创建/更新成功响应（与提交结构一致）
```json
{
  "code": 0,
  "msg": "OK",
  "data": {
    "id": 123456,
    "name": "张三",
    "mobile": "13800000000",
    "status": 1,
    "customer_profile": { "profile_city": "广州", "profile_major": "计算机" },
    "customer_tags": [
      { "id": 789, "tag_name": "vip", "tag_level": 1 },
      { "tag_name": "new", "tag_level": 1 }
    ],
    "orders": [
      { "id": 555, "order_no": "SO2024-0001", "amount": 100.00, "status": "paid" },
      { "order_no": "SO2024-0002", "amount": 88.00, "status": "created" }
    ]
  }
}
```

- 删除成功响应（与提交结构一致）
```json
{
  "code": 0,
  "msg": "OK",
  "data": { "id": 123456, "deleted": true }
}
```

- 详情响应（与提交结构一致）
```json
{
  "code": 0,
  "msg": "OK",
  "data": {
    "id": 123456,
    "name": "张三",
    "mobile": "13800000000",
    "status": 1,
    "customer_profile": { "profile_city": "广州", "profile_major": "计算机" },
    "customer_tags": [
      { "id": 789, "tag_name": "vip", "tag_level": 1 }
    ],
    "orders": [
      { "id": 555, "order_no": "SO2024-0001", "amount": 100.00, "status": "paid" }
    ]
  }
}
```

- 分页响应（与提交结构一致）
```json
{
  "code": 0,
  "msg": "OK",
  "data": {
    "list": [
      {
        "id": 123456,
        "name": "张三",
        "mobile": "13800000000",
        "status": 1
      },
      {
        "id": 123457,
        "name": "李四",
        "mobile": "13900000000",
        "status": 0
      }
    ],
    "total": 2,
    "pageNo": 1,
    "pageSize": 20
  }
}
```

## 单元测试规划
- 成功场景：
  - 自动识别关系与子表（对象/数组）拆分正确
  - `id`、`deleted` 行语义顺序与执行正确（删除优先）
  - `menuId` 参数与 `X-Trace-Id` 头的注入旧 VO 正确
- 异常场景：
  - 缺失 `menuId`、空 `data`
  - 未知键、类型不匹配、非法数字键
- 覆盖率目标：≥ 80%

## 兼容与迁移
- 控制器内完成新体结构到旧 VO 的转换，服务层保持不变
- 允许短期回退：若旧客户端仍在体内携带 `menuId`，可作为兼容读取，但建议统一到查询参数

## DTO 设计总览
- 模型与值分层：模型用 `EntitySchemaDTO`；值用 `ValueModelDTO`
- 统一承载：`RecordDTO`= `context` + `entity` + `value`
- 连接器统一抽象：关系与子表用 `ConnectorSchemaDTO`（模型）与 `ConnectorValueDTO`（值）；基数用枚举控制

### 关键枚举
```java
public enum ConnectorTypeEnum { RELATION, SUBTABLE }
public enum ConnectorCardinalityEnum { ONE, MANY }
public enum SortDirectionEnum { ASC, DESC }
```

### RecordDTO（请求/响应统一）
```java
@Data
@Schema(description = "记录承载 DTO：包含上下文、实体模型、值模型")
public class RecordDTO {
  @Schema(description = "上下文：权限、追踪、分页与查询控制")
  private RecordContextDTO context;

  @Schema(description = "实体模型：可仅传 id/code，响应可回填完整模型")
  private EntitySchemaDTO entity;

  @Schema(description = "值模型：主实体字段与连接器值")
  private ValueModelDTO value;
}

@Data
@Schema(description = "记录上下文")
public class RecordContextDTO {
  @Schema(description = "菜单ID", requiredMode = Schema.RequiredMode.REQUIRED)
  private Long menuId;

  @Schema(description = "链路追踪ID")
  private String traceId;

  @Schema(description = "是否包含子表")
  private Boolean containSubTable;

  @Schema(description = "是否包含关系")
  private Boolean containRelation;

  @Schema(description = "页码")
  private Integer pageNo;

  @Schema(description = "分页大小")
  private Integer pageSize;

  @Schema(description = "多字段排序规则")
  private List<SortRuleDTO> sortBy;

  @Schema(description = "过滤条件")
  private Map<String,Object> filters;
}
```

```java
@Data
@Schema(description = "排序规则 DTO")
public class SortRuleDTO {
  @Schema(description = "排序字段")
  private String field;

  @Schema(description = "排序方向")
  private SortDirectionEnum direction;
}
```

### EntitySchemaDTO（实体模型）
```java
@Data
@Schema(description = "实体模型 DTO")
public class EntitySchemaDTO {
  @Schema(description = "实体ID")
  private Long id;

  @Schema(description = "实体编码")
  private String code;

  @Schema(description = "实体显示名称")
  private String displayName;

  @Schema(description = "主表名")
  private String tableName;

  @Schema(description = "字段模型列表")
  private List<FieldSchemaDTO> fields;

  @Schema(description = "连接器模型列表：关系与子表")
  private List<ConnectorSchemaDTO> connectors;
}

@Data
@Schema(description = "字段模型 DTO")
public class FieldSchemaDTO {
  private Long id;
  private String fieldCode;
  private String fieldName;
  private String displayName;
  private String fieldType;
  private Integer dataLength;
  private Integer decimalPlaces;
  private Boolean isRequired;
  private Boolean isUnique;
  private Boolean isSystemField;
  private Boolean isPrimaryKey;
}

@Data
@Schema(description = "连接器模型 DTO（统一表示关系与子表）")
public class ConnectorSchemaDTO {
  @Schema(description = "连接器名称（语义化键，如 customer_tags、orders）")
  private String name;

  @Schema(description = "连接器类型：关系/子表")
  private ConnectorTypeEnum type;

  @Schema(description = "基数：ONE/MANY")
  private ConnectorCardinalityEnum cardinality;

  @Schema(description = "源实体ID（主实体）")
  private Long sourceEntityId;

  @Schema(description = "目标实体ID（关联/子表实体）")
  private Long targetEntityId;

  @Schema(description = "连接键：源字段ID")
  private Long sourceKeyFieldId;

  @Schema(description = "连接键：目标字段ID")
  private Long targetKeyFieldId;

  @Schema(description = "关系自身属性字段模型（若有）")
  private List<FieldSchemaDTO> relationAttributes;
}
```

### ValueModelDTO（值模型）
```java
@Data
@Schema(description = "值模型 DTO：主实体字段与连接器值")
public class ValueModelDTO {
  @Schema(description = "主实体字段值，业务键对应 ValueDTO")
  private Map<String,ValueDTO> data;

  @Schema(description = "连接器值，key 为连接器名称")
  private Map<String,ConnectorValueDTO> connectors;
}

@Data
@Schema(description = "字段值 DTO")
public class ValueDTO {
  @Schema(description = "字段原始值")
  private Object value;

  @Schema(description = "值类型代码，例如 STRING/NUMBER/DATE/BOOLEAN")
  private String type;
}

@Data
@Schema(description = "连接器值 DTO")
public class ConnectorValueDTO {
  @Schema(description = "ONE 基数时的单行值")
  private RowValueDTO row;

  @Schema(description = "MANY 基数时的多行值")
  private List<RowValueDTO> rows;
}

@Data
@Schema(description = "行值 DTO：统一行语义")
public class RowValueDTO {
  private Object id;
  private Boolean deleted;
  private Map<String,Object> fields;
}
```

## 控制器适配要点
- 路由解析：`{entityCode}` → `entityId`（参考 src/main/java/com/cmsr/onebase/module/metadata/runtime/controller/datamethod/DynamicDataController.java:99-105）
- 上下文注入：`menuId` 来自查询参数，`traceId` 来自请求头，传入旧 VO（参考 147-155 行）
- 值拆分：`value.data`→主实体字段；`value.connectors`→按 `ConnectorSchemaDTO.cardinality` 转旧版子表VO（对象→单行，数组→多行，参考 152-155 行）
- 删除优先：若 `value.data.deleted=true` 则先执行删除（参考 47-48 行）

## 校验与错误码（与规范一致）
- 必填：`context.menuId`、`value`、`value.data`
- 类型：关系 `ONE` 用对象、`MANY` 用数组；业务字段键必须合法
- 未知键拒绝：业务字段键需在主实体字段定义中；连接器名称需匹配已定义连接器
- 错误码：`BUSINESS_ENTITY_NOT_EXISTS`、参数校验失败（空 `value.data`、未知键、类型不匹配、非法 `menuId`）

## 迁移策略
- Controller 内实现 `RecordDTO` → 旧版 VO 的转换，Core/Service 保持不变
- 允许兼容期：读取旧客户端体内 `menuId`，但建议统一到查询参数
## 语义动态数据 API（服务接口）

### 条件更新
- 方法：`SemanticDynamicDataApi#updateDataByCondition`
- 体：
```json
{
  "tableName": "customer",
  "semanticConditionDTO": {
    "fieldName": "status",
    "operator": "EQ",
    "fieldValue": [1]
  },
  "updateProperties": {
    "mobile": "13900000000",
    "status": 2
  }
}
```
- 行为：
- 仅更新实体模型中存在的合法字段；忽略未知键
- 自动填充系统字段占位：`updated_time`/`updater`（由数据层统一处理）
- 返回：更新后的语义值列表（已执行引用解析与字段权限过滤）

### 条件删除
- 方法：`SemanticDynamicDataApi#deleteDataByCondition`
- 体：
```json
{
  "tableName": "customer",
  "semanticConditionDTO": {
    "fieldName": "deleted",
    "operator": "EQ",
    "fieldValue": [0]
  }
}
```
- 行为：
- 若存在 `deleted` 字段则执行软删除，否则物理删除
- 为避免误删全表，条件必填且需包含字段与值
- 返回：影响行数（整数）
