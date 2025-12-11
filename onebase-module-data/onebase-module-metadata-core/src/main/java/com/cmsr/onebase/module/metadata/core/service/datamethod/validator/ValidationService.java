package com.cmsr.onebase.module.metadata.core.service.datamethod.validator;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.domain.query.MetadataDataMethodSubEntityContext;

import java.util.List;
import java.util.Map;

/**
 * 校验服务接口
 * 
 * 定义统一的校验服务接口，所有具体校验服务都需要实现此接口
 *
 */
public interface ValidationService {

    /**
     * 校验字段数据
     *
     * @param entityUuid 实体UUID
     * @param fieldUuid 字段UUID
     * @param field 字段信息
     * @param value 字段值
     * @param data 完整数据对象
     * @param subEntities 子实体上下文列表
     */
    void validate(String entityUuid, String fieldUuid, MetadataEntityFieldDO field, Object value, Map<String, Object> data, List<MetadataDataMethodSubEntityContext> subEntities);

    /**
     * 获取校验类型
     *
     * @return 校验类型标识
     */
    String getValidationType();

    /**
     * 是否支持该字段类型
     *
     * @param fieldType 字段类型
     * @return 是否支持
     */
    boolean supports(String fieldType);
}
