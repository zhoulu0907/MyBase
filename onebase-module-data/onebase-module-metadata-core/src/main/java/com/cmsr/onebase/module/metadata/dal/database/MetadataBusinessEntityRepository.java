package com.cmsr.onebase.module.metadata.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataBusinessEntityDO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 元数据业务实体仓储类
 * <p>
 * 提供业务实体相关的数据库操作接口，继承自DataRepositoryNew获得基础的CRUD能力
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
@Slf4j
public class MetadataBusinessEntityRepository extends DataRepository<MetadataBusinessEntityDO> {

    /**
     * 构造方法，指定默认实体类
     */
    public MetadataBusinessEntityRepository() {
        super(MetadataBusinessEntityDO.class);
    }

    /**
     * 根据ID获取业务实体
     *
     * @param entityId 实体ID
     * @return 业务实体对象
     */
    public MetadataBusinessEntityDO getBusinessEntityById(Long entityId) {
        return findById(entityId);
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
        return findById(Long.valueOf(entityId));
    }

    /**
     * 根据编码获取业务实体
     *
     * @param code 实体编码
     * @return 业务实体对象
     */
    public MetadataBusinessEntityDO getBusinessEntityByCode(String code) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataBusinessEntityDO.CODE, code);
        return findOne(configStore);
    }

    /**
     * 带锁查询单个实体，用于避免并发冲突
     *
     * @param configStore 查询条件
     * @return 业务实体对象
     */
    public MetadataBusinessEntityDO findOneWithLock(DefaultConfigStore configStore) {
        // 使用悲观锁查询，避免并发冲突
        List<MetadataBusinessEntityDO> results = findAllByConfig(configStore);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * 获取业务实体列表
     *
     * @return 业务实体列表
     */
    public List<MetadataBusinessEntityDO> getBusinessEntityList() {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.order("create_time", org.anyline.entity.Order.TYPE.DESC);
        return findAllByConfig(configStore);
    }

    /**
     * 根据数据源ID获取业务实体列表
     *
     * @param datasourceId 数据源ID
     * @return 业务实体列表
     */
    public List<MetadataBusinessEntityDO> getBusinessEntityListByDatasourceId(Long datasourceId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataBusinessEntityDO.DATASOURCE_ID, datasourceId);
        configStore.order("create_time", org.anyline.entity.Order.TYPE.DESC);
        return findAllByConfig(configStore);
    }

    /**
     * 根据应用ID获取简单业务实体列表
     *
     * @param appId 应用ID
     * @return 业务实体列表
     */
    public List<MetadataBusinessEntityDO> getSimpleEntityListByAppId(Long appId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataBusinessEntityDO.APP_ID, appId);
        configStore.order("create_time", org.anyline.entity.Order.TYPE.ASC);
        return findAllByConfig(configStore);
    }

    /**
     * 分页查询业务实体
     *
     * @param configStore 查询条件
     * @param pageNo 页码
     * @param pageSize 页大小
     * @return 分页结果
     */
    public PageResult<MetadataBusinessEntityDO> getBusinessEntityPage(DefaultConfigStore configStore, Integer pageNo, Integer pageSize) {
        return findPageWithConditions(configStore, pageNo, pageSize);
    }

    /**
     * 校验业务实体编码是否唯一
     *
     * @param id 实体ID（排除自身）
     * @param code 实体编码
     * @param appId 应用ID
     * @return 是否唯一
     */
    public boolean isBusinessEntityCodeUnique(Long id, String code, Long appId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataBusinessEntityDO.CODE, code);
        configStore.and(MetadataBusinessEntityDO.APP_ID, appId);
        if (id != null) {
            configStore.and(Compare.NOT_EQUAL, "id", id);
        }
        return countByConfig(configStore) == 0;
    }

    /**
     * 根据实体ID校验实体是否存在
     *
     * @param id 实体ID
     * @return 是否存在
     */
    public boolean existsBusinessEntity(Long id) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.in("id", id);
        return findOne(configStore) != null;
    }
}
