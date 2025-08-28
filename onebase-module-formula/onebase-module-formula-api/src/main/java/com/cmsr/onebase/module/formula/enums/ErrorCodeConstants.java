package com.cmsr.onebase.module.formula.enums;

import com.cmsr.onebase.framework.common.exception.ErrorCode;

/**
 * Formula 错误码枚举类
 *
 * formula 公式，使用 1-007-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== 函数模块 1-007-001-000 ==========
    ErrorCode FUNCTION_NOT_EXISTS = new ErrorCode(1_007_001_000, "函数不存在");
    ErrorCode FUNCTION_NAME_DUPLICATE = new ErrorCode(1_007_001_001, "已经存在名为【{}】的函数");
    ErrorCode FUNCTION_IS_DISABLE = new ErrorCode(1_007_001_002, "名为【{}】的函数已被禁用");

    // ========== 公式模块 1-007-002-000 ==========
    ErrorCode FORMULA_NOT_EXISTS = new ErrorCode(1_007_002_000, "公式不存在");
    ErrorCode FORMULA_NAME_DUPLICATE = new ErrorCode(1_007_002_001, "已经存在名为【{}】的公式");

}
