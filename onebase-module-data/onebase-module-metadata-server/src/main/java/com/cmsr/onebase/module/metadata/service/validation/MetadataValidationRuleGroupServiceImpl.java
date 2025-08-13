package com.cmsr.onebase.module.metadata.service.validation;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationRuleDefinitionVO;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationRuleGroupPageReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationRuleGroupSaveReqVO;
import com.cmsr.onebase.module.metadata.convert.validation.ValidationRuleGroupConvert;
import com.cmsr.onebase.module.metadata.dal.database.MetadataValidationRuleDefinitionRepository;
import com.cmsr.onebase.module.metadata.dal.database.MetadataValidationRuleGroupRepository;
import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationRuleDefinitionDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationRuleGroupDO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.enums.ErrorCodeConstants.VALIDATION_RULE_GROUP_NOT_EXISTS;
import static com.cmsr.onebase.module.metadata.enums.ErrorCodeConstants.VALIDATION_RULE_GROUP_NAME_DUPLICATE;

/**
 * 校验规则分组 Service 实现类
 *
 * @author bty418
 * @date 2025-01-25
 */
@Service
@Slf4j
public class MetadataValidationRuleGroupServiceImpl implements MetadataValidationRuleGroupService {

    @Resource
    private MetadataValidationRuleGroupRepository validationRuleGroupRepository;

    @Resource
    private MetadataValidationRuleDefinitionRepository validationRuleDefinitionRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createValidationRuleGroup(@Valid ValidationRuleGroupSaveReqVO createReqVO) {
        // 校验规则组名称唯一性
        validateRuleGroupNameUnique(null, createReqVO.getRgName());

        // 插入校验规则分组
        MetadataValidationRuleGroupDO ruleGroup = BeanUtils.toBean(createReqVO, MetadataValidationRuleGroupDO.class);
        // 设置默认状态
        if (!StringUtils.hasText(ruleGroup.getRgStatus())) {
            ruleGroup.setRgStatus("ACTIVE");
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
        validationRuleGroupRepository.upsert(updateObj);

        // 处理规则定义：先删除旧的，再插入新的
        Long groupId = updateReqVO.getId();
        validationRuleDefinitionRepository.deleteByGroupId(groupId);
        
        if (!CollectionUtils.isEmpty(updateReqVO.getValueRules())) {
            saveValueRules(groupId, updateReqVO.getValueRules());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteValidationRuleGroup(Long id) {
        // 校验存在
        validateValidationRuleGroupExists(id);
        
        // 先删除关联的规则定义
        validationRuleDefinitionRepository.deleteByGroupId(id);
        
        // 删除校验规则分组
        validationRuleGroupRepository.deleteById(id);
    }

    @Override
    public MetadataValidationRuleGroupDO getValidationRuleGroup(Long id) {
        return validationRuleGroupRepository.findById(id);
    }

    @Override
    public PageResult<MetadataValidationRuleGroupDO> getValidationRuleGroupPage(ValidationRuleGroupPageReqVO pageReqVO) {
        return validationRuleGroupRepository.selectPage(pageReqVO);
    }

    /**
     * 校验校验规则分组是否存在
     *
     * @param id 校验规则分组编号
     */
    private void validateValidationRuleGroupExists(Long id) {
        if (validationRuleGroupRepository.findById(id) == null) {
            throw exception(VALIDATION_RULE_GROUP_NOT_EXISTS);
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
            throw exception(VALIDATION_RULE_GROUP_NAME_DUPLICATE);
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
        mainOrRule.setStatus("ACTIVE");
        validationRuleDefinitionRepository.upsert(mainOrRule);
        Long mainOrRuleId = mainOrRule.getId();

        // 处理每个OR分组（内层数组）
        for (List<ValidationRuleDefinitionVO> andGroup : valueRules) {
            if (CollectionUtils.isEmpty(andGroup)) {
                continue;
            }

            if (andGroup.size() == 1) {
                // 只有一个条件，直接添加到主OR节点下
                ValidationRuleDefinitionVO singleRule = andGroup.get(0);
                MetadataValidationRuleDefinitionDO ruleDO = ValidationRuleGroupConvert.INSTANCE.convertToRuleDefinitionDO(singleRule);
                ruleDO.setGroupId(groupId);
                ruleDO.setParentRuleId(mainOrRuleId);
                if (!StringUtils.hasText(ruleDO.getStatus())) {
                    ruleDO.setStatus("ACTIVE");
                }
                validationRuleDefinitionRepository.upsert(ruleDO);
            } else {
                // 多个条件，创建AND分组节点
                MetadataValidationRuleDefinitionDO andGroupRule = new MetadataValidationRuleDefinitionDO();
                andGroupRule.setGroupId(groupId);
                andGroupRule.setParentRuleId(mainOrRuleId);
                andGroupRule.setLogicType("LOGIC");
                andGroupRule.setLogicOperator("AND");
                andGroupRule.setStatus("ACTIVE");
                validationRuleDefinitionRepository.upsert(andGroupRule);
                Long andGroupRuleId = andGroupRule.getId();

                // 添加AND分组内的所有条件
                for (ValidationRuleDefinitionVO conditionRule : andGroup) {
                    MetadataValidationRuleDefinitionDO ruleDO = ValidationRuleGroupConvert.INSTANCE.convertToRuleDefinitionDO(conditionRule);
                    ruleDO.setGroupId(groupId);
                    ruleDO.setParentRuleId(andGroupRuleId);
                    if (!StringUtils.hasText(ruleDO.getStatus())) {
                        ruleDO.setStatus("ACTIVE");
                    }
                    validationRuleDefinitionRepository.upsert(ruleDO);
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
        List<MetadataValidationRuleDefinitionDO> allRules = validationRuleDefinitionRepository.selectByGroupId(groupId);
        if (CollectionUtils.isEmpty(allRules)) {
            return new ArrayList<>();
        }

        // 转换为VO并建立映射关系
        List<ValidationRuleDefinitionVO> allRuleVOs = ValidationRuleGroupConvert.INSTANCE.convertRuleDefinitionList(allRules);
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
     * 构建规则定义的树形结构
     *
     * @param groupId 规则组ID
     * @return 树形结构的规则定义列表
     */
    public List<ValidationRuleDefinitionVO> buildRuleDefinitionTree(Long groupId) {
        // 获取该规则组下的所有规则定义
        List<MetadataValidationRuleDefinitionDO> allRules = validationRuleDefinitionRepository.selectByGroupId(groupId);
        if (CollectionUtils.isEmpty(allRules)) {
            return new ArrayList<>();
        }

        // 转换为VO
        List<ValidationRuleDefinitionVO> allRuleVOs = ValidationRuleGroupConvert.INSTANCE.convertRuleDefinitionList(allRules);

        // 构建ID到VO的映射
        Map<Long, ValidationRuleDefinitionVO> ruleMap = new HashMap<>();
        for (ValidationRuleDefinitionVO ruleVO : allRuleVOs) {
            ruleVO.setChildren(new ArrayList<>());
            ruleMap.put(ruleVO.getId(), ruleVO);
        }

        // 构建树形结构
        List<ValidationRuleDefinitionVO> rootRules = new ArrayList<>();
        for (ValidationRuleDefinitionVO ruleVO : allRuleVOs) {
            if (ruleVO.getParentRuleId() == null) {
                // 顶级规则
                rootRules.add(ruleVO);
            } else {
                // 子规则，添加到父规则的children中
                ValidationRuleDefinitionVO parentRule = ruleMap.get(ruleVO.getParentRuleId());
                if (parentRule != null) {
                    parentRule.getChildren().add(ruleVO);
                }
            }
        }

        return rootRules;
    }

}
