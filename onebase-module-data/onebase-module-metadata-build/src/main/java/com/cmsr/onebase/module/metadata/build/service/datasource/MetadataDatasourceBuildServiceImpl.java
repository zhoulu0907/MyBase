package com.cmsr.onebase.module.metadata.build.service.datasource;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.build.controller.admin.datasource.vo.ColumnInfoRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.datasource.vo.DatasourcePageReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.datasource.vo.DatasourceRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.datasource.vo.DatasourceSaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.datasource.vo.DatasourceTestConnectionReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.datasource.vo.DatasourceTestConnectionRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.datasource.vo.DatasourceTypeRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.datasource.vo.TableInfoRespVO;
import com.cmsr.onebase.module.metadata.core.dal.database.TemporaryDatasourceService;
import com.cmsr.onebase.module.metadata.build.service.datasource.vo.ColumnQueryVO;
import com.cmsr.onebase.module.metadata.build.service.datasource.vo.TableQueryVO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.core.config.MetadataConfig;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataDatasourceRepository;
import org.anyline.metadata.type.DatabaseType;
import com.cmsr.onebase.module.metadata.core.service.datasource.MetadataAppAndDatasourceCoreService;
import com.cmsr.onebase.module.metadata.core.service.datasource.MetadataDatasourceCoreService;
import com.cmsr.onebase.framework.security.core.util.SecurityFrameworkUtils;
import com.cmsr.onebase.module.system.api.user.AdminUserApi;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.anyline.metadata.Column;
import org.anyline.metadata.Table;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jakarta.validation.Valid;
import org.anyline.data.jdbc.util.DataSourceUtil;
import org.anyline.proxy.ServiceProxy;
import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.DATASOURCE_NOT_EXISTS;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.DATASOURCE_CODE_DUPLICATE;
import org.anyline.entity.Compare;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataAppAndDatasourceDO;

/**
 * 数据源构建模块服务实现类 - 提供面向VO的业务操作
 *
 * @author matianyu
 * @date 2025-09-12
 */
@Service
@Slf4j
public class MetadataDatasourceBuildServiceImpl implements MetadataDatasourceBuildService {

    @Resource
    private MetadataConfig metadataConfig;
    @Resource
    private ModelMapper modelMapper;
    @Resource
    private MetadataDatasourceRepository metadataDatasourceRepository;
    @Resource
    private TemporaryDatasourceService temporaryDatasourceService;
    @Resource
    private MetadataAppAndDatasourceCoreService appAndDatasourceService;
    @Resource
    private MetadataDatasourceCoreService metadataDatasourceCoreService;
    @Resource
    private AdminUserApi adminUserApi;

    @Override
    public List<DatasourceTypeRespVO> getDatasourceTypes() {
        // 定义支持的数据库类型：PostgreSQL、达梦、人大金仓
        DatabaseType[] supportedTypes = {
                DatabaseType.PostgreSQL,
                DatabaseType.DM,
                DatabaseType.KingBase
        };
        
        return Arrays.stream(supportedTypes)
                .map(this::convertToTypeRespVO)
                .toList();
    }

    @Override
    public List<TableInfoRespVO> getTablesByDatasourceId(TableQueryVO queryVO) {
        // 获取数据源信息
        String datasourceIdStr = queryVO.getDatasourceId();
        Long datasourceId = (datasourceIdStr != null && !datasourceIdStr.trim().isEmpty()) ? Long.valueOf(datasourceIdStr) : null;
        if (datasourceId == null) {
            throw exception(DATASOURCE_NOT_EXISTS);
        }
        MetadataDatasourceDO datasource = getDatasource(datasourceId);
        if (datasource == null) {
            throw exception(DATASOURCE_NOT_EXISTS);
        }

        // 创建临时数据源连接
        AnylineService<?> temporaryService = temporaryDatasourceService.createTemporaryService(datasource);

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
        String datasourceIdStr = queryVO.getDatasourceId();
        Long datasourceId = (datasourceIdStr != null && !datasourceIdStr.trim().isEmpty()) ? Long.valueOf(datasourceIdStr) : null;
        if (datasourceId == null) {
            throw exception(DATASOURCE_NOT_EXISTS);
        }
        MetadataDatasourceDO datasource = getDatasource(datasourceId);
        if (datasource == null) {
            throw exception(DATASOURCE_NOT_EXISTS);
        }

        // 创建临时数据源连接
        AnylineService<?> temporaryService = temporaryDatasourceService.createTemporaryService(datasource);

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
     * 将Anyline数据库类型枚举转换为响应VO
     *
     * @param dbType Anyline数据库类型枚举
     * @return 数据源类型响应VO
     */
    private DatasourceTypeRespVO convertToTypeRespVO(DatabaseType dbType) {
        DatasourceTypeRespVO respVO = new DatasourceTypeRespVO();
        respVO.setDatasourceType(dbType.name());        // 使用枚举的name()作为类型编码
        respVO.setDisplayName(dbType.title());           // 使用title()作为显示名称
        respVO.setDescription("支持 " + dbType.title() + " 数据库");
        respVO.setDefaultPort(null);                     // 不提供默认端口，要求用户必须输入
        respVO.setJdbcDriverClass(dbType.driver());      // 使用Anyline的driver()方法
        respVO.setUrlTemplate(dbType.url());             // 使用Anyline的url()方法
        // 所有数据源类型都支持读写和模式发现功能
        respVO.setSupportFeatures(Arrays.asList("READ", "WRITE", "SCHEMA_DISCOVERY"));
        return respVO;
    }

    @Transactional(rollbackFor = Exception.class)
    public Long createDatasource(@Valid MetadataDatasourceDO datasource) {
        // 使用 core 模块的基础服务
        return metadataDatasourceCoreService.createDatasource(datasource);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDatasource(@Valid DatasourceSaveReqVO createReqVO) {
        // 校验编码唯一性（创建时ID为null，所以传null；appId需要安全转换）
        Long id = (createReqVO.getId() != null && !createReqVO.getId().trim().isEmpty()) ? Long.valueOf(createReqVO.getId()) : null;
        Long appId = (createReqVO.getAppId() != null && !createReqVO.getAppId().trim().isEmpty()) ? Long.valueOf(createReqVO.getAppId()) : null;
        validateDatasourceCodeUnique(id, createReqVO.getCode(), appId);

        // 转换VO为DO
        MetadataDatasourceDO datasource = modelMapper.map(createReqVO, MetadataDatasourceDO.class);

        // 使用 core 模块基础服务创建数据源
        Long datasourceId = createDatasource(datasource);

        // 创建应用与数据源的关联关系（使用之前安全转换的appId）
        if (appId == null) {
            throw new IllegalArgumentException("应用ID不能为空");
        }
        metadataDatasourceCoreService.createAppDatasourceRelation(appId, datasourceId,
                datasource.getDatasourceType(), createReqVO.getAppUid());

        log.info("创建数据源成功，ID: {}，应用ID: {}", datasourceId, appId);
        return datasourceId;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateDatasource(@Valid MetadataDatasourceDO datasource) {
        // 使用 core 模块的基础服务
        metadataDatasourceCoreService.updateDatasource(datasource);
    }

    @Transactional(rollbackFor = Exception.class)
    public Long createDefaultDatasource(Long appId, String appUid, String datasourceType, String configJson) {
        // 使用 core 模块的基础服务
        return metadataDatasourceCoreService.createDefaultDatasource(appId, appUid, datasourceType, configJson);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createAppDatasourceRelation(Long appId, Long datasourceId, String datasourceType, String appUid) {
        // 使用 core 模块的基础服务
        metadataDatasourceCoreService.createAppDatasourceRelation(appId, datasourceId, datasourceType, appUid);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDefaultDatasource(Long appId, String appUid) {
        // 从配置类中读取默认数据源参数

        // 构造配置 map
        java.util.Map<String, Object> config = new java.util.HashMap<>();
        config.put("host", metadataConfig.getDefaultDatasourceHost());
        config.put("port", metadataConfig.getDefaultDatasourcePort());
        config.put("database", metadataConfig.getDefaultDatasourceDatabase());
        config.put("username", metadataConfig.getDefaultDatasourceUsername());
        config.put("password", metadataConfig.getDefaultDatasourcePassword());

        // 构造保存请求
        DatasourceSaveReqVO reqVO = new DatasourceSaveReqVO();
        reqVO.setDatasourceName(metadataConfig.getDefaultDatasourceDatabase());
        reqVO.setCode(metadataConfig.getDefaultDatasourceDatabase());
        reqVO.setDatasourceType(metadataConfig.getDefaultDatasourceType());
        reqVO.setConfig(config);
        reqVO.setDescription(metadataConfig.getDefaultDatasourceDescription());
        reqVO.setRunMode(metadataConfig.getDefaultDatasourceRunMode());
        reqVO.setDatasourceOrigin(metadataConfig.getDefaultDatasourceDatasourceOrigin());
        reqVO.setAppId(String.valueOf(appId));
        // 关键：传递 appUid，确保关联表插入不为 null
        reqVO.setAppUid(appUid);

        // 生成唯一的数据源编码，避免重复
        String uniqueCode = metadataConfig.getDefaultDatasourceDatabase() + "_" + UUID.randomUUID().toString().replace("-", "");
        reqVO.setCode(uniqueCode);

        // 调用已有创建方法
        return createDatasource(reqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDatasource(@Valid DatasourceSaveReqVO updateReqVO) {
        // 安全转换ID和AppID
        Long id = (updateReqVO.getId() != null && !updateReqVO.getId().trim().isEmpty()) ? Long.valueOf(updateReqVO.getId()) : null;
        Long appId = (updateReqVO.getAppId() != null && !updateReqVO.getAppId().trim().isEmpty()) ? Long.valueOf(updateReqVO.getAppId()) : null;

        // 校验存在
        if (id == null) {
            throw new IllegalArgumentException("更新数据源时ID不能为空");
        }
        validateDatasourceExists(id);
        // 校验编码唯一性
        validateDatasourceCodeUnique(id, updateReqVO.getCode(), appId);

        // 更新数据源（不再设置appId，因为关联关系在单独的表中维护）
        MetadataDatasourceDO updateObj = modelMapper.map(updateReqVO, MetadataDatasourceDO.class);
        // 手动设置ID，确保更新操作正常进行（使用之前安全转换的id）
        updateObj.setId(id);

        // 设置更新人和更新时间
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        if (currentUserId != null) {
            updateObj.setUpdater(currentUserId);
        }
        updateObj.setUpdateTime(LocalDateTime.now());

        // 不再设置appId，因为关联关系由关联表维护
        metadataDatasourceRepository.update(updateObj);

        // 如果 appUid 发生变化，同步更新关联表（使用之前安全转换的id和appId）
        String newAppUid = updateReqVO.getAppUid();

        // 获取当前关联关系
        if (appId != null) {
            MetadataAppAndDatasourceDO currentRelation = appAndDatasourceService.getRelation(appId, id);
            if (currentRelation != null && !newAppUid.equals(currentRelation.getAppUid())) {
                // appUid 发生变化，更新关联表
                log.info("数据源{}的appUid从{}更新为{}，同步更新关联表", id, currentRelation.getAppUid(), newAppUid);
                appAndDatasourceService.updateRelationAppUid(appId, id, newAppUid);
            }
        }

        log.info("更新数据源成功，ID: {}，更新人: {}", updateReqVO.getId(), currentUserId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDatasource(Long id) {
        // 校验存在
        validateDatasourceExists(id);

        // 删除数据源关联关系
        long deletedRelations = appAndDatasourceService.deleteRelationsByDatasourceId(id);
        log.info("删除数据源{}相关联的关系数量: {}", id, deletedRelations);

        // 删除数据源
        metadataDatasourceRepository.deleteById(id);
        log.info("删除数据源成功，ID: {}", id);
    }

    private void validateDatasourceExists(Long id) {
        if (metadataDatasourceRepository.findById(id) == null) {
            throw exception(DATASOURCE_NOT_EXISTS);
        }
    }

    private void validateDatasourceCodeUnique(Long id, String code, Long appId) {
        // 获取同一应用下的所有数据源
        List<MetadataDatasourceDO> appDatasources = appAndDatasourceService.getDatasourcesByApplicationId(appId);

        // 检查编码是否重复
        boolean isDuplicate = appDatasources.stream()
                .filter(datasource -> !datasource.getId().equals(id)) // 排除自身
                .anyMatch(datasource -> code.equals(datasource.getCode()));

        if (isDuplicate) {
            throw exception(DATASOURCE_CODE_DUPLICATE);
        }
    }

    @Override
    public MetadataDatasourceDO getDatasource(Long id) {
        // 使用 core 模块的基础服务
        return metadataDatasourceCoreService.getDatasource(id);
    }

    @Override
    public PageResult<MetadataDatasourceDO> getDatasourcePage(DatasourcePageReqVO pageReqVO) {
        // 如果指定了应用ID，需要通过关联表查询
        if (pageReqVO.getAppId() != null && !pageReqVO.getAppId().trim().isEmpty()) {
            Long appId;
            try {
                appId = Long.valueOf(pageReqVO.getAppId());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("应用ID格式不正确: " + pageReqVO.getAppId());
            }

            // 获取应用关联的数据源列表
            List<MetadataDatasourceDO> appDatasources = appAndDatasourceService.getDatasourcesByApplicationId(appId);

            // 基于获取的数据源进行进一步过滤
            List<MetadataDatasourceDO> filteredDatasources = appDatasources.stream()
                    .filter(datasource -> {
                        // 应用各种过滤条件
                        if (pageReqVO.getDatasourceName() != null &&
                            !datasource.getDatasourceName().contains(pageReqVO.getDatasourceName())) {
                            return false;
                        }
                        if (pageReqVO.getCode() != null &&
                            !datasource.getCode().contains(pageReqVO.getCode())) {
                            return false;
                        }
                        if (pageReqVO.getDatasourceType() != null &&
                            !pageReqVO.getDatasourceType().equals(datasource.getDatasourceType())) {
                            return false;
                        }
                        if (pageReqVO.getRunMode() != null &&
                            !pageReqVO.getRunMode().equals(datasource.getRunMode())) {
                            return false;
                        }
                        // TODO: 支持数据源来源过滤
                        // 当前：缺少datasourceOrigin字段支持
                        // 需要：1.在MetadataDatasourceDO中添加datasourceOrigin字段
                        //      2.在数据库表中添加对应字段
                        //      3.在此处添加过滤逻辑
                        // if (pageReqVO.getDatasourceOrigin() != null &&
                        //     !pageReqVO.getDatasourceOrigin().equals(datasource.getDatasourceOrigin())) {
                        //     return false;
                        // }
                        return true;
                    })
                    .toList();

            // 手动实现分页
            int pageNo = pageReqVO.getPageNo();
            int pageSize = pageReqVO.getPageSize();
            int startIndex = (pageNo - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, filteredDatasources.size());

            List<MetadataDatasourceDO> pageData = filteredDatasources.subList(
                Math.max(0, startIndex),
                Math.max(0, endIndex)
            );

            return new PageResult<>(pageData, (long) filteredDatasources.size());
        }

        // 如果没有指定应用ID，查询所有数据源
        DefaultConfigStore configStore = new DefaultConfigStore();

        // 添加查询条件
        if (pageReqVO.getDatasourceName() != null) {
            configStore.and(Compare.LIKE, MetadataDatasourceDO.DATASOURCE_NAME, "%" + pageReqVO.getDatasourceName() + "%");
        }
        if (pageReqVO.getCode() != null) {
            configStore.and(Compare.LIKE, MetadataDatasourceDO.CODE, "%" + pageReqVO.getCode() + "%");
        }
        if (pageReqVO.getDatasourceType() != null) {
            configStore.and(MetadataDatasourceDO.DATASOURCE_TYPE, pageReqVO.getDatasourceType());
        }
        if (pageReqVO.getDatasourceOrigin() != null) {
            configStore.and(MetadataDatasourceDO.DATASOURCE_ORIGIN, pageReqVO.getDatasourceOrigin());
        }
        if (pageReqVO.getRunMode() != null) {
            configStore.and(MetadataDatasourceDO.RUN_MODE, pageReqVO.getRunMode());
        }

        // 分页查询
        return metadataDatasourceRepository.findPageWithConditions(configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }

    @Override
    public List<MetadataDatasourceDO> getDatasourceList() {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.order("create_time", Order.TYPE.DESC);
        return metadataDatasourceRepository.findAllByConfig(configStore);
    }

    @Override
    public List<MetadataDatasourceDO> getDatasourceListByAppId(Long appId) {
        // 使用新的关联服务查询应用关联的数据源
        return appAndDatasourceService.getDatasourcesByApplicationId(appId);
    }

    @Override
    public MetadataDatasourceDO getDatasourceByCode(String code) {
        // 使用 core 模块的基础服务
        return metadataDatasourceCoreService.getDatasourceByCode(code);
    }

    @Override
    public List<MetadataDatasourceDO> findAllByConfig(DefaultConfigStore configStore) {
        return metadataDatasourceRepository.findAllByConfig(configStore);
    }

    @Override
    public List<DatasourceRespVO> buildDatasourceRespVOList(List<MetadataDatasourceDO> datasourceList) {
        if (datasourceList == null || datasourceList.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> userIds = datasourceList.stream()
                .flatMap(datasource -> Stream.of(datasource.getCreator(), datasource.getUpdater()))
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(HashSet::new));

        Map<Long, AdminUserRespDTO> userMap = Collections.emptyMap();
        if (!userIds.isEmpty()) {
            Map<Long, AdminUserRespDTO> fetchedUserMap = adminUserApi.getUserMap(userIds);
            if (fetchedUserMap != null) {
                userMap = fetchedUserMap;
            }
        }

        Map<Long, AdminUserRespDTO> finalUserMap = userMap;
        return datasourceList.stream()
                .map(datasource -> convertDatasource(datasource, finalUserMap))
                .toList();
    }

    @Override
    public DatasourceRespVO buildDatasourceRespVO(MetadataDatasourceDO datasource) {
        if (datasource == null) {
            return null;
        }
        List<DatasourceRespVO> respVOList = buildDatasourceRespVOList(Collections.singletonList(datasource));
        return respVOList.isEmpty() ? null : respVOList.get(0);
    }

    private DatasourceRespVO convertDatasource(MetadataDatasourceDO datasource, Map<Long, AdminUserRespDTO> userMap) {
        DatasourceRespVO respVO = modelMapper.map(datasource, DatasourceRespVO.class);

        Long creatorId = datasource.getCreator();
        if (creatorId != null) {
            AdminUserRespDTO user = userMap.get(creatorId);
            respVO.setCreatorId(creatorId);
            respVO.setCreator(user != null ? user.getNickname() : String.valueOf(creatorId));
        } else {
            respVO.setCreatorId(null);
            respVO.setCreator(null);
        }

        Long updaterId = datasource.getUpdater();
        if (updaterId != null) {
            AdminUserRespDTO user = userMap.get(updaterId);
            respVO.setUpdaterId(updaterId);
            respVO.setUpdater(user != null ? user.getNickname() : String.valueOf(updaterId));
        } else {
            respVO.setUpdaterId(null);
            respVO.setUpdater(null);
        }

        return respVO;
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
            // 根据数据库类型使用不同的测试查询
            String testQuery = getTestQueryByType(datasourceType);
            temporaryService.query(testQuery);

            return true;
        } catch (Exception e) {
            log.error("数据库连接测试失败: datasourceType={}, url={}, username={}",
                    datasourceType, url, username, e);
            return false;
        }
    }

    /**
     * 根据数据源类型获取对应的驱动类名
     * 直接使用Anyline的DatabaseType枚举，不支持的类型将抛出异常
     *
     * @param datasourceType 数据源类型字符串
     * @return 驱动类名
     * @throws IllegalArgumentException 如果数据源类型不被支持
     */
    private String getDriverByType(String datasourceType) {
        DatabaseType dbType = DatabaseType.valueOf(datasourceType);
        return dbType.driver();
    }

    /**
     * 根据数据库类型获取测试查询SQL
     * <p>
     * 不同数据库使用不同的测试查询语句：
     * - PostgreSQL/KingBase/MySQL: SELECT 1
     * - DM(达梦)/Oracle: SELECT 1 FROM DUAL
     * <p>
     * 复用TemporaryDatasourceService的逻辑，保持一致性
     *
     * @param datasourceType 数据源类型字符串，如"PostgreSQL"、"DM"、"KingBase"
     * @return 测试查询SQL
     */
    private String getTestQueryByType(String datasourceType) {
        try {
            DatabaseType dbType = DatabaseType.valueOf(datasourceType);
            
            switch (dbType) {
                case DM:
                case ORACLE:
                    // 达梦和Oracle使用FROM DUAL
                    return "SELECT 1 FROM DUAL";
                case PostgreSQL:
                case KingBase:
                case MySQL:
                default:
                    // PostgreSQL、金仓、MySQL等使用简单的SELECT 1
                    return "SELECT 1";
            }
        } catch (IllegalArgumentException e) {
            // 如果数据库类型无法识别，使用最通用的查询
            log.warn("无法识别的数据库类型[{}]，使用默认测试查询SELECT 1", datasourceType);
            return "SELECT 1";
        }
    }

}
