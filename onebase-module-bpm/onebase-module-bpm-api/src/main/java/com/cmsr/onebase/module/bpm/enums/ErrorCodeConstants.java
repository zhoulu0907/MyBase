package com.cmsr.onebase.module.bpm.enums;

import com.cmsr.onebase.framework.common.exception.ErrorCode;

/**
 *
 * 模块 bpm 错误码区间 [1-009-000-000 ~ 1-010-000-000)
 */
public interface ErrorCodeConstants {


    // ========== 审批流模块 1-009-001-000 ==========
    ErrorCode FLOW_NOT_EXISTS = new ErrorCode(1_009_001_000, "流程不存在");

    ErrorCode QUERY_FLOW_FAILED = new ErrorCode(1_009_001_001, "查询流程失败");

    ErrorCode SAVE_FLOW_FAILED = new ErrorCode(1_009_001_002, "保存流程失败");


    ErrorCode UNSUPPORT_NODE_TYPE = new ErrorCode(1_009_001_003, "不支持的节点类型");


    ErrorCode BPM_NODE_EXT_EMPTY = new ErrorCode(1_009_001_004, "节点扩展信息不能为空");

}
