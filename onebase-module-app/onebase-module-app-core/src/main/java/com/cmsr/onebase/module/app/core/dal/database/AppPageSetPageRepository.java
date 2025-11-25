package com.cmsr.onebase.module.app.core.dal.database;

import com.cmsr.onebase.module.app.core.dal.dataobject.PageSetPageDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppPageSetPageMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AppPageSetPageRepository extends ServiceImpl<AppPageSetPageMapper, PageSetPageDO> {

    public void deleteByPageId(Long pageId) {
        this.updateChain().eq(PageSetPageDO::getPageId, pageId)
                .remove();
    }

    public void deleteByPageSetId(Long pageSetId) {
        this.updateChain().eq(PageSetPageDO::getPageSetId, pageSetId)
                .remove();
    }

    public List<PageSetPageDO> findByPageSetId(Long pageSetId) {
        QueryWrapper queryWrapper = this.query().eq(PageSetPageDO::getPageSetId, pageSetId);
        return list(queryWrapper);
    }

    public PageSetPageDO findByPageSetIdAndPageId(Long pageSetId, Long pageId) {
        QueryWrapper queryWrapper = this.query()
                .eq(PageSetPageDO::getPageSetId, pageSetId)
                .eq(PageSetPageDO::getPageId, pageId);
        return getOne(queryWrapper);
    }

    public PageSetPageDO findByPageId(Long pageId) {
        QueryWrapper queryWrapper = this.query().eq(PageSetPageDO::getPageId, pageId);
        return getOne(queryWrapper);
    }

    public List<PageSetPageDO> findByPageSetIds(List<Long> pageSetIds) {
        QueryWrapper queryWrapper = this.query().in(PageSetPageDO::getPageSetId, pageSetIds);
        return list(queryWrapper);
    }

}
