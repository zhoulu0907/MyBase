package com.cmsr.onebase.module.app.dal.dataobject.appresource;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Table(name="app_pageset_detail_page_ref")
@Data
@EqualsAndHashCode(callSuper = true)
public class PageSetPageDO extends TenantBaseDO {

    @Column(name = "pageset_ref", columnDefinition= "VARCHAR(255) NOT NULL", comment = "页面集编码")
    private String pageSetRef;

    @Column(name = "pageset_type", columnDefinition= "VARCHAR(64) NOT NULL", comment = "页面类型 list edit detail")
    private String pageType;

    @Column(name = "page_ref", columnDefinition= "VARCHAR(255) NOT NULL", comment = "页面编码")
    private String pageRef;

    @Column(name = "is_default", columnDefinition= "BOOLEAN NOT NULL", comment = "是否默认")
    private Boolean isDefault;

    @Column(name = "default_seq", columnDefinition= "INTEGER NOT NULL", comment = "默认顺序")
    private Integer defaultSeq;
}
