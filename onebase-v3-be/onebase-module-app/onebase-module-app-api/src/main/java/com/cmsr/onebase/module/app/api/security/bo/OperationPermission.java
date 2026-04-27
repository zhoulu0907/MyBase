package com.cmsr.onebase.module.app.api.security.bo;

import lombok.Data;

/**
 * @Description: 界面配置的功能权限
 * @Author：huangjie
 * @Date：2025/10/25 19:45
 */
@Data
public class OperationPermission {

    /**
     * 页面是否可访问
     */
    private boolean pageAllowed = false;

    /**
     * 所有视图是否可访问
     */
    private boolean allViewsAllowed = false;

    /**
     * 所有字段是否可访问
     */
    private boolean allFieldsAllowed = false;

    /**
     * 是否可创建
     */
    private boolean canCreate = false;

    /**
     * 是否可编辑
     */
    private boolean canEdit = false;

    /**
     * 是否可删除
     */
    private boolean canDelete = false;

    /**
     * 是否可导入
     */
    private boolean canImport = false;

    /**
     * 是否可导出
     */
    private boolean canExport = false;

    /**
     * 是否可分享
     */
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
