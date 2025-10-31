package com.cmsr.onebase.module.app.api.security.bo;

import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/10/25 19:45
 */
@Data
public class OperationPermission {

    private boolean pageAllowed = false;

    private boolean allViewsAllowed = false;

    private boolean allFieldsAllowed = false;

    private boolean canCreate = false;

    private boolean canEdit = false;

    private boolean canDelete = false;

    private boolean canImport = false;

    private boolean canExport = false;

    private boolean canShare = false;

    public void allAllow() {
        pageAllowed = true;
        allViewsAllowed = true;
        allFieldsAllowed = true;
        canCreate = true;
        canEdit = true;
        canDelete = true;
        canImport = true;
        canExport = true;
        canShare = true;
        return;
    }

    public void allDeny() {
        pageAllowed = false;
        allViewsAllowed = false;
        allFieldsAllowed = false;
        canCreate = false;
        canEdit = false;
        canDelete = false;
        canImport = false;
        canExport = false;
        canShare = false;
    }


}
