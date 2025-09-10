package com.cmsr.onebase.module.app.core.dal.database.appresource;

import java.util.List;

import com.cmsr.onebase.module.app.core.dal.dataobject.appresource.PageDO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import com.cmsr.onebase.framework.aynline.DataRepository;

/**
 * @Author：mickey.zhou
 *                     @Date：2025/8/6 9:31
 */
@Repository
public class AppPageRepository extends DataRepository<PageDO> {

    public AppPageRepository() {
        super(PageDO.class);
    }

    public void updatePageName(Long pageId, String pageName) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("id", pageId);
        PageDO pageDO = findOne(configs);
        pageDO.setPageName(pageName);
        update(pageDO);

        return;
    }

    public void deletePageByIds(List<Long> pageIds) {
        ConfigStore configs = new DefaultConfigStore();
        configs.in("id", pageIds);
        deleteByConfig(configs);
    }

    public List<PageDO> findByPageIds(List<Long> pageIds) {
        ConfigStore configs = new DefaultConfigStore();
        configs.in("id", pageIds);
        return findAllByConfig(configs);
    }

}
