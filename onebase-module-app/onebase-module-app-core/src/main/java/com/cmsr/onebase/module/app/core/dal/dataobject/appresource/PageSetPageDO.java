package com.cmsr.onebase.module.app.core.dal.dataobject.appresource;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Table(name = "app_resource_pageset_page")
@Data
@EqualsAndHashCode(callSuper = true)
public class PageSetPageDO extends TenantBaseDO {
    public static final String PAGE_SET_ID = "pageset_id";
    public static final String PAGE_ID = "page_id";

    @Column(name = "pageset_id", columnDefinition = "BIGINT NOT NULL", nullable = false, comment = "页面集ID")
    private Long pageSetId;

    @Column(name = "page_id", columnDefinition = "BIGINT NOT NULL", nullable = false, comment = "页面ID")
    private Long pageId;

    @Column(name = "page_type", columnDefinition = "VARCHAR(64) NOT NULL", comment = "页面类型 list edit detail")
    private String pageType;

    @Column(name = "is_default", columnDefinition = "INT(2) NOT NULL", comment = "是否默认")
    private Integer isDefault;

    @Column(name = "default_seq", columnDefinition = "INTEGER NOT NULL", comment = "默认顺序")
    private Integer defaultSeq;
}
