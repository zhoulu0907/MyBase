package com.cmsr.onebase.module.system.dal.flex.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 *  表定义层。
 *
 * @author xiaoc
 * @since 2025-12-22
 */
public class SystemConfigTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public static final SystemConfigTableDef SYSTEM_CONFIG = new SystemConfigTableDef();

    
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 名称
     */
    public final QueryColumn NAME = new QueryColumn(this, "name");

    /**
     * 应用id
     */
    public final QueryColumn APP_ID = new QueryColumn(this, "app_id");

    /**
     * 企业id
     */
    public final QueryColumn CORP_ID = new QueryColumn(this, "corp_id");

    
    public final QueryColumn REMARK = new QueryColumn(this, "remark");

    /**
     * 状态，启用，禁用
     */
    public final QueryColumn STATUS = new QueryColumn(this, "status");

    
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    /**
     * 空间id
     */
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    /**
     * 类型编码
     */
    public final QueryColumn CONFIG_KEY = new QueryColumn(this, "config_key");

    /**
     * 配置类型 global/tenant/corp/app
     */
    public final QueryColumn CONFIG_TYPE = new QueryColumn(this, "config_type");

    
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 值
     */
    public final QueryColumn CONFIG_VALUE = new QueryColumn(this, "config_value");

    
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    /**
     * 互斥项code
     */
    public final QueryColumn EXCLUSIVE_ITEM = new QueryColumn(this, "exclusive_item");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, CONFIG_TYPE, NAME, CONFIG_KEY, CONFIG_VALUE, TENANT_ID, CORP_ID, STATUS, REMARK, LOCK_VERSION, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, EXCLUSIVE_ITEM, APP_ID};

    public SystemConfigTableDef() {
        super("", "system_config");
    }

    private SystemConfigTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SystemConfigTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SystemConfigTableDef("", "system_config", alias));
    }

}
