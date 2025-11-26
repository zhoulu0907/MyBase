package com.cmsr.onebase.module.app.core.provider;

import com.cmsr.onebase.module.app.core.dal.database.AppSqlQueryRepository;
import com.cmsr.onebase.module.app.core.dal.database.AppAuthRoleUserRepository;
import com.cmsr.onebase.module.app.core.dal.database.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthRoleUserDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppMenuDO;
import com.cmsr.onebase.module.app.core.utils.CacheUtils;
import com.cmsr.onebase.module.app.core.vo.auth.AuthPermissionReq;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/10/30 14:04
 */
@Slf4j
@Setter
@Service
public class AppCacheProvider {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private AppMenuRepository appMenuRepository;

    @Autowired
    private AppAuthRoleUserRepository appAuthRoleUserRepository;

    @Autowired
    private AppSqlQueryRepository appSqlQueryRepository;

    private List<Long> findAllMenuIds(Long applicationId) {
        return appMenuRepository.findByApplicationId(applicationId)
                .stream().map(AppMenuDO::getId).toList();
    }

    private List<Long> findAllUserIds(Long roleId) {
        return appAuthRoleUserRepository.findByRoleId(roleId)
                .stream().map(AppAuthRoleUserDO::getUserId).toList();
    }

    public void usersChanged(Long applicationId, List<Long> userIds) {
        List<Long> menuIds = findAllMenuIds(applicationId);
        for (Long userId : userIds) {
            for (Long menuId : menuIds) {
                allCacheKeys(userId, applicationId, menuId).forEach(key -> {
                    log.info("usersChanged 删除缓存：{}", key);
                    redissonClient.getBucket(key).delete();
                });
            }
        }
    }

    public void roleMenuChanged(AuthPermissionReq permissionReq) {
        roleMenuChanged(permissionReq.getApplicationId(), permissionReq.getRoleId(), permissionReq.getMenuId());
    }

    public void roleMenuChanged(Long applicationId, Long roleId, Long menuId) {
        List<Long> userIds = findAllUserIds(roleId);
        for (Long userId : userIds) {
            allCacheKeys(userId, applicationId, menuId).forEach(key -> {
                log.info("roleMenuChanged 删除缓存：{}", key);
                redissonClient.getBucket(key).delete();
            });
        }
    }

    public void deptChanged(Long applicationId, Long deptId, Integer isIncludeChild) {
        List<Long> menuIds = findAllMenuIds(applicationId);
        List<Long> userIds = appSqlQueryRepository.findAllUserIdsByDeptIds(deptId, isIncludeChild);
        for (Long userId : userIds) {
            for (Long menuId : menuIds) {
                allCacheKeys(userId, applicationId, menuId).forEach(key -> {
                    log.info("deptsChanged 删除缓存：{}", key);
                    redissonClient.getBucket(key).delete();
                });
            }
        }
    }

    public void deptsChanged(Long applicationId, List<Long> deptIds, Integer isIncludeChild) {
        deptIds.forEach(deptId -> deptChanged(applicationId, deptId, isIncludeChild));
    }


    public List<String> allCacheKeys(Long userId, Long applicationId, Long menuId) {
        return List.of(
                CacheUtils.keyForDataPermission(userId, applicationId, menuId),
                CacheUtils.keyForFieldPermission(userId, applicationId, menuId),
                CacheUtils.keyForOperationPermission(userId, applicationId, menuId),
                CacheUtils.keyForPagePermission(userId, applicationId, menuId)
        );
    }


}
