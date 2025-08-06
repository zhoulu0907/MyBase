package com.cmsr.onebase.module.app.dal.database.app;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.module.app.dal.dataobject.app.VersionDO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/6 14:42
 */
@Repository
public class AppVersionRepository extends DataRepository {

    public AppVersionRepository() {
        super(VersionDO.class);
    }

    public void deleteByApplicationId(Long applicationId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", applicationId);
        deleteByConfig(VersionDO.class, configs);
    }

    public List<VersionDO> findByApplicationId(Long applicationId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", applicationId);
        configs.order(BaseDO.CREATE_TIME, Order.TYPE.DESC);
        return findAll(VersionDO.class, configs);
    }
}
