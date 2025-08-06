package com.cmsr.onebase.module.metadata.service.datasource;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.controller.admin.datasource.vo.ColumnInfoRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.datasource.vo.DatasourcePageReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.datasource.vo.DatasourceSaveReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.datasource.vo.DatasourceTestConnectionReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.datasource.vo.DatasourceTestConnectionRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.datasource.vo.DatasourceTypeRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.datasource.vo.TableInfoRespVO;
import com.cmsr.onebase.module.metadata.service.datasource.vo.ColumnQueryVO;
import com.cmsr.onebase.module.metadata.service.datasource.vo.TableQueryVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.convert.datasource.DatasourceConvert;
import com.cmsr.onebase.module.metadata.enums.DatasourceTypeEnum;
import com.cmsr.onebase.module.metadata.service.helper.DatasourceServiceHelper;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.jdbc.util.DataSourceUtil;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.anyline.metadata.Column;
import org.anyline.metadata.Table;
import org.anyline.proxy.ServiceProxy;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.enums.ErrorCodeConstants.DATASOURCE_NOT_EXISTS;
import static com.cmsr.onebase.module.metadata.enums.ErrorCodeConstants.DATASOURCE_CODE_DUPLICATE;
import org.anyline.entity.Compare;

/**
 * 数据源 Service 实现类
 */
@Service
@Slf4j
public class MetadataDatasourceServiceImpl implements MetadataDatasourceService {

    @Resource
    private DataRepository dataRepository;
    @Resource
    private org.springframework.core.env.Environment env;
    @Resource
    private DatasourceConvert datasourceConvert;
    @Resource
    private DatasourceServiceHelper datasourceServiceHelper;

    @Override
    public List<DatasourceTypeRespVO> getDatasourceTypes() {
        return Arrays.stream(DatasourceTypeEnum.values())
                .map(this::convertToTypeRespVO)
                .toList();
    }

    @Override
    public List<TableInfoRespVO> getTablesByDatasourceId(TableQueryVO queryVO) {
        // 获取数据源信息
        MetadataDatasourceDO datasource = getDatasource(Long.valueOf(queryVO.getDatasourceId()));
        if (datasource == null) {
            throw exception(DATASOURCE_NOT_EXISTS);
        }

        // 创建临时数据源连接
        AnylineService<?> temporaryService = datasourceServiceHelper.createTemporaryService(datasource);

        // 获取所有表信息
        List<String> tableNames = temporaryService.tables();

        List<TableInfoRespVO> result = new ArrayList<>();
        for (String tableNameStr : tableNames) {
            // 过滤条件
            if (StringUtils.hasText(queryVO.getKeyword()) && !tableNameStr.toLowerCase().contains(queryVO.getKeyword().toLowerCase())) {
                continue;
            }

            // 构建Table对象来获取详细信息
            Table<?> table = new Table<>(tableNameStr);
            if (StringUtils.hasText(queryVO.getSchemaName())) {
                table.setSchema(queryVO.getSchemaName());
            }

            // 获取表的详细信息
            Table<?> tableDetail = temporaryService.metadata().table(tableNameStr);
            if (tableDetail == null) {
                tableDetail = table; // 如果获取不到详细信息，使用基本信息
            }

            TableInfoRespVO tableInfo = new TableInfoRespVO();
            tableInfo.setTableName(tableDetail.getName());
            tableInfo.setDisplayName(StringUtils.hasText(tableDetail.getComment()) ? tableDetail.getComment() : tableDetail.getName());
            tableInfo.setTableComment(tableDetail.getComment());
            tableInfo.setTableType("TABLE");
            tableInfo.setSchemaName(tableDetail.getSchema() != null ? tableDetail.getSchema().toString() : queryVO.getSchemaName());
            // 获取行数（可能比较耗时，这里暂时设为0）
            tableInfo.setRowCount(0L);

            result.add(tableInfo);
        }
        return result;
    }

    @Override
    public List<ColumnInfoRespVO> getColumnsByTableName(ColumnQueryVO queryVO) {
        // 获取数据源信息
        MetadataDatasourceDO datasource = getDatasource(Long.valueOf(queryVO.getDatasourceId()));
        if (datasource == null) {
            throw exception(DATASOURCE_NOT_EXISTS);
        }

        // 创建临时数据源连接
        AnylineService<?> temporaryService = datasourceServiceHelper.createTemporaryService(datasource);

        // 构建表对象
        Table<?> table = new Table<>(queryVO.getTableName());
        if (StringUtils.hasText(queryVO.getSchemaName())) {
            table.setSchema(queryVO.getSchemaName());
        }

        // 获取表的所有字段信息
        List<String> columnNames = temporaryService.columns(table);

        List<ColumnInfoRespVO> result = new ArrayList<>();
        for (String columnName : columnNames) {
            // 构建Column对象来获取详细信息
            Column column = new Column(columnName);

            // 获取字段的详细信息
            Column columnDetail = temporaryService.metadata().column(table, columnName);
            if (columnDetail == null) {
                columnDetail = column; // 如果获取不到详细信息，使用基本信息
            }

            ColumnInfoRespVO columnInfo = new ColumnInfoRespVO();
            columnInfo.setColumnName(columnDetail.getName());
            columnInfo.setDisplayName(StringUtils.hasText(columnDetail.getComment()) ? columnDetail.getComment() : columnDetail.getName());
            columnInfo.setDataType(columnDetail.getTypeName());
            columnInfo.setDataLength(columnDetail.getPrecision());
            columnInfo.setDecimalPlaces(columnDetail.getScale());
            columnInfo.setIsNullable(columnDetail.isNullable());
            columnInfo.setIsPrimaryKey(columnDetail.isPrimaryKey());
            columnInfo.setIsAutoIncrement(columnDetail.isAutoIncrement());
            columnInfo.setDefaultValue(columnDetail.getDefaultValue() != null ? columnDetail.getDefaultValue().toString() : null);
            columnInfo.setColumnComment(columnDetail.getComment());
            columnInfo.setOrdinalPosition(columnDetail.getPosition());

            result.add(columnInfo);
        }
        return result;
    }

    /**
     * 将数据源类型枚举转换为响应VO
     *
     * @param typeEnum 数据源类型枚举
     * @return 数据源类型响应VO
     */
    private DatasourceTypeRespVO convertToTypeRespVO(DatasourceTypeEnum typeEnum) {
        DatasourceTypeRespVO respVO = new DatasourceTypeRespVO();
        respVO.setDatasourceType(typeEnum.getCode());
        respVO.setDisplayName(typeEnum.getDisplayName());
        respVO.setDescription(typeEnum.getDescription());
        respVO.setDefaultPort(typeEnum.getDefaultPort());
        respVO.setJdbcDriverClass(typeEnum.getJdbcDriverClass());
        respVO.setUrlTemplate(typeEnum.getUrlTemplate());
        // 所有数据源类型都支持读写和模式发现功能
        respVO.setSupportFeatures(Arrays.asList("READ", "WRITE", "SCHEMA_DISCOVERY"));
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDatasource(@Valid DatasourceSaveReqVO createReqVO) {
        // 校验编码唯一性
        validateDatasourceCodeUnique(null, createReqVO.getCode(), Long.valueOf(createReqVO.getAppId()));

        // 插入数据源
        MetadataDatasourceDO datasource = datasourceConvert.convert(createReqVO);
        datasource.setAppId(Long.valueOf(createReqVO.getAppId()));
        dataRepository.insert(datasource);

        return datasource.getId();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDefaultDatasource(Long appId) {
        // 从配置文件中读取默认数据源参数
        String host = env.getProperty("default.datasource.host");
        Integer port = env.getProperty("default.datasource.port", Integer.class);
        String database = env.getProperty("default.datasource.database");
        String username = env.getProperty("default.datasource.username");
        String password = env.getProperty("default.datasource.password");
        // 构造配置 map
        java.util.Map<String, Object> config = new java.util.HashMap<>();
        config.put("host", host);
        config.put("port", port);
        config.put("database", database);
        config.put("username", username);
        config.put("password", password);
        // 构造保存请求
        DatasourceSaveReqVO reqVO = new DatasourceSaveReqVO();
        reqVO.setDatasourceName(database);
        reqVO.setCode(database);
        reqVO.setDatasourceType("POSTGRESQL");
        reqVO.setConfig(config);
        reqVO.setDescription("默认数据源");
        reqVO.setRunMode(1);
        reqVO.setDatasourceOrigin(0);
        reqVO.setAppId(String.valueOf(appId));
        // 生成唯一的数据源编码，避免重复
        String uniqueCode = database + "_" + UUID.randomUUID().toString().replace("-", "");
        reqVO.setCode(uniqueCode);
        // 调用已有创建方法
        return createDatasource(reqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDatasource(@Valid DatasourceSaveReqVO updateReqVO) {
        // 校验存在
        validateDatasourceExists(Long.valueOf(updateReqVO.getId()));
        // 校验编码唯一性
        validateDatasourceCodeUnique(Long.valueOf(updateReqVO.getId()), updateReqVO.getCode(), Long.valueOf(updateReqVO.getAppId()));

        // 更新数据源
        MetadataDatasourceDO updateObj = datasourceConvert.convert(updateReqVO);
        // 手动设置ID，确保更新操作正常进行
        updateObj.setId(Long.valueOf(updateReqVO.getId()));
        updateObj.setAppId(Long.valueOf(updateReqVO.getAppId()));
        dataRepository.update(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDatasource(Long id) {
        // 校验存在
        validateDatasourceExists(id);

        // 删除数据源
        dataRepository.deleteById(MetadataDatasourceDO.class, id);
    }

    private void validateDatasourceExists(Long id) {
        if (dataRepository.findById(MetadataDatasourceDO.class, id) == null) {
            throw exception(DATASOURCE_NOT_EXISTS);
        }
    }

    private void validateDatasourceCodeUnique(Long id, String code, Long appId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("code", code);
        configStore.and("app_id", appId);
        if (id != null) {
            configStore.and(Compare.NOT_EQUAL, "id", id);
        }

        long count = dataRepository.countByConfig(MetadataDatasourceDO.class, configStore);
        if (count > 0) {
            throw exception(DATASOURCE_CODE_DUPLICATE);
        }
    }

    @Override
    public MetadataDatasourceDO getDatasource(Long id) {
        return dataRepository.findById(MetadataDatasourceDO.class, id);
    }

    @Override
    public PageResult<MetadataDatasourceDO> getDatasourcePage(DatasourcePageReqVO pageReqVO) {
        DefaultConfigStore configStore = new DefaultConfigStore();

        // 添加查询条件
        if (pageReqVO.getDatasourceName() != null) {
            configStore.and(Compare.LIKE, "datasource_name", "%" + pageReqVO.getDatasourceName() + "%");
        }
        if (pageReqVO.getCode() != null) {
            configStore.and(Compare.LIKE, "code", "%" + pageReqVO.getCode() + "%");
        }
        if (pageReqVO.getDatasourceType() != null) {
            configStore.and("datasource_type", pageReqVO.getDatasourceType());
        }
        if (pageReqVO.getDatasourceOrigin() != null) {
            configStore.and("datasource_origin", pageReqVO.getDatasourceOrigin());
        }
        if (pageReqVO.getRunMode() != null) {
            configStore.and("run_mode", pageReqVO.getRunMode());
        }
        if (pageReqVO.getAppId() != null) {
            configStore.and("app_id", pageReqVO.getAppId());
        }

        // 分页查询
        return dataRepository.findPageWithConditions(MetadataDatasourceDO.class, configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }

    @Override
    public List<MetadataDatasourceDO> getDatasourceList() {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.order("create_time", Order.TYPE.DESC);
        return dataRepository.findAllByConfig(MetadataDatasourceDO.class, configStore);
    }

    @Override
    public MetadataDatasourceDO getDatasourceByCode(String code) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("code", code);
        return dataRepository.findOne(MetadataDatasourceDO.class, configStore);
    }

    /**
     * 测试数据源连接
     *
     * @param reqVO 测试连接请求参数
     * @return 测试结果，包含连接状态、错误信息和耗时
     */
    @Override
    public DatasourceTestConnectionRespVO testConnection(@Valid DatasourceTestConnectionReqVO reqVO) {
        long startTime = System.currentTimeMillis();

        try {
            // 从配置中获取连接参数
            Map<String, Object> config = reqVO.getConfig();
            String url = (String) config.get("url");
            String username = (String) config.get("username");
            String password = (String) config.get("password");

            // 参数校验
            if (url == null || url.trim().isEmpty()) {
                return DatasourceTestConnectionRespVO.failed("数据源URL不能为空");
            }
            if (username == null || username.trim().isEmpty()) {
                return DatasourceTestConnectionRespVO.failed("用户名不能为空");
            }
            if (password == null) {
                password = ""; // 密码可以为空
            }

            // 测试连接
            boolean connectionOK = testDatabaseConnection(reqVO.getDatasourceType(), url, username, password);

            long duration = System.currentTimeMillis() - startTime;

            if (connectionOK) {
                return DatasourceTestConnectionRespVO.success(duration);
            } else {
                return DatasourceTestConnectionRespVO.failed("连接失败，请检查数据源配置信息");
            }

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("数据源连接测试失败", e);
            DatasourceTestConnectionRespVO respVO = DatasourceTestConnectionRespVO.failed("连接测试异常：" + e.getMessage());
            respVO.setDuration(duration);
            return respVO;
        }
    }

    /**
     * 测试数据库连接
     *
     * @param datasourceType 数据源类型
     * @param url JDBC URL
     * @param username 用户名
     * @param password 密码
     * @return 是否连接成功
     */
    private boolean testDatabaseConnection(String datasourceType, String url, String username, String password) {
        try {
            // 构建数据源配置，不指定连接池类型，使用默认连接池
            Map<String, Object> config = Map.of(
                    "url", url,
                    "user", username,
                    "password", password,
                    "driver", getDriverByType(datasourceType)
                    // 移除 pool 配置，让 AnyLine 使用默认连接池
            );

            // 使用 anyline 的 DataSourceUtil 构建数据源
            DataSource dataSource = DataSourceUtil.build(config);

            // 创建临时的 AnylineService 来测试连接
            AnylineService<?> temporaryService = ServiceProxy.temporary(dataSource);

            // 使用 query 方法执行查询语句，避免框架的租户、软删除等特性影响
            temporaryService.query("SELECT 1");

            return true;
        } catch (Exception e) {
            log.error("数据库连接测试失败: datasourceType={}, url={}, username={}",
                    datasourceType, url, username, e);
            return false;
        }
    }

    /**
     * 根据数据源类型获取对应的驱动类名
     *
     * @param datasourceType 数据源类型
     * @return 驱动类名
     */
    private String getDriverByType(String datasourceType) {
        return switch (datasourceType.toUpperCase()) {
            case "MYSQL" -> "com.mysql.cj.jdbc.Driver";
            case "POSTGRESQL" -> "org.postgresql.Driver";
            case "ORACLE" -> "oracle.jdbc.driver.OracleDriver";
            case "SQLSERVER" -> "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            case "KINGBASE" -> "com.kingbase8.Driver";
            case "TDENGINE" -> "com.taosdata.jdbc.TSDBDriver";
            case "CLICKHOUSE" -> "ru.yandex.clickhouse.ClickHouseDriver";
            case "DM" -> "dm.jdbc.driver.DmDriver";
            case "OPENGAUSS" -> "org.opengauss.Driver";
            case "DB2" -> "com.ibm.db2.jcc.DB2Driver";
            default -> {
                log.warn("未知的数据源类型: {}", datasourceType);
                yield ""; // 返回空字符串作为默认值
            }
        };
    }

}
