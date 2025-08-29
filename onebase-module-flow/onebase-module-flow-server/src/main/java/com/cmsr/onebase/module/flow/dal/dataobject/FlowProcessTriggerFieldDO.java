package com.cmsr.onebase.module.flow.dal.dataobject;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "flow_process_trigger_field")
public class FlowProcessTriggerFieldDO extends TenantBaseDO {

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
     * 节点ID (对应表单"ID")
     */
    @Column(name = "node_id", length = 64, nullable = false)
    private String nodeId;
    /**
     * 数据源ID
     */
    @Column(name = "datasource_id", length = 19)
    private Long datasourceId;
    /**
     * 触发实体ID
     */
    @Column(name = "entity_id", length = 19)
    private Long entityId;
    /**
     * 触发字段ID
     */
    @Column(name = "field_id", length = 19)
    private Long fieldId;
    /**
     * 基准日期，前还是后
     */
    @Column(name = "offset_direction", length = 64)
    private String offsetDirection;
    /**
     * 偏移量，如30 1
     */
    @Column(name = "offset_value", length = 19)
    private Long offsetValue;
    /**
     * 偏移单位，天、月、年
     */
    @Column(name = "offset_unit", length = 64)
    private String offsetUnit;
    /**
     * 每日触发时间点，当天具体触发时间
     */
    @Column(name = "daily_exec_time", length = 64)
    private String dailyExecTime;
    /**
     * 数据过滤条件，金额 > 50000 且 客户等级 = 'VIP
     */
    @Column(name = "filter_condition", length = 2147483647)
    private String filterCondition;
    /**
     * 批量处理模式：single(逐条触发) / batch(批量触发)
     */
    @Column(name = "batch_mode", length = 32)
    private String batchMode;
    /**
     * 批量处理大小（每批最大记录数）
     */
    @Column(name = "batch_size", length = 10)
    private Integer batchSize;
    /**
     * 执行超时时间（秒）
     */
    @Column(name = "exec_timeout_seconds", length = 10)
    private Integer execTimeoutSeconds;
    /**
     * 数据携带范围：full(全量字段) / specified(指定字段) / related(关联表数据)
     */
    @Column(name = "data_carry_scope", length = 32)
    private String dataCarryScope;
    /**
     * 指定字段列表（JSON格式，当data_carry_scope为specified时使用）
     */
    @Column(name = "data_carry_fields", length = 2147483647)
    private String dataCarryFields;

}