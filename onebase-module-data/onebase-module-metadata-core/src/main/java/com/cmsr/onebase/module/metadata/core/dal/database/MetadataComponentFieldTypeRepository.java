package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataComponentFieldTypeDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 元数据组件字段类型 Repository
 *
 * @author matianyu
 * @date 2025-09-12
 */
@Repository
public class MetadataComponentFieldTypeRepository extends DataRepository<MetadataComponentFieldTypeDO> {

    /**
     * 构造方法，指定默认实体类
     */
    public MetadataComponentFieldTypeRepository() {
        super(MetadataComponentFieldTypeDO.class);
    }

    /**
     * 根据字段类型编码查询字段类型信息
     *
     * @param fieldTypeCode 字段类型编码
     * @return 字段类型DO
     */
    public MetadataComponentFieldTypeDO findByFieldTypeCode(String fieldTypeCode) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataComponentFieldTypeDO.FIELD_TYPE_CODE, fieldTypeCode);
        configStore.and(MetadataComponentFieldTypeDO.STATUS, 1); // 只查询启用状态的
        return findOne(configStore);
    }

    /**
     * 查询所有启用的字段类型
     *
     * @return 字段类型列表
     */
    public List<MetadataComponentFieldTypeDO> findAllEnabled() {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataComponentFieldTypeDO.STATUS, 1); // 只查询启用状态的
        configStore.order(MetadataComponentFieldTypeDO.SORT_ORDER, Order.TYPE.ASC);
        return findAllByConfig(configStore);
    }
}
