package com.cmsr.onebase.module.app.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 应用标签管理表 表定义层。
 *
 * @author HuangJie
 * @since 2025-12-01
 */
public class AppApplicationTagTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 应用标签管理表
     */
    public static final AppApplicationTagTableDef APP_APPLICATION_TAG = new AppApplicationTagTableDef();

    /**
     * 主键Id
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 标签Id
     */
    public final QueryColumn TAG_ID = new QueryColumn(this, "tag_id");

    
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    /**
     * 应用Id
     */
    public final QueryColumn APPLICATION_ID = new QueryColumn(this, "application_id");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, APPLICATION_ID, TAG_ID, LOCK_VERSION, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID};

    public AppApplicationTagTableDef() {
        super("", "app_application_tag");
    }

    private AppApplicationTagTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public AppApplicationTagTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new AppApplicationTagTableDef("", "app_application_tag", alias));
    }

}
