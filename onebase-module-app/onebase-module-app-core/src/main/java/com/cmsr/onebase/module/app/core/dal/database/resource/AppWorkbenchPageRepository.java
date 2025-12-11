package com.cmsr.onebase.module.app.core.dal.database.resource;

import com.cmsr.onebase.framework.orm.repo.BaseBizRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourceWorkbenchPageDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppResourceWorkbenchPageMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppResourceWorkbenchPageTableDef.APP_RESOURCE_WORKBENCH_PAGE;

@Repository
public class AppWorkbenchPageRepository extends BaseBizRepository<AppResourceWorkbenchPageMapper, AppResourceWorkbenchPageDO> {

//    public void updatePageName(Long pageId, String pageName) {
//        this.updateChain()
//                .set(APP_RESOURCE_WORKBENCH_PAGE.PAGE_NAME, pageName)
//                .where(APP_RESOURCE_WORKBENCH_PAGE.ID.eq(pageId))
//                .update();
//    }

    /**
     * 根据页面集ID查询工作台页面
     *
     * @param pageSetUuid 页面集ID
     * @return 工作台页面列表
     */
    public List<AppResourceWorkbenchPageDO> findByPageSetUuid(Long applicationId, String pageSetUuid) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_RESOURCE_WORKBENCH_PAGE.APPLICATION_ID.eq(applicationId))
                .where(APP_RESOURCE_WORKBENCH_PAGE.PAGESET_UUID.eq(pageSetUuid));
        return list(queryWrapper);
    }

//    public AppResourceWorkbenchPageDO getByUuid(Long applicationId, String pageUuid) {
//        QueryWrapper queryWrapper = this.query()
//                .where(APP_RESOURCE_WORKBENCH_PAGE.APPLICATION_ID.eq(applicationId))
//                .where(APP_RESOURCE_WORKBENCH_PAGE.PAGE_UUID.eq(pageUuid));
//        return getOne(queryWrapper);
//    }

}
