## 目标

* 让详情查询流程与创建流程风格一致：统一记录装配、权限上下文、校验、查询与结果格式化。

* 保持职责分离，避免在详情执行器中重复底层流程编排；使用已有的统一编排器以减少重复。

## 总体思路

* 参考创建执行器的入口与编排风格（`SemanticCreateExecutor.java:67-81`、`86`），但将实际的校验/上下文/查询等流程委托给 `SemanticDataMethodExecutor.executeDetail(...)`（其内部 `doExecuteProcess(...)` 已统一编排）。

* 在详情执行器中：

  * 解析实体（按 `tableName` 或现有 `entityId`）

  * 构建 `SemanticRecordDTO`（设置 `GET` 操作、菜单、traceId、是否包含子表/关系、目标主键 `id`）

  * 委托 `SemanticDataMethodExecutor` 完成后续流程

## 具体改动

* 类：`/onebase-module-data/onebase-module-metadata-runtime/src/main/java/com/cmsr/onebase/module/metadata/runtime/semantic/executor/SemanticDetailExecutor.java`

* 依赖注入：

  * 保留并使用：`SemanticDataMethodExecutor`、`MetadataBusinessEntityCoreService`

  * 无需在本类重复注入：`SemanticPermissionContextLoader`、`SemanticValidationManager`、`SemanticDataFetcher`、`SemanticResultFormatter` 等（由 `SemanticDataMethodExecutor` 统一驱动）

* 方法签名与实现：

  1. 保留已有方法：

     * `public Map<String, Object> execute(Long entityId, Long menuId, String traceId, SemanticRecordDTO record)`（当前已直接委托，继续沿用）
  2. 新增重载方法（对齐创建执行器的 `tableName` 风格）：

     * `public Map<String, Object> execute(String tableName, Long menuId, String traceId, SemanticTargetBodyVO body)`

     * 步骤：

       * 通过 `MetadataBusinessEntityCoreService.getBusinessEntityByTableName(tableName)` 获取实体；若为空抛出 `BUSINESS_ENTITY_NOT_EXISTS`（参考当前类注释中抛错风格 `SemanticDetailExecutor.java:27-33`）。

       * 构建 `SemanticRecordDTO`：

         * `recordContext`: 设置 `menuId`、`traceId`、`operationType = GET`、`containSubTable`、`containRelation`（来自 `body`）。

         * `entitySchema`: 设置 `id`、`tableName`（必要字段）；如可用，再补充 `code`、`datasourceId`。

         * `entityValue`: 在 `fieldValueMap` 中放入主键 `"id"`，其值来自 `body.data.get("id")`。

       * 调用 `semanticDataMethodExecutor.executeDetail(entity.getId(), menuId, traceId, record)`。

## 关键类与字段

* `SemanticRecordDTO`：`recordContext`、`entitySchema`、`entityValue`（均为 Lombok setter，便于手动装配）

* `SemanticTargetBodyVO`：`data.id`、`containSubTable`、`containRelation`、`methodCode`（目标行定位与展开选项）

* `MetadataBusinessEntityCoreService`：`getBusinessEntityByTableName(String)`、`getBusinessEntityByCode(String)`（实体解析）

* 统一编排器：`SemanticDataMethodExecutor.executeDetail(...)`（避免重复实现流程）

## 方法流程（与创建流程对应）

* 详情的高层流程将由 `SemanticDataMethodExecutor` 执行，内部步骤与创建一致（实体校验→字段装载→上下文初始化→权限校验→数据校验→查询→结果格式化→日志），参考创建流程示意：`SemanticCreateExecutor.java:67-81,86`。

## 日志与异常处理

* 在实体未找到或委托执行异常时记录错误日志并向上抛出（对齐创建执行器的错误记录风格：`SemanticCreateExecutor.java:98-100`）。

## 单元测试

* 为新增 `execute(tableName, menuId, traceId, body)` 添加测试：

  * Mock `MetadataBusinessEntityCoreService` 返回实体

  * 构造 `SemanticTargetBodyVO`（含 `data.id`、展开选项）

  * 断言 `SemanticDataMethodExecutor.executeDetail(...)` 被按期望参数调用并返回结果

  * 覆盖：实体不存在抛 `BUSINESS_ENTITY_NOT_EXISTS`

## 代码引用

* 创建执行器编排参考：`onebase-module-data/.../SemanticCreateExecutor.java:67-81,86`

* 详情执行器现状入口：`onebase-module-data/.../SemanticDetailExecutor.java:23-25`

* 详情执行器注释参考抛错风格：`onebase-module-data/.../SemanticDetailExecutor.java:27-33`

## 交付结果

* 更新 `SemanticDetailExecutor`，新增基于 `tableName + SemanticTargetBodyVO` 的入口方法，内部统一组装 `SemanticRecordDTO` 并委托统一编排器执行，完成对齐创建流程的风格化、可扩展的详情查询实现。

