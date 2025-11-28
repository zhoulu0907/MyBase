package com.cmsr.onebase.module.app.core.dal.database.resource;

import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourceWorkbenchComponentDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppResourceWorkbenchComponentMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppResourceWorkbenchComponentTableDef.APP_RESOURCE_WORKBENCH_COMPONENT;

@Repository
public class AppWorkbenchComponentRepository extends ServiceImpl<AppResourceWorkbenchComponentMapper, AppResourceWorkbenchComponentDO> {


    public void deleteComponentByPageId(Long pageId) {
        QueryWrapper queryWrapper = query().where(APP_RESOURCE_WORKBENCH_COMPONENT.PAGE_ID.eq(pageId));
        this.remove(queryWrapper);
    }

    public List<AppResourceWorkbenchComponentDO> findByPageId(Long pageId) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_RESOURCE_WORKBENCH_COMPONENT.PAGE_ID.eq(pageId))
                .orderBy(APP_RESOURCE_WORKBENCH_COMPONENT.COMPONENT_INDEX, true);
        return this.list(queryWrapper);
    }

}
