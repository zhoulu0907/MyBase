package com.cmsr.onebase.module.metadata.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepositoryNew;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.FieldTypeMappingDO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 字段类型映射仓储类
 * <p>
 * 提供字段类型映射相关的数据库操作接口，继承自DataRepository获得基础的CRUD能力
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
@Slf4j
public class FieldTypeMappingRepository extends DataRepositoryNew<FieldTypeMappingDO> {

    /**
     * 构造方法，指定默认实体类
     */
    public FieldTypeMappingRepository() {
        super(FieldTypeMappingDO.class);
    }

    /**
     * 根据数据库类型获取字段类型映射列表
     *
     * @param databaseType 数据库类型
     * @return 字段类型映射列表
     */
    public List<FieldTypeMappingDO> getFieldTypeMappingsByDatabaseType(String databaseType) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.eq(FieldTypeMappingDO.DATABASE_TYPE, databaseType);
        configStore.order("create_time", org.anyline.entity.Order.TYPE.DESC);
        return findAllByConfig(configStore);
    }

    /**
     * 根据Java类型获取字段类型映射列表
     *
     * @param javaType Java类型
     * @return 字段类型映射列表
     */
    public List<FieldTypeMappingDO> getFieldTypeMappingsByJavaType(String javaType) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.eq(FieldTypeMappingDO.BUSINESS_FIELD_TYPE, javaType);
        configStore.order("create_time", org.anyline.entity.Order.TYPE.DESC);
        return findAllByConfig(configStore);
    }

    /**
     * 根据数据库类型和数据库字段类型获取映射
     *
     * @param databaseType 数据库类型
     * @param dbFieldType 数据库字段类型
     * @return 字段类型映射对象
     */
    public FieldTypeMappingDO getFieldTypeMappingByDbType(String databaseType, String dbFieldType) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.eq(FieldTypeMappingDO.DATABASE_TYPE, databaseType);
        configStore.eq(FieldTypeMappingDO.DATABASE_FIELD, dbFieldType);
        return findOne(configStore);
    }

    /**
     * 获取所有字段类型映射列表
     *
     * @return 字段类型映射列表
     */
    public List<FieldTypeMappingDO> getAllFieldTypeMappings() {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.order(FieldTypeMappingDO.DATABASE_TYPE, org.anyline.entity.Order.TYPE.ASC);
        configStore.order("create_time", org.anyline.entity.Order.TYPE.DESC);
        return findAllByConfig(configStore);
    }
}
