package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRuleDefinitionDO;
import com.cmsr.onebase.module.metadata.core.dal.mapper.MetadataValidationRuleDefinitionMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 校验规则定义仓储类
 * <p>
 * 提供校验规则定义相关的数据库操作接口，继承自ServiceImpl获得基础的CRUD能力
 *
 * @author bty418
 * @date 2025-01-25
 */
@Repository
@Slf4j
public class MetadataValidationRuleDefinitionRepository extends ServiceImpl<MetadataValidationRuleDefinitionMapper, MetadataValidationRuleDefinitionDO> {

    /**
     * 根据规则组ID查询所有规则定义
     *
     * @param groupId 规则组ID
     * @return 规则定义列表
     */
    public List<MetadataValidationRuleDefinitionDO> selectByGroupId(Long groupId) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataValidationRuleDefinitionDO::getGroupId, groupId)
                .orderBy(MetadataValidationRuleDefinitionDO::getId, true);
        return list(queryWrapper);
    }

    /**
     * 根据父规则ID查询子规则
     *
     * @param parentRuleId 父规则ID
     * @return 子规则列表
     */
    public List<MetadataValidationRuleDefinitionDO> selectByParentRuleId(Long parentRuleId) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataValidationRuleDefinitionDO::getParentRuleId, parentRuleId)
                .orderBy(MetadataValidationRuleDefinitionDO::getId, true);
        return list(queryWrapper);
    }

    /**
     * 根据规则组ID删除所有规则定义
     *
     * @param groupId 规则组ID
     */
    public void deleteByGroupId(Long groupId) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataValidationRuleDefinitionDO::getGroupId, groupId);
        remove(queryWrapper);
    }

    /**
     * 根据规则组ID查询顶级规则（parent_rule_id为NULL的规则）
     *
     * @param groupId 规则组ID
     * @return 顶级规则列表
     */
    public List<MetadataValidationRuleDefinitionDO> selectTopLevelRulesByGroupId(Long groupId) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataValidationRuleDefinitionDO::getGroupId, groupId)
                .isNull(MetadataValidationRuleDefinitionDO::getParentRuleId)
                .orderBy(MetadataValidationRuleDefinitionDO::getId, true);
        return list(queryWrapper);
    }
}
