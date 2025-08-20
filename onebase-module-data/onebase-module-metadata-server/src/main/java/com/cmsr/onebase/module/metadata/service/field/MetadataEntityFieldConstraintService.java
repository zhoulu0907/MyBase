package com.cmsr.onebase.module.metadata.service.field;

import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.FieldConstraintRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.FieldConstraintSaveReqVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.field.MetadataEntityFieldConstraintDO;

import java.util.List;

/**
 * 字段约束 Service
 *
 * @author bty418
 * @date 2025-08-18
 */
public interface MetadataEntityFieldConstraintService {

    List<MetadataEntityFieldConstraintDO> listByFieldId(Long fieldId);

    void upsert(MetadataEntityFieldConstraintDO constraint);

    void delete(Long fieldId, String constraintType);

    void deleteByFieldId(Long fieldId);
    
    /**
     * 获取字段约束配置
     *
     * @param fieldId 字段ID
     * @return 约束配置响应VO
     */
    FieldConstraintRespVO getFieldConstraintConfig(Long fieldId);
    
    /**
     * 保存/更新字段约束配置
     *
     * @param req 约束配置请求VO
     */
    void saveFieldConstraintConfig(FieldConstraintSaveReqVO req);
}


