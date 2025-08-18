package com.cmsr.onebase.module.app.dal.database.version;

import com.cmsr.onebase.framework.aynline.DataRepositoryNew;
import com.cmsr.onebase.module.app.dal.dataobject.version.VersionResourceDO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

/**
 * @Author：huangjie
 * @Date：2025/8/6 14:44
 */
@Repository
public class AppVersionResourceRepository extends DataRepositoryNew<VersionResourceDO> {

    public AppVersionResourceRepository() {
        super(VersionResourceDO.class);
    }

    public void deleteByApplicationId(Long applicationId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", applicationId);
        deleteByConfig(configs);
    }



    public VersionResourceDO findByApplicationIdAndVersionIdAndResType(Long applicationId, Long versionId, String resType) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", applicationId);
        configs.eq("version_id", versionId);
        configs.eq("res_type", resType);
        return findOne(configs);
    }

    public void deleteByVersionId(Long versionId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("version_id", versionId);
        deleteByConfig(configs);
    }



}
