package com.cmsr.onebase.module.metadata.build.service.validation;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRuleDefinitionVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRuleGroupPageReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRuleGroupSaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRuleGroupSimpleRespVO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationRuleGroupRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRuleDefinitionDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRuleGroupDO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.metadata.core.util.StatusEnumUtil;

/**
 * 校验规则分组 Service 实现类
 *
 * @author bty418
 * @date 2025-01-25
 */
@Service
@Slf4j
public class MetadataValidationRuleGroupBuildServiceImpl implements MetadataValidationRuleGroupBuildService {

    @Resource
    private MetadataValidationRuleGroupRepository validationRuleGroupRepository;

    @Resource
    private MetadataValidationRuleDefinitionBuildService validationRuleDefinitionService;

    @Resource
    private ModelMapper modelMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createValidationRuleGroup(@Valid ValidationRuleGroupSaveReqVO createReqVO) {
        // 校验规则组名称唯一性
        validateRuleGroupNameUnique(null, createReqVO.getRgName());

        // 插入校验规则分组
        MetadataValidationRuleGroupDO ruleGroup = BeanUtils.toBean(createReqVO, MetadataValidationRuleGroupDO.class);
        // 设置默认状态
        if (ruleGroup.getRgStatus() == null) {
            ruleGroup.setRgStatus(StatusEnumUtil.ACTIVE);
        }

        validationRuleGroupRepository.upsert(ruleGroup);
        Long groupId = ruleGroup.getId();

        // 处理规则定义
        if (!CollectionUtils.isEmpty(createReqVO.getValueRules())) {
            saveValueRules(groupId, createReqVO.getValueRules());
        }

        return groupId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateValidationRuleGroup(@Valid ValidationRuleGroupSaveReqVO updateReqVO) {
        // 校验存在
        validateValidationRuleGroupExists(updateReqVO.getId());

        // 校验规则组名称唯一性
        validateRuleGroupNameUnique(updateReqVO.getId(), updateReqVO.getRgName());

        // 更新校验规则分组
        MetadataValidationRuleGroupDO updateObj = BeanUtils.toBean(updateReqVO, MetadataValidationRuleGroupDO.class);
        validationRuleGroupRepository.update(updateObj); // 使用update而不是upsert，避免主键冲突

        // 处理规则定义：仅当前端传入了新的规则结构时，才删除重建；
        // 否则保留原有规则，便于只更新valMethod/popPrompt/popType等基础信息。
        if (updateReqVO.getValueRules() != null) {
            Long groupId = updateReqVO.getId();
            // 传入了空列表视为清空规则
            validationRuleDefinitionService.deleteByGroupId(groupId);
            if (!CollectionUtils.isEmpty(updateReqVO.getValueRules())) {
                saveValueRules(groupId, updateReqVO.getValueRules());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteValidationRuleGroup(Long id) {
        // 校验存在
        validateValidationRuleGroupExists(id);

        // 先删除关联的规则定义
        validationRuleDefinitionService.deleteByGroupId(id);

        // 删除校验规则分组
        validationRuleGroupRepository.deleteById(id);
    }

    @Override
    public MetadataValidationRuleGroupDO getValidationRuleGroup(Long id) {
        return validationRuleGroupRepository.findById(id);
    }

    @Override
    public PageResult<MetadataValidationRuleGroupDO> getValidationRuleGroupPage(ValidationRuleGroupPageReqVO pageReqVO) {
        return validationRuleGroupRepository.selectPage(pageReqVO.getPageNo(), pageReqVO.getPageSize(), pageReqVO.getRgName());
    }

    @Override
    public PageResult<ValidationRuleGroupSimpleRespVO> getValidationRuleGroupPageSimple(ValidationRuleGroupPageReqVO pageReqVO) {
        PageResult<MetadataValidationRuleGroupDO> page = validationRuleGroupRepository.selectPage(pageReqVO.getPageNo(), pageReqVO.getPageSize(), pageReqVO.getRgName());
        List<ValidationRuleGroupSimpleRespVO> list = new ArrayList<>();
        for (MetadataValidationRuleGroupDO groupDO : page.getList()) {
            ValidationRuleGroupSimpleRespVO vo = new ValidationRuleGroupSimpleRespVO();
            vo.setId(groupDO.getId());
            vo.setRgName(groupDO.getRgName());
            // 构建派生字段
            buildDerivedFieldsForSimpleVO(vo, groupDO.getId(), groupDO.getPopPrompt());
            list.add(vo);
        }
        return new PageResult<>(list, page.getTotal());
    }

    @Override
    public MetadataValidationRuleGroupDO getByName(String rgName) {
        return validationRuleGroupRepository.selectByRgName(rgName, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long ensureFieldRuleGroup(Long fieldId) {
        String rgName = "RG_FIELD_" + fieldId;
        MetadataValidationRuleGroupDO group = validationRuleGroupRepository.selectByRgName(rgName, null);
        if (group == null) {
            group = new MetadataValidationRuleGroupDO();
            group.setRgName(rgName);
            group.setRgDesc("字段" + fieldId + "的规则组");
            group.setRgStatus(StatusEnumUtil.ACTIVE);
            validationRuleGroupRepository.insert(group);
        }
        return group.getId();
    }

    /**
     * 校验校验规则分组是否存在
     *
     * @param id 校验规则分组编号
     */
    private void validateValidationRuleGroupExists(Long id) {
        if (validationRuleGroupRepository.findById(id) == null) {
            throw exception(ErrorCodeConstants.VALIDATION_RULE_GROUP_NOT_EXISTS);
        }
    }

    /**
     * 校验规则组名称唯一性
     *
     * @param id 校验规则分组编号（用于修改时排除自身）
     * @param rgName 规则组名称
     */
    private void validateRuleGroupNameUnique(Long id, String rgName) {
        if (!StringUtils.hasText(rgName)) {
            return;
        }
        MetadataValidationRuleGroupDO ruleGroup = validationRuleGroupRepository.selectByRgName(rgName, id);
        if (ruleGroup != null) {
            throw exception(ErrorCodeConstants.VALIDATION_RULE_GROUP_NAME_DUPLICATE);
        }
    }

    /**
     * 保存规则定义（处理二维数组结构）
     *
     * @param groupId 规则组ID
     * @param valueRules 规则定义二维数组，外层数组元素间为OR关系，内层数组元素间为AND关系
     */
    private void saveValueRules(Long groupId, List<List<ValidationRuleDefinitionVO>> valueRules) {
        if (CollectionUtils.isEmpty(valueRules)) {
            return;
        }

        // 创建主OR规则节点
        MetadataValidationRuleDefinitionDO mainOrRule = new MetadataValidationRuleDefinitionDO();
        mainOrRule.setGroupId(groupId);
        mainOrRule.setLogicType("LOGIC");
        mainOrRule.setLogicOperator("OR");
        mainOrRule.setStatus(StatusEnumUtil.ACTIVE);
        validationRuleDefinitionService.saveRuleDefinition(mainOrRule);
        Long mainOrRuleId = mainOrRule.getId();

        // 处理每个OR分组（内层数组）
        for (List<ValidationRuleDefinitionVO> andGroup : valueRules) {
            if (CollectionUtils.isEmpty(andGroup)) {
                continue;
            }

            if (andGroup.size() == 1) {
                // 只有一个条件，直接添加到主OR节点下
                ValidationRuleDefinitionVO singleRule = andGroup.get(0);
                MetadataValidationRuleDefinitionDO ruleDO = modelMapper.map(singleRule, MetadataValidationRuleDefinitionDO.class);
                ruleDO.setGroupId(groupId);
                ruleDO.setParentRuleId(mainOrRuleId);
                if (ruleDO.getStatus() == null) {
                    ruleDO.setStatus(StatusEnumUtil.ACTIVE);
                }
                validationRuleDefinitionService.saveRuleDefinition(ruleDO);
            } else {
                // 多个条件，创建AND分组节点
                MetadataValidationRuleDefinitionDO andGroupRule = new MetadataValidationRuleDefinitionDO();
                andGroupRule.setGroupId(groupId);
                andGroupRule.setParentRuleId(mainOrRuleId);
                andGroupRule.setLogicType("LOGIC");
                andGroupRule.setLogicOperator("AND");
                andGroupRule.setStatus(StatusEnumUtil.ACTIVE);
                validationRuleDefinitionService.saveRuleDefinition(andGroupRule);
                Long andGroupRuleId = andGroupRule.getId();

                // 添加AND分组内的所有条件
                for (ValidationRuleDefinitionVO conditionRule : andGroup) {
                    MetadataValidationRuleDefinitionDO ruleDO = modelMapper.map(conditionRule, MetadataValidationRuleDefinitionDO.class);
                    ruleDO.setGroupId(groupId);
                    ruleDO.setParentRuleId(andGroupRuleId);
                    if (ruleDO.getStatus() == null) {
                        ruleDO.setStatus(StatusEnumUtil.ACTIVE);
                    }
                    validationRuleDefinitionService.saveRuleDefinition(ruleDO);
                }
            }
        }
    }

    /**
     * 构建规则定义的二维数组结构
     *
     * @param groupId 规则组ID
     * @return 二维数组结构的规则定义列表，外层数组元素间为OR关系，内层数组元素间为AND关系
     */
    public List<List<ValidationRuleDefinitionVO>> buildValueRulesStructure(Long groupId) {
        // 获取该规则组下的所有规则定义
        List<MetadataValidationRuleDefinitionDO> allRules = validationRuleDefinitionService.getByGroupId(groupId);
        if (CollectionUtils.isEmpty(allRules)) {
            return new ArrayList<>();
        }

        // 转换为VO并建立映射关系
        List<ValidationRuleDefinitionVO> allRuleVOs = allRules.stream()
                .map(rule -> modelMapper.map(rule, ValidationRuleDefinitionVO.class))
                .toList();
        Map<Long, ValidationRuleDefinitionVO> ruleMap = new HashMap<>();
        for (ValidationRuleDefinitionVO ruleVO : allRuleVOs) {
            ruleMap.put(ruleVO.getId(), ruleVO);
        }

        // 找到主OR节点（顶级LOGIC节点且logicOperator为OR）
        ValidationRuleDefinitionVO mainOrRule = null;
        for (ValidationRuleDefinitionVO ruleVO : allRuleVOs) {
            if (ruleVO.getParentRuleId() == null && "LOGIC".equals(ruleVO.getLogicType()) && "OR".equals(ruleVO.getLogicOperator())) {
                mainOrRule = ruleVO;
                break;
            }
        }

        if (mainOrRule == null) {
            return new ArrayList<>();
        }

        // 构建二维数组结构
        List<List<ValidationRuleDefinitionVO>> valueRules = new ArrayList<>();

        // 获取主OR节点的所有子规则
        for (ValidationRuleDefinitionVO ruleVO : allRuleVOs) {
            if (mainOrRule.getId().equals(ruleVO.getParentRuleId())) {
                if ("CONDITION".equals(ruleVO.getLogicType())) {
                    // 直接是条件规则，单独成组
                    List<ValidationRuleDefinitionVO> singleGroup = new ArrayList<>();
                    singleGroup.add(ruleVO);
                    valueRules.add(singleGroup);
                } else if ("LOGIC".equals(ruleVO.getLogicType()) && "AND".equals(ruleVO.getLogicOperator())) {
                    // AND分组，获取其所有子条件
                    List<ValidationRuleDefinitionVO> andGroup = new ArrayList<>();
                    for (ValidationRuleDefinitionVO conditionVO : allRuleVOs) {
                        if (ruleVO.getId().equals(conditionVO.getParentRuleId()) && "CONDITION".equals(conditionVO.getLogicType())) {
                            andGroup.add(conditionVO);
                        }
                    }
                    if (!andGroup.isEmpty()) {
                        valueRules.add(andGroup);
                    }
                }
            }
        }

        return valueRules;
    }

    /**
     * 为精简VO构建派生字段
     * validationType : 取第一条条件规则的 fieldCode
     * validationItems : 每个OR分组转可读表达式(内部AND用 AND 连接)
     * errorMessage : 复用 popPrompt
     */
    private void buildDerivedFieldsForSimpleVO(ValidationRuleGroupSimpleRespVO vo, Long groupId, String popPrompt) {
        List<List<ValidationRuleDefinitionVO>> valueRules = buildValueRulesStructure(groupId);
        vo.setErrorMessage(popPrompt);
        if (valueRules == null || valueRules.isEmpty()) {
            vo.setValidationItems(new ArrayList<>());
            return;
        }
        // validationType
        outer: for (List<ValidationRuleDefinitionVO> andGroup : valueRules) {
            if (andGroup == null) continue;
            for (ValidationRuleDefinitionVO rule : andGroup) {
                if ("CONDITION".equalsIgnoreCase(rule.getLogicType())) {
                    vo.setValidationType(rule.getFieldCode());
                    break outer;
                }
            }
        }
        // items
        List<String> items = new ArrayList<>();
        for (List<ValidationRuleDefinitionVO> andGroup : valueRules) {
            if (andGroup == null || andGroup.isEmpty()) continue;
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (ValidationRuleDefinitionVO rule : andGroup) {
                if (!"CONDITION".equalsIgnoreCase(rule.getLogicType())) continue;
                String left = rule.getFieldCode() != null ? rule.getFieldCode() : "";
                String op = rule.getOperator() != null ? rule.getOperator() : "";
                String v1 = rule.getFieldValue() != null ? String.valueOf(rule.getFieldValue()) : "";
                String v2 = rule.getFieldValue2() != null ? String.valueOf(rule.getFieldValue2()) : null;
                String expr;
                if ("BETWEEN".equalsIgnoreCase(op) && v2 != null) {
                    expr = left + " BETWEEN " + v1 + " AND " + v2;
                } else if ("IN".equalsIgnoreCase(op) || "NOT IN".equalsIgnoreCase(op)) {
                    expr = left + " " + op + " (" + v1 + ")"; // 这里假设v1包含逗号分隔值
                } else {
                    expr = left + " " + op + " " + v1;
                }
                if (!first) {
                    sb.append(" AND ");
                }
                sb.append(expr.trim());
                first = false;
            }
            if (sb.length() > 0) {
                items.add(sb.toString());
            }
        }
        vo.setValidationItems(items);
    }



}
