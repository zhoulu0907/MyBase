package com.cmsr.onebase.module.app.dal.database.app;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.app.dal.dataobject.app.TagDO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/6 14:19
 */
@Repository
public class AppTagRepository extends DataRepository {

    public AppTagRepository() {
        super(TagDO.class);
    }

    public List<TagDO> findByTagNameLike(String tagName) {
        ConfigStore configs = new DefaultConfigStore();
        configs.like("tag_name", tagName);
        configs.order("tag_name", Order.TYPE.DESC);
        return findAll(TagDO.class, configs);
    }

    public Long countByTagName(String tagName) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("tag_name", tagName);
        return countByConfig(TagDO.class, configs);
    }

}
