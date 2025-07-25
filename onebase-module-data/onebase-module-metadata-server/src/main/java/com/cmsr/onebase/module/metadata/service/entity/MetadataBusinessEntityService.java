package com.cmsr.onebase.module.metadata.service.entity;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.BusinessEntityPageReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.BusinessEntitySaveReqVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataBusinessEntityDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 业务实体 Service 接口
 */
public interface MetadataBusinessEntityService {

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
     * 根据数据源ID获得业务实体列表
     *
     * @param datasourceId 数据源ID
     * @return 业务实体列表
     */
    List<MetadataBusinessEntityDO> getBusinessEntityListByDatasourceId(Long datasourceId);

}
