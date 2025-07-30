package com.cmsr.onebase.module.metadata.enums;

import com.cmsr.onebase.framework.common.exception.ErrorCode;

/**
 * 元数据管理错误码枚举类
 *
 * @author bty418
 * @date 2025-01-25
 */
public interface ErrorCodeConstants {

    // ========== 数据源管理 1-001-000-000 ==========
    ErrorCode DATASOURCE_NOT_EXISTS = new ErrorCode(1_001_000_001, "数据源不存在");
    ErrorCode DATASOURCE_CODE_DUPLICATE = new ErrorCode(1_001_000_002, "数据源编码重复");
    ErrorCode DATASOURCE_CONNECTION_FAILED = new ErrorCode(1_001_000_003, "数据源连接失败");

    // ========== 业务实体管理 1-001-001-000 ==========
    ErrorCode BUSINESS_ENTITY_NOT_EXISTS = new ErrorCode(1_001_001_001, "业务实体不存在");
    ErrorCode BUSINESS_ENTITY_CODE_DUPLICATE = new ErrorCode(1_001_001_002, "业务实体编码重复");

    // ========== 实体字段管理 1-001-002-000 ==========
    ErrorCode ENTITY_FIELD_NOT_EXISTS = new ErrorCode(1_001_002_001, "实体字段不存在");
    ErrorCode ENTITY_FIELD_NAME_DUPLICATE = new ErrorCode(1_001_002_002, "实体字段名称重复");
    ErrorCode ENTITY_FIELD_CODE_DUPLICATE = new ErrorCode(1_001_002_003, "实体字段编码重复");

    // ========== 实体关系管理 1-001-003-000 ==========
    ErrorCode ENTITY_RELATIONSHIP_NOT_EXISTS = new ErrorCode(1_001_003_001, "实体关系不存在");

    // ========== 校验规则管理 1-001-004-000 ==========
    ErrorCode VALIDATION_RULE_NOT_EXISTS = new ErrorCode(1_001_004_001, "校验规则不存在");
    ErrorCode VALIDATION_RULE_CODE_DUPLICATE = new ErrorCode(1_001_004_002, "校验规则编码重复");

} 