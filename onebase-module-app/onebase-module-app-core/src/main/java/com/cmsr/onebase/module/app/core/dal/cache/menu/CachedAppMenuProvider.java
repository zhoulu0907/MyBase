package com.cmsr.onebase.module.app.core.dal.cache.menu;

import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.menu.MenuDO;
import lombok.Setter;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @Author：huangjie
 * @Date：2025/10/25 13:12
 */
@Setter
@Service
public class CachedAppMenuProvider {

    @Setter
    private AppMenuRepository appMenuRepository;

    @Cacheable(cacheNames = "appMenu", key = "#id")
    public MenuDO findById(Long id) {
        return appMenuRepository.findById(id);
    }

}
