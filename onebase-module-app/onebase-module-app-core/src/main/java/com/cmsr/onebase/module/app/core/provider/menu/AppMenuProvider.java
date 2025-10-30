package com.cmsr.onebase.module.app.core.provider.menu;

import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.menu.MenuDO;
import lombok.Setter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @Author：huangjie
 * @Date：2025/10/25 13:12
 */
@Setter
@Service
public class AppMenuProvider {

    @Autowired
    private AppMenuRepository appMenuRepository;

    @Autowired
    private RedissonClient redissonClient;

    public MenuDO findByMenuId(Long menuId) {
        return appMenuRepository.findById(menuId);
    }

    public Set<Long> findPageIdsByAppIdAndMenuId(Long applicationId, Long menuId) {
        return appMenuRepository.findPageIdsByAppIdAndMenuId(applicationId, menuId);
    }
}
