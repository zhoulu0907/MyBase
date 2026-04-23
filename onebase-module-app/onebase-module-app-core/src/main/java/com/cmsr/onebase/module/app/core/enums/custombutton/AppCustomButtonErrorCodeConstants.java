package com.cmsr.onebase.module.app.core.enums.custombutton;

import com.cmsr.onebase.framework.common.exception.ErrorCode;

/**
 * 自定义按钮错误码
 */
public interface AppCustomButtonErrorCodeConstants {

    ErrorCode CUSTOM_BUTTON_NOT_EXISTS = new ErrorCode(21001, "自定义按钮不存在");

    ErrorCode CUSTOM_BUTTON_DISABLED = new ErrorCode(21002, "自定义按钮已停用");

    ErrorCode CUSTOM_BUTTON_NAME_DUPLICATE = new ErrorCode(21003, "同一页面集下按钮名称重复");

    ErrorCode CUSTOM_BUTTON_COUNT_EXCEED_LIMIT = new ErrorCode(21004, "同一页面集下自定义按钮数量超过上限(10)");

    ErrorCode CUSTOM_BUTTON_ACTION_TYPE_INVALID = new ErrorCode(21005, "按钮动作类型不合法");

    ErrorCode CUSTOM_BUTTON_SCOPE_INVALID = new ErrorCode(21006, "按钮操作范围不合法");

    ErrorCode CUSTOM_BUTTON_PERMISSION_DENIED = new ErrorCode(21007, "当前用户无按钮操作权限");

    ErrorCode CUSTOM_BUTTON_PAGESET_NOT_EXISTS = new ErrorCode(21008, "页面集不存在");

    ErrorCode CUSTOM_BUTTON_FLOW_CONFIG_REQUIRED = new ErrorCode(21009, "执行自动化流动作必须配置流程信息");

    ErrorCode CUSTOM_BUTTON_BATCH_RECORDS_EMPTY = new ErrorCode(21010, "批量执行记录不能为空");
}
