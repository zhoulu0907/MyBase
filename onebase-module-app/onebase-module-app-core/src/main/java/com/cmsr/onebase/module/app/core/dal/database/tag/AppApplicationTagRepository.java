package com.cmsr.onebase.module.app.core.dal.database.tag;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.app.ApplicationTagDO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/6 14:15
 */
@Repository
public class AppApplicationTagRepository extends DataRepository<ApplicationTagDO> {

    public AppApplicationTagRepository() {
        super(ApplicationTagDO.class);
    }

    public List<Long> findTagIdsByApplicationId(Long applicationId) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.eq("application_id", applicationId);
        return findAllByConfig(configStore).stream()
                .map(ApplicationTagDO::getTagId)
                .toList();
    }

    public  List<ApplicationTagDO>  findTagIdsByApplicationIds(List<Long> applicationIds) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.in("application_id", applicationIds);
        return findAllByConfig(configStore);
    }


    public void deleteByApplicationId(Long applicationId) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.eq("application_id", applicationId);
        this.deleteByConfig(configStore);
    }

    public void deleteByByApplicationIdAndTagsNotIn(Long applicationId, List<Long> tagIds) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.eq("application_id", applicationId);
        configStore.notIn("tag_id", tagIds);
        deleteByConfig(configStore);
    }

    public void deleteByTagId(Long tagId) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.eq("tag_id", tagId);
        deleteByConfig(configStore);
    }

    public void saveAll(Long applicationId, List<Long> tagIds) {
        for (Long tagId : tagIds) {
            ConfigStore existConfig = new DefaultConfigStore();
            existConfig.eq("application_id", applicationId);
            existConfig.eq("tag_id", tagId);
            long count = this.countByConfig(existConfig);
            if (count == 0) {
                ApplicationTagDO applicationTagDO = new ApplicationTagDO();
                applicationTagDO.setApplicationId(applicationId);
                applicationTagDO.setTagId(tagId);
                this.insert(applicationTagDO);
            }
        }
    }


}