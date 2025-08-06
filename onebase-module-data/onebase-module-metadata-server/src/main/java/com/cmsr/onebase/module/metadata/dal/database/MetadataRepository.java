package com.cmsr.onebase.module.metadata.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.metadata.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataSystemFieldsDO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 元数据仓储类
 * <p>
 * 提供元数据相关的数据库操作接口，继承自DataRepository获得基础的CRUD能力
 *
 * @author matianyu
 * @date 2025-08-05
 */
@Component
@Slf4j
public class MetadataRepository extends DataRepository {

    // ==================== 数据源特定操作方法 ====================

    /**
     * 根据ID获取数据源
     *
     * @param datasourceId 数据源ID
     * @return 数据源对象
     */
    public MetadataDatasourceDO getDatasourceById(String datasourceId) {
        if (datasourceId == null || datasourceId.trim().isEmpty()) {
            return null;
        }

        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("id", Long.valueOf(datasourceId));
        return findOne(MetadataDatasourceDO.class, configStore);
    }

    /**
     * 根据ID获取数据源
     *
     * @param datasourceId 数据源ID
     * @return 数据源对象
     */
    public MetadataDatasourceDO getDatasourceById(Long datasourceId) {
        return findById(MetadataDatasourceDO.class, datasourceId);
    }

    /**
     * 获取系统字段列表
     *
     * @return 系统字段列表
     */
    public List<MetadataSystemFieldsDO> getSystemFields() {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("is_system_field", true);
        return findAllByConfig(MetadataSystemFieldsDO.class, configStore);
    }

    /**
     * 根据ID获取业务实体
     *
     * @param entityId 实体ID
     * @return 业务实体对象
     */
    public MetadataBusinessEntityDO getBusinessEntityById(Long entityId) {
        return findById(MetadataBusinessEntityDO.class, entityId);
    }

    /**
     * 根据ID获取业务实体
     *
     * @param entityId 实体ID（字符串格式）
     * @return 业务实体对象
     */
    public MetadataBusinessEntityDO getBusinessEntityById(String entityId) {
        if (entityId == null || entityId.trim().isEmpty()) {
            return null;
        }
        return findById(MetadataBusinessEntityDO.class, Long.valueOf(entityId));
    }
}
