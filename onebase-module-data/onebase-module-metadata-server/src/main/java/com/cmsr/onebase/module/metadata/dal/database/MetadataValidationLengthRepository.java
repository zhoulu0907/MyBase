package com.cmsr.onebase.module.metadata.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationLengthDO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class MetadataValidationLengthRepository extends DataRepository<MetadataValidationLengthDO> {
    public MetadataValidationLengthRepository() { super(MetadataValidationLengthDO.class); }

    public MetadataValidationLengthDO findOneByFieldId(Long fieldId) {
        DefaultConfigStore cs = new DefaultConfigStore();
        cs.and(MetadataValidationLengthDO.FIELD_ID, fieldId);
        cs.and("deleted", 0);
        return findOne(cs);
    }

    public List<MetadataValidationLengthDO> findByFieldId(Long fieldId) {
        DefaultConfigStore cs = new DefaultConfigStore();
        cs.and(MetadataValidationLengthDO.FIELD_ID, fieldId);
        cs.and("deleted", 0);
        return findAllByConfig(cs);
    }

    public void deleteByFieldId(Long fieldId) {
        for (var item : findByFieldId(fieldId)) { deleteById(item.getId()); }
    }
}
