package com.cmsr.onebase.module.app.core.enums;

import com.cmsr.onebase.framework.common.exception.ErrorCode;

/**
 * @Author：huangjie
 * @Date：2025/7/24 8:51
 */
public interface AppErrorCodeConstants {

    ErrorCode APP_NOT_EXIST = new ErrorCode(10001, "应用不存在");

    ErrorCode APP_NAME_ERROR = new ErrorCode(10002, "应用名称错误");

    ErrorCode APP_VERSION_NOT_EXIST = new ErrorCode(10003, "应用版本不存在");

    ErrorCode APP_MENU_NOT_EXIST = new ErrorCode(10004, "应用菜单不存在");

    ErrorCode APP_MENU_GROUP_HAS_CHILDREN = new ErrorCode(10005, "菜单组内有页面不可删除，如需删除请先移出页面");

    ErrorCode APP_MENU_GROUP_NOT_ALLOW_COPY = new ErrorCode(10006, "菜单组不可复制");

    ErrorCode APP_TAG_EXIST = new ErrorCode(10007, "标签已存在");

    ErrorCode APP_CODE_DUPLICATE = new ErrorCode(10008, "应用编码重复");

    ErrorCode APP_MENU_TYPE_ERROR = new ErrorCode(10009, "菜单类型错误");

    ErrorCode APP_AUTH_ROLE_NAME_EXISTS = new ErrorCode(10010, "角色名称已存在");

    ErrorCode APP_AUTH_ROLE_NOT_EXISTS = new ErrorCode(10011, "角色不存在");

    ErrorCode APP_AUTH_ROLE_NOT_ALLOW_DELETE = new ErrorCode(10012, "角色不允许删除");
    ErrorCode APP_AUTH_ROLE_NOT_ALLOW_RENAME = new ErrorCode(10013, "角色不允许重命名");
    ErrorCode APP_UID_GENERATE_FAILED = new ErrorCode(10014, "应用UID生成失败");

    ErrorCode NOT_LOGIN = new ErrorCode(10015, "用户未登录或登录信息已失效,请检查");
}
