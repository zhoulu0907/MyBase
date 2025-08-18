package com.cmsr.onebase.module.app.dal.database.tag;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.app.dal.dataobject.tag.TagDO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/6 14:19
 */
@Repository
public class AppTagRepository extends DataRepository<TagDO> {

    public AppTagRepository() {
        super(TagDO.class);
    }

    public List<TagDO> findByTagNameLike(String tagName) {
        ConfigStore configs = new DefaultConfigStore();
        if (StringUtils.isNotEmpty(tagName)) {
            configs.like("tag_name", tagName);
        }
        configs.order("tag_name", Order.TYPE.ASC);
        return findAllByConfig(configs);
    }

    public Long countByTagName(String tagName) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("tag_name", tagName);
        return countByConfig(configs);
    }

}
