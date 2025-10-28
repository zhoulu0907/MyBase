package com.cmsr.onebase.module.app.core.dal.cache.menu;

import com.cmsr.onebase.module.app.api.auth.dto.MenuDTO;
import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.menu.MenuDO;
import com.cmsr.onebase.module.app.core.utils.CacheUtils;
import lombok.Setter;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author：huangjie
 * @Date：2025/10/25 13:12
 */
@Setter
@Service
public class CachedAppMenuProvider {

    @Autowired
    private AppMenuRepository appMenuRepository;

    @Autowired
    private RedissonClient redissonClient;

    public MenuDTO findById(Long id) {
        String key = String.format(CacheUtils.REDIS_MENU_KEY, id);
        RBucket<MenuDTO> bucket = redissonClient.getBucket(key, CacheUtils.KRYO5_CODEC);
        if (bucket.isExists()) {
            return bucket.get();
        }
        MenuDO menuDO = appMenuRepository.findById(id);
        if (menuDO == null) {
            return null;
        }
        MenuDTO menuDTO = new MenuDTO();
        menuDTO.setId(menuDO.getId());
        menuDTO.setApplicationId(menuDO.getApplicationId());
        menuDTO.setEntityId(menuDO.getEntityId());
        menuDTO.setMenuCode(menuDO.getMenuCode());
        bucket.set(menuDTO, CacheUtils.CACHE_TIMEOUT);
        return menuDTO;
    }

}
