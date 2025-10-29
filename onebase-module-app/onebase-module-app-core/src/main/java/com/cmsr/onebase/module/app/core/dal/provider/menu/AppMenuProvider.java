package com.cmsr.onebase.module.app.core.dal.provider.menu;

import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.menu.MenuDO;
import com.cmsr.onebase.module.app.core.utils.CacheUtils;
import lombok.Setter;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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


    public List<MenuDO> findByApplicationId(Long applicationId) {
        String key = String.format(CacheUtils.REDIS_APPLICATION_MENU_KEY, applicationId);
        RBucket<List<MenuDO>> bucket = redissonClient.getBucket(key, CacheUtils.KRYO5_CODEC);
        if (bucket.isExists()) {
            return bucket.get();
        }
        List<MenuDO> menuDOS = appMenuRepository.findByApplicationId(applicationId);
        bucket.set(menuDOS, CacheUtils.CACHE_TIMEOUT);
        return menuDOS;
    }

}
