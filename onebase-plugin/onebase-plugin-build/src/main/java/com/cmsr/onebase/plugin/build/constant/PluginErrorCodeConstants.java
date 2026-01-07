package com.cmsr.onebase.plugin.build.constant;

import com.cmsr.onebase.framework.common.exception.ErrorCode;

/**
 * 插件模块错误码常量
 * 
 * 错误码区间：1-030-000-000 ~ 1-030-999-999
 *
 * @author matianyu
 * @date 2026-01-06
 */
public interface PluginErrorCodeConstants {

    // ========== 文件相关 1-030-001-xxx ==========
    ErrorCode PLUGIN_FILE_EMPTY = new ErrorCode(1_030_001_001, "插件文件不能为空");
    ErrorCode PLUGIN_FILE_TOO_LARGE = new ErrorCode(1_030_001_002, "插件文件大小超过限制（最大100MB）");
    ErrorCode PLUGIN_FILE_NAME_INVALID = new ErrorCode(1_030_001_003, "插件文件名无效");
    ErrorCode PLUGIN_FILE_TYPE_INVALID = new ErrorCode(1_030_001_004, "插件文件类型不支持，仅支持zip和jar格式");
    ErrorCode PLUGIN_FILE_READ_ERROR = new ErrorCode(1_030_001_005, "读取插件文件失败");

    // ========== ZIP校验相关 1-030-002-xxx ==========
    ErrorCode PLUGIN_ZIP_INVALID = new ErrorCode(1_030_002_001, "无效的ZIP文件格式");
    ErrorCode PLUGIN_ZIP_PATH_TRAVERSAL = new ErrorCode(1_030_002_002, "ZIP文件包含非法路径");
    ErrorCode PLUGIN_ZIP_FORBIDDEN_FILE = new ErrorCode(1_030_002_003, "ZIP文件包含禁止的文件类型");
    ErrorCode PLUGIN_JSON_NOT_FOUND = new ErrorCode(1_030_002_004, "ZIP文件中未找到plugin.json");
    ErrorCode PLUGIN_JSON_READ_ERROR = new ErrorCode(1_030_002_005, "读取plugin.json失败");

    // ========== 元数据校验相关 1-030-003-xxx ==========
    ErrorCode PLUGIN_JSON_EMPTY = new ErrorCode(1_030_003_001, "plugin.json内容为空");
    ErrorCode PLUGIN_JSON_PARSE_ERROR = new ErrorCode(1_030_003_002, "plugin.json解析失败");
    ErrorCode PLUGIN_META_PLUGIN_ID_REQUIRED = new ErrorCode(1_030_003_003, "pluginId不能为空");
    ErrorCode PLUGIN_META_PLUGIN_NAME_REQUIRED = new ErrorCode(1_030_003_004, "pluginName不能为空");
    ErrorCode PLUGIN_META_VERSION_REQUIRED = new ErrorCode(1_030_003_005, "pluginVersion不能为空");
    ErrorCode PLUGIN_META_VERSION_FORMAT_INVALID = new ErrorCode(1_030_003_006, "版本号格式无效，应为x.y.z格式");
    ErrorCode PLUGIN_META_PLUGIN_NAME_TOO_LONG = new ErrorCode(1_030_003_007, "插件名称过长（最大200字符）");

    // ========== 插件操作相关 1-030-004-xxx ==========
    ErrorCode PLUGIN_NOT_FOUND = new ErrorCode(1_030_004_001, "插件不存在");
    ErrorCode PLUGIN_ALREADY_EXISTS = new ErrorCode(1_030_004_002, "插件已存在，请使用上传新版本功能");
    ErrorCode PLUGIN_VERSION_ALREADY_EXISTS = new ErrorCode(1_030_004_003, "该版本号已存在");
    ErrorCode PLUGIN_VERSION_NOT_FOUND = new ErrorCode(1_030_004_004, "插件版本不存在");
    ErrorCode PLUGIN_ALREADY_ENABLED = new ErrorCode(1_030_004_005, "插件已处于启用状态");
    ErrorCode PLUGIN_ALREADY_DISABLED = new ErrorCode(1_030_004_006, "插件已处于停用状态");
    ErrorCode PLUGIN_HAS_ENABLED_VERSION = new ErrorCode(1_030_004_007, "该插件存在已启用的版本，请先禁用后再启用新版本");
    ErrorCode PLUGIN_ENABLED_CANNOT_UPDATE = new ErrorCode(1_030_004_008, "启用状态的版本不可更新");
    ErrorCode PLUGIN_ENABLED_CANNOT_DELETE = new ErrorCode(1_030_004_009, "启用状态的版本不可删除");
    ErrorCode PLUGIN_ONLY_VERSION_CANNOT_DELETE = new ErrorCode(1_030_004_010, "唯一版本不可删除，每个插件至少保留一个版本");

}
