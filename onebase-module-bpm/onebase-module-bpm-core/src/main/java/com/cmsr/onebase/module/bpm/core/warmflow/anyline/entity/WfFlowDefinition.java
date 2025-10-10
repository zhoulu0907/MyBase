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

import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.module.bpm.core.warmflow.anyline.dataobject.BpmFlowDefinitionDO;
import lombok.Data;
import lombok.experimental.Delegate;
import org.dromara.warm.flow.core.entity.Definition;
import org.dromara.warm.flow.core.entity.Node;
import org.dromara.warm.flow.core.entity.User;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

/**
 * 流程定义对象 flow_definition
 * 同时继承 BaseDO 和实现 Definition 接口
 * 使用字段隐藏解决类型冲突
 *
 * @author warm
 * @since 2023-03-29
 */
@Data
public class WfFlowDefinition extends BaseDO implements Definition {
//    @Delegate
//    private BpmFlowDefinitionDO flowDefinitionDO;
//
//    private BpmFlowDefinitionDO ensure() {
//        if (flowDefinitionDO == null) {
//            flowDefinitionDO = new BpmFlowDefinitionDO();
//        }
//        return flowDefinitionDO;
//    }

//    private Definition chain(Consumer<BpmFlowDefinitionDO> f) {
//        f.accept(ensure());
//        return this;
//    }


//    @Override
//    public Long getId() {
//        return flowDefinitionDO == null ? null : flowDefinitionDO.getId();
//    }
//
//    @Override
//    public Definition setId(Long id) {
//        return chain(d -> d.setId(id));
//    }
//
//    @Override
//    public Date getCreateTime() {
//        if (flowDefinitionDO == null || flowDefinitionDO.getCreateTime() == null) {
//            return null;
//        }
//        return java.util.Date.from(flowDefinitionDO.getCreateTime()
//                .atZone(java.time.ZoneId.systemDefault()).toInstant());
//    }
//
//    @Override
//    public Definition setCreateTime(Date createTime) {
//        return chain(d -> d.setCreateTime(createTime == null ? null :
//                java.time.LocalDateTime.ofInstant(createTime.toInstant(), java.time.ZoneId.systemDefault())));
//    }
//
//    @Override
//    public Date getUpdateTime() {
//        if (flowDefinitionDO == null || flowDefinitionDO.getUpdateTime() == null) {
//            return null;
//        }
//        return java.util.Date.from(flowDefinitionDO.getUpdateTime()
//                .atZone(java.time.ZoneId.systemDefault()).toInstant());
//    }
//
//    @Override
//    public Definition setUpdateTime(Date updateTime) {
//        return chain(d -> d.setUpdateTime(updateTime == null ? null :
//                java.time.LocalDateTime.ofInstant(updateTime.toInstant(), java.time.ZoneId.systemDefault())));
//    }
//
//    @Override
//    public String getTenantId() {
//        return flowDefinitionDO == null ? null : flowDefinitionDO.getTenantId();
//    }
//
//    @Override
//    public Definition setTenantId(String tenantId) {
//        return chain(d -> d.setTenantId(tenantId));
//    }
//
//    @Override
//    public String getDelFlag() {
//        return flowDefinitionDO == null ? null : (flowDefinitionDO.getDeleted() == null ? null : flowDefinitionDO.getDeleted().toString());
//    }
//
//    @Override
//    public Definition setDelFlag(String delFlag) {
//        return chain(d -> d.setDeleted(delFlag == null ? null : Long.valueOf(delFlag)));
//    }
//
//    @Override
//    public String getFlowCode() {
//        return flowDefinitionDO == null ? null : flowDefinitionDO.getFlowCode();
//    }
//
//    @Override
//    public Definition setFlowCode(String flowCode) {
//        return chain(d -> d.setFlowCode(flowCode));
//    }
//
//    @Override
//    public String getFlowName() {
//        return flowDefinitionDO == null ? null : flowDefinitionDO.getFlowName();
//    }
//
//    @Override
//    public Definition setFlowName(String flowName) {
//        return chain(d -> d.setFlowName(flowName));
//    }
//
//    @Override
//    public String getModelValue() {
//        return flowDefinitionDO == null ? null : flowDefinitionDO.getModelValue();
//    }
//
//    @Override
//    public Definition setModelValue(String modelValue) {
//        return chain(d -> d.setModelValue(modelValue));
//    }
//
//    @Override
//    public String getCategory() {
//        return flowDefinitionDO == null ? null : flowDefinitionDO.getCategory();
//    }
//
//    @Override
//    public Definition setCategory(String category) {
//        return chain(d -> d.setCategory(category));
//    }

//    @Override
//    public String getVersion() {
//        // 当前 DO 未定义 version 字段，保持为 null
//        return null;
//    }
//
//    @Override
//    public Definition setVersion(String version) {
//        // 当前 DO 未定义 version 字段，直接返回 this 保持链式
//        return this;
//    }
//
//    @Override
//    public Integer getIsPublish() {
//        return flowDefinitionDO == null ? null : flowDefinitionDO.getIsPublish();
//    }
//
//    @Override
//    public Definition setIsPublish(Integer isPublish) {
//        return chain(d -> d.setIsPublish(isPublish));
//    }
//
//    @Override
//    public String getFormCustom() {
//        return flowDefinitionDO == null ? null : flowDefinitionDO.getFormCustom();
//    }
//
//    @Override
//    public Definition setFormCustom(String formCustom) {
//        return chain(d -> d.setFormCustom(formCustom));
//    }
//
//    @Override
//    public String getFormPath() {
//        return flowDefinitionDO == null ? null : flowDefinitionDO.getFormPath();
//    }
//
//    @Override
//    public Definition setFormPath(String formPath) {
//        return chain(d -> d.setFormPath(formPath));
//    }
//
//    @Override
//    public Integer getActivityStatus() {
//        return flowDefinitionDO == null ? null : flowDefinitionDO.getActivityStatus();
//    }
//
//    @Override
//    public Definition setActivityStatus(Integer activityStatus) {
//        return chain(d -> d.setActivityStatus(activityStatus));
//    }
//
//    @Override
//    public String getListenerType() {
//        return flowDefinitionDO == null ? null : flowDefinitionDO.getListenerType();
//    }
//
//    @Override
//    public Definition setListenerType(String listenerType) {
//        return chain(d -> d.setListenerType(listenerType));
//    }
//
//    @Override
//    public String getListenerPath() {
//        return flowDefinitionDO == null ? null : flowDefinitionDO.getListenerPath();
//    }
//
//    @Override
//    public Definition setListenerPath(String listenerPath) {
//        return chain(d -> d.setListenerPath(listenerPath));
//    }
//
//    @Override
//    public String getExt() {
//        return flowDefinitionDO == null ? null : flowDefinitionDO.getExt();
//    }
//
//    @Override
//    public Definition setExt(String ext) {
//        return chain(d -> d.setExt(ext));
//    }
//
//    @Override
//    public List<Node> getNodeList() {
//        return flowDefinitionDO == null ? null : flowDefinitionDO.getNodeList();
//    }
//
//    @Override
//    public Definition setNodeList(List<Node> nodeList) {
//        return chain(d -> d.setNodeList(nodeList));
//    }
//
//    @Override
//    public List<User> getUserList() {
//        return flowDefinitionDO == null ? null : flowDefinitionDO.getUserList();
//    }
//
//    @Override
//    public Definition setUserList(List<User> userList) {
//        return chain(d -> d.setUserList(userList));
//    }
}