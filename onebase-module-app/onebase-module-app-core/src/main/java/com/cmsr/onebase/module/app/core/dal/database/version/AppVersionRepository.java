package com.cmsr.onebase.module.app.core.dal.database.version;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageParam;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.version.VersionDO;
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
public class AppVersionRepository extends DataRepository<VersionDO> {

    public AppVersionRepository() {
        super(VersionDO.class);
    }

    public void deleteByApplicationId(Long applicationId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", applicationId);
        deleteByConfig(configs);
    }

    public List<VersionDO> findByApplicationId(Long applicationId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", applicationId);
        configs.order(BaseDO.CREATE_TIME, Order.TYPE.DESC);
        return findAllByConfig(configs);
    }

    public PageResult<VersionDO> selectPage(Long applicationId, PageParam pageParam) {
        ConfigStore configs = new DefaultConfigStore();
        if (applicationId > 0) {
            configs.eq("application_id", applicationId);
        }
        return findPageWithConditions(configs, pageParam.getPageNo(), pageParam.getPageSize());
    }

    public List<VersionDO> findVersionList(List<Long> appIds) {
        ConfigStore configs = new DefaultConfigStore();
        configs.in(VersionDO.APPLICATION_ID, appIds);
        configs.order(BaseDO.UPDATE_TIME, Order.TYPE.DESC);
        return findAllByConfig(configs);
    }
}
