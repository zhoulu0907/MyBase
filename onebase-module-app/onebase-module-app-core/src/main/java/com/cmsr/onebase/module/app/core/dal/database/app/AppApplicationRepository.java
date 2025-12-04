package com.cmsr.onebase.module.app.core.dal.database.app;

import com.cmsr.onebase.framework.common.enums.OwnerTagEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppApplicationDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppApplicationMapper;
import com.cmsr.onebase.module.app.core.vo.app.ApplicationPageReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.tenant.TenantManager;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppApplicationTableDef.APP_APPLICATION;

/**
 * @Author：huangjie
 * @Date：2025/8/6 14:08
 */
@Repository
public class AppApplicationRepository extends ServiceImpl<AppApplicationMapper, AppApplicationDO> {

    public PageResult<AppApplicationDO> selectPage(ApplicationPageReqVO pageReqVO, Long userId) {
        boolean filterByUser = pageReqVO.getOwnerTag() != null && pageReqVO.getOwnerTag().equals(OwnerTagEnum.MY.getValue()) && userId != null;
        QueryWrapper queryWrapper = this.query()
                .like(AppApplicationDO::getAppName, pageReqVO.getName(), StringUtils::isNotBlank)
                .eq(AppApplicationDO::getAppStatus, pageReqVO.getStatus(), pageReqVO.getStatus() != null)
                .eq(AppApplicationDO::getPublishModel, pageReqVO.getPublishModel(), StringUtils::isNotBlank)
                .eq(AppApplicationDO::getCreator, userId, filterByUser);
        if (StringUtils.equalsIgnoreCase(pageReqVO.getOrderByTime(), "create")) {
            queryWrapper = queryWrapper.orderBy(AppApplicationDO::getCreateTime, false);
        }
        if (StringUtils.equalsIgnoreCase(pageReqVO.getOrderByTime(), "update")) {
            queryWrapper = queryWrapper.orderBy(AppApplicationDO::getUpdateTime, false);
        }
        Page<AppApplicationDO> pageQuery = Page.of(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        Page<AppApplicationDO> pageResult = this.page(pageQuery, queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }

    public long findOneByAppCode(String appCode) {
        QueryWrapper queryWrapper = this.query().eq(AppApplicationDO::getAppCode, appCode);
        return count(queryWrapper);
    }

    public long findOneByUid(String uid) {
        QueryWrapper queryWrapper = this.query().eq(AppApplicationDO::getAppUid, uid);
        return count(queryWrapper);
    }

    public long findByAppCodeAndIdNot(String appCode, Long id) {
        QueryWrapper queryWrapper = this.query().eq(AppApplicationDO::getAppCode, appCode)
                .ne(AppApplicationDO::getId, id);
        return count(queryWrapper);
    }

    public Long countByTenantId(Long tenantId) {
        QueryWrapper queryWrapper = this.query().eq(AppApplicationDO::getTenantId, tenantId);
        return this.count(queryWrapper);
    }

    public AppApplicationDO findByTenantIdAndAppId(Long tenantId, Long appId) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_APPLICATION.TENANT_ID.eq(tenantId))
                .and(APP_APPLICATION.ID.eq(appId));
        return this.getOne(queryWrapper);
    }

    public List<AppApplicationDO> getSimpleAppList(Integer status) {
        QueryWrapper queryWrapper = this.query().eq(AppApplicationDO::getAppStatus, status)
                .orderBy(AppApplicationDO::getCreateTime, false);
        return list(queryWrapper);
    }

    public List<AppApplicationDO> findAppApplicationByAppName(String appName) {
        QueryWrapper queryWrapper = this.query()
                .like(AppApplicationDO::getAppName, appName, StringUtils::isNotBlank)
                .orderBy(AppApplicationDO::getUpdateTime, false)
                .orderBy(AppApplicationDO::getCreateTime, false);
        return list(queryWrapper);
    }

    public Map<Long, Integer> countAppByTenantId() {
        QueryWrapper queryWrapper = this.query()
                .select(
                        APP_APPLICATION.TENANT_ID.as("tenant_id"),
                        QueryMethods.count(APP_APPLICATION.ID).as("counts")
                ).groupBy(APP_APPLICATION.TENANT_ID);
        List<Row> rows = TenantManager.withoutTenantCondition(() -> getMapper().selectRowsByQuery(queryWrapper));
        Map<Long, Integer> result = rows.stream()
                .collect(Collectors.toMap(
                        row -> row.getLong("tenant_id"),
                        row -> row.getInt("counts")
                ));
        return result;
    }

    public List<AppApplicationDO> findAppApplicationByAppIds(Collection<Long> appIds) {
        QueryWrapper queryWrapper = this.query()
                .in(AppApplicationDO::getId, appIds, CollectionUtils.isNotEmpty(appIds))
                .orderBy(AppApplicationDO::getUpdateTime, false)
                .orderBy(AppApplicationDO::getCreateTime, false);
        return list(queryWrapper);
    }

    public List<AppApplicationDO> findMyAppApplicationByAppName(String appName, Long userId) {
        QueryWrapper queryWrapper = this.query()
                .like(AppApplicationDO::getAppName, appName, StringUtils::isNotBlank)
                .eq(AppApplicationDO::getCreator, userId)
                .orderBy(AppApplicationDO::getUpdateTime, false)
                .orderBy(AppApplicationDO::getCreateTime, false);
        return list(queryWrapper);
    }

    public void updateAppTimeByApplicationId(Long appId) {
        this.updateChain().set(AppApplicationDO::getUpdateTime, LocalDateTime.now())
                .eq(AppApplicationDO::getId, appId)
                .update();
    }


}
