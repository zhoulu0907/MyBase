package com.cmsr.onebase.module.system.dal.flex.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 短信渠道 表定义层。
 *
 * @author xiaoc
 * @since 2025-12-22
 */
public class SystemSmsChannelTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 短信渠道
     */
    public static final SystemSmsChannelTableDef SYSTEM_SMS_CHANNEL = new SystemSmsChannelTableDef();

    /**
     * 编号
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 渠道编码
     */
    public final QueryColumn CODE = new QueryColumn(this, "code");

    /**
     * 短信 API 的账号
     */
    public final QueryColumn API_KEY = new QueryColumn(this, "api_key");

    /**
     * 备注
     */
    public final QueryColumn REMARK = new QueryColumn(this, "remark");

    /**
     * 状态（0停用，1启用）
     */
    public final QueryColumn STATUS = new QueryColumn(this, "status");

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
     * 短信 API 的秘钥
     */
    public final QueryColumn API_SECRET = new QueryColumn(this, "api_secret");

    /**
     * 短信签名
     */
    public final QueryColumn SIGNATURE = new QueryColumn(this, "signature");

    /**
     * 创建时间
     */
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 短信发送回调 URL
     */
    public final QueryColumn CALLBACK_URL = new QueryColumn(this, "callback_url");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, SIGNATURE, CODE, STATUS, REMARK, API_KEY, API_SECRET, CALLBACK_URL, CREATOR, CREATE_TIME, UPDATER, UPDATE_TIME, DELETED};

    public SystemSmsChannelTableDef() {
        super("", "system_sms_channel");
    }

    private SystemSmsChannelTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SystemSmsChannelTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SystemSmsChannelTableDef("", "system_sms_channel", alias));
    }

}
