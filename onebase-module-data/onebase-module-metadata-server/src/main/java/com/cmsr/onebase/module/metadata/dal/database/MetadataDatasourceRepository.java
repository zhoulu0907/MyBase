package com.cmsr.onebase.module.metadata.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepositoryNew;
import com.cmsr.onebase.module.metadata.dal.dataobject.datasource.MetadataDatasourceDO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 元数据数据源仓储类
 * <p>
 * 提供数据源相关的数据库操作接口，继承自DataRepositoryNew获得基础的CRUD能力
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
@Slf4j
public class MetadataDatasourceRepository extends DataRepositoryNew<MetadataDatasourceDO> {

    /**
     * 构造方法，指定默认实体类
     */
    public MetadataDatasourceRepository() {
        super(MetadataDatasourceDO.class);
    }

    /**
     * 根据ID获取数据源
     *
     * @param datasourceId 数据源ID（字符串格式）
     * @return 数据源对象
     */
    public MetadataDatasourceDO getDatasourceById(String datasourceId) {
        if (datasourceId == null || datasourceId.trim().isEmpty()) {
            return null;
        }

        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("id", Long.valueOf(datasourceId));
        return findOne(configStore);
    }

    /**
     * 根据ID获取数据源
     *
     * @param datasourceId 数据源ID
     * @return 数据源对象
     */
    public MetadataDatasourceDO getDatasourceById(Long datasourceId) {
        return findById(datasourceId);
    }

    /**
     * 根据编码获取数据源
     *
     * @param code 数据源编码
     * @return 数据源对象
     */
    public MetadataDatasourceDO getDatasourceByCode(String code) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("code", code);
        return findOne(configStore);
    }

    /**
     * 获取数据源列表
     *
     * @return 数据源列表
     */
    public List<MetadataDatasourceDO> getDatasourceList() {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.order("create_time", org.anyline.entity.Order.TYPE.DESC);
        return findAllByConfig(configStore);
    }

    /**
     * 根据应用ID获取数据源列表
     *
     * @param appId 应用ID
     * @return 数据源列表
     */
    public List<MetadataDatasourceDO> getDatasourceListByAppId(Long appId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("app_id", appId);
        configStore.order("create_time", org.anyline.entity.Order.TYPE.DESC);
        return findAllByConfig(configStore);
    }

    /**
     * 校验数据源编码是否唯一
     *
     * @param id 数据源ID（排除自身）
     * @param code 数据源编码
     * @param appId 应用ID
     * @return 是否唯一
     */
    public boolean isDatasourceCodeUnique(Long id, String code, Long appId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("code", code);
        configStore.and("app_id", appId);
        if (id != null) {
            configStore.and(org.anyline.entity.Compare.NOT_EQUAL, "id", id);
        }
        return countByConfig(configStore) == 0;
    }
}
