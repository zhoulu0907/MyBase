package com.cmsr.onebase.module.app.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Table(value = "app_resource_pageset")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppResourcePagesetDO extends BaseTenantEntity {

    @Column(value = "pageset_code", comment = "页面集编码")
    private String pageSetCode;

    @Column(value = "menu_id", comment = "菜单编码")
    private Long menuId;

    @Column(value = "main_metadata", comment = "页面主元数据")
    private String mainMetadata;

    @Column(value = "pageset_name", comment = "页面集名称")
    private String pageSetName;

    @Column(value = "pageset_type", comment = "页面集类型 1-普通表单 2-流程表单 3-工作台")
    private Integer pageSetType;


    @Column(value = "display_name", comment = "页面集显示名称")
    private String displayName;

    @Column(value = "description", comment = "页面集描述")
    private String description;

}
