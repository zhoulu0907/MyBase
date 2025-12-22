package com.cmsr.onebase.module.flow.core.enums;

import com.cmsr.onebase.framework.common.exception.ErrorCode;
import com.cmsr.onebase.framework.common.pojo.CommonResult;

/**
 * @Author：huangjie
 * @Date：2025/7/24 8:51
 */
public interface FlowErrorCodeConstants {

    ErrorCode FLOW_NOT_EXIST = new ErrorCode(10001, "流程不存在");


    ErrorCode LOG_NOT_EXIST = new ErrorCode(10002, "执行日志不存在");


    ErrorCode CONNECTOR_NOT_EXISTS = new ErrorCode(1123784, "连接器不存在");

    ErrorCode CONNECTOR_SCRIPT_NOT_EXISTS = new ErrorCode(1123784, "脚本配置不存在");

    ErrorCode PROCESS_NOT_EXIST = new ErrorCode(10003, "流程不存在");
    ErrorCode NODE_CONFIG_NOT_EXIST = new ErrorCode(10004, "节点配置不存在");
}
