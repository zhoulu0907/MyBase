package com.cmsr.onebase.module.app.core.dal.database.resource;

import com.cmsr.onebase.framework.orm.repo.BaseBizRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourceWorkbenchComponentDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppResourceWorkbenchComponentMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppResourceWorkbenchComponentTableDef.APP_RESOURCE_WORKBENCH_COMPONENT;

@Repository
public class AppWorkbenchComponentRepository extends BaseBizRepository<AppResourceWorkbenchComponentMapper, AppResourceWorkbenchComponentDO> {

    /**
     * 根据applicationId和pageUuid删除工作台组件
     *
     * @param applicationId 应用ID
     * @param pageUuid 页面UUID
     */
    public void deleteByPageUuid(Long applicationId, String pageUuid) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_RESOURCE_WORKBENCH_COMPONENT.APPLICATION_ID.eq(applicationId))
                .where(APP_RESOURCE_WORKBENCH_COMPONENT.PAGE_UUID.eq(pageUuid));
        this.remove(queryWrapper);
    }

    public List<AppResourceWorkbenchComponentDO> findByPageUuid(Long applicationId, String pageUuid) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_RESOURCE_WORKBENCH_COMPONENT.APPLICATION_ID.eq(applicationId))
                .where(APP_RESOURCE_WORKBENCH_COMPONENT.PAGE_UUID.eq(pageUuid))
                .orderBy(APP_RESOURCE_WORKBENCH_COMPONENT.COMPONENT_INDEX, true);
        return this.list(queryWrapper);
    }

}
