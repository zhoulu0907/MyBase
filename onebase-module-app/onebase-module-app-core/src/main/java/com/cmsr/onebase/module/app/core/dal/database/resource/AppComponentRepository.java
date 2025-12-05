package com.cmsr.onebase.module.app.core.dal.database.resource;

import com.cmsr.onebase.framework.orm.repo.BaseBizRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourceComponentDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppResourceComponentMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppResourceComponentTableDef.APP_RESOURCE_COMPONENT;

@Repository
public class AppComponentRepository extends BaseBizRepository<AppResourceComponentMapper, AppResourceComponentDO> {

    public void deleteComponentByPageUuid(Long applicationId, String pageUuid) {
        QueryWrapper queryWrapper = query()
                .where(APP_RESOURCE_COMPONENT.APPLICATION_ID.eq(applicationId))
                .where(APP_RESOURCE_COMPONENT.PAGE_UUID.eq(pageUuid));
        this.remove(queryWrapper);
    }

    public List<AppResourceComponentDO> findByAppIdAndPageUuid(Long applicationId, String pageUuid) {
        QueryWrapper queryWrapper = query()
                .where(APP_RESOURCE_COMPONENT.APPLICATION_ID.eq(applicationId))
                .where(APP_RESOURCE_COMPONENT.PAGE_UUID.eq(pageUuid))
                .orderBy(APP_RESOURCE_COMPONENT.COMPONENT_INDEX, true);
        return this.list(queryWrapper);
    }

    public List<AppResourceComponentDO> findByAppIdAndPageUuids(Long applicationId, List<String> pageUuids) {
        QueryWrapper queryWrapper = query()
                .where(APP_RESOURCE_COMPONENT.APPLICATION_ID.eq(applicationId))
                .where(APP_RESOURCE_COMPONENT.PAGE_UUID.in(pageUuids));
        return this.list(queryWrapper);
    }
}
