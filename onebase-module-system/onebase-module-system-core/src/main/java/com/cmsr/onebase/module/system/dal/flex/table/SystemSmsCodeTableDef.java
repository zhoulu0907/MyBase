package com.cmsr.onebase.module.system.dal.flex.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 手机验证码 表定义层。
 *
 * @author matianyu
 * @since 2025-12-22
 */
public class SystemSmsCodeTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 手机验证码
     */
    public static final SystemSmsCodeTableDef SYSTEM_SMS_CODE = new SystemSmsCodeTableDef();

    /**
     * 编号
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 验证码
     */
    public final QueryColumn CODE = new QueryColumn(this, "code");

    /**
     * 是否使用
     */
    public final QueryColumn USED = new QueryColumn(this, "used");

    /**
     * 发送场景
     */
    public final QueryColumn SCENE = new QueryColumn(this, "scene");

    /**
     * 手机号
     */
    public final QueryColumn MOBILE = new QueryColumn(this, "mobile");

    /**
     * 使用 IP
     */
    public final QueryColumn USED_IP = new QueryColumn(this, "used_ip");

    /**
     * 创建者
     */
    public final QueryColumn CREATOR = new QueryColumn(this, "creator");

    /**
     * 是否删除
     */
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    /**
     * 更新者
     */
    public final QueryColumn UPDATER = new QueryColumn(this, "updater");

    /**
     * 创建 IP
     */
    public final QueryColumn CREATE_IP = new QueryColumn(this, "create_ip");

    /**
     * 租户编号
     */
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    /**
     * 使用时间
     */
    public final QueryColumn USED_TIME = new QueryColumn(this, "used_time");

    /**
     * 创建时间
     */
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 今日发送的第几条
     */
    public final QueryColumn TODAY_INDEX = new QueryColumn(this, "today_index");

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
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, MOBILE, CODE, CREATE_IP, SCENE, TODAY_INDEX, USED, USED_TIME, USED_IP, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED, TENANT_ID};

    public SystemSmsCodeTableDef() {
        super("", "system_sms_code");
    }

    private SystemSmsCodeTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SystemSmsCodeTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SystemSmsCodeTableDef("", "system_sms_code", alias));
    }

}
