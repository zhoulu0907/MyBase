package com.cmsr.onebase.module.metadata.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationRuleDefinitionDO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 校验规则定义仓储类
 * <p>
 * 提供校验规则定义相关的数据库操作接口，继承自DataRepositoryNew获得基础的CRUD能力
 *
 * @author bty418
 * @date 2025-01-25
 */
@Repository
@Slf4j
public class MetadataValidationRuleDefinitionRepository extends DataRepository<MetadataValidationRuleDefinitionDO> {

    /**
     * 构造方法，指定默认实体类
     */
    public MetadataValidationRuleDefinitionRepository() {
        super(MetadataValidationRuleDefinitionDO.class);
    }

    /**
     * 根据规则组ID查询所有规则定义
     *
     * @param groupId 规则组ID
     * @return 规则定义列表
     */
    public List<MetadataValidationRuleDefinitionDO> selectByGroupId(Long groupId) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.eq("group_id", groupId);
        configStore.order("id", Order.TYPE.ASC);
        return findAllByConfig(configStore);
    }

    /**
     * 根据父规则ID查询子规则
     *
     * @param parentRuleId 父规则ID
     * @return 子规则列表
     */
    public List<MetadataValidationRuleDefinitionDO> selectByParentRuleId(Long parentRuleId) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.eq("parent_rule_id", parentRuleId);
        configStore.order("id", Order.TYPE.ASC);
        return findAllByConfig(configStore);
    }

    /**
     * 根据规则组ID删除所有规则定义
     *
     * @param groupId 规则组ID
     */
    public void deleteByGroupId(Long groupId) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.eq("group_id", groupId);
        deleteByConfig(configStore);
    }

    /**
     * 根据规则组ID查询顶级规则（parent_rule_id为NULL的规则）
     *
     * @param groupId 规则组ID
     * @return 顶级规则列表
     */
    public List<MetadataValidationRuleDefinitionDO> selectTopLevelRulesByGroupId(Long groupId) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.eq("group_id", groupId);
        configStore.isNull("parent_rule_id");
        configStore.order("id", Order.TYPE.ASC);
        return findAllByConfig(configStore);
    }

}
