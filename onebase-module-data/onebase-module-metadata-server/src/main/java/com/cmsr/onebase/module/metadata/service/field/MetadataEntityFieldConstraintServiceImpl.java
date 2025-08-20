package com.cmsr.onebase.module.metadata.service.field;

import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.FieldConstraintRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.FieldConstraintSaveReqVO;
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

    @Override
    public FieldConstraintRespVO getFieldConstraintConfig(Long fieldId) {
        List<MetadataEntityFieldConstraintDO> list = listByFieldId(fieldId);
        FieldConstraintRespVO resp = new FieldConstraintRespVO();
        
        if (list != null) {
            list.forEach(c -> {
                if ("LENGTH_RANGE".equalsIgnoreCase(c.getConstraintType())) {
                    resp.setLengthEnabled(c.getIsEnabled());
                    resp.setMinLength(c.getMinLength());
                    resp.setMaxLength(c.getMaxLength());
                    resp.setLengthPrompt(c.getPromptMessage());
                } else if ("REGEX".equalsIgnoreCase(c.getConstraintType())) {
                    resp.setRegexEnabled(c.getIsEnabled());
                    resp.setRegexPattern(c.getRegexPattern());
                    resp.setRegexPrompt(c.getPromptMessage());
                }
            });
        }
        
        return resp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveFieldConstraintConfig(FieldConstraintSaveReqVO req) {
        // 校验：LENGTH_RANGE 需校验区间合理
        if ("LENGTH_RANGE".equalsIgnoreCase(req.getConstraintType())) {
            Integer min = req.getMinLength();
            Integer max = req.getMaxLength();
            if (min != null && max != null && min > max) {
                throw new IllegalArgumentException("最小长度不能大于最大长度");
            }
        }
        
        MetadataEntityFieldConstraintDO constraint = new MetadataEntityFieldConstraintDO();
        constraint.setFieldId(req.getFieldId());
        constraint.setConstraintType(req.getConstraintType());
        constraint.setMinLength(req.getMinLength());
        constraint.setMaxLength(req.getMaxLength());
        constraint.setRegexPattern(req.getRegexPattern());
        constraint.setPromptMessage(req.getPromptMessage());
        constraint.setIsEnabled(req.getIsEnabled());
        constraint.setRunMode(req.getRunMode());
        constraint.setAppId(req.getAppId());
        
        upsert(constraint);
    }
}


