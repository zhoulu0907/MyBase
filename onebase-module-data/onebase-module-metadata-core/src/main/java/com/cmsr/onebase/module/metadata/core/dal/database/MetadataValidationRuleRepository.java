package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRuleDefinitionDO;
import com.cmsr.onebase.module.metadata.core.dal.mapper.MetadataValidationRuleDefinitionMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 元数据验证规则仓储类
 * <p>
 * 提供验证规则相关的数据库操作接口，继承自ServiceImpl获得基础的CRUD能力
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
@Slf4j
public class MetadataValidationRuleRepository extends ServiceImpl<MetadataValidationRuleDefinitionMapper, MetadataValidationRuleDefinitionDO> {

    /**
     * 根据字段ID获取验证规则列表
     *
     * @param fieldId 字段ID
     * @return 验证规则列表
     */
    public List<MetadataValidationRuleDefinitionDO> getValidationRulesByFieldId(Long fieldId) {
        QueryWrapper queryWrapper = this.query()
                .eq("field_id", fieldId)
                .orderBy(MetadataValidationRuleDefinitionDO::getCreateTime, false);
        return list(queryWrapper);
    }

    /**
     * 根据规则类型获取验证规则列表
     *
     * @param ruleType 规则类型
     * @return 验证规则列表
     */
    public List<MetadataValidationRuleDefinitionDO> getValidationRulesByType(String ruleType) {
        QueryWrapper queryWrapper = this.query()
                .eq("rule_type", ruleType)
                .orderBy(MetadataValidationRuleDefinitionDO::getCreateTime, false);
        return list(queryWrapper);
    }

    /**
     * 根据字段ID和规则类型获取验证规则
     *
     * @param fieldId 字段ID
     * @param ruleType 规则类型
     * @return 验证规则对象
     */
    public MetadataValidationRuleDefinitionDO getValidationRuleByFieldAndType(Long fieldId, String ruleType) {
        QueryWrapper queryWrapper = this.query()
                .eq("field_id", fieldId)
                .eq("rule_type", ruleType);
        return getOne(queryWrapper);
    }

    /**
     * 获取所有验证规则列表
     *
     * @return 验证规则列表
     */
    public List<MetadataValidationRuleDefinitionDO> getAllValidationRules() {
        QueryWrapper queryWrapper = this.query()
                .orderBy(MetadataValidationRuleDefinitionDO::getCreateTime, false);
        return list(queryWrapper);
    }
}
