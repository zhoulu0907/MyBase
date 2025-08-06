package com.cmsr.onebase.framework.aynline;

import com.cmsr.onebase.framework.common.anyline.web.BizException;
import com.cmsr.onebase.framework.common.anyline.web.StatusCode;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.data.base.BaseDO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.Run;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.*;
import org.anyline.entity.generator.PrimaryGenerator;
import org.anyline.metadata.Constraint;
import org.anyline.metadata.Table;
import org.anyline.service.AnylineService;
import org.anyline.util.ConfigTable;
import org.anyline.data.jdbc.util.DataSourceUtil;
import org.anyline.proxy.ServiceProxy;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    static {
        ConfigTable.GENERATOR.set(PrimaryGenerator.GENERATOR.SNOWFLAKE);
        ConfigTable.IS_AUTO_CHECK_METADATA = true;
        ConfigTable.IS_INSERT_NULL_COLUMN = false;
        ConfigTable.IS_INSERT_NULL_FIELD = false;
        ConfigTable.IS_INSERT_EMPTY_FIELD = true;
        ConfigTable.IS_INSERT_EMPTY_COLUMN = true;
        // ConfigTable.IS_ENABLE_SQL_DATATYPE_CONVERT = true;
    }

    @Resource
    private AnylineService<?> anylineService;

    public DataRepository() {
    }

    /**
     * 获取实体对应的表名
     *
     * @param clazz 实体类
     * @return 表名
     */
    private String getTableName(Class<?> clazz) {
        jakarta.persistence.Table annotation = clazz.getAnnotation(jakarta.persistence.Table.class);
        return annotation != null ? annotation.name() : clazz.getSimpleName().toLowerCase();
    }

    /**
     * 保存实体（插入）
     *
     * @param entity 要保存的实体
     * @param <T>    实体类型
     * @return 保存后的实体
     */
    public <T extends BaseDO> T insert(T entity) {
        try {
            Long result = anylineService.insert(entity);
            if (result == 0) {
                throw new BizException(StatusCode.DB_INSERT_ERROR);
            }
            return entity;
        } catch (Exception e) {
            log.error("保存实体失败: {}", entity.getClass().getSimpleName(), e);
            throw e;
        }
    }

    /**
     * 批量插入实体
     *
     * @param entities 实体列表
     * @param <T>      实体类型
     * @return 保存后的实体列表
     */
    public <T extends BaseDO> List<T> insertBatch(List<T> entities) {
        try {
            for (T entity : entities) {
                insert(entity);
            }
            return entities;
        } catch (Exception e) {
            log.error("批量保存实体失败", e);
            throw new BizException(StatusCode.DB_INSERT_ERROR);
        }
    }

    /**
     * 更新
     *
     * @param entity 要保存的实体
     * @param <T>    实体类型
     * @return 保存后的实体
     */
    public <T extends BaseDO> T update(T entity) {
        if (entity.getId() == null || entity.getId() == 0) {
            throw new BizException(StatusCode.DB_ID_NULL);
        }
        try {
            // 更新
            ConfigStore configs = new DefaultConfigStore();
            configs.and(Compare.EQUAL, "id", entity.getId());
            Long result = anylineService.update(entity, configs);
            log.info("[{}] update  ---> effect rows = {}", entity.getClass().getSimpleName(), result);

            if (result == 0) {
                throw new BizException(StatusCode.DB_UPDATE_ERROR);
            }
            return entity;
        } catch (Exception e) {
            log.error("保存实体失败: {}", entity.getClass().getSimpleName(), e);
            throw new BizException(StatusCode.DB_UPDATE_ERROR);
        }
    }


    /**
     * 更新
     * @param <T>
     *
     * @param entity 要保存的实体
     * @param <T>    实体类型
     * @return 保存后的实体
     */
    public <T> long updateByConfig(Class<T> clazz, ConfigStore configs) {
        if (clazz== null) {
            throw new BizException(StatusCode.DB_UPDATE_ERROR);
        }
        try {
            // 更新
            long result = anylineService.update(clazz, configs);
            log.info("[{}] updateByConfig  ---> effect rows = {}", clazz.getSimpleName(), result);
            return result;
        } catch (Exception e) {
            log.error("保存实体失败: {}", clazz.getSimpleName(), e);
            throw new BizException(StatusCode.DB_UPDATE_ERROR);
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
            String tableName = getTableName(clazz);
            return anylineService.select(tableName, clazz, configs);
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
            String tableName = getTableName(clazz);
            DataSet dataSet = anylineService.querys(tableName, configs);
            return dataSet.entity(clazz);
        } catch (Exception e) {
            log.error("查找所有实体失败: class={}", clazz.getSimpleName(), e);
            throw new BizException(StatusCode.DB_SELECT_ERROR);
        }
    }

    /**
     * 自定义查找实体列表
     *
     * @param clazz   实体类
     * @param configs configs
     * @param <T>     实体类型
     * @return 实体列表
     */
    public <T> List<T> findAllByConfig(Class<T> clazz, ConfigStore configs) {
        try {
            String tableName = getTableName(clazz);
            DataSet dataSet = anylineService.querys(tableName, configs);
            return dataSet.entity(clazz);
        } catch (Exception e) {
            log.error("根据ID列表查找实体失败: class={}, configs={}", clazz.getSimpleName(), configs, e);
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
    public <T extends BaseDO> List<T> findAllByIds(Class<T> clazz, Collection<Long> ids) {
        try {
            ConfigStore configs = new DefaultConfigStore();
            configs.and(Compare.IN, "id", ids);

            String tableName = getTableName(clazz);
            DataSet dataSet = anylineService.querys(tableName, configs);
            return dataSet.entity(clazz);
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

            String tableName = getTableName(clazz);
            DataSet dataSet = anylineService.querys(tableName, configs);
            return dataSet.total();
        } catch (Exception e) {
            log.error("统计实体数量失败: class={}", clazz.getSimpleName(), e);
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
    public <T extends BaseDO> long countByConfig(Class<T> clazz, ConfigStore configs) {
        try {
            String tableName = getTableName(clazz);
            DataSet dataSet = anylineService.querys(tableName, configs);
            return dataSet.total();
        } catch (Exception e) {
            log.error("统计实体数量失败: class={}", clazz.getSimpleName(), e);
            throw new BizException(StatusCode.DB_SELECT_ERROR);
        }
    }

    /**
     * 根据ID删除实体（软删除）
     *
     * @param clazz   实体类
     * @param configs configs
     * @param <T>     实体类型
     */
    public <T extends BaseDO>  long deleteByConfig(Class<T> clazz, ConfigStore configs) {
        try {
            DataRow row = new DataRow();
            row.put("deleted", 1);  // 设置逻辑删除标记
            long result = anylineService.update(getTableName(clazz), row, configs);
            log.info("[{}] deleteByConfig  ---> effect rows = {}", clazz, result);

            return result;
        } catch (Exception e) {
            log.error("根据ID删除实体失败: class={}, configs={}", clazz.getSimpleName(), configs, e);
            throw new BizException(StatusCode.DB_DELETE_ERROR);
        }
    }

    /**
     * 根据ID删除实体（软删除）
     *
     * @param clazz   实体类
     * @param configs configs
     * @param <T>     实体类型
     */
    public <T extends BaseDO> long deleteByConfigReturn(Class<T> clazz, ConfigStore configs) {
        try {
            DataRow row = new DataRow();
            row.put("deleted", 1);  // 设置逻辑删除标记
            long result = anylineService.update(getTableName(clazz), row, configs);
            log.info("[{}] deleteByConfig  ---> effect rows = {}", clazz, result);
            return result;
        } catch (Exception e) {
            log.error("根据ID删除实体失败: class={}, configs={}", clazz.getSimpleName(), configs, e);
            throw new BizException(StatusCode.DB_DELETE_ERROR);
        }
    }

    /**
     * 根据ID删除实体（软删除）
     *
     * @param clazz 实体类
     * @param id    实体ID
     * @param <T>   实体类型
     */
    public <T extends BaseDO> long deleteById(Class<T> clazz, Long id) {
        try {
            ConfigStore configs = new DefaultConfigStore();
            configs.and(Compare.EQUAL, "id", id);
            DataRow row = new DataRow();
            row.put("deleted", 1);  // 设置逻辑删除标记
            long result = anylineService.update(getTableName(clazz), row, configs);
            log.info("[{}] deleteById  ---> effect rows = {}, id = {}", clazz, result, id);

            return result;
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
    public <T extends BaseDO> long delete(T entity) {
        if (entity != null && entity.getId() != null) {
            @SuppressWarnings("unchecked")
            Class<T> entityClass = (Class<T>) entity.getClass();
            return deleteById(entityClass, entity.getId());
        }
        return 0;
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
    public <T extends BaseDO> long deleteAllById(Class<T> clazz, Collection<Long> ids) {
        try {
            ConfigStore configs = new DefaultConfigStore();
            configs.and(Compare.IN, "id", ids);
            DataRow row = new DataRow();
            row.put("deleted", 1);  // 设置逻辑删除标记

            long result = anylineService.update(getTableName(clazz), row, configs);
            log.info("[{}] deleteAllById  ---> effect rows={}, ids={}", clazz, result,ids);
            return result;
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
            DataRow row = new DataRow();
            row.put("deleted", 1);  // 设置逻辑删除标记

            long result = anylineService.update(getTableName(clazz), row, configs);
            log.info("[{}] deleteAll  ---> effect rows={}", clazz, result);

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

            PageNavi page = new DefaultPageNavi(pageIndex, pageSize);
            configs.setPageNavi(page);

            String tableName = getTableName(clazz);
            DataSet dataSet = anylineService.querys(tableName, configs);

            return new PageResult<>(
                    dataSet.entitys(clazz).stream().toList(),
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
            String tableName = getTableName(clazz);
            DataSet dataSet = anylineService.querys(tableName, configs);
            return dataSet.entity(clazz);
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
            String tableName = getTableName(clazz);
            return anylineService.select(tableName, clazz, configs);
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
     * 条件分页查询
     *
     * @param clazz     实体类
     * @param configs   查询条件
     * @param pageIndex 页码（从1开始）
     * @param pageSize  页大小
     * @param <T>       实体类型
     * @return 分页结果
     */
    public <T extends BaseDO> com.cmsr.onebase.framework.common.pojo.PageResult<T> findPageWithConditions(
            Class<T> clazz, ConfigStore configs, int pageIndex, int pageSize) {
        try {

            PageNavi page = new DefaultPageNavi(pageIndex, pageSize);
            configs.setPageNavi(page);

            String tableName = getTableName(clazz);
            DataSet dataSet = anylineService.querys(tableName, configs);

            return new com.cmsr.onebase.framework.common.pojo.PageResult<>(
                    dataSet.entitys(clazz).stream().toList(),
                    dataSet.total()
            );
        } catch (Exception e) {
            log.error("条件分页查询失败: class={}, pageIndex={}, pageSize={}",
                    clazz.getSimpleName(), pageIndex, pageSize, e);
            throw new BizException(StatusCode.DB_SELECT_ERROR);
        }
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
        if (anylineService == null) {
            throw new Exception("[DataRepository.createTable] AnylineService is null.");
        }

        log.info("CreateTable: {}", clazz);
        Table<?> table = Table.from(clazz);

        if (anylineService.metadata().exists(table) && reset) {
            log.info("DropTable: {}", clazz);
            anylineService.ddl().drop(table);
        }

        table.execute(execute);
        anylineService.ddl().create(table);

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

                anylineService.ddl().add(uk);
            }
        }

        List<Run> ddls = (List<Run>) table.runs();
        for (Run ddl : ddls) {
            log.info(ddl.getFinalUpdate());
        }
    }

    // ==================== 数据源动态连接相关的公共方法 ====================
    
    /**
     * 创建临时的AnylineService用于数据库操作
     * @param datasourceConfig 数据源配置信息 
     * @return AnylineService实例
     */
    public AnylineService<?> createTemporaryService(Map<String, Object> datasourceConfig) {
        try {
            String url = (String) datasourceConfig.get("url");
            String username = (String) datasourceConfig.get("username");
            String password = (String) datasourceConfig.get("password");
            String datasourceType = (String) datasourceConfig.get("datasourceType");

            // 如果配置中没有完整的URL，则根据host、port、database构建JDBC URL
            if (url == null || url.trim().isEmpty()) {
                String host = (String) datasourceConfig.get("host");
                Object portObj = datasourceConfig.get("port");
                String database = (String) datasourceConfig.get("database");
                if (host != null && !host.trim().isEmpty()) {
                    int port = getDefaultPort(datasourceType);
                    if (portObj instanceof Integer) {
                        port = (Integer) portObj;
                    } else if (portObj instanceof String) {
                        port = Integer.parseInt((String) portObj);
                    }
                    // 根据数据源类型构建JDBC URL
                    url = buildJdbcUrl(datasourceType, host, port, database);
                }
            }

            // 参数校验
            if (url == null || url.trim().isEmpty()) {
                throw new RuntimeException("无法构建数据源连接URL，请检查配置信息");
            }

        // 构建数据源配置 - 不使用连接池参数，让AnyLine使用默认处理
        Map<String, Object> dsConfig = Map.of(
                "url", url,
                "user", username != null ? username : "",
                "password", password != null ? password : "",
                "driver", getDriverByType(datasourceType)
        );            // 使用 anyline 的 DataSourceUtil 构建数据源
            DataSource dataSource = DataSourceUtil.build(dsConfig);

            // 创建临时的 AnylineService
            return ServiceProxy.temporary(dataSource);
        } catch (Exception e) {
            throw new RuntimeException("创建数据库连接失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 执行DDL语句
     * @param datasourceConfig 数据源配置信息
     * @param ddl DDL语句
     */
    public void executeDDL(Map<String, Object> datasourceConfig, String ddl) {
        try {
            AnylineService<?> temporaryService = createTemporaryService(datasourceConfig);
            temporaryService.execute(ddl);
            log.info("成功执行DDL: {}", ddl);
        } catch (Exception e) {
            log.error("执行DDL失败: {}", ddl, e);
            throw new RuntimeException("执行DDL失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 根据数据源类型构建JDBC URL
     */
    public String buildJdbcUrl(String datasourceType, String host, int port, String database) {
        if (host == null || host.trim().isEmpty()) {
            throw new RuntimeException("主机地址不能为空");
        }
        
        String databasePart = (database != null && !database.trim().isEmpty()) ? database : "";
        
        switch (datasourceType.toUpperCase()) {
            case "POSTGRESQL":
                return String.format("jdbc:postgresql://%s:%d/%s", host, port, databasePart);
            case "MYSQL":
                return String.format("jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai", 
                        host, port, databasePart);
            case "ORACLE":
                return String.format("jdbc:oracle:thin:@%s:%d:%s", host, port, databasePart);
            case "SQLSERVER":
                return String.format("jdbc:sqlserver://%s:%d;DatabaseName=%s", host, port, databasePart);
            case "KINGBASE":
                return String.format("jdbc:kingbase8://%s:%d/%s", host, port, databasePart);
            case "TDENGINE":
                return String.format("jdbc:TAOS-RS://%s:%d/%s", host, port, databasePart);
            case "CLICKHOUSE":
                return String.format("jdbc:clickhouse://%s:%d/%s", host, port, databasePart);
            case "DM":
                return String.format("jdbc:dm://%s:%d/%s", host, port, databasePart);
            case "OPENGAUSS":
                return String.format("jdbc:opengauss://%s:%d/%s", host, port, databasePart);
            case "DB2":
                return String.format("jdbc:db2://%s:%d/%s", host, port, databasePart);
            default:
                log.warn("未知的数据源类型，使用通用格式: {}", datasourceType);
                return String.format("jdbc:%s://%s:%d/%s", datasourceType.toLowerCase(), host, port, databasePart);
        }
    }
    
    /**
     * 根据数据源类型获取对应的驱动类名
     */
    public String getDriverByType(String datasourceType) {
        switch (datasourceType.toUpperCase()) {
            case "POSTGRESQL":
                return "org.postgresql.Driver";
            case "MYSQL":
                return "com.mysql.cj.jdbc.Driver";
            case "ORACLE":
                return "oracle.jdbc.driver.OracleDriver";
            case "SQLSERVER":
                return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            case "KINGBASE":
                return "com.kingbase8.Driver";
            case "TDENGINE":
                return "com.taosdata.jdbc.TSDBDriver";
            case "CLICKHOUSE":
                return "ru.yandex.clickhouse.ClickHouseDriver";
            case "DM":
                return "dm.jdbc.driver.DmDriver";
            case "OPENGAUSS":
                return "org.opengauss.Driver";
            case "DB2":
                return "com.ibm.db2.jcc.DB2Driver";
            default:
                throw new RuntimeException("不支持的数据源类型: " + datasourceType);
        }
    }
    
    /**
     * 根据数据源类型获取默认端口
     */
    public int getDefaultPort(String datasourceType) {
        switch (datasourceType.toUpperCase()) {
            case "POSTGRESQL":
                return 5432;
            case "MYSQL":
                return 3306;
            case "ORACLE":
                return 1521;
            case "SQLSERVER":
                return 1433;
            case "KINGBASE":
                return 54321;
            case "TDENGINE":
                return 6041;
            case "CLICKHOUSE":
                return 8123;
            case "DM":
                return 5236;
            case "OPENGAUSS":
                return 5432;
            case "DB2":
                return 50000;
            default:
                return 5432; // 默认使用PostgreSQL端口
        }
    }


}
