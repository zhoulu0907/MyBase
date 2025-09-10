package com.cmsr.onebase.module.app.core.dal.dataobject.appresource;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Table(name = "app_resource_page_ref_router")
@Data
@EqualsAndHashCode(callSuper = true)
public class PageRefRouterDO extends TenantBaseDO {
    public static final String PAGE_ID = "page_id";

    @Column(name = "page_id", columnDefinition = "BIGINT NOT NULL", nullable = false, comment = "页面ID")
    private Long pageId;

    @Column(name = "router_name", columnDefinition = "VARCHAR(255) NOT NULL", comment = "路由名称")
    private String routerName;

    @Column(name = "router_type", columnDefinition = "VARCHAR(255) NOT NULL", comment = "路由类型")
    private String routerType;

}
