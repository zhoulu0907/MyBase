package com.cmsr.onebase.module.metadata.build.service.validation;

import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationLengthRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationLengthSaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationLengthUpdateReqVO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationLengthDO;

/**
 * 长度校验 Service 接口
 *
 * 提供：新增、修改、查看（按字段）能力
 *
 * @author bty418
 * @date 2025-08-27
 */
public interface MetadataValidationLengthBuildService {

    /**
     * 按字段UUID查询长度校验配置
     *
     * @param fieldUuid 字段UUID
     * @return 长度校验DO，可能为null
     */
    MetadataValidationLengthDO getByFieldId(String fieldUuid);

    /**
     * 按字段UUID查询长度校验配置（包含规则组名称）
     *
     * @param fieldUuid 字段UUID
     * @return 长度校验VO，可能为null
     */
    ValidationLengthRespVO getByFieldIdWithRgName(String fieldUuid);

    /**
     * 新增长度校验配置
     *
     * @param vo 待保存的VO数据
     * @return 新记录ID
     */
    Long create(ValidationLengthSaveReqVO vo);

    /**
     * 修改长度校验配置
     *
     * @param vo 待更新数据（需要携带id和rgName）
     */
    void update(ValidationLengthUpdateReqVO vo);

    /**
     * 按字段UUID删除长度校验配置
     *
     * @param fieldUuid 字段UUID
     */
    void deleteByFieldId(String fieldUuid);

    /**
     * 按主键ID查询长度校验配置（包含规则组名称）
     *
     * @param id 长度校验规则主键ID
     * @return 长度校验VO，可能为null
     */
    ValidationLengthRespVO getById(Long id);

    /**
     * 按主键ID删除长度校验配置
     *
     * @param id 长度校验规则主键ID
     */
    void deleteById(Long id);
}
