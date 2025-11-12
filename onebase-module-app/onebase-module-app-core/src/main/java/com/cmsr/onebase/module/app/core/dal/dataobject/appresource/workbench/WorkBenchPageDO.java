package com.cmsr.onebase.module.app.core.dal.dataobject.appresource.workbench;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 这个类是工作台页面的全局配置
 *
 */
@Table(name = "app_resource_workbench_page")
@Data
@EqualsAndHashCode(callSuper = true)
public class WorkBenchPageDO extends TenantBaseDO {
    public static final String PAGE_SET_ID = "pageset_id";

    @Column(name = "pageset_id", columnDefinition = "BIGINT NOT NULL", nullable = false, comment = "页面集ID")
    private Long pageSetId;

    @Column(name = "page_name", columnDefinition = "VARCHAR(255) NOT NULL", comment = "页面名称")
    private String pageName;

    @Column(name = "page_type", columnDefinition = "VARCHAR(64) NOT NULL", comment = "页面类型 workbench")
    private String pageType;

    @Column(name = "edit_view_mode", columnDefinition = "INT(2)", comment = "编辑模式" )
    private Integer editViewMode;

    @Column(name = "detail_view_mode", columnDefinition = "INT(2)", comment = "详情模式" )
    private Integer detailViewMode;

    @Column(name = "is_default_edit_view_mode", columnDefinition = "INT(2)", comment = "是否默认编辑视图")
    private Integer isDefaultEditViewMode;

    @Column(name = "is_default_detail_view_mode", columnDefinition = "INT(2)", comment = "是否默认详情视图")
    private Integer isDefaultDetailViewMode;

    @Column(name = "is_latest_updated", columnDefinition = "INT(2)", comment = "最新更新的视图")
    private Integer isLatestUpdated;

    /**
     * 页面标题
     */
    @Column(name = "title", columnDefinition = "VARCHAR(255) NOT NULL", comment = "页面标题")
    private String title;

    /**
     * 页面布局方式，vertical（垂直布局）、horizontal（水平布局）
     */
    @Column(name = "layout", columnDefinition = "VARCHAR(255) NOT NULL", comment = "页面布局方式")
    private String layout;

    /**
     * 页面宽度，auto 或固定宽度
     */
    @Column(name = "width", columnDefinition = "VARCHAR(255) NOT NULL", comment = "页面宽度")
    private String width;

    /**
     * 页面外边距，默认 0
     */
    @Column(name = "margin", columnDefinition = "VARCHAR(255) NOT NULL", comment = "页面外边距")
    private String margin;

    /**
     * 页面背景色，默认 #fff
     */
    @Column(name = "background_color", columnDefinition = "VARCHAR(255) NOT NULL", comment = "页面背景色")
    private String backgroundColor;

    // TODO(mickey): remove
    /**
     * 页面主元数据
     */
    @Column(name = "main_metadata", columnDefinition = "VARCHAR(255) NOT NULL", comment = "页面主元数据")
    private String mainMetadata;

    /**
     * 路由路径
     * 例如: /user/profile
     */
    @Column(name = "router_path", columnDefinition = "VARCHAR(255) NOT NULL", comment = "路由路径")
    private String routerPath;

    /**
     * 路由名称
     * 例如: user-profile
     */
    @Column(name = "router_name", columnDefinition = "VARCHAR(255) NOT NULL", comment = "路由名称")
    private String routerName;

    /**
     * 路由元数据-页面标题
     * 例如: 用户资料 - 个人中心
     */
    @Column(name = "router_meta_title", columnDefinition = "VARCHAR(255) NOT NULL", comment = "路由元数据-页面标题")
    private String routerMetaTitle;
}
