package com.cmsr.onebase.module.etl.core.enums;

import com.cmsr.onebase.framework.common.exception.ErrorCode;

public interface ETLErrorCodeConstants {
    // TODO: 后续规范化后需要修改

    // 通用异常
    ErrorCode UNKNOWN_ERROR = new ErrorCode(110000, "未知内部异常");
    ErrorCode DATASOURCE_ILLEGAL = new ErrorCode(110001, "数据源信息非法");
    ErrorCode DATA_CONFLICT = new ErrorCode(110002, "未知异常，数据冲突");
    // 数据源类型异常
    ErrorCode DATASOURCE_NOT_SUPPORTED = new ErrorCode(110101, "数据源类型不支持");
    ErrorCode ILLEGAL_DATASOURCE_TYPE = new ErrorCode(110102, "数据源类型异常");
    // 数据源信息相关异常
    ErrorCode DATASOURCE_NOT_EXIST = new ErrorCode(110201, "数据源不存在");
    ErrorCode DATASOURCE_CODE_DUPLICATE = new ErrorCode(110202, "数据源编码重复");
    ErrorCode DATASOURCE_PROPERTY_INSUFFICIENT = new ErrorCode(110203, "数据源信息不齐全");
    ErrorCode DATASOURCE_STATUS_CHANGE_ERR = new ErrorCode(110204, "数据源状态转换异常");
    ErrorCode DATASOURCE_IN_USAGE = new ErrorCode(110205, "数据源已在使用中");
    ErrorCode DATASOURCE_READONLY = new ErrorCode(110206, "数据源为只读数据源");

    // 元数据采集异常
    ErrorCode INVALID_COLLECT_STATUS = new ErrorCode(110501, "无效的采集状态");
    ErrorCode METADATA_EMPTY = new ErrorCode(110502, "目标元数据信息不存在");
    ErrorCode METADATA_COLLECT_FAILED = new ErrorCode(110503, "元数据采集失败");
    ErrorCode METADATA_COLLECT_RUNNING = new ErrorCode(110504, "元数据采集运行中，请5分钟后再试");
    ErrorCode NO_CATALOG_AVAILABLE = new ErrorCode(110505, "未采集到CATALOG");
    ErrorCode NO_SCHEMA_AVAILABLE = new ErrorCode(110505, "未采集到SCHEMA");
    ErrorCode NO_TABLE_AVAILABLE = new ErrorCode(110505, "未采集到表");

    // 元数据类型异常
    ErrorCode ILLEGAL_METADATA_TYPE = new ErrorCode(110601, "元数据类型异常");
    ErrorCode TABLE_NOT_EXIST = new ErrorCode(110602, "数据表不存在");

    // 工作流异常
    ErrorCode WORKFLOW_NOT_EXIST = new ErrorCode(110700, "ETL流程不存在");
    ErrorCode WORKFLOW_NAME_DUPLICATE = new ErrorCode(110701, "ETL流程名称重复");
    ErrorCode WORKFLOW_ENABLED = new ErrorCode(110704, "ETL流程已启用，请下线后再试");
    ErrorCode WORKFLOW_DISABLED = new ErrorCode(110705, "ETL流程已停用");

    // 调度类异常
    ErrorCode ILLEGAL_SCHEDULE_TYPE = new ErrorCode(110901, "非法的调度类型");
    ErrorCode WORKFLOW_ALREADY_OFFLINE = new ErrorCode(110902, "ETL流程已下线");

    ErrorCode WORKFLOW_PARAM_ERROR = new ErrorCode(110903, "ETL流程参数错误");
    ErrorCode PREVIEW_ERROR = new ErrorCode(110904, "数据预览异常");
}
