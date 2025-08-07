package com.cmsr.onebase.module.app.dal.database.version;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.app.dal.dataobject.version.VersionResourceDO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/6 14:44
 */
@Repository
public class AppVersionResourceRepository extends DataRepository {

    public AppVersionResourceRepository() {
        super(VersionResourceDO.class);
    }

    public void deleteByApplicationId(Long applicationId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", applicationId);
        deleteByConfig(VersionResourceDO.class, configs);
    }

    public List<VersionResourceDO> findByApplicationIdAndVersionId(Long applicationId, Long versionId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", applicationId);
        configs.eq("version_id", versionId);
        return findAll(VersionResourceDO.class, configs);
    }

    public void deleteByVersionId(Long versionId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("version_id", versionId);
        deleteByConfig(VersionResourceDO.class, configs);
    }
}
