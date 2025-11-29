package com.cmsr.onebase.framework.orm.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Warm-Flow 流程定义基础对象
 *
 * 手动实现与BaseEntity的差异的字段
 *
 * @author liyang
 * @date 2025-11-27
 *
 */
@NoArgsConstructor
@AllArgsConstructor
public class WarmFlowBaseEntity {
    /**
     * 设置为注入雪花ID
     */
    @Getter
    @Id(comment = "主键ID")
    protected Long id;

    @Getter
    @Setter
    @Column(value = "creator", comment = "创建人")
    protected Long creator;

    @Getter
    @Column(value = "create_time", comment = "创建时间")
    protected LocalDateTime createTime;

    @Getter
    @Setter
    @Column(value = "updater", comment = "更新人")
    protected Long updater;

    @Getter
    @Column(value = "update_time", comment = "更新时间")
    protected LocalDateTime updateTime;

    @Getter
    @Setter
    @Column(value = "deleted", comment = "删除标识")
    protected Long deleted;

    @Getter
    @Setter
    @Column(value = "lock_version", comment = "乐观锁")
    protected Long lockVersion;

    @Column(value = "tenant_id", comment = "租户ID", tenantId = true)
    protected Long tenantId;

    /**
     * 多租户转成字符串
     */
    public String getTenantId() {
        if (tenantId == null) {
            return null;
        }
        return String.valueOf(tenantId);
    }

    /**
     * 删除标识转成字符串
     *
     */
    public String getDelFlag() {
        if (deleted == null) {
            return null;
        }
        return String.valueOf(deleted);
    }

    /**
     * 更新人转成字符串
     *
     */
    public String getUpdateBy() {
        if (updater == null) {
            return null;
        }
        return String.valueOf(updater);
    }

    /**
     * 创建人转成字符串
     */
    public String getCreateBy() {
        if (creator == null) {
            return null;
        }
        return String.valueOf(creator);
    }
}
