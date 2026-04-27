package com.cmsr.onebase.framework.data.base;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Warm-Flow 流程定义基础对象
 *
 * 手动实现与BaseDO的差异的字段
 *
 */
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntity implements BaseDOInterface {
    public static final String ID = "id";
    public static final String CREATE_TIME = "create_time";
    public static final String UPDATE_TIME = "update_time";
    public static final String CREATOR = "creator";
    public static final String UPDATER = "updater";
    public static final String DELETED = "deleted";
    private static final String LOCK_VERSION = "lock_version";

    public static final String TENANT_ID = "tenant_id";

    /**
     * 创建时间
     * 设置为注入雪花ID
     */
    @Id
    @Column(name = ID, columnDefinition = "BIGINT NOT NULL PRIMARY KEY")
    @Getter
    protected Long id;
    /**
     * 创建时间
     */
    @Getter
    @Column(name = CREATE_TIME)
    protected LocalDateTime createTime;
    /**
     * 最后更新时间
     */
    @Getter
    @Column(name = UPDATE_TIME)
    protected LocalDateTime updateTime;

    /**
     * 创建者，目前使用 SysUser 的 id 编号
     *
     */
    @Getter
    @Setter
    @Column(name = CREATOR)
    protected Long creator;

    /**
     * 更新者，目前使用 SysUser 的 id 编号
     *
     */
    @Getter
    @Setter
    @Column(name = UPDATER)
    protected Long updater;


    /**
     * 是否删除
     */
    @Getter
    @Setter
    @Column(name = DELETED, columnDefinition = "INT8 NOT NULL DEFAULT 0", comment = "是否删除")
    protected Long deleted;

    /**
     * 乐观锁版本号
     */
    @Getter
    @Setter
    @Column(name = LOCK_VERSION)
    protected Long lockVersion;

    /**
     * 多租户编号
     */
    @Column(name = TENANT_ID)
    protected Long tenantId;

    public String getTenantId() {
        if (tenantId == null) {
            return null;
        }
        return String.valueOf(tenantId);
    }

    public String getDelFlag() {
        if (deleted == null) {
            return null;
        }
        return String.valueOf(deleted);
    }

    public String getUpdateBy() {
        if (updater == null) {
            return null;
        }
        return String.valueOf(updater);
    }

    public String getCreateBy() {
        if (creator == null) {
            return null;
        }
        return String.valueOf(creator);
    }

    // =============== 以下实现get函数提供给Anyline的Listener使用 =================

    public Long getIdByListener() {
        return this.id;
    }

    public Long getCreatorByListener() {
        return this.creator;
    }

    public Long getUpdaterByListener() {
        return this.updater;
    }

    public Long getDeletedByListener() {
        return this.deleted;
    }

    public Long getTenantIdByListener() {
        return this.tenantId;
    }

    public LocalDateTime getCreateTimeByListener() {
        return this.createTime;
    }

    public LocalDateTime getUpdateTimeByListener() {
        return this.updateTime;
    }

    // =============== 以下实现set函数提供给Anyline的Listener使用 =================


    public void setIdByListener(Long id) {
        this.id = id;
    }

    public void setTenantIdByListener(Long tenantId) {
        this.tenantId = tenantId;
    }

    public void setDeletedByListener(Long delFlag) {
        this.deleted = delFlag;
    }

    public void setCreatorByListener(Long creator) {
        this.creator = creator;
    }

    public void setUpdaterByListener(Long updater) {
        this.updater = updater;
    }

    public void setCreateTimeByListener(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public void setUpdateTimeByListener(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 把 creator、createTime、updateTime、updater 都清空，避免前端直接传递 creator 之类的字段，直接就被更新了
     */
    public void clean() {
        this.creator = null;
        this.createTime = null;
        this.updater = null;
        this.updateTime = null;
    }
}
