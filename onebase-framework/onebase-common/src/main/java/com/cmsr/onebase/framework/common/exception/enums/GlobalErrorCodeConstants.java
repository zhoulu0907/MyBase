package com.cmsr.onebase.framework.common.exception.enums;

import com.cmsr.onebase.framework.common.exception.ErrorCode;

/**
 * 全局错误码枚举
 * 0-999 系统异常编码保留
 * <p>
 * 一般情况下，使用 HTTP 响应状态码 https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Status
 * 虽然说，HTTP 响应状态码作为业务使用表达能力偏弱，但是使用在系统层面还是非常不错的
 * 比较特殊的是，因为之前一直使用 0 作为成功，就不使用 200 啦。
 *
 */
public interface GlobalErrorCodeConstants {

    ErrorCode SUCCESS = new ErrorCode(0, "成功");

    // ========== 客户端错误段 ==========

    ErrorCode BAD_REQUEST        = new ErrorCode(400, "请求参数不正确");
    ErrorCode UNAUTHORIZED       = new ErrorCode(401, "登录已过期，请重新登录");
    ErrorCode FORBIDDEN          = new ErrorCode(403, "没有该操作权限");
    ErrorCode NOT_FOUND          = new ErrorCode(404, "请求未找到");
    ErrorCode METHOD_NOT_ALLOWED = new ErrorCode(405, "请求方法不正确");
    ErrorCode LOCKED             = new ErrorCode(423, "请求失败，请稍后重试"); // 并发请求，不允许
    ErrorCode TOO_MANY_REQUESTS  = new ErrorCode(429, "请求过于频繁，请稍后重试");
    ErrorCode SEESION_TIMEOUT     = new ErrorCode(401, "会话超时，请重新登录");
    ErrorCode FORBIDDEN_APP     = new ErrorCode(403, "没有该应用的操作权限");

    // ========== 服务端错误段 ==========

    ErrorCode INTERNAL_SERVER_ERROR = new ErrorCode(500, "系统异常");
    ErrorCode NOT_IMPLEMENTED       = new ErrorCode(501, "功能未实现/未开启");
    ErrorCode ERROR_CONFIGURATION   = new ErrorCode(502, "错误的配置项");
    ErrorCode LICENSE_NOT_ENABLE    = new ErrorCode(505, "缺失有效凭证");
    ErrorCode LICENSE_IS_EXPIRED    = new ErrorCode(505, "凭证已过期");
    ErrorCode LICENSE_GET_ERROR     = new ErrorCode(505, "获取凭证失败");


    ErrorCode APP_PERM_CHECK_ERROR     = new ErrorCode(506, "应用权限校验异常");


    // ========== 自定义错误段 ==========
    ErrorCode REPEATED_REQUESTS = new ErrorCode(900, "重复请求，请稍后重试"); // 重复请求

    ErrorCode UNKNOWN = new ErrorCode(999, "未知错误");

}
