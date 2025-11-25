package com.cmsr.onebase.module.app.core.dal.database.appresource;

import com.cmsr.onebase.module.app.core.dal.dataobject.appresource.PageDO;
import com.cmsr.onebase.module.app.core.dal.mapper.appresource.AppPageMapper;
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
public class AppPageRepository extends ServiceImpl<AppPageMapper, PageDO> {

    public void updatePageName(Long pageId, String pageName) {
        this.updateChain()
                .set(PageDO::getPageName, pageName)
                .where(PageDO::getId).eq(pageId)
                .update();
    }

    public List<PageDO> findAllFormPageByPageSetId(Long pageSetId) {
        QueryWrapper queryWrapper = this.query().eq(PageDO::getPageSetId, pageSetId)
                .eq(PageDO::getPageType, PageEnum.FORM.getValue());
        return list(queryWrapper);
    }

}
