package com.cmsr.onebase.module.app.core.dal.database;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.orm.mybatis.BaseAppRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.VersionDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppVersionMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/6 14:42
 */
@Repository
public class AppVersionRepository extends BaseAppRepository<AppVersionMapper, VersionDO> {

    public void deleteByApplicationId(Long applicationId) {
        this.updateChain()
                .eq(VersionDO::getApplicationId, applicationId)
                .remove();
    }

    public List<VersionDO> findByApplicationId(Long applicationId) {
        QueryWrapper queryWrapper = this.query()
                .eq(VersionDO::getApplicationId, applicationId)
                .orderBy(VersionDO::getUpdateTime, false)
                .orderBy(VersionDO::getCreateTime, false);
        return list(queryWrapper);
    }

    public PageResult<VersionDO> selectPage(Long applicationId, PageParam pageParam) {
        QueryWrapper queryWrapper = this.query()
                .eq(VersionDO::getApplicationId, applicationId);
        Page<VersionDO> pageQuery = Page.of(pageParam.getPageNo(), pageParam.getPageSize());
        Page<VersionDO> pageResult = this.page(pageQuery, queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }

    public List<VersionDO> findVersionList(List<Long> appIds) {
        QueryWrapper queryWrapper = this.query()
                .in(VersionDO::getApplicationId, appIds)
                .orderBy(VersionDO::getUpdateTime, false)
                .orderBy(VersionDO::getCreateTime, false);
        return this.list(queryWrapper);
    }
}
