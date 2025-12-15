package com.cmsr.onebase.module.system.dal.dataobject.config;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(name = "system_general_config")
public class SystemGeneralConfigDO extends TenantBaseDO {


    public static final String CATEGORY = "category";
    public static final String NAME = "name";
    public static final String CONFIG_KEY = "config_key";
    public static final String CONFIG_VALUE = "config_value";
    public static final String CORP_ID = "corp_id";
    public static final String STATUS = "status";
    public static final String REMARK = "remark";

    /**
     * 参数分类
     */
    @Column(name = CATEGORY)
    private String category;
    /**
     * 参数名称
     */
    @Column(name = NAME)
    private String name;
    /**
     * 参数键名
     *
     */
    @Column(name = CONFIG_KEY)
    private String configKey;
    /**
     * 参数键值
     */
    @Column(name = CONFIG_VALUE)
    private String configValue;
    /**
     * 参数类型
     *
     * 枚举
     */
    @Column(name = STATUS)
    private Integer status;

    /**
     * 归属企业ID
     */
    @Column(name = CORP_ID)
    private Long corpId;

    /**
     * 备注
     */
    @Column(name = REMARK)
    private String remark;
}
