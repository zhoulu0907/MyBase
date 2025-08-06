package com.cmsr.onebase.module.metadata.service.helper;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.module.metadata.convert.datasource.DatasourceConvert;
import com.cmsr.onebase.module.metadata.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataSystemFieldsDO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.jdbc.util.DataSourceUtil;
import org.anyline.proxy.ServiceProxy;
import javax.sql.DataSource;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 数据源服务助手类
 * <p>
 * 提供数据源相关的公共服务方法和统一的数据库操作接口，避免在多个Service中重复相同的逻辑
 *
 * @author matianyu
 * @date 2025-08-05
 */
@Component
@Slf4j
public class DatasourceServiceHelper extends DataRepository {

    @Resource
    private DataRepository dataRepository;

    @Resource
    private DatasourceConvert datasourceConvert;

    // ==================== 数据源相关方法 ====================

    /**
     * 根据数据源DO对象创建临时的AnylineService用于数据库操作
     *
     * @param datasource 数据源配置对象
     * @return AnylineService实例
     */
    public AnylineService<?> createTemporaryService(MetadataDatasourceDO datasource) {
        // 从数据源配置中获取连接参数
        Map<String, Object> config = datasourceConvert.stringToMap(datasource.getConfig());
        config.put("datasourceType", datasource.getDatasourceType());

        // 使用本助手的 Map 版本创建临时服务
        return createTemporaryService(config);
    }

    /**
     * 根据数据源配置参数创建临时 AnylineService 服务
     *
     * @param datasourceConfig 数据源配置参数
     * @return AnylineService 实例
     */
    public AnylineService<?> createTemporaryService(Map<String, Object> datasourceConfig) {
        try {
            String url = (String) datasourceConfig.get("url");
            String username = (String) datasourceConfig.get("username");
            String password = (String) datasourceConfig.get("password");
            String datasourceType = (String) datasourceConfig.get("datasourceType");

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
                    url = buildJdbcUrl(datasourceType, host, port, database);
                }
            }

            if (url == null || url.trim().isEmpty()) {
                throw new RuntimeException("无法构建数据源连接URL，请检查配置信息");
            }

            Map<String, Object> dsConfig = Map.of(
                    "url", url,
                    "user", username != null ? username : "",
                    "password", password != null ? password : "",
                    "driver", getDriverByType(datasourceType)
            );
            DataSource dataSource = DataSourceUtil.build(dsConfig);
            return ServiceProxy.temporary(dataSource);
        } catch (Exception e) {
            throw new RuntimeException("创建数据库连接失败: " + e.getMessage(), e);
        }
    }

    /**
     * 执行 DDL 语句
     *
     * @param datasourceConfig 数据源配置参数
     * @param ddl DDL 语句
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
     * 根据数据源类型构建 JDBC URL
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
                return String.format(
                        "jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai",
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
     * 根据数据源类型获取驱动类名
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
                return 5432;
        }
    }

    // ==================== 通用数据库操作方法 ====================

    /**
     * 插入实体
     *
     * @param entity 要插入的实体
     * @param <T> 实体类型
     * @return 插入后的实体（包含生成的ID）
     */
    public <T extends BaseDO> T insert(T entity) {
        return dataRepository.insert(entity);
    }

    /**
     * 更新实体
     *
     * @param entity 要更新的实体
     * @param <T> 实体类型
     * @return 更新后的实体
     */
    public <T extends BaseDO> T update(T entity) {
        return dataRepository.update(entity);
    }

    /**
     * 根据ID删除实体
     *
     * @param clazz 实体类
     * @param id 实体ID
     * @param <T> 实体类型
     */
    public <T extends BaseDO> void deleteById(Class<T> clazz, Long id) {
        dataRepository.deleteById(clazz, id);
    }

    /**
     * 根据条件删除实体
     *
     * @param clazz 实体类
     * @param configStore 查询条件
     * @param <T> 实体类型
     */
    public <T extends BaseDO> void deleteByConfig(Class<T> clazz, DefaultConfigStore configStore) {
        dataRepository.deleteByConfig(clazz, configStore);
    }

    /**
     * 根据ID查找实体
     *
     * @param clazz 实体类
     * @param id 实体ID
     * @param <T> 实体类型
     * @return 实体对象，不存在则返回null
     */
    public <T extends BaseDO> T findById(Class<T> clazz, Long id) {
        return dataRepository.findById(clazz, id);
    }

    /**
     * 根据条件查找单个实体
     *
     * @param clazz 实体类
     * @param configStore 查询条件
     * @param <T> 实体类型
     * @return 实体对象，不存在则返回null
     */
    public <T extends BaseDO> T findOne(Class<T> clazz, DefaultConfigStore configStore) {
        return dataRepository.findOne(clazz, configStore);
    }

    /**
     * 根据条件查找所有实体
     *
     * @param clazz 实体类
     * @param configStore 查询条件
     * @param <T> 实体类型
     * @return 实体列表
     */
    public <T extends BaseDO> List<T> findAllByConfig(Class<T> clazz, DefaultConfigStore configStore) {
        return dataRepository.findAllByConfig(clazz, configStore);
    }

    /**
     * 根据条件统计数量
     *
     * @param clazz 实体类
     * @param configStore 查询条件
     * @param <T> 实体类型
     * @return 统计数量
     */
    public <T extends BaseDO> long countByConfig(Class<T> clazz, ConfigStore configStore) {
        return dataRepository.countByConfig(clazz, configStore);
    }

    /**
     * 分页查询实体
     *
     * @param clazz 实体类
     * @param configStore 查询条件
     * @param pageNo 页码（从1开始）
     * @param pageSize 每页大小
     * @param <T> 实体类型
     * @return 分页结果
     */
    public <T extends BaseDO> PageResult<T> findPageWithConditions(Class<T> clazz, ConfigStore configStore,
                                                   int pageNo, int pageSize) {
        return dataRepository.findPageWithConditions(clazz, configStore, pageNo, pageSize);
    }

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
