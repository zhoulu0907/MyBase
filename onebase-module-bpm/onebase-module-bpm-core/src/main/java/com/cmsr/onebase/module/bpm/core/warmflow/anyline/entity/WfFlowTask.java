package com.cmsr.onebase.module.bpm.core.warmflow.anyline.entity;

import com.cmsr.onebase.module.bpm.core.warmflow.anyline.dataobject.BpmFlowTaskDO;
import lombok.Data;
import org.dromara.warm.flow.core.entity.Task;
import org.dromara.warm.flow.core.entity.User;

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
public class WfFlowTask extends BpmFlowTaskDO {

//    private BpmFlowTaskDO flowTaskDO;
//
//    private BpmFlowTaskDO ensure() {
//        if (flowTaskDO == null) {
//            flowTaskDO = new BpmFlowTaskDO();
//        }
//        return flowTaskDO;
//    }
//
//    private Task chain(java.util.function.Consumer<BpmFlowTaskDO> f) {
//        f.accept(ensure());
//        return this;
//    }
//
//
//    @Override
//    public Long getId() {
//        return flowTaskDO == null ? null : flowTaskDO.getId();
//    }
//
//    @Override
//    public Task setId(Long id) {
//        return chain(d -> d.setId(id));
//    }
//
//    @Override
//    public Date getCreateTime() {
//        if (flowTaskDO == null || flowTaskDO.getCreateTime() == null) {
//            return null;
//        }
//        return java.util.Date.from(flowTaskDO.getCreateTime().atZone(java.time.ZoneId.systemDefault()).toInstant());
//    }
//
//    @Override
//    public Task setCreateTime(Date createTime) {
//        return chain(d -> d.setCreateTime(createTime == null ? null :
//                java.time.LocalDateTime.ofInstant(createTime.toInstant(), java.time.ZoneId.systemDefault())));
//    }
//
//    @Override
//    public Date getUpdateTime() {
//        if (flowTaskDO == null || flowTaskDO.getUpdateTime() == null) {
//            return null;
//        }
//        return java.util.Date.from(flowTaskDO.getUpdateTime().atZone(java.time.ZoneId.systemDefault()).toInstant());
//    }
//
//    @Override
//    public Task setUpdateTime(Date updateTime) {
//        return chain(d -> d.setUpdateTime(updateTime == null ? null :
//                java.time.LocalDateTime.ofInstant(updateTime.toInstant(), java.time.ZoneId.systemDefault())));
//    }
//
//    @Override
//    public String getTenantId() {
//        return flowTaskDO == null ? null : flowTaskDO.getTenantId();
//    }
//
//    @Override
//    public Task setTenantId(String tenantId) {
//        return chain(d -> d.setTenantId(tenantId));
//    }
//
//    @Override
//    public String getDelFlag() {
//        return flowTaskDO == null ? null : (flowTaskDO.getDeleted() == null ? null : flowTaskDO.getDeleted().toString());
//    }
//
//    @Override
//    public Task setDelFlag(String delFlag) {
//        return chain(d -> d.setDeleted(delFlag == null ? null : Long.valueOf(delFlag)));
//    }
//
//    @Override
//    public Long getDefinitionId() {
//        return flowTaskDO == null ? null : flowTaskDO.getDefinitionId();
//    }
//
//    @Override
//    public Task setDefinitionId(Long definitionId) {
//        return chain(d -> d.setDefinitionId(definitionId));
//    }
//
//    @Override
//    public Long getInstanceId() {
//        return flowTaskDO == null ? null : flowTaskDO.getInstanceId();
//    }
//
//    @Override
//    public Task setInstanceId(Long instanceId) {
//        return chain(d -> d.setInstanceId(instanceId));
//    }
//
//    @Override
//    public String getFlowName() {
//        return flowTaskDO == null ? null : flowTaskDO.getNodeName();
//    }
//
//    @Override
//    public Task setFlowName(String flowName) {
//        return chain(d -> d.setNodeName(flowName));
//    }
//
//    @Override
//    public String getBusinessId() {
//        return flowTaskDO == null ? null : flowTaskDO.getBusinessId();
//    }
//
//    @Override
//    public Task setBusinessId(String businessId) {
//        return chain(d -> d.setBusinessId(businessId));
//    }
//
//    @Override
//    public String getNodeCode() {
//        return flowTaskDO == null ? null : flowTaskDO.getNodeCode();
//    }
//
//    @Override
//    public Task setNodeCode(String nodeCode) {
//        return chain(d -> d.setNodeCode(nodeCode));
//    }
//
//    @Override
//    public String getNodeName() {
//        return flowTaskDO == null ? null : flowTaskDO.getNodeName();
//    }
//
//    @Override
//    public Task setNodeName(String nodeName) {
//        return chain(d -> d.setNodeName(nodeName));
//    }
//
//    @Override
//    public Integer getNodeType() {
//        return flowTaskDO == null ? null : flowTaskDO.getNodeType();
//    }
//
//    @Override
//    public Task setNodeType(Integer nodeType) {
//        return chain(d -> d.setNodeType(nodeType));
//    }
//
//    @Override
//    public String getFlowStatus() {
//        return flowTaskDO == null ? null : flowTaskDO.getFlowStatus();
//    }
//
//    @Override
//    public Task setFlowStatus(String flowStatus) {
//        return chain(d -> d.setFlowStatus(flowStatus));
//    }
//
//    @Override
//    public List<String> getPermissionList() {
//        return null; // 可按需实现映射
//    }
//
//    @Override
//    public Task setPermissionList(List<String> permissionList) {
//        return this; // 可按需实现映射
//    }
//
//    @Override
//    public List<User> getUserList() {
//        return null; // 可按需实现映射
//    }
//
//    @Override
//    public Task setUserList(List<User> userList) {
//        return this; // 可按需实现映射
//    }
//
//    @Override
//    public String getFormCustom() {
//        return flowTaskDO == null ? null : flowTaskDO.getFormCustom();
//    }
//
//    @Override
//    public Task setFormCustom(String formCustom) {
//        if (flowTaskDO == null) {
//            flowTaskDO = new BpmFlowTaskDO();
//        }
//        flowTaskDO.setFormCustom(formCustom);
//        return this;
//    }
//
//    @Override
//    public String getFormPath() {
//        return flowTaskDO == null ? null : flowTaskDO.getFormPath();
//    }
//
//    @Override
//    public Task setFormPath(String formPath) {
//        if (flowTaskDO == null) {
//            flowTaskDO = new BpmFlowTaskDO();
//        }
//        flowTaskDO.setFormPath(formPath);
//        return this;
//    }
}
