package com.cmsr.onebase.module.metadata.build.service.datasource;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.build.controller.admin.datasource.vo.ColumnInfoRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.datasource.vo.DatasourcePageReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.datasource.vo.DatasourceRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.datasource.vo.DatasourceSaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.datasource.vo.DatasourceTestConnectionReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.datasource.vo.DatasourceTestConnectionRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.datasource.vo.DatasourceTypeRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.datasource.vo.TableInfoRespVO;
import com.cmsr.onebase.module.metadata.build.service.datasource.vo.ColumnQueryVO;
import com.cmsr.onebase.module.metadata.build.service.datasource.vo.TableQueryVO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 数据源构建模块服务接口 - 提供面向VO的业务操作
 * 独立接口，不继承核心模块接口，避免Bean冲突
 *
 * @author matianyu
 * @date 2025-09-12
 */
public interface MetadataDatasourceBuildService {

    /**
     * 获取所有支持的数据源类型
     *
     * @return 数据源类型列表
     */
    List<DatasourceTypeRespVO> getDatasourceTypes();

    /**
     * 根据数据源ID查询表名列表
     *
     * @param queryVO 查询条件VO
     * @return 表信息列表
     */
    List<TableInfoRespVO> getTablesByDatasourceId(TableQueryVO queryVO);

    /**
     * 根据表名查询字段信息
     *
     * @param queryVO 查询条件VO
     * @return 字段信息列表
     */
    List<ColumnInfoRespVO> getColumnsByTableName(ColumnQueryVO queryVO);

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
     * 根据应用ID获得数据源列表
     *
     * @param appId 应用ID
     * @return 数据源列表
     */
    List<MetadataDatasourceDO> getDatasourceListByAppId(Long appId);

    /**
     * 将数据源数据对象转换为响应列表，并填充创建人、更新人名称
     *
     * @param datasourceList 数据源列表
     * @return 数据源响应列表
     */
    List<DatasourceRespVO> buildDatasourceRespVOList(List<MetadataDatasourceDO> datasourceList);

    /**
     * 将单个数据源数据对象转换为响应VO，并填充创建人、更新人名称
     *
     * @param datasource 数据源数据对象
     * @return 数据源响应VO
     */
    DatasourceRespVO buildDatasourceRespVO(MetadataDatasourceDO datasource);

    /**
     * 测试数据源连接
     *
     * @param reqVO 测试连接请求
     * @return 测试结果
     */
    DatasourceTestConnectionRespVO testConnection(@Valid DatasourceTestConnectionReqVO reqVO);

    /**
     * 根据编码获得数据源
     *
     * @param code 编码
     * @return 数据源
     */
    MetadataDatasourceDO getDatasourceByCode(String code);

    /**
     * 根据UUID获得数据源
     *
     * @param datasourceUuid 数据源UUID
     * @return 数据源
     */
    MetadataDatasourceDO getDatasourceByUuid(String datasourceUuid);

    /**
     * 创建默认数据源，使用配置文件中 default.datasource 配置
     *
     * @param appId 应用ID
     * @param appUid 应用唯一UID，用于建立应用与数据源的关联
     * @return 数据源编号
     */
    Long createDefaultDatasource(Long appId, String appUid);

}
