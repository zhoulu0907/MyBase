package com.cmsr.onebase.module.app.api.security.bo;

import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/10/20 17:07
 */
@Data
public class FieldPermissionItem {

    /**
     * 字段ID
     */
    private Long fieldId;

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
}
