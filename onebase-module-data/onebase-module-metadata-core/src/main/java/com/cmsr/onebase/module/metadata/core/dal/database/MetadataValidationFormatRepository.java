package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationFormatDO;
import com.cmsr.onebase.module.metadata.core.dal.mapper.MetadataValidationFormatMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 格式验证规则仓储类
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
@Slf4j
public class MetadataValidationFormatRepository extends ServiceImpl<MetadataValidationFormatMapper, MetadataValidationFormatDO> {

    /**
     * 根据字段UUID查询格式验证规则列表
     *
     * @param fieldUuid 字段UUID
     * @return 格式验证规则列表
     */
    public List<MetadataValidationFormatDO> findByFieldUuid(String fieldUuid) {
        QueryWrapper queryWrapper = query()
                .eq(MetadataValidationFormatDO::getFieldUuid, fieldUuid);
        return list(queryWrapper);
    }

    /**
     * 根据字段UUID查询正则表达式格式验证规则
     *
     * @param fieldUuid 字段UUID
     * @return 正则表达式格式验证规则
     */
    public MetadataValidationFormatDO findRegexByFieldUuid(String fieldUuid) {
        QueryWrapper queryWrapper = query()
                .eq(MetadataValidationFormatDO::getFieldUuid, fieldUuid)
                .eq(MetadataValidationFormatDO::getFormatCode, "REGEX");
        return getOne(queryWrapper);
    }

    /**
     * 根据字段UUID删除格式验证规则
     *
     * @param fieldUuid 字段UUID
     */
    public void deleteByFieldUuid(String fieldUuid) {
        for (var item : findByFieldUuid(fieldUuid)) {
            removeById(item.getId());
        }
    }

    /**
     * 根据组UUID查询格式验证规则列表
     *
     * @param groupUuid 组UUID
     * @return 格式验证规则列表
     */
    public List<MetadataValidationFormatDO> findByGroupUuid(String groupUuid) {
        QueryWrapper queryWrapper = query()
                .eq(MetadataValidationFormatDO::getGroupUuid, groupUuid);
        return list(queryWrapper);
    }

    // ====== 兼容旧代码的方法 ======

    /**
     * 根据字段ID查询正则表达式格式验证规则（兼容旧代码）
     * @deprecated 请使用 findRegexByFieldUuid()
     */
    @Deprecated
    public MetadataValidationFormatDO findRegexByFieldId(Long fieldId) {
        return findRegexByFieldUuid(String.valueOf(fieldId));
    }

    /**
     * 根据组ID查询格式验证规则列表（兼容旧代码）
     * @deprecated 请使用 findByGroupUuid()
     */
    @Deprecated
    public List<MetadataValidationFormatDO> findByGroupId(Long groupId) {
        return findByGroupUuid(String.valueOf(groupId));
    }

    /**
     * 根据字段ID删除格式验证规则（兼容旧代码）
     * @deprecated 请使用 deleteByFieldUuid()
     */
    @Deprecated
    public void deleteByFieldId(Long fieldId) {
        deleteByFieldUuid(String.valueOf(fieldId));
    }
}
