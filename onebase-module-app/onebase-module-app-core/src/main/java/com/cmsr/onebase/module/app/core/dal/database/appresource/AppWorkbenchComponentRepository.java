package com.cmsr.onebase.module.app.core.dal.database.appresource;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.appresource.ComponentDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.appresource.workbench.WorkbenchComponentDO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AppWorkbenchComponentRepository extends DataRepository<WorkbenchComponentDO> {

    public AppWorkbenchComponentRepository() {
        super(WorkbenchComponentDO.class);
    }

    public void deleteComponentByPageId(Long pageId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq(ComponentDO.PAGE_ID, pageId);
        deleteByConfig(configs);
    }

    public List<WorkbenchComponentDO> findByPageId(Long pageId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq(WorkbenchComponentDO.PAGE_ID, pageId);
        configs.order(WorkbenchComponentDO.COMPONENT_INDEX, Order.TYPE.ASC);
        return findAllByConfig(configs);
    }


}
