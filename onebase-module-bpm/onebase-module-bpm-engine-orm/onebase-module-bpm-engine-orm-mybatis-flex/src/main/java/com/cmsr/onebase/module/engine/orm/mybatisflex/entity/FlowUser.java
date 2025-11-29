package com.cmsr.onebase.module.engine.orm.mybatisflex.entity;

import com.cmsr.onebase.framework.orm.entity.WarmFlowBaseEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.warm.flow.core.entity.User;

import java.time.LocalDateTime;

/**
 * WarmFlow 流程用户 DO，对应表 bpm_flow_user。
 *
 * @author liyang
 * @date 2025-09-29
 */
@Data
@Accessors(chain = true)
@Table(value = "bpm_flow_user")
public class FlowUser extends WarmFlowBaseEntity implements User {
    /** 人员类型（1审批人 2转办人 3委托人） */
    @Column(value = "type", comment = "人员类型（1审批人 2转办人 3委托人）")
    private String type;

    /** 权限人（可能是角色ID或用户ID等） */
    @Column(value = "processed_by", comment = "权限人（可能是角色ID或用户ID等）")
    private String processedBy;

    /** 关联的任务ID */
    @Column(value = "associated", comment = "关联的任务ID")
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
            this.creator = Long.valueOf(createBy);
        } else {
            this.creator = null;
        }

        return this;
    }

    @Override
    public User setUpdateBy(String updateBy) {
        if (updateBy != null) {
            this.updater = Long.valueOf(updateBy);
        } else {
            this.updater = null;
        }

        return this;
    }

    @Override
    public User setTenantId(String tenantId) {
        if (tenantId != null) {
            this.wfTenantId = Long.valueOf(tenantId);
        } else {
            this.wfTenantId = null;
        }

        return this;
    }

    @Override
    public User setDelFlag(String delFlag) {
        if (delFlag != null) {
            this.deleted = Long.valueOf(delFlag);
        } else {
            this.deleted = null;
        }

        return this;
    }
}


