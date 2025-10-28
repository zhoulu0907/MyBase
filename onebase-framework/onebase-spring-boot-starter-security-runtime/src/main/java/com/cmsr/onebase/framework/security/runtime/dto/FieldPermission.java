package com.cmsr.onebase.framework.security.runtime.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/10/26 8:25
 */
@Data
public class FieldPermission {

    private boolean allAllowed = false;

    private boolean allDenied = false;

    private Map<Long, FieldPermissionItem> items = new HashMap<>();

}
