package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationChildNotEmptyDO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class MetadataValidationChildNotEmptyRepository extends DataRepository<MetadataValidationChildNotEmptyDO> {
    public MetadataValidationChildNotEmptyRepository() { super(MetadataValidationChildNotEmptyDO.class); }

    public MetadataValidationChildNotEmptyDO findOneByFieldId(Long fieldId) {
        DefaultConfigStore cs = new DefaultConfigStore();
        cs.and(MetadataValidationChildNotEmptyDO.FIELD_ID, fieldId);
        cs.and("deleted", 0);
        return findOne(cs);
    }

    public List<MetadataValidationChildNotEmptyDO> findByFieldId(Long fieldId) {
        DefaultConfigStore cs = new DefaultConfigStore();
        cs.and(MetadataValidationChildNotEmptyDO.FIELD_ID, fieldId);
        cs.and("deleted", 0);
        return findAllByConfig(cs);
    }

    public void deleteByFieldId(Long fieldId) {
        for (var item : findByFieldId(fieldId)) { deleteById(item.getId()); }
    }

    public List<MetadataValidationChildNotEmptyDO> findByGroupId(Long groupId) {
        DefaultConfigStore cs = new DefaultConfigStore();
        cs.and("group_id", groupId);
        cs.and("deleted", 0);
        return findAllByConfig(cs);
    }
}
