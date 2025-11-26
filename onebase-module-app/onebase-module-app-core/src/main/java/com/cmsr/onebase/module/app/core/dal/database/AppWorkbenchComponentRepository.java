package com.cmsr.onebase.module.app.core.dal.database;

import com.cmsr.onebase.module.app.core.dal.dataobject.WorkbenchComponentDO;
import com.cmsr.onebase.module.app.core.dal.mapper.WorkbenchComponentMapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AppWorkbenchComponentRepository extends ServiceImpl<WorkbenchComponentMapper, WorkbenchComponentDO> {


    public void deleteComponentByPageId(Long pageId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq(WorkbenchComponentDO.PAGE_ID, pageId);
        deleteByConfig(configs);
    }

    public List<WorkbenchComponentDO> findByPageId(Long pageId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq(WorkbenchComponentDO.PAGE_ID, pageId);
        configs.order(WorkbenchComponentDO.COMPONENT_INDEX, Order.TYPE.ASC);
        return findAllByConfig(configs);
    }


}
