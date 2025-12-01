package com.cmsr.onebase.module.app.core.dal.database.resource;

import com.cmsr.onebase.framework.orm.repo.BaseBizRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetPageDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppResourcePagesetPageMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppResourcePagesetPageTableDef.APP_RESOURCE_PAGESET_PAGE;

@Repository
public class AppPageSetPageRepository extends BaseBizRepository<AppResourcePagesetPageMapper, AppResourcePagesetPageDO> {

    public void deleteByPageUuid(String pageUuid) {
        this.updateChain()
                .where(APP_RESOURCE_PAGESET_PAGE.PAGE_UUID.eq(pageUuid))
                .remove();
    }

    public void deleteByPageSetUuid(String pageSetUuid) {
        this.updateChain()
                .where(APP_RESOURCE_PAGESET_PAGE.PAGESET_UUID.eq(pageSetUuid))
                .remove();
    }

    public List<AppResourcePagesetPageDO> findByPageSetUuid(String pageSetUuid) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_RESOURCE_PAGESET_PAGE.PAGESET_UUID.eq(pageSetUuid));
        return list(queryWrapper);
    }

    public AppResourcePagesetPageDO findByPageSetUuidAndPageUuid(String pageSetUuid, String pageUuid) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_RESOURCE_PAGESET_PAGE.PAGESET_UUID.eq(pageSetUuid))
                .where(APP_RESOURCE_PAGESET_PAGE.PAGE_UUID.eq(pageUuid));
        return getOne(queryWrapper);
    }

    public AppResourcePagesetPageDO findByPageUuid(String pageUuid) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_RESOURCE_PAGESET_PAGE.PAGE_UUID.eq(pageUuid));
        return getOne(queryWrapper);
    }

    public List<AppResourcePagesetPageDO> findByPageSetUuids(List<String> pageSetUuids) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_RESOURCE_PAGESET_PAGE.PAGESET_UUID.in(pageSetUuids));
        return list(queryWrapper);
    }

}
