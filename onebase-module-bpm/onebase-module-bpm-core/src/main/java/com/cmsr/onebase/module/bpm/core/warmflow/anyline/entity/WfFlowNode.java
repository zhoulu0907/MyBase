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

import com.cmsr.onebase.module.bpm.core.warmflow.anyline.dataobject.BpmFlowHisTaskDO;
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
public class WfFlowNode extends BpmFlowNodeDO {
//    private BpmFlowNodeDO flowNodeDO;
//
//
//    @Override
//    public Long getId() {
//        return flowNodeDO == null ? null : flowNodeDO.getId();
//    }
//
//    @Override
//    public Node setId(Long id) {
//        if (flowNodeDO == null) {
//            flowNodeDO = new BpmFlowNodeDO();
//        }
//        flowNodeDO.setId(id);
//        return this;
//    }
//
//    @Override
//    public Date getCreateTime() {
//        if (flowNodeDO == null || flowNodeDO.getCreateTime() == null) {
//            return null;
//        }
//        return java.util.Date.from(flowNodeDO.getCreateTime().atZone(java.time.ZoneId.systemDefault()).toInstant());
//    }
//
//    @Override
//    public Node setCreateTime(Date createTime) {
//        if (flowNodeDO == null) {
//            flowNodeDO = new BpmFlowNodeDO();
//        }
//        flowNodeDO.setCreateTime(createTime == null ? null : java.time.LocalDateTime.ofInstant(createTime.toInstant(), java.time.ZoneId.systemDefault()));
//        return this;
//    }
//
//    @Override
//    public Date getUpdateTime() {
//        if (flowNodeDO == null || flowNodeDO.getUpdateTime() == null) {
//            return null;
//        }
//        return java.util.Date.from(flowNodeDO.getUpdateTime().atZone(java.time.ZoneId.systemDefault()).toInstant());
//    }
//
//    @Override
//    public Node setUpdateTime(Date updateTime) {
//        if (flowNodeDO == null) {
//            flowNodeDO = new BpmFlowNodeDO();
//        }
//        flowNodeDO.setUpdateTime(updateTime == null ? null : java.time.LocalDateTime.ofInstant(updateTime.toInstant(), java.time.ZoneId.systemDefault()));
//        return this;
//    }
//
//    @Override
//    public String getTenantId() {
//        return flowNodeDO == null ? null : flowNodeDO.getTenantId();
//    }
//
//    @Override
//    public Node setTenantId(String tenantId) {
//        if (flowNodeDO == null) {
//            flowNodeDO = new BpmFlowNodeDO();
//        }
//        flowNodeDO.setTenantId(tenantId);
//        return this;
//    }
//
//    @Override
//    public String getDelFlag() {
//        return flowNodeDO == null ? null : String.valueOf(flowNodeDO.getDeleted());
//    }
//
//    @Override
//    public Node setDelFlag(String delFlag) {
//        if (flowNodeDO == null) {
//            flowNodeDO = new BpmFlowNodeDO();
//        }
//        flowNodeDO.setDeleted(Long.valueOf(delFlag));
//        return this;
//    }
//
//    @Override
//    public Integer getNodeType() {
//        return flowNodeDO == null ? null : flowNodeDO.getNodeType();
//    }
//
//    @Override
//    public Node setNodeType(Integer nodeType) {
//        if (flowNodeDO == null) {
//            flowNodeDO = new BpmFlowNodeDO();
//        }
//        flowNodeDO.setNodeType(nodeType);
//        return this;
//    }
//
//    @Override
//    public Long getDefinitionId() {
//        return flowNodeDO == null ? null : flowNodeDO.getDefinitionId();
//    }
//
//    @Override
//    public Node setDefinitionId(Long definitionId) {
//        if (flowNodeDO == null) {
//            flowNodeDO = new BpmFlowNodeDO();
//        }
//        flowNodeDO.setDefinitionId(definitionId);
//        return this;
//    }
//
//    @Override
//    public String getNodeCode() {
//        return flowNodeDO == null ? null : flowNodeDO.getNodeCode();
//    }
//
//    @Override
//    public Node setNodeCode(String nodeCode) {
//        if (flowNodeDO == null) {
//            flowNodeDO = new BpmFlowNodeDO();
//        }
//        flowNodeDO.setNodeCode(nodeCode);
//        return this;
//    }
//
//    @Override
//    public String getNodeName() {
//        return flowNodeDO == null ? null : flowNodeDO.getNodeName();
//    }
//
//    @Override
//    public Node setNodeName(String nodeName) {
//        if (flowNodeDO == null) {
//            flowNodeDO = new BpmFlowNodeDO();
//        }
//        flowNodeDO.setNodeName(nodeName);
//        return this;
//    }
//
//    @Override
//    public BigDecimal getNodeRatio() {
//        return flowNodeDO == null ? null : flowNodeDO.getNodeRatio();
//    }
//
//    @Override
//    public Node setNodeRatio(BigDecimal nodeRatio) {
//        if (flowNodeDO == null) {
//            flowNodeDO = new BpmFlowNodeDO();
//        }
//        flowNodeDO.setNodeRatio(nodeRatio);
//        return this;
//    }
//
//    @Override
//    public String getPermissionFlag() {
//        return flowNodeDO == null ? null : flowNodeDO.getPermissionFlag();
//    }
//
//    @Override
//    public Node setPermissionFlag(String permissionFlag) {
//        if (flowNodeDO == null) {
//            flowNodeDO = new BpmFlowNodeDO();
//        }
//        flowNodeDO.setPermissionFlag(permissionFlag);
//        return this;
//    }
//
//    @Override
//    public String getCoordinate() {
//        return flowNodeDO == null ? null : flowNodeDO.getCoordinate();
//    }
//
//    @Override
//    public Node setCoordinate(String coordinate) {
//        if (flowNodeDO == null) {
//            flowNodeDO = new BpmFlowNodeDO();
//        }
//        flowNodeDO.setCoordinate(coordinate);
//        return this;
//    }
//
//    @Override
//    public String getAnyNodeSkip() {
//        return flowNodeDO == null ? null : flowNodeDO.getAnyNodeSkip();
//    }
//
//    @Override
//    public Node setAnyNodeSkip(String anyNodeSkip) {
//        if (flowNodeDO == null) {
//            flowNodeDO = new BpmFlowNodeDO();
//        }
//        flowNodeDO.setAnyNodeSkip(anyNodeSkip);
//        return this;
//    }
//
//    @Override
//    public String getListenerType() {
//        return flowNodeDO == null ? null : flowNodeDO.getListenerType();
//    }
//
//    @Override
//    public Node setListenerType(String listenerType) {
//        if (flowNodeDO == null) {
//            flowNodeDO = new BpmFlowNodeDO();
//        }
//        flowNodeDO.setListenerType(listenerType);
//        return this;
//    }
//
//    @Override
//    public String getListenerPath() {
//        return flowNodeDO == null ? null : flowNodeDO.getListenerPath();
//    }
//
//    @Override
//    public Node setListenerPath(String listenerPath) {
//        if (flowNodeDO == null) {
//            flowNodeDO = new BpmFlowNodeDO();
//        }
//        flowNodeDO.setListenerPath(listenerPath);
//        return this;
//    }
//
//    @Override
//    public String getHandlerType() {
//        return flowNodeDO == null ? null : flowNodeDO.getHandlerType();
//    }
//
//    @Override
//    public Node setHandlerType(String listenerType) {
//        if (flowNodeDO == null) {
//            flowNodeDO = new BpmFlowNodeDO();
//        }
//        flowNodeDO.setHandlerType(listenerType);
//        return this;
//    }
//
//    @Override
//    public String getHandlerPath() {
//        return flowNodeDO == null ? null : flowNodeDO.getHandlerPath();
//    }
//
//    @Override
//    public Node setHandlerPath(String listenerPath) {
//        if (flowNodeDO == null) {
//            flowNodeDO = new BpmFlowNodeDO();
//        }
//        flowNodeDO.setHandlerPath(listenerPath);
//        return this;
//    }
//
//    @Override
//    public String getFormCustom() {
//        return flowNodeDO == null ? null : flowNodeDO.getFormCustom();
//    }
//
//    @Override
//    public Node setFormCustom(String formCustom) {
//        if (flowNodeDO == null) {
//            flowNodeDO = new BpmFlowNodeDO();
//        }
//        flowNodeDO.setFormCustom(formCustom);
//        return this;
//    }
//
//    @Override
//    public String getFormPath() {
//        return flowNodeDO == null ? null : flowNodeDO.getFormPath();
//    }
//
//    @Override
//    public Node setFormPath(String formPath) {
//        if (flowNodeDO == null) {
//            flowNodeDO = new BpmFlowNodeDO();
//        }
//        flowNodeDO.setFormPath(formPath);
//        return this;
//    }
//
//    @Override
//    public String getExt() {
//        return flowNodeDO == null ? null : flowNodeDO.getExt();
//    }
//
//    @Override
//    public Node setExt(String ext) {
//        if (flowNodeDO == null) {
//            flowNodeDO = new BpmFlowNodeDO();
//        }
//        flowNodeDO.setExt(ext);
//        return this;
//    }
//
//    @Override
//    public String getVersion() {
//        return flowNodeDO == null ? null : String.valueOf(flowNodeDO.getLockVersion());
//    }
//
//    @Override
//    public Node setVersion(String version) {
//        if (flowNodeDO == null) {
//            flowNodeDO = new BpmFlowNodeDO();
//        }
//        flowNodeDO.setLockVersion(Long.valueOf(version));
//        return this;
//    }
//
//    @Override
//    public List<Skip> getSkipList() {
//        if (flowNodeDO == null) {
//            return Collections.emptyList();
//        }
//
//        return flowNodeDO.getSkipList();
//    }
//
//    @Override
//    public Node setSkipList(List<Skip> skipList) {
//        if (flowNodeDO == null) {
//            flowNodeDO = new BpmFlowNodeDO();
//        }
//        flowNodeDO.setSkipList(skipList);
//        return this;
//    }
}