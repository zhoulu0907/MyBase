package com.cmsr.onebase.module.app.dal.dataobject.appresource;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Table(name="app_resource_page_ref_router")
@Data
@EqualsAndHashCode(callSuper = true)
public class PageRefRouterDO extends TenantBaseDO {

    /**
     * 页面编码
     */
    @Column(name = "page_code", columnDefinition = "VARCHAR(255) NOT NULL", comment = "页面编码")
    private String pageCode;

    @Column(name = "router_name", columnDefinition= "VARCHAR(255) NOT NULL", comment = "路由名称")
    private String routerName;

    @Column(name = "router_type", columnDefinition= "VARCHAR(255) NOT NULL", comment = "路由类型")
    private String routerType;

}
