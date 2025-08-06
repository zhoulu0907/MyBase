package com.cmsr.onebase.module.app.dal.dataobject.app;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/7/22 17:50
 */
@Data
@Table(name = "app_application_tag")
public class ApplicationTagDO extends TenantBaseDO {

    @Column(name = "application_id", nullable = false, comment = "应用ID")
    private Long applicationId;

    @Column(name = "tag_id", nullable = false, comment = "标签ID")
    private Long tagId;


}