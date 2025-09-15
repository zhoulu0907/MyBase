package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataSystemFieldsDO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 元数据系统字段仓储类
 * <p>
 * 提供系统字段相关的数据库操作接口，继承自DataRepositoryNew获得基础的CRUD能力
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
@Slf4j
public class MetadataSystemFieldsRepository extends DataRepository<MetadataSystemFieldsDO> {

    /**
     * 构造方法，指定默认实体类
     */
    public MetadataSystemFieldsRepository() {
        super(MetadataSystemFieldsDO.class);
    }

    /**
     * 获取系统字段列表
     *
     * @return 系统字段列表
     */
    public List<MetadataSystemFieldsDO> getSystemFields() {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("is_system_field", true);
        configStore.and(MetadataSystemFieldsDO.IS_ENABLED, CommonStatusEnum.ENABLE.getStatus());
        return findAllByConfig(configStore);
    }

    /**
     * 获取所有系统字段列表（不过滤启用状态）
     *
     * @return 系统字段列表
     */
    public List<MetadataSystemFieldsDO> getAllSystemFields() {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("is_system_field", true);
        configStore.order("create_time", org.anyline.entity.Order.TYPE.DESC);
        return findAllByConfig(configStore);
    }

    /**
     * 根据字段名获取系统字段
     *
     * @param fieldName 字段名
     * @return 系统字段对象
     */
    public MetadataSystemFieldsDO getSystemFieldByName(String fieldName) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataSystemFieldsDO.FIELD_NAME, fieldName);
        configStore.and("is_system_field", true);
        return findOne(configStore);
    }

    /**
     * 根据字段编码获取系统字段
     *
     * @param fieldCode 字段编码
     * @return 系统字段对象
     */
    public MetadataSystemFieldsDO getSystemFieldByCode(String fieldCode) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("field_code", fieldCode);
        configStore.and("is_system_field", true);
        return findOne(configStore);
    }
}
