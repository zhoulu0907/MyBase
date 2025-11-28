package com.cmsr.onebase.module.app.core.dal.database.resource;

import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourceWorkbenchPageDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppResourceWorkbenchPageMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AppWorkbenchPageRepository extends ServiceImpl<AppResourceWorkbenchPageMapper, AppResourceWorkbenchPageDO> {

    public void updatePageName(Long pageId, String pageName) {
        AppResourceWorkbenchPageDO pageDO = new AppResourceWorkbenchPageDO();
        pageDO.setPageName(pageName);
        pageDO.setId(pageId);
        updateById(pageDO, true);
    }

    public void deletePageByIds(List<Long> pageIds) {
        this.removeByIds(pageIds);
    }

    public List<AppResourceWorkbenchPageDO> findByPageIds(List<Long> pageIds) {
        return this.listByIds(pageIds);
    }

    /**
     * 根据页面集ID查询工作台页面
     *
     * @param pageSetId 页面集ID
     * @return 工作台页面列表
     */
    public List<AppResourceWorkbenchPageDO> findByPageSetId(Long pageSetId) {
        QueryWrapper queryWrapper = this.query().eq(AppResourceWorkbenchPageDO::getPageSetId, pageSetId);
        return list(queryWrapper);
    }

}
