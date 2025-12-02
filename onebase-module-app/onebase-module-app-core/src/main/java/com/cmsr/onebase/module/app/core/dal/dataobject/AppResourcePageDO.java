package com.cmsr.onebase.module.app.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseBizEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @ClassName AppResourcePageDO
 * @Description TODO
 * @Author mickey
 * @Date 2025/7/30 14:51
 */
@Table(value = "app_resource_page")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppResourcePageDO extends BaseBizEntity {
    @Column(value = "page_uuid")
    private String pageUuid;

    @Column(value = "pageset_uuid")
    private String pageSetUuid;

    @Column(value = "page_name")
    private String pageName;

    @Column(value = "page_type")
    private String pageType;

    @Column(value = "title")
    private String title;

    @Column(value = "layout")
    private String layout;

    @Column(value = "width")
    private String width;

    @Column(value = "margin")
    private String margin;

    @Column(value = "background_color")
    private String backgroundColor;

    @Column(value = "main_metadata")
    private String mainMetadata;

    @Column(value = "router_path")
    private String routerPath;

    @Column(value = "router_name")
    private String routerName;

    @Column(value = "router_meta_title")
    private String routerMetaTitle;

    @Column(value = "edit_view_mode")
    private Integer editViewMode;

    @Column(value = "detail_view_mode")
    private Integer detailViewMode;

    @Column(value = "is_default_edit_view_mode")
    private Integer isDefaultEditViewMode;

    @Column(value = "is_default_detail_view_mode")
    private Integer isDefaultDetailViewMode;

    @Column(value = "is_latest_updated")
    private Integer isLatestUpdated;

    @Column(value = "interaction_rules")
    private String interactionRules;
}
