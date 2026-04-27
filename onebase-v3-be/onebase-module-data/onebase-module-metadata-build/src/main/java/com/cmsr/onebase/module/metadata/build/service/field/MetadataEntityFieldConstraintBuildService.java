package com.cmsr.onebase.module.metadata.build.service.field;

import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.FieldConstraintRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.FieldConstraintSaveReqVO;

/**
 * 字段约束 Service（长度/格式等），基于新校验表实现
 */
public interface MetadataEntityFieldConstraintBuildService {

    /** 按字段UUID清空所有约束（长度/格式/范围/必填/唯一/子表非空等，逐步覆盖） */
    void deleteByFieldId(String fieldUuid);

    /** 获取字段约束配置（目前支持长度、正则） */
    FieldConstraintRespVO getFieldConstraintConfig(String fieldUuid);

    /** 保存/更新字段约束配置（目前支持 LENGTH_RANGE / REGEX） */
    void saveFieldConstraintConfig(FieldConstraintSaveReqVO req);

    /** 删除某类型约束（可选） */
    void delete(String fieldUuid, String constraintType);
}


