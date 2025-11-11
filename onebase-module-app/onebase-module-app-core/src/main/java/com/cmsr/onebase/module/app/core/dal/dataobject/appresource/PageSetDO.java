package com.cmsr.onebase.module.app.core.dal.dataobject.appresource;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Table(name = "app_resource_pageset")
@Data
@EqualsAndHashCode(callSuper = true)
public class PageSetDO extends TenantBaseDO {

    public static final String PAGE_SET_CODE = "pageset_code";
    public static final String MENU_ID = "menu_id";
    public static final String PAGESET_TYPE = "pageset_type";

    @Column(name = "pageset_code", columnDefinition = "VARCHAR(255) NOT NULL", nullable = false, comment = "页面集编码")
    private String pageSetCode;

    @Column(name = "menu_id", columnDefinition = "BIGINT NOT NULL", nullable = false, comment = "菜单编码")
    private Long menuId;

    @Column(name = "main_metadata", columnDefinition = "VARCHAR(255) NOT NULL", comment = "页面主元数据")
    private String mainMetadata;

    @Column(name = "pageset_name", columnDefinition = "VARCHAR(255) NOT NULL", comment = "页面集名称")
    private String pageSetName;

    @Column(name = "pageset_type", columnDefinition = "INT NOT NULL",  nullable = false, comment = "页面集类型 1-普通表单 2-流程表单 3-工作台")
    private Integer pageSetType;


    @Column(name = "display_name", columnDefinition = "VARCHAR(255) NOT NULL", comment = "页面集显示名称")
    private String displayName;

    @Column(name = "description", columnDefinition = "TEXT", comment = "页面集描述")
    private String description;

}
