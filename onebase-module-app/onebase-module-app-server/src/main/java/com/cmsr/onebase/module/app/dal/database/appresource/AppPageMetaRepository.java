package com.cmsr.onebase.module.app.dal.database.appresource;

import org.springframework.stereotype.Repository;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.PageMetadataDO;

/**
 * @Author：mickey.zhou
 * @Date：2025/8/6 9:31
 */
@Repository
public class AppPageMetaRepository extends DataRepository {

    public AppPageMetaRepository() {
        super(PageMetadataDO.class);
    }


}
