package com.cmsr.onebase.etl.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.core.dataobject.MetadataCatalogDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class MetadataCatalogRepository extends DataRepository<MetadataCatalogDO> {

    public MetadataCatalogRepository() {
        super(MetadataCatalogDO.class);
    }


}
