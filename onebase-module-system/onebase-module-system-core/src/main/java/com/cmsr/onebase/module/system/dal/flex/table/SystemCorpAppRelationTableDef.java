package com.cmsr.onebase.module.system.dal.flex.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 企业应用关联表 表定义层。
 *
 * @author xiaoc
 * @since 2025-12-22
 */
public class SystemCorpAppRelationTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 企业应用关联表
     */
    public static final SystemCorpAppRelationTableDef SYSTEM_CORP_APP_RELATION = new SystemCorpAppRelationTableDef();

    
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 企业id
     */
    public final QueryColumn CORP_ID = new QueryColumn(this, "corp_id");

    /**
     * 状态
     */
    public final QueryColumn STATUS = new QueryColumn(this, "status");

    
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    /**
     * 空间id
     */
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 过期日期
     */
    public final QueryColumn EXPIRES_TIME = new QueryColumn(this, "expires_time");

    /**
     * 锁标识
     */
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    /**
     * 应用id
     */
    public final QueryColumn APPLICATION_ID = new QueryColumn(this, "application_id");

    /**
     * 授权时间
     */
    public final QueryColumn AUTHORIZATION_TIME = new QueryColumn(this, "authorization_time");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, CORP_ID, APPLICATION_ID, TENANT_ID, LOCK_VERSION, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, EXPIRES_TIME, AUTHORIZATION_TIME, STATUS};

    public SystemCorpAppRelationTableDef() {
        super("", "system_corp_app_relation");
    }

    private SystemCorpAppRelationTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SystemCorpAppRelationTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SystemCorpAppRelationTableDef("", "system_corp_app_relation", alias));
    }

}
