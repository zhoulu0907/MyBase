package com.cmsr.onebase.module.app.core.dal.database.app;

import com.cmsr.onebase.framework.common.enums.OwnerTagEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppApplicationDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppApplicationMapper;
import com.cmsr.onebase.module.app.core.enums.app.AppPublishEnum;
import com.cmsr.onebase.module.app.core.enums.app.AppStatusEnum;
import com.cmsr.onebase.module.app.core.vo.app.ApplicationPageReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.tenant.TenantManager;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
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
                .where(APP_APPLICATION.APP_NAME.like(pageReqVO.getName()).when(StringUtils.isNotBlank(pageReqVO.getName())))
                .where(APP_APPLICATION.APP_STATUS.eq(pageReqVO.getStatus()).when(pageReqVO.getStatus() != null))
                .where(APP_APPLICATION.PUBLISH_MODEL.eq(pageReqVO.getPublishModel()).when(StringUtils.isNotBlank(pageReqVO.getPublishModel())))
                .where(APP_APPLICATION.CREATOR.eq(userId).when(filterByUser));
        if (Strings.CI.equals(pageReqVO.getOrderByTime(), "create")) {
            queryWrapper = queryWrapper.orderBy(APP_APPLICATION.CREATE_TIME, false);
        }
        if (Strings.CI.equals(pageReqVO.getOrderByTime(), "update")) {
            queryWrapper = queryWrapper.orderBy(APP_APPLICATION.UPDATE_TIME, false);
        }
        Page<AppApplicationDO> pageQuery = Page.of(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        Page<AppApplicationDO> pageResult = this.page(pageQuery, queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }

    public long findOneByAppCode(String appCode) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_APPLICATION.APP_CODE.eq(appCode));
        return count(queryWrapper);
    }

    public long findOneByUid(String uid) {
        QueryWrapper queryWrapper = this.query().where(APP_APPLICATION.APP_UID.eq(uid));
        return count(queryWrapper);
    }

    public long findByAppCodeAndIdNot(String appCode, Long id) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_APPLICATION.APP_CODE.eq(appCode))
                .where(APP_APPLICATION.ID.ne(id));
        return count(queryWrapper);
    }

    public Long countByTenantId(Long tenantId) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_APPLICATION.TENANT_ID.eq(tenantId));
        return this.count(queryWrapper);
    }

    public AppApplicationDO findByTenantIdAndAppId(Long tenantId, Long appId) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_APPLICATION.TENANT_ID.eq(tenantId))
                .and(APP_APPLICATION.ID.eq(appId));
        return this.getOne(queryWrapper);
    }

    public List<AppApplicationDO> getSimpleAppList(Integer status) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_APPLICATION.APP_STATUS.eq(status))
                .orderBy(APP_APPLICATION.UPDATE_TIME, false)
                .orderBy(APP_APPLICATION.CREATE_TIME, false);
        return list(queryWrapper);
    }

    public List<AppApplicationDO> findAppApplicationByAppName(String appName,Integer status) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_APPLICATION.APP_NAME.eq(appName).when(StringUtils.isNotBlank(appName)))
                .where(APP_APPLICATION.APP_STATUS.eq(status).when(status != null))
                .orderBy(APP_APPLICATION.UPDATE_TIME, false)
                .orderBy(APP_APPLICATION.CREATE_TIME, false);
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
                .where(APP_APPLICATION.ID.in(appIds).when(CollectionUtils.isNotEmpty(appIds)))
                .orderBy(APP_APPLICATION.UPDATE_TIME, false)
                .orderBy(APP_APPLICATION.CREATE_TIME, false);
        return list(queryWrapper);
    }

    public List<AppApplicationDO> findMyAppApplicationByAppName(String appName, Long userId) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_APPLICATION.APP_NAME.eq(appName).when(StringUtils.isNotBlank(appName)))
                .where(APP_APPLICATION.CREATOR.eq(userId))
                .orderBy(APP_APPLICATION.UPDATE_TIME, false)
                .orderBy(APP_APPLICATION.CREATE_TIME, false);
        return list(queryWrapper);
    }

    public void updateAppTimeByApplicationId(Long appId) {
        this.updateChain()
                .set(APP_APPLICATION.UPDATE_TIME, LocalDateTime.now())
                .where(APP_APPLICATION.ID.eq(appId))
                .update();
    }

    public void updateStatusByApplicationId(Long applicationId, AppStatusEnum status, AppPublishEnum publishStatus) {
        this.updateChain()
                .set(APP_APPLICATION.APP_STATUS, status.getValue())
                .set(APP_APPLICATION.PUBLISH_STATUS, publishStatus.getValue())
                .where(APP_APPLICATION.ID.eq(applicationId))
                .update();
    }

    public void updateAppStatusByApplicationId(Long applicationId, AppStatusEnum status) {
        this.updateChain()
                .set(APP_APPLICATION.APP_STATUS, status.getValue())
                .where(APP_APPLICATION.ID.eq(applicationId))
                .update();
    }

}
