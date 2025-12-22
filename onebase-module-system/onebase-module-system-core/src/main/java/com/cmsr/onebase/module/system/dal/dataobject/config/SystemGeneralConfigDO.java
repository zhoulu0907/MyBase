package com.cmsr.onebase.module.system.dal.dataobject.config;

import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.annotation.Column;
import lombok.Data;

@Data
@Table(value = "system_config")
public class SystemGeneralConfigDO extends BaseTenantEntity {

    public static final String CONFIG_TYPE = "config_type";
    public static final String NAME        = "name";
    public static final String CONFIG_KEY = "config_key";
    public static final String CONFIG_VALUE = "config_value";
    public static final String CORP_ID = "corp_id";
    public static final String APP_ID = "app_id";
    public static final String STATUS = "status";
    public static final String REMARK = "remark";
    public static final String EXCLUSIVE_ITEM = "exclusive_item";

    /**
     * 参数分类
     */
    @Column(value = CONFIG_TYPE)
    private String configType;
    /**
     * 参数名称
     */
    @Column(value = NAME)
    private String name;
    /**
     * 参数键名
     *
     */
    @Column(value = CONFIG_KEY)
    private String configKey;
    /**
     * 参数键值
     */
    @Column(value = CONFIG_VALUE)
    private Object configValue;

    /**
     * 互斥项
     */
    @Column(value = EXCLUSIVE_ITEM)
    private String exclusiveItem;

    /**
     * 参数类型
     *
     * 枚举
     */
    @Column(value = STATUS)
    private Integer status;

    /**
     * 归属企业ID
     */
    @Column(value = CORP_ID)
    private Long corpId;

    /**
     * appId
     */
    @Column(value = APP_ID)
    private Long appId;

    /**
     * 备注
     */
    @Column(value = REMARK)
    private String remark;
}
