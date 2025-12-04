package com.cmsr.onebase.module.app.api.security.bo;

import lombok.Data;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * @Author：huangjie
 * @Date：2025/10/20 16:54
 */
@Data
public class DataPermissionFilter {

    /**
     * 数据权限字段ID
     */
    private String fieldUuid;

    /**
     * 数据权限字段操作符，比如：等于 不等于
     */
    private String fieldOperator;

    /**
     * 公式 静态值 变量
     */
    private String fieldValueType;

    /**
     * 数据权限字段值
     */
    private String fieldValue;

    //TODO 为了兼容暂时的
    @Deprecated
    public Long getFieldId() {
        return fieldUuid == null ? null : NumberUtils.toLong(fieldUuid);
    }
}
