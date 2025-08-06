package com.cmsr.onebase.module.app.dal.database.appresource;

import java.util.List;

import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.PageSetLabelDO;

@Repository
public class AppPageSetLabelRepository extends DataRepository {
    public AppPageSetLabelRepository() {
        super(PageSetLabelDO.class);
    }

    public List<PageSetLabelDO> findByPageSetCode(String pageSetCode) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("pageset_code", pageSetCode);
        return findAll(PageSetLabelDO.class, configs);
    }

}
