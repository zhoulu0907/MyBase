package com.cmsr.onebase.module.app.dal.database.appresource;

import org.springframework.stereotype.Repository;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.PageMetadataDO;

@Repository
public class AppPageMetadataRepository extends DataRepository {
    public AppPageMetadataRepository() {
        super(PageMetadataDO.class);
    }

}
