package com.cmsr.onebase.module.app.enums.app;

import com.cmsr.onebase.framework.common.exception.ErrorCode;

/**
 * @Author：huangjie
 * @Date：2025/7/24 8:51
 */
public interface AppErrorCodeConstants {

    ErrorCode APP_NOT_EXIST = new ErrorCode(10001, "应用不存在");

    ErrorCode APP_NAME_ERROR = new ErrorCode(10002, "应用名称错误");

    ErrorCode APPLICATION_VERSION_NOT_EXIST = new ErrorCode(10003, "应用版本不存在");
}
