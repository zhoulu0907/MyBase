package com.cmsr.onebase.module.metadata.service.datasource;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.dal.dataobject.datasource.MetadataDatasourceDO;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

/**
 * 数据源 Service 接口 - 核心数据层接口
 * TODO: Controller层应该使用build模块中的DatasourceBuildService，该接口负责VO转换
 *
 * @author matianyu
 * @date 2025-08-20
 */
public interface MetadataDatasourceService {

    /**
     * 创建数据源
     *
     * @param datasource 数据源DO
     * @return 数据源编号
     */
    Long createDatasource(@Valid MetadataDatasourceDO datasource);

    /**
     * 更新数据源
     *
     * @param datasource 数据源DO
     */
    void updateDatasource(@Valid MetadataDatasourceDO datasource);

    /**
     * 删除数据源
     *
     * @param id 数据源编号
     */
    void deleteDatasource(Long id);

    /**
     * 获得数据源
     *
     * @param id 数据源编号
     * @return 数据源DO
     */
    MetadataDatasourceDO getDatasource(Long id);

    /**
     * 获得数据源列表
     *
     * @return 数据源列表
     */
    List<MetadataDatasourceDO> getDatasourceList();

    /**
     * 获得数据源分页
     *
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 数据源分页
     */
    PageResult<MetadataDatasourceDO> getDatasourcePage(int pageNum, int pageSize);

    /**
     * 测试数据源连接
     *
     * @param config 数据源配置
     * @return 连接测试结果
     */
    boolean testConnection(Map<String, Object> config);

    /**
     * 根据数据源ID获取表信息
     *
     * @param datasourceId 数据源ID
     * @param tableName 表名（可选）
     * @return 表信息列表
     */
    List<Map<String, Object>> getTableInfo(Long datasourceId, String tableName);

    /**
     * 根据数据源ID和表名获取列信息
     *
     * @param datasourceId 数据源ID
     * @param tableName 表名
     * @return 列信息列表
     */
    List<Map<String, Object>> getColumnInfo(Long datasourceId, String tableName);

    /**
     * 根据数据源编码获取数据源
     *
     * @param code 数据源编码
     * @return 数据源DO
     */
    MetadataDatasourceDO getDatasourceByCode(String code);

    // TODO: 以下方法需要在build模块中实现，涉及VO转换
    /*
    List<DatasourceTypeRespVO> getDatasourceTypes();
    List<TableInfoRespVO> getTableInfo(TableQueryVO queryVO);
    List<ColumnInfoRespVO> getColumnInfo(ColumnQueryVO queryVO);
    DatasourceTestConnectionRespVO testConnection(DatasourceTestConnectionReqVO reqVO);
    PageResult<DatasourceRespVO> getDatasourcePage(DatasourcePageReqVO pageReqVO);
    DatasourceRespVO createDatasourceWithResponse(@Valid DatasourceSaveReqVO reqVO);
    DatasourceRespVO getDatasourceDetail(Long id);
    */
}