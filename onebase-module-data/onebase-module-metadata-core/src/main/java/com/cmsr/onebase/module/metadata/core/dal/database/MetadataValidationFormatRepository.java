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
     * 根据字段ID查询格式验证规则列表
     *
     * @param fieldId 字段ID
     * @return 格式验证规则列表
     */
    public List<MetadataValidationFormatDO> findByFieldId(Long fieldId) {
        QueryWrapper queryWrapper = query()
                .eq(MetadataValidationFormatDO::getFieldId, fieldId);
        return list(queryWrapper);
    }

    /**
     * 根据字段ID查询正则表达式格式验证规则
     *
     * @param fieldId 字段ID
     * @return 正则表达式格式验证规则
     */
    public MetadataValidationFormatDO findRegexByFieldId(Long fieldId) {
        QueryWrapper queryWrapper = query()
                .eq(MetadataValidationFormatDO::getFieldId, fieldId)
                .eq(MetadataValidationFormatDO::getFormatCode, "REGEX");
        return getOne(queryWrapper);
    }

    /**
     * 根据字段ID删除格式验证规则
     *
     * @param fieldId 字段ID
     */
    public void deleteByFieldId(Long fieldId) {
        for (var item : findByFieldId(fieldId)) {
            removeById(item.getId());
        }
    }

    /**
     * 根据组ID查询格式验证规则列表
     *
     * @param groupId 组ID
     * @return 格式验证规则列表
     */
    public List<MetadataValidationFormatDO> findByGroupId(Long groupId) {
        QueryWrapper queryWrapper = query()
                .eq(MetadataValidationFormatDO::getGroupId, groupId);
        return list(queryWrapper);
    }
}
