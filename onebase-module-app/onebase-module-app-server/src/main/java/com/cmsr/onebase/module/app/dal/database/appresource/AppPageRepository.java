package com.cmsr.onebase.module.app.dal.database.appresource;

import java.util.List;

import com.cmsr.onebase.framework.aynline.DataRepositoryNew;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.PageDO;

/**
 * @Author：mickey.zhou
 * @Date：2025/8/6 9:31
 */
@Repository
public class AppPageRepository extends DataRepositoryNew<PageDO> {

    public AppPageRepository() {
        super(PageDO.class);
    }

    public PageDO selectPageByCode(String pageCode) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("page_code", pageCode);
        return findOne(configs);
    }

    public void updatePageName(String pageCode, String pageName) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq( "page_code", pageCode);
        PageDO pageDO = findOne(configs);
        pageDO.setPageName(pageName);
        update(pageDO);

        return;
    }


    public void deletePageByCode(String pageCode) {
         ConfigStore configs = new DefaultConfigStore();
         configs.eq("page_code", pageCode);
         deleteByConfig(configs);
    }

    public void deletePageByCodes(List<String>  pageCodes) {
        ConfigStore configs = new DefaultConfigStore();
        configs.in("page_code", pageCodes);
        deleteByConfig(configs);
   }

}
