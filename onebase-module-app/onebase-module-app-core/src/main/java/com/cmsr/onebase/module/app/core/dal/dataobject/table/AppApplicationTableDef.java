package com.cmsr.onebase.module.app.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 应用管理表 表定义层。
 *
 * @author HuangJie
 * @since 2025-11-26
 */
public class AppApplicationTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 应用管理表
     */
    public static final AppApplicationTableDef APP_APPLICATION = new AppApplicationTableDef();

    /**
     * 主键ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 应用uid(自动生成短码)
     */
    public final QueryColumn APP_UID = new QueryColumn(this, "app_uid");

    /**
     * 应用编码(用户输入)
     */
    public final QueryColumn APP_CODE = new QueryColumn(this, "app_code");

    /**
     * 应用模式
     */
    public final QueryColumn APP_MODE = new QueryColumn(this, "app_mode");

    /**
     * 应用名称
     */
    public final QueryColumn APP_NAME = new QueryColumn(this, "app_name");

    
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    /**
     * 应用图标
     */
    public final QueryColumn ICON_NAME = new QueryColumn(this, "icon_name");

    
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    /**
     * 状态（编辑、发布）
     */
    public final QueryColumn APP_STATUS = new QueryColumn(this, "app_status");

    /**
     * 图标颜色
     */
    public final QueryColumn ICON_COLOR = new QueryColumn(this, "icon_color");

    
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 主题颜色
     */
    public final QueryColumn THEME_COLOR = new QueryColumn(this, "theme_color");

    
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    
    public final QueryColumn VERSION_URL = new QueryColumn(this, "version_url");

    /**
     * 应用描述
     */
    public final QueryColumn DESCRIPTION = new QueryColumn(this, "description");

    
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    /**
     * 内部模式 inner，SaaS模式 saas
     */
    public final QueryColumn PUBLISH_MODEL = new QueryColumn(this, "publish_model");

    /**
     * 当前版本
     */
    public final QueryColumn VERSION_NUMBER = new QueryColumn(this, "version_number");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, APP_UID, APP_NAME, APP_CODE, APP_MODE, THEME_COLOR, ICON_NAME, ICON_COLOR, VERSION_NUMBER, APP_STATUS, VERSION_URL, DESCRIPTION, LOCK_VERSION, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID, PUBLISH_MODEL};

    public AppApplicationTableDef() {
        super("", "app_application");
    }

    private AppApplicationTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public AppApplicationTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new AppApplicationTableDef("", "app_application", alias));
    }

}
