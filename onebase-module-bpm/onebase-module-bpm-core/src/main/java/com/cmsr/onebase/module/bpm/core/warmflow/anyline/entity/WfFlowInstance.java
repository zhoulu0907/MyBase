package com.cmsr.onebase.module.bpm.core.warmflow.anyline.entity;

import com.cmsr.onebase.module.bpm.core.warmflow.anyline.dataobject.BpmFlowInstanceDO;
import lombok.Data;
import org.dromara.warm.flow.core.entity.Instance;

import java.util.Date;

/**
 * WarmFlow 流程实例适配器
 * 实现 Instance 接口，使用组合模式
 *
 * @author liyang
 * @date 2025-01-27
 */
@Data
public class WfFlowInstance extends BpmFlowInstanceDO {

//    private BpmFlowInstanceDO flowInstanceDO;
//
//    private BpmFlowInstanceDO ensure() {
//        if (flowInstanceDO == null) {
//            flowInstanceDO = new BpmFlowInstanceDO();
//        }
//        return flowInstanceDO;
//    }
//
//    private Instance chain(java.util.function.Consumer<BpmFlowInstanceDO> f) {
//        f.accept(ensure());
//        return this;
//    }
//
//
//    @Override
//    public Long getId() {
//        return flowInstanceDO == null ? null : flowInstanceDO.getId();
//    }
//
//    @Override
//    public Instance setId(Long id) {
//        return chain(d -> d.setId(id));
//    }
//
//    @Override
//    public Date getCreateTime() {
//        if (flowInstanceDO == null || flowInstanceDO.getCreateTime() == null) {
//            return null;
//        }
//        return java.util.Date.from(flowInstanceDO.getCreateTime().atZone(java.time.ZoneId.systemDefault()).toInstant());
//    }
//
//    @Override
//    public Instance setCreateTime(Date createTime) {
//        return chain(d -> d.setCreateTime(createTime == null ? null :
//                java.time.LocalDateTime.ofInstant(createTime.toInstant(), java.time.ZoneId.systemDefault())));
//    }
//
//    @Override
//    public Date getUpdateTime() {
//        if (flowInstanceDO == null || flowInstanceDO.getUpdateTime() == null) {
//            return null;
//        }
//        return java.util.Date.from(flowInstanceDO.getUpdateTime().atZone(java.time.ZoneId.systemDefault()).toInstant());
//    }
//
//    @Override
//    public Instance setUpdateTime(Date updateTime) {
//        return chain(d -> d.setUpdateTime(updateTime == null ? null :
//                java.time.LocalDateTime.ofInstant(updateTime.toInstant(), java.time.ZoneId.systemDefault())));
//    }
//
//    @Override
//    public String getTenantId() {
//        return flowInstanceDO == null ? null : flowInstanceDO.getTenantId();
//    }
//
//    @Override
//    public Instance setTenantId(String tenantId) {
//        return chain(d -> d.setTenantId(tenantId));
//    }
//
//    @Override
//    public String getDelFlag() {
//        return flowInstanceDO == null ? null : String.valueOf(flowInstanceDO.getDeleted());
//    }
//
//    @Override
//    public Instance setDelFlag(String delFlag) {
//        return chain(d -> d.setDeleted(Long.valueOf(delFlag)));
//    }
//
//    @Override
//    public Long getDefinitionId() {
//        return flowInstanceDO == null ? null : flowInstanceDO.getDefinitionId();
//    }
//
//    @Override
//    public Instance setDefinitionId(Long definitionId) {
//        return chain(d -> d.setDefinitionId(definitionId));
//    }
//
//    @Override
//    public String getFlowName() {
//        return flowInstanceDO == null ? null : flowInstanceDO.getNodeName();
//    }
//
//    @Override
//    public Instance setFlowName(String flowName) {
//        return chain(d -> d.setNodeName(flowName));
//    }
//
//    @Override
//    public String getBusinessId() {
//        return flowInstanceDO == null ? null : flowInstanceDO.getBusinessId();
//    }
//
//    @Override
//    public Instance setBusinessId(String businessId) {
//        return chain(d -> d.setBusinessId(businessId));
//    }
//
//    @Override
//    public Integer getNodeType() {
//        return flowInstanceDO == null ? null : flowInstanceDO.getNodeType();
//    }
//
//    @Override
//    public Instance setNodeType(Integer nodeType) {
//        return chain(d -> d.setNodeType(nodeType));
//    }
//
//    @Override
//    public String getNodeCode() {
//        return flowInstanceDO == null ? null : flowInstanceDO.getNodeCode();
//    }
//
//    @Override
//    public Instance setNodeCode(String nodeCode) {
//        return chain(d -> d.setNodeCode(nodeCode));
//    }
//
//    @Override
//    public String getNodeName() {
//        return flowInstanceDO == null ? null : flowInstanceDO.getNodeName();
//    }
//
//    @Override
//    public Instance setNodeName(String nodeName) {
//        return chain(d -> d.setNodeName(nodeName));
//    }
//
//    @Override
//    public String getVariable() {
//        return flowInstanceDO == null ? null : flowInstanceDO.getVariable();
//    }
//
//    @Override
//    public Instance setVariable(String variable) {
//        return chain(d -> d.setVariable(variable));
//    }
//
//    @Override
//    public String getFlowStatus() {
//        return flowInstanceDO == null ? null : flowInstanceDO.getFlowStatus();
//    }
//
//    @Override
//    public Instance setFlowStatus(String flowStatus) {
//        return chain(d -> d.setFlowStatus(flowStatus));
//    }
//
//    @Override
//    public String getCreateBy() {
//        return flowInstanceDO == null ? null : String.valueOf(flowInstanceDO.getCreator());
//    }
//
//    @Override
//    public Instance setCreateBy(String createBy) {
//        return chain(d -> d.setCreator(Long.valueOf(createBy)));
//    }
//
//    @Override
//    public String getFormCustom() {
//        return flowInstanceDO == null ? null : flowInstanceDO.getFormCustom();
//    }
//
//    @Override
//    public Instance setFormCustom(String formCustom) {
//        return chain(d -> d.setFormCustom(formCustom));
//    }
//
//    @Override
//    public String getFormPath() {
//        return flowInstanceDO == null ? null : flowInstanceDO.getFormPath();
//    }
//
//    @Override
//    public Instance setFormPath(String formPath) {
//        return chain(d -> d.setFormPath(formPath));
//    }
//
//    @Override
//    public String getDefJson() {
//        return flowInstanceDO == null ? null : flowInstanceDO.getDefJson();
//    }
//
//    @Override
//    public Instance setDefJson(String defJson) {
//        return chain(d -> d.setDefJson(defJson));
//    }
//
//    @Override
//    public String getExt() {
//        return flowInstanceDO == null ? null : flowInstanceDO.getExt();
//    }
//
//    @Override
//    public Instance setExt(String ext) {
//        return chain(d -> d.setExt(ext));
//    }
//
//    @Override
//    public Integer getActivityStatus() {
//        return flowInstanceDO == null ? null : flowInstanceDO.getActivityStatus();
//    }
//
//    @Override
//    public Instance setActivityStatus(Integer activityStatus) {
//        return chain(d -> d.setActivityStatus(activityStatus));
//    }
}
