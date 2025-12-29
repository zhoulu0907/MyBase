package com.cmsr.onebase.module.app.api.security;

import com.cmsr.onebase.module.app.api.security.bo.DataPermission;
import com.cmsr.onebase.module.app.api.security.bo.FieldPermission;
import com.cmsr.onebase.module.app.api.security.bo.OperationPermission;

import java.util.List;

/**
 * @Author：huangjie
 */
public interface AppAuthSecurityApi {

    void loadAuthCache(Long userId, Long applicationId);

    void cleanAuthCache(Long userId, Long applicationId);

    boolean isApplicationAdmin(Long userId, Long applicationId);

    boolean hasApplicationPermission(Long userId, Long applicationId);

    boolean checkMenuEntity(Long applicationId, Long menuId, String entityUuid);

    List<Long> getVisibleMenuIds(Long userId, Long applicationId);

    OperationPermission getMenuOperationPermission(Long userId, Long applicationId, Long menuId);

    DataPermission getMenuDataPermission(Long userId, Long applicationId, Long menuId);

    FieldPermission getMenuFieldPermission(Long userId, Long applicationId, Long menuId);

    List<String> getMenuViewUuids(Long userId, Long applicationId, Long menuId);

}
