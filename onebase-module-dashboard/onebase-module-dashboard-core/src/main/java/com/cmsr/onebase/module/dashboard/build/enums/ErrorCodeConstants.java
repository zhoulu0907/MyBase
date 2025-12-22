package com.cmsr.onebase.module.dashboard.build.enums;

import com.cmsr.onebase.framework.common.exception.ErrorCode;

/**
 * Dashboard 错误码枚举类
 *
 * dashboard 系统，使用 1-005-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== 仪表盘模板相关 1-005-000-000 ==========
    ErrorCode TEMPLATE_NOT_EXISTS = new ErrorCode(1_005_000_000, "仪表盘模板不存在");
    ErrorCode TEMPLATE_NAME_DUPLICATE = new ErrorCode(1_005_000_001, "已经存在该名称的仪表盘模板");

}