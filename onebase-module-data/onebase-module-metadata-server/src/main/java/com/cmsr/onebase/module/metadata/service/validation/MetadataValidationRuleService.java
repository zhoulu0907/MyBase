package com.cmsr.onebase.module.metadata.service.validation;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationRulePageReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationRuleRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationRuleSaveReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationTypeConfigRespVO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 校验规则 Service 接口
 *
 * @author bty418
 * @date 2025-01-25
 */
public interface MetadataValidationRuleService {

    /**
     * 创建校验规则
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createValidationRule(@Valid ValidationRuleSaveReqVO createReqVO);

    /**
     * 更新校验规则
     *
     * @param updateReqVO 更新信息
     */
    void updateValidationRule(@Valid ValidationRuleSaveReqVO updateReqVO);

    /**
     * 删除校验规则
     *
     * @param id 编号
     */
    void deleteValidationRule(Long id);

    /**
     * 获取校验规则详情
     *
     * @param id 编号
     * @return 校验规则详情
     */
    ValidationRuleRespVO getValidationRuleDetail(Long id);

    /**
     * 获取校验规则分页
     *
     * @param pageReqVO 分页查询
     * @return 校验规则分页
     */
    PageResult<ValidationRuleRespVO> getValidationRulePage(ValidationRulePageReqVO pageReqVO);

    /**
     * 获取校验类型列表
     *
     * @return 校验类型列表
     */
    List<ValidationTypeConfigRespVO> getValidationTypes();

} 