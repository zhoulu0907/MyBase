package com.cmsr.onebase.module.infra.enums;

import com.cmsr.onebase.framework.common.exception.ErrorCode;

/**
 * Infra 错误码枚举类
 *
 * infra 系统，使用 1-001-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== 参数配置 1-001-000-000 ==========
    ErrorCode CONFIG_NOT_EXISTS = new ErrorCode(1_001_000_001, "参数配置不存在");
    ErrorCode CONFIG_KEY_DUPLICATE = new ErrorCode(1_001_000_002, "参数配置 key 重复");
    ErrorCode CONFIG_CAN_NOT_DELETE_SYSTEM_TYPE = new ErrorCode(1_001_000_003, "不能删除类型为系统内置的参数配置");
    ErrorCode CONFIG_GET_VALUE_ERROR_IF_VISIBLE = new ErrorCode(1_001_000_004, "获取参数配置失败，原因：不允许获取不可见配置");

    // ========== 定时任务 1-001-001-000 ==========

    // ========== API 错误日志 1-001-002-000 ==========
    ErrorCode API_ERROR_LOG_NOT_FOUND = new ErrorCode(1_001_002_000, "API 错误日志不存在");
    ErrorCode API_ERROR_LOG_PROCESSED = new ErrorCode(1_001_002_001, "API 错误日志已处理");

    // ========= 文件相关 1-001-003-000 =================
    ErrorCode FILE_PATH_EXISTS = new ErrorCode(1_001_003_000, "文件路径已存在");
    ErrorCode FILE_NOT_EXISTS = new ErrorCode(1_001_003_001, "文件不存在");
    ErrorCode FILE_IS_EMPTY = new ErrorCode(1_001_003_002, "文件为空");

    // ========== 安全相关 1-001-004-000 ==========
    ErrorCode SECURITY_CONFIG_NOT_EXIST = new ErrorCode(1_001_004_000, "配置项[{}]不存在");
    ErrorCode SECURITY_CONFIG_ITEM_REQUIRED = new ErrorCode(1_001_004_001, "配置项[{}]不能为空");
    ErrorCode SECURITY_CONFIG_DATA_TYPE_NOT_SUPPORT = new ErrorCode(1_001_004_002, "配置项[{}]的数据类型[{}]不支持");
    ErrorCode SECURITY_CONFIG_DATA_TYPE_WRONG = new ErrorCode(1_001_004_003, "配置项[{}]的数据类型不正确，应为[{}]");
    ErrorCode SECURITY_CONFIG_MIN_VALUE = new ErrorCode(1_001_004_004, "配置项[{}]的值必须大于等于{}");
    ErrorCode SECURITY_CONFIG_MAX_VALUE = new ErrorCode(1_001_004_005, "配置项[{}]的值必须小于等于{}");

    // ========== 文件配置 1-001-006-000 ==========
    ErrorCode FILE_CONFIG_NOT_EXISTS = new ErrorCode(1_001_006_000, "文件配置不存在");
    ErrorCode FILE_CONFIG_DELETE_FAIL_MASTER = new ErrorCode(1_001_006_001, "该文件配置不允许删除，原因：它是主配置，删除会导致无法上传文件");
    ErrorCode DATA_SOURCE_CONFIG_NOT_EXISTS = new ErrorCode(1_001_007_000, "数据源配置不存在");
    ErrorCode DATA_SOURCE_CONFIG_NOT_OK = new ErrorCode(1_001_007_001, "数据源配置不正确，无法进行连接");


}
