package com.cmsr.onebase.module.app.dal.database.appresource;

import java.util.List;

import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import com.cmsr.onebase.framework.aynline.DataRepositoryNew;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.PageSetPageDO;

@Repository
public class AppPageSetPageRepository extends DataRepositoryNew<PageSetPageDO> {
    public AppPageSetPageRepository() {
        super(PageSetPageDO.class);
    }

    public void deleteByPageId(Long pageId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq(PageSetPageDO.PAGE_ID, pageId);
        deleteByConfig(configs);

        return;
    }

    public void deleteByPageSetId(Long pageSetId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq(PageSetPageDO.PAGE_SET_ID, pageSetId);
        deleteByConfig(configs);

        return;
    }

    public List<PageSetPageDO> findByPageSetId(Long pageSetId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq(PageSetPageDO.PAGE_SET_ID, pageSetId);
        return findAllByConfig(configs);
    }

    public PageSetPageDO findByPageSetIdAndPageId(Long pageSetId, Long pageId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq(PageSetPageDO.PAGE_SET_ID, pageSetId);
        configs.eq(PageSetPageDO.PAGE_ID, pageId);
        return findOne(configs);
    }

    public List<PageSetPageDO> findByPageSetIds(List<Long> pageSetIds) {
        ConfigStore configs = new DefaultConfigStore();
        configs.in(PageSetPageDO.PAGE_SET_ID, pageSetIds);
        return findAllByConfig(configs);
    }

}
