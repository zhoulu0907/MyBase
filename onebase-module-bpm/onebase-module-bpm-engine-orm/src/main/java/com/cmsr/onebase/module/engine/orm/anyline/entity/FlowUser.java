package com.cmsr.onebase.module.engine.orm.anyline.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.warm.flow.core.entity.User;

import java.time.LocalDateTime;

/**
 * WarmFlow 流程用户 DO，对应表 flow_user。
 *
 * @author liyang
 * @date 2025-09-29
 */
@Data
@Accessors(chain = true)
@Table(name = "bpm_flow_user")
public class FlowUser extends BaseEntity implements User {

    /** 人员类型（1审批人 2转办人 3委托人） */
    @Column(name = "type", length = 1, nullable = false)
    private String type;

    /** 权限人（可能是角色ID或用户ID等） */
    @Column(name = "processed_by", length = 80)
    private String processedBy;

    /** 关联的任务ID */
    @Column(name = "associated", nullable = false)
    private Long associated;



    /* ==================== 以下为 User 接口方法实现 ==================== */

    @Override
    public User setId(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public User setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    @Override
    public User setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    @Override
    public User setCreateBy(String createBy) {
        if (createBy != null) {
            this.createBy = Long.valueOf(createBy);
        } else {
            this.createBy = null;
        }

        return this;
    }

    @Override
    public User setUpdateBy(String updateBy) {
        if (updateBy != null) {
            this.updateBy = Long.valueOf(updateBy);
        } else {
            this.updateBy = null;
        }

        return this;
    }

    @Override
    public User setTenantId(String tenantId) {
        if (tenantId != null) {
            this.tenantId = Long.valueOf(tenantId);
        } else {
            this.tenantId = null;
        }

        return this;
    }

    @Override
    public User setDelFlag(String delFlag) {
        if (delFlag != null) {
            this.delFlag = Long.valueOf(delFlag);
        } else {
            this.delFlag = null;
        }

        return this;
    }
}


