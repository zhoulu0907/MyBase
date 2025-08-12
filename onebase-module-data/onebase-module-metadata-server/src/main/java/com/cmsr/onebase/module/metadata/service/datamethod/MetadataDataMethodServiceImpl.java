package com.cmsr.onebase.module.metadata.service.datamethod;

import cn.hutool.core.util.IdUtil;
import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo.*;
import com.cmsr.onebase.module.metadata.service.datamethod.vo.DataMethodQueryVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.method.MetadataDataSystemMethodDO;
import com.cmsr.onebase.module.metadata.dal.database.MetadataBusinessEntityRepository;
import com.cmsr.onebase.module.metadata.dal.database.MetadataDataSystemMethodRepository;
import com.cmsr.onebase.module.metadata.dal.database.MetadataDatasourceRepository;
import com.cmsr.onebase.module.metadata.dal.database.MetadataEntityFieldRepository;
import com.cmsr.onebase.module.metadata.dal.database.TemporaryDatasourceService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.entity.DataRow;
import org.anyline.entity.DataSet;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.data.param.ConfigStore;
import org.anyline.entity.Compare;
import org.anyline.entity.Order;

import org.anyline.service.AnylineService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.enums.ErrorCodeConstants.*;

/**
 * 数据方法 Service 实现类
 *
 * @author bty418
 * @date 2025-01-25
 */
@Service
@Slf4j
public class MetadataDataMethodServiceImpl implements MetadataDataMethodService {

    @Resource
    private MetadataBusinessEntityRepository metadataBusinessEntityRepository;

    @Resource
    private MetadataDataSystemMethodRepository metadataDataSystemMethodRepository;

    @Resource
    private MetadataDatasourceRepository metadataDatasourceRepository;

    @Resource
    private MetadataEntityFieldRepository metadataEntityFieldRepository;

    @Resource
    private AnylineService<?> anylineService;

    @Resource
    private TemporaryDatasourceService temporaryDatasourceService;

    @Override
    public List<DataMethodRespVO> getDataMethodList(DataMethodQueryVO queryVO) {
        // 校验实体存在
        MetadataBusinessEntityDO entity = metadataBusinessEntityRepository.findById(Long.valueOf(queryVO.getEntityId()));
        if (entity == null) {
            throw exception(BUSINESS_ENTITY_NOT_EXISTS);
        }

        // 构建查询条件
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("is_enabled", CommonStatusEnum.ENABLE.getStatus()); // 只获取启用的数据方法（0-启用，1-禁用）
        configStore.and("deleted", 0);

        // 根据条件过滤
        if (StringUtils.hasText(queryVO.getMethodType())) {
            configStore.and("method_type", queryVO.getMethodType());
        }

        if (StringUtils.hasText(queryVO.getKeyword())) {
            configStore.and("method_name", "%" + queryVO.getKeyword() + "%", "like");
        }

        // 添加排序
        configStore.order("method_code", Order.TYPE.ASC);

        // 从数据库查询数据方法
        List<MetadataDataSystemMethodDO> methodDOList = metadataDataSystemMethodRepository.getEnabledDataMethodList();

        // 转换为响应VO
        List<DataMethodRespVO> methods = new ArrayList<>();
        for (MetadataDataSystemMethodDO methodDO : methodDOList) {
            DataMethodRespVO method = new DataMethodRespVO();
            method.setMethodName(methodDO.getMethodName());
            method.setMethodCode(methodDO.getMethodCode());
            method.setMethodType(methodDO.getMethodType());
            method.setUrl(methodDO.getMethodUrl());
            method.setHttpMethod(methodDO.getRequestMethod());
            method.setDescription(methodDO.getMethodDescription());
            method.setId(methodDO.getId().toString());
            // 设置输入输出参数（这里可以根据需要进一步扩展）
            method.setInputParameters(new ArrayList<>());
            method.setOutputParameters(new DataMethodOutputParameterVO());

            methods.add(method);
        }

        return methods;
    }

    @Override
    public DataMethodDetailRespVO getDataMethodDetail(Long entityId, String methodCode) {
        // 校验实体存在
        MetadataBusinessEntityDO entity = metadataBusinessEntityRepository.findById(entityId);
        if (entity == null) {
            throw exception(BUSINESS_ENTITY_NOT_EXISTS);
        }

        // 根据方法代码查询数据方法
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("method_code", methodCode);
        configStore.and("is_enabled", CommonStatusEnum.ENABLE.getStatus()); // 只获取启用的数据方法（0-启用，1-禁用）
        configStore.and("deleted", 0);

        MetadataDataSystemMethodDO methodDO = metadataDataSystemMethodRepository.getDataMethodByCode(methodCode);
        if (methodDO == null) {
            throw exception(DATA_METHOD_NOT_EXISTS);
        }

        // 构建详情响应
        DataMethodDetailRespVO detail = new DataMethodDetailRespVO();
        detail.setMethodName(methodDO.getMethodName());
        detail.setMethodCode(methodDO.getMethodCode());
        detail.setMethodType(methodDO.getMethodType());
        detail.setUrl(methodDO.getMethodUrl());
        detail.setHttpMethod(methodDO.getRequestMethod());
        detail.setDescription(methodDO.getMethodDescription());

        // 设置输入输出参数（可以根据需要进一步完善）
        detail.setInputParameters(new ArrayList<>());
        detail.setOutputParameters(new DataMethodDetailOutputParameterVO());
        detail.setRequestExample(null);
        detail.setResponseExample(null);

        return detail;
    }

    // ========== 系统级别的动态数据操作方法实现 ==========

    @Override
    public DynamicDataRespVO createData(DynamicDataCreateReqVO reqVO) {
        // 1. 校验实体存在
        MetadataBusinessEntityDO entity = validateEntityExists(Long.valueOf(reqVO.getEntityId()));

        // 2. 获取实体字段信息
        List<MetadataEntityFieldDO> fields = getEntityFields(Long.valueOf(reqVO.getEntityId()));

        // 3. 校验数据完整性
        validateDataForCreate(reqVO.getData(), fields);

        // 4. 处理数据并设置默认值
        Map<String, Object> processedData = processDataForCreate(reqVO.getData(), fields);

        // 5. 使用Anyline执行插入操作
        // 切换到指定数据源
        switchToDataSource(entity.getDatasourceId());

        // 执行插入
        DataRow dataRow = new DataRow(processedData);
        Object insertResult = anylineService.insert(entity.getTableName(), dataRow);
        log.info("创建数据成功，实体ID: {}, 表名: {}, 插入结果: {}", reqVO.getEntityId(), entity.getTableName(), insertResult);

        // 6. 查询插入后的完整数据 
        Object primaryKeyValue;
        
        // 优先使用插入操作返回的ID，通常这是影响的行数，我们需要使用实际的主键值
        primaryKeyValue = getPrimaryKeyValue(processedData, fields);
        log.info("从处理数据中获取主键值: {}, 插入结果: {}", primaryKeyValue, insertResult);
        
        // 确保主键值不为null
        if (primaryKeyValue == null) {
            log.warn("无法获取主键值，跳过查询插入后的数据，实体ID: {}, 表名: {}", reqVO.getEntityId(), entity.getTableName());
            // 返回插入的数据
            return buildDynamicDataRespVO(entity, processedData);
        }

        Map<String, Object> resultData = queryDataById(entity.getTableName(), primaryKeyValue, fields);

        // 7. 构建响应
        return buildDynamicDataRespVO(entity, resultData);
    }

    @Override
    public DynamicDataRespVO updateData(DynamicDataUpdateReqVO reqVO) {
        // 1. 校验实体存在
        MetadataBusinessEntityDO entity = validateEntityExists(Long.valueOf(reqVO.getEntityId()));

        // 2. 获取实体字段信息
        List<MetadataEntityFieldDO> fields = getEntityFields(Long.valueOf(reqVO.getEntityId()));

        // 3. 校验数据存在
        validateDataExists(entity.getTableName(), reqVO.getId(), fields);

        // 4. 校验更新数据
        validateDataForUpdate(reqVO.getData(), fields);

        // 5. 处理更新数据
        Map<String, Object> processedData = processDataForUpdate(reqVO.getData(), fields);

        // 6. 使用Anyline执行更新操作
        // 切换到指定数据源
        switchToDataSource(entity.getDatasourceId());

        // 获取主键字段名
        String primaryKeyField = getPrimaryKeyFieldName(fields);

        // 构建更新条件
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(primaryKeyField, reqVO.getId());

        // 执行更新
        DataRow dataRow = new DataRow(processedData);
        long updateCount = anylineService.update(entity.getTableName(), dataRow, configStore);
        log.info("更新数据成功，实体ID: {}, 表名: {}, 更新记录数: {}", reqVO.getEntityId(), entity.getTableName(), updateCount);

        // 7. 查询更新后的完整数据
        Map<String, Object> resultData = queryDataById(entity.getTableName(), reqVO.getId(), fields);

        // 8. 构建响应
        return buildDynamicDataRespVO(entity, resultData);
    }

    @Override
    public Boolean deleteData(DynamicDataDeleteReqVO reqVO) {
        // 1. 校验实体存在
        MetadataBusinessEntityDO entity = validateEntityExists(Long.valueOf(reqVO.getEntityId()));

        // 2. 获取实体字段信息
        List<MetadataEntityFieldDO> fields = getEntityFields(Long.valueOf(reqVO.getEntityId()));

        // 3. 校验数据存在
        validateDataExists(entity.getTableName(), reqVO.getId(), fields);

        // 4. 使用Anyline执行删除操作
        // 切换到指定数据源
        switchToDataSource(entity.getDatasourceId());

        // 获取主键字段名
        String primaryKeyField = getPrimaryKeyFieldName(fields);

        // 构建删除条件
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(primaryKeyField, reqVO.getId());

        // 执行删除
        long deleteCount = anylineService.delete(entity.getTableName(), configStore);
        log.info("删除数据成功，实体ID: {}, 表名: {}, 删除记录数: {}", reqVO.getEntityId(), entity.getTableName(), deleteCount);

        return deleteCount > 0;
    }

    @Override
    public DynamicDataRespVO getData(DynamicDataGetReqVO reqVO) {
        // 1. 校验实体存在
        MetadataBusinessEntityDO entity = validateEntityExists(Long.valueOf(reqVO.getEntityId()));

        // 2. 获取实体字段信息
        List<MetadataEntityFieldDO> fields = getEntityFields(Long.valueOf(reqVO.getEntityId()));

        // 3. 使用Anyline查询数据
        // 切换到指定数据源
        switchToDataSource(entity.getDatasourceId());

        // 查询数据
        Map<String, Object> resultData = queryDataById(entity.getTableName(), reqVO.getId(), fields);

        if (resultData == null || resultData.isEmpty()) {
            throw exception(DATA_NOT_EXISTS);
        }

        // 4. 构建响应
        return buildDynamicDataRespVO(entity, resultData);
    }

    @Override
    public PageResult<DynamicDataRespVO> getDataPage(DynamicDataPageReqVO reqVO) {
        // 1. 校验实体存在
        MetadataBusinessEntityDO entity = validateEntityExists(Long.valueOf(reqVO.getEntityId()));

        // 2. 获取实体字段信息
        List<MetadataEntityFieldDO> fields = getEntityFields(Long.valueOf(reqVO.getEntityId()));

        // 3. 使用Anyline执行分页查询
        // 获取临时数据源服务
        MetadataDatasourceDO datasource = metadataDatasourceRepository.findById(entity.getDatasourceId());
        if (datasource == null) {
            throw exception(DATASOURCE_NOT_EXISTS);
        }
        
        AnylineService<?> temporaryService = temporaryDatasourceService.createTemporaryService(datasource);
        log.info("成功切换到数据源：{}", datasource.getCode());

        // 检查表中是否有软删除字段
        boolean hasDeletedField = fields.stream().anyMatch(f -> "deleted".equalsIgnoreCase(f.getFieldName()));

        // 动态业务表忽略租户条件 - 使用TenantUtils.executeIgnore包装查询
        return TenantUtils.executeIgnore(() -> {

        // 使用ConfigStore替代SQL字符串拼接
        ConfigStore configs = new DefaultConfigStore();
        
        // 标记是否已添加deleted条件，避免重复
        boolean deletedConditionAdded = false;
        
        // 添加用户筛选条件（只对存在的字段进行过滤，排除系统字段）
        if (reqVO.getFilters() != null && !reqVO.getFilters().isEmpty()) {
            // 创建字段名集合用于快速查找
            Set<String> fieldNames = fields.stream()
                    .map(MetadataEntityFieldDO::getFieldName)
                    .collect(Collectors.toSet());
            
            for (Map.Entry<String, Object> entry : reqVO.getFilters().entrySet()) {
                String fieldName = entry.getKey();
                Object fieldValue = entry.getValue();
                
                // 跳过系统字段，避免重复添加deleted等条件
                if ("deleted".equalsIgnoreCase(fieldName) || "tenant_id".equalsIgnoreCase(fieldName)) {
                    log.debug("跳过系统字段过滤条件: {} = {}", fieldName, fieldValue);
                    continue;
                }
                
                // 只对表中实际存在的字段进行过滤
                if (fieldValue != null && fieldNames.contains(fieldName)) {
                    configs.and(Compare.EQUAL, fieldName, fieldValue);
                    log.debug("添加过滤条件: {} = {}", fieldName, fieldValue);
                } else if (fieldValue != null) {
                    log.warn("忽略不存在的字段过滤条件: {} = {}", fieldName, fieldValue);
                }
            }
        }

        // 添加软删除条件（如果字段存在且未添加）
        if (hasDeletedField && !deletedConditionAdded) {
            configs.and(Compare.EQUAL, "deleted", "0");
            deletedConditionAdded = true;
            log.debug("添加软删除条件: deleted = 0");
        }

        log.info("详细配置信息: {}", configs.toString());

        // 不添加租户条件，因为业务表可能没有tenant_id字段
        // if (hasTenantIdField) {
        //     configs.and(Compare.EQUAL, "tenant_id", 1L);
        // }

        // 添加排序（只对存在的字段进行排序）
        Set<String> fieldNames = fields.stream()
                .map(MetadataEntityFieldDO::getFieldName)
                .collect(Collectors.toSet());
                
        if (StringUtils.hasText(reqVO.getSortField()) && fieldNames.contains(reqVO.getSortField())) {
            String orderClause = reqVO.getSortField();
            if ("desc".equalsIgnoreCase(reqVO.getSortDirection())) {
                orderClause += " DESC";
            } else {
                orderClause += " ASC";
            }
            configs.order(orderClause);
            log.debug("添加排序条件: {} {}", reqVO.getSortField(), reqVO.getSortDirection());
        } else {
            // 默认按主键倒序
            String primaryKeyField = getPrimaryKeyFieldName(fields);
            configs.order(primaryKeyField + " DESC");
            if (StringUtils.hasText(reqVO.getSortField())) {
                log.warn("忽略不存在的排序字段: {}", reqVO.getSortField());
            }
        }

        // 添加分页 - 手动计算OFFSET，确保从0开始
        if (reqVO.getPageNo() != null && reqVO.getPageSize() != null) {
            // 计算正确的offset：第1页从0开始，第2页从pageSize开始
            int offset = (reqVO.getPageNo() - 1) * reqVO.getPageSize();
            configs.scope(offset, reqVO.getPageSize());
            log.debug("分页参数: pageNo={}, pageSize={}, offset={}", reqVO.getPageNo(), reqVO.getPageSize(), offset);
        }

        log.info("使用ConfigStore查询表: {}", entity.getTableName());
        log.info("ConfigStore配置: {}", configs.toString());

        // 使用临时服务执行查询
        DataSet dataSet = temporaryService.querys(entity.getTableName(), configs);

        // 获取总记录数和当前页数据
        long total = dataSet.total();  // ConfigStore分页后会自动设置总数
        
        // 转换结果
        List<DynamicDataRespVO> list = new ArrayList<>();
        for (int i = 0; i < dataSet.size(); i++) {
            DataRow row = dataSet.getRow(i);
            Map<String, Object> data = convertDataRowToMap(row, fields);
            list.add(buildDynamicDataRespVO(entity, data));
        }
        
        log.info("分页查询数据成功，实体ID: {}, 表名: {}, 页码: {}, 页大小: {}, 总记录数: {}",
                reqVO.getEntityId(), entity.getTableName(), reqVO.getPageNo(), reqVO.getPageSize(), total);

        return new PageResult<>(list, total);
        
        }); // TenantUtils.executeIgnore 闭合
    }

    // ========== 私有辅助方法 ==========

    /**
     * 校验实体存在
     */
    private MetadataBusinessEntityDO validateEntityExists(Long entityId) {
        MetadataBusinessEntityDO entity = metadataBusinessEntityRepository.findById(entityId);
        if (entity == null) {
            throw exception(BUSINESS_ENTITY_NOT_EXISTS);
        }
        return entity;
    }

    /**
     * 获取实体字段
     */
    private List<MetadataEntityFieldDO> getEntityFields(Long entityId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("entity_id", entityId);
        configStore.and("deleted", 0);
        List<MetadataEntityFieldDO> fields = metadataEntityFieldRepository.getEntityFieldListByEntityId(entityId);
        if (fields == null || fields.isEmpty()) {
            throw exception(ENTITY_FIELD_NOT_EXISTS);
        }
        return fields;
    }

    /**
     * 切换到指定数据源
     */
    private void switchToDataSource(Long datasourceId) {
        if (datasourceId != null) {
            MetadataDatasourceDO datasource = metadataDatasourceRepository.findById(datasourceId);
            if (datasource != null) {
                try {
                    // 使用临时数据源服务创建新的AnylineService实例
                    AnylineService<?> temporaryService = temporaryDatasourceService.createTemporaryService(datasource);
                    
                    // 替换当前的anylineService实例
                    this.anylineService = temporaryService;
                    
                    log.info("成功切换到数据源：{}", datasource.getCode());
                } catch (Exception e) {
                    log.error("切换数据源失败，使用默认数据源：{}", e.getMessage(), e);
                    throw new RuntimeException("切换数据源失败: " + e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 校验创建数据的完整性
     */
    private void validateDataForCreate(Map<String, Object> data, List<MetadataEntityFieldDO> fields) {
        for (MetadataEntityFieldDO field : fields) {
            // 跳过系统字段和主键字段
            if (field.getIsSystemField() || field.getIsPrimaryKey()) {
                continue;
            }

            // 校验必填字段
            if (field.getIsRequired() && (data.get(field.getFieldName()) == null ||
                    String.valueOf(data.get(field.getFieldName())).trim().isEmpty())) {
                throw exception(FIELD_REQUIRED, field.getDisplayName());
            }
        }
    }

    /**
     * 校验更新数据
     */
    private void validateDataForUpdate(Map<String, Object> data, List<MetadataEntityFieldDO> fields) {
        // 更新时不校验必填，只校验数据类型等
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String fieldName = entry.getKey();
            MetadataEntityFieldDO field = fields.stream()
                    .filter(f -> f.getFieldName().equals(fieldName))
                    .findFirst()
                    .orElse(null);

            if (field == null) {
                throw exception(FIELD_NOT_EXISTS, fieldName);
            }

            // 不允许更新主键字段
            if (field.getIsPrimaryKey()) {
                throw exception(PRIMARY_KEY_UPDATE_NOT_ALLOWED);
            }
        }
    }

    /**
     * 处理创建数据
     */
    private Map<String, Object> processDataForCreate(Map<String, Object> data, List<MetadataEntityFieldDO> fields) {
        Map<String, Object> processedData = new HashMap<>(data);

        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();

        for (MetadataEntityFieldDO field : fields) {
            String fieldName = field.getFieldName();
            
            // 处理主键字段
            if (field.getIsPrimaryKey()) {
                if (!processedData.containsKey(fieldName)) {
                    // 生成雪花ID作为主键
                    processedData.put(fieldName, IdUtil.getSnowflakeNextId());
                }
                continue;
            }

            // 处理系统字段
            if (field.getIsSystemField()) {
                switch (fieldName.toLowerCase()) {
                    case "created_time":
                    case "createtime":
                        processedData.put(fieldName, now);
                        break;
                    case "updated_time":
                    case "updatetime":
                        processedData.put(fieldName, now);
                        break;
                    case "deleted":
                        // 统一使用字符串类型，避免PostgreSQL类型不匹配问题
                        processedData.put(fieldName, "0");
                        break;
                    case "lock_version":
                    case "lockversion":
                        processedData.put(fieldName, 0);
                        break;
                    case "tenant_id":
                    case "tenantid":
                        // 这里可以从当前上下文获取租户ID，暂时设置为1
                        processedData.put(fieldName, 1L);
                        break;
                    default:
                        // 其他系统字段按默认值处理
                        if (StringUtils.hasText(field.getDefaultValue())) {
                            processedData.put(fieldName, field.getDefaultValue());
                        }
                        break;
                }
                continue;
            }

            // 设置业务字段默认值
            if (!processedData.containsKey(fieldName) && StringUtils.hasText(field.getDefaultValue())) {
                processedData.put(fieldName, field.getDefaultValue());
            }
        }

        return processedData;
    }

    /**
     * 处理更新数据
     */
    private Map<String, Object> processDataForUpdate(Map<String, Object> data, List<MetadataEntityFieldDO> fields) {
        Map<String, Object> processedData = new HashMap<>();

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String fieldName = entry.getKey();
            MetadataEntityFieldDO field = fields.stream()
                    .filter(f -> f.getFieldName().equals(fieldName))
                    .findFirst()
                    .orElse(null);

            if (field != null && !field.getIsPrimaryKey() && !field.getIsSystemField()) {
                processedData.put(fieldName, entry.getValue());
            }
        }

        return processedData;
    }

    /**
     * 校验数据是否存在
     */
    private void validateDataExists(String tableName, Object id, List<MetadataEntityFieldDO> fields) {
        Map<String, Object> existingData = queryDataById(tableName, id, fields);
        if (existingData == null || existingData.isEmpty()) {
            throw exception(DATA_NOT_EXISTS);
        }
    }

    /**
     * 根据ID查询数据
     */
    private Map<String, Object> queryDataById(String tableName, Object id, List<MetadataEntityFieldDO> fields) {
        String primaryKeyField = getPrimaryKeyFieldName(fields);

        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(primaryKeyField, id);
        
        // 检查表中是否有 tenant_id 字段，如果没有则添加 deleted 条件
        boolean hasTenantIdField = fields.stream()
                .anyMatch(field -> "tenant_id".equalsIgnoreCase(field.getFieldName()));
        
        boolean hasDeletedField = fields.stream()
                .anyMatch(field -> "deleted".equalsIgnoreCase(field.getFieldName()));
        
        if (hasDeletedField) {
            configStore.and("deleted", 0);
        }
        
        // 如果没有 tenant_id 字段，需要禁用租户自动条件
        if (!hasTenantIdField) {
            // 使用原始SQL方式查询，避免框架自动添加租户条件
            String sql = "SELECT * FROM " + tableName + " WHERE " + primaryKeyField + " = ? ";
            if (hasDeletedField) {
                // 修复：将deleted字段的值作为字符串传递，以匹配数据库中的character varying类型
                sql += "AND deleted = '0'";
            }
            
            DataSet dataSet = anylineService.querys(sql, id);
            if (dataSet == null || dataSet.size() == 0) {
                return null;
            }
            DataRow dataRow = dataSet.getRow(0);
            return convertDataRowToMap(dataRow, fields);
        } else {
            // 有 tenant_id 字段，使用正常查询
            DataSet dataSet = anylineService.querys(tableName, configStore);
            if (dataSet == null || dataSet.size() == 0) {
                return null;
            }
            DataRow dataRow = dataSet.getRow(0);
            return convertDataRowToMap(dataRow, fields);
        }
    }

    /**
     * 获取主键字段名
     */
    private String getPrimaryKeyFieldName(List<MetadataEntityFieldDO> fields) {
        return fields.stream()
                .filter(MetadataEntityFieldDO::getIsPrimaryKey)
                .map(MetadataEntityFieldDO::getFieldName)
                .findFirst()
                .orElse("id");
    }

    /**
     * 获取主键值
     */
    private Object getPrimaryKeyValue(Map<String, Object> data, List<MetadataEntityFieldDO> fields) {
        String primaryKeyField = getPrimaryKeyFieldName(fields);
        Object value = data.get(primaryKeyField);
        
        log.debug("获取主键值 - 主键字段名: {}, 数据中的值: {}, 数据内容: {}", 
                primaryKeyField, value, data);
                
        return value;
    }

    /**
     * 转换DataRow为Map
     */
    private Map<String, Object> convertDataRowToMap(DataRow dataRow, List<MetadataEntityFieldDO> fields) {
        Map<String, Object> resultMap = new HashMap<>();

        for (MetadataEntityFieldDO field : fields) {
            String fieldName = field.getFieldName();
            Object value = dataRow.get(fieldName);
            if (value != null) {
                resultMap.put(fieldName, value);
            }
        }

        return resultMap;
    }

    /**
     * 构建动态数据响应VO
     */
    private DynamicDataRespVO buildDynamicDataRespVO(MetadataBusinessEntityDO entity, Map<String, Object> data) {
        DynamicDataRespVO respVO = new DynamicDataRespVO();
        respVO.setEntityId(String.valueOf(entity.getId()));
        respVO.setEntityName(entity.getDisplayName());
        respVO.setData(data);
        return respVO;
    }

}
