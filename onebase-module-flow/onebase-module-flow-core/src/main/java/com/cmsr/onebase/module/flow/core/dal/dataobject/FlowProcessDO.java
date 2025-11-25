package com.cmsr.onebase.module.flow.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.data.BaseAppEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;

@Data
@Table(value = "flow_process")
public class FlowProcessDO extends BaseAppEntity {
    /**
     * 主键ID
     */
    @Column(value = "id")
    private Long id;
    /**
     * 应用ID
     */
    @Column(value = "application_id")
    private Long applicationId;
    /**
     * 流程名称
     */
    @Column(value = "process_name")
    private String processName;
    /**
     * 描述
     */
    @Column(value = "process_description")
    private String processDescription;
    /**
     * 流程json
     */
    @Column(value = "process_definition")
    private String processDefinition;
    /**
     * 启用状态（编辑、发布）
     */
    @Column(value = "enable_status")
    private Integer enableStatus;
    /**
     * 发布状态（编辑、发布）
     */
    @Column(value = "publish_status")
    private Integer publishStatus;
    /**
     * 流程类型，如表达触发，定时触发，API触发等
     */
    @Column(value = "trigger_type")
    private String triggerType;

}