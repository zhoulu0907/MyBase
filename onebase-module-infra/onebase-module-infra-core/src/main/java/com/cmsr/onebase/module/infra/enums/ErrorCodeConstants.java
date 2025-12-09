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
    ErrorCode FILE_SIZE_OVERRUN = new ErrorCode(1_001_003_003, "文件大小超过限制，最大允许[{}]MB");
    ErrorCode FILE_NAME_LENGTH_OVERRUN = new ErrorCode(1_001_003_004, "文件名称长度超过限制，最大允许[{}]字符");
    ErrorCode FILE_EXTENSION_UNIDENTIFIABLE = new ErrorCode(1_001_003_005, "无法识别文件扩展名");
    ErrorCode FILE_EXTENSION_NOT_ALLOW = new ErrorCode(1_001_003_006, "不被允许上传的文件类型");
    ErrorCode FILE_MIMETYPE_AND_EXTENSION_MISMATCHING = new ErrorCode(1_001_003_007, "文件MIME类型与扩展名不匹配");
    ErrorCode FILE_FORMAT_AND_EXTENSION_MISMATCHING = new ErrorCode(1_001_003_008, "文件实际格式与扩展名不匹配");
    ErrorCode FILE_TYPE_PDF_CONTENT_NOT_STANDARD = new ErrorCode(1_001_003_009, "PDF文件包含不合规范内容");
    ErrorCode FILE_CHECK_LIST_NOT_EXISTS = new ErrorCode(1_001_003_010, "文件上传检查项配置不能为空");
    ErrorCode FILE_DOWNLOAD_NOT_LOGIN = new ErrorCode(1_001_003_011, "当前用户未登录,无法下载该文件");
    ErrorCode FILE_NOT_DOWNLOAD = new ErrorCode(1_001_003_012, "无法获取文件：环境标识不匹配");
    ErrorCode FILE_PATH_NOT_EXISTS = new ErrorCode(1_001_003_013, "文件路径为空，该文件无法下载");

    // ========== 安全相关 1-001-004-000 ==========
    ErrorCode SECURITY_CONFIG_NOT_EXIST = new ErrorCode(1_001_004_000, "配置项[{}]不存在");
    ErrorCode SECURITY_CONFIG_ITEM_REQUIRED = new ErrorCode(1_001_004_001, "配置项[{}]不能为空");
    ErrorCode SECURITY_CONFIG_DATA_TYPE_NOT_SUPPORT = new ErrorCode(1_001_004_002, "配置项[{}]的数据类型[{}]不支持");
    ErrorCode SECURITY_CONFIG_DATA_TYPE_WRONG = new ErrorCode(1_001_004_003, "配置项[{}]的数据类型不正确，应为[{}]");
    ErrorCode SECURITY_CONFIG_MIN_VALUE = new ErrorCode(1_001_004_004, "配置项[{}]的值必须大于等于{}");
    ErrorCode SECURITY_CONFIG_MAX_VALUE = new ErrorCode(1_001_004_005, "配置项[{}]的值必须小于等于{}");

    // ========== 密码校验相关 1-001-005-000 ==========
    ErrorCode WEAK_PASSWORD_EMPTY = new ErrorCode(1_001_005_000, "密码不能为空");
    ErrorCode WEAK_PASSWORD_TOO_SHORT = new ErrorCode(1_001_005_001, "密码长度不能少于{}位");
    ErrorCode WEAK_PASSWORD_TOO_LONG = new ErrorCode(1_001_005_002, "密码长度不能超过{}位");
    ErrorCode WEAK_PASSWORD_CONTAINS_NO_DIGIT = new ErrorCode(1_001_005_003, "密码必须包含数字");
    ErrorCode WEAK_PASSWORD_CONTAINS_NO_LETTER = new ErrorCode(1_001_005_004, "密码必须包含字母");
    ErrorCode WEAK_PASSWORD_CONTAINS_NO_LOWER_CASE = new ErrorCode(1_001_005_005, "密码必须包含小写字母");
    ErrorCode WEAK_PASSWORD_CONTAINS_NO_UPPER_CASE = new ErrorCode(1_001_005_006, "密码必须包含大写字母");
    ErrorCode WEAK_PASSWORD_CONTAINS_NO_SPECIAL_CHAR = new ErrorCode(1_001_005_007, "密码必须包含特殊符号");
    ErrorCode WEAK_PASSWORD_KEYBOARD_HORIZONTAL_SEQ = new ErrorCode(1_001_005_008, "密码包含键盘横向连续字符");
    ErrorCode WEAK_PASSWORD_KEYBOARD_SLOPE_SEQ = new ErrorCode(1_001_005_009, "密码包含键盘斜向连续字符");
    ErrorCode WEAK_PASSWORD_LOGIC_SEQUENTIAL = new ErrorCode(1_001_005_010, "密码包含逻辑连续字符");
    ErrorCode WEAK_PASSWORD_SAME_CHAR_SEQUENTIAL = new ErrorCode(1_001_005_011, "密码包含连续相同字符");
    ErrorCode WEAK_PASSWORD_TENANT_EMPTY = new ErrorCode(1_001_005_012, "无法获取租户ID信息");
    ErrorCode PASSWORD_IN_HISTORY = new ErrorCode(1_001_005_013, "新密码不能与最近{}次历史密码相同");

    // ========== 防暴力破解相关 1-001-005-020 ==========
    ErrorCode AUTH_LOGIN_ACCOUNT_LOCKED = new ErrorCode(1_001_005_020, "账号已被锁定，请{}后再试");

    // ========== 文件配置 1-001-006-000 ==========
    ErrorCode FILE_CONFIG_NOT_EXISTS = new ErrorCode(1_001_006_000, "文件配置不存在");
    ErrorCode FILE_CONFIG_DELETE_FAIL_MASTER = new ErrorCode(1_001_006_001, "该文件配置不允许删除，原因：它是主配置，删除会导致无法上传文件");
    ErrorCode DATA_SOURCE_CONFIG_NOT_EXISTS = new ErrorCode(1_001_007_000, "数据源配置不存在");
    ErrorCode DATA_SOURCE_CONFIG_NOT_OK = new ErrorCode(1_001_007_001, "数据源配置不正确，无法进行连接");


}
