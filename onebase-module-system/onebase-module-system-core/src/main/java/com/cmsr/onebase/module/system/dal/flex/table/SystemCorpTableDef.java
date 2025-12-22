package com.cmsr.onebase.module.system.dal.flex.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 企业基本信息表 表定义层。
 *
 * @author xiaoc
 * @since 2025-12-22
 */
public class SystemCorpTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 企业基本信息表
     */
    public static final SystemCorpTableDef SYSTEM_CORP = new SystemCorpTableDef();

    
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 企业状态(选项启用/停用)
     */
    public final QueryColumn STATUS = new QueryColumn(this, "status");

    /**
     * 联系地址
     */
    public final QueryColumn ADDRESS = new QueryColumn(this, "address");

    /**
     * 管理员
     */
    public final QueryColumn ADMIN_ID = new QueryColumn(this, "admin_id");

    
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    /**
     * 企业编码
     */
    public final QueryColumn CORP_CODE = new QueryColumn(this, "corp_code");

    /**
     * 企业logo
     */
    public final QueryColumn CORP_LOGO = new QueryColumn(this, "corp_logo");

    /**
     * 企业名称
     */
    public final QueryColumn CORP_NAME = new QueryColumn(this, "corp_name");

    
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    /**
     * 用户数量上限
     */
    public final QueryColumn USER_LIMIT = new QueryColumn(this, "user_limit");

    
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    
    public final QueryColumn LOCK_VERSION = new QueryColumn(this, "lock_version");

    /**
     * 行业类型
     */
    public final QueryColumn INDUSTRY_TYPE = new QueryColumn(this, "industry_type");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, CORP_CODE, CORP_NAME, INDUSTRY_TYPE, STATUS, ADDRESS, ADMIN_ID, USER_LIMIT, LOCK_VERSION, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID, CORP_LOGO};

    public SystemCorpTableDef() {
        super("", "system_corp");
    }

    private SystemCorpTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SystemCorpTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SystemCorpTableDef("", "system_corp", alias));
    }

}
