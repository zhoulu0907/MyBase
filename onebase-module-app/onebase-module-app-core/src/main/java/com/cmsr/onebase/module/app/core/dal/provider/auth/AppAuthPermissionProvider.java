package com.cmsr.onebase.module.app.core.dal.provider.auth;

import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthPermissionRepository;
import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.auth.AuthPermissionDO;
import com.cmsr.onebase.module.app.core.enums.auth.AuthDefaultFactory;
import com.cmsr.onebase.module.app.core.vo.auth.AuthPermissionReq;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @Author：huangjie
 * @Date：2025/10/28 13:20
 */
@Slf4j
@Setter
@Service
public class AppAuthPermissionProvider {

    @Autowired
    private AppMenuRepository appMenuRepository;

    @Autowired
    private AppAuthPermissionRepository appAuthPermissionRepository;

    @Autowired
    private RedissonClient redissonClient;

    public List<AuthPermissionDO> findPermissions(Long applicationId, Set<Long> roleIds, Long menuId) {
        //这里有问题，是某个角色没有，才给默认值，用表连接，如果连接没有到，就是默认的 角色 left join perm表，没有的，就是默认的
        List<AuthPermissionDO> permissionDOS = appAuthPermissionRepository.findByAppIdAndRoleIdsAndMenuId(applicationId, roleIds, menuId);
        if (CollectionUtils.isEmpty(permissionDOS)) {
            AuthPermissionReq req = new AuthPermissionReq();
            permissionDOS = List.of(AuthDefaultFactory.createAuthPermissionDO(req));
        }
        return permissionDOS;
    }


}
