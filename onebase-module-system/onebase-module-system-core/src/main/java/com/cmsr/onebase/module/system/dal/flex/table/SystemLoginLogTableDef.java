package com.cmsr.onebase.module.system.dal.flex.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 系统访问记录 表定义层。
 *
 * @author matianyu
 * @since 2025-12-22
 */
public class SystemLoginLogTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 系统访问记录
     */
    public static final SystemLoginLogTableDef SYSTEM_LOGIN_LOG = new SystemLoginLogTableDef();

    /**
     * 访问ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 登陆结果
     */
    public final QueryColumn RESULT = new QueryColumn(this, "result");

    /**
     * 用户编号
     */
    public final QueryColumn USER_ID = new QueryColumn(this, "user_id");

    /**
     * 用户 IP
     */
    public final QueryColumn USER_IP = new QueryColumn(this, "user_ip");

    /**
     * 创建者
     */
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    /**
     * 是否删除
     */
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    /**
     * 日志类型
     */
    public final QueryColumn LOG_TYPE = new QueryColumn(this, "log_type");

    /**
     * 链路追踪编号
     */
    public final QueryColumn TRACE_ID = new QueryColumn(this, "trace_id");

    /**
     * 更新者
     */
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    /**
     * 租户编号
     */
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    /**
     * 用户类型
     */
    public final QueryColumn USER_TYPE = new QueryColumn(this, "user_type");

    /**
     * 用户账号
     */
    public final QueryColumn USERNAME = new QueryColumn(this, "username");

    /**
     * 浏览器 UA
     */
    public final QueryColumn USER_AGENT = new QueryColumn(this, "user_agent");

    /**
     * 创建时间
     */
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, LOG_TYPE, TRACE_ID, USER_ID, USER_TYPE, USERNAME, RESULT, USER_IP, USER_AGENT, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID};

    public SystemLoginLogTableDef() {
        super("", "system_login_log");
    }

    private SystemLoginLogTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SystemLoginLogTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SystemLoginLogTableDef("", "system_login_log", alias));
    }

}
