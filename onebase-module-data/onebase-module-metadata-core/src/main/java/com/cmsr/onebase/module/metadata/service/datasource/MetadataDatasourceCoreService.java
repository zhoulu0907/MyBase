package com.cmsr.onebase.module.metadata.service.datasource;

import com.cmsr.onebase.module.metadata.dal.dataobject.datasource.MetadataDatasourceDO;
import jakarta.validation.Valid;

/**
 * 数据源核心基础服务接口 - 提供核心业务逻辑
 *
 * @author matianyu
 * @date 2025-09-12
 */
public interface MetadataDatasourceCoreService {

    /**
     * 创建数据源 - 基础方法
     *
     * @param datasource 数据源DO
     * @return 数据源ID
     */
    Long createDatasource(@Valid MetadataDatasourceDO datasource);

    /**
     * 创建默认数据源 - 基础方法
     *
     * @param appId 应用ID
     * @param appUid 应用UID
     * @param datasourceType 数据源类型
     * @param configJson 配置JSON字符串
     * @return 数据源ID
     */
    Long createDefaultDatasource(Long appId, String appUid, String datasourceType, String configJson);

    /**
     * 获取数据源 - 基础方法
     *
     * @param id 数据源ID
     * @return 数据源DO
     */
    MetadataDatasourceDO getDatasource(Long id);

    /**
     * 更新数据源 - 基础方法
     *
     * @param datasource 数据源DO
     */
    void updateDatasource(@Valid MetadataDatasourceDO datasource);

    /**
     * 删除数据源 - 基础方法
     *
     * @param id 数据源ID
     */
    void deleteDatasource(Long id);

    /**
     * 根据编码获取数据源
     *
     * @param code 数据源编码
     * @return 数据源DO
     */
    MetadataDatasourceDO getDatasourceByCode(String code);

    /**
     * 创建应用与数据源的关联关系
     *
     * @param appId 应用ID
     * @param datasourceId 数据源ID
     * @param datasourceType 数据源类型
     * @param appUid 应用UID
     */
    void createAppDatasourceRelation(Long appId, Long datasourceId, String datasourceType, String appUid);
}
