package com.cmsr.onebase.module.app.core.dal.database.app;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.enums.OwnerTagEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.framework.security.core.LoginUser;
import com.cmsr.onebase.framework.security.core.util.SecurityFrameworkUtils;
import com.cmsr.onebase.module.app.core.dal.dataobject.app.ApplicationDO;
import com.cmsr.onebase.module.app.core.enums.AppErrorCodeConstants;
import com.cmsr.onebase.module.app.core.vo.app.ApplicationPageReqVO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.anyline.entity.Order;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import java.util.Collection;
import java.util.List;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;

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
            configs.and(Compare.EQUAL, ApplicationDO.APP_STATUS, pageReqVO.getStatus());
        }

        if (StringUtils.isNotBlank(pageReqVO.getPublishModel())) {
            configs.and(Compare.EQUAL, ApplicationDO.PUBLISH_MODEL, pageReqVO.getPublishModel());
        }
        if (pageReqVO.getOwnerTag() != null && pageReqVO.getOwnerTag().equals(OwnerTagEnum.MY.getValue())) {
            LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
            if (loginUser != null) {
                configs.and(Compare.EQUAL, ApplicationDO.CREATOR, loginUser.getId());
            }
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
        configs.eq(ApplicationDO.APP_UID, uid);
        configs.ne(ApplicationDO.ID, id);
        return findOne(configs);
    }

    public Long countByTenantId(Long tenantId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq(ApplicationDO.TENANT_ID, tenantId);
        return countByConfig(configs);
    }

    public List<ApplicationDO> getSimpleAppList(Integer status) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.eq(ApplicationDO.APP_STATUS, status);
        configStore.order(BaseDO.CREATE_TIME, Order.TYPE.DESC);
        return findAllByConfig(configStore);
    }

    public List<ApplicationDO> findAppApplicationByAppName(String appName) {
        ConfigStore configStore = new DefaultConfigStore();
        if (StringUtils.isNotBlank(appName)) {
            configStore.and(Compare.LIKE, ApplicationDO.APP_NAME, appName);
        }
        configStore.order(BaseDO.CREATE_TIME, Order.TYPE.DESC);
        return findAllByConfig(configStore);
    }

    public List<ApplicationDO> finAppApplicationAll() {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.order(BaseDO.CREATE_TIME, Order.TYPE.DESC);
        return findAllByConfig(configStore);
    }

    public List<ApplicationDO> findAppApplicationByAppIds(Collection<Long> appIds) {
        ConfigStore configStore = new DefaultConfigStore();
        if (CollectionUtils.isNotEmpty(appIds)) {
            configStore.in(ApplicationDO.ID, appIds);
        }
        configStore.order(BaseDO.CREATE_TIME, Order.TYPE.DESC);
        return findAllByConfig(configStore);
    }

    public List<ApplicationDO> findMyAppApplicationByAppName(String appName) {
        ConfigStore configStore = new DefaultConfigStore();
        if (StringUtils.isNotBlank(appName)) {
            configStore.and(Compare.LIKE, ApplicationDO.APP_NAME, appName);
        }
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            throw exception(AppErrorCodeConstants.NOT_LOGIN);
        }
        configStore.and(Compare.EQUAL, ApplicationDO.CREATOR, loginUser.getId());
        configStore.order(BaseDO.CREATE_TIME, Order.TYPE.DESC);
        return findAllByConfig(configStore);
    }
}
