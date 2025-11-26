package com.cmsr.onebase.module.app.core.dal.database;

import com.cmsr.onebase.framework.orm.repo.BaseAppRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.VersionResourceDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppVersionResourceMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

/**
 * @Author：huangjie
 * @Date：2025/8/6 14:44
 */
@Repository
public class AppVersionResourceRepository extends BaseAppRepository<AppVersionResourceMapper, VersionResourceDO> {

    public void deleteByApplicationId(Long applicationId) {
        this.updateChain()
                .eq(VersionResourceDO::getApplicationId, applicationId)
                .remove();
    }


    public VersionResourceDO findByApplicationIdAndVersionIdAndResType(Long applicationId, Long versionId, String resType) {
        QueryWrapper queryWrapper = this.query()
                .eq(VersionResourceDO::getApplicationId, applicationId)
                .eq(VersionResourceDO::getVersionId, versionId)
                .eq(VersionResourceDO::getResType, resType);
        return this.getOne(queryWrapper);
    }

    public void deleteByVersionId(Long versionId) {
        this.updateChain()
                .eq(VersionResourceDO::getVersionId, versionId)
                .remove();
    }

}
