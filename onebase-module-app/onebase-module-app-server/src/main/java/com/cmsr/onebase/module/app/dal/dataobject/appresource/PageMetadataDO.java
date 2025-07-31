package com.cmsr.onebase.module.app.dal.dataobject.appresource;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @ClassName PageMetadataDO
 * @Description TODO
 * @Author mickey
 * @Date 2025/7/30 14:51
 */
@Table(name="app_page_metadata")
@Data
@EqualsAndHashCode(callSuper = true)
public class PageMetadataDO extends TenantBaseDO {

    @Column(name = "page_id", columnDefinition= "BIGINT NOT NULL", comment = "页面ID")
    private Long pageId;

    @Column(name = "metadata", columnDefinition= "VARCHAR(255) NOT NULL", comment = "页面元数据")
    private String metadata;

}
