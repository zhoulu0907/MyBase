package com.cmsr.onebase.module.dashboard.build.dal.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 大屏模板表 表定义层。
 *
 * @author mty
 * @since 2025-12-29
 */
public class DashboardTemplateTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 大屏模板表
     */
    public static final DashboardTemplateTableDef DASHBOARD_TEMPLATE = new DashboardTemplateTableDef();

    /**
     * id标识
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 热门模板
     */
    public final QueryColumn HOT = new QueryColumn(this, "hot");

    /**
     * 应用标识
     */
    public final QueryColumn APP_ID = new QueryColumn(this, "app_id");

    /**
     * 模板内容
     */
    public final QueryColumn CONTENT = new QueryColumn(this, "content");

    /**
     * 创建者
     */
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    /**
     * 删除标识
     */
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    /**
     * 备注
     */
    public final QueryColumn REMARKS = new QueryColumn(this, "remarks");

    /**
     * 更新者
     */
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    /**
     * 创建时间
     */
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 图片地址
     */
    public final QueryColumn INDEX_IMAGE = new QueryColumn(this, "index_image");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 模板名称
     */
    public final QueryColumn TEMPLATE_NAME = new QueryColumn(this, "template_name");

    /**
     * 模板类型
     */
    public final QueryColumn TEMPLATE_TYPE = new QueryColumn(this, "template_type");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, CONTENT, TEMPLATE_TYPE, HOT, APP_ID, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, INDEX_IMAGE, TEMPLATE_NAME, REMARKS};

    public DashboardTemplateTableDef() {
        super("", "dashboard_template");
    }

    private DashboardTemplateTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public DashboardTemplateTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new DashboardTemplateTableDef("", "dashboard_template", alias));
    }

}
