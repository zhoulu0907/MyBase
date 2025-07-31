package com.cmsr.onebase.module.app.dal.dataobject.appresource;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Table(name="app_pageset_list_page_ref")
@Data
@EqualsAndHashCode(callSuper = true)
public class ListPageRefDO extends TenantBaseDO {

    @Column(name = "pageset_code", columnDefinition= "VARCHAR(255) NOT NULL", comment = "页面集编码")
    private String pageSetCode;

    @Column(name = "page_ref", columnDefinition= "VARCHAR(255) NOT NULL", comment = "页面编码")
    private String pageRef;

    @Column(name = "page_name", columnDefinition= "VARCHAR(255) NOT NULL", comment = "页面名称")
    private String pageName;

    @Column(name = "title", columnDefinition= "VARCHAR(255) NOT NULL", comment = "页面标题")
    private String title;

    @Column(name = "is_default", columnDefinition= "BOOLEAN NOT NULL", comment = "是否默认")
    private Boolean isDefault;

    @Column(name = "default_seq", columnDefinition= "INTEGER NOT NULL", comment = "默认顺序")
    private Integer defaultSeq;
}
