package com.cmsr.onebase.module.bpm.core.dal.adapt;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.exception.DatabaseAccessErrorCodes;
import com.cmsr.onebase.framework.common.exception.DatabaseAccessException;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.data.base.BaseDO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.*;
import org.anyline.service.AnylineService;
import org.anyline.util.ConfigTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * WarmFlow DataRepository V2 - 使用组合模式复用 DataRepository
 * <p>
 * 通过组合 DataRepository 来提供 WarmFlow 特定的功能，同时保持与现有框架的兼容性
 * 支持实体类的增删改查操作，包含分页、排序、条件查询等功能
 *
 * @author liyang
 * @date 2025-01-27
 */
@Slf4j
public class WarmFlowDataRepositoryV2<T extends BaseDO> {

    static {
        // ConfigTable.GENERATOR.set(PrimaryGenerator.GENERATOR.SNOWFLAKE);
        ConfigTable.IS_AUTO_CHECK_METADATA = true;
        ConfigTable.IS_INSERT_NULL_COLUMN = false;
        ConfigTable.IS_INSERT_NULL_FIELD = false;
        ConfigTable.IS_INSERT_EMPTY_FIELD = true;
        ConfigTable.IS_INSERT_EMPTY_COLUMN = true;

        ConfigTable.IS_UPDATE_NULL_COLUMN = false;
        ConfigTable.IS_UPDATE_NULL_FIELD = false;
        ConfigTable.IS_UPDATE_EMPTY_COLUMN = true;
        ConfigTable.IS_UPDATE_EMPTY_FIELD = true;
    }

    @Resource
    private AnylineService<?> anylineService;

    private Class<T> defaultClazz = null;

    // 组合 DataRepository 实例
    private DataRepository<T> dataRepository;

    public WarmFlowDataRepositoryV2(Class<T> defaultClazz) {
        this.defaultClazz = defaultClazz;
        this.dataRepository = new DataRepository<>(defaultClazz);
    }

    /**
     * 获取实体对应的表名
     *
     * @param clazz 实体类
     * @return 表名
     */
    private String getTableName(Class<? extends BaseDO> clazz) {
        jakarta.persistence.Table annotation = clazz.getAnnotation(jakarta.persistence.Table.class);
        return annotation != null ? annotation.name() : clazz.getSimpleName().toLowerCase();
    }

    // ==================== 委托给 DataRepository 的方法 ====================

    /**
     * 保存实体（插入）
     *
     * @param entity 要保存的实体
     * @return 保存后的实体
     * @throws DatabaseAccessException 插入失败时抛出
     */
    public T insert(T entity) {
        return dataRepository.insert(entity);
    }

    /**
     * 批量插入实体
     *
     * @param entities 实体列表
     * @return 保存后的实体列表
     * @throws DatabaseAccessException 插入失败时抛出
     */
    public List<T> insertBatch(List<T> entities) {
        return dataRepository.insertBatch(entities);
    }

    /**
     * 更新实体
     *
     * @param entity 要更新的实体
     * @return 更新后的实体
     * @throws DatabaseAccessException 更新失败时抛出
     */
    public T update(T entity) {
        dataRepository.update(entity);
        return entity;
    }

    /**
     * 批量更新实体
     *
     * @param entities 实体列表
     * @return 更新后的实体列表
     * @throws DatabaseAccessException 更新失败时抛出
     */
    public List<T> updateBatch(List<T> entities) {
        return dataRepository.upsertBatch(entities);
    }

    /**
     * 根据ID删除实体
     *
     * @param id 实体ID
     * @return 删除数量
     * @throws DatabaseAccessException 删除失败时抛出
     */
    public long deleteById(Long id) {
        return dataRepository.deleteById(id);
    }

    /**
     * 根据ID列表批量删除实体
     *
     * @param ids ID列表
     * @return 删除数量
     * @throws DatabaseAccessException 删除失败时抛出
     */
    public long deleteByIds(Collection<Long> ids) {
        return dataRepository.deleteByIds(new ArrayList<>(ids));
    }

    /**
     * 根据ID查找实体
     *
     * @param id 实体ID
     * @return 实体
     * @throws DatabaseAccessException 查询失败时抛出
     */
    public T findById(Long id) {
        return dataRepository.findById(id);
    }

    /**
     * 根据ID查找实体（返回Optional）
     *
     * @param id 实体ID
     * @return Optional包装的实体对象
     */
    public Optional<T> findByIdOptional(Long id) {
        return dataRepository.findByIdOptional(id);
    }

    /**
     * 检查实体是否存在
     *
     * @param id 实体ID
     * @return 是否存在
     */
    public boolean existsById(Long id) {
        return dataRepository.existsById(id);
    }

    /**
     * 根据ID列表查找实体
     *
     * @param ids ID列表
     * @return 实体列表
     * @throws DatabaseAccessException 查询失败时抛出
     */
    public List<T> findAllByIds(Collection<Long> ids) {
        return dataRepository.findAllByIds(ids);
    }

    /**
     * 自定义查找实体列表
     *
     * @param configs 查询条件
     * @return 实体列表
     * @throws DatabaseAccessException 查询失败时抛出
     */
    public List<T> findAllByConfig(ConfigStore configs) {
        return dataRepository.findAllByConfig(configs);
    }

    /**
     * 条件查询单个实体
     *
     * @param configs 查询条件
     * @return 实体对象，如果不存在返回null
     * @throws DatabaseAccessException 查询失败时抛出
     */
    public T findOne(ConfigStore configs) {
        return dataRepository.findOne(configs);
    }

    /**
     * 条件查询单个实体（返回Optional）
     *
     * @param configs 查询条件
     * @return Optional包装的实体对象
     */
    public Optional<T> findOneOptional(ConfigStore configs) {
        return dataRepository.findOneOptional(configs);
    }

    /**
     * 统计实体数量
     *
     * @param configs 查询条件
     * @return 实体数量
     * @throws DatabaseAccessException 查询失败时抛出
     */
    public long countByConfig(ConfigStore configs) {
        return dataRepository.countByConfig(configs);
    }

    /**
     * 分页查询实体
     *
     * @param configs 查询条件
     * @param page    页码（从1开始）
     * @param size    每页大小
     * @return 分页结果
     * @throws DatabaseAccessException 查询失败时抛出
     */
    public PageResult<T> findPage(ConfigStore configs, Integer page, Integer size) {
        return dataRepository.findPageWithConditions(configs, page, size);
    }

    // ==================== WarmFlow 特定的扩展方法 ====================

    /**
     * WarmFlow 特定的条件更新方法
     * 支持更复杂的更新逻辑
     *
     * @param dataRow 更新数据
     * @param configs 更新条件
     * @return 更新数量
     * @throws DatabaseAccessException 更新失败时抛出
     */
    public long updateByConfig(DataRow dataRow, ConfigStore configs) {
        try {
            long result = anylineService.update(1000, getTableName(defaultClazz), dataRow, configs);
            log.debug("WarmFlow updateByConfig class={}, effect rows = {}", defaultClazz, result);
            return result;
        } catch (Exception e) {
            log.error("WarmFlow updateByConfig error, class={}, configs={}", defaultClazz, configs, e);
            throw new DatabaseAccessException(DatabaseAccessErrorCodes.DB_UPDATE_ERROR, e);
        }
    }

    /**
     * WarmFlow 特定的批量插入方法
     * 支持更高效的批量操作
     *
     * @param entities 实体列表
     * @param batchSize 批次大小
     * @return 插入数量
     * @throws DatabaseAccessException 插入失败时抛出
     */
    public int insertBatchWithSize(List<T> entities, int batchSize) {
        if (entities == null || entities.isEmpty()) {
            return 0;
        }

        int totalInserted = 0;
        for (int i = 0; i < entities.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, entities.size());
            List<T> batch = entities.subList(i, endIndex);
            dataRepository.insertBatch(batch);
            totalInserted += batch.size();
        }

        return totalInserted;
    }

    /**
     * WarmFlow 特定的条件删除方法
     * 支持更复杂的删除逻辑
     *
     * @param configs 删除条件
     * @return 删除数量
     * @throws DatabaseAccessException 删除失败时抛出
     */
    public long deleteByConfig(ConfigStore configs) {
        try {
            long result = anylineService.delete(getTableName(defaultClazz), configs);
            log.debug("WarmFlow deleteByConfig class={}, effect rows = {}", defaultClazz, result);
            return result;
        } catch (Exception e) {
            log.error("WarmFlow deleteByConfig error, class={}, configs={}", defaultClazz, configs, e);
            throw new DatabaseAccessException(DatabaseAccessErrorCodes.DB_DELETE_ERROR, e);
        }
    }

    /**
     * WarmFlow 特定的统计方法
     * 支持更复杂的统计逻辑
     *
     * @param configs 查询条件
     * @param groupBy 分组字段
     * @return 统计结果
     * @throws DatabaseAccessException 查询失败时抛出
     */
    public List<DataRow> countByConfigWithGroup(ConfigStore configs, String groupBy) {
        try {
            String tableName = getTableName(defaultClazz);
            DataSet dataSet = anylineService.querys(tableName, configs);
            return dataSet.getRows();
        } catch (Exception e) {
            log.error("WarmFlow countByConfigWithGroup error, class={}, configs={}", defaultClazz.getSimpleName(), configs, e);
            throw new DatabaseAccessException(DatabaseAccessErrorCodes.DB_SELECT_ERROR, e);
        }
    }

    /**
     * WarmFlow 特定的分页查询方法
     * 支持更灵活的分页参数
     *
     * @param configs 查询条件
     * @param page    页码（从1开始）
     * @param size    每页大小
     * @param orderBy 排序字段
     * @param asc     是否升序
     * @return 分页结果
     * @throws DatabaseAccessException 查询失败时抛出
     */
    public PageResult<T> findPageWithOrder(ConfigStore configs, Integer page, Integer size, String orderBy, boolean asc) {
        try {
            if (orderBy != null && !orderBy.isEmpty()) {
                configs.order(orderBy, asc);
            }
            return dataRepository.findPageWithConditions(configs, page, size);
        } catch (Exception e) {
            log.error("WarmFlow findPageWithOrder error, class={}, configs={}", defaultClazz.getSimpleName(), configs, e);
            throw new DatabaseAccessException(DatabaseAccessErrorCodes.DB_SELECT_ERROR, e);
        }
    }

    /**
     * WarmFlow 特定的存在性检查方法
     * 支持更复杂的条件检查
     *
     * @param configs 查询条件
     * @return 是否存在
     * @throws DatabaseAccessException 查询失败时抛出
     */
    public boolean existsByConfig(ConfigStore configs) {
        try {
            String tableName = getTableName(defaultClazz);
            DataSet dataSet = anylineService.querys(tableName, configs);
            return dataSet != null && dataSet.size() > 0;
        } catch (Exception e) {
            log.error("WarmFlow existsByConfig error, class={}, configs={}", defaultClazz.getSimpleName(), configs, e);
            throw new DatabaseAccessException(DatabaseAccessErrorCodes.DB_SELECT_ERROR, e);
        }
    }

    // ==================== 工具方法 ====================

    /**
     * 获取实体类
     *
     * @return 实体类
     */
    public Class<T> getEntityClass() {
        return defaultClazz;
    }

    /**
     * 获取表名
     *
     * @return 表名
     */
    public String getTableName() {
        return getTableName(defaultClazz);
    }

    /**
     * 创建默认查询条件
     *
     * @return 默认查询条件
     */
    public DefaultConfigStore createDefaultConfig() {
        return new DefaultConfigStore();
    }

    /**
     * 创建查询条件构建器
     *
     * @return 查询条件构建器
     */
    public DefaultConfigStore createConfigBuilder() {
        return new DefaultConfigStore();
    }
}
