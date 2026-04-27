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

    ErrorCode CONNECTOR_ENV_NOT_CONFIGURED = new ErrorCode(1123787, "连接器未配置环境信息，请先配置环境后再启用");

    ErrorCode CONNECTOR_SCRIPT_NOT_EXISTS = new ErrorCode(1123784, "脚本配置不存在");

    ErrorCode PROCESS_NOT_EXIST = new ErrorCode(10003, "流程不存在");
    ErrorCode NODE_CONFIG_NOT_EXIST = new ErrorCode(10004, "节点配置不存在");

    ErrorCode INVALID_CONNECTOR_CONFIG = new ErrorCode(1123785, "连接器配置格式错误");

    ErrorCode ACTION_NOT_EXISTS = new ErrorCode(1123786, "动作不存在");

    /**
     * 环境配置不存在
     */
    ErrorCode ENV_CONFIG_NOT_EXISTS = new ErrorCode(1123788, "环境配置不存在：envCode={}");

    /**
     * 节点配置不存在
     */
    ErrorCode NODE_CONFIG_NOT_EXISTS = new ErrorCode(1123790, "节点配置不存在，typeCode={}");

    /**
     * 节点配置无效
     */
    ErrorCode NODE_CONFIG_INVALID = new ErrorCode(1123791, "节点配置无效，typeCode={}");

    /**
     * 动作配置模板为空
     */
    ErrorCode ACTION_CONFIG_EMPTY = new ErrorCode(1123792, "动作配置模板为空");

    /**
     * 环境配置已存在
     */
    ErrorCode ENV_ALREADY_EXISTS = new ErrorCode(1123793, "环境配置已存在");

    /**
     * 环境配置不存在（用于编辑更新）
     */
    ErrorCode ENV_NOT_EXISTS = new ErrorCode(1123800, "环境配置不存在：envName={}");

    /**
     * 启用环境未配置
     */
    ErrorCode ENABLE_ENV_NOT_CONFIGURED = new ErrorCode(1123801, "未正确配置环境信息");

    /**
     * 环境配置格式无效
     */
    ErrorCode INVALID_ENV_CONFIG = new ErrorCode(1123794, "环境配置格式无效");

    /**
     * 动作已存在
     */
    ErrorCode ACTION_ALREADY_EXISTS = new ErrorCode(1123795, "动作已存在：actionCode={}");

    /**
     * 动作配置格式无效
     */
    ErrorCode INVALID_ACTION_CONFIG = new ErrorCode(1123796, "动作配置格式无效，缺少必填字段");

    /**
     * 动作未配置调试信息
     */
    ErrorCode ACTION_DEBUG_NOT_EXISTS = new ErrorCode(1123797, "动作未配置调试信息");

    /**
     * 调试配置URL不能为空
     */
    ErrorCode DEBUG_URL_REQUIRED = new ErrorCode(1123798, "调试配置的URL不能为空");

    /**
     * 调试配置方法不能为空
     */
    ErrorCode DEBUG_METHOD_REQUIRED = new ErrorCode(1123799, "调试配置的请求方法不能为空");
}
