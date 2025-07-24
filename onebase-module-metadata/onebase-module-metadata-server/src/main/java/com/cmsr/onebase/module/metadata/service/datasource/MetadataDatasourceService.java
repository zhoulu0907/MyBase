package com.cmsr.onebase.module.metadata.service.datasource;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.controller.admin.datasource.vo.DatasourcePageReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.datasource.vo.DatasourceSaveReqVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.datasource.MetadataDatasourceDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 数据源 Service 接口
 */
public interface MetadataDatasourceService {

    /**
     * 创建数据源
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createDatasource(@Valid DatasourceSaveReqVO createReqVO);

    /**
     * 更新数据源
     *
     * @param updateReqVO 更新信息
     */
    void updateDatasource(@Valid DatasourceSaveReqVO updateReqVO);

    /**
     * 删除数据源
     *
     * @param id 编号
     */
    void deleteDatasource(Long id);

    /**
     * 获得数据源
     *
     * @param id 编号
     * @return 数据源
     */
    MetadataDatasourceDO getDatasource(Long id);

    /**
     * 获得数据源分页
     *
     * @param pageReqVO 分页查询
     * @return 数据源分页
     */
    PageResult<MetadataDatasourceDO> getDatasourcePage(DatasourcePageReqVO pageReqVO);

    /**
     * 获得数据源列表
     *
     * @return 数据源列表
     */
    List<MetadataDatasourceDO> getDatasourceList();

    /**
     * 根据编码获得数据源
     *
     * @param code 编码
     * @return 数据源
     */
    MetadataDatasourceDO getDatasourceByCode(String code);

}
