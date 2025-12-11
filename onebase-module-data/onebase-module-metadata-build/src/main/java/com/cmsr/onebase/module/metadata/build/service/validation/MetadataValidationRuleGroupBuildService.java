package com.cmsr.onebase.module.metadata.build.service.validation;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRuleDefinitionVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRuleGroupPageReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRuleGroupSaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRuleGroupSimpleRespVO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRuleGroupDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 校验规则分组 Service 接口
 *
 * @author bty418
 * @date 2025-01-25
 */
public interface MetadataValidationRuleGroupBuildService {

    /**
     * 创建校验规则分组
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createValidationRuleGroup(@Valid ValidationRuleGroupSaveReqVO createReqVO);

    /**
     * 更新校验规则分组
     *
     * @param updateReqVO 更新信息
     */
    void updateValidationRuleGroup(@Valid ValidationRuleGroupSaveReqVO updateReqVO);

    /**
     * 删除校验规则分组
     *
     * @param id 编号
     */
    void deleteValidationRuleGroup(Long id);

    /**
     * 获得校验规则分组
     *
     * @param id 编号
     * @return 校验规则分组
     */
    MetadataValidationRuleGroupDO getValidationRuleGroup(Long id);

    /**
     * 根据UUID获得校验规则分组
     *
     * @param groupUuid 规则组UUID
     * @return 校验规则分组
     */
    MetadataValidationRuleGroupDO getValidationRuleGroupByUuid(String groupUuid);

    /**
     * 获得校验规则分组分页
     *
     * @param pageReqVO 分页查询
     * @return 校验规则分组分页
     */
    PageResult<MetadataValidationRuleGroupDO> getValidationRuleGroupPage(ValidationRuleGroupPageReqVO pageReqVO);

    /**
     * 获得校验规则分组分页(精简字段，用于前端下拉/选择)
     *
     * @param pageReqVO 分页查询
     * @return 精简展示分页
     */
    PageResult<ValidationRuleGroupSimpleRespVO> getValidationRuleGroupPageSimple(ValidationRuleGroupPageReqVO pageReqVO);

    /**
     * 构建规则定义的二维数组结构
     *
     * @param groupId 规则组ID
     * @return 二维数组结构的规则定义列表，外层数组元素间为OR关系，内层数组元素间为AND关系
     */
    List<List<ValidationRuleDefinitionVO>> buildValueRulesStructure(Long groupId);

    /**
     * 根据名称获取规则组
     *
     * @param rgName 规则组名称
     * @return 规则组DO，可能为null
     */
    MetadataValidationRuleGroupDO getByName(String rgName);

    /**
     * 确保存在指定字段专属规则组（RG_FIELD_{fieldId}），不存在则创建
     *
     * @param fieldId 字段ID
     * @return 规则组ID
     */
    Long ensureFieldRuleGroup(Long fieldId);

    /**
     * 直接物理删除规则组（调用方需保证无其他类型引用）。
     * @param groupId 规则组ID
     */
    void safeDeleteGroupDirect(Long groupId);

    /**
     * 根据UUID直接物理删除规则组（调用方需保证无其他类型引用）。
     * @param groupUuid 规则组UUID
     */
    void safeDeleteGroupDirect(String groupUuid);

}
