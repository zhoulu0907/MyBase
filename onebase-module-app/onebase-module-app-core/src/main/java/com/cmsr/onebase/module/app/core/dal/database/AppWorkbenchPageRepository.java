package com.cmsr.onebase.module.app.core.dal.database;

import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourceWorkbenchPageDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppResourceWorkbenchPageMapper;
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
        this.deletePageByIds(pageIds);
    }

    public List<AppResourceWorkbenchPageDO> findByPageIds(List<Long> pageIds) {
        return this.listByIds(pageIds);
    }

}
