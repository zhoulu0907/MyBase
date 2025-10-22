package com.cmsr.onebase.module.etl.core.enums;

import com.cmsr.onebase.framework.common.exception.ErrorCode;

public interface DataFactoryErrorCodeConstants {
    // TODO: 后续规范化后需要修改

    // 数据源信息
    ErrorCode UNKNOWN_ERROR                         = new ErrorCode(110000, "未知内部异常");
    ErrorCode DATASOURCE_ILLEGAL                    = new ErrorCode(110001, "数据源信息非法");
    // 数据源类型异常
    ErrorCode DATASOURCE_NOT_SUPPORTED              = new ErrorCode(110101, "数据源类型不支持");
    ErrorCode ILLEGAL_DATASOURCE_TYPE               = new ErrorCode(110102, "数据源类型异常");
    // 数据源信息相关异常
    ErrorCode DATASOURCE_NOT_EXIST                  = new ErrorCode(110201, "数据源不存在");
    ErrorCode DATASOURCE_CODE_DUPLICATE             = new ErrorCode(110202, "数据源编码重复");
    ErrorCode DATASOURCE_PROPERTY_INSUFFICIENT      = new ErrorCode(110203, "数据源信息不齐全");

    // 元数据采集异常
    ErrorCode INVALID_COLLECT_STATUS                = new ErrorCode(110501, "无效的采集状态");
    ErrorCode METADATA_EMPTY                        = new ErrorCode(110502, "目标元数据信息不存在");
    ErrorCode METADATA_COLLECT_FAILED               = new ErrorCode(110503, "元数据采集失败");
}
