package com.cmsr.onebase.module.app.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseBizEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Table(value = "app_resource_pageset_page")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppResourcePagesetPageDO extends BaseBizEntity {

    @Column("pageset_uuid")
    private String pageSetUuid;

    @Column("page_uuid")
    private String pageUuid;

    @Column(value = "page_type", comment = "页面类型 list edit detail")
    private String pageType;

    @Column(value = "is_default", comment = "是否默认")
    private Integer isDefault;

    @Column(value = "default_seq", comment = "默认顺序")
    private Integer defaultSeq;
}