package com.cmsr.onebase.module.app.dal.database.appresource;

import java.util.List;

import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.PageMetadataDO;

/**
 * @Author：mickey.zhou
 *                     @Date：2025/8/6 9:31
 */
@Repository
public class AppPageMetaRepository extends DataRepository<PageMetadataDO> {

    public AppPageMetaRepository() {
        super(PageMetadataDO.class);
    }

    public List<PageMetadataDO> findByPageId(Long pageId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq(PageMetadataDO.PAGE_ID, pageId);
        return findAllByConfig(configs);
    }

}
