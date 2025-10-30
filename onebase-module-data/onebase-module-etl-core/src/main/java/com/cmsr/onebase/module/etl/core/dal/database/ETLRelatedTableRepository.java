package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLRelatedTableDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class ETLRelatedTableRepository extends DataRepository<ETLRelatedTableDO> {
    public ETLRelatedTableRepository(Class<ETLRelatedTableDO> defaultClazz) {
        super(defaultClazz);
    }
}
