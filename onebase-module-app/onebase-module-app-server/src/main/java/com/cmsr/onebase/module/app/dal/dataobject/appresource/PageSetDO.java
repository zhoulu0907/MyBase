package com.cmsr.onebase.module.app.dal.dataobject.appresource;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Table(name="app_resource_pageset")
@Data
@EqualsAndHashCode(callSuper = true)
public class PageSetDO extends TenantBaseDO {

    @Column(name = "pageset_code", columnDefinition= "VARCHAR(255) NOT NULL", comment = "页面集编码")
    private String pageSetCode;

    @Column(name = "menu_code", columnDefinition= "VARCHAR(255) NOT NULL", comment = "菜单编码")
    private String menuCode;

    @Column(name = "pageset_name", columnDefinition= "VARCHAR(255) NOT NULL", comment = "页面集名称")
    private String pageSetName;

    @Column(name = "display_name", columnDefinition= "VARCHAR(255) NOT NULL", comment = "页面集显示名称")
    private String displayName;

    @Column(name = "description", columnDefinition= "TEXT", comment = "页面集描述")
    private String description;

}
