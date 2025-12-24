package com.cmsr.onebase.module.app.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 应用管理表 表定义层。
 *
 * @author HuangJie
 * @since 2025-12-13
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


    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    /**
     * 状态（1上线、0下线）
     */
    public final QueryColumn APP_STATUS = new QueryColumn(this, "app_status");


    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");


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
     * 发布状态，0从未发布 1发布过
     */
    public final QueryColumn PUBLISH_STATUS = new QueryColumn(this, "publish_status");


    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, APP_UID, APP_NAME, APP_CODE, APP_MODE, APP_STATUS, VERSION_URL, DESCRIPTION, LOCK_VERSION, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID, PUBLISH_MODEL, PUBLISH_STATUS};

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
