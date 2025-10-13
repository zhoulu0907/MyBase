package com.cmsr.onebase.module.bpm.enums;

import com.cmsr.onebase.framework.common.exception.ErrorCode;

/**
 * bpm 错误码枚举类
 *
 */
public interface ErrorCodeConstants {

    // ========== 审批流模块 1-008-001-000 ==========
    ErrorCode FLOW_NOT_EXISTS = new ErrorCode(1_008_001_000, "流程不存在");
    ErrorCode FLOW_HAVE_USELESS_SKIP = new ErrorCode(1_008_001_001, "存在无用的跳转");
    ErrorCode MUST_SKIP = new ErrorCode(1_008_001_002, "开始或者中间节点必须画跳转线");
    ErrorCode LOST_NODE_CODE = new ErrorCode(1_008_001_003, "节点编码缺失");
    ErrorCode MUL_START_SKIP = new ErrorCode(1_008_001_004, "节点流转条件不能超过1个");
    ErrorCode LOST_DEST_NODE = new ErrorCode(1_008_001_005, "目标节点为空");
    ErrorCode SAME_CONDITION_NODE = new ErrorCode(1_008_001_006, "互斥网关，同一个节点不能有相同跳转条件，跳转同一个目标节点!");
    ErrorCode SAME_DEST_NODE = new ErrorCode(1_008_001_007, "并行网关，同一个节点不能跳转同一个目标节点!");
    ErrorCode SAME_CONDITION_VALUE = new ErrorCode(1_008_001_008, "中间节点，同一个节点不能有相同跳转类型，跳转同一个目标节点!");
    ErrorCode MUL_START_NODE = new ErrorCode(1_008_001_009, "开始节点不能超过1个!");
    ErrorCode SAME_NODE_CODE = new ErrorCode(1_008_001_010, "同一流程中节点编码重复!");
    ErrorCode LOST_START_NODE = new ErrorCode(1_008_001_011, "流程缺少开始节点!");
    ErrorCode MUL_SKIP_BETWEEN = new ErrorCode(1_008_001_012, "不可同时通过或者退回到多个中间节点，必须先流转到网关节点!");
    ErrorCode NULL_NODE_CODE = new ErrorCode(1_008_001_013, "目标节点编码不存在!");




}
