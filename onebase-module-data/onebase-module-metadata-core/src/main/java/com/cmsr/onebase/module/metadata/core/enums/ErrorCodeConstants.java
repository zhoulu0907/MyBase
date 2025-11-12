package com.cmsr.onebase.module.metadata.core.enums;

import com.cmsr.onebase.framework.common.exception.ErrorCode;

/**
 * Metadata 错误码枚举类
 *
 * metadata 系统，使用 1-003-000-000 段
 *
 * @author matianyu
 * @date 2025-08-20
 */
public interface ErrorCodeConstants {

    // ========== 校验规则分组相关 1-003-001-000 ==========
    ErrorCode VALIDATION_RULE_GROUP_NOT_EXISTS = new ErrorCode(1_003_001_000, "校验规则分组不存在");
    ErrorCode VALIDATION_RULE_GROUP_NAME_DUPLICATE = new ErrorCode(1_003_001_001, "校验规则分组名称已存在");

    // ========== 业务实体相关 1-003-002-000 ==========
    ErrorCode BUSINESS_ENTITY_NOT_EXISTS = new ErrorCode(1_003_002_000, "业务实体不存在");
    ErrorCode BUSINESS_ENTITY_NAME_DUPLICATE = new ErrorCode(1_003_002_001, "业务实体名称已存在");
    ErrorCode BUSINESS_ENTITY_CODE_DUPLICATE = new ErrorCode(1_003_002_002, "业务实体编码已存在");

    // ========== 实体字段相关 1-003-003-000 ==========
    ErrorCode ENTITY_FIELD_NOT_EXISTS = new ErrorCode(1_003_003_000, "实体字段不存在");
    ErrorCode ENTITY_FIELD_NAME_DUPLICATE = new ErrorCode(1_003_003_001, "实体字段名称[{}]已存在");
    ErrorCode ENTITY_FIELD_CODE_DUPLICATE = new ErrorCode(1_003_003_002, "实体字段编码已存在");
    ErrorCode ENTITY_FIELD_DISPLAY_NAME_DUPLICATE = new ErrorCode(1_003_003_003, "实体字段显示名称[{}]已存在");
    ErrorCode ENTITY_FIELD_NAME_IS_SYSTEM_RESERVED = new ErrorCode(1_003_003_004, "字段名[{}]与系统保留字段冲突，不能使用");

    // ========== 数据源相关 1-003-004-000 ==========
    ErrorCode DATASOURCE_NOT_EXISTS = new ErrorCode(1_003_004_000, "数据源不存在");
    ErrorCode DATASOURCE_NAME_DUPLICATE = new ErrorCode(1_003_004_001, "数据源名称已存在");
    ErrorCode DATASOURCE_CONNECTION_FAILED = new ErrorCode(1_003_004_002, "数据源连接失败");
    ErrorCode DATASOURCE_CODE_DUPLICATE = new ErrorCode(1_003_004_003, "数据源编码已存在");

    // ========== 实体关系相关 1-003-007-000 ==========
    ErrorCode ENTITY_RELATIONSHIP_NOT_EXISTS = new ErrorCode(1_003_007_000, "实体关系不存在");

    // ========== 自动编号相关 1-003-005-000 ==========
    ErrorCode AUTO_NUMBER_CONFIG_NOT_EXISTS = new ErrorCode(1_003_005_000, "自动编号配置不存在");
    ErrorCode AUTO_NUMBER_CONFIG_DUPLICATE = new ErrorCode(1_003_005_001, "自动编号配置已存在");
    ErrorCode AUTO_NUMBER_GENERATE_FAILED = new ErrorCode(1_003_005_002, "自动编号生成失败");

    // ========== 数据方法相关 1-003-006-000 ==========
    ErrorCode DATA_METHOD_NOT_EXISTS = new ErrorCode(1_003_006_000, "数据方法不存在");
    ErrorCode FIELD_REQUIRED = new ErrorCode(1_003_006_001, "字段[{}]为必填字段");
    ErrorCode PRIMARY_KEY_UPDATE_NOT_ALLOWED = new ErrorCode(1_003_006_002, "不允许更新主键字段");
    ErrorCode DATA_NOT_EXISTS = new ErrorCode(1_003_006_003, "数据不存在");

    // ========== 前置后置流程触发相关 1-003-008-000 ==========
    ErrorCode PROCESS_ERROR_BEFORE_CREATE = new ErrorCode(1_003_008_000, "数据插入前置工作流触发失败，接口返回：{}");
    ErrorCode PROCESS_ERROR_AFTER_CREATE = new ErrorCode(1_003_008_001, "数据插入前置工作流触发失败，接口返回：{}");
    ErrorCode PROCESS_ERROR_BEFORE_UPDATE = new ErrorCode(1_003_008_002, "数据更新前置工作流触发失败，接口返回：{}");
    ErrorCode PROCESS_ERROR_AFTER_UPDATE = new ErrorCode(1_003_008_003, "数据更新后置工作流触发失败，接口返回：{}");
    ErrorCode PROCESS_ERROR_BEFORE_DELETE = new ErrorCode(1_003_008_004, "数据删除前置工作流触发失败，接口返回：{}");
    ErrorCode PROCESS_ERROR_AFTER_DELETE = new ErrorCode(1_003_008_005, "数据删除后置工作流触发失败，接口返回：{}");

    // ========== 数据CRUD相关 1-003-009-000 ==========
    ErrorCode DB_OPERATION_ERROR_CREATE = new ErrorCode(1_003_009_000, "数据插入失败：{}");
    ErrorCode DB_OPERATION_ERROR_UPDATE = new ErrorCode(1_003_009_001, "数据更新失败：{}");
    ErrorCode DB_OPERATION_ERROR_DELETE = new ErrorCode(1_003_009_002, "数据删除失败：{}");
    ErrorCode DB_OPERATION_ERROR_QUERY = new ErrorCode(1_003_009_003, "数据查询失败：{}");

    ErrorCode DATA_METHOD_EXEC_FAIL = new ErrorCode(1_003_006_004, "数据方法执行失败: {}");

    ErrorCode METADATA_DATA_METHOD_RUNTIME_MENU_ID_REQUIRED = new ErrorCode(1_003_006_005, "菜单ID不能为空");



}
