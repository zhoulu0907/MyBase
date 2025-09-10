package com.cmsr.onebase.module.app.core.dal.dataobject.appresource;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Table(name = "app_resource_pageset_label")
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class PageSetLabelDO extends TenantBaseDO {
    public static final String PAGE_SET_ID = "pageset_id";

    @Column(name = "pageset_id", columnDefinition = "BIGINT NOT NULL", nullable = false, comment = "页面集ID")
    private Long pageSetId;

    @Column(name = "label_name", columnDefinition = "VARCHAR(255) NOT NULL", comment = "标签名称")
    private String labelName;

    @Column(name = "label_value", columnDefinition = "VARCHAR(255) NOT NULL", comment = "标签值")
    private String labelValue;
}
