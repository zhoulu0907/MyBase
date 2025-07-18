package com.cmsr.onebase.framework.aynline;

import com.cmsr.onebase.framework.common.anyline.entity.BaseDO;
import com.cmsr.onebase.framework.common.anyline.utils.JpaUtils;
import com.cmsr.onebase.framework.common.anyline.web.BizException;
import com.cmsr.onebase.framework.common.anyline.web.PageResult;
import com.cmsr.onebase.framework.common.anyline.web.StatusCode;
import com.cmsr.onebase.framework.common.util.snowflake.SnowflakeId;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.Run;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.*;
import org.anyline.metadata.Constraint;
import org.anyline.metadata.Table;
import org.anyline.service.AnylineService;
import org.anyline.util.ConfigTable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Collections;

/**
 * DataRepository - JPA风格的CRUD操作工具类
 * <p>
 * 提供标准的CRUD操作接口，遵循Spring Data JPA的设计模式
 * 支持实体类的增删改查操作，包含分页、排序、条件查询等功能
 *
 * @author mickey
 */
@Slf4j
public class DataRepository {
    private final AnylineService<?> service;

    public DataRepository(AnylineService<?> service) {
        ConfigTable.IS_AUTO_CHECK_METADATA = true;
        ConfigTable.IS_INSERT_NULL_COLUMN = false;
        ConfigTable.IS_INSERT_NULL_FIELD = false;
        ConfigTable.IS_INSERT_EMPTY_FIELD = false;
        ConfigTable.IS_INSERT_EMPTY_COLUMN = false;
        this.service = service;
        if (service == null) {
            throw new IllegalArgumentException("AnylineService cannot be null");
        }
    }

    /**
     * 获取实体对应的表名
     *
     * @param clazz 实体类
     * @return 表名
     */
    private String getTableName(Class<?> clazz) {
        String tableName = JpaUtils.getTableName(clazz);
        return tableName != null ? tableName : clazz.getSimpleName().toLowerCase();
    }

    /**
     * 保存实体（插入或更新）
     *
     * @param entity 要保存的实体
     * @param <T>    实体类型
     * @return 保存后的实体
     */
    public <T extends com.cmsr.onebase.framework.mybatis.core.dataobject.BaseDO> T saveNew(T entity) {
        try {
            // 新增
            entity.setCreateTime(LocalDateTime.now());
            System.out.println("查看entity.getId():"+entity.getId());
            
            // 设置Anyline配置，避免类型转换问题
            ConfigTable.IS_AUTO_CHECK_METADATA = false;
            ConfigTable.IS_INSERT_NULL_COLUMN = true;
            ConfigTable.IS_INSERT_NULL_FIELD = true;
            ConfigTable.IS_INSERT_EMPTY_FIELD = true;
            ConfigTable.IS_INSERT_EMPTY_COLUMN = true;
            
            Long result = service.insert(entity);
            if (result == 0) {
                throw new BizException(StatusCode.DB_INSERT_ERROR);
            }
            
            return entity;
        } catch (Exception e) {
            log.error("保存实体失败: {}", entity.getClass().getSimpleName(), e);
            throw new BizException(StatusCode.DB_INSERT_ERROR);
        }
    }

    /**
     * 保存实体（插入或更新）
     *
     * @param entity 要保存的实体
     * @param <T>    实体类型
     * @return 保存后的实体
     */
    public <T extends BaseDO> T save(T entity) {
        try {
            if (entity.getId() == null || entity.getId() == 0) {
                // 新增
                entity.setCreatedTime(LocalDateTime.now());
                entity.setId(SnowflakeId.nextId());
                Long result = service.insert(entity);
                if (result == 0) {
                    throw new BizException(StatusCode.DB_INSERT_ERROR);
                }
            } else {
                // 更新
                entity.setUpdatedTime(LocalDateTime.now());
                Long result = service.update(entity);
                if (result == 0) {
                    throw new BizException(StatusCode.DB_UPDATE_ERROR);
                }
            }
            return entity;
        } catch (Exception e) {
            log.error("保存实体失败: {}", entity.getClass().getSimpleName(), e);
            throw new BizException(StatusCode.DB_INSERT_ERROR);
        }
    }

    /**
     * 批量保存实体
     *
     * @param entities 实体列表
     * @param <T>      实体类型
     * @return 保存后的实体列表
     */
    public <T extends BaseDO> List<T> saveAll(List<T> entities) {
        try {
            for (T entity : entities) {
                save(entity);
            }
            return entities;
        } catch (Exception e) {
            log.error("批量保存实体失败", e);
            throw new BizException(StatusCode.DB_INSERT_ERROR);
        }
    }

    /**
     * 根据ID查找实体
     *
     * @param clazz 实体类
     * @param id    实体ID
     * @param <T>   实体类型
     * @return 实体对象，如果不存在返回null
     */
    public <T extends BaseDO> T findById(Class<T> clazz, Long id) {
        try {
            ConfigStore configs = new DefaultConfigStore();
            configs.and(Compare.EQUAL, "id", id);
            configs.and(Compare.NULL, "deleted_time");

            return clazz.cast(service.select(clazz, configs));
        } catch (Exception e) {
            log.error("根据ID查找实体失败: class={}, id={}", clazz.getSimpleName(), id, e);
            throw new BizException(StatusCode.DB_SELECT_ERROR);
        }
    }

    /**
     * 根据ID查找实体（返回Optional）
     *
     * @param clazz 实体类
     * @param id    实体ID
     * @param <T>   实体类型
     * @return Optional包装的实体对象
     */
    public <T extends BaseDO> Optional<T> findByIdOptional(Class<T> clazz, Long id) {
        T entity = findById(clazz, id);
        return Optional.ofNullable(entity);
    }

    /**
     * 检查实体是否存在
     *
     * @param clazz 实体类
     * @param id    实体ID
     * @param <T>   实体类型
     * @return 是否存在
     */
    public <T extends BaseDO> boolean existsById(Class<T> clazz, Long id) {
        return findById(clazz, id) != null;
    }

    /**
     * 查找所有实体
     *
     * @param clazz 实体类
     * @param <T>   实体类型
     * @return 实体列表
     */
    public <T extends BaseDO> List<T> findAll(Class<T> clazz) {
        try {
            ConfigStore configs = new DefaultConfigStore();
            configs.and(Compare.NULL, "deleted_time");

            String tableName = getTableName(clazz);
            DataSet dataSet = service.querys(tableName, configs);
            return dataSet.entitys(clazz).stream().toList();
        } catch (Exception e) {
            log.error("查找所有实体失败: class={}", clazz.getSimpleName(), e);
            throw new BizException(StatusCode.DB_SELECT_ERROR);
        }
    }

    /**
     * 根据ID列表查找实体
     *
     * @param clazz 实体类
     * @param ids   ID列表
     * @param <T>   实体类型
     * @return 实体列表
     */
    public <T extends BaseDO> List<T> findAllById(Class<T> clazz, List<Long> ids) {
        try {
            ConfigStore configs = new DefaultConfigStore();
            configs.and(Compare.IN, "id", ids);
            configs.and(Compare.NULL, "deleted_time");

            String tableName = getTableName(clazz);
            DataSet dataSet = service.querys(tableName, configs);
            return dataSet.entitys(clazz).stream().toList();
        } catch (Exception e) {
            log.error("根据ID列表查找实体失败: class={}, ids={}", clazz.getSimpleName(), ids, e);
            throw new BizException(StatusCode.DB_SELECT_ERROR);
        }
    }

    /**
     * 统计实体数量
     *
     * @param clazz 实体类
     * @param <T>   实体类型
     * @return 实体数量
     */
    public <T extends BaseDO> long count(Class<T> clazz) {
        try {
            ConfigStore configs = new DefaultConfigStore();
            configs.and(Compare.NULL, "deleted_time");

            String tableName = getTableName(clazz);
            DataSet dataSet = service.querys(tableName, configs);
            return dataSet.total();
        } catch (Exception e) {
            log.error("统计实体数量失败: class={}", clazz.getSimpleName(), e);
            throw new BizException(StatusCode.DB_SELECT_ERROR);
        }
    }

    /**
     * 根据ID删除实体（软删除）
     *
     * @param clazz 实体类
     * @param id    实体ID
     * @param <T>   实体类型
     */
    public <T extends BaseDO> void deleteById(Class<T> clazz, Long id) {
        try {
            ConfigStore configs = new DefaultConfigStore();
            configs.and(Compare.EQUAL, "id", id);
            configs.and(Compare.NULL, "deleted_time");

            DataRow row = new DataRow();
            row.put("deleted_time", LocalDateTime.now());

            long result = service.update(getTableName(clazz), row, configs);
            if (result == 0) {
                throw new BizException(StatusCode.DB_DELETE_ERROR);
            }
        } catch (Exception e) {
            log.error("根据ID删除实体失败: class={}, id={}", clazz.getSimpleName(), id, e);
            throw new BizException(StatusCode.DB_DELETE_ERROR);
        }
    }

    /**
     * 删除实体（软删除）
     *
     * @param entity 要删除的实体
     * @param <T>    实体类型
     */
    public <T extends BaseDO> void delete(T entity) {
        if (entity != null && entity.getId() != null) {
            @SuppressWarnings("unchecked")
            Class<T> entityClass = (Class<T>) entity.getClass();
            deleteById(entityClass, entity.getId());
        }
    }

    /**
     * 批量删除实体（软删除）
     *
     * @param entities 实体列表
     * @param <T>      实体类型
     */
    public <T extends BaseDO> void deleteAll(List<T> entities) {
        for (T entity : entities) {
            delete(entity);
        }
    }

    /**
     * 根据ID列表删除实体（软删除）
     *
     * @param clazz 实体类
     * @param ids   ID列表
     * @param <T>   实体类型
     */
    public <T extends BaseDO> void deleteAllById(Class<T> clazz, List<Long> ids) {
        try {
            ConfigStore configs = new DefaultConfigStore();
            configs.and(Compare.IN, "id", ids);
            configs.and(Compare.NULL, "deleted_time");

            DataRow row = new DataRow();
            row.put("deleted_time", LocalDateTime.now());

            long result = service.update(getTableName(clazz), row, configs);
            if (result == 0) {
                throw new BizException(StatusCode.DB_DELETE_ERROR);
            }
        } catch (Exception e) {
            log.error("根据ID列表删除实体失败: class={}, ids={}", clazz.getSimpleName(), ids, e);
            throw new BizException(StatusCode.DB_DELETE_ERROR);
        }
    }

    /**
     * 删除所有实体（软删除）
     *
     * @param clazz 实体类
     * @param <T>   实体类型
     */
    public <T extends BaseDO> void deleteAll(Class<T> clazz) {
        try {
            ConfigStore configs = new DefaultConfigStore();
            configs.and(Compare.NULL, "deleted_time");

            DataRow row = new DataRow();
            row.put("deleted_time", LocalDateTime.now());

            service.update(getTableName(clazz), row, configs);
        } catch (Exception e) {
            log.error("删除所有实体失败: class={}", clazz.getSimpleName(), e);
            throw new BizException(StatusCode.DB_DELETE_ERROR);
        }
    }

    /**
     * 分页查询
     *
     * @param clazz     实体类
     * @param pageIndex 页码（从1开始）
     * @param pageSize  页大小
     * @param <T>       实体类型
     * @return 分页结果
     */
    public <T extends BaseDO> PageResult<T> findAll(Class<T> clazz, int pageIndex, int pageSize) {
        try {
            ConfigStore configs = new DefaultConfigStore();
            configs.and(Compare.NULL, "deleted_time");

            PageNavi page = new DefaultPageNavi(pageIndex, pageSize);
            configs.setPageNavi(page);

            String tableName = getTableName(clazz);
            DataSet dataSet = service.querys(tableName, configs);

            return new PageResult<>(
                    dataSet.entitys(clazz).stream().toList(),
                    pageIndex,
                    pageSize,
                    dataSet.total()
            );
        } catch (Exception e) {
            log.error("分页查询失败: class={}, pageIndex={}, pageSize={}",
                    clazz.getSimpleName(), pageIndex, pageSize, e);
            throw new BizException(StatusCode.DB_SELECT_ERROR);
        }
    }

    /**
     * 条件查询
     *
     * @param clazz   实体类
     * @param configs 查询条件
     * @param <T>     实体类型
     * @return 实体列表
     */
    public <T extends BaseDO> List<T> findAll(Class<T> clazz, ConfigStore configs) {
        try {
            configs.and(Compare.NULL, "deleted_time");

            String tableName = getTableName(clazz);
            DataSet dataSet = service.querys(tableName, configs);
            return dataSet.entitys(clazz).stream().toList();
        } catch (Exception e) {
            log.error("条件查询失败: class={}", clazz.getSimpleName(), e);
            throw new BizException(StatusCode.DB_SELECT_ERROR);
        }
    }

    /**
     * 条件查询单个实体
     *
     * @param clazz   实体类
     * @param configs 查询条件
     * @param <T>     实体类型
     * @return 实体对象，如果不存在返回null
     */
    public <T extends BaseDO> T findOne(Class<T> clazz, ConfigStore configs) {
        try {
            configs.and(Compare.NULL, "deleted_time");

            return clazz.cast(service.select(clazz, configs));
        } catch (Exception e) {
            log.error("条件查询单个实体失败: class={}", clazz.getSimpleName(), e);
            throw new BizException(StatusCode.DB_SELECT_ERROR);
        }
    }

    /**
     * 条件查询单个实体（返回Optional）
     *
     * @param clazz   实体类
     * @param configs 查询条件
     * @param <T>     实体类型
     * @return Optional包装的实体对象
     */
    public <T extends BaseDO> Optional<T> findOneOptional(Class<T> clazz, ConfigStore configs) {
        T entity = findOne(clazz, configs);
        return Optional.ofNullable(entity);
    }

    /**
     * 创建表
     *
     * @param clazz   实体类
     * @param reset   是否删除已存在的表
     * @param execute 是否执行DDL
     * @throws Exception 异常
     */
    public void createTable(Class<?> clazz, boolean reset, boolean execute) throws Exception {
        if (service == null) {
            throw new Exception("[DataRepository.createTable] AnylineService is null.");
        }

        log.info("CreateTable: {}", clazz);
        Table<?> table = Table.from(clazz);

        if (service.metadata().exists(table) && reset) {
            log.info("DropTable: {}", clazz);
            service.ddl().drop(table);
        }

        table.execute(execute);
        service.ddl().create(table);

        // 处理唯一约束
        if (clazz.isAnnotationPresent(jakarta.persistence.Table.class)) {
            jakarta.persistence.Table tableAnnotation =
                    (jakarta.persistence.Table) clazz.getAnnotation(jakarta.persistence.Table.class);
            jakarta.persistence.UniqueConstraint[] uniqueConstraints = tableAnnotation.uniqueConstraints();

            for (jakarta.persistence.UniqueConstraint constraint : uniqueConstraints) {
                log.info("表名: {} 约束名称: {} 约束列名 {}",
                        table.getName(), constraint.name(), constraint.columnNames());

                Constraint<?> uk = new Constraint<>(table, constraint.name())
                        .setType(Constraint.TYPE.UNIQUE);

                for (String column : constraint.columnNames()) {
                    log.info(column);
                    uk.addColumn(column);
                }

                service.ddl().add(uk);
            }
        }

        List<Run> ddls = (List<Run>) table.runs();
        for (Run ddl : ddls) {
            log.info(ddl.getFinalUpdate());
        }
    }


}