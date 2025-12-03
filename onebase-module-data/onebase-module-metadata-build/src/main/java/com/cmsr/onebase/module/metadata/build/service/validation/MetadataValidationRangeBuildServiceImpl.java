package com.cmsr.onebase.module.metadata.build.service.validation;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRangeRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRangeSaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRangeUpdateReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRuleGroupSaveReqVO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationRangeRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRangeDO;
import com.cmsr.onebase.module.metadata.build.service.entity.MetadataEntityFieldBuildService;
import com.cmsr.onebase.module.metadata.core.util.MetadataIdUuidConverter;
import com.cmsr.onebase.module.metadata.core.util.StatusEnumUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 范围校验 Service 实现
 */
@Service
public class MetadataValidationRangeBuildServiceImpl implements MetadataValidationRangeBuildService {

    @Resource private MetadataValidationRangeRepository rangeRepository; // 自身仓库
    @Resource private MetadataValidationRuleGroupBuildService ruleGroupService; // 其他服务
    @Resource private MetadataEntityFieldBuildService entityFieldService; // 其他服务
    @Resource private MetadataIdUuidConverter idUuidConverter; // ID转UUID工具

    @Override
    public MetadataValidationRangeDO getByFieldId(String fieldUuid) {
        // 当前每字段至多一条，若未来支持多条，可调整为取最新或抛错
        return rangeRepository.findByFieldUuid(fieldUuid).stream().findFirst().orElse(null);
    }

    @Override
    public ValidationRangeRespVO getByFieldIdWithRgName(String fieldUuid) {
        MetadataValidationRangeDO rangeDO = getByFieldId(fieldUuid);
        if (rangeDO == null) {
            return null;
        }

        // 转换DO为VO
        ValidationRangeRespVO respVO = BeanUtils.toBean(rangeDO, ValidationRangeRespVO.class);

        // 格式化数值，最多保留4位小数
        formatDecimalValues(respVO, rangeDO);

        // 获取规则组信息，包括提示语等字段
        var ruleGroup = ruleGroupService.getValidationRuleGroupByUuid(rangeDO.getGroupUuid());
        if (ruleGroup != null) {
            respVO.setRgName(ruleGroup.getRgName());
            respVO.setPromptMessage(ruleGroup.getPopPrompt());
        }

        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ValidationRangeSaveReqVO vo) {
        Assert.notNull(vo, "vo不能为空");
        // ID与UUID兼容处理：优先使用UUID，若为空则通过ID转换
        vo.setFieldUuid(idUuidConverter.resolveFieldUuid(vo.getFieldUuid(), vo.getFieldId()));
        Assert.hasText(vo.getRgName(), "规则组名称不能为空");

        // 获取字段信息
        MetadataEntityFieldDO field = entityFieldService.getEntityFieldByUuid(vo.getFieldUuid());
        Assert.notNull(field, "字段不存在");

        // 检查同一字段是否已存在范围校验规则
        MetadataValidationRangeDO existingRule = getByFieldId(vo.getFieldUuid());
        if (existingRule != null) {
            throw new IllegalStateException("该字段已存在范围校验规则，同一字段只能有一条范围校验规则");
        }

        // 处理规则组：先查找，不存在则创建；存在但已被其他字段复用则新建
        String groupUuid = null;
        var existingGroup = ruleGroupService.getByName(vo.getRgName());
        boolean needCreateGroup = false;
        if (existingGroup != null) {
            var groupRangeList = rangeRepository.findByGroupUuid(existingGroup.getGroupUuid());
            boolean reused = groupRangeList.stream().anyMatch(u -> !u.getFieldUuid().equals(vo.getFieldUuid()));
            if (reused) {
                needCreateGroup = true;
            } else {
                groupUuid = existingGroup.getGroupUuid();
            }
        } else {
            needCreateGroup = true;
        }
        if (needCreateGroup) {
            // 创建新的规则组
            ValidationRuleGroupSaveReqVO groupVO = new ValidationRuleGroupSaveReqVO();
            groupVO.setRgName(vo.getRgName());
            groupVO.setRgDesc("自动创建的规则组：" + vo.getRgName());
            groupVO.setRgStatus(StatusEnumUtil.ACTIVE);
            // 透传可选的组级提示配置
            groupVO.setValMethod(vo.getValMethod());
            groupVO.setPopPrompt(vo.getPopPrompt());
            groupVO.setPopType(vo.getPopType());
            groupVO.setValidationType("RANGE");
            // 修复：同步entityUuid到规则组
            groupVO.setEntityUuid(field.getEntityUuid());
            Long groupId = ruleGroupService.createValidationRuleGroup(groupVO);
            // 获取新创建的组的UUID
            var newGroup = ruleGroupService.getValidationRuleGroup(groupId);
            groupUuid = newGroup != null ? newGroup.getGroupUuid() : null;
        }

        // 转换VO为DO并设置必要字段
        MetadataValidationRangeDO data = BeanUtils.toBean(vo, MetadataValidationRangeDO.class);
        data.setEntityUuid(field.getEntityUuid());
        data.setGroupUuid(groupUuid);
        
        // 设置默认值
        if (data.getIsEnabled() == null) {
            data.setIsEnabled(1); // 默认启用
        }
        if (data.getIncludeMin() == null) {
            data.setIncludeMin(1); // 默认包含最小边界
        }
        if (data.getIncludeMax() == null) {
            data.setIncludeMax(1); // 默认包含最大边界
        }
        
        // 根据传入的值自动判断范围类型
        if (data.getRangeType() == null) {
            if (data.getMinValue() != null || data.getMaxValue() != null) {
                data.setRangeType("NUMBER");
            } else if (data.getMinDate() != null || data.getMaxDate() != null) {
                data.setRangeType("DATE");
            } else {
                // 默认为数值类型
                data.setRangeType("NUMBER");
            }
        }

        // 保存范围校验规则
        rangeRepository.saveOrUpdate(data);
        return data.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ValidationRangeUpdateReqVO reqVO) {
        // 约定：reqVO.id 为 groupId(数据库主键)
        Long groupIdParam = reqVO.getId();
        Assert.notNull(groupIdParam, "规则组ID不能为空");
        // 先根据主键ID获取规则组
        var groupDO = ruleGroupService.getValidationRuleGroup(groupIdParam);
        Assert.notNull(groupDO, "规则组不存在(组ID=" + groupIdParam + ")");
        // 再根据groupUuid获取范围校验记录
        var list = rangeRepository.findByGroupUuid(groupDO.getGroupUuid());
        Assert.notEmpty(list, "当前范围校验规则不存在(组ID=" + groupIdParam + ")");
        if (list.size() > 1) { throw new IllegalStateException("数据异常：同一组存在多条范围校验规则(组ID=" + groupIdParam + ")"); }
        MetadataValidationRangeDO existingDO = list.get(0);
        MetadataEntityFieldDO entityFieldDO = entityFieldService.getEntityFieldByUuid(existingDO.getFieldUuid());
        Assert.notNull(entityFieldDO, "字段不存在");
        String targetGroupUuid = groupDO.getGroupUuid(); // 不迁移组，但同步组配置
        if (groupDO != null) {
            boolean needGroupUpdate = false;
            ValidationRuleGroupSaveReqVO updateGroupVO = new ValidationRuleGroupSaveReqVO();
            updateGroupVO.setId(groupDO.getId());
            updateGroupVO.setRgName(groupDO.getRgName());
            updateGroupVO.setRgDesc(groupDO.getRgDesc());
            updateGroupVO.setRgStatus(groupDO.getRgStatus());
            updateGroupVO.setValidationType(groupDO.getValidationType());
            updateGroupVO.setEntityUuid(groupDO.getEntityUuid());
            if (reqVO.getPopPrompt() != null && !reqVO.getPopPrompt().equals(groupDO.getPopPrompt())) { updateGroupVO.setPopPrompt(reqVO.getPopPrompt()); needGroupUpdate = true; }
            if (reqVO.getValMethod() != null && !reqVO.getValMethod().equals(groupDO.getValMethod())) { updateGroupVO.setValMethod(reqVO.getValMethod()); needGroupUpdate = true; }
            if (reqVO.getPopType() != null && !reqVO.getPopType().equals(groupDO.getPopType())) { updateGroupVO.setPopType(reqVO.getPopType()); needGroupUpdate = true; }
            if (needGroupUpdate) { ruleGroupService.updateValidationRuleGroup(updateGroupVO); }
        }
        MetadataValidationRangeDO updateDO = BeanUtils.toBean(reqVO, MetadataValidationRangeDO.class);
        updateDO.setId(existingDO.getId());
        updateDO.setFieldUuid(existingDO.getFieldUuid());
        updateDO.setEntityUuid(existingDO.getEntityUuid());
        updateDO.setGroupUuid(targetGroupUuid);
        rangeRepository.updateById(updateDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByFieldId(String fieldUuid) {
        // 先获取要删除的记录，以便后续删除关联的校验规则分组
        MetadataValidationRangeDO recordToDelete = getByFieldId(fieldUuid);
        
        // 删除范围校验记录
        rangeRepository.deleteByFieldUuid(fieldUuid);
        
        // 删除关联的校验规则分组(根据groupUuid获取规则组ID后删除)
        if (recordToDelete != null && recordToDelete.getGroupUuid() != null) {
            var groupDO = ruleGroupService.getValidationRuleGroupByUuid(recordToDelete.getGroupUuid());
            if (groupDO != null) {
                ruleGroupService.safeDeleteGroupDirect(groupDO.getId());
            }
        }
    }

    @Override
    public ValidationRangeRespVO getById(Long id) {
        // 先根据主键ID获取规则组
        var groupDO = ruleGroupService.getValidationRuleGroup(id);
        if (groupDO == null) { return null; }
        // 再根据groupUuid获取范围校验记录
        var list = rangeRepository.findByGroupUuid(groupDO.getGroupUuid());
        if (list.isEmpty()) { return null; }
        if (list.size() > 1) { throw new IllegalStateException("数据异常：同一组存在多条范围校验规则(组ID=" + id + ")"); }
        MetadataValidationRangeDO rangeDO = list.get(0);
        ValidationRangeRespVO respVO = BeanUtils.toBean(rangeDO, ValidationRangeRespVO.class);
        
        // 格式化数值，最多保留4位小数
        formatDecimalValues(respVO, rangeDO);
        
        // 获取规则组信息，包括提示语等字段
        respVO.setRgName(groupDO.getRgName());
        respVO.setPromptMessage(groupDO.getPopPrompt());
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        // 先根据主键ID获取规则组
        var groupDO = ruleGroupService.getValidationRuleGroup(id);
        
        // 删除子表记录
        if (groupDO != null) {
            var list = rangeRepository.findByGroupUuid(groupDO.getGroupUuid());
            if (!list.isEmpty()) {
                if (list.size() > 1) {
                    throw new IllegalStateException("数据异常：同一组存在多条范围校验规则(组ID=" + id + ")");
                }
                MetadataValidationRangeDO rangeDO = list.get(0);
                rangeRepository.removeById(rangeDO.getId());
            }
        }
        
        // 无论子表是否存在，都要删除主表作为兖底（防止脏数据）
        ruleGroupService.safeDeleteGroupDirect(id);
    }

    /**
     * 格式化范围校验响应VO中的数值字段，最多保留4位小数
     * 
     * @param respVO 响应VO对象
     * @param rangeDO 范围校验DO对象
     */
    private void formatDecimalValues(ValidationRangeRespVO respVO, MetadataValidationRangeDO rangeDO) {
        if (rangeDO.getMinValue() != null) {
            respVO.setMinValue(formatBigDecimal(rangeDO.getMinValue()));
        }
        if (rangeDO.getMaxValue() != null) {
            respVO.setMaxValue(formatBigDecimal(rangeDO.getMaxValue()));
        }
    }

    /**
     * 格式化BigDecimal为字符串，最多保留4位小数，去除尾部无意义的0
     * 
     * @param value BigDecimal值
     * @return 格式化后的字符串
     */
    private String formatBigDecimal(BigDecimal value) {
        if (value == null) {
            return null;
        }
        // 保留4位小数，使用HALF_UP舍入模式（四舍五入）
        BigDecimal scaled = value.setScale(4, RoundingMode.HALF_UP);
        // 使用stripTrailingZeros去除尾部的0，然后转为字符串
        // 使用toPlainString避免科学计数法
        return scaled.stripTrailingZeros().toPlainString();
    }
}
