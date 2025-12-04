package com.cmsr.onebase.module.app.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseBizEntity;
import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 这个类是工作台页面的全局配置
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "app_resource_workbench_page")
public class AppResourceWorkbenchPageDO extends BaseBizEntity {

    @Column("page_uuid")
    private String pageUuid;

    @Column("pageset_uuid")
    private String pageSetUuid;

    @Column(value = "edit_view_mode", comment = "编辑模式")
    private Integer editViewMode;

    @Column(value = "detail_view_mode", comment = "详情模式")
    private Integer detailViewMode;

    @Column(value = "is_default_edit_view_mode", comment = "是否默认编辑视图")
    private Integer isDefaultEditViewMode;

    @Column(value = "is_default_detail_view_mode", comment = "是否默认详情视图")
    private Integer isDefaultDetailViewMode;

    @Column(value = "is_latest_updated", comment = "最新更新的视图")
    private Integer isLatestUpdated;

    @Column(value = "page_name", comment = "页面名称")
    private String pageName;

    @Column(value = "page_type", comment = "页面类型 workbench")
    private String pageType;

    /**
     * 页面标题
     */
    @Column(value = "title", comment = "页面标题")
    private String title;

    /**
     * 页面布局方式，vertical（垂直布局）、horizontal（水平布局）
     */
    @Column(value = "layout", comment = "页面布局方式")
    private String layout;

    /**
     * 页面宽度，auto 或固定宽度
     */
    @Column(value = "width", comment = "页面宽度")
    private String width;

    /**
     * 页面外边距，默认 0
     */
    @Column(value = "margin", comment = "页面外边距")
    private String margin;

    /**
     * 页面背景色，默认 #fff
     */
    @Column(value = "background_color", comment = "页面背景色")
    private String backgroundColor;

    // TODO(mickey): remove
    /**
     * 页面主元数据
     */
    @Column(value = "main_metadata", comment = "页面主元数据")
    private String mainMetadata;

    /**
     * 路由路径
     * 例如: /user/profile
     */
    @Column(value = "router_path", comment = "路由路径")
    private String routerPath;

    /**
     * 路由名称
     * 例如: user-profile
     */
    @Column(value = "router_name", comment = "路由名称")
    private String routerName;

    /**
     * 路由元数据-页面标题
     * 例如: 用户资料 - 个人中心
     */
    @Column(value = "router_meta_title", comment = "路由元数据-页面标题")
    private String routerMetaTitle;
}