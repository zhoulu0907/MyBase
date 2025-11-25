package com.cmsr.onebase.module.app.core.dal.database;

import com.cmsr.onebase.framework.common.enums.OwnerTagEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.app.core.dal.dataobject.ApplicationDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppApplicationMapper;
import com.cmsr.onebase.module.app.core.vo.app.ApplicationPageReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.tenant.TenantManager;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/6 14:08
 */
@Repository
public class AppApplicationRepository extends ServiceImpl<AppApplicationMapper, ApplicationDO> {

    public PageResult<ApplicationDO> selectPage(ApplicationPageReqVO pageReqVO, Long userId) {
        boolean filterByUser = pageReqVO.getOwnerTag() != null && pageReqVO.getOwnerTag().equals(OwnerTagEnum.MY.getValue()) && userId != null;
        QueryWrapper queryWrapper = this.query()
                .like(ApplicationDO::getAppName, pageReqVO.getName(), StringUtils::isNotBlank)
                .eq(ApplicationDO::getAppStatus, pageReqVO.getStatus(), pageReqVO.getStatus() != null)
                .eq(ApplicationDO::getPublishModel, pageReqVO.getPublishModel(), StringUtils::isNotBlank)
                .eq(ApplicationDO::getCreator, userId, filterByUser);
        if (StringUtils.equalsIgnoreCase(pageReqVO.getOrderByTime(), "create")) {
            queryWrapper = queryWrapper.orderBy(ApplicationDO::getUpdateTime, false);
        }
        if (StringUtils.equalsIgnoreCase(pageReqVO.getOrderByTime(), "update")) {
            queryWrapper = queryWrapper.orderBy(ApplicationDO::getCreateTime, false);
        }
        Page<ApplicationDO> pageQuery = Page.of(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        Page<ApplicationDO> pageResult = this.page(pageQuery, queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }

    public ApplicationDO findOneByAppCode(String appCode) {
        QueryWrapper queryWrapper = this.query().eq(ApplicationDO::getAppCode, appCode);
        return getOne(queryWrapper);
    }

    public ApplicationDO findOneByUid(String uid) {
        QueryWrapper queryWrapper = this.query().eq(ApplicationDO::getAppUid, uid);
        return getOne(queryWrapper);
    }

    public ApplicationDO findByAppCodeAndIdNot(String appCode, Long id) {
        QueryWrapper queryWrapper = this.query().eq(ApplicationDO::getAppCode, appCode)
                .ne(ApplicationDO::getId, id);
        return getOne(queryWrapper);
    }

    public ApplicationDO findByUidAndIdNot(String uid, Long id) {
        QueryWrapper queryWrapper = this.query().eq(ApplicationDO::getAppUid, uid)
                .ne(ApplicationDO::getId, id);
        return getOne(queryWrapper);
    }

    public Long countByTenantId(Long tenantId) {
        QueryWrapper queryWrapper = this.query().eq(ApplicationDO::getTenantId, tenantId);
        return TenantManager.withoutTenantCondition(() -> this.count(queryWrapper));
    }

    public List<ApplicationDO> getSimpleAppList(Integer status) {
        QueryWrapper queryWrapper = this.query().eq(ApplicationDO::getAppStatus, status)
                .orderBy(ApplicationDO::getCreateTime, false);
        return list(queryWrapper);
    }

    public List<ApplicationDO> findAppApplicationByAppName(String appName) {
        QueryWrapper queryWrapper = this.query()
                .like(ApplicationDO::getAppName, appName, StringUtils::isNotBlank)
                .orderBy(ApplicationDO::getUpdateTime, false)
                .orderBy(ApplicationDO::getCreateTime, false);
        return list(queryWrapper);
    }

    public List<ApplicationDO> finAppApplicationAll() {
        QueryWrapper queryWrapper = this.query().orderBy(ApplicationDO::getUpdateTime, false)
                .orderBy(ApplicationDO::getCreateTime, false);
        return list(queryWrapper);
    }

    public List<ApplicationDO> findAppApplicationByAppIds(Collection<Long> appIds) {
        QueryWrapper queryWrapper = this.query()
                .in(ApplicationDO::getId, appIds, CollectionUtils.isNotEmpty(appIds))
                .orderBy(ApplicationDO::getUpdateTime, false)
                .orderBy(ApplicationDO::getCreateTime, false);
        return list(queryWrapper);
    }

    public List<ApplicationDO> findMyAppApplicationByAppName(String appName, Long userId) {
        QueryWrapper queryWrapper = this.query()
                .like(ApplicationDO::getAppName, appName, StringUtils::isNotBlank)
                .eq(ApplicationDO::getCreator, userId)
                .orderBy(ApplicationDO::getUpdateTime, false)
                .orderBy(ApplicationDO::getCreateTime, false);
        return list(queryWrapper);
    }

    public void updateAppTimeByApplicationId(Long appId) {
        this.updateChain().set(ApplicationDO::getUpdateTime, LocalDateTime.now())
                .eq(ApplicationDO::getId, appId)
                .update();
    }
}
