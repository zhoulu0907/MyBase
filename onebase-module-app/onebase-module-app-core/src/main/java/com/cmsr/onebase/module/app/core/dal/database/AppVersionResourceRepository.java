package com.cmsr.onebase.module.app.core.dal.database;

import com.cmsr.onebase.framework.orm.repo.BaseAppRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppVersionResourceDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppVersionResourceMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

/**
 * @Author：huangjie
 * @Date：2025/8/6 14:44
 */
@Repository
public class AppVersionResourceRepository extends BaseAppRepository<AppVersionResourceMapper, AppVersionResourceDO> {

    public void deleteByApplicationId(Long applicationId) {
        this.updateChain()
                .eq(AppVersionResourceDO::getApplicationId, applicationId)
                .remove();
    }


    public AppVersionResourceDO findByApplicationIdAndVersionIdAndResType(Long applicationId, Long versionId, String resType) {
        QueryWrapper queryWrapper = this.query()
                .eq(AppVersionResourceDO::getApplicationId, applicationId)
                .eq(AppVersionResourceDO::getVersionId, versionId)
                .eq(AppVersionResourceDO::getResType, resType);
        return this.getOne(queryWrapper);
    }

    public void deleteByVersionId(Long versionId) {
        this.updateChain()
                .eq(AppVersionResourceDO::getVersionId, versionId)
                .remove();
    }

}
