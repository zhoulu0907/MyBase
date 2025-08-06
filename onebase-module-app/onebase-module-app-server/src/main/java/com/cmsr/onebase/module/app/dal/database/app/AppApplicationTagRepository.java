package com.cmsr.onebase.module.app.dal.database.app;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.app.dal.dataobject.app.ApplicationTagDO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/6 14:15
 */
@Repository
public class AppApplicationTagRepository extends DataRepository {

    public AppApplicationTagRepository() {
        super(ApplicationTagDO.class);
    }

    public List<Long> findTagIdsByApplicationId(Long applicationId) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.eq("application_id", applicationId);
        return findAll(ApplicationTagDO.class, configStore).stream()
                .map(ApplicationTagDO::getTagId)
                .toList();
    }

    /**
     * 更新应用关联的标签，先删除没有的标签，再添加新的标签
     *
     * @param applicationId
     * @param tagIds
     */
    public void mergeApplicationTags(Long applicationId, List<Long> tagIds) {
        if (CollectionUtils.isEmpty(tagIds)) {
            ConfigStore configStore = new DefaultConfigStore();
            configStore.eq("application_id", applicationId);
            this.deleteByConfig(ApplicationTagDO.class, configStore);
        } else {
            ConfigStore configStore = new DefaultConfigStore();
            configStore.eq("application_id", applicationId);
            configStore.notIn("tag_id", tagIds);
            deleteByConfig(ApplicationTagDO.class, configStore);
            for (Long tagId : tagIds) {
                configStore = new DefaultConfigStore();
                configStore.eq("application_id", applicationId);
                configStore.eq("tag_id", tagId);
                if (this.countByConfig(ApplicationTagDO.class, configStore) == 0) {
                    ApplicationTagDO applicationTagDO = new ApplicationTagDO();
                    applicationTagDO.setApplicationId(applicationId);
                    applicationTagDO.setTagId(tagId);
                    this.insert(applicationTagDO);
                }
            }
        }
    }
}
