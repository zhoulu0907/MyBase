package com.cmsr.onebase.framework.mybatis;


import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BaseBizEntity {

    @Id
    private Long id;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 最后更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建者，目前使用 SysUser 的 id 编号
     */
    private Long creator;

    /**
     * 更新者，目前使用 SysUser 的 id 编号
     */
    private Long updater;

    /**
     * 是否删除
     */
    private Long deleted;

    /**
     * 乐观锁版本号
     */
    private Long lockVersion;

    @Column("application_id")
    private Long applicationId;

    @Column("version_id")
    private Long versionId;

    @Column("version_status")
    private String versionStatus;

    @Column("tenant_id")
    private Long tenantId;

}
