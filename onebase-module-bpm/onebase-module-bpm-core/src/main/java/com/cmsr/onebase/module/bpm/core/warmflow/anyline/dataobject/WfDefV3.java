//package com.cmsr.onebase.module.bpm.core.dal.dataobject;
//
//import com.cmsr.onebase.framework.data.base.BaseDO;
//import jakarta.persistence.Column;
//import jakarta.persistence.Table;
//import lombok.Data;
//import lombok.experimental.Accessors;
//import org.dromara.warm.flow.core.entity.Definition;
//import org.dromara.warm.flow.core.entity.Node;
//import org.dromara.warm.flow.core.entity.User;
//
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//@Data
//@Accessors(chain = true)
//@Table(name = "bpm_flow_definition")
//public class WfDefV3 extends BaseDO implements Definition {
//
//    // 其他 WarmFlow 字段自行声明：tenantId、delFlag、flowCode、... 等
//
//    // === Definition 的时间方法（Date 签名） ===
//    @Override
//    public Date getCreateTime() {
//        var t = super.getCreateTime(); // LocalDateTime
//        return t == null ? null : Date.from(t.atZone(ZoneId.systemDefault()).toInstant());
//    }
//
//    @Override
//    public Definition setCreateTime(Date createTime) {
//        super.setCreateTime(createTime == null ? null :
//                LocalDateTime.ofInstant(createTime.toInstant(), ZoneId.systemDefault()));
//        return this;
//    }
//
//    @Override
//    public Date getUpdateTime() {
//        var t = super.getUpdateTime(); // LocalDateTime
//        return t == null ? null : Date.from(t.atZone(ZoneId.systemDefault()).toInstant());
//    }
//
//    @Override
//    public Definition setUpdateTime(Date updateTime) {
//        super.setUpdateTime(updateTime == null ? null :
//                LocalDateTime.ofInstant(updateTime.toInstant(), ZoneId.systemDefault()));
//        return this;
//    }
//
//    // === Definition 的 id 签名（链式返回） ===
//    @Override
//    public Long getId() {
//        return super.getId(); // 与 BaseDO 一致
//    }
//
//    @Override
//    public Definition setId(Long id) {
//        super.setId(id);      // BaseDO 的 setter
//        return this;          // 符合 Definition 的链式返回
//    }
//
//
//    /**
//     * 创建人
//     */
//    @Column(name = "create_by", length = 64)
//    private String createBy;
//
//    /**
//     * 更新人
//     */
//    @Column(name = "update_by", length = 64)
//    private String updateBy;
//
//    /**
//     * 租户ID
//     */
//    @Column(name = "tenant_id", length = 40)
//    private String tenantId;
//
//    /**
//     * 删除标记
//     */
//    @Column(name = "del_flag", length = 1)
//    private String delFlag;
//
//    /**
//     * 流程编码
//     */
//    @Column(name = "flow_code", length = 40, nullable = false)
//    private String flowCode;
//
//    /**
//     * 流程名称
//     */
//    @Column(name = "flow_name", length = 100, nullable = false)
//    private String flowName;
//
//    /**
//     * 设计器模型（CLASSICS经典模型 MIMIC仿钉钉模型）
//     */
//    @Column(name = "model_value", length = 40, nullable = false)
//    private String modelValue;
//
//    /**
//     * 流程类别
//     */
//    @Column(name = "category", length = 100)
//    private String category;
//
//    /**
//     * 流程版本
//     */
//    @Column(name = "version", length = 20, nullable = false)
//    private String version;
//
//    /**
//     * 是否发布（0未开启 1开启）
//     */
//    @Column(name = "is_publish", nullable = false)
//    private Integer isPublish;
//
//    /**
//     * 审批表单是否自定义（Y是 2否）
//     */
//    @Column(name = "form_custom", length = 1)
//    private String formCustom;
//
//    /**
//     * 审批表单路径
//     */
//    @Column(name = "form_path", length = 100)
//    private String formPath;
//
//    /**
//     * 流程激活状态（0挂起 1激活）
//     */
//    @Column(name = "activity_status", nullable = false)
//    private Integer activityStatus;
//
//    /**
//     * 监听器类型
//     */
//    @Column(name = "listener_type", length = 100)
//    private String listenerType;
//
//    /**
//     * 监听器路径
//     */
//    @Column(name = "listener_path", length = 400)
//    private String listenerPath;
//
//    /**
//     * 扩展字段，预留给业务系统使用
//     */
//    @Column(name = "ext", length = 500)
//    private String ext;
//
//    private List<Node> nodeList = new ArrayList<>();
//
//    private List<User> userList = new ArrayList<>();
//
//    // 其他 Definition 方法（tenantId、delFlag、...）照常实现或直接字段+@Data
//}