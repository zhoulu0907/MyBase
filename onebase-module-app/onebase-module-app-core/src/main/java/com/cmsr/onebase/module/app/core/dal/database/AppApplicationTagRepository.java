package com.cmsr.onebase.module.app.core.dal.database;

import com.cmsr.onebase.framework.orm.mybatis.BaseAppRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.ApplicationTagDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppApplicationTagMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/6 14:15
 */
@Repository
public class AppApplicationTagRepository extends BaseAppRepository<AppApplicationTagMapper, ApplicationTagDO> {

    public List<Long> findTagIdsByApplicationId(Long applicationId) {
        QueryWrapper queryWrapper = this.query()
                .select(ApplicationTagDO::getTagId)
                .eq(ApplicationTagDO::getApplicationId, applicationId);
        return this.objListAs(queryWrapper, Long.class);
    }


    public void deleteByApplicationId(Long applicationId) {
        this.updateChain()
                .eq(ApplicationTagDO::getApplicationId, applicationId)
                .remove();
    }

    public void deleteByByApplicationIdAndTagsNotIn(Long applicationId, List<Long> tagIds) {
        this.updateChain()
                .eq(ApplicationTagDO::getApplicationId, applicationId)
                .notIn(ApplicationTagDO::getTagId, tagIds)
                .remove();
    }

    public void deleteByTagId(Long tagId) {
        this.updateChain()
                .eq(ApplicationTagDO::getTagId, tagId)
                .remove();
    }

    public void saveAll(Long applicationId, List<Long> tagIds) {
        for (Long tagId : tagIds) {
            QueryWrapper queryWrapper = this.query()
                    .eq("application_id", applicationId)
                    .eq("tag_id", tagId);
            boolean exists = exists(queryWrapper);
            if (!exists) {
                ApplicationTagDO applicationTagDO = new ApplicationTagDO();
                applicationTagDO.setApplicationId(applicationId);
                applicationTagDO.setTagId(tagId);
                this.save(applicationTagDO);
            }
        }
    }
}