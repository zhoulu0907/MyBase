package com.cmsr.onebase.framework.security.runtime;

import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/10/20 17:07
 */
@Data
public class FieldPermission {

    private Long fieldId;

    private boolean canRead;

    private boolean canEdit;

    private boolean danDownload;
}
