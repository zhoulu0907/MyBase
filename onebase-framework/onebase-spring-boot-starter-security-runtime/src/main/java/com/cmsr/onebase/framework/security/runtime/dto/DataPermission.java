package com.cmsr.onebase.framework.security.runtime.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/10/26 8:16
 */
@Data
public class DataPermission {

    private boolean allAllowed;

    private boolean allDenied;

    private List<DataPermissionItem> items = new ArrayList<>();

}
