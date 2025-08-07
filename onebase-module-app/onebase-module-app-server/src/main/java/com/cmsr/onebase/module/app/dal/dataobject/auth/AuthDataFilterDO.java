package com.cmsr.onebase.module.app.dal.dataobject.auth;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 数据权限配置-数据过滤条件
 *
 * @author lingma
 * @date 2025-07-25
 */
@Data
@Table(name = "app_auth_data_filter")
public class AuthDataFilterDO extends TenantBaseDO {

    /**
     * 数据权限组Id
     */
    @Column(name = "group_id", nullable = false, comment = "数据权限组Id")
    private Long groupId;

    /**
     * 字段名称
     */
    @Column(name = "field_name", nullable = false, length = 100, comment = "字段名称")
    private String fieldName;

    /**
     * 字段值类型
     */
    @Column(name = "field_value_type", nullable = false, length = 20, comment = "字段值类型")
    private String fieldValueType;

    /**
     * 比较操作符号
     */
    @Column(name = "field_operator", nullable = false, length = 20, comment = "比较操作符号")
    private String fieldOperator;

    /**
     * 字段值
     */
    @Column(name = "field_value", nullable = false, length = 100, comment = "字段值")
    private String fieldValue;

}