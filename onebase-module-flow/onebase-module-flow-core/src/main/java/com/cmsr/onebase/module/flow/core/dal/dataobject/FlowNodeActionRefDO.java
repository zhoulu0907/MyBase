package com.cmsr.onebase.module.flow.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 逻辑流节点对连接器动作的引用关系
 *
 * @author onebase
 * @since 2026-01-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "flow_node_action_ref")
public class FlowNodeActionRefDO extends BaseEntity {

    @Column(value = "node_id")
    private Long nodeId;

    @Column(value = "connector_id")
    private Long connectorId;

    @Column(value = "action_id")
    private String actionId;

    @Column(value = "action_version")
    private Integer actionVersion;

    @Column(value = "flow_version")
    private String flowVersion;
}
