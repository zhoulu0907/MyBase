package com.cmsr.onebase.module.metadata.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepositoryNew;
import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationRuleDefinitionDO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 元数据验证规则仓储类
 * <p>
 * 提供验证规则相关的数据库操作接口，继承自DataRepositoryNew获得基础的CRUD能力
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
@Slf4j
public class MetadataValidationRuleRepository extends DataRepositoryNew<MetadataValidationRuleDefinitionDO> {

    /**
     * 构造方法，指定默认实体类
     */
    public MetadataValidationRuleRepository() {
        super(MetadataValidationRuleDefinitionDO.class);
    }

    /**
     * 根据字段ID获取验证规则列表
     *
     * @param fieldId 字段ID
     * @return 验证规则列表
     */
    public List<MetadataValidationRuleDefinitionDO> getValidationRulesByFieldId(Long fieldId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("field_id", fieldId);
        configStore.order("create_time", org.anyline.entity.Order.TYPE.DESC);
        return findAllByConfig(configStore);
    }

    /**
     * 根据规则类型获取验证规则列表
     *
     * @param ruleType 规则类型
     * @return 验证规则列表
     */
    public List<MetadataValidationRuleDefinitionDO> getValidationRulesByType(String ruleType) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("rule_type", ruleType);
        configStore.order("create_time", org.anyline.entity.Order.TYPE.DESC);
        return findAllByConfig(configStore);
    }

    /**
     * 根据字段ID和规则类型获取验证规则
     *
     * @param fieldId 字段ID
     * @param ruleType 规则类型
     * @return 验证规则对象
     */
    public MetadataValidationRuleDefinitionDO getValidationRuleByFieldAndType(Long fieldId, String ruleType) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("field_id", fieldId);
        configStore.and("rule_type", ruleType);
        return findOne(configStore);
    }

    /**
     * 获取所有验证规则列表
     *
     * @return 验证规则列表
     */
    public List<MetadataValidationRuleDefinitionDO> getAllValidationRules() {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.order("create_time", org.anyline.entity.Order.TYPE.DESC);
        return findAllByConfig(configStore);
    }
}
