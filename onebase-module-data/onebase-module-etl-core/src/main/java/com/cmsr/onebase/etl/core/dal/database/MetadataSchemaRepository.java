package com.cmsr.onebase.etl.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.core.dataobject.MetadataSchemaDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class MetadataSchemaRepository extends DataRepository<MetadataSchemaDO> {

    public MetadataSchemaRepository() {
        super(MetadataSchemaDO.class);
    }


}
