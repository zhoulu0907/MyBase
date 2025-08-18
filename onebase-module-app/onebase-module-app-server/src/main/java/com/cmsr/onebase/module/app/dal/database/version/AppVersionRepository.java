package com.cmsr.onebase.module.app.dal.database.version;

import com.cmsr.onebase.framework.aynline.DataRepositoryNew;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.module.app.controller.admin.version.vo.VersionPageReqVo;
import com.cmsr.onebase.module.app.dal.dataobject.version.VersionDO;
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
public class AppVersionRepository extends DataRepositoryNew<VersionDO> {

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

    public PageResult<VersionDO> selectPage(VersionPageReqVo pageReqVO) {
        ConfigStore configs = new DefaultConfigStore();
        if (pageReqVO.getApplicationId() > 0) {
            configs.eq( "application_id", pageReqVO.getApplicationId());
        }
        return findPageWithConditions(configs, pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }
}
