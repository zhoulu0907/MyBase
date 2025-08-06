package com.cmsr.onebase.module.app.dal.database.app;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.app.dal.dataobject.app.VersionMenuDO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/6 14:43
 */
@Repository
public class AppVersionMenuRepository extends DataRepository {

    public AppVersionMenuRepository() {
        super(VersionMenuDO.class);
    }

    public void deleteByApplicationId(Long applicationId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", applicationId);
        deleteByConfig(VersionMenuDO.class, configs);
    }

    public List<VersionMenuDO> findByApplicationIdAndVersionId(Long applicationId, Long versionId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", applicationId);
        configs.eq("version_id", versionId);
        return findAll(VersionMenuDO.class, configs);
    }

    public void deleteByVersionId(Long versionId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("version_id", versionId);
        deleteByConfig(VersionMenuDO.class, configs);
    }
}
