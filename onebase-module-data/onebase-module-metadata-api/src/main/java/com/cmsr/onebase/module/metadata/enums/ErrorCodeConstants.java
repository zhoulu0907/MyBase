package com.cmsr.onebase.module.metadata.enums;

import com.cmsr.onebase.framework.common.exception.ErrorCode;

/**
 * Metadata 错误码枚举类
 *
 * metadata 系统，使用 1-010-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== 数据源 1-010-001-000 ==========
    ErrorCode DATASOURCE_NOT_EXISTS = new ErrorCode(1_010_001_000, "数据源不存在");
    ErrorCode DATASOURCE_CODE_DUPLICATE = new ErrorCode(1_010_001_001, "已经存在编码为【{}】的数据源");

    // ========== 业务实体 1-010-002-000 ==========
    ErrorCode BUSINESS_ENTITY_NOT_EXISTS = new ErrorCode(1_010_002_000, "业务实体不存在");
    ErrorCode BUSINESS_ENTITY_CODE_DUPLICATE = new ErrorCode(1_010_002_001, "已经存在编码为【{}】的业务实体");

    // ========== 实体字段 1-010-003-000 ==========
    ErrorCode ENTITY_FIELD_NOT_EXISTS = new ErrorCode(1_010_003_000, "实体字段不存在");
    ErrorCode ENTITY_FIELD_CODE_DUPLICATE = new ErrorCode(1_010_003_001, "已经存在编码为【{}】的实体字段");

    // ========== 数据方法 1-010-004-000 ==========
    ErrorCode DATA_METHOD_NOT_EXISTS = new ErrorCode(1_010_004_000, "数据方法不存在");

}
