package com.cmsr.onebase.module.app.core.dal.database.resource;

import com.cmsr.onebase.framework.orm.repo.BaseBizRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePageDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppResourcePageMapper;
import com.cmsr.onebase.module.app.core.enums.appresource.PageEnum;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppResourcePageTableDef.APP_RESOURCE_PAGE;

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

    public List<AppResourcePageDO> findAllFormPageByPageSetId(String pageSetUuid) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_RESOURCE_PAGE.PAGESET_UUID.eq(pageSetUuid))
                .and(APP_RESOURCE_PAGE.PAGE_TYPE.eq(PageEnum.FORM.getValue()));
        return list(queryWrapper);
    }

    public List<AppResourcePageDO> findAllFormPageByPageSetIds(List<String> pageSetUuidList) {
        if (CollectionUtils.isEmpty(pageSetUuidList)) {
            return Collections.emptyList();
        }
        QueryWrapper queryWrapper = this.query()
                .where(APP_RESOURCE_PAGE.PAGESET_UUID.in(pageSetUuidList))
                .and(APP_RESOURCE_PAGE.PAGE_TYPE.eq(PageEnum.FORM.getValue()));
        return list(queryWrapper);
    }
}
