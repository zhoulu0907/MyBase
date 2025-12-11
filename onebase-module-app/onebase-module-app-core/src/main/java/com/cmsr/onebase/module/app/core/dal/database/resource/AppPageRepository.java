package com.cmsr.onebase.module.app.core.dal.database.resource;

import com.cmsr.onebase.framework.orm.repo.BaseBizRepository;
import com.cmsr.onebase.module.app.api.appresource.dto.PageRespDTO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePageDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppResourcePageMapper;
import com.cmsr.onebase.module.app.core.enums.appresource.PageEnum;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppMenuTableDef.APP_MENU;
import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppResourcePageTableDef.APP_RESOURCE_PAGE;
import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppResourcePagesetTableDef.APP_RESOURCE_PAGESET;

/**
 * @Author：mickey.zhou
 * @Date：2025/8/6 9:31
 */
@Repository
public class AppPageRepository extends BaseBizRepository<AppResourcePageMapper, AppResourcePageDO> {

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

    public List<AppResourcePageDO> findPagesByMenuId(Long menuId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(
                        APP_RESOURCE_PAGE.ALL_COLUMNS
                ).from(APP_RESOURCE_PAGE)
                .leftJoin(APP_RESOURCE_PAGESET)
                .on(APP_RESOURCE_PAGE.PAGESET_UUID.eq(APP_RESOURCE_PAGESET.PAGESET_UUID)
                        .and(APP_RESOURCE_PAGE.APPLICATION_ID.eq(APP_RESOURCE_PAGESET.APPLICATION_ID))
                        .and(APP_RESOURCE_PAGE.VERSION_TAG.eq(APP_RESOURCE_PAGESET.VERSION_TAG)))
                .leftJoin(APP_MENU)
                .on(APP_RESOURCE_PAGESET.MENU_UUID.eq(APP_MENU.MENU_UUID)
                        .and(APP_RESOURCE_PAGE.APPLICATION_ID.eq(APP_MENU.APPLICATION_ID))
                        .and(APP_RESOURCE_PAGE.VERSION_TAG.eq(APP_MENU.VERSION_TAG))
                )
                .where(APP_MENU.ID.eq(menuId));
        return this.list(queryWrapper);
    }

    public List<Long> findIdsByAppIdAndPageSetUuid(Long applicationId, String pageSetUuid) {
        QueryWrapper queryWrapper = this.query()
                .select(APP_RESOURCE_PAGE.ID)
                .where(APP_RESOURCE_PAGE.APPLICATION_ID.eq(applicationId))
                .where(APP_RESOURCE_PAGE.PAGESET_UUID.eq(pageSetUuid));
        return this.objListAs(queryWrapper, Long.class);
    }


    public AppResourcePageDO findByAppIdAndPageUuid(Long applicationId, String pageUuid) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_RESOURCE_PAGE.APPLICATION_ID.eq(applicationId))
                .where(APP_RESOURCE_PAGE.PAGE_UUID.eq(pageUuid));
        return getOne(queryWrapper);
    }

    public PageRespDTO getByUuidInApplication(Long applicationId, String pageUuid) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_RESOURCE_PAGE.APPLICATION_ID.eq(applicationId))
                .where(APP_RESOURCE_PAGE.PAGE_UUID.eq(pageUuid));
        return this.getOneAs(queryWrapper, PageRespDTO.class);
    }
}
