package com.cmsr.onebase.module.metadata.build.service.component;

import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.FieldTypeConfigRespVO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataComponentFieldTypeDO;

import java.util.List;

/**
 * 元数据组件字段类型 Service 接口
 *
 * @author matianyu
 * @date 2025-08-21
 */
public interface MetadataComponentFieldTypeBuildService {

    /**
     * 获取所有启用的字段类型列表
     *
     * @return 字段类型列表
     */
    List<MetadataComponentFieldTypeDO> getAllFieldTypes();

    /**
     * 获取字段类型配置列表（转换为响应VO）
     *
     * @return 字段类型配置响应VO列表
     */
    List<FieldTypeConfigRespVO> getFieldTypeConfigs();

    /**
     * 根据字段类型编码获取字段类型配置
     *
     * @param fieldTypeCode 字段类型编码
     * @return 字段类型配置DO
     */
    MetadataComponentFieldTypeDO getByFieldTypeCode(String fieldTypeCode);

    /**
     * 根据字段类型编码映射数据库字段类型
     *
     * @param fieldTypeCode 字段类型编码
     * @param dataLength 数据长度
     * @return 数据库字段类型
     */
    String mapFieldTypeToDatabaseType(String fieldTypeCode, Integer dataLength);
}
