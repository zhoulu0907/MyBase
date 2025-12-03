package com.cmsr.onebase.module.app.api.security.bo;

import lombok.Data;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * @Author：huangjie
 * @Date：2025/10/20 17:07
 */
@Data
public class FieldPermissionItem {

    /**
     * 字段ID
     */
    private String fieldUuid;

    /**
     * 可读
     */
    private boolean canRead;

    /**
     * 可编辑
     */
    private boolean canEdit;

    /**
     * 可下载
     */
    private boolean canDownload;

    //TODO 为了兼容暂时的
    @Deprecated
    public Long getFieldId() {
        return fieldUuid == null ? null : NumberUtils.toLong(fieldUuid);
    }
}
