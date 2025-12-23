package com.cmsr.onebase.module.metadata.build.service.validation;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRuleDefinitionVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRuleGroupPageReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRuleGroupSaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRuleGroupSimpleRespVO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationRuleGroupRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationRequiredRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationLengthRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationUniqueRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationRangeRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationFormatRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationChildNotEmptyRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityFieldRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRuleDefinitionDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRuleGroupDO;
import com.cmsr.onebase.module.metadata.core.util.MetadataIdUuidConverter;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.cmsr.onebase.framework.common.util.string.UuidUtils;

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

    // 注入各种校验类型的Service
    @Resource
    private MetadataValidationLengthBuildService lengthService;

    @Resource
    private MetadataValidationRangeBuildService rangeService;

    @Resource
    private MetadataValidationRequiredBuildService requiredService;

    @Resource
    private MetadataValidationUniqueBuildService uniqueService;

    @Resource
    private MetadataValidationFormatBuildService formatService;

    @Resource
    private MetadataValidationChildNotEmptyBuildService childNotEmptyService;

    @Resource
    private MetadataIdUuidConverter idUuidConverter;

    // 注入各种校验Repository用于查询
    @Resource
    private MetadataValidationRequiredRepository requiredRepository;

    @Resource
    private MetadataValidationLengthRepository lengthRepository;

    @Resource
    private MetadataValidationUniqueRepository uniqueRepository;

    @Resource
    private MetadataValidationRangeRepository rangeRepository;

    @Resource
    private MetadataValidationFormatRepository formatRepository;

    @Resource
    private MetadataValidationChildNotEmptyRepository childNotEmptyRepository;

    @Resource
    private MetadataEntityFieldRepository entityFieldRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createValidationRuleGroup(@Valid ValidationRuleGroupSaveReqVO createReqVO) {
        // 移除规则组名称唯一性校验，允许不同字段使用相同的规则组名称
        // 实际的唯一性由"每个实体的每个字段的每种校验类型只有一条生效的数据"来保证
        // validateRuleGroupNameUnique(null, createReqVO.getRgName());

        // 插入校验规则分组
        MetadataValidationRuleGroupDO ruleGroup = BeanUtils.toBean(createReqVO, MetadataValidationRuleGroupDO.class);
        // 设置默认状态
        if (ruleGroup.getRgStatus() == null) {
            ruleGroup.setRgStatus(StatusEnumUtil.ACTIVE);
        }
        // 自动生成 groupUuid（如果为空）
        if (ruleGroup.getGroupUuid() == null || ruleGroup.getGroupUuid().isEmpty()) {
            ruleGroup.setGroupUuid(UuidUtils.getUuid());
        }

        validationRuleGroupRepository.saveOrUpdate(ruleGroup);
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

        // 移除规则组名称唯一性校验，允许不同字段使用相同的规则组名称
        // validateRuleGroupNameUnique(updateReqVO.getId(), updateReqVO.getRgName());

        // 更新校验规则分组
        MetadataValidationRuleGroupDO updateObj = BeanUtils.toBean(updateReqVO, MetadataValidationRuleGroupDO.class);
        validationRuleGroupRepository.updateById(updateObj); // 使用updateById而不是upsert，避免主键冲突

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
        validationRuleGroupRepository.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void safeDeleteGroupDirect(Long groupId) {
        if (groupId == null) { return; }
        MetadataValidationRuleGroupDO group = validationRuleGroupRepository.getById(groupId);
        if (group == null) { return; }
        validationRuleDefinitionService.deleteByGroupId(groupId);
        validationRuleGroupRepository.removeById(groupId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void safeDeleteGroupDirect(String groupUuid) {
        if (groupUuid == null || groupUuid.isBlank()) { return; }
        MetadataValidationRuleGroupDO group = validationRuleGroupRepository.getByGroupUuid(groupUuid);
        if (group == null) { return; }
        validationRuleDefinitionService.deleteByGroupId(group.getId());
        validationRuleGroupRepository.removeById(group.getId());
    }

    @Override
    public MetadataValidationRuleGroupDO getValidationRuleGroup(Long id) {
        return validationRuleGroupRepository.getById(id);
    }

    @Override
    public MetadataValidationRuleGroupDO getValidationRuleGroupByUuid(String groupUuid) {
        return validationRuleGroupRepository.getByGroupUuid(groupUuid);
    }

    @Override
    public PageResult<MetadataValidationRuleGroupDO> getValidationRuleGroupPage(ValidationRuleGroupPageReqVO pageReqVO) {
        // 优先使用entityUuid，若为空则使用entityId（转换为UUID）
        String entityUuid = pageReqVO.getEntityUuid();
        if (entityUuid == null && pageReqVO.getEntityId() != null) {
            entityUuid = idUuidConverter.toEntityUuid(pageReqVO.getEntityId());
        }
        return validationRuleGroupRepository.selectPage(
                pageReqVO.getPageNo(),
                pageReqVO.getPageSize(),
                pageReqVO.getRgName(),
                entityUuid
        );
    }

    @Override
    public PageResult<ValidationRuleGroupSimpleRespVO> getValidationRuleGroupPageSimple(ValidationRuleGroupPageReqVO pageReqVO) {
        // 优先使用entityUuid，若为空则使用entityId（转换为UUID）
        String entityUuid = pageReqVO.getEntityUuid();
        if (entityUuid == null && pageReqVO.getEntityId() != null) {
            entityUuid = idUuidConverter.toEntityUuid(pageReqVO.getEntityId());
        }
        PageResult<MetadataValidationRuleGroupDO> page = validationRuleGroupRepository.selectPage(
                pageReqVO.getPageNo(),
                pageReqVO.getPageSize(),
                pageReqVO.getRgName(),
                entityUuid
        );
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
        // 调用重载方法，使用默认参数
        return ensureFieldRuleGroup(fieldId, null, null);
    }

    /**
     * 确保存在指定字段专属规则组，支持设置校验类型和提示信息
     *
     * @param fieldId 字段ID
     * @param validationType 校验类型，如果为null则不设置
     * @param popPrompt 提示信息，如果为null则不设置
     * @return 规则组ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long ensureFieldRuleGroup(Long fieldId, String validationType, String popPrompt) {
        String rgName = "RG_FIELD_" + fieldId;
        MetadataValidationRuleGroupDO group = validationRuleGroupRepository.selectByRgName(rgName, null);
        if (group == null) {
            group = new MetadataValidationRuleGroupDO();
            group.setRgName(rgName);
            group.setRgDesc("字段" + fieldId + "的规则组");
            group.setRgStatus(StatusEnumUtil.ACTIVE);
            // 自动生成 groupUuid
            group.setGroupUuid(UuidUtils.getUuid());
            // 同步entityUuid
            var fieldDO = entityFieldRepository.getById(fieldId);
            if (fieldDO != null) {
                group.setEntityUuid(fieldDO.getEntityUuid());
            }
            // 设置校验类型和提示信息（如果提供）
            if (validationType != null) {
                group.setValidationType(validationType);
            }
            if (popPrompt != null) {
                group.setPopPrompt(popPrompt);
            }
            validationRuleGroupRepository.save(group);
        } else {
            // 如果组已存在但缺少validation_type或pop_prompt，则更新
            boolean needUpdate = false;
            if (validationType != null && group.getValidationType() == null) {
                group.setValidationType(validationType);
                needUpdate = true;
            }
            if (popPrompt != null && group.getPopPrompt() == null) {
                group.setPopPrompt(popPrompt);
                needUpdate = true;
            }
            if (needUpdate) {
                validationRuleGroupRepository.updateById(group);
            }
        }
        return group.getId();
    }

    /**
     * 校验校验规则分组是否存在
     *
     * @param id 校验规则分组编号
     */
    private void validateValidationRuleGroupExists(Long id) {
        if (validationRuleGroupRepository.getById(id) == null) {
            throw exception(ErrorCodeConstants.VALIDATION_RULE_GROUP_NOT_EXISTS);
        }
    }

    /**
     * 校验规则组名称唯一性
     *
     * @param id 校验规则分组编号（用于修改时排除自身）
     * @param rgName 规则组名称
     * @deprecated 不再需要校验规则组名称唯一性，允许不同字段使用相同的规则组名称。
     *             实际的唯一性由"每个实体的每个字段的每种校验类型只有一条生效的数据"来保证。
     */
    @Deprecated
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
        mainOrRule.setGroupUuid(String.valueOf(groupId));
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
                ruleDO.setGroupUuid(String.valueOf(groupId));
                ruleDO.setParentRuleUuid(String.valueOf(mainOrRuleId));
                ruleDO.setLogicType("CONDITION");
                if (ruleDO.getStatus() == null) {
                    ruleDO.setStatus(StatusEnumUtil.ACTIVE);
                }
                validationRuleDefinitionService.saveRuleDefinition(ruleDO);
            } else {
                // 多个条件，创建AND分组节点
                MetadataValidationRuleDefinitionDO andGroupRule = new MetadataValidationRuleDefinitionDO();
                andGroupRule.setGroupUuid(String.valueOf(groupId));
                andGroupRule.setParentRuleUuid(String.valueOf(mainOrRuleId));
                andGroupRule.setLogicType("LOGIC");
                andGroupRule.setLogicOperator("AND");
                andGroupRule.setStatus(StatusEnumUtil.ACTIVE);
                validationRuleDefinitionService.saveRuleDefinition(andGroupRule);
                Long andGroupRuleId = andGroupRule.getId();

                // 添加AND分组内的所有条件
                for (ValidationRuleDefinitionVO conditionRule : andGroup) {
                    MetadataValidationRuleDefinitionDO ruleDO = modelMapper.map(conditionRule, MetadataValidationRuleDefinitionDO.class);
                    ruleDO.setGroupUuid(String.valueOf(groupId));
                    ruleDO.setParentRuleUuid(String.valueOf(andGroupRuleId));
                    ruleDO.setLogicType("CONDITION");
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
        log.info("构建规则结构，groupId: {}", groupId);

        // 获取该规则组下的所有规则定义
        List<MetadataValidationRuleDefinitionDO> allRules = validationRuleDefinitionService.getByGroupId(groupId);
        log.info("从数据库获取到规则定义数量: {}", allRules != null ? allRules.size() : 0);

        if (allRules == null || allRules.isEmpty()) {
            log.info("规则组 {} 下没有规则定义，返回空列表", groupId);
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
     * validationType : 从规则组的validation_type字段获取
     * validationItems : 根据校验类型查询关联的字段名称
     * errorMessage : 复用 popPrompt
     */
    private void buildDerivedFieldsForSimpleVO(ValidationRuleGroupSimpleRespVO vo, Long groupId, String popPrompt) {
        log.info("开始构建派生字段，groupId: {}, popPrompt: {}", groupId, popPrompt);

        // 设置错误消息，直接使用popPrompt，可能为null
        vo.setErrorMessage(popPrompt);

        // 获取规则组详细信息
        MetadataValidationRuleGroupDO ruleGroup = validationRuleGroupRepository.getById(groupId);
        if (ruleGroup == null) {
            log.info("规则组 {} 不存在，返回空值", groupId);
            vo.setValidationType(null);
            vo.setValidationItems(new ArrayList<>());
            return;
        }

        String validationType = ruleGroup.getValidationType();
        vo.setValidationType(validationType);
        log.info("设置validationType: {}", validationType);

        // 根据校验类型获取关联的字段名称
        List<String> fieldNames = getFieldNamesByValidationType(groupId, validationType);
        vo.setValidationItems(fieldNames);
        log.info("设置validationItems: {}", fieldNames);
    }

    /**
     * 根据校验类型和规则组ID获取关联的字段名称
     */
    private List<String> getFieldNamesByValidationType(Long groupId, String validationType) {
        List<String> fieldNames = new ArrayList<>();

        if (validationType == null) {
            return fieldNames;
        }

        log.info("查询字段名称：groupId={}, validationType={}", groupId, validationType);

        // 先获取规则组的 groupUuid，用于查询校验记录
        MetadataValidationRuleGroupDO group = validationRuleGroupRepository.getById(groupId);
        String groupUuid = null;
        if (group != null) {
            groupUuid = group.getGroupUuid();
        }
        // 如果 groupUuid 为 null，则使用 groupId 的字符串形式作为备选（兼容历史数据）
        String queryGroupUuid = (groupUuid != null && !groupUuid.isEmpty()) ? groupUuid : String.valueOf(groupId);

        try {
            switch (validationType) {
                case "REQUIRED":
                    // 查询必填校验关联的字段
                    var requiredFields = requiredRepository.findByGroupUuid(queryGroupUuid);
                    log.info("查询到必填校验字段数量：{}", requiredFields != null ? requiredFields.size() : 0);
                    if (requiredFields != null) {
                        for (var field : requiredFields) {
                            log.info("处理字段UUID：{}", field.getFieldUuid());
                            String fieldName = getFieldNameByUuid(field.getFieldUuid());
                            log.info("获取到字段名称：{}", fieldName);
                            if (fieldName != null) {
                                fieldNames.add(fieldName);
                            }
                        }
                    }
                    break;

                case "LENGTH":
                    // 查询长度校验关联的字段
                    var lengthFields = lengthRepository.findByGroupUuid(queryGroupUuid);
                    log.info("查询到长度校验字段数量：{}", lengthFields != null ? lengthFields.size() : 0);
                    if (lengthFields != null) {
                        for (var field : lengthFields) {
                            log.info("处理长度校验字段UUID：{}", field.getFieldUuid());
                            String fieldName = getFieldNameByUuid(field.getFieldUuid());
                            log.info("获取到长度校验字段名称：{}", fieldName);
                            if (fieldName != null) {
                                fieldNames.add(fieldName);
                            }
                        }
                    }
                    break;

                case "UNIQUE":
                    // 查询唯一性校验关联的字段
                    var uniqueFields = uniqueRepository.findByGroupUuid(queryGroupUuid);
                    log.info("查询到唯一性校验字段数量：{}", uniqueFields != null ? uniqueFields.size() : 0);
                    if (uniqueFields != null) {
                        for (var field : uniqueFields) {
                            log.info("处理唯一性校验字段UUID：{}", field.getFieldUuid());
                            String fieldName = getFieldNameByUuid(field.getFieldUuid());
                            log.info("获取到唯一性校验字段名称：{}", fieldName);
                            if (fieldName != null) {
                                fieldNames.add(fieldName);
                            }
                        }
                    }
                    break;

                case "RANGE":
                    // 查询范围校验关联的字段
                    var rangeFields = rangeRepository.findByGroupUuid(queryGroupUuid);
                    log.info("查询到范围校验字段数量：{}", rangeFields != null ? rangeFields.size() : 0);
                    if (rangeFields != null) {
                        for (var field : rangeFields) {
                            log.info("处理范围校验字段UUID：{}", field.getFieldUuid());
                            String fieldName = getFieldNameByUuid(field.getFieldUuid());
                            log.info("获取到范围校验字段名称：{}", fieldName);
                            if (fieldName != null) {
                                fieldNames.add(fieldName);
                            }
                        }
                    }
                    break;

                case "FORMAT":
                    // 查询格式校验关联的字段
                    var formatFields = formatRepository.findByGroupUuid(queryGroupUuid);
                    log.info("查询到格式校验字段数量：{}", formatFields != null ? formatFields.size() : 0);
                    if (formatFields != null) {
                        for (var field : formatFields) {
                            log.info("处理格式校验字段UUID：{}", field.getFieldUuid());
                            String fieldName = getFieldNameByUuid(field.getFieldUuid());
                            log.info("获取到格式校验字段名称：{}", fieldName);
                            if (fieldName != null) {
                                fieldNames.add(fieldName);
                            }
                        }
                    }
                    break;

                case "CHILD_NOT_EMPTY":
                    // 查询子表非空校验关联的字段
                    var childNotEmptyFields = childNotEmptyRepository.findByGroupUuid(queryGroupUuid);
                    log.info("查询到子表非空校验字段数量：{}", childNotEmptyFields != null ? childNotEmptyFields.size() : 0);
                    if (childNotEmptyFields != null) {
                        for (var field : childNotEmptyFields) {
                            log.info("处理子表非空校验字段UUID：{}", field.getFieldUuid());
                            String fieldName = getFieldNameByUuid(field.getFieldUuid());
                            log.info("获取到子表非空校验字段名称：{}", fieldName);
                            if (fieldName != null) {
                                fieldNames.add(fieldName);
                            }
                        }
                    }
                    break;

                default:
                    log.warn("未知的校验类型: {}", validationType);
            }
        } catch (Exception e) {
            log.error("获取字段名称时发生错误，groupId: {}, validationType: {}", groupId, validationType, e);
        }

        log.info("最终返回字段名称列表：{}", fieldNames);
        return fieldNames;
    }

    /**
     * 根据字段ID获取字段显示名称
     * @deprecated 请使用 getFieldNameByUuid(String)
     */
    @Deprecated
    private String getFieldNameById(Long fieldId) {
        if (fieldId == null) {
            return null;
        }
        return getFieldNameByUuid(String.valueOf(fieldId));
    }

    /**
     * 根据字段UUID获取字段显示名称
     */
    private String getFieldNameByUuid(String fieldUuid) {
        if (fieldUuid == null) {
            return null;
        }

        try {
            // 使用 entityFieldRepository 查询字段信息，返回displayName而不是fieldName
            var fieldDO = entityFieldRepository.getByUuid(fieldUuid);
            return fieldDO != null ? fieldDO.getDisplayName() : null;
        } catch (Exception e) {
            log.error("根据字段UUID获取字段显示名称失败，fieldUuid: {}", fieldUuid, e);
            return null;
        }
    }



}
