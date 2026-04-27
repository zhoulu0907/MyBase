package com.cmsr.onebase.framework.common.exception;

/**
 * @Author：huangjie
 * @Date：2025/8/22 17:14
 */
public interface DatabaseAccessErrorCodes {

    ErrorCode DB_SELECT_ERROR = new ErrorCode(100, "数据查询失败");

    ErrorCode DB_INSERT_ERROR = new ErrorCode(101, "数据插入失败");

    ErrorCode DB_UPDATE_ERROR = new ErrorCode(102, "数据更新失败");

    ErrorCode DB_DELETE_ERROR = new ErrorCode(103, "数据删除失败");

    ErrorCode DB_ID_NULL = new ErrorCode(104, "数据ID为空");

    ErrorCode UPDATE_WHERE_IS_NULL = new ErrorCode(105, "更新条件为空");
}
