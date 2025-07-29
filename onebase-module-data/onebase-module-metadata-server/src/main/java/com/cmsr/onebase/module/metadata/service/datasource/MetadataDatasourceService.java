package com.cmsr.onebase.module.metadata.service.datasource;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.controller.admin.datasource.vo.ColumnInfoRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.datasource.vo.DatasourcePageReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.datasource.vo.DatasourceSaveReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.datasource.vo.DatasourceTestConnectionReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.datasource.vo.DatasourceTestConnectionRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.datasource.vo.DatasourceTypeRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.datasource.vo.TableInfoRespVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.datasource.MetadataDatasourceDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 数据源 Service 接口
 */
public interface MetadataDatasourceService {

    /**
     * 获取所有支持的数据源类型
     *
     * @return 数据源类型列表
     */
    List<DatasourceTypeRespVO> getDatasourceTypes();

    /**
     * 根据数据源ID查询表名列表
     *
     * @param datasourceId 数据源ID
     * @param schemaName 数据库模式名(可选)
     * @param keyword 表名搜索关键词(可选)
     * @return 表信息列表
     */
    List<TableInfoRespVO> getTablesByDatasourceId(Long datasourceId, String schemaName, String keyword);

    /**
     * 根据表名查询字段信息
     *
     * @param datasourceId 数据源ID
     * @param tableName 表名
     * @param schemaName 数据库模式名(可选)
     * @return 字段信息列表
     */
    List<ColumnInfoRespVO> getColumnsByTableName(Long datasourceId, String tableName, String schemaName);

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

    /**
     * 测试数据源连接
     *
     * @param reqVO 测试连接请求
     * @return 测试结果
     */
    DatasourceTestConnectionRespVO testConnection(@Valid DatasourceTestConnectionReqVO reqVO);

}
