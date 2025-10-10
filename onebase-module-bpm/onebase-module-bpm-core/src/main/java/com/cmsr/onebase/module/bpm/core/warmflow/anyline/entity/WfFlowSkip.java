package com.cmsr.onebase.module.bpm.core.warmflow.anyline.entity;

import com.cmsr.onebase.module.bpm.core.warmflow.anyline.dataobject.BpmFlowSkipDO;
import lombok.Data;
import org.dromara.warm.flow.core.entity.Skip;

import java.util.Date;

/**
 * WarmFlow 节点跳转适配器
 * 使用组合模式承载 {@link BpmFlowSkipDO}
 */
@Data
public class WfFlowSkip implements Skip {

    private BpmFlowSkipDO flowSkipDO;

    private BpmFlowSkipDO ensure() {
        if (flowSkipDO == null) {
            flowSkipDO = new BpmFlowSkipDO();
        }
        return flowSkipDO;
    }

    private Skip chain(java.util.function.Consumer<BpmFlowSkipDO> f) {
        f.accept(ensure());
        return this;
    }

    @Override
    public Long getId() {
        return flowSkipDO == null ? null : flowSkipDO.getId();
    }

    @Override
    public Skip setId(Long id) {
        return chain(d -> d.setId(id));
    }

    @Override
    public Date getCreateTime() {
        return flowSkipDO == null ? null : (flowSkipDO.getCreateTime() == null ? null : java.util.Date.from(flowSkipDO.getCreateTime().atZone(java.time.ZoneId.systemDefault()).toInstant()));
    }

    @Override
    public Skip setCreateTime(Date createTime) {
        return chain(d -> d.setCreateTime(createTime == null ? null :
                java.time.LocalDateTime.ofInstant(createTime.toInstant(), java.time.ZoneId.systemDefault())));
    }

    @Override
    public Date getUpdateTime() {
        return flowSkipDO == null ? null : (flowSkipDO.getUpdateTime() == null ? null : java.util.Date.from(flowSkipDO.getUpdateTime().atZone(java.time.ZoneId.systemDefault()).toInstant()));
    }

    @Override
    public Skip setUpdateTime(Date updateTime) {
        return chain(d -> d.setUpdateTime(updateTime == null ? null :
                java.time.LocalDateTime.ofInstant(updateTime.toInstant(), java.time.ZoneId.systemDefault())));
    }

    @Override
    public String getTenantId() {
        return flowSkipDO == null ? null : flowSkipDO.getTenantId();
    }

    @Override
    public Skip setTenantId(String tenantId) {
        return chain(d -> d.setTenantId(tenantId));
    }

    @Override
    public String getDelFlag() {
        return flowSkipDO == null ? null : String.valueOf(flowSkipDO.getDeleted());
    }

    @Override
    public Skip setDelFlag(String delFlag) {
        return chain(d -> d.setDeleted(Long.valueOf(delFlag)));
    }

    @Override
    public Long getDefinitionId() {
        return flowSkipDO == null ? null : flowSkipDO.getDefinitionId();
    }

    @Override
    public Skip setDefinitionId(Long definitionId) {
        return chain(d -> d.setDefinitionId(definitionId));
    }

    @Override
    public Long getNodeId() {
        if (flowSkipDO != null) {
            return flowSkipDO.getNodeId();
        }
        return null;
    }

    @Override
    public Skip setNodeId(Long nodeId) {
        return chain(d -> d.setNodeId(nodeId));
    }

    @Override
    public String getNowNodeCode() {
        return flowSkipDO == null ? null : flowSkipDO.getNowNodeCode();
    }

    @Override
    public Skip setNowNodeCode(String nowNodeCode) {
        return chain(d -> d.setNowNodeCode(nowNodeCode));
    }

    @Override
    public Integer getNowNodeType() {
        return flowSkipDO == null ? null : flowSkipDO.getNowNodeType();
    }

    @Override
    public Skip setNowNodeType(Integer nowNodeType) {
        return chain(d -> d.setNowNodeType(nowNodeType));
    }

    @Override
    public String getNextNodeCode() {
        return flowSkipDO == null ? null : flowSkipDO.getNextNodeCode();
    }

    @Override
    public Skip setNextNodeCode(String nextNodeCode) {
        return chain(d -> d.setNextNodeCode(nextNodeCode));
    }

    @Override
    public Integer getNextNodeType() {
        return flowSkipDO == null ? null : flowSkipDO.getNextNodeType();
    }

    @Override
    public Skip setNextNodeType(Integer nextNodeType) {
        return chain(d -> d.setNextNodeType(nextNodeType));
    }

    @Override
    public String getSkipName() {
        return flowSkipDO == null ? null : flowSkipDO.getSkipName();
    }

    @Override
    public Skip setSkipName(String skipName) {
        return chain(d -> d.setSkipName(skipName));
    }

    @Override
    public String getSkipType() {
        return flowSkipDO == null ? null : flowSkipDO.getSkipType();
    }

    @Override
    public Skip setSkipType(String skipType) {
        return chain(d -> d.setSkipType(skipType));
    }

    @Override
    public String getSkipCondition() {
        return flowSkipDO == null ? null : flowSkipDO.getSkipCondition();
    }

    @Override
    public Skip setSkipCondition(String skipCondition) {
        return chain(d -> d.setSkipCondition(skipCondition));
    }

    @Override
    public String getCoordinate() {
        return flowSkipDO == null ? null : flowSkipDO.getCoordinate();
    }

    @Override
    public Skip setCoordinate(String coordinate) {
        return chain(d -> d.setCoordinate(coordinate));
    }
}


