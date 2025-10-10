package com.cmsr.onebase.module.bpm.core.warmflow.anyline.entity;

import com.cmsr.onebase.module.bpm.core.warmflow.anyline.dataobject.BpmFlowHisTaskDO;
import lombok.Data;
import lombok.experimental.Delegate;
import org.dromara.warm.flow.core.entity.HisTask;

import java.util.Date;
import java.util.List;

/**
 * WarmFlow 待办任务适配器
 * 实现 Task 接口，使用组合模式
 *
 * @author liyang
 * @date 2025-01-27
 */
@Data
public class WfFlowHisTask implements HisTask {

    private BpmFlowHisTaskDO flowHisTaskDO;

    // 懒创建，避免 NPE
    private BpmFlowHisTaskDO ensure() {
        if (flowHisTaskDO == null) {
            flowHisTaskDO = new BpmFlowHisTaskDO();
        }
        return flowHisTaskDO;
    }

    // 统一链式助手
    private HisTask chain(java.util.function.Consumer<BpmFlowHisTaskDO> f) {
        f.accept(ensure());
        return this;
    }


    @Override
    public Long getId() {
        return flowHisTaskDO == null ? null : flowHisTaskDO.getId();
    }

    @Override
    public HisTask setId(Long id) {
        return chain(d -> d.setId(id));
    }

    @Override
    public Date getCreateTime() {
        if (flowHisTaskDO == null || flowHisTaskDO.getCreateTime() == null) {
            return null;
        }
        return java.util.Date.from(flowHisTaskDO.getCreateTime().atZone(java.time.ZoneId.systemDefault()).toInstant());
    }

    @Override
    public HisTask setCreateTime(Date createTime) {
        return chain(d -> d.setCreateTime(createTime == null ? null :
                java.time.LocalDateTime.ofInstant(createTime.toInstant(), java.time.ZoneId.systemDefault())));
    }

    @Override
    public Date getUpdateTime() {
        if (flowHisTaskDO == null || flowHisTaskDO.getUpdateTime() == null) {
            return null;
        }
        return java.util.Date.from(flowHisTaskDO.getUpdateTime().atZone(java.time.ZoneId.systemDefault()).toInstant());
    }

    @Override
    public HisTask setUpdateTime(Date updateTime) {
        return chain(d -> d.setUpdateTime(updateTime == null ? null :
                java.time.LocalDateTime.ofInstant(updateTime.toInstant(), java.time.ZoneId.systemDefault())));
    }

    @Override
    public String getTenantId() {
        return flowHisTaskDO == null ? null : flowHisTaskDO.getTenantId();
    }

    @Override
    public HisTask setTenantId(String tenantId) {
        return chain(d -> d.setTenantId(tenantId));
    }

    @Override
    public String getDelFlag() {
        return flowHisTaskDO == null ? null : (flowHisTaskDO.getDeleted() == null ? null : flowHisTaskDO.getDeleted().toString());
    }

    @Override
    public HisTask setDelFlag(String delFlag) {
        return chain(d -> d.setDeleted(delFlag == null ? null : Long.valueOf(delFlag)));
    }

    @Override
    public Long getDefinitionId() {
        return flowHisTaskDO == null ? null : flowHisTaskDO.getDefinitionId();
    }

    @Override
    public HisTask setDefinitionId(Long definitionId) {
        return chain(d -> d.setDefinitionId(definitionId));
    }

    @Override
    public String getFlowName() {
        return flowHisTaskDO == null ? null : flowHisTaskDO.getFlowName();
    }

    @Override
    public HisTask setFlowName(String flowName) {
        return chain(d -> d.setFlowName(flowName));
    }

    @Override
    public Long getInstanceId() {
        return flowHisTaskDO == null ? null : flowHisTaskDO.getInstanceId();
    }

    @Override
    public HisTask setInstanceId(Long instanceId) {
        return chain(d -> d.setInstanceId(instanceId));
    }

    @Override
    public Integer getCooperateType() {
        return flowHisTaskDO == null ? null : flowHisTaskDO.getCooperateType();
    }

    @Override
    public HisTask setCooperateType(Integer cooperateType) {
        return chain(d -> d.setCooperateType(cooperateType));
    }

    @Override
    public Long getTaskId() {
        return flowHisTaskDO == null ? null : flowHisTaskDO.getTaskId();
    }

    @Override
    public HisTask setTaskId(Long taskId) {
        return chain(d -> d.setTaskId(taskId));
    }

    @Override
    public String getBusinessId() {
        return flowHisTaskDO == null ? null : flowHisTaskDO.getBusinessId();
    }

    @Override
    public HisTask setBusinessId(String businessId) {
        return chain(d -> d.setBusinessId(businessId));
    }

    @Override
    public String getNodeCode() {
        return flowHisTaskDO == null ? null : flowHisTaskDO.getNodeCode();
    }

    @Override
    public HisTask setNodeCode(String nodeCode) {
        return chain(d -> d.setNodeCode(nodeCode));
    }

    @Override
    public String getNodeName() {
        return flowHisTaskDO == null ? null : flowHisTaskDO.getNodeName();
    }

    @Override
    public HisTask setNodeName(String nodeName) {
        return chain(d -> d.setNodeName(nodeName));
    }

    @Override
    public Integer getNodeType() {
        return flowHisTaskDO == null ? null : flowHisTaskDO.getNodeType();
    }

    @Override
    public HisTask setNodeType(Integer nodeType) {
        return chain(d -> d.setNodeType(nodeType));
    }

    @Override
    public String getTargetNodeCode() {
        return flowHisTaskDO == null ? null : flowHisTaskDO.getTargetNodeCode();
    }

    @Override
    public HisTask setTargetNodeCode(String targetNodeCode) {
        return chain(d -> d.setTargetNodeCode(targetNodeCode));
    }

    @Override
    public String getTargetNodeName() {
        return flowHisTaskDO == null ? null : flowHisTaskDO.getTargetNodeName();
    }

    @Override
    public HisTask setTargetNodeName(String targetNodeName) {
        return chain(d -> d.setTargetNodeName(targetNodeName));
    }

    @Override
    public String getApprover() {
        return flowHisTaskDO == null ? null : flowHisTaskDO.getApprover();
    }

    @Override
    public HisTask setApprover(String approver) {
        return chain(d -> d.setApprover(approver));
    }

    @Override
    public String getCollaborator() {
        return flowHisTaskDO == null ? null : flowHisTaskDO.getCollaborator();
    }

    @Override
    public HisTask setCollaborator(String collaborator) {
        return chain(d -> d.setCollaborator(collaborator));
    }

    @Override
    public List<String> getPermissionList() {
        return flowHisTaskDO == null ? null : flowHisTaskDO.getPermissionList();
    }

    @Override
    public HisTask setPermissionList(List<String> permissionList) {
        return chain(d -> d.setPermissionList(permissionList));
    }

    @Override
    public String getSkipType() {
        return flowHisTaskDO == null ? null : flowHisTaskDO.getSkipType();
    }

    @Override
    public HisTask setSkipType(String skipType) {
        return chain(d -> d.setSkipType(skipType));
    }

    @Override
    public String getFlowStatus() {
        return flowHisTaskDO == null ? null : flowHisTaskDO.getFlowStatus();
    }

    @Override
    public HisTask setFlowStatus(String flowStatus) {
        return chain(d -> d.setFlowStatus(flowStatus));
    }

    @Override
    public String getMessage() {
        return flowHisTaskDO == null ? null : flowHisTaskDO.getMessage();
    }

    @Override
    public HisTask setMessage(String message) {
        return chain(d -> d.setMessage(message));
    }

    @Override
    public String getVariable() {
        return flowHisTaskDO == null ? null : flowHisTaskDO.getVariable();
    }

    @Override
    public HisTask setVariable(String variable) {
        return chain(d -> d.setVariable(variable));
    }

    @Override
    public String getExt() {
        return flowHisTaskDO == null ? null : flowHisTaskDO.getExt();
    }

    @Override
    public HisTask setExt(String ext) {
        return chain(d -> d.setExt(ext));
    }

    @Override
    public String getCreateBy() {
        return flowHisTaskDO == null ? null : (flowHisTaskDO.getCreator() == null ? null : flowHisTaskDO.getCreator().toString());
    }

    @Override
    public HisTask setCreateBy(String createBy) {
        return chain(d -> d.setCreator(createBy == null ? null : Long.valueOf(createBy)));
    }

    @Override
    public String getFormCustom() {
        return flowHisTaskDO == null ? null : flowHisTaskDO.getFormCustom();
    }

    @Override
    public HisTask setFormCustom(String formCustom) {
        return chain(d -> d.setFormCustom(formCustom));
    }

    @Override
    public String getFormPath() {
        return flowHisTaskDO == null ? null : flowHisTaskDO.getFormPath();
    }

    @Override
    public HisTask setFormPath(String formPath) {
        return chain(d -> d.setFormPath(formPath));
    }
}
