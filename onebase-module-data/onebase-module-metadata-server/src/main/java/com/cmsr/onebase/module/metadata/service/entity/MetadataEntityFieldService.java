package com.cmsr.onebase.module.metadata.service.entity;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldBatchCreateReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldBatchCreateRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldBatchSortReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldBatchUpdateReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldBatchUpdateRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldDetailRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldPageReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldSaveReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.FieldTypeConfigRespVO;
import com.cmsr.onebase.module.metadata.service.entity.vo.EntityFieldQueryVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataEntityFieldDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 实体字段 Service 接口
 */
public interface MetadataEntityFieldService {

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
    EntityFieldDetailRespVO getEntityFieldDetail(Long id);

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
    void deleteEntityField(Long id);

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
    MetadataEntityFieldDO getEntityField(Long id);

    /**
     * 获得实体字段分页
     *
     * @param pageReqVO 分页查询
     * @return 实体字段分页
     */
    PageResult<MetadataEntityFieldDO> getEntityFieldPage(EntityFieldPageReqVO pageReqVO);

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
    List<MetadataEntityFieldDO> getEntityFieldListByEntityId(Long entityId);

    /**
     * 批量删除实体字段
     *
     * @param entityId 实体ID
     */
    void deleteEntityFieldsByEntityId(Long entityId);

}
