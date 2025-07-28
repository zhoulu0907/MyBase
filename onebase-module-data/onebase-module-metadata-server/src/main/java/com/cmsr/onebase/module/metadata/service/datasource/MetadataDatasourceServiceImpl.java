package com.cmsr.onebase.module.metadata.service.datasource;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.controller.admin.datasource.vo.DatasourcePageReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.datasource.vo.DatasourceSaveReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.datasource.vo.DatasourceTestConnectionReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.datasource.vo.DatasourceTestConnectionRespVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.datasource.MetadataDatasourceDO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.jdbc.util.DataSourceUtil;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.anyline.proxy.ServiceProxy;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.enums.ErrorCodeConstants.DATASOURCE_NOT_EXISTS;
import static com.cmsr.onebase.module.metadata.enums.ErrorCodeConstants.DATASOURCE_CODE_DUPLICATE;

/**
 * 数据源 Service 实现类
 */
@Service
@Slf4j
public class MetadataDatasourceServiceImpl implements MetadataDatasourceService {

    @Resource
    private DataRepository dataRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDatasource(@Valid DatasourceSaveReqVO createReqVO) {
        // 校验编码唯一性
        validateDatasourceCodeUnique(null, createReqVO.getCode(), createReqVO.getAppId());

        // 插入数据源
        MetadataDatasourceDO datasource = BeanUtils.toBean(createReqVO, MetadataDatasourceDO.class);
        dataRepository.insert(datasource);
        
        return datasource.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDatasource(@Valid DatasourceSaveReqVO updateReqVO) {
        // 校验存在
        validateDatasourceExists(updateReqVO.getId());
        // 校验编码唯一性
        validateDatasourceCodeUnique(updateReqVO.getId(), updateReqVO.getCode(), updateReqVO.getAppId());

        // 更新数据源
        MetadataDatasourceDO updateObj = BeanUtils.toBean(updateReqVO, MetadataDatasourceDO.class);
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
            configStore.and("id", "!=", id);
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
            configStore.and("datasource_name", "LIKE", "%" + pageReqVO.getDatasourceName() + "%");
        }
        if (pageReqVO.getCode() != null) {
            configStore.and("code", "LIKE", "%" + pageReqVO.getCode() + "%");
        }
        if (pageReqVO.getDatasourceType() != null) {
            configStore.and("datasource_type", pageReqVO.getDatasourceType());
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
            // 构建数据源配置，添加连接池类型
            Map<String, Object> config = Map.of(
                    "url", url,
                    "user", username,
                    "password", password,
                    "driver", getDriverByType(datasourceType),
                    "pool", "com.zaxxer.hikari.HikariDataSource"  // 指定连接池类型
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
     * 根据数据源类型获取驱动类名
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
