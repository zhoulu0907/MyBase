package com.cmsr.onebase.module.metadata.build.service.entity;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.BusinessEntityPageReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.BusinessEntityRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.BusinessEntitySaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.ERDiagramRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.SimpleEntityRespVO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 业务实体 Service 接口
 *
 * @author matianyu
 * @date 2025-08-20
 */
public interface MetadataBusinessEntityBuildService {

    /**
     * 创建业务实体
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createBusinessEntity(@Valid BusinessEntitySaveReqVO createReqVO);

    /**
     * 更新业务实体
     *
     * @param updateReqVO 更新信息
     */
    void updateBusinessEntity(@Valid BusinessEntitySaveReqVO updateReqVO);

    /**
     * 删除业务实体
     *
     * @param id 编号
     */
    void deleteBusinessEntity(Long id);

    /**
     * 获得业务实体
     *
     * @param id 编号
     * @return 业务实体
     */
    MetadataBusinessEntityDO getBusinessEntity(Long id);

    /**
     * 根据实体UUID获得业务实体
     *
     * @param entityUuid 实体UUID
     * @return 业务实体
     */
    MetadataBusinessEntityDO getBusinessEntityByUuid(String entityUuid);

    /**
     * 获得业务实体分页
     *
     * @param pageReqVO 分页查询
     * @return 业务实体分页
     */
    PageResult<MetadataBusinessEntityDO> getBusinessEntityPage(BusinessEntityPageReqVO pageReqVO);

    /**
     * 获得业务实体列表
     *
     * @return 业务实体列表
     */
    List<MetadataBusinessEntityDO> getBusinessEntityList();

    /**
     * 根据编码获得业务实体
     *
     * @param code 编码
     * @return 业务实体
     */
    MetadataBusinessEntityDO getBusinessEntityByCode(String code);

    /**
     * 根据数据源UUID获得业务实体列表
     *
     * @param datasourceUuid 数据源UUID
     * @return 业务实体列表
     */
    List<MetadataBusinessEntityDO> getBusinessEntityListByDatasourceUuid(String datasourceUuid);

    /**
     * 根据数据源UUID获取ER图数据
     *
     * @param datasourceUuid 数据源UUID
     * @return ER图数据
     */
    ERDiagramRespVO getERDiagramByDatasourceUuid(String datasourceUuid);

    /**
     * 根据应用ID获取实体列表（仅包含ID和名称）
     * 首先根据appId查询datasourceId，如果有多个datasourceId就选默认第一个
     *
     * @param appId 应用ID
     * @return 简单实体信息列表
     */
    List<SimpleEntityRespVO> getSimpleEntityListByAppId(Long appId);

    /**
     * 根据条件查询业务实体列表
     *
     * @param queryWrapper 查询条件
     * @return 业务实体列表
     */
    List<MetadataBusinessEntityDO> findAllByConfig(com.mybatisflex.core.query.QueryWrapper queryWrapper);

    /**
     * 创建业务实体并返回响应VO
     *
     * @param reqVO 创建请求VO
     * @return 业务实体响应VO
     */
    BusinessEntityRespVO createBusinessEntityWithResponse(@Valid BusinessEntitySaveReqVO reqVO);

    /**
     * 获取业务实体详细信息
     *
     * @param id 业务实体ID
     * @return 业务实体响应VO
     */
    BusinessEntityRespVO getBusinessEntityDetail(Long id);

    /**
     * 获取业务实体分页（带响应VO转换）
     *
     * @param pageReqVO 分页查询请求VO
     * @return 业务实体分页响应VO
     */
    PageResult<BusinessEntityRespVO> getBusinessEntityPageWithResponse(BusinessEntityPageReqVO pageReqVO);

    /**
     * 根据数据源UUID获得业务实体列表（带关系类型）
     *
     * @param datasourceUuid 数据源UUID
     * @return 业务实体响应VO列表
     */
    List<BusinessEntityRespVO> getBusinessEntityListByDatasourceUuidWithRelationType(String datasourceUuid);

    /**
     * 重新创建业务实体的物理表
     * 当发现表不存在时，可以调用此方法来重新创建表
     *
     * @param entityId 业务实体ID
     */
    void recreatePhysicalTable(Long entityId);

}
