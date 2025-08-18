package com.cmsr.onebase.module.metadata.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.metadata.dal.dataobject.field.MetadataEntityFieldConstraintDO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 字段约束 仓储
 *
 * @author bty418
 * @date 2025-08-18
 */
@Repository
@Slf4j
public class MetadataEntityFieldConstraintRepository extends DataRepository<MetadataEntityFieldConstraintDO> {

    public MetadataEntityFieldConstraintRepository() {
        super(MetadataEntityFieldConstraintDO.class);
    }

    public List<MetadataEntityFieldConstraintDO> findByFieldId(Long fieldId) {
        DefaultConfigStore cs = new DefaultConfigStore();
        cs.and(MetadataEntityFieldConstraintDO.FIELD_ID, fieldId);
        cs.and("deleted", 0);
        return findAllByConfig(cs);
    }

    public MetadataEntityFieldConstraintDO findOneByType(Long fieldId, String constraintType) {
        DefaultConfigStore cs = new DefaultConfigStore();
        cs.and(MetadataEntityFieldConstraintDO.FIELD_ID, fieldId);
        cs.and(MetadataEntityFieldConstraintDO.CONSTRAINT_TYPE, constraintType);
        cs.and("deleted", 0);
        return findOne(cs);
    }

    public void deleteByFieldId(Long fieldId) {
        DefaultConfigStore cs = new DefaultConfigStore();
        cs.and(MetadataEntityFieldConstraintDO.FIELD_ID, fieldId);
        List<MetadataEntityFieldConstraintDO> list = findAllByConfig(cs);
        for (MetadataEntityFieldConstraintDO item : list) {
            deleteById(item.getId());
        }
    }
}


