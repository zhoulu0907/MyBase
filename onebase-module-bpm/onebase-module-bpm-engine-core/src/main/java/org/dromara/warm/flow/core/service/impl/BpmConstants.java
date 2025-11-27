package org.dromara.warm.flow.core.service.impl;

/**
 * 流程相关的常量
 *
 * @author liyang
 * @date 2025-11-10
 */
public interface BpmConstants {
    String SYS_USER_ID = "-1";

    /**  节点审批人列表最多100个用户 */
    Integer MAX_NODE_APPROVER_USERS = 100;

    /**  节点审批人列表最多10个角色 */
    Integer MAX_NODE_APPROVER_ROLES = 10;

    /**  节点抄送人列表最多500个用户 */
    Integer MAX_NODE_CC_USERS = 500;

    /**  节点抄送人列表最多50个角色 */
    Integer MAX_NODE_CC_ROLES = 50;

    /**  flow variable 中appId的key */
    String VAR_APP_ID_KEY = "appId";

    /**  flow variable 中entityId的key */
    String VAR_ENTITY_ID_KEY = "entityId";

    /**  flow variable 中editPageView的key */
    String VAR_EDIT_PAGE_VIEW_KEY = "editPageView";

    /**  flow variable 中detailPageView的key */
    String VAR_DETAIL_PAGE_VIEW_KEY = "detailPageView";

    /**  flow variable 中bindingViewId的key */
    String VAR_BINDING_VIEW_ID_KEY = "bindingViewId";

    /**  flow variable 中detailPageView的key */
    String VAR_PAGE_VIEW_GROUP_KEY = "pageViewGroup";

    /**
     * 抄送人常量
     */
    String VAR_CC_USERS_KEY = "ccUsers";

    String AGENT_TITLE_PREFIX = "【代理审批】";
}
