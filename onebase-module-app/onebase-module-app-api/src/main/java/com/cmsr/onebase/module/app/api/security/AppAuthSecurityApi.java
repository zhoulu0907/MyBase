package com.cmsr.onebase.module.app.api.security;

import com.cmsr.onebase.module.app.api.security.bo.DataPermission;
import com.cmsr.onebase.module.app.api.security.bo.FieldPermission;
import com.cmsr.onebase.module.app.api.security.bo.OperationPermission;

/**
 * @Author：huangjie
 */
public interface AppAuthSecurityApi {

    boolean isApplicationAdmin(Long userId, Long applicationId);

    boolean checkMenuEntity(Long applicationId, Long menuId,  String entityUuid);

    OperationPermission getMenuOperationPermission(Long userId, Long applicationId, Long menuId);

    DataPermission getMenuDataPermission(Long userId, Long applicationId, Long menuId);

    FieldPermission getMenuFieldPermission(Long userId, Long applicationId, Long menuId);

}
