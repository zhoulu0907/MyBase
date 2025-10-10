package com.cmsr.onebase.module.bpm.core.warmflow.anyline.dataobject;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * WarmFlow 待办任务 DO，对应表 flow_task。
 *
 * @author liyang
 * @date 2025-01-27
 */
@Data
@Accessors(chain = true)
@Table(name = "flow_task")
public class BpmFlowTaskDO extends BpmWarmFlowBaseDO {

    /** 对应flow_definition表的id */
    @Column(name = "definition_id", nullable = false)
    private Long definitionId;

    /** 对应flow_instance表的id */
    @Column(name = "instance_id", nullable = false)
    private Long instanceId;

    /** 节点编码 */
    @Column(name = "node_code", length = 100, nullable = false)
    private String nodeCode;

    /** 节点名称 */
    @Column(name = "node_name", length = 100)
    private String nodeName;

    /** 节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关） */
    @Column(name = "node_type", nullable = false)
    private Integer nodeType;

    /** 流程状态（0待提交 1审批中 2审批通过 4终止 5作废 6撤销 8已完成 9已退回 10失效 11拿回） */
    @Column(name = "flow_status", length = 20, nullable = false)
    private String flowStatus;

    /** 审批表单是否自定义（Y是 N否） */
    @Column(name = "form_custom", length = 1)
    private String formCustom;

    /** 审批表单路径 */
    @Column(name = "form_path", length = 100)
    private String formPath;

    /** 业务id */
    @Column(name = "business_id", length = 40)
    private String businessId;
}