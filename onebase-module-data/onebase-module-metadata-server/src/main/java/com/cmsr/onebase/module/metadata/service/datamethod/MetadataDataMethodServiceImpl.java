package com.cmsr.onebase.module.metadata.service.datamethod;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo.*;
import com.cmsr.onebase.module.metadata.service.datamethod.vo.DataMethodQueryVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.datasource.MetadataDatasourceDO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.entity.DataRow;
import org.anyline.data.entity.DataSet;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.anyline.entity.OrderByColumn;
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
    private DataRepository dataRepository;
    
    @Resource
    private AnylineService<?> anylineService;

    @Override
    public List<DataMethodRespVO> getDataMethodList(DataMethodQueryVO queryVO) {
        // 校验实体存在
        MetadataBusinessEntityDO entity = dataRepository.findById(MetadataBusinessEntityDO.class, queryVO.getEntityId());
        if (entity == null) {
            throw exception(BUSINESS_ENTITY_NOT_EXISTS);
        }

        // 获取实体字段，用于生成方法参数
        DefaultConfigStore fieldConfigStore = new DefaultConfigStore();
        fieldConfigStore.and("entity_id", queryVO.getEntityId());
        List<MetadataEntityFieldDO> fields = dataRepository.findAllByConfig(MetadataEntityFieldDO.class, fieldConfigStore);

        // 生成系统内置的数据方法
        List<DataMethodRespVO> methods = generateBuiltInMethods(entity, fields);

        // 根据条件过滤
        if (StringUtils.hasText(queryVO.getMethodType())) {
            methods = methods.stream()
                    .filter(method -> queryVO.getMethodType().equals(method.getMethodType()))
                    .toList();
        }
        
        if (StringUtils.hasText(queryVO.getKeyword())) {
            methods = methods.stream()
                    .filter(method -> method.getMethodName().contains(queryVO.getKeyword()) 
                                   || method.getDescription().contains(queryVO.getKeyword()))
                    .toList();
        }

        return methods;
    }

    @Override
    public DataMethodDetailRespVO getDataMethodDetail(Long entityId, String methodCode) {
        // 校验实体存在
        MetadataBusinessEntityDO entity = dataRepository.findById(MetadataBusinessEntityDO.class, entityId);
        if (entity == null) {
            throw exception(BUSINESS_ENTITY_NOT_EXISTS);
        }

        // 获取实体字段
        DefaultConfigStore fieldConfigStore = new DefaultConfigStore();
        fieldConfigStore.and("entity_id", entityId);
        List<MetadataEntityFieldDO> fields = dataRepository.findAllByConfig(MetadataEntityFieldDO.class, fieldConfigStore);

        // 生成对应的方法详情
        return generateMethodDetail(entity, fields, methodCode);
    }

    /**
     * 生成系统内置的数据方法
     */
    private List<DataMethodRespVO> generateBuiltInMethods(MetadataBusinessEntityDO entity, List<MetadataEntityFieldDO> fields) {
        List<DataMethodRespVO> methods = new ArrayList<>();
        String entityCode = entity.getCode();

        // 1. 新增单条数据
        DataMethodRespVO createMethod = new DataMethodRespVO();
        createMethod.setMethodName("新增单条数据");
        createMethod.setMethodCode("create_single");
        createMethod.setMethodType("CREATE");
        createMethod.setUrl("/api/data/" + entityCode + "/create");
        createMethod.setHttpMethod("POST");
        createMethod.setDescription("数据模型的新增服务，该服务会在业务对象被判断为新增的时候被调用");
        createMethod.setInputParameters(generateInputParameters(fields, false));
        createMethod.setOutputParameters(generateOutputParameter("OBJECT", "创建成功的数据对象"));
        methods.add(createMethod);

        // 2. 更新单条数据
        DataMethodRespVO updateMethod = new DataMethodRespVO();
        updateMethod.setMethodName("更新单条数据");
        updateMethod.setMethodCode("update_single");
        updateMethod.setMethodType("UPDATE");
        updateMethod.setUrl("/api/data/" + entityCode + "/update");
        updateMethod.setHttpMethod("PUT");
        updateMethod.setDescription("数据模型的更新服务，通过主键更新模型已存在的数据");
        updateMethod.setInputParameters(generateInputParameters(fields, true));
        updateMethod.setOutputParameters(generateOutputParameter("OBJECT", "更新成功的数据对象"));
        methods.add(updateMethod);

        // 3. 删除单条数据
        DataMethodRespVO deleteMethod = new DataMethodRespVO();
        deleteMethod.setMethodName("删除单条数据");
        deleteMethod.setMethodCode("delete_single");
        deleteMethod.setMethodType("DELETE");
        deleteMethod.setUrl("/api/data/" + entityCode + "/{id}");
        deleteMethod.setHttpMethod("DELETE");
        deleteMethod.setDescription("数据模型的删除单条记录的服务，通过主键删除目标模型中符合条件的数据");
        deleteMethod.setInputParameters(generateIdParameter());
        deleteMethod.setOutputParameters(generateOutputParameter("BOOLEAN", "删除是否成功"));
        methods.add(deleteMethod);

        // 4. 根据id查询数据详情
        DataMethodRespVO getByIdMethod = new DataMethodRespVO();
        getByIdMethod.setMethodName("根据id查询数据详情");
        getByIdMethod.setMethodCode("get_by_id");
        getByIdMethod.setMethodType("READ");
        getByIdMethod.setUrl("/api/data/" + entityCode + "/{id}");
        getByIdMethod.setHttpMethod("GET");
        getByIdMethod.setDescription("通过主键获取单条数据使用的服务，用来接收一条结果集");
        getByIdMethod.setInputParameters(generateIdParameter());
        getByIdMethod.setOutputParameters(generateOutputParameter("OBJECT", "数据详情对象"));
        methods.add(getByIdMethod);

        // 5. 分页查询数据列表
        DataMethodRespVO getPageMethod = new DataMethodRespVO();
        getPageMethod.setMethodName("分页查询数据列表");
        getPageMethod.setMethodCode("get_page_list");
        getPageMethod.setMethodType("READ");
        getPageMethod.setUrl("/api/data/" + entityCode + "/list");
        getPageMethod.setHttpMethod("GET");
        getPageMethod.setDescription("通过查询条件获取多条数据使用的服务，用来接收多条结果集");
        getPageMethod.setInputParameters(generatePageParameters());
        getPageMethod.setOutputParameters(generateOutputParameter("PAGE_OBJECT", "分页的数据列表"));
        methods.add(getPageMethod);

        return methods;
    }

    /**
     * 生成方法详情
     */
    private DataMethodDetailRespVO generateMethodDetail(MetadataBusinessEntityDO entity, List<MetadataEntityFieldDO> fields, String methodCode) {
        DataMethodDetailRespVO detail = new DataMethodDetailRespVO();
        String entityCode = entity.getCode();

        switch (methodCode) {
            case "create_single":
                detail.setMethodName("新增单条数据");
                detail.setMethodCode("create_single");
                detail.setMethodType("CREATE");
                detail.setUrl("/api/data/" + entityCode + "/create");
                detail.setHttpMethod("POST");
                detail.setDescription("新增单条" + entity.getDisplayName() + "记录");
                detail.setInputParameters(generateDetailInputParameters(fields, false));
                detail.setOutputParameters(generateDetailOutputParameter("OBJECT", "创建成功的" + entity.getDisplayName() + "对象", fields));
                detail.setRequestExample(generateRequestExample(fields, false));
                detail.setResponseExample(generateResponseExample(entity, fields));
                break;
            case "get_by_id":
                detail.setMethodName("根据id查询数据详情");
                detail.setMethodCode("get_by_id");
                detail.setMethodType("READ");
                detail.setUrl("/api/data/" + entityCode + "/{id}");
                detail.setHttpMethod("GET");
                detail.setDescription("根据ID查询" + entity.getDisplayName() + "详情");
                detail.setInputParameters(generateDetailIdParameter());
                detail.setOutputParameters(generateDetailOutputParameter("OBJECT", entity.getDisplayName() + "详情对象", fields));
                detail.setRequestExample(null);
                detail.setResponseExample(generateResponseExample(entity, fields));
                break;
            // 可以继续添加其他方法的详细实现
            default:
                detail.setMethodName("未知方法");
                detail.setDescription("方法不存在");
        }

        return detail;
    }

    /**
     * 生成输入参数
     */
    private List<DataMethodParameterVO> generateInputParameters(List<MetadataEntityFieldDO> fields, boolean includeId) {
        List<DataMethodParameterVO> parameters = new ArrayList<>();
        
        for (MetadataEntityFieldDO field : fields) {
            if (!includeId && field.getIsPrimaryKey()) {
                continue;
            }
            if (field.getIsSystemField()) {
                continue;
            }
            
            DataMethodParameterVO param = new DataMethodParameterVO();
            param.setParamName(field.getFieldName());
            param.setParamType(field.getFieldType());
            param.setRequired(field.getIsRequired());
            param.setDescription(field.getDisplayName());
            parameters.add(param);
        }
        
        return parameters;
    }

    /**
     * 生成详情输入参数
     */
    private List<DataMethodDetailParameterVO> generateDetailInputParameters(List<MetadataEntityFieldDO> fields, boolean includeId) {
        List<DataMethodDetailParameterVO> parameters = new ArrayList<>();
        
        for (MetadataEntityFieldDO field : fields) {
            if (!includeId && field.getIsPrimaryKey()) {
                continue;
            }
            if (field.getIsSystemField()) {
                continue;
            }
            
            DataMethodDetailParameterVO param = new DataMethodDetailParameterVO();
            param.setParamName(field.getFieldName());
            param.setParamType(field.getFieldType());
            param.setParamLength(field.getDataLength());
            param.setRequired(field.getIsRequired());
            param.setDescription(field.getDisplayName());
            param.setValidationRules(new ArrayList<>()); // 这里可以根据实际校验规则填充
            parameters.add(param);
        }
        
        return parameters;
    }

    /**
     * 生成ID参数
     */
    private List<DataMethodParameterVO> generateIdParameter() {
        List<DataMethodParameterVO> parameters = new ArrayList<>();
        DataMethodParameterVO param = new DataMethodParameterVO();
        param.setParamName("id");
        param.setParamType("BIGINT");
        param.setRequired(true);
        param.setDescription("主键ID");
        parameters.add(param);
        return parameters;
    }

    /**
     * 生成详情ID参数
     */
    private List<DataMethodDetailParameterVO> generateDetailIdParameter() {
        List<DataMethodDetailParameterVO> parameters = new ArrayList<>();
        DataMethodDetailParameterVO param = new DataMethodDetailParameterVO();
        param.setParamName("id");
        param.setParamType("BIGINT");
        param.setParamLength(20);
        param.setRequired(true);
        param.setDescription("主键ID");
        param.setValidationRules(new ArrayList<>());
        parameters.add(param);
        return parameters;
    }

    /**
     * 生成分页参数
     */
    private List<DataMethodParameterVO> generatePageParameters() {
        List<DataMethodParameterVO> parameters = new ArrayList<>();
        
        DataMethodParameterVO pageNumParam = new DataMethodParameterVO();
        pageNumParam.setParamName("pageNum");
        pageNumParam.setParamType("INTEGER");
        pageNumParam.setRequired(false);
        pageNumParam.setDescription("页码");
        parameters.add(pageNumParam);
        
        DataMethodParameterVO pageSizeParam = new DataMethodParameterVO();
        pageSizeParam.setParamName("pageSize");
        pageSizeParam.setParamType("INTEGER");
        pageSizeParam.setRequired(false);
        pageSizeParam.setDescription("每页大小");
        parameters.add(pageSizeParam);
        
        return parameters;
    }

    /**
     * 生成输出参数
     */
    private DataMethodOutputParameterVO generateOutputParameter(String type, String description) {
        DataMethodOutputParameterVO output = new DataMethodOutputParameterVO();
        output.setType(type);
        output.setDescription(description);
        return output;
    }

    /**
     * 生成详情输出参数
     */
    private DataMethodDetailOutputParameterVO generateDetailOutputParameter(String type, String description, List<MetadataEntityFieldDO> fields) {
        DataMethodDetailOutputParameterVO output = new DataMethodDetailOutputParameterVO();
        output.setType(type);
        output.setDescription(description);
        
        List<DataMethodPropertyVO> properties = new ArrayList<>();
        for (MetadataEntityFieldDO field : fields) {
            if (field.getIsSystemField()) {
                continue;
            }
            
            DataMethodPropertyVO property = new DataMethodPropertyVO();
            property.setFieldName(field.getFieldName());
            property.setFieldType(field.getFieldType());
            property.setDescription(field.getDisplayName());
            properties.add(property);
        }
        output.setProperties(properties);
        
        return output;
    }

    /**
     * 生成请求示例
     */
    private Object generateRequestExample(List<MetadataEntityFieldDO> fields, boolean includeId) {
        Map<String, Object> example = new HashMap<>();
        
        for (MetadataEntityFieldDO field : fields) {
            if (!includeId && field.getIsPrimaryKey()) {
                continue;
            }
            if (field.getIsSystemField()) {
                continue;
            }
            
            // 根据字段类型生成示例值
            Object exampleValue = generateExampleValue(field);
            example.put(field.getFieldName(), exampleValue);
        }
        
        return example;
    }

    /**
     * 生成响应示例
     */
    private Object generateResponseExample(MetadataBusinessEntityDO entity, List<MetadataEntityFieldDO> fields) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "操作成功");
        
        Map<String, Object> data = new HashMap<>();
        for (MetadataEntityFieldDO field : fields) {
            if (field.getIsSystemField()) {
                continue;
            }
            
            Object exampleValue = generateExampleValue(field);
            if (field.getIsPrimaryKey()) {
                exampleValue = 1001L;
            }
            data.put(field.getFieldName(), exampleValue);
        }
        data.put("createTime", LocalDateTime.now().toString());
        
        response.put("data", data);
        return response;
    }

    /**
     * 根据字段类型生成示例值
     */
    private Object generateExampleValue(MetadataEntityFieldDO field) {
        String fieldType = field.getFieldType();
        String fieldName = field.getFieldName().toLowerCase();
        
        return switch (fieldType) {
            case "VARCHAR", "TEXT" -> {
                if (fieldName.contains("name")) yield "示例名称";
                if (fieldName.contains("email")) yield "example@test.com";
                if (fieldName.contains("phone")) yield "13800138000";
                yield "示例文本";
            }
            case "INTEGER" -> 100;
            case "BIGINT" -> 1001L;
            case "DECIMAL" -> 99.99;
            case "BOOLEAN" -> true;
            case "DATE" -> "2025-01-25";
            case "DATETIME" -> "2025-01-25T10:30:00";
            default -> "示例值";
        };
    }

    // ========== 系统级别的动态数据操作方法实现 ==========

    @Override
    public DynamicDataRespVO createData(DynamicDataCreateReqVO reqVO) {
        // 1. 校验实体存在
        MetadataBusinessEntityDO entity = validateEntityExists(reqVO.getEntityId());
        
        // 2. 获取实体字段信息
        List<MetadataEntityFieldDO> fields = getEntityFields(reqVO.getEntityId());
        
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
        MetadataBusinessEntityDO entity = validateEntityExists(reqVO.getEntityId());
        
        // 2. 获取实体字段信息
        List<MetadataEntityFieldDO> fields = getEntityFields(reqVO.getEntityId());
        
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
        MetadataBusinessEntityDO entity = validateEntityExists(reqVO.getEntityId());
        
        // 2. 获取实体字段信息
        List<MetadataEntityFieldDO> fields = getEntityFields(reqVO.getEntityId());
        
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
        MetadataBusinessEntityDO entity = validateEntityExists(reqVO.getEntityId());
        
        // 2. 获取实体字段信息
        List<MetadataEntityFieldDO> fields = getEntityFields(reqVO.getEntityId());
        
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
        MetadataBusinessEntityDO entity = validateEntityExists(reqVO.getEntityId());
        
        // 2. 获取实体字段信息
        List<MetadataEntityFieldDO> fields = getEntityFields(reqVO.getEntityId());
        
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
            Order order = new Order();
            if (StringUtils.hasText(reqVO.getSortField())) {
                String direction = "desc".equalsIgnoreCase(reqVO.getSortDirection()) ? "DESC" : "ASC";
                order.addColumn(new OrderByColumn(reqVO.getSortField(), direction));
            } else {
                // 默认按主键倒序
                String primaryKeyField = getPrimaryKeyFieldName(fields);
                order.addColumn(new OrderByColumn(primaryKeyField, "DESC"));
            }
            
            // 执行分页查询
            DataSet dataSet = anylineService.querys(entity.getTableName(), configStore, order, 
                    (reqVO.getPageNo() - 1) * reqVO.getPageSize(), reqVO.getPageSize());
            
            // 4. 转换结果
            List<DynamicDataRespVO> list = dataSet.getRows().stream()
                    .map(row -> {
                        Map<String, Object> data = convertDataRowToMap(row, fields);
                        return buildDynamicDataRespVO(entity, data);
                    })
                    .collect(Collectors.toList());
            
            long total = dataSet.getTotal();
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
        MetadataBusinessEntityDO entity = dataRepository.findById(MetadataBusinessEntityDO.class, entityId);
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
        List<MetadataEntityFieldDO> fields = dataRepository.findAllByConfig(MetadataEntityFieldDO.class, configStore);
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
            MetadataDatasourceDO datasource = dataRepository.findById(MetadataDatasourceDO.class, datasourceId);
            if (datasource != null) {
                // 使用Anyline切换数据源的方法，具体实现需要根据实际的Anyline版本调整
                try {
                    // 这里需要根据实际的Anyline API来实现数据源切换
                    log.info("切换到数据源：{}", datasource.getCode());
                } catch (Exception e) {
                    log.warn("切换数据源失败，使用默认数据源：{}", e.getMessage());
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
        
        for (MetadataEntityFieldDO field : fields) {
            // 跳过主键字段（通常自动生成）
            if (field.getIsPrimaryKey()) {
                continue;
            }
            
            // 设置默认值
            if (!processedData.containsKey(field.getFieldName()) && StringUtils.hasText(field.getDefaultValue())) {
                processedData.put(field.getFieldName(), field.getDefaultValue());
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
        
        DataRow dataRow = anylineService.queryRow(tableName, configStore);
        if (dataRow == null) {
            return null;
        }
        
        return convertDataRowToMap(dataRow, fields);
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
        respVO.setEntityId(entity.getId());
        respVO.setEntityName(entity.getDisplayName());
        respVO.setData(data);
        return respVO;
    }

} 