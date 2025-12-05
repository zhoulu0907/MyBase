package com.cmsr.onebase.module.app.core.dal.database.resource;

import com.cmsr.onebase.framework.orm.repo.BaseBizRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePageDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppResourcePageMapper;
import com.cmsr.onebase.module.app.core.enums.appresource.PageEnum;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppResourcePageTableDef.APP_RESOURCE_PAGE;

/**
 * @Author：mickey.zhou
 * @Date：2025/8/6 9:31
 */
@Repository
public class AppPageRepository extends BaseBizRepository<AppResourcePageMapper, AppResourcePageDO> {

//    public AppResourcePageDO getByUuid(String pageUuid) {
//        QueryWrapper queryWrapper = this.query()
//                .where(APP_RESOURCE_PAGE.PAGE_UUID.eq(pageUuid));
//        return getOne(queryWrapper);
//    }

//    public String getPageSetUuidByPageUuid(String pageUuid) {
//        QueryWrapper queryWrapper = this.query()
//                .select(APP_RESOURCE_PAGE.PAGESET_UUID)
//                .where(APP_RESOURCE_PAGE.PAGE_UUID.eq(pageUuid));
//        return getObjAs(queryWrapper, String.class);
//    }

    public void updatePageName(Long pageId, String pageName) {
        this.updateChain()
                .set(APP_RESOURCE_PAGE.PAGE_NAME, pageName)
                .where(APP_RESOURCE_PAGE.ID.eq(pageId))
                .update();
    }

    public List<AppResourcePageDO> findAllFormPageByAppIdAndPageSetUuid(Long applicationId, String pageSetUuid) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_RESOURCE_PAGE.APPLICATION_ID.eq(applicationId))
                .where(APP_RESOURCE_PAGE.PAGESET_UUID.eq(pageSetUuid))
                .and(APP_RESOURCE_PAGE.PAGE_TYPE.eq(PageEnum.FORM.getValue()));
        return list(queryWrapper);
    }

    public List<AppResourcePageDO> findAllFormPageByPageSetUuids(Long applicationId, List<String> pageSetUuidList) {
        if (CollectionUtils.isEmpty(pageSetUuidList)) {
            return Collections.emptyList();
        }
        QueryWrapper queryWrapper = this.query()
                .where(APP_RESOURCE_PAGE.APPLICATION_ID.eq(applicationId))
                .where(APP_RESOURCE_PAGE.PAGESET_UUID.in(pageSetUuidList))
                .and(APP_RESOURCE_PAGE.PAGE_TYPE.eq(PageEnum.FORM.getValue()));
        return list(queryWrapper);
    }

    public List<AppResourcePageDO> findByPageSetUuid(Long applicationId, String pageSetUuid) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_RESOURCE_PAGE.APPLICATION_ID.eq(applicationId))
                .where(APP_RESOURCE_PAGE.PAGESET_UUID.eq(pageSetUuid));
        return list(queryWrapper);
    }

//    public void deleteByUuidList(Collection<String> pageUuids) {
//        if (CollectionUtils.isEmpty(pageUuids)) {
//            return;
//        }
//        this.updateChain()
//                .where(APP_RESOURCE_PAGE.PAGE_UUID.in(pageUuids))
//                .remove();
//    }

    public List<Long> findIdsByAppIdAndPageSetUuid(Long applicationId, String pageSetUuid) {
        QueryWrapper queryWrapper = this.query()
                .select(APP_RESOURCE_PAGE.ID)
                .where(APP_RESOURCE_PAGE.APPLICATION_ID.eq(applicationId))
                .where(APP_RESOURCE_PAGE.PAGESET_UUID.eq(pageSetUuid));
        return this.objListAs(queryWrapper, Long.class);
    }


}
