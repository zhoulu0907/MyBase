package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberConfigDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

/**
 * 自动编号-配置 仓储
 */
@Repository
public class MetadataAutoNumberConfigRepository extends DataRepository<MetadataAutoNumberConfigDO> {
    public MetadataAutoNumberConfigRepository() { super(MetadataAutoNumberConfigDO.class); }

    public MetadataAutoNumberConfigDO findByFieldId(Long fieldId) {
        DefaultConfigStore cs = new DefaultConfigStore();
        cs.and(MetadataAutoNumberConfigDO.FIELD_ID, fieldId);
        cs.and("deleted", 0);
        return findOne(cs);
    }

    public void deleteByFieldId(Long fieldId) {
        DefaultConfigStore cs = new DefaultConfigStore();
        cs.and(MetadataAutoNumberConfigDO.FIELD_ID, fieldId);
        deleteByConfig(cs);
    }
}


