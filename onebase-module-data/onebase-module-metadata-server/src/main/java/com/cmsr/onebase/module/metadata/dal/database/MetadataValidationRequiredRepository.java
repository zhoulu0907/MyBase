package com.cmsr.onebase.module.metadata.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationRequiredDO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class MetadataValidationRequiredRepository extends DataRepository<MetadataValidationRequiredDO> {
    public MetadataValidationRequiredRepository() { super(MetadataValidationRequiredDO.class); }

    public MetadataValidationRequiredDO findOneByFieldId(Long fieldId) {
        DefaultConfigStore cs = new DefaultConfigStore();
        cs.and(MetadataValidationRequiredDO.FIELD_ID, fieldId);
        cs.and("deleted", 0);
        return findOne(cs);
    }

    public List<MetadataValidationRequiredDO> findByFieldId(Long fieldId) {
        DefaultConfigStore cs = new DefaultConfigStore();
        cs.and(MetadataValidationRequiredDO.FIELD_ID, fieldId);
        cs.and("deleted", 0);
        return findAllByConfig(cs);
    }

    public void deleteByFieldId(Long fieldId) {
        for (var item : findByFieldId(fieldId)) { deleteById(item.getId()); }
    }
}
