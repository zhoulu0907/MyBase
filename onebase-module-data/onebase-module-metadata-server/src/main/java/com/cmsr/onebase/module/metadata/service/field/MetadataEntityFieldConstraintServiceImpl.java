package com.cmsr.onebase.module.metadata.service.field;

import com.cmsr.onebase.module.metadata.dal.dataobject.field.MetadataEntityFieldConstraintDO;
import com.cmsr.onebase.module.metadata.dal.database.MetadataEntityFieldConstraintRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 字段约束 Service 实现
 *
 * @author bty418
 * @date 2025-08-18
 */
@Service
public class MetadataEntityFieldConstraintServiceImpl implements MetadataEntityFieldConstraintService {

    @Resource
    private MetadataEntityFieldConstraintRepository constraintRepository;

    @Override
    public List<MetadataEntityFieldConstraintDO> listByFieldId(Long fieldId) {
        return constraintRepository.findByFieldId(fieldId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upsert(MetadataEntityFieldConstraintDO constraint) {
        MetadataEntityFieldConstraintDO exist = constraintRepository.findOneByType(constraint.getFieldId(), constraint.getConstraintType());
        if (exist == null) {
            constraintRepository.insert(constraint);
        } else {
            constraint.setId(exist.getId());
            constraintRepository.update(constraint);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long fieldId, String constraintType) {
        MetadataEntityFieldConstraintDO exist = constraintRepository.findOneByType(fieldId, constraintType);
        if (exist != null) {
            constraintRepository.deleteById(exist.getId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByFieldId(Long fieldId) {
        constraintRepository.deleteByFieldId(fieldId);
    }
}


