package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.framework.orm.repo.BaseBizRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRuleDefinitionDO;
import com.cmsr.onebase.module.metadata.core.dal.mapper.MetadataValidationRuleDefinitionMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
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
public class MetadataValidationRuleDefinitionRepository extends BaseBizRepository<MetadataValidationRuleDefinitionMapper, MetadataValidationRuleDefinitionDO> {

    /**
     * 根据规则组ID查询所有规则定义
     *
     * @param groupId 规则组ID
     * @return 规则定义列表
     */
    public List<MetadataValidationRuleDefinitionDO> selectByGroupId(Long groupId) {
        // 修复：使用 groupId 转换为字符串后按 groupUuid 过滤
        return selectByGroupUuid(String.valueOf(groupId));
    }

    /**
     * 根据规则组UUID查询所有规则定义
     *
     * @param groupUuid 规则组UUID
     * @return 规则定义列表
     */
    public List<MetadataValidationRuleDefinitionDO> selectByGroupUuid(String groupUuid) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataValidationRuleDefinitionDO::getGroupUuid, groupUuid)
                .orderBy(MetadataValidationRuleDefinitionDO::getId, true);
        return list(queryWrapper);
    }

    /**
     * 根据父规则UUID查询子规则
     *
     * @param parentRuleUuid 父规则UUID
     * @return 子规则列表
     */
    public List<MetadataValidationRuleDefinitionDO> selectByParentRuleUuid(String parentRuleUuid) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataValidationRuleDefinitionDO::getParentRuleUuid, parentRuleUuid)
                .orderBy(MetadataValidationRuleDefinitionDO::getId, true);
        return list(queryWrapper);
    }

    /**
     * 根据规则组UUID删除所有规则定义
     *
     * @param groupUuid 规则组UUID
     */
    public void deleteByGroupUuid(String groupUuid) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataValidationRuleDefinitionDO::getGroupUuid, groupUuid);
        remove(queryWrapper);
    }

    /**
     * 根据规则组UUID查询顶级规则（parent_rule_uuid为NULL的规则）
     *
     * @param groupUuid 规则组UUID
     * @return 顶级规则列表
     */
    public List<MetadataValidationRuleDefinitionDO> selectTopLevelRulesByGroupUuid(String groupUuid) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataValidationRuleDefinitionDO::getGroupUuid, groupUuid)
                .isNull(MetadataValidationRuleDefinitionDO::getParentRuleUuid)
                .orderBy(MetadataValidationRuleDefinitionDO::getId, true);
        return list(queryWrapper);
    }

    public List<MetadataValidationRuleDefinitionDO> selectByGroupUuids(Collection<String> groupUuids) {
        if (groupUuids == null || groupUuids.isEmpty()) { return Collections.emptyList(); }
        QueryWrapper queryWrapper = this.query()
                .in(MetadataValidationRuleDefinitionDO::getGroupUuid, groupUuids);
        return list(queryWrapper);
    }

    // ====== 兼容旧代码的方法 ======

    /**
     * 根据规则组ID删除所有规则定义（兼容旧代码）
     * @deprecated 请使用 deleteByGroupUuid()
     */
    @Deprecated
    public void deleteByGroupId(Long groupId) {
        deleteByGroupUuid(String.valueOf(groupId));
    }
}
