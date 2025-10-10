/*
 *    Copyright 2024-2025, Warm-Flow (290631660@qq.com).
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.cmsr.onebase.module.bpm.core.warmflow.anyline.entity;

import com.cmsr.onebase.module.bpm.core.warmflow.anyline.dataobject.BpmFlowNodeDO;
import lombok.Data;
import org.dromara.warm.flow.core.entity.Node;
import org.dromara.warm.flow.core.entity.Skip;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 流程定义对象 flow_definition
 * 同时继承 BaseDO 和实现 Definition 接口
 * 使用字段隐藏解决类型冲突
 *
 * @author warm
 * @since 2023-03-29
 */
@Data
public class WfFlowNode implements Node {
    private BpmFlowNodeDO flowNodeDO;

    private BpmFlowNodeDO ensure() {
        if (flowNodeDO == null) {
            flowNodeDO = new BpmFlowNodeDO();
        }
        return flowNodeDO;
    }

    private Node chain(java.util.function.Consumer<BpmFlowNodeDO> f) {
        f.accept(ensure());
        return this;
    }


    @Override
    public Long getId() {
        return flowNodeDO == null ? null : flowNodeDO.getId();
    }

    @Override
    public Node setId(Long id) {
        return chain(d -> d.setId(id));
    }

    @Override
    public Date getCreateTime() {
        if (flowNodeDO == null || flowNodeDO.getCreateTime() == null) {
            return null;
        }
        return java.util.Date.from(flowNodeDO.getCreateTime().atZone(java.time.ZoneId.systemDefault()).toInstant());
    }

    @Override
    public Node setCreateTime(Date createTime) {
        return chain(d -> d.setCreateTime(createTime == null ? null :
                java.time.LocalDateTime.ofInstant(createTime.toInstant(), java.time.ZoneId.systemDefault())));
    }

    @Override
    public Date getUpdateTime() {
        if (flowNodeDO == null || flowNodeDO.getUpdateTime() == null) {
            return null;
        }
        return java.util.Date.from(flowNodeDO.getUpdateTime().atZone(java.time.ZoneId.systemDefault()).toInstant());
    }

    @Override
    public Node setUpdateTime(Date updateTime) {
        return chain(d -> d.setUpdateTime(updateTime == null ? null :
                java.time.LocalDateTime.ofInstant(updateTime.toInstant(), java.time.ZoneId.systemDefault())));
    }

    @Override
    public String getTenantId() {
        return flowNodeDO == null ? null : flowNodeDO.getTenantId();
    }

    @Override
    public Node setTenantId(String tenantId) {
        return chain(d -> d.setTenantId(tenantId));
    }

    @Override
    public String getDelFlag() {
        return flowNodeDO == null ? null : String.valueOf(flowNodeDO.getDeleted());
    }

    @Override
    public Node setDelFlag(String delFlag) {
        return chain(d -> d.setDeleted(Long.valueOf(delFlag)));
    }

    @Override
    public Integer getNodeType() {
        return flowNodeDO == null ? null : flowNodeDO.getNodeType();
    }

    @Override
    public Node setNodeType(Integer nodeType) {
        return chain(d -> d.setNodeType(nodeType));
    }

    @Override
    public Long getDefinitionId() {
        return flowNodeDO == null ? null : flowNodeDO.getDefinitionId();
    }

    @Override
    public Node setDefinitionId(Long definitionId) {
        return chain(d -> d.setDefinitionId(definitionId));
    }

    @Override
    public String getNodeCode() {
        return flowNodeDO == null ? null : flowNodeDO.getNodeCode();
    }

    @Override
    public Node setNodeCode(String nodeCode) {
        return chain(d -> d.setNodeCode(nodeCode));
    }

    @Override
    public String getNodeName() {
        return flowNodeDO == null ? null : flowNodeDO.getNodeName();
    }

    @Override
    public Node setNodeName(String nodeName) {
        return chain(d -> d.setNodeName(nodeName));
    }

    @Override
    public BigDecimal getNodeRatio() {
        return flowNodeDO == null ? null : flowNodeDO.getNodeRatio();
    }

    @Override
    public Node setNodeRatio(BigDecimal nodeRatio) {
        return chain(d -> d.setNodeRatio(nodeRatio));
    }

    @Override
    public String getPermissionFlag() {
        return flowNodeDO == null ? null : flowNodeDO.getPermissionFlag();
    }

    @Override
    public Node setPermissionFlag(String permissionFlag) {
        return chain(d -> d.setPermissionFlag(permissionFlag));
    }

    @Override
    public String getCoordinate() {
        return flowNodeDO == null ? null : flowNodeDO.getCoordinate();
    }

    @Override
    public Node setCoordinate(String coordinate) {
        return chain(d -> d.setCoordinate(coordinate));
    }

    @Override
    public String getAnyNodeSkip() {
        return flowNodeDO == null ? null : flowNodeDO.getAnyNodeSkip();
    }

    @Override
    public Node setAnyNodeSkip(String anyNodeSkip) {
        return chain(d -> d.setAnyNodeSkip(anyNodeSkip));
    }

    @Override
    public String getListenerType() {
        return flowNodeDO == null ? null : flowNodeDO.getListenerType();
    }

    @Override
    public Node setListenerType(String listenerType) {
        return chain(d -> d.setListenerType(listenerType));
    }

    @Override
    public String getListenerPath() {
        return flowNodeDO == null ? null : flowNodeDO.getListenerPath();
    }

    @Override
    public Node setListenerPath(String listenerPath) {
        return chain(d -> d.setListenerPath(listenerPath));
    }

    @Override
    public String getHandlerType() {
        return flowNodeDO == null ? null : flowNodeDO.getHandlerType();
    }

    @Override
    public Node setHandlerType(String listenerType) {
        return chain(d -> d.setHandlerType(listenerType));
    }

    @Override
    public String getHandlerPath() {
        return flowNodeDO == null ? null : flowNodeDO.getHandlerPath();
    }

    @Override
    public Node setHandlerPath(String listenerPath) {
        return chain(d -> d.setHandlerPath(listenerPath));
    }

    @Override
    public String getFormCustom() {
        return flowNodeDO == null ? null : flowNodeDO.getFormCustom();
    }

    @Override
    public Node setFormCustom(String formCustom) {
        return chain(d -> d.setFormCustom(formCustom));
    }

    @Override
    public String getFormPath() {
        return flowNodeDO == null ? null : flowNodeDO.getFormPath();
    }

    @Override
    public Node setFormPath(String formPath) {
        return chain(d -> d.setFormPath(formPath));
    }

    @Override
    public String getExt() {
        return flowNodeDO == null ? null : flowNodeDO.getExt();
    }

    @Override
    public Node setExt(String ext) {
        return chain(d -> d.setExt(ext));
    }

    @Override
    public String getVersion() {
        return flowNodeDO == null ? null : String.valueOf(flowNodeDO.getLockVersion());
    }

    @Override
    public Node setVersion(String version) {
        return chain(d -> d.setLockVersion(Long.valueOf(version)));
    }

    @Override
    public List<Skip> getSkipList() {
        if (flowNodeDO == null) {
            return Collections.emptyList();
        }

        return flowNodeDO.getSkipList();
    }

    @Override
    public Node setSkipList(List<Skip> skipList) {
        return chain(d -> d.setSkipList(skipList));
    }
}