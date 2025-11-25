package com.cmsr.onebase.framework.aynline;

import com.cmsr.onebase.framework.common.exception.DatabaseAccessErrorCodes;
import com.cmsr.onebase.framework.common.exception.DatabaseAccessException;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.framework.data.base.BaseDOInterface;
import jakarta.annotation.Resource;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.*;
import org.anyline.service.AnylineService;
import org.anyline.util.ConfigTable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * AnyLine DataRepository - 基于AnyLine框架的JPA风格的CRUD操作工具类
 * <p>
 * 提供标准的CRUD操作接口，遵循Spring Data JPA的设计模式
 * 支持实体类的增删改查操作，包含分页、排序、条件查询等功能
 *
 * @author matianyu
 * @date 2025-08-07
 */
@Slf4j
public class DataRepository<T extends BaseDOInterface> {

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
    @Setter
    protected AnylineService<?> anylineService;

    private Class<T> defaultClazz = null;

    public DataRepository(Class<T> defaultClazz) {
        this.defaultClazz = defaultClazz;
    }

    public DataRepository() {
    }

    /**
     * 获取实体对应的表名
     *
     * @param clazz 实体类
     * @return 表名
     */
    private String getTableName(Class<? extends BaseDOInterface> clazz) {
        jakarta.persistence.Table annotation = clazz.getAnnotation(jakarta.persistence.Table.class);
        return annotation != null ? annotation.name() : clazz.getSimpleName().toLowerCase();
    }

    // ---------------------------- insert 方法 ----------------------------

    /**
     * 保存实体（插入）
     *
     * @param entity 要保存的实体
     * @return 保存后的实体
     * @throws DatabaseAccessException 插入失败时抛出
     */
    public T insert(T entity) {
        try {
            long result = anylineService.insert(entity);
            if (result == 0) {
                throw new DatabaseAccessException(DatabaseAccessErrorCodes.DB_INSERT_ERROR);
            }
        } catch (Exception e) {
            log.error("insert error, class={}, entity={}", defaultClazz, entity, e);
            throw new DatabaseAccessException(DatabaseAccessErrorCodes.DB_INSERT_ERROR, e);
        }
        return entity;
    }

    /**
     * 批量插入实体
     *
     * @param entities 实体列表
     * @return 保存后的实体列表
     * @throws DatabaseAccessException 插入失败时抛出
     */
    public List<T> insertBatch(List<T> entities) {
        try {
            long result = anylineService.insert(entities);
            if (result == 0) {
                throw new DatabaseAccessException(DatabaseAccessErrorCodes.DB_INSERT_ERROR);
            }
        } catch (Exception e) {
            log.error("insert error, class={}, entity={}", defaultClazz, entities, e);
            throw new DatabaseAccessException(DatabaseAccessErrorCodes.DB_INSERT_ERROR, e);
        }
        return entities;
    }


    // ---------------------------- update 方法 ----------------------------

    /**
     * 保存实体
     *
     * @param entity 要保存的实体
     * @return 保存后的实体
     * @throws DatabaseAccessException 插入失败时抛出
     */
    public T upsert(T entity) {
        try {
            long result = anylineService.upsert(entity);
            log.debug("upsert  ---> class={}, effect rows = {}", entity.getClass().getSimpleName(), result);
        } catch (Exception e) {
            log.error("upsert error, class={}, entity={}", defaultClazz, entity, e);
            throw new DatabaseAccessException(DatabaseAccessErrorCodes.DB_UPDATE_ERROR, e);
        }
        return entity;
    }

    /**
     * 批量保存实体
     *
     * @param entities 实体列表
     * @return 保存后的实体列表
     * @throws DatabaseAccessException 插入失败时抛出
     */
    public List<T> upsertBatch(List<T> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        for (T entity : entities) {
            upsert(entity);
        }
        return entities;
    }
    // ---------------------------- update 方法 ----------------------------

    /**
     * 安全更新，不抛出异常的场景（推荐使用 update）
     * 1. 批量更新操作：部分记录更新失败不影响整体流程
     * 2. 可选更新操作：如用户偏好设置、缓存更新等
     * 3. 幂等性操作：重复执行不会产生副作用的操作
     *
     * @param entity 要保存的实体
     * @return 影响行数
     * @throws DatabaseAccessException 当实体ID为空或更新失败时抛出
     */
    public long update(T entity) {
        if (entity == null || entity.getId() == null || entity.getId() == 0) {
            throw new DatabaseAccessException(DatabaseAccessErrorCodes.DB_ID_NULL);
        }
        try {
            ConfigStore configs = new DefaultConfigStore();
            configs.eq(BaseDO.ID, entity.getId());
            Long result = anylineService.update(entity, configs);
            log.debug("update  ---> class={}, effect rows = {}", entity.getClass().getSimpleName(), result);
            return result;
        } catch (Exception e) {
            log.error("update error, class={}, entity={}", defaultClazz, entity, e);
            throw new DatabaseAccessException(DatabaseAccessErrorCodes.DB_UPDATE_ERROR, e);
        }
    }

    /**
     * 严格更新，严格模式，如果更新失败则抛出异常(推荐使用 updateStrict）
     * 1. 关键业务操作：如订单状态更新、支付状态变更等
     * 2. 并发控制场景：需要确保数据一致性的操作
     * 3. 审计要求严格：需要确保每次更新都有明确结果
     *
     * @param entity 要保存的实体
     * @return 影响行数
     * @throws DatabaseAccessException 更新失败时抛出
     */
    public long updateStrict(T entity) {
        long result = update(entity);
        if (result == 0) {
            throw new DatabaseAccessException(DatabaseAccessErrorCodes.DB_UPDATE_ERROR);
        }
        return result;
    }

    /**
     * 条件更新，安全更新
     *
     * @param configs 更新条件
     * @return 更新数量
     * @throws DatabaseAccessException 当实体ID为空或更新失败时抛出
     */
    public long updateByConfig(DataRow dataRow, ConfigStore configs) {
        try {
            long result = anylineService.update(1000, getTableName(defaultClazz), dataRow, configs);
            log.debug("updateByConfig class={}, effect rows = {}", defaultClazz, result);
            return result;
        } catch (Exception e) {
            log.error("updateByConfig error, class={}, configs={}", defaultClazz, configs, e);
            throw new DatabaseAccessException(DatabaseAccessErrorCodes.DB_UPDATE_ERROR, e);
        }
    }

    // ---------------------------- query/find 方法 ----------------------------

    /**
     * 根据ID查找实体
     *
     * @param id 实体ID
     * @return 实体
     * @throws DatabaseAccessException 查询失败时抛出
     */
    public T findById(Long id) {
        try {
            if (id == null) {
                throw new DatabaseAccessException(DatabaseAccessErrorCodes.DB_ID_NULL);
            }
            ConfigStore configs = new DefaultConfigStore();
            configs.and(Compare.EQUAL, BaseDO.ID, id);
            String tableName = getTableName(defaultClazz);
            return anylineService.select(tableName, defaultClazz, configs);
        } catch (Exception e) {
            log.error("findById error, class={}, id={}", defaultClazz, id, e);
            throw new DatabaseAccessException(DatabaseAccessErrorCodes.DB_SELECT_ERROR, e);
        }
    }

    /**
     * 根据ID查找实体（返回Optional）
     *
     * @param id 实体ID
     * @return Optional包装的实体对象
     */
    public Optional<T> findByIdOptional(Long id) {
        return Optional.ofNullable(findById(id));
    }

    /**
     * 检查实体是否存在
     *
     * @param id 实体ID
     * @return 是否存在
     */
    public boolean existsById(Long id) {
        try {
            if (id == null) {
                throw new DatabaseAccessException(DatabaseAccessErrorCodes.DB_ID_NULL);
            }
            ConfigStore configs = new DefaultConfigStore();
            configs.and(Compare.EQUAL, BaseDO.ID, id);
            String tableName = getTableName(defaultClazz);
            return anylineService.count(tableName, configs) > 0;
        } catch (Exception e) {
            log.error("existsById error, class={}, id={}", defaultClazz, id, e);
            throw new DatabaseAccessException(DatabaseAccessErrorCodes.DB_SELECT_ERROR, e);
        }
    }

    /**
     * 统计实体数量
     *
     * @return 实体数量
     * @throws DatabaseAccessException 查询失败时抛出
     */
    @Deprecated
    public long count() {
        try {
            ConfigStore configs = new DefaultConfigStore();
            String tableName = getTableName(defaultClazz);
            return anylineService.count(tableName, configs);
        } catch (Exception e) {
            log.error("count error, class={}", defaultClazz.getSimpleName(), e);
            throw new DatabaseAccessException(DatabaseAccessErrorCodes.DB_SELECT_ERROR, e);
        }
    }

    /**
     * 统计实体数量
     *
     * @param configs 查询条件
     * @return 实体数量
     * @throws DatabaseAccessException 查询失败时抛出
     */
    public long countByConfig(ConfigStore configs) {
        try {
            String tableName = getTableName(defaultClazz);
            return anylineService.count(tableName, configs);
        } catch (Exception e) {
            log.error("countByConfig error. ---> class={}", defaultClazz.getSimpleName(), e);
            throw new DatabaseAccessException(DatabaseAccessErrorCodes.DB_SELECT_ERROR, e);
        }
    }

    /**
     * 分页查询
     *
     * @param pageIndex 页码（从1开始）
     * @param pageSize  页大小
     * @return 分页结果
     * @throws DatabaseAccessException 查询失败时抛出
     */
    @Deprecated
    public PageResult<T> findAll(int pageIndex, int pageSize) {
        try {
            ConfigStore configs = new DefaultConfigStore();

            PageNavi page = new DefaultPageNavi(pageIndex, pageSize);
            configs.setPageNavi(page);

            String tableName = getTableName(defaultClazz);
            DataSet dataSet = anylineService.querys(tableName, configs);

            return new PageResult<>(
                    dataSet.entitys(defaultClazz).stream().toList(),
                    dataSet.total()
            );
        } catch (Exception e) {
            log.error("findAll error, class={}, pageIndex={}, pageSize={}",
                    defaultClazz.getSimpleName(), pageIndex, pageSize, e);
            throw new DatabaseAccessException(DatabaseAccessErrorCodes.DB_SELECT_ERROR, e);
        }
    }

    /**
     * 查找所有实体
     *
     * @return 实体列表
     * @throws DatabaseAccessException 查询失败时抛出
     */
    @Deprecated
    public List<T> findAll() {
        try {
            ConfigStore configs = new DefaultConfigStore();
            String tableName = getTableName(defaultClazz);
            DataSet dataSet = anylineService.querys(tableName, configs);
            log.debug("findAll --->  dataSet.size = {}", dataSet.size());
            return dataSet.entity(defaultClazz);
        } catch (Exception e) {
            log.error("findAll error, class={}", defaultClazz, e);
            throw new DatabaseAccessException(DatabaseAccessErrorCodes.DB_SELECT_ERROR, e);
        }
    }

    /**
     * 自定义查找实体列表
     *
     * @param configs 查询条件
     * @return 实体列表
     * @throws DatabaseAccessException 查询失败时抛出
     */
    public List<T> findAllByConfig(ConfigStore configs) {
        try {
            String tableName = getTableName(defaultClazz);
            DataSet dataSet = anylineService.querys(tableName, configs);
            return dataSet.entity(defaultClazz);
        } catch (Exception e) {
            log.error("findAllByConfig error, class={}, configs={}", defaultClazz.getSimpleName(), configs, e);
            throw new DatabaseAccessException(DatabaseAccessErrorCodes.DB_SELECT_ERROR, e);
        }
    }

    /**
     * 根据ID列表查找实体
     *
     * @param ids ID列表
     * @return 实体列表
     * @throws DatabaseAccessException 查询失败时抛出
     */
    public List<T> findAllByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            ConfigStore configs = new DefaultConfigStore();
            configs.in(BaseDO.ID, ids);
            String tableName = getTableName(defaultClazz);
            DataSet dataSet = anylineService.querys(tableName, configs);
            return dataSet.entity(defaultClazz);
        } catch (Exception e) {
            log.error("findAllByIds error, class={}, ids={}", defaultClazz.getSimpleName(), ids, e);
            throw new DatabaseAccessException(DatabaseAccessErrorCodes.DB_SELECT_ERROR, e);
        }
    }

    /**
     * 条件查询单个实体
     *
     * @param configs 查询条件
     * @return 实体对象，如果不存在返回null
     * @throws DatabaseAccessException 查询失败时抛出
     */
    public T findOne(ConfigStore configs) {
        try {
            String tableName = getTableName(defaultClazz);
            return anylineService.select(tableName, defaultClazz, configs);
        } catch (Exception e) {
            log.error("findOne error, class={}", defaultClazz.getSimpleName(), e);
            throw new DatabaseAccessException(DatabaseAccessErrorCodes.DB_SELECT_ERROR, e);
        }
    }

    /**
     * 条件查询单个实体（返回Optional）
     *
     * @param configs 查询条件
     * @return Optional包装的实体对象
     */
    public Optional<T> findOneOptional(ConfigStore configs) {
        T entity = findOne(configs);
        return Optional.ofNullable(entity);
    }

    /**
     * 条件分页查询
     *
     * @param configs   查询条件
     * @param pageIndex 页码（从1开始）
     * @param pageSize  页大小
     * @return 分页结果
     * @throws DatabaseAccessException 查询失败时抛出
     */
    public PageResult<T> findPageWithConditions(ConfigStore configs, int pageIndex, int pageSize) {
        try {
            PageNavi page = new DefaultPageNavi(pageIndex, pageSize);
            configs.setPageNavi(page);

            String tableName = getTableName(defaultClazz);
            DataSet dataSet = anylineService.querys(tableName, configs);

            return new PageResult<>(
                    dataSet.entitys(defaultClazz).stream().toList(),
                    dataSet.total()
            );
        } catch (Exception e) {
            log.error("findPageWithConditions error, class={}, pageIndex={}, pageSize={}",
                    defaultClazz.getSimpleName(), pageIndex, pageSize, e);
            throw new DatabaseAccessException(DatabaseAccessErrorCodes.DB_SELECT_ERROR, e);
        }
    }


    // ---------------------------- delete 方法 ----------------------------

    /**
     * 根据条件删除实体（软删除）
     *
     * @param configs 删除条件
     * @return 删除的记录数
     * @throws DatabaseAccessException 删除失败时抛出
     */
    public long deleteByConfig(ConfigStore configs) {
        try {
            DataRow row = new DataRow();
            row.put(BaseDO.DELETED, System.currentTimeMillis());  // 设置逻辑删除标记
            long result = anylineService.update(getTableName(defaultClazz), row, configs);
            log.debug("deleteByConfig  ---> class={}, effect rows = {}", defaultClazz, result);
            return result;
        } catch (Exception e) {
            log.error("deleteByConfig error, class={}, configs={}", defaultClazz.getSimpleName(), configs, e);
            throw new DatabaseAccessException(DatabaseAccessErrorCodes.DB_DELETE_ERROR, e);
        }
    }

    /**
     * 根据ID删除实体（软删除）
     *
     * @param id 实体ID
     * @return 删除的记录数
     * @throws DatabaseAccessException 删除失败时抛出
     */
    public long deleteById(Long id) {
        try {
            if (id == null || id == 0) {
                throw new DatabaseAccessException(DatabaseAccessErrorCodes.DB_ID_NULL);
            }
            ConfigStore configs = new DefaultConfigStore();
            configs.and(Compare.EQUAL, BaseDO.ID, id);
            DataRow row = new DataRow();
            row.put(BaseDO.DELETED, System.currentTimeMillis());  // 设置逻辑删除标记
            long result = anylineService.update(getTableName(defaultClazz), row, configs);
            log.debug("deleteById  ---> class={}, effect rows = {}, id = {}", defaultClazz, result, id);
            return result;
        } catch (Exception e) {
            log.error("deleteById error, class={}, id={}", defaultClazz.getSimpleName(), id, e);
            throw new DatabaseAccessException(DatabaseAccessErrorCodes.DB_DELETE_ERROR, e);
        }
    }

    public long deleteByIds(List<Long> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                return 0;
            }
            ConfigStore configs = new DefaultConfigStore();
            configs.in(BaseDO.ID, ids);
            DataRow row = new DataRow();
            row.put(BaseDO.DELETED, System.currentTimeMillis());
            long result = anylineService.update(getTableName(defaultClazz), row, configs);
            log.debug("deleteByIds  ---> class={}, effect rows = {}, ids = {}", defaultClazz, result, ids);
            return result;
        } catch (Exception e) {
            log.error("deleteByIds error, class={}, ids={}", defaultClazz.getSimpleName(), ids, e);
            throw new DatabaseAccessException(DatabaseAccessErrorCodes.DB_DELETE_ERROR, e);
        }
    }

    /**
     * 删除实体（软删除）
     *
     * @param entity 要删除的实体
     * @return 删除的记录数
     */
    public long delete(T entity) {
        if (entity != null && entity.getId() != null && entity.getId() != 0) {
            return deleteById(entity.getId());
        }
        return 0;
    }

    /**
     * 批量删除实体（软删除）
     *
     * @param entities 实体列表
     */
    public void deleteAll(List<T> entities) {
        for (T entity : entities) {
            delete(entity);
        }
    }

    /**
     * 根据ID列表删除实体（软删除）
     *
     * @param ids ID列表
     * @return 删除的记录数
     * @throws DatabaseAccessException 删除失败时抛出
     */
    @Deprecated
    public long deleteAllById(Collection<Long> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                return 0;
            }
            ConfigStore configs = new DefaultConfigStore();
            configs.and(Compare.IN, BaseDO.ID, ids);
            DataRow row = new DataRow();
            row.put(BaseDO.DELETED, System.currentTimeMillis());  // 设置逻辑删除标记
            long result = anylineService.update(getTableName(defaultClazz), row, configs);
            log.debug("deleteAllById  ---> class={}, effect rows={}, ids={}", defaultClazz, result, ids);
            return result;
        } catch (Exception e) {
            log.error("deleteAllById error, class={}, ids={}", defaultClazz.getSimpleName(), ids, e);
            throw new DatabaseAccessException(DatabaseAccessErrorCodes.DB_DELETE_ERROR, e);
        }
    }

    /**
     * 删除所有实体（软删除）
     *
     * @throws DatabaseAccessException 删除失败时抛出
     */
    public void deleteAll() {
        try {
            ConfigStore configs = new DefaultConfigStore();
            DataRow row = new DataRow();
            row.put(BaseDO.DELETED, System.currentTimeMillis());  // 设置逻辑删除标记
            long result = anylineService.update(getTableName(defaultClazz), row, configs);
            log.debug("deleteAll  ---> class={}, effect rows={}", defaultClazz, result);
        } catch (Exception e) {
            log.error("deleteAll error, class={}", defaultClazz.getSimpleName(), e);
            throw new DatabaseAccessException(DatabaseAccessErrorCodes.DB_DELETE_ERROR, e);
        }
    }

    public DataSet querys(String dest, ConfigStore configs, String... conditions) {
        return anylineService.querys(dest, configs, conditions);
    }

    public DataSet querysPage(String dest, ConfigStore configs, int pageIndex, int pageSize, String... conditions) {
        PageNavi page = new DefaultPageNavi(pageIndex, pageSize);
        configs.setPageNavi(page);
        return anylineService.querys(dest, configs, conditions);
    }

}
