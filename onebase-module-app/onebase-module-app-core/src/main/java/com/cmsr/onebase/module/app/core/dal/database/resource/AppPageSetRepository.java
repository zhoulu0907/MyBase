package com.cmsr.onebase.module.app.core.dal.database.resource;

import com.cmsr.onebase.framework.orm.repo.BaseBizRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppResourcePagesetMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppResourcePagesetTableDef.APP_RESOURCE_PAGESET;

@Repository
public class AppPageSetRepository extends BaseBizRepository<AppResourcePagesetMapper, AppResourcePagesetDO> {

//    public AppResourcePagesetDO getByUuid(String pageSetUuid) {
//        QueryWrapper queryWrapper = this.query()
//                .where(APP_RESOURCE_PAGESET.PAGESET_UUID.eq(pageSetUuid));
//        return getOne(queryWrapper);
//    }

    public List<String> findPageSetUuidListByMenuUuids(Long applicationId, Collection<String> menuUuids) {
        QueryWrapper queryWrapper = this.query()
                .select(APP_RESOURCE_PAGESET.PAGESET_UUID)
                .where(APP_RESOURCE_PAGESET.APPLICATION_ID.eq(applicationId))
                .where(APP_RESOURCE_PAGESET.MENU_UUID.in(menuUuids));
        return objListAs(queryWrapper, String.class);
    }

    public AppResourcePagesetDO findPageSetByAppIdAndMenuUuid(Long applicationId, String menuUuid) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_RESOURCE_PAGESET.APPLICATION_ID.eq(applicationId))
                .where(APP_RESOURCE_PAGESET.MENU_UUID.eq(menuUuid));
        return getOne(queryWrapper);
    }

//    public String findPageSetUuidByMenuUuid(String menuUuid) {
//        QueryWrapper queryWrapper = this.query()
//                .select(APP_RESOURCE_PAGESET.PAGESET_UUID)
//                .where(APP_RESOURCE_PAGESET.MENU_UUID.eq(menuUuid));
//        return this.getObjAs(queryWrapper, String.class);
//    }

    public List<AppResourcePagesetDO> findByMenuUuids(Long applicationId, List<String> menuUuids) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_RESOURCE_PAGESET.APPLICATION_ID.eq(applicationId))
                .where(APP_RESOURCE_PAGESET.MENU_UUID.in(menuUuids));
        return list(queryWrapper);
    }

    public void deletePageSetByMenuUuid(String menuUuid) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_RESOURCE_PAGESET.MENU_UUID.eq(menuUuid));
        remove(queryWrapper);
    }

    public List<AppResourcePagesetDO> findByMenuUuidAndType(Long applicationId,List<String> menuUuids, Integer pageSetType) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_RESOURCE_PAGESET.APPLICATION_ID.eq(applicationId))
                .where(APP_RESOURCE_PAGESET.MENU_UUID.in(menuUuids))
                .where(APP_RESOURCE_PAGESET.PAGESET_TYPE.eq(pageSetType, pageSetType != null));
        return list(queryWrapper);
    }

    public String getMainMetadataByAppIdAndUuid(Long applicationId, String pageSetUuid) {
        QueryWrapper queryWrapper = this.query()
                .select(APP_RESOURCE_PAGESET.MAIN_METADATA)
                .where(APP_RESOURCE_PAGESET.APPLICATION_ID.eq(applicationId))
                .where(APP_RESOURCE_PAGESET.PAGESET_UUID.eq(pageSetUuid));
        return this.getObjAs(queryWrapper, String.class);
    }
}
