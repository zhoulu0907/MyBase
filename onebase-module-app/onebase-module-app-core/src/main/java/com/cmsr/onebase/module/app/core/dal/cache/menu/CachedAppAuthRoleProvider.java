package com.cmsr.onebase.module.app.core.dal.cache.menu;

import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthRoleRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.auth.AuthRoleDO;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/10/25 14:58
 */
@Slf4j
@Setter
@Service
public class CachedAppAuthRoleProvider {

    private AppAuthRoleRepository appAuthRoleRepository;

    @Cacheable(cacheNames = "appAuthRole", key = "#applicationId + ':' + #userId")
    public List<AuthRoleDO> findByApplicationIdAndUserId(Long applicationId, Long userId) {
        return appAuthRoleRepository.findByApplicationIdAndUserId(applicationId, userId);
    }

    @CacheEvict(cacheNames = "appAuthRole", key = "#applicationId + ':' + #userId")
    public void evictByApplicationIdAndUserId(Long applicationId, Long userId) {
        log.debug("清除缓存 appAuthRole:{}:{}", applicationId, userId);
    }


}
