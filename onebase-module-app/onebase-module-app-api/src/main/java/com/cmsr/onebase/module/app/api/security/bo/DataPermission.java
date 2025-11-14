package com.cmsr.onebase.module.app.api.security.bo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据权限定义
 * @Author：huangjie
 * @Date：2025/10/26 8:16
 */
@Data
public class DataPermission {

    /**
     * 所有权限允许，如果有那么groups里面的配置都可以忽略了。
     */
    private boolean allAllowed;

    /**
     * 所有权限拒绝，如果有那么groups里面的配置都可以忽略了。
     */
    private boolean allDenied;

    /**
     * 数据权限组
     */
    private List<DataPermissionGroup> groups = new ArrayList<>();

}
