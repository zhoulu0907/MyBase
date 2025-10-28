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

    ErrorCode FLOW_INSTANCE_NOT_EXISTS = new ErrorCode(1_009_001_008, "流程实例不存在");

    ErrorCode FLOW_NODE_NOT_EXISTS = new ErrorCode(1_009_001_009, "流程节点不存在");

    ErrorCode FLOW_PERMISSION_DENY = new ErrorCode(1_009_001_010, "权限不足");

    ErrorCode PUBLISHED_FLOW_NOT_EXISTS = new ErrorCode(1_009_001_000, "不存在已发布流程");

    ErrorCode FLOW_ENTITY_DATA_ID_NOT_EXISTS = new ErrorCode(1_009_001_011, "流程实体数据ID不存在");


    // ============= 校验 =============

    ErrorCode UNSUPPORT_NODE_TYPE = new ErrorCode(1_009_002_000, "不支持的节点类型");


    ErrorCode BPM_NODE_EXT_EMPTY = new ErrorCode(1_009_002_001, "节点扩展信息不能为空");

    ErrorCode VALIDATE_BPM_DEF_JSON_FAILED = new ErrorCode(1_009_002_002, "校验流程定义JSON失败");

    ErrorCode UNSUPPORT_NODE_APPROVER_TYPE = new ErrorCode(1_009_002_003, "不支持的审批人类型");

    ErrorCode MISSING_NODE_USER_LIST = new ErrorCode(1_009_002_004, "审批人列表不能为空");

    ErrorCode MISSING_NODE_ROLE_LIST = new ErrorCode(1_009_002_005, "审批角色列表不能为空");

    ErrorCode UNSUPPORT_NODE_APPROVAL_MODE = new ErrorCode(1_009_002_006, "不支持的审批方式");

    ErrorCode MISSING_NODE_VO_DATA = new ErrorCode(1_009_002_007, "缺少节点视图数据");

    ErrorCode MISSING_NODE_BTN_CFG = new ErrorCode(1_009_002_008, "节点缺少按钮配置");

    ErrorCode MISSING_ACTION_BUTTON_TYPE = new ErrorCode(1_009_002_009, "缺少操作按钮类型");

    ErrorCode UNSUPPORT_ACTION_BUTTON_TYPE = new ErrorCode(1_009_002_010, "不支持的操作按钮类型");

    ErrorCode MISSING_NODE_FIELD_PERM_CFG = new ErrorCode(1_009_002_011, "节点缺少字段权限配置");

    ErrorCode FLOW_NODE_TYPE_MUST_BE_INITIATION = new ErrorCode(1_009_002_012, "流程节点类型必须为提交节点");

    ErrorCode FLOW_TASK_NOT_EXISTS = new ErrorCode(1_009_002_013, "流程任务不存在");
}