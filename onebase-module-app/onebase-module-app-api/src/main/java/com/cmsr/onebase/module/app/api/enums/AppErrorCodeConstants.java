package com.cmsr.onebase.module.app.api.enums;

import com.cmsr.onebase.framework.common.exception.ErrorCode;

/**
 * @Author：huangjie
 * @Date：2025/7/24 8:51
 */
public interface AppErrorCodeConstants {

    ErrorCode APP_NOT_EXIST = new ErrorCode(10001, "应用不存在");

    ErrorCode APP_NAME_ERROR = new ErrorCode(10002, "应用名称错误");
}
