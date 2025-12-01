package com.cmsr.onebase.module.app.core.dal.database.resource;

import com.cmsr.onebase.framework.orm.repo.BaseBizRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppResourcePagesetMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppResourcePagesetTableDef.APP_RESOURCE_PAGESET;

@Repository
public class AppPageSetRepository extends BaseBizRepository<AppResourcePagesetMapper, AppResourcePagesetDO> {

    public AppResourcePagesetDO findPageSetByMenuUuid(String menuUuid) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_RESOURCE_PAGESET.MENU_UUID.eq(menuUuid));
        return getOne(queryWrapper);
    }

    public List<AppResourcePagesetDO> findByMenuUuids(List<String> menuUuids) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_RESOURCE_PAGESET.MENU_UUID.in(menuUuids));
        return list(queryWrapper);
    }

    public void deletePageSetByMenuUuid(String menuUuid) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_RESOURCE_PAGESET.MENU_UUID.eq(menuUuid));
        remove(queryWrapper);
    }

    public List<AppResourcePagesetDO> findByMenuUuidAndType(List<String> menuUuids, Integer pageSetType) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_RESOURCE_PAGESET.MENU_UUID.in(menuUuids))
                .and(APP_RESOURCE_PAGESET.PAGESET_TYPE.eq(pageSetType, pageSetType != null));
        return list(queryWrapper);
    }

}
