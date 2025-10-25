package com.cmsr.onebase.module.bpm.api.enums;

import com.cmsr.onebase.framework.common.exception.ErrorCode;

/**
 *
 * 模块 bpm 错误码区间 [1-009-000-000 ~ 1-010-000-000)
 *
 * @author liyang
 * @date 2025-10-21
 */
public interface ErrorCodeConstants {


    // ============= 数据相关操作 =============

    ErrorCode FLOW_NOT_EXISTS = new ErrorCode(1_009_001_000, "流程不存在");

    ErrorCode QUERY_FLOW_FAILED = new ErrorCode(1_009_001_001, "查询流程失败");

    ErrorCode SAVE_FLOW_FAILED = new ErrorCode(1_009_001_002, "保存流程失败");

    ErrorCode DELETE_FLOW_FAILED = new ErrorCode(1_009_001_003, "删除流程失败");

    ErrorCode DELETE_FLOW_FAILED_FOR_PUBLISHED = new ErrorCode(1_009_001_004, "已发布流程无法删除");

    ErrorCode DELETE_FLOW_FAILED_FOR_INS_NOT_FINISHED = new ErrorCode(1_009_001_005, "包含历史未完结的历史版本无法删除");

    ErrorCode SAVE_FLOW_FAILED_FOR_NOT_DESIGN_STATUS = new ErrorCode(1_009_001_006, "非设计状态流程无法更新");

    ErrorCode DESIGNING_FLOW_EXISTS = new ErrorCode(1_009_001_007, "存在设计中状态的流程");

    // ============= 校验 =============

    ErrorCode UNSUPPORT_NODE_TYPE = new ErrorCode(1_009_002_000, "不支持的节点类型");


    ErrorCode BPM_NODE_EXT_EMPTY = new ErrorCode(1_009_002_001, "节点扩展信息不能为空");

    ErrorCode VALIDATE_BPM_DEF_JSON_FAILED = new ErrorCode(1_009_002_002, "校验流程定义JSON失败");

}
