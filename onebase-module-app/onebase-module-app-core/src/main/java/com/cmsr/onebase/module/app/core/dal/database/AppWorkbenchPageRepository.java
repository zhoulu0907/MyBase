package com.cmsr.onebase.module.app.core.dal.database;

import com.cmsr.onebase.module.app.core.dal.dataobject.WorkBenchPageDO;
import com.cmsr.onebase.module.app.core.dal.mapper.WorkBenchPageMapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AppWorkbenchPageRepository extends ServiceImpl<WorkBenchPageMapper, WorkBenchPageDO> {


    public void updatePageName(Long pageId, String pageName) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("id", pageId);
        WorkBenchPageDO pageDO = findOne(configs);
        pageDO.setPageName(pageName);
        update(pageDO);

        return;
    }

    public void deletePageByIds(List<Long> pageIds) {
        ConfigStore configs = new DefaultConfigStore();
        configs.in("id", pageIds);
        deleteByConfig(configs);
    }

    public List<WorkBenchPageDO> findByPageIds(List<Long> pageIds) {
        ConfigStore configs = new DefaultConfigStore();
        configs.in("id", pageIds);
        return findAllByConfig(configs);
    }

}
