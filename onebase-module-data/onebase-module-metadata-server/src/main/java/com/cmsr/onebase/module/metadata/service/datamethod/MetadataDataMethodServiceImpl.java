package com.cmsr.onebase.module.metadata.service.datamethod;

import cn.hutool.core.util.IdUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo.*;
import com.cmsr.onebase.module.metadata.service.datamethod.vo.DataMethodQueryVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.method.MetadataDataSystemMethodDO;
import com.cmsr.onebase.module.metadata.dal.database.MetadataRepository;
import com.cmsr.onebase.module.metadata.dal.database.TemporaryDatasourceService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.entity.DataRow;
import org.anyline.entity.DataSet;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;

import org.anyline.service.AnylineService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

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
    private MetadataRepository metadataRepository;

    @Resource
    private AnylineService<?> anylineService;

    @Resource
    private TemporaryDatasourceService temporaryDatasourceService;

    @Override
    public List<DataMethodRespVO> getDataMethodList(DataMethodQueryVO queryVO) {
        // 校验实体存在
        MetadataBusinessEntityDO entity = metadataRepository.findById(MetadataBusinessEntityDO.class, Long.valueOf(queryVO.getEntityId()));
        if (entity == null) {
            throw exception(BUSINESS_ENTITY_NOT_EXISTS);
        }

        // 构建查询条件
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("is_enabled", true);
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
        List<MetadataDataSystemMethodDO> methodDOList = metadataRepository.findAllByConfig(MetadataDataSystemMethodDO.class, configStore);

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
        MetadataBusinessEntityDO entity = metadataRepository.findById(MetadataBusinessEntityDO.class, entityId);
        if (entity == null) {
            throw exception(BUSINESS_ENTITY_NOT_EXISTS);
        }

        // 根据方法代码查询数据方法
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("method_code", methodCode);
        configStore.and("is_enabled", true);
        configStore.and("deleted", 0);

        MetadataDataSystemMethodDO methodDO = metadataRepository.findOne(MetadataDataSystemMethodDO.class, configStore);
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
        try {
            // 切换到指定数据源
            switchToDataSource(entity.getDatasourceId());

            // 执行插入
            DataRow dataRow = new DataRow(processedData);
            Object insertResult = anylineService.insert(entity.getTableName(), dataRow);
            log.info("创建数据成功，实体ID: {}, 表名: {}, 插入结果: {}", reqVO.getEntityId(), entity.getTableName(), insertResult);

            // 6. 查询插入后的完整数据
            Object primaryKeyValue = getPrimaryKeyValue(processedData, fields);
            if (primaryKeyValue == null && insertResult != null) {
                primaryKeyValue = insertResult;
            }

            Map<String, Object> resultData = queryDataById(entity.getTableName(), primaryKeyValue, fields);

            // 7. 构建响应
            return buildDynamicDataRespVO(entity, resultData);

        } catch (Exception e) {
            log.error("创建数据失败，实体ID: {}, 错误信息: {}", reqVO.getEntityId(), e.getMessage(), e);
            throw exception(DATA_CREATE_FAILED, e.getMessage());
        }
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
        try {
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

        } catch (Exception e) {
            log.error("更新数据失败，实体ID: {}, 数据ID: {}, 错误信息: {}", reqVO.getEntityId(), reqVO.getId(), e.getMessage(), e);
            throw exception(DATA_UPDATE_FAILED, e.getMessage());
        }
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
        try {
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

        } catch (Exception e) {
            log.error("删除数据失败，实体ID: {}, 数据ID: {}, 错误信息: {}", reqVO.getEntityId(), reqVO.getId(), e.getMessage(), e);
            throw exception(DATA_DELETE_FAILED, e.getMessage());
        }
    }

    @Override
    public DynamicDataRespVO getData(DynamicDataGetReqVO reqVO) {
        // 1. 校验实体存在
        MetadataBusinessEntityDO entity = validateEntityExists(Long.valueOf(reqVO.getEntityId()));

        // 2. 获取实体字段信息
        List<MetadataEntityFieldDO> fields = getEntityFields(Long.valueOf(reqVO.getEntityId()));

        // 3. 使用Anyline查询数据
        try {
            // 切换到指定数据源
            switchToDataSource(entity.getDatasourceId());

            // 查询数据
            Map<String, Object> resultData = queryDataById(entity.getTableName(), reqVO.getId(), fields);

            if (resultData == null || resultData.isEmpty()) {
                throw exception(DATA_NOT_EXISTS);
            }

            // 4. 构建响应
            return buildDynamicDataRespVO(entity, resultData);

        } catch (Exception e) {
            log.error("查询数据失败，实体ID: {}, 数据ID: {}, 错误信息: {}", reqVO.getEntityId(), reqVO.getId(), e.getMessage(), e);
            throw exception(DATA_QUERY_FAILED, e.getMessage());
        }
    }

    @Override
    public PageResult<DynamicDataRespVO> getDataPage(DynamicDataPageReqVO reqVO) {
        // 1. 校验实体存在
        MetadataBusinessEntityDO entity = validateEntityExists(Long.valueOf(reqVO.getEntityId()));

        // 2. 获取实体字段信息
        List<MetadataEntityFieldDO> fields = getEntityFields(Long.valueOf(reqVO.getEntityId()));

        // 3. 使用Anyline执行分页查询
        try {
            // 切换到指定数据源
            switchToDataSource(entity.getDatasourceId());

            // 构建查询条件
            DefaultConfigStore configStore = new DefaultConfigStore();
            if (reqVO.getFilters() != null && !reqVO.getFilters().isEmpty()) {
                for (Map.Entry<String, Object> entry : reqVO.getFilters().entrySet()) {
                    if (entry.getValue() != null) {
                        configStore.and(entry.getKey(), entry.getValue());
                    }
                }
            }

            // 构建排序
            if (StringUtils.hasText(reqVO.getSortField())) {
                Order.TYPE direction = "desc".equalsIgnoreCase(reqVO.getSortDirection()) ? Order.TYPE.DESC : Order.TYPE.ASC;
                configStore.order(reqVO.getSortField(), direction);
            } else {
                // 默认按主键倒序
                String primaryKeyField = getPrimaryKeyFieldName(fields);
                configStore.order(primaryKeyField, Order.TYPE.DESC);
            }

            // 执行分页查询
            DataSet dataSet = anylineService.querys(entity.getTableName(), configStore);

            // 手动实现分页
            long total = dataSet.total();
            int startIndex = (reqVO.getPageNo() - 1) * reqVO.getPageSize();
            int endIndex = Math.min(startIndex + reqVO.getPageSize(), dataSet.size());

            // 4. 转换结果
            List<DynamicDataRespVO> list = new ArrayList<>();
            for (int i = startIndex; i < endIndex; i++) {
                DataRow row = dataSet.getRow(i);
                Map<String, Object> data = convertDataRowToMap(row, fields);
                list.add(buildDynamicDataRespVO(entity, data));
            }
            log.info("分页查询数据成功，实体ID: {}, 表名: {}, 页码: {}, 页大小: {}, 总记录数: {}",
                    reqVO.getEntityId(), entity.getTableName(), reqVO.getPageNo(), reqVO.getPageSize(), total);

            return new PageResult<>(list, total);

        } catch (Exception e) {
            log.error("分页查询数据失败，实体ID: {}, 错误信息: {}", reqVO.getEntityId(), e.getMessage(), e);
            throw exception(DATA_QUERY_FAILED, e.getMessage());
        }
    }

    // ========== 私有辅助方法 ==========

    /**
     * 校验实体存在
     */
    private MetadataBusinessEntityDO validateEntityExists(Long entityId) {
        MetadataBusinessEntityDO entity = metadataRepository.findById(MetadataBusinessEntityDO.class, entityId);
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
        List<MetadataEntityFieldDO> fields = metadataRepository.findAllByConfig(MetadataEntityFieldDO.class, configStore);
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
            MetadataDatasourceDO datasource = metadataRepository.findById(MetadataDatasourceDO.class, datasourceId);
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
                        processedData.put(fieldName, 0);
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
                sql += "AND deleted = 0";
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
        return data.get(primaryKeyField);
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
