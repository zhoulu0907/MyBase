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
    ErrorCode FORMULA_NOT_EXISTS   = new ErrorCode(1_007_002_000, "公式不存在");
    ErrorCode FORMULA_SYNTAX_ERROR = new ErrorCode(1_007_002_001, "公式语法错误（请检查公式拼写，是否存在中文字符等异常）");
    ErrorCode FORMULA_GUEST_EXCEPTION = new ErrorCode(1_007_002_002, "客户语言代码异常(您编写的公式在运行时发生了错误)");
    ErrorCode FORMULA_HOST_EXCEPTION = new ErrorCode(1_007_002_003, "主机运行时异常(系统底层运行时发生错误)");
    ErrorCode FORMULA_IS_CANCELLED = new ErrorCode(1_007_002_004, "执行被取消(程序执行已被中断或取消)");
    ErrorCode FORMULA_ISEXIT_ERROR = new ErrorCode(1_007_002_005, "程序请求退出(程序已自行退出)");
    ErrorCode FORMULA_INCOMPLETESOURCE_ERROR = new ErrorCode(1_007_002_006, "代码不完整，请补充完整后重试。");
    ErrorCode FORMULA_INTERNAL_ERROR = new ErrorCode(1_007_002_007, "公式系统内部错误");
    ErrorCode FORMULA_INTERRUPTED_ERROR = new ErrorCode(1_007_002_008, "公式执行被中断");
    ErrorCode FORMULA_RESOURCEEXHAUSTED_ERROR = new ErrorCode(1_007_002_009, "系统资源不足，无法继续执行公式。");
    ErrorCode FORMULA_OTHER_ERROR = new ErrorCode(1_007_002_010, "公式执行异常，稍后再试。");

}
