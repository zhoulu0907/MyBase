package com.cmsr.onebase.module.metadata.build.service.validation;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationLengthRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationLengthSaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationLengthUpdateReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRuleGroupSaveReqVO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationLengthRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityFieldRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationRuleGroupRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationLengthDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRuleGroupDO;
import com.cmsr.onebase.module.metadata.build.service.entity.MetadataEntityFieldBuildService;
import com.cmsr.onebase.module.metadata.core.util.MetadataIdUuidConverter;
import com.cmsr.onebase.module.metadata.core.util.StatusEnumUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 长度校验 Service 实现
 *
 * @author bty418
 * @date 2025-08-27
 */
@Service
@Slf4j
public class MetadataValidationLengthBuildServiceImpl implements MetadataValidationLengthBuildService {

    @Resource private MetadataValidationLengthRepository lengthRepository; // 自身仓库
    @Resource private MetadataValidationRuleGroupBuildService ruleGroupService; // 其他服务
    @Resource private MetadataValidationRuleGroupRepository ruleGroupRepository; // 规则组仓库
    @Resource private MetadataEntityFieldBuildService entityFieldService; // 其他服务
    @Resource private MetadataEntityFieldRepository entityFieldRepository; // 字段仓库，用于同步数据
    @Resource private MetadataIdUuidConverter idUuidConverter; // ID转UUID工具

    @Override
    public MetadataValidationLengthDO getByFieldId(String fieldUuid) {
        return lengthRepository.findOneByFieldUuid(fieldUuid);
    }

    @Override
    public ValidationLengthRespVO getByFieldIdWithRgName(String fieldUuid) {
        MetadataValidationLengthDO lengthDO = lengthRepository.findOneByFieldUuid(fieldUuid);
        if (lengthDO == null) {
            return null;
        }

        // 转换DO为VO
        ValidationLengthRespVO respVO = BeanUtils.toBean(lengthDO, ValidationLengthRespVO.class);

        // 获取规则组信息，包括提示语等字段
        if (lengthDO.getGroupUuid() != null) {
            MetadataValidationRuleGroupDO ruleGroup = ruleGroupRepository.getOne(
                ruleGroupRepository.query().eq(MetadataValidationRuleGroupDO::getGroupUuid, lengthDO.getGroupUuid())
            );
            if (ruleGroup != null) {
                respVO.setRgName(ruleGroup.getRgName());
                respVO.setPromptMessage(ruleGroup.getPopPrompt());
            }
        }

        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ValidationLengthSaveReqVO vo) {
        Assert.notNull(vo, "vo不能为空");
        // ID与UUID兼容处理：优先使用UUID，若为空则通过ID转换
        vo.setFieldUuid(idUuidConverter.resolveFieldUuid(vo.getFieldUuid(), vo.getFieldId()));
        Assert.hasText(vo.getRgName(), "规则组名称不能为空");

        // 获取字段信息
        MetadataEntityFieldDO field = entityFieldService.getEntityFieldByUuid(vo.getFieldUuid());
        Assert.notNull(field, "字段不存在");

        // 检查同一字段是否已存在长度校验规则
        MetadataValidationLengthDO existingRule = lengthRepository.findOneByFieldUuid(vo.getFieldUuid());
        if (existingRule != null) {
            throw new IllegalStateException("该字段已存在长度校验规则，同一字段只能有一条长度校验规则");
        }

        // 处理规则组：先查找，不存在则创建，且禁止不同字段复用同一group_id
        Long groupId;
        var existingGroup = ruleGroupService.getByName(vo.getRgName());
        boolean canReuse = false;
        if (existingGroup != null) {
            // 检查该group_id下是否已存在其他字段的长度校验
            List<MetadataValidationLengthDO> groupLengthList = lengthRepository.findByGroupUuid(existingGroup.getGroupUuid());
            if (groupLengthList.isEmpty() || (groupLengthList.size() == 1 && groupLengthList.get(0).getFieldUuid().equals(vo.getFieldUuid()))) {
                canReuse = true;
            }
        }
        if (existingGroup != null && canReuse) {
            groupId = existingGroup.getId();
        } else {
            // 创建新的规则组
            ValidationRuleGroupSaveReqVO groupVO = new ValidationRuleGroupSaveReqVO();
            groupVO.setRgName(vo.getRgName());
            groupVO.setRgDesc("自动创建的规则组：" + vo.getRgName());
            groupVO.setRgStatus(StatusEnumUtil.ACTIVE);
            // 透传可选的组级提示配置
            groupVO.setValMethod(vo.getValMethod());
            groupVO.setPopPrompt(vo.getPopPrompt());
            groupVO.setPopType(vo.getPopType());
            groupVO.setValidationType("LENGTH");
            // 修复：同步entityUuid到规则组
            groupVO.setEntityUuid(field.getEntityUuid());
            groupId = ruleGroupService.createValidationRuleGroup(groupVO);
        }

        // 转换VO为DO并设置必要字段
        MetadataValidationLengthDO data = BeanUtils.toBean(vo, MetadataValidationLengthDO.class);
        data.setEntityUuid(field.getEntityUuid());
        data.setApplicationId(field.getApplicationId()); // 使用字段的applicationId
        MetadataValidationRuleGroupDO group = ruleGroupService.getValidationRuleGroup(groupId);
        // 确保 groupUuid 不为空，如果为空则生成并更新规则组
        String groupUuid = group.getGroupUuid();
        if (groupUuid == null || groupUuid.isEmpty()) {
            groupUuid = com.cmsr.onebase.framework.common.util.string.UuidUtils.getUuid();
            group.setGroupUuid(groupUuid);
            ruleGroupRepository.updateById(group);
        }
        data.setGroupUuid(groupUuid);
        data.setPromptMessage(vo.getPopPrompt());
        // 保存长度校验规则
        lengthRepository.saveOrUpdate(data);
        
        // 同步到MetadataEntityFieldDO：如果设置了maxLength，则同步到dataLength字段
        syncToEntityField(vo.getFieldUuid(), data.getMaxLength());
        
        return data.getId();
    }

        @Override
        @Transactional(rollbackFor = Exception.class)
        public void update(ValidationLengthUpdateReqVO reqVO) {
            // 约定：前端传入的 reqVO.id 为 规则组 groupId
            Long groupIdAsParam = reqVO.getId();
            Assert.notNull(groupIdAsParam, "规则组ID不能为空");
            MetadataValidationRuleGroupDO groupDO = ruleGroupService.getValidationRuleGroup(groupIdAsParam);
            Assert.notNull(groupDO, "规则组不存在");
            String groupUuidAsParam = groupDO.getGroupUuid();

            // 根据 groupUuid 查询对应长度校验记录
            List<MetadataValidationLengthDO> list = lengthRepository.findByGroupUuid(groupUuidAsParam);
            Assert.notEmpty(list, "当前长度校验规则不存在(组UUID=" + groupUuidAsParam + ")");
            if (list.size() > 1) {
                throw new IllegalStateException("数据异常：同一组存在多条长度校验规则(组UUID=" + groupUuidAsParam + ")");
            }
            MetadataValidationLengthDO existingDO = list.get(0);

            // 查询字段信息
            MetadataEntityFieldDO entityFieldDO = entityFieldService.getEntityFieldByUuid(existingDO.getFieldUuid());
            Assert.notNull(entityFieldDO, "字段不存在");

            // 处理规则组：根据新的 rgName 若与当前不同，且不可复用，则新建
            String targetGroupUuid;
            var existingGroup = ruleGroupService.getByName(reqVO.getRgName());
            boolean canReuse = false;
            if (existingGroup != null) {
                List<MetadataValidationLengthDO> groupLengthList = lengthRepository.findByGroupUuid(existingGroup.getGroupUuid());
                boolean reusedByOtherField = groupLengthList.stream().anyMatch(r -> !r.getFieldUuid().equals(existingDO.getFieldUuid()));
                if (!reusedByOtherField) {
                    canReuse = true;
                }
            }
            if (existingGroup != null && canReuse) {
                targetGroupUuid = existingGroup.getGroupUuid();
                // 确保 groupUuid 不为空，如果为空则生成并更新规则组
                if (targetGroupUuid == null || targetGroupUuid.isEmpty()) {
                    targetGroupUuid = com.cmsr.onebase.framework.common.util.string.UuidUtils.getUuid();
                    existingGroup.setGroupUuid(targetGroupUuid);
                    ruleGroupRepository.updateById(existingGroup);
                }
                // 如果复用已有规则组，需要根据请求更新组级别配置(popPrompt/valMethod/popType)，避免列表查询仍显示旧值
                boolean needGroupUpdate = false;
                ValidationRuleGroupSaveReqVO updateGroupVO = new ValidationRuleGroupSaveReqVO();
                updateGroupVO.setId(existingGroup.getId());
                updateGroupVO.setRgName(existingGroup.getRgName()); // 名称不变
                updateGroupVO.setRgDesc(existingGroup.getRgDesc());
                updateGroupVO.setRgStatus(existingGroup.getRgStatus());
                updateGroupVO.setValidationType(existingGroup.getValidationType());
                updateGroupVO.setEntityUuid(existingGroup.getEntityUuid());
                // 判定及赋值: 仅当传入值非空且与现有不同才更新
                if (reqVO.getPopPrompt() != null && !reqVO.getPopPrompt().equals(existingGroup.getPopPrompt())) {
                    updateGroupVO.setPopPrompt(reqVO.getPopPrompt());
                    needGroupUpdate = true;
                }
                if (reqVO.getValMethod() != null && !reqVO.getValMethod().equals(existingGroup.getValMethod())) {
                    updateGroupVO.setValMethod(reqVO.getValMethod());
                    needGroupUpdate = true;
                }
                if (reqVO.getPopType() != null && !reqVO.getPopType().equals(existingGroup.getPopType())) {
                    updateGroupVO.setPopType(reqVO.getPopType());
                    needGroupUpdate = true;
                }
                if (needGroupUpdate) {
                    ruleGroupService.updateValidationRuleGroup(updateGroupVO);
                }
            } else {
                ValidationRuleGroupSaveReqVO groupVO = new ValidationRuleGroupSaveReqVO();
                groupVO.setRgName(reqVO.getRgName());
                groupVO.setRgDesc("自动创建的规则组：" + reqVO.getRgName());
                groupVO.setRgStatus(StatusEnumUtil.ACTIVE);
                groupVO.setValMethod(reqVO.getValMethod());
                groupVO.setPopPrompt(reqVO.getPopPrompt());
                groupVO.setPopType(reqVO.getPopType());
                groupVO.setValidationType("LENGTH");
                groupVO.setEntityUuid(entityFieldDO.getEntityUuid());
                Long groupId = ruleGroupService.createValidationRuleGroup(groupVO);
                MetadataValidationRuleGroupDO newGroup = ruleGroupService.getValidationRuleGroup(groupId);
                targetGroupUuid = newGroup.getGroupUuid();
            }

            // 构造更新对象
            MetadataValidationLengthDO updateDO = BeanUtils.toBean(reqVO, MetadataValidationLengthDO.class);
            updateDO.setId(existingDO.getId()); // 保留原记录主键
            updateDO.setFieldUuid(existingDO.getFieldUuid());
            updateDO.setEntityUuid(existingDO.getEntityUuid());
            updateDO.setGroupUuid(targetGroupUuid);
            updateDO.setPromptMessage(reqVO.getPopPrompt());

            // 执行更新
            lengthRepository.updateById(updateDO);

            // 同步字段 dataLength
            syncToEntityField(existingDO.getFieldUuid(), updateDO.getMaxLength());
        }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByFieldId(String fieldUuid) {
        // 先获取要删除的记录，以便后续删除关联的校验规则分组
        List<MetadataValidationLengthDO> recordsToDelete = lengthRepository.findByFieldUuid(fieldUuid);
        
        // 删除长度校验记录
        lengthRepository.deleteByFieldUuid(fieldUuid);
        
        // 删除关联的校验规则分组（如果没有其他记录引用）
        for (MetadataValidationLengthDO record : recordsToDelete) {
            if (record.getGroupUuid() != null) {
                MetadataValidationRuleGroupDO group = ruleGroupRepository.getOne(
                    ruleGroupRepository.query().eq(MetadataValidationRuleGroupDO::getGroupUuid, record.getGroupUuid())
                );
                if (group != null) {
                    deleteRuleGroupIfNotReferenced(group.getId());
                }
            }
        }
        
        // 同步到MetadataEntityFieldDO：清空dataLength字段
        syncToEntityField(fieldUuid, null);
    }

    @Override
    public ValidationLengthRespVO getById(Long id) {
        // 约定：传入 id 为 groupId
        MetadataValidationRuleGroupDO group = ruleGroupService.getValidationRuleGroup(id);
        if (group == null) {
            return null;
        }
        
        // 查询长度校验记录，优先使用 groupUuid，如果为 null 则尝试用 groupId 的字符串形式查询（兼容历史数据）
        List<MetadataValidationLengthDO> list;
        String groupUuid = group.getGroupUuid();
        if (groupUuid != null && !groupUuid.isEmpty()) {
            list = lengthRepository.findByGroupUuid(groupUuid);
        } else {
            // 兼容历史数据：尝试用 groupId 字符串形式查询
            list = lengthRepository.findByGroupUuid(String.valueOf(id));
        }
        
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() > 1) {
            throw new IllegalStateException("数据异常：同一组存在多条长度校验规则(组ID=" + id + ")");
        }
        MetadataValidationLengthDO lengthDO = list.get(0);
        ValidationLengthRespVO respVO = BeanUtils.toBean(lengthDO, ValidationLengthRespVO.class);
        
        // 获取规则组信息，包括提示语等字段
        if (group != null) {
            respVO.setRgName(group.getRgName());
            respVO.setPromptMessage(group.getPopPrompt());
        }
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        // 约定：id 为 groupId，查找对应规则
        MetadataValidationRuleGroupDO group = ruleGroupService.getValidationRuleGroup(id);
        if (group == null) {
            return;
        }
        
        // 查询长度校验记录，优先使用 groupUuid，如果为 null 则尝试用 groupId 的字符串形式查询（兼容历史数据）
        List<MetadataValidationLengthDO> list;
        String groupUuid = group.getGroupUuid();
        if (groupUuid != null && !groupUuid.isEmpty()) {
            list = lengthRepository.findByGroupUuid(groupUuid);
        } else {
            list = lengthRepository.findByGroupUuid(String.valueOf(id));
        }
        
        String fieldUuid = null;
        
        // 删除子表记录
        if (!list.isEmpty()) {
            if (list.size() > 1) {
                throw new IllegalStateException("数据异常：同一组存在多条长度校验规则(组ID=" + id + ")");
            }
            MetadataValidationLengthDO lengthDO = list.get(0);
            fieldUuid = lengthDO.getFieldUuid();
            lengthRepository.removeById(lengthDO.getId());
        }
        
        // 无论子表是否存在，都要删除主表作为兜底（防止脏数据）
        deleteRuleGroupIfNotReferenced(id);
        
        // 同步字段状态
        if (fieldUuid != null) {
            syncToEntityField(fieldUuid, null);
        }
    }
    
    /**
     * 同步长度校验到MetadataEntityFieldDO
     * 根据TODO需求：如果调用了ValidationLengthController增删改接口，也需要将信息同步到metadataEntityFieldDO中
     */
    private void syncToEntityField(String fieldUuid, Integer maxLength) {
        try {
            // 获取字段信息
            MetadataEntityFieldDO field = entityFieldService.getEntityFieldByUuid(fieldUuid);
            if (field != null) {
                // 创建更新对象，只更新dataLength字段
                MetadataEntityFieldDO updateField = new MetadataEntityFieldDO();
                updateField.setId(field.getId());
                updateField.setDataLength(maxLength); // 将maxLength同步到dataLength
                
                // 直接使用字段仓库进行更新
                entityFieldRepository.updateById(updateField);
            }
        } catch (Exception e) {
            // 记录错误但不影响主流程
            log.warn("同步长度校验到字段时发生异常，字段UUID: {}, maxLength: {}, 错误: {}", fieldUuid, maxLength, e.getMessage(), e);
        }
    }
    
    /**
     * 删除校验规则分组（如果没有其他记录引用）
     */
    private void deleteRuleGroupIfNotReferenced(Long groupId) {
        try {
            MetadataValidationRuleGroupDO group = ruleGroupService.getValidationRuleGroup(groupId);
            if (group == null) {
                return;
            }
            // 检查是否还有其他校验记录引用这个分组
            boolean hasOtherReferences = false;
            
            // 检查长度校验表，优先使用 groupUuid，如果为 null 则尝试用 groupId 字符串形式查询
            List<MetadataValidationLengthDO> lengthRecords;
            String groupUuid = group.getGroupUuid();
            if (groupUuid != null && !groupUuid.isEmpty()) {
                lengthRecords = lengthRepository.findByGroupUuid(groupUuid);
            } else {
                lengthRecords = lengthRepository.findByGroupUuid(String.valueOf(groupId));
            }
            if (!lengthRecords.isEmpty()) {
                hasOtherReferences = true;
            }
            
            // TODO: 如果需要，可以检查其他校验类型表（Required、Unique、Range等）
            
            // 如果没有其他引用，删除规则分组
            if (!hasOtherReferences) {
                ruleGroupService.deleteValidationRuleGroup(groupId);
                log.info("删除了无引用的校验规则分组: {}", groupId);
            }
        } catch (Exception e) {
            // 记录错误但不影响主流程
            log.warn("删除校验规则分组时发生异常，分组ID: {}, 错误: {}", groupId, e.getMessage(), e);
        }
    }
}
