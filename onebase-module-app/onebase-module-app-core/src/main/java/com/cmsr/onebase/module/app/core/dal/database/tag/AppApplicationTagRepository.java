package com.cmsr.onebase.module.app.core.dal.database.tag;

import com.cmsr.onebase.framework.orm.repo.BaseAppRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppApplicationTagDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppApplicationTagMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/6 14:15
 */
@Repository
public class AppApplicationTagRepository extends BaseAppRepository<AppApplicationTagMapper, AppApplicationTagDO> {

    public List<AppApplicationTagDO> findTagIdsByApplicationIds(List<Long> applicationIds) {
        QueryWrapper queryWrapper = this.query()
                .in(AppApplicationTagDO::getApplicationId, applicationIds);
        return this.list(queryWrapper);
    }

    public List<Long> findTagIdsByApplicationId(Long applicationId) {
        QueryWrapper queryWrapper = this.query()
                .select(AppApplicationTagDO::getTagId)
                .eq(AppApplicationTagDO::getApplicationId, applicationId);
        return this.objListAs(queryWrapper, Long.class);
    }


    public void deleteByApplicationId(Long applicationId) {
        this.updateChain()
                .eq(AppApplicationTagDO::getApplicationId, applicationId)
                .remove();
    }

    public void deleteByByApplicationIdAndTagsNotIn(Long applicationId, List<Long> tagIds) {
        this.updateChain()
                .eq(AppApplicationTagDO::getApplicationId, applicationId)
                .notIn(AppApplicationTagDO::getTagId, tagIds)
                .remove();
    }

    public void deleteByTagId(Long tagId) {
        this.updateChain()
                .eq(AppApplicationTagDO::getTagId, tagId)
                .remove();
    }

    public void saveAll(Long applicationId, List<Long> tagIds) {
        for (Long tagId : tagIds) {
            QueryWrapper queryWrapper = this.query()
                    .eq(AppApplicationTagDO::getApplicationId, applicationId)
                    .eq(AppApplicationTagDO::getTagId, tagId);
            boolean exists = exists(queryWrapper);
            if (!exists) {
                AppApplicationTagDO applicationTagDO = new AppApplicationTagDO();
                applicationTagDO.setApplicationId(applicationId);
                applicationTagDO.setTagId(tagId);
                this.save(applicationTagDO);
            }
        }
    }
}