package com.cmsr.onebase.module.metadata.service.validation;

import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationLengthDO;

/**
 * 长度校验 Service 接口
 *
 * 提供：新增、修改、查看（按字段）能力
 *
 * @author bty418
 * @date 2025-08-27
 */
public interface MetadataValidationLengthService {

    /**
     * 按字段ID查询长度校验配置
     *
     * @param fieldId 字段ID
     * @return 长度校验DO，可能为null
     */
    MetadataValidationLengthDO getByFieldId(Long fieldId);

    /**
     * 新增长度校验配置
     *
     * @param data 待保存数据（需要包含fieldId；groupId可为空将自动创建；entityId/appId将基于字段回填）
     * @return 新记录ID
     */
    Long create(MetadataValidationLengthDO data);

    /**
     * 修改长度校验配置
     *
     * @param data 待更新数据（需要携带id）
     */
    void update(MetadataValidationLengthDO data);

    /**
     * 按字段ID删除长度校验配置
     *
     * @param fieldId 字段ID
     */
    void deleteByFieldId(Long fieldId);
}
