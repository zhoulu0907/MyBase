package com.cmsr.onebase.module.app.api.security.bo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/10/26 8:25
 */
@Data
public class FieldPermission {

    private boolean allAllowed = false;

    private boolean allDenied = false;

    private List<FieldPermissionItem> fields = new ArrayList<>();

}
