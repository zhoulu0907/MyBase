package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLInfoDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class ETLInfoRepository extends DataRepository<ETLInfoDO> {
    public ETLInfoRepository(Class<ETLInfoDO> defaultClazz) {
        super(defaultClazz);
    }


}
