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

    /**  节点抄送人列表最多500个用户 */
    Integer MAX_NODE_CC_USERS = 500;

    /**  节点抄送人列表最多50个角色 */
    Integer MAX_NODE_CC_ROLES = 50;
}
