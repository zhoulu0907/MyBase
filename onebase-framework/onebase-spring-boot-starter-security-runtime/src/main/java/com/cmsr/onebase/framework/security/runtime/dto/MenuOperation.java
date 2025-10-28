package com.cmsr.onebase.framework.security.runtime.dto;

import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/10/25 19:45
 */
@Data
public class MenuOperation {

    private boolean accessible = false;

    private boolean canCreate = false;

    private boolean canEdit = false;

    private boolean canDelete = false;

    private boolean canImport = false;

    private boolean canExport = false;

    private boolean canShare = false;

    public void allAllow() {
        this.canCreate = true;
        this.canEdit = true;
        this.canDelete = true;
        this.canImport = true;
        this.canExport = true;
        this.canShare = true;
    }

    public void allDeny() {
        this.canCreate = false;
        this.canEdit = false;
        this.canDelete = false;
        this.canImport = false;
        this.canExport = false;
        this.canShare = false;
    }

}
