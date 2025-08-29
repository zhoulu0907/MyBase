package com.cmsr.onebase.module.flow.dal.dataobject;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "flow_process_trigger_entity")
public class FlowProcessTriggerEntityDO extends TenantBaseDO {

    /**
     * 主键ID
     */
    @Column(name = "id", length = 19, nullable = false)
    private Long id;
    /**
     * 流程ID
     */
    @Column(name = "process_id", length = 19, nullable = false)
    private Long processId;
    /**
     * 节点ID
     */
    @Column(name = "node_id", length = 64, nullable = false)
    private String nodeId;
    /**
     * 触发事件
     */
    @Column(name = "trigger_event", length = 64)
    private String triggerEvent;
    /**
     * 触发实体ID
     */
    @Column(name = "entity_id", length = 19, nullable = false)
    private Long entityId;
    /**
     * 触发字段ID,哪些字段变更时才触发
     */
    @Column(name = "trigger_fields", length = 2147483647)
    private String triggerFields;
    /**
     * 数据过滤条件，金额 > 50000 且 客户等级 = 'VIP
     */
    @Column(name = "filter_condition", length = 2147483647)
    private String filterCondition;
    /**
     * 是否允许递归触发
     */
    @Column(name = "is_recursion_trigger_allowed", length = 5)
    private Integer isRecursionTriggerAllowed;

}