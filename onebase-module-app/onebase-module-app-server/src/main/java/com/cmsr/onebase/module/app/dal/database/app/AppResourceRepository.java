package com.cmsr.onebase.module.app.dal.database.app;

import com.cmsr.onebase.framework.aynline.DataRepositoryNew;
import com.cmsr.onebase.module.app.dal.dataobject.app.ResourceDO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/6 14:40
 */
@Repository
public class AppResourceRepository extends DataRepositoryNew<ResourceDO> {

    public AppResourceRepository() {
        super(ResourceDO.class);
    }

    public void deleteByApplicationId(Long applicationId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", applicationId);
        deleteByConfig(configs);
    }

    public List<ResourceDO> findByApplicationId(Long applicationId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", applicationId);
        return findAllByConfig(configs);
    }

}
