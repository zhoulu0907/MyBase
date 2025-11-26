package com.cmsr.onebase.module.app.core.dal.database;

import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePageDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppResourcePageMapper;
import com.cmsr.onebase.module.app.core.enums.appresource.PageEnum;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

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

}
