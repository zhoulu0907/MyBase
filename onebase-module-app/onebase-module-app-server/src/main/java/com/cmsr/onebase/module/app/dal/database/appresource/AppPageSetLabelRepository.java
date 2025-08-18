package com.cmsr.onebase.module.app.dal.database.appresource;

import java.util.List;

import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import com.cmsr.onebase.framework.aynline.DataRepositoryNew;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.PageSetLabelDO;

@Repository
public class AppPageSetLabelRepository extends DataRepositoryNew<PageSetLabelDO> {
    public AppPageSetLabelRepository() {
        super(PageSetLabelDO.class);
    }

    public List<PageSetLabelDO> findByPageSetId(Long pageSetId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq(PageSetLabelDO.PAGE_SET_ID, pageSetId);
        return findAllByConfig(configs);
    }

    public List<PageSetLabelDO> findByPageSetIds(List<Long> pageSetIds) {
        ConfigStore configs = new DefaultConfigStore();
        configs.in(PageSetLabelDO.PAGE_SET_ID, pageSetIds);
        return findAllByConfig(configs);
    }

}
