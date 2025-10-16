package com.cmsr.onebase.module.engine.orm.anyline.entity;

import com.cmsr.onebase.framework.data.base.BaseDOInterface;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @Column(name = CREATOR)
    protected Long createBy;

    /**
     * 更新者，目前使用 SysUser 的 id 编号
     *
     */
    @Column(name = UPDATER)
    protected Long updateBy;


    /**
     * 是否删除
     */
    @Getter
    @Column(name = DELETED, columnDefinition = "INT8 NOT NULL DEFAULT 0", comment = "是否删除")
    protected Long delFlag;

    /**
     * 乐观锁版本号
     */
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
        if (delFlag == null) {
            return null;
        }
        return String.valueOf(delFlag);
    }

    public String getUpdateBy() {
        if (updateBy == null) {
            return null;
        }
        return String.valueOf(updateBy);
    }

    public String getCreateBy() {
        if (createBy == null) {
            return null;
        }
        return String.valueOf(createBy);
    }

    /**
     * 把 creator、createTime、updateTime、updater 都清空，避免前端直接传递 creator 之类的字段，直接就被更新了
     */
    public void clean() {
        this.createBy = null;
        this.createTime = null;
        this.updateBy = null;
        this.updateTime = null;
    }
}
