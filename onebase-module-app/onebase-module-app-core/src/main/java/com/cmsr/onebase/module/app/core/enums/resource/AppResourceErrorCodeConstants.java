package com.cmsr.onebase.module.app.core.enums.resource;

import com.cmsr.onebase.framework.common.exception.ErrorCode;

/**
 * @Author：mickey
 * @Date：2025/7/31 10:00
 */
public interface AppResourceErrorCodeConstants {

    ErrorCode PAGE_SET_NOT_EXIST = new ErrorCode(20001, "页面集不存在");

    ErrorCode PAGE_NOT_EXIST = new ErrorCode(20002, "页面不存在");

    ErrorCode PAGE_TYPE_ERROR = new ErrorCode(20003, "页面类型错误");

    ErrorCode APP_RESOURCE_MENU_NOT_EXIST = new ErrorCode(20004, "菜单不存在");

}
