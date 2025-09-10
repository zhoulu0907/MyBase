package com.cmsr.onebase.module.app.core.dal.database.appresource;

import java.util.List;

import com.cmsr.onebase.module.app.core.dal.dataobject.appresource.PageSetLabelDO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import com.cmsr.onebase.framework.aynline.DataRepository;

@Repository
public class AppPageSetLabelRepository extends DataRepository<PageSetLabelDO> {
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
