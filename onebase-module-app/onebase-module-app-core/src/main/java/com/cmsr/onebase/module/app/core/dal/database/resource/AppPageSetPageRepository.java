package com.cmsr.onebase.module.app.core.dal.database.resource;

import com.cmsr.onebase.framework.orm.repo.BaseBizRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetPageDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppResourcePagesetPageMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AppPageSetPageRepository extends BaseBizRepository<AppResourcePagesetPageMapper, AppResourcePagesetPageDO> {

    public void deleteByPageId(Long pageId) {
        this.updateChain().eq(AppResourcePagesetPageDO::getPageId, pageId)
                .remove();
    }

    public void deleteByPageSetId(Long pageSetId) {
        this.updateChain().eq(AppResourcePagesetPageDO::getPageSetId, pageSetId)
                .remove();
    }

    public List<AppResourcePagesetPageDO> findByPageSetId(Long pageSetId) {
        QueryWrapper queryWrapper = this.query().eq(AppResourcePagesetPageDO::getPageSetId, pageSetId);
        return list(queryWrapper);
    }

    public AppResourcePagesetPageDO findByPageSetIdAndPageId(Long pageSetId, Long pageId) {
        QueryWrapper queryWrapper = this.query()
                .eq(AppResourcePagesetPageDO::getPageSetId, pageSetId)
                .eq(AppResourcePagesetPageDO::getPageId, pageId);
        return getOne(queryWrapper);
    }

    public AppResourcePagesetPageDO findByPageId(Long pageId) {
        QueryWrapper queryWrapper = this.query().eq(AppResourcePagesetPageDO::getPageId, pageId);
        return getOne(queryWrapper);
    }

    public List<AppResourcePagesetPageDO> findByPageSetIds(List<Long> pageSetIds) {
        QueryWrapper queryWrapper = this.query().in(AppResourcePagesetPageDO::getPageSetId, pageSetIds);
        return list(queryWrapper);
    }

}
