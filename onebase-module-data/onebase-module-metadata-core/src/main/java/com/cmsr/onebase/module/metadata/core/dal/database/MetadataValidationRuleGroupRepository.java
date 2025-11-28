package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRuleGroupDO;
import com.cmsr.onebase.module.metadata.core.dal.mapper.MetadataValidationRuleGroupMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

/**
 * 校验规则分组仓储类
 * <p>
 * 提供校验规则分组相关的数据库操作接口，继承自ServiceImpl获得基础的CRUD能力
 *
 * @author bty418
 * @date 2025-01-25
 */
@Repository
@Slf4j
public class MetadataValidationRuleGroupRepository extends ServiceImpl<MetadataValidationRuleGroupMapper, MetadataValidationRuleGroupDO> {

    /**
     * 分页查询校验规则分组，支持按名称和业务实体ID过滤
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param name 规则组名称
     * @param entityId 业务实体ID，可为null
     * @return 分页结果
     */
    public PageResult<MetadataValidationRuleGroupDO> selectPage(int pageNum, int pageSize, String name, Long entityId) {
        QueryWrapper queryWrapper = this.query()
                .like(MetadataValidationRuleGroupDO::getRgName, name, StringUtils.hasText(name))
                .eq(MetadataValidationRuleGroupDO::getEntityId, entityId, entityId != null)
                .orderBy(MetadataValidationRuleGroupDO::getCreateTime, false);

        Page<MetadataValidationRuleGroupDO> pageQuery = Page.of(pageNum, pageSize);
        Page<MetadataValidationRuleGroupDO> pageResult = this.page(pageQuery, queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }

    /**
     * 根据规则组名称查询（用于校验名称唯一性）
     *
     * @param rgName 规则组名称
     * @param excludeId 排除的ID（用于修改时排除自身）
     * @return 校验规则分组对象
     */
    public MetadataValidationRuleGroupDO selectByRgName(String rgName, Long excludeId) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataValidationRuleGroupDO::getRgName, rgName)
                .ne(MetadataValidationRuleGroupDO::getId, excludeId, excludeId != null);
        return getOne(queryWrapper);
    }
}
