package com.cmsr.onebase.module.app.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 *  表定义层。
 *
 * @author HuangJie
 * @since 2025-12-01
 */
public class AppResourcePageTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public static final AppResourcePageTableDef APP_RESOURCE_PAGE = new AppResourcePageTableDef();

    
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

    
    public final QueryColumn PAGE_UUID = new QueryColumn(this, "page_uuid");

    
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    
    public final QueryColumn ROUTER_NAME = new QueryColumn(this, "router_name");

    
    public final QueryColumn ROUTER_PATH = new QueryColumn(this, "router_path");

    
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    
    public final QueryColumn VERSION_TAG = new QueryColumn(this, "version_tag");

    
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    
    public final QueryColumn PAGESET_UUID = new QueryColumn(this, "pageset_uuid");

    /**
     * 编辑模式
     */
    public final QueryColumn EDIT_VIEW_MODE = new QueryColumn(this, "edit_view_mode");

    
    public final QueryColumn MAIN_METADATA = new QueryColumn(this, "main_metadata");

    
    public final QueryColumn APPLICATION_ID = new QueryColumn(this, "application_id");

    /**
     * 详情模式
     */
    public final QueryColumn DETAIL_VIEW_MODE = new QueryColumn(this, "detail_view_mode");

    
    public final QueryColumn BACKGROUND_COLOR = new QueryColumn(this, "background_color");

    /**
     * 最新更新的视图
     */
    public final QueryColumn IS_LATEST_UPDATED = new QueryColumn(this, "is_latest_updated");

    
    public final QueryColumn ROUTER_META_TITLE = new QueryColumn(this, "router_meta_title");

    /**
     * 视图规则
     */
    public final QueryColumn INTERACTION_RULES = new QueryColumn(this, "interaction_rules");

    /**
     * 是否默认编辑视图
     */
    public final QueryColumn IS_DEFAULT_EDIT_VIEW_MODE = new QueryColumn(this, "is_default_edit_view_mode");

    /**
     * 是否默认详情视图
     */
    public final QueryColumn IS_DEFAULT_DETAIL_VIEW_MODE = new QueryColumn(this, "is_default_detail_view_mode");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, PAGE_UUID, APPLICATION_ID, VERSION_TAG, PAGESET_UUID, PAGE_NAME, PAGE_TYPE, TITLE, LAYOUT, WIDTH, MARGIN, BACKGROUND_COLOR, MAIN_METADATA, ROUTER_PATH, ROUTER_NAME, ROUTER_META_TITLE, EDIT_VIEW_MODE, DETAIL_VIEW_MODE, IS_DEFAULT_EDIT_VIEW_MODE, IS_DEFAULT_DETAIL_VIEW_MODE, IS_LATEST_UPDATED, INTERACTION_RULES, CREATE_TIME, UPDATE_TIME, CREATOR, UPDATER, DELETED, LOCK_VERSION, TENANT_ID};

    public AppResourcePageTableDef() {
        super("", "app_resource_page");
    }

    private AppResourcePageTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public AppResourcePageTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new AppResourcePageTableDef("", "app_resource_page", alias));
    }

}
