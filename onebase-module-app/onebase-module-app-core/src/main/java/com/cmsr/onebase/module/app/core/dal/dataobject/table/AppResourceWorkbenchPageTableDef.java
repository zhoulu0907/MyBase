package com.cmsr.onebase.module.app.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 *  表定义层。
 *
 * @author v1endr3
 * @since 2025-11-25
 */
public class AppResourceWorkbenchPageTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public static final AppResourceWorkbenchPageTableDef APP_RESOURCE_WORKBENCH_PAGE = new AppResourceWorkbenchPageTableDef();

    
    public final QueryColumn ID = new QueryColumn(this, "id");

    
    public final QueryColumn TITLE = new QueryColumn(this, "title");

    
    public final QueryColumn WIDTH = new QueryColumn(this, "width");

    
    public final QueryColumn LAYOUT = new QueryColumn(this, "layout");

    
    public final QueryColumn MARGIN = new QueryColumn(this, "margin");

    
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    
    public final QueryColumn PAGE_NAME = new QueryColumn(this, "page_name");

    
    public final QueryColumn PAGE_TYPE = new QueryColumn(this, "page_type");

    
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    
    public final QueryColumn PAGESET_ID = new QueryColumn(this, "pageset_id");

    
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    
    public final QueryColumn ROUTER_NAME = new QueryColumn(this, "router_name");

    
    public final QueryColumn ROUTER_PATH = new QueryColumn(this, "router_path");

    
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    
    public final QueryColumn EDIT_VIEW_MODE = new QueryColumn(this, "edit_view_mode");

    
    public final QueryColumn MAIN_METADATA = new QueryColumn(this, "main_metadata");

    
    public final QueryColumn DETAIL_VIEW_MODE = new QueryColumn(this, "detail_view_mode");

    
    public final QueryColumn BACKGROUND_COLOR = new QueryColumn(this, "background_color");

    
    public final QueryColumn IS_LATEST_UPDATED = new QueryColumn(this, "is_latest_updated");

    
    public final QueryColumn ROUTER_META_TITLE = new QueryColumn(this, "router_meta_title");

    
    public final QueryColumn IS_DEFAULT_EDIT_VIEW_MODE = new QueryColumn(this, "is_default_edit_view_mode");

    
    public final QueryColumn IS_DEFAULT_DETAIL_VIEW_MODE = new QueryColumn(this, "is_default_detail_view_mode");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{PAGESET_ID, PAGE_NAME, PAGE_TYPE, TITLE, LAYOUT, WIDTH, MARGIN, BACKGROUND_COLOR, MAIN_METADATA, ROUTER_PATH, ROUTER_NAME, ROUTER_META_TITLE, TENANT_ID, ID, CREATE_TIME, UPDATE_TIME, CREATOR, UPDATER, DELETED, LOCK_VERSION, EDIT_VIEW_MODE, DETAIL_VIEW_MODE, IS_DEFAULT_EDIT_VIEW_MODE, IS_DEFAULT_DETAIL_VIEW_MODE, IS_LATEST_UPDATED};

    public AppResourceWorkbenchPageTableDef() {
        super("", "app_resource_workbench_page");
    }

    private AppResourceWorkbenchPageTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public AppResourceWorkbenchPageTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new AppResourceWorkbenchPageTableDef("", "app_resource_workbench_page", alias));
    }

}
