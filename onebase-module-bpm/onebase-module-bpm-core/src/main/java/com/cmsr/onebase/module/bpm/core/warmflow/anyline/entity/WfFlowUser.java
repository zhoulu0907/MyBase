package com.cmsr.onebase.module.bpm.core.warmflow.anyline.entity;

import com.cmsr.onebase.module.bpm.core.warmflow.anyline.dataobject.BpmFlowUserDO;
import lombok.Data;
import org.dromara.warm.flow.core.entity.User;

import java.util.Date;

/**
 * WarmFlow 用户适配器
 * 使用组合模式承载 {@link FlowUserDO}
 */
@Data
public class WfFlowUser extends BpmFlowUserDO {

//    private BpmFlowUserDO flowUserDO;
//
//    private BpmFlowUserDO ensure() {
//        if (flowUserDO == null) {
//            flowUserDO = new BpmFlowUserDO();
//        }
//        return flowUserDO;
//    }
//
//    private User chain(java.util.function.Consumer<BpmFlowUserDO> f) {
//        f.accept(ensure());
//        return this;
//    }
//
//    @Override
//    public Long getId() {
//        return flowUserDO == null ? null : flowUserDO.getId();
//    }
//
//    @Override
//    public User setId(Long id) {
//        return chain(d -> d.setId(id));
//    }
//
//    @Override
//    public Date getCreateTime() {
//        return flowUserDO == null ? null : (flowUserDO.getCreateTime() == null ? null : java.util.Date.from(flowUserDO.getCreateTime().atZone(java.time.ZoneId.systemDefault()).toInstant()));
//    }
//
//    @Override
//    public User setCreateTime(Date createTime) {
//        return chain(d -> d.setCreateTime(createTime == null ? null :
//                java.time.LocalDateTime.ofInstant(createTime.toInstant(), java.time.ZoneId.systemDefault())));
//    }
//
//    @Override
//    public Date getUpdateTime() {
//        return flowUserDO == null ? null : (flowUserDO.getUpdateTime() == null ? null : java.util.Date.from(flowUserDO.getUpdateTime().atZone(java.time.ZoneId.systemDefault()).toInstant()));
//    }
//
//    @Override
//    public User setUpdateTime(Date updateTime) {
//        return chain(d -> d.setUpdateTime(updateTime == null ? null :
//                java.time.LocalDateTime.ofInstant(updateTime.toInstant(), java.time.ZoneId.systemDefault())));
//    }
//
//    @Override
//    public String getTenantId() {
//        return flowUserDO == null ? null : flowUserDO.getTenantId();
//    }
//
//    @Override
//    public User setTenantId(String tenantId) {
//        return chain(d -> d.setTenantId(tenantId));
//    }
//
//    @Override
//    public String getDelFlag() {
//        return flowUserDO == null ? null : (flowUserDO.getDeleted() == null ? null : flowUserDO.getDeleted().toString());
//    }
//
//    @Override
//    public User setDelFlag(String delFlag) {
//        return chain(d -> d.setDeleted(delFlag == null ? null : Long.valueOf(delFlag)));
//    }
//
//    @Override
//    public String getProcessedBy() {
//        return flowUserDO == null ? null : flowUserDO.getProcessedBy();
//    }
//
//    @Override
//    public User setProcessedBy(String processedBy) {
//        return chain(d -> d.setProcessedBy(processedBy));
//    }
//
//    @Override
//    public String getType() {
//        return flowUserDO == null ? null : flowUserDO.getType();
//    }
//
//    @Override
//    public User setType(String type) {
//        return chain(d -> d.setType(type));
//    }
//
//    @Override
//    public Long getAssociated() {
//        return flowUserDO == null ? null : flowUserDO.getAssociated();
//    }
//
//    @Override
//    public User setAssociated(Long associated) {
//        return chain(d -> d.setAssociated(associated));
//    }
//
//    @Override
//    public String getCreateBy() {
//        return flowUserDO == null ? null : (flowUserDO.getCreator() == null ? null : flowUserDO.getCreator().toString());
//    }
//
//    @Override
//    public User setCreateBy(String createBy) {
//        return chain(d -> d.setCreator(createBy == null ? null : Long.valueOf(createBy)));
//    }
}


