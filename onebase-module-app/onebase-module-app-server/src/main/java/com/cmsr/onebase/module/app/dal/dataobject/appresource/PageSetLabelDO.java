package com.cmsr.onebase.module.app.dal.dataobject.appresource;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Table(name="app_pageset_label")
@Data
@EqualsAndHashCode(callSuper = true)
public class PageSetLabelDO extends TenantBaseDO {

    @Column(name = "pageset_code", columnDefinition= "VARCHAR(255) NOT NULL", comment = "页面集编码")
    private String pagesetCode;

    @Column(name = "label_name", columnDefinition= "VARCHAR(255) NOT NULL", comment = "标签名称")
    private String labelName;

    @Column(name = "label_value", columnDefinition= "VARCHAR(255) NOT NULL", comment = "标签值")
    private String labelValue;
}
