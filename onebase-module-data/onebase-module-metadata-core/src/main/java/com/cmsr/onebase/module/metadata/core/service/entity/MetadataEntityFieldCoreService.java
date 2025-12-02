package com.cmsr.onebase.module.metadata.core.service.entity;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

/**
 * 实体字段 Service 接口 - 核心数据层接口
 * TODO: Controller层应该使用build模块中的EntityFieldBuildService，该接口负责VO转换
 *
 * @author matianyu
 * @date 2025-08-20
 */
public interface MetadataEntityFieldCoreService {

    /**
     * 创建实体字段
     *
     * @param entityField 实体字段DO
     * @return 实体字段编号
     */
    Long createEntityField(@Valid MetadataEntityFieldDO entityField);

    /**
     * 更新实体字段
     *
     * @param entityField 实体字段DO
     */
    void updateEntityField(@Valid MetadataEntityFieldDO entityField);

    /**
     * 删除实体字段
     *
     * @param id 实体字段编号
     */
    void deleteEntityField(Long id);

    /**
     * 获得实体字段
     *
     * @param id 实体字段编号
     * @return 实体字段DO
     */
    MetadataEntityFieldDO getEntityField(Long id);

    /**
     * 获得实体字段列表
     *
     * @param entityId 实体编号
     * @return 实体字段列表
     */
    List<MetadataEntityFieldDO> getEntityFieldListByEntityId(Long entityId);

    /**
     * 根据字段ID列表批量获取实体字段
     *
     * @param fieldIds 字段ID列表
     * @return 实体字段列表
     */
    List<MetadataEntityFieldDO> getEntityFieldListByIds(List<Long> fieldIds);

    /**
     * 获得实体字段分页
     *
     * @param pageNum  页码
     * @param pageSize 页大小
     * @param entityId 实体编号
     * @return 实体字段分页
     */
    PageResult<MetadataEntityFieldDO> getEntityFieldPage(int pageNum, int pageSize, Long entityId);

    /**
     * 根据字段编码获得实体字段
     *
     * @param fieldCode 字段编码
     * @param entityId  实体编号
     * @return 实体字段
     */
    MetadataEntityFieldDO getEntityFieldByCode(String fieldCode, Long entityId);

    /**
     * 批量创建实体字段
     *
     * @param entityFields 实体字段列表
     * @return 创建的字段数量
     */
    int batchCreateEntityFields(@Valid List<MetadataEntityFieldDO> entityFields);

    /**
     * 根据字段ID列表返回对应的JDBC数据类型
     * 先查 metadata_entity_field 获取字段类型编码，再查 metadata_component_field_type 获取
     * data_type
     *
     * @param fieldIds 字段ID列表
     * @return 字段ID到JDBC类型的映射
     */
    Map<Long, String> getFieldJdbcTypes(List<Long> fieldIds);

    /**
     * 根据字段ID列表返回对应的JDBC数据类型和字段类型信息
     * 先查 metadata_entity_field 获取字段类型编码，再查 metadata_component_field_type 获取
     * data_type
     *
     * @param fieldIds 字段ID列表
     * @return 字段JDBC类型和字段类型信息映射
     */
    Map<Long, Map<String, String>> getFieldJdbcTypesWithFieldType(List<Long> fieldIds);

    /**
     * 根据字典类型ID统计引用该字典的字段数量
     * 用于在删除字典前验证是否有字段引用
     *
     * @param dictTypeId 字典类型ID
     * @return 引用该字典的字段数量
     */
    long countByDictTypeId(Long dictTypeId);

    // TODO: 以下方法需要在build模块中实现，涉及VO转换
    /*
     * List<FieldTypeConfigRespVO> getFieldTypeConfigs();
     * EntityFieldBatchCreateRespVO batchCreateEntityFields(@Valid
     * EntityFieldBatchCreateReqVO reqVO);
     * EntityFieldDetailRespVO getEntityFieldDetail(Long id);
     * EntityFieldBatchUpdateRespVO batchUpdateEntityFields(@Valid
     * EntityFieldBatchUpdateReqVO reqVO);
     * void batchSortEntityFields(@Valid EntityFieldBatchSortReqVO reqVO);
     * PageResult<EntityFieldRespVO> getEntityFieldPage(EntityFieldPageReqVO
     * pageReqVO);
     * EntityFieldBatchSaveRespVO batchSaveEntityFields(@Valid
     * EntityFieldBatchSaveReqVO reqVO);
     * EntityFieldRespVO saveEntityField(@Valid EntityFieldSaveReqVO reqVO);
     * List<EntityFieldRespVO> getEntityFieldListByQuery(EntityFieldQueryReqVO
     * reqVO);
     * EntityFieldValidationTypesRespVO getEntityFieldValidationTypes(@Valid
     * EntityFieldValidationTypesReqVO reqVO);
     */
}
