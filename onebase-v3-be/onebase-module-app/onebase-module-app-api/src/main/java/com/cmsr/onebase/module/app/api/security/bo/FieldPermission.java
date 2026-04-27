package com.cmsr.onebase.module.app.api.security.bo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 字段权限
 *
 * @Author：huangjie
 * @Date：2025/10/26 8:25
 */
@Data
public class FieldPermission {

    /**
     * 所有字段都允许
     */
    private boolean allAllowed = false;

    /**
     * 所有字段都不允许
     */
    private boolean allDenied = false;

    /**
     * 字段权限项
     */
    private List<FieldPermissionItem> fields = new ArrayList<>();

}
