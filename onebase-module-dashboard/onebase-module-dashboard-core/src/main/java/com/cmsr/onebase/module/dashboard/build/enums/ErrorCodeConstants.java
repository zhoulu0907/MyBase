package com.cmsr.onebase.module.dashboard.build.enums;

import com.cmsr.onebase.framework.common.exception.ErrorCode;

/**
 * Dashboard 错误码枚举类
 *
 * dashboard 系统，使用 1-005-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== 大屏模板相关 1-005-000-000 ==========
    ErrorCode TEMPLATE_NOT_EXISTS = new ErrorCode(1_005_000_000, "大屏模板不存在");
    ErrorCode TEMPLATE_NAME_DUPLICATE = new ErrorCode(1_005_000_001, "已经存在该名称的大屏模板");
    ErrorCode TEMPLATE_CANT_NOT_UPATE_DEL = new ErrorCode(1_005_000_002, "系统模板不允许修改或删除");

    // ========== 大屏相关 1-006-000-000 ==========
    ErrorCode DASHBOARD_CONTENT_NOT_EXIST = new ErrorCode(1_006_000_001, "大屏内容为空，不能另存为模板");

}