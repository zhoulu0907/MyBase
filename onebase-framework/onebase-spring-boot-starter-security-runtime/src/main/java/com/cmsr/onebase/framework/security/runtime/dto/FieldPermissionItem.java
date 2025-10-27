package com.cmsr.onebase.framework.security.runtime.dto;

import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/10/20 17:07
 */
@Data
public class FieldPermissionItem {

    private Long fieldId;

    private boolean canRead;

    private boolean canEdit;

    private boolean canDownload;
}
