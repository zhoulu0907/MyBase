package com.cmsr.onebase.module.app.core.dal.cache.auth;

import com.cmsr.onebase.module.app.api.auth.dto.RoleDTO;
import com.cmsr.onebase.module.app.api.auth.dto.UserRole;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthRoleRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.auth.AuthRoleDO;
import com.cmsr.onebase.module.app.core.enums.auth.AuthRoleTypeEnum;
import com.cmsr.onebase.module.app.core.utils.CacheUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author：huangjie
 * @Date：2025/10/25 14:58
 */
@Slf4j
@Setter
@Service
public class CachedAppAuthRoleProvider {

    @Autowired
    private AppAuthRoleRepository appAuthRoleRepository;

    @Autowired
    private RedissonClient redissonClient;


    public UserRole findByApplicationIdAndUserId(Long applicationId, Long userId) {
        log.debug("获取缓存 appAuthRole:{}:{}", applicationId, userId);
        String key = String.format(CacheUtils.REDIS_USER_ROLE_KEY, applicationId, userId);
        RBucket<UserRole> bucket = redissonClient.getBucket(key, CacheUtils.KRYO5_CODEC);
        if (bucket.isExists()) {
            return bucket.get();
        }
        UserRole userRole = loadUserRoleFromDB(applicationId, userId);
        bucket.set(userRole, CacheUtils.CACHE_TIMEOUT);
        return userRole;
    }

    private UserRole loadUserRoleFromDB(Long applicationId, Long userId) {
        List<AuthRoleDO> authRoleDOS = appAuthRoleRepository.findByApplicationIdAndUserId(applicationId, userId);
        UserRole userRole = new UserRole();
        userRole.setAdminRole(false);
        if (authRoleDOS != null && !authRoleDOS.isEmpty()) {
            for (AuthRoleDO authRoleDO : authRoleDOS) {
                if (AuthRoleTypeEnum.isSystemAdminRole(authRoleDO.getRoleType())) {
                    userRole.setAdminRole(true);
                }
            }
            Set<Long> ids = authRoleDOS.stream().map(AuthRoleDO::getId).collect(Collectors.toSet());
            userRole.setRoleIds(ids);
            List<RoleDTO> roleDTOS = authRoleDOS.stream().map(this::convertToRoleDTO).toList();
            userRole.setRoles(roleDTOS);
        } else {
            userRole.setRoleIds(Set.of());
            userRole.setRoles(List.of());
        }
        return userRole;
    }

    public void evictByApplicationIdAndUserId(Long applicationId, Long userId) {
        log.debug("清除缓存 appAuthRole:{}:{}", applicationId, userId);
        String key = String.format(CacheUtils.REDIS_USER_ROLE_KEY, applicationId, userId);
        redissonClient.getBucket(key, CacheUtils.KRYO5_CODEC).delete();
    }

    /**
     * 将AuthRoleDO转换为RoleDTO
     *
     * @param authRoleDO 权限角色数据对象
     * @return RoleDTO
     */
    private RoleDTO convertToRoleDTO(AuthRoleDO authRoleDO) {
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId(authRoleDO.getId());
        roleDTO.setRoleName(authRoleDO.getRoleName());
        roleDTO.setRoleCode(authRoleDO.getRoleCode());
        return roleDTO;
    }
}
