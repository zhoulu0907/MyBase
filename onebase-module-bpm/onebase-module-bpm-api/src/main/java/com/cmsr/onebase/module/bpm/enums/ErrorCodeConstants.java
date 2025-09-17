package com.cmsr.onebase.module.bpm.enums;

import com.cmsr.onebase.framework.common.exception.ErrorCode;

/**
 * bpm 错误码枚举类
 *
 */
public interface ErrorCodeConstants {

    // ========== 审批流模块 1-008-001-000 ==========
    ErrorCode FLOW_NOT_EXISTS = new ErrorCode(1_008_001_000, "流程不存在");

}
