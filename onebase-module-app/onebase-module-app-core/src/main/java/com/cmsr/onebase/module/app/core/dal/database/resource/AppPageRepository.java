package com.cmsr.onebase.module.app.core.dal.database.resource;

import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePageDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppResourcePageMapper;
import com.cmsr.onebase.module.app.core.enums.appresource.PageEnum;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

/**
 * @Author：mickey.zhou
 * @Date：2025/8/6 9:31
 */
@Repository
public class AppPageRepository extends ServiceImpl<AppResourcePageMapper, AppResourcePageDO> {

    public void updatePageName(Long pageId, String pageName) {
        this.updateChain()
                .set(AppResourcePageDO::getPageName, pageName)
                .where(AppResourcePageDO::getId).eq(pageId)
                .update();
    }

    public List<AppResourcePageDO> findAllFormPageByPageSetId(Long pageSetId) {
        QueryWrapper queryWrapper = this.query().eq(AppResourcePageDO::getPageSetId, pageSetId)
                .eq(AppResourcePageDO::getPageType, PageEnum.FORM.getValue());
        return list(queryWrapper);
    }

    public List<AppResourcePageDO> findAllFormPageByPageSetIds(List<Long> pageSetIdList) {
        if (CollectionUtils.isEmpty(pageSetIdList)) {
            return Collections.emptyList();
        }
        QueryWrapper queryWrapper = this.query()
                .in(AppResourcePageDO::getPageSetId, pageSetIdList)
                .eq(AppResourcePageDO::getPageType, PageEnum.FORM.getValue());
        return list(queryWrapper);
    }

    /**
     * 根据pageSetId查询所有视图(list)页面
     *
     * @param pageSetId 页面集ID
     * @return 视图页面列表
     */
    public List<AppResourcePageDO> findAllViewPageByPageSetId(Long pageSetId) {
        QueryWrapper queryWrapper = this.query().eq(AppResourcePageDO::getPageSetId, pageSetId)
                .eq(AppResourcePageDO::getPageType, PageEnum.LIST.getValue());
        return list(queryWrapper);
    }
}
