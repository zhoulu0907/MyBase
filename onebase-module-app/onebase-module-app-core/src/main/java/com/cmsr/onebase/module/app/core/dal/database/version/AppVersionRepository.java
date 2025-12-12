package com.cmsr.onebase.module.app.core.dal.database.version;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.orm.repo.BaseAppRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppVersionDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppVersionMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppVersionTableDef.APP_VERSION;

/**
 * @Author：huangjie
 * @Date：2025/8/6 14:42
 */
@Repository
public class AppVersionRepository extends BaseAppRepository<AppVersionMapper, AppVersionDO> {

    public void deleteByApplicationId(Long applicationId) {
        this.updateChain()
                .eq(AppVersionDO::getApplicationId, applicationId)
                .remove();
    }

    public List<AppVersionDO> findByApplicationId(Long applicationId) {
        QueryWrapper queryWrapper = this.query()
                .eq(AppVersionDO::getApplicationId, applicationId)
                .orderBy(AppVersionDO::getUpdateTime, false)
                .orderBy(AppVersionDO::getCreateTime, false);
        return list(queryWrapper);
    }

    public PageResult<AppVersionDO> selectPage(Long applicationId, PageParam pageParam) {
        QueryWrapper queryWrapper = this.query()
                .eq(AppVersionDO::getApplicationId, applicationId)
                .orderBy(APP_VERSION.VERSION_TYPE, true)
                .orderBy(APP_VERSION.CREATE_TIME, false);
        Page<AppVersionDO> pageQuery = Page.of(pageParam.getPageNo(), pageParam.getPageSize());
        Page<AppVersionDO> pageResult = this.page(pageQuery, queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }

    public List<AppVersionDO> findVersionList(List<Long> appIds) {
        QueryWrapper queryWrapper = this.query()
                .in(AppVersionDO::getApplicationId, appIds)
                .orderBy(AppVersionDO::getUpdateTime, false)
                .orderBy(AppVersionDO::getCreateTime, false);
        return this.list(queryWrapper);
    }

    public AppVersionDO findByApplicationIdAndVersionType(Long applicationId, int versionType) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_VERSION.APPLICATION_ID.eq(applicationId))
                .where(APP_VERSION.VERSION_TYPE.eq(versionType));
        return this.getOne(queryWrapper);
    }
}
