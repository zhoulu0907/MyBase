package com.cmsr.onebase.module.app.core.dal.dataobject.appresource;

import com.cmsr.onebase.framework.orm.data.BaseTenantEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Table(value = "app_resource_pageset_page")
@Data
@EqualsAndHashCode(callSuper = true)
public class PageSetPageDO extends BaseTenantEntity {

    @Column(value = "pageset_id", comment = "页面集ID")
    private Long pageSetId;

    @Column(value = "page_id", comment = "页面ID")
    private Long pageId;

    @Column(value = "page_type", comment = "页面类型 list edit detail")
    private String pageType;

    @Column(value = "is_default", comment = "是否默认")
    private Integer isDefault;

    @Column(value = "default_seq", comment = "默认顺序")
    private Integer defaultSeq;
}
