package com.cmsr.onebase.module.app.dal.database.version;

import com.cmsr.onebase.framework.aynline.DataRepositoryNew;
import com.cmsr.onebase.module.app.dal.dataobject.version.VersionMenuDO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/6 14:43
 */
@Repository
public class AppVersionMenuRepository extends DataRepositoryNew<VersionMenuDO> {

    public AppVersionMenuRepository() {
        super(VersionMenuDO.class);
    }

    public void deleteByApplicationId(Long applicationId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", applicationId);
        deleteByConfig(configs);
    }

    public List<VersionMenuDO> findByApplicationIdAndVersionId(Long applicationId, Long versionId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", applicationId);
        configs.eq("version_id", versionId);
        return findAllByConfig(configs);
    }

    public void deleteByVersionId(Long versionId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("version_id", versionId);
        deleteByConfig(configs);
    }
}
