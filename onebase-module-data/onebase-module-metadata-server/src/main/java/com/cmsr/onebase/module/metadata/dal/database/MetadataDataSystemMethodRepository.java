package com.cmsr.onebase.module.metadata.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.module.metadata.dal.dataobject.method.MetadataDataSystemMethodDO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 元数据数据系统方法仓储类
 * <p>
 * 提供数据系统方法相关的数据库操作接口，继承自DataRepositoryNew获得基础的CRUD能力
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
@Slf4j
public class MetadataDataSystemMethodRepository extends DataRepository<MetadataDataSystemMethodDO> {

    /**
     * 构造方法，指定默认实体类
     */
    public MetadataDataSystemMethodRepository() {
        super(MetadataDataSystemMethodDO.class);
    }

    /**
     * 获取启用的数据方法列表
     *
     * @return 数据方法列表
     */
    public List<MetadataDataSystemMethodDO> getEnabledDataMethodList() {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("is_enabled", CommonStatusEnum.ENABLE.getStatus());
        configStore.order("create_time", org.anyline.entity.Order.TYPE.DESC);
        return findAllByConfig(configStore);
    }

    /**
     * 根据方法编码获取数据方法
     *
     * @param methodCode 方法编码
     * @return 数据方法对象
     */
    public MetadataDataSystemMethodDO getDataMethodByCode(String methodCode) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("method_code", methodCode);
        configStore.and("is_enabled", CommonStatusEnum.ENABLE.getStatus());
        return findOne(configStore);
    }

    /**
     * 根据方法类型获取数据方法列表
     *
     * @param methodType 方法类型
     * @return 数据方法列表
     */
    public List<MetadataDataSystemMethodDO> getDataMethodListByType(String methodType) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("method_type", methodType);
        configStore.and("is_enabled", CommonStatusEnum.ENABLE.getStatus());
        configStore.order("create_time", org.anyline.entity.Order.TYPE.DESC);
        return findAllByConfig(configStore);
    }

    /**
     * 获取所有数据方法列表（不过滤启用状态）
     *
     * @return 数据方法列表
     */
    public List<MetadataDataSystemMethodDO> getAllDataMethodList() {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.order("create_time", org.anyline.entity.Order.TYPE.DESC);
        return findAllByConfig(configStore);
    }
}
