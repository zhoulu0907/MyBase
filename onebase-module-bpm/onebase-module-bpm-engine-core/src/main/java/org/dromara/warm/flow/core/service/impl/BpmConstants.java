package org.dromara.warm.flow.core.service.impl;

/**
 * 流程相关的常量
 *
 * @author liyang
 * @date 2025-11-10
 */
public interface BpmConstants {

    /**  节点审批人列表最多100个用户 */
    Integer MAX_NODE_APPROVER_USERS = 100;

    /**  节点审批人列表最多10个角色 */
    Integer MAX_NODE_APPROVER_ROLES = 10;

    /**  flow variable 中appId的key */
    String VAR_APP_ID_KEY = "appId";
}
