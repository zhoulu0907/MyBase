package com.cmsr.onebase.module.bpm.core.warmflow.anyline.dataobject;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.warm.flow.core.entity.Instance;

import java.util.Date;

/**
 * WarmFlow 流程实例 DO，对应表 flow_instance。
 *
 * @author liyang
 * @date 2025-01-27
 */
@Data
@Accessors(chain = true)
@Table(name = "flow_instance")
public class BpmFlowInstanceDO extends BpmWarmFlowBaseDO implements Instance {

    /** 对应flow_definition表的id */
    @Column(name = "definition_id", nullable = false)
    private Long definitionId;

    /** 业务id */
    @Column(name = "business_id", length = 40, nullable = false)
    private String businessId;

    /** 节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关） */
    @Column(name = "node_type", nullable = false)
    private Integer nodeType;

    /** 流程节点编码 */
    @Column(name = "node_code", length = 40, nullable = false)
    private String nodeCode;

    /** 流程节点名称 */
    @Column(name = "node_name", length = 100)
    private String nodeName;

    /** 任务变量 */
    @Column(name = "variable", columnDefinition = "TEXT")
    private String variable;

    /** 流程状态（0待提交 1审批中 2审批通过 4终止 5作废 6撤销 8已完成 9已退回 10失效 11拿回） */
    @Column(name = "flow_status", length = 20, nullable = false)
    private String flowStatus;

    /** 流程激活状态（0挂起 1激活） */
    @Column(name = "activity_status", nullable = false)
    private Integer activityStatus;

    /** 流程定义json */
    @Column(name = "def_json", columnDefinition = "TEXT")
    private String defJson;

    /** 扩展字段，预留给业务系统使用 */
    @Column(name = "ext", length = 500)
    private String ext;

    /** 审批表单是否自定义（Y是 N否） */
    @Column(name = "form_custom", length = 1)
    private String formCustom;

    /** 审批表单路径 */
    @Column(name = "form_path", length = 100)
    private String formPath;


    /* ==================== 以下为 Instance 接口方法实现 ==================== */

    @Override
    public Instance setId(Long id) {
        return this;
    }

    @Override
    public Instance setCreateTime(Date createTime) {
        return null;
    }

    @Override
    public Instance setUpdateTime(Date updateTime) {
        return null;
    }

    @Override
    public Instance setTenantId(String tenantId) {
        return null;
    }

    @Override
    public Instance setDelFlag(String delFlag) {
        return null;
    }

    @Override
    public String getFlowName() {
        return null;
    }

    @Override
    public Instance setFlowName(String flowName) {
        return null;
    }

    @Override
    public Instance setCreateBy(String createBy) {
        return null;
    }
}