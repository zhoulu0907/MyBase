package com.cmsr.onebase.module.flow.dal.dataobject;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "flow_process_trigger_form")
public class FlowProcessTriggerFormDO extends TenantBaseDO {

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
     * 触发范围：record/field
     */
    @Column(name = "trigger_scope", length = 32, nullable = false)
    private String triggerScope;
    /**
     * 触发事件
     */
    @Column(name = "trigger_event", length = 64)
    private String triggerEvent;
    /**
     * 触发人，枚举值：creator/modifier/specific
     */
    @Column(name = "trigger_user_type", length = 32)
    private String triggerUserType;
    /**
     * 触发人具体值
     */
    @Column(name = "trigger_user_value", length = 256)
    private String triggerUserValue;
    /**
     * 页面Id
     */
    @Column(name = "page_id", length = 19)
    private Long pageId;
    /**
     * 字段Id
     */
    @Column(name = "field_id", length = 19)
    private Long fieldId;
    /**
     * 数据过滤条件，金额 > 50000 且 客户等级 = 'VIP
     */
    @Column(name = "filter_condition", length = 2147483647)
    private String filterCondition;
    /**
     * 数据范围，全部数据、变更数据
     */
    @Column(name = "data_scope", length = 64)
    private String dataScope;
    /**
     * 是否允许关联子表触发 0/1布尔值
     */
    @Column(name = "is_child_trigger_allowed", length = 5)
    private Integer isChildTriggerAllowed;


}