package com.cmsr.onebase.module.metadata.build.service.entity;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.EntityFieldBatchCreateReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.EntityFieldBatchCreateRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.EntityFieldBatchSortReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.EntityFieldBatchUpdateReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.EntityFieldBatchUpdateRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.EntityFieldDetailRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.EntityFieldPageReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.EntityFieldSaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.EntityFieldRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.EntityFieldQueryReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.FieldTypeConfigRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.EntityFieldBatchSaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.EntityFieldBatchSaveRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.EntityFieldValidationTypesReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.EntityFieldValidationTypesRespVO;
import com.cmsr.onebase.module.metadata.build.service.entity.vo.EntityFieldQueryVO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 实体字段 Service 接口
 */
public interface MetadataEntityFieldBuildService {

    /**
     * 获取系统支持的字段类型列表
     *
     * @return 字段类型配置列表
     */
    List<FieldTypeConfigRespVO> getFieldTypes();

    /**
     * 批量为业务实体创建字段
     *
     * @param reqVO 批量创建请求
     * @return 批量创建结果
     */
    EntityFieldBatchCreateRespVO batchCreateEntityFields(@Valid EntityFieldBatchCreateReqVO reqVO);

    /**
     * 创建实体字段
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createEntityField(@Valid EntityFieldSaveReqVO createReqVO);

    /**
     * 根据条件查询实体字段列表
     *
     * @param queryVO 查询条件VO
     * @return 字段列表
     */
    List<MetadataEntityFieldDO> getEntityFieldListByConditions(EntityFieldQueryVO queryVO);

    /**
     * 获取字段详细信息
     *
     * @param id 字段ID
     * @return 字段详情
     */
    EntityFieldDetailRespVO getEntityFieldDetail(String id);

    /**
     * 批量更新实体字段信息
     *
     * @param reqVO 批量更新请求
     * @return 批量更新结果
     */
    EntityFieldBatchUpdateRespVO batchUpdateEntityFields(@Valid EntityFieldBatchUpdateReqVO reqVO);

    /**
     * 更新实体字段
     *
     * @param updateReqVO 更新信息
     */
    void updateEntityField(@Valid EntityFieldSaveReqVO updateReqVO);

    /**
     * 删除实体字段
     *
     * @param id 编号
     */
    void deleteEntityField(String id);

    /**
     * 批量更新字段排序
     *
     * @param reqVO 批量排序请求
     */
    void batchSortEntityFields(@Valid EntityFieldBatchSortReqVO reqVO);

    /**
     * 获得实体字段
     *
     * @param id 编号
     * @return 实体字段
     */
    MetadataEntityFieldDO getEntityField(String id);

    /**
     * 创建实体字段（内部使用）
     *
     * @param entityField 实体字段DO
     * @return 字段ID
     */
    Long createEntityFieldInternal(MetadataEntityFieldDO entityField);

    /**
     * 获得实体字段分页
     *
     * @param pageReqVO 分页查询
     * @return 实体字段分页
     */
    PageResult<MetadataEntityFieldDO> getEntityFieldPage(EntityFieldPageReqVO pageReqVO);

    /**
     * 获得实体字段分页（含关联数据）
     *
     * @param pageReqVO 分页查询
     * @return 实体字段分页响应VO，包含选项、约束、自动编号等完整信息
     */
    PageResult<EntityFieldRespVO> getEntityFieldPageWithRelated(EntityFieldPageReqVO pageReqVO);

    /**
     * 获得实体字段列表
     *
     * @return 实体字段列表
     */
    List<MetadataEntityFieldDO> getEntityFieldList();

    /**
     * 根据实体ID获得字段列表
     *
     * @param entityId 实体ID
     * @return 字段列表
     */
    List<MetadataEntityFieldDO> getEntityFieldListByEntityId(String entityId);

    /**
     * 根据实体ID获得字段列表（Long 重载）
     * <p>说明：为兼容核心 datamethod 模块通过 Long 形参调用，避免 NoSuchMethodError。</p>
     *
     * @param entityId 实体ID（Long）
     * @return 字段列表
     */
    default List<MetadataEntityFieldDO> getEntityFieldListByEntityId(Long entityId) {
        if (entityId == null) {
            return java.util.Collections.emptyList();
        }
        return getEntityFieldListByEntityId(String.valueOf(entityId));
    }

    /**
     * 批量删除实体字段
     *
     * @param entityId 实体ID
     */
    void deleteEntityFieldsByEntityId(String entityId);

    /**
     * 批量保存（增删改）实体字段
     * 若任一物理 DDL 失败则整体回滚
     */
    EntityFieldBatchSaveRespVO batchSaveEntityFields(@Valid EntityFieldBatchSaveReqVO reqVO);

    /**
     * 根据条件查询实体字段列表
     *
     * @param queryWrapper 查询条件
     * @return 实体字段列表
     */
    List<MetadataEntityFieldDO> findAllByConfig(com.mybatisflex.core.query.QueryWrapper queryWrapper);

    // ==================== 新增方法：处理包含自动编号的业务逻辑 ====================

    /**
     * 创建字段（包含选项、约束、自动编号处理）
     *
     * @param reqVO 创建请求
     * @return 字段响应信息
     */
    EntityFieldRespVO createEntityFieldWithRelated(@Valid EntityFieldSaveReqVO reqVO);

    /**
     * 更新字段（包含选项、约束、自动编号处理）
     *
     * @param reqVO 更新请求
     * @return 是否成功
     */
    Boolean updateEntityFieldWithRelated(@Valid EntityFieldSaveReqVO reqVO);

    /**
     * 查询字段列表（包含选项、约束、自动编号信息）
     *
     * @param reqVO 查询请求
     * @return 字段列表
     */
    List<EntityFieldRespVO> getEntityFieldListWithRelated(@Valid EntityFieldQueryReqVO reqVO);

    /**
     * 获取字段详情（包含完整的自动编号配置）
     *
     * @param id 字段ID
     * @return 字段详情
     */
    EntityFieldDetailRespVO getEntityFieldDetailWithFullConfig(String id);

    /**
     * 批量查询字段可选校验类型
     *
     * 实现逻辑：
     * 1. 根据 fieldId 查询 metadata_entity_field 得到 field_type
     * 2. 根据 field_type 对应的 metadata_component_field_type.id 关联 metadata_permit_ref_otft
     * 3. 关联 metadata_validation_type 获取校验类型编码/名称/描述/排序
     *
     * @param reqVO 字段ID列表请求
     * @return 每个字段可选校验类型列表
     */
    List<EntityFieldValidationTypesRespVO> getFieldValidationTypes(@Valid EntityFieldValidationTypesReqVO reqVO);

}
