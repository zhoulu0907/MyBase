package com.cmsr.onebase.module.app.dal.database.app;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.module.app.controller.admin.app.vo.ApplicationPageReqVO;
import com.cmsr.onebase.module.app.dal.dataobject.app.ApplicationDO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.anyline.entity.Order;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

/**
 * @Author：huangjie
 * @Date：2025/8/6 14:08
 */
@Repository
public class AppApplicationRepository extends DataRepository<ApplicationDO> {

    public AppApplicationRepository() {
        super(ApplicationDO.class);
    }

    public PageResult<ApplicationDO> selectPage(ApplicationPageReqVO pageReqVO) {
        ConfigStore configs = new DefaultConfigStore();
        if (StringUtils.isNotBlank(pageReqVO.getName())) {
            configs.and(Compare.LIKE, "app_name", pageReqVO.getName());
        }
        if (pageReqVO.getStatus() != null) {
            configs.and(Compare.EQUAL, "app_status", pageReqVO.getStatus());
        }
        if (StringUtils.equalsIgnoreCase(pageReqVO.getOrderByTime(), "create")) {
            configs.order(BaseDO.CREATE_TIME, Order.TYPE.DESC);
        }
        if (StringUtils.equalsIgnoreCase(pageReqVO.getOrderByTime(), "update")) {
            configs.order(BaseDO.UPDATE_TIME, Order.TYPE.DESC);
        }
        return findPageWithConditions(configs, pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }

    public ApplicationDO findOneByAppCode(String appCode) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("app_code", appCode);
        return findOne(configs);
    }

    public ApplicationDO findOneByUid(String uid) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("app_uid", uid);
        return findOne(configs);
    }

    public ApplicationDO findByAppCodeAndIdNot(String appCode, Long id) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("app_code", appCode);
        configs.ne("id", id);
        return findOne(configs);
    }

    public ApplicationDO findByUidAndIdNot(String uid, Long id) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("app_uid", uid);
        configs.ne("id", id);
        return findOne(configs);
    }

    public Long countByTenantId(Long tenantId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("tenant_id", tenantId);
        return countByConfig(configs);
    }

}
