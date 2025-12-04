package com.cmsr.onebase.module.metadata.api.semantic;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticEntitySchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticEntityValueDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldSchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanicMergeConditionVO;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanticPageConditionVO;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanticTargetBodyVO;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanicTargetConditionVO;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * 数据方法API接口
 * 
 * @author shiyutian
 * @date 2025-12-01
 */
@Tag(name = "动态数据方法 API")
public interface SemanticDynamicDataApi {


    /**
     * 根据实体 UUID 构建实体语义模型
     *
     * - 返回字段、连接器（子表/关系）等完整语义模型
     *
     * @param entityUuid 实体 UUID
     * @return 实体语义模型（包含字段与连接器定义）
     */
    @Operation(summary = "根据实体 UUID 构建实体语义模型")
    public SemanticEntitySchemaDTO buildEntitySchemaByUuid(String entityUuid);

    /**
     * 根据实体表名构建实体语义模型
     *
     * - 返回字段、连接器（子表/关系）等完整语义模型
     *
     * @param entityTableName 主表名
     * @return 实体语义模型（包含字段与连接器定义）
     */
    @Operation(summary = "根据实体表名构建实体语义模型")
    public SemanticEntitySchemaDTO buildEntitySchemaByTableName(String entityTableName);
    
    /**
     * 根据字段 UUID 列表构建字段语义模型列表
     *
     * - 仅根据字段 UUID 查询对应字段的语义信息
     *
     * @param fieldUuids 字段 UUID 列表
     * @return 字段语义模型列表
     */
    @Operation(summary = "根据字段 UUID 列表构建字段语义模型列表")
    public List<SemanticFieldSchemaDTO> buildEntityFieldsSchemaByTableName(List<String> fieldUuids);

    /**
     * 根据表名与字段名-值映射构建实体值模型
     *
     * - `fieldNameValues` 以字段名为键，值为原始值
     * - 返回包含存储值与展示值的语义化实体值
     *
     * @param tableName 主表名
     * @param fieldNameValues 字段名到值的映射
     * @return 语义化的实体值
     */
    @Operation(summary = "根据表名与字段名-值映射构建实体值模型")
    public SemanticEntityValueDTO buildEntityValueByName(String tableName, Map<String, Object> fieldNameValues);

    /**
     * 根据实体 UUID 与字段 UUID-值映射构建实体值模型
     *
     * - 自动将字段 UUID 转换为字段名
     * - 返回包含存储值与展示值的语义化实体值
     *
     * @param entityUuid 实体 UUID
     * @param fieldUuidValues 字段 UUID 到值的映射
     * @return 语义化的实体值
     */
    @Operation(summary = "根据实体 UUID 与字段 UUID-值映射构建实体值模型")
    public SemanticEntityValueDTO buildEntityValueByUuid(String entityUuid, Map<String, Object> fieldUuidValues);


    /**
     * 根据条件分页查询实体数据
     *
     * - 支持多条件过滤与多字段排序
     * - 返回语义化的实体值列表与总数
     *
     * @param body 分页与过滤请求体
     * @return 分页结果（语义值列表）
     */
    @Operation(summary = "根据条件分页查询实体数据")
    PageResult<SemanticEntityValueDTO> getDataByCondition(@Valid @RequestBody SemanticPageConditionVO body);
    
    /**
     * 根据主键 ID 查询实体详情
     *
     * - 返回包含连接器值（在实现层读取）与展示值的语义实体
     *
     * @param body 目标请求体（含表名与主键）
     * @return 单条语义化实体值
     */
    @Operation(summary = "根据主键 ID 查询实体详情")
    SemanticEntityValueDTO getDataById(@Valid @RequestBody SemanticTargetBodyVO body);

    /**
     * 根据主键 ID 删除实体数据
     *
     * - 存在 `deleted` 字段时执行软删除，否则物理删除
     *
     * @param body 目标请求体（含表名与主键）
     * @return 删除影响行数
     */
    Integer deleteDataById(@Valid @RequestBody SemanticTargetBodyVO body);

  /**
     * 根据条件删除实体数据
     *
     * - 存在 `deleted` 字段时执行软删除，否则物理删除
     * - 为避免误删全表，条件必填
     *
     * @param body 条件请求体（表名 + 过滤条件）
     * @return 删除影响行数
     */
    Integer deleteDataByCondition(@Valid @RequestBody SemanicTargetConditionVO body);

    /**
     * 根据条件批量更新实体数据并返回更新后的结果
     *
     * - 仅更新实体定义中的合法字段（忽略未知键）
     * - 自动填充系统字段占位（如 `updated_time`、`updater`）
     * - 返回更新后的行的语义值列表（已做权限过滤与引用解析）
     *
     * @param body 条件与更新内容请求体（`tableName` + `semanticConditionDTO` + `updateProperties`）
     * @return 更新后的语义值列表
     */
    List<SemanticEntityValueDTO> updateDataByCondition(@Valid @RequestBody SemanicTargetConditionVO body);

    /**
     * 插入实体数据（编辑态调用）
     *
     * - `SemanicMergeConditionVO` 顶层键为业务字段或连接器名称
     * - 自动编号、系统字段由实现层统一处理
     *
     * @param body 合并请求体（主字段 + 连接器值）
     * @return 插入后的语义值（包含展示值与必要回显）
     */
    SemanticEntityValueDTO insertData(@Valid @RequestBody SemanicMergeConditionVO body);

    /**
     * 根据主键更新实体数据
     *
     * - `SemanticMergeBodyVO` 顶层键为业务字段或连接器名称
     * - 主键 `id` 必传；系统字段（如 `updated_time`）由实现层统一处理
     *
     * @param body 合并请求体（含主键与更新内容）
     * @return 更新后的语义值
     */
    SemanticEntityValueDTO updateDataById(@Valid @RequestBody SemanicMergeConditionVO body);
    
}
