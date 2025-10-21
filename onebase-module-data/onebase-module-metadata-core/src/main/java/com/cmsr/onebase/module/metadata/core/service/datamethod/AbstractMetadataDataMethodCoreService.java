package com.cmsr.onebase.module.metadata.core.service.datamethod;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.framework.uid.UidGenerator;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.service.datamethod.validator.ValidationManager;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataEntityFieldCoreService;
import com.cmsr.onebase.module.metadata.core.service.datasource.MetadataDatasourceCoreService;
import com.cmsr.onebase.module.metadata.core.dal.database.TemporaryDatasourceService;
import com.cmsr.onebase.module.metadata.core.service.number.AutoNumberService;
import com.cmsr.onebase.module.metadata.core.enums.BooleanStatusEnum;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.anyline.entity.DataRow;
import org.anyline.entity.DataSet;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.service.AnylineService;

import java.util.*;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.*;


/**
 * 抽象数据方法核心服务类
 *
 * 定义了基于设计文档的11步流程框架，所有数据操作方法将遵循此流程。
 * 包含所有公共方法，供子类复用。
 *
 * @author matianyu
 * @date 2025-01-27
 */
@Slf4j
public abstract class AbstractMetadataDataMethodCoreService  implements MetadataDataMethodCoreServiceV2{


    // ========== 依赖注入 ==========
    @Resource
    protected MetadataBusinessEntityCoreService metadataBusinessEntityCoreService;

    @Resource
    protected MetadataDatasourceCoreService metadataDatasourceCoreService;

    @Resource
    protected MetadataEntityFieldCoreService metadataEntityFieldService;

    @Resource
    protected TemporaryDatasourceService temporaryDatasourceService;

    @Resource
    protected UidGenerator uidGenerator;

    @Resource
    protected AutoNumberService autoNumberService;

    @Resource
    protected ValidationManager validationManager;

    // ========== 公共方法 ==========

    /**
     * 校验实体存在
     */
    protected MetadataBusinessEntityDO validateEntityExists(Long entityId) {
        MetadataBusinessEntityDO entity = metadataBusinessEntityCoreService.getBusinessEntity(entityId);
        if (entity == null) {
            throw exception(BUSINESS_ENTITY_NOT_EXISTS);
        }
        return entity;
    }

    /**
     * 获取实体字段
     */
    protected List<MetadataEntityFieldDO> getEntityFields(Long entityId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataEntityFieldDO.ENTITY_ID, entityId);
        configStore.and("deleted", 0);
        List<MetadataEntityFieldDO> fields = metadataEntityFieldService.getEntityFieldListByEntityId(entityId);
        if (fields == null || fields.isEmpty()) {
            throw exception(ENTITY_FIELD_NOT_EXISTS);
        }
        return fields;
    }









    /**
     * 获取主键字段名
     */
    protected String getPrimaryKeyFieldName(List<MetadataEntityFieldDO> fields) {
        // 1) 只考虑非系统字段中的主键，避免把 deleted/lock_version 等系统字段当作主键
        List<MetadataEntityFieldDO> pkCandidates = fields.stream()
            .filter(field -> BooleanStatusEnum.isYes(field.getIsPrimaryKey()))
            .filter(field -> !BooleanStatusEnum.isYes(field.getIsSystemField()))
            .collect(Collectors.toList());

        // 2) 在候选中优先选择名字为 id 的字段
        Optional<String> idNamed = pkCandidates.stream()
            .map(MetadataEntityFieldDO::getFieldName)
            .filter(Objects::nonNull)
            .filter(name -> "id".equalsIgnoreCase(name))
            .findFirst();
        if (idNamed.isPresent()) {
            String pk = idNamed.get();
            log.info("检测到非系统主键字段优先为: " + pk);
            return pk;
        }

        // 3) 否则取第一个非系统主键候选
        Optional<String> firstPk = pkCandidates.stream()
            .map(MetadataEntityFieldDO::getFieldName)
            .filter(Objects::nonNull)
            .findFirst();
        if (firstPk.isPresent()) {
            String pk = firstPk.get();
            log.info("检测到非系统主键字段: " + pk);
            return pk;
        }

        // 4) 如果未配置主键，则回退到列名为 id（即使它被标记为系统字段，也作为兜底使用）
        boolean hasId = fields.stream()
            .map(MetadataEntityFieldDO::getFieldName)
            .anyMatch(name -> name != null && "id".equalsIgnoreCase(name));
        if (hasId) {
            log.info("未配置主键，回退使用列名 id 作为主键");
            return "id";
        }

        // 5) 最终兜底
        log.warn("未找到主键字段且不存在列名为 id 的字段，使用默认 id 作为主键名称");
        return "id";
    }

    /**
     * 获取主键值
     */
    protected Object getPrimaryKeyValue(Map<String, Object> data, List<MetadataEntityFieldDO> fields) {
        String primaryKeyField = getPrimaryKeyFieldName(fields);
        Object value = data.get(primaryKeyField);

        log.info("获取主键值 - 主键字段名: " + primaryKeyField + ", 数据中的值: " + value + ", 数据内容: " + data);

        return value;
    }

    /**
     * 转换DataRow为Map，并对JSON字段进行反序列化
     */
    protected Map<String, Object> convertDataRowToMap(DataRow dataRow, List<MetadataEntityFieldDO> fields) {
        Map<String, Object> resultMap = new HashMap<>();

        for (MetadataEntityFieldDO field : fields) {
            String fieldName = field.getFieldName();
            String fieldType = field.getFieldType();
            Object value = dataRow.get(fieldName);
            
            if (value != null) {
                // 对需要JSON反序列化的字段进行处理
                if (needsJsonDeserialization(fieldType, value)) {
                    try {
                        // 尝试将JSON字符串反序列化为对象
                        Object deserializedValue = JsonUtils.parseObject(value.toString(), Object.class);
                        resultMap.put(fieldName, deserializedValue);
                        log.debug("字段 {} (类型: {}) 的值已从JSON反序列化", fieldName, fieldType);
                    } catch (Exception e) {
                        // 反序列化失败时，保持原值
                        log.debug("字段 {} 的JSON反序列化失败，保持原值: {}", fieldName, e.getMessage());
                        resultMap.put(fieldName, value);
                    }
                } else {
                    resultMap.put(fieldName, value);
                }
            }
        }

        return resultMap;
    }

    /**
     * 判断字段类型是否需要JSON反序列化
     * 
     * @param fieldType 字段类型
     * @param fieldValue 字段值
     * @return 是否需要反序列化
     */
    private boolean needsJsonDeserialization(String fieldType, Object fieldValue) {
        if (fieldType == null || fieldValue == null) {
            return false;
        }
        
        // 只有当值是字符串类型时才考虑反序列化
        if (!(fieldValue instanceof String)) {
            return false;
        }
        
        String upperFieldType = fieldType.toUpperCase();
        
        // 字段类型包含以下关键字的需要JSON反序列化
        boolean isComplexType = upperFieldType.contains("SELECT") ||       // 选择类型（包括SELECT、MULTI_SELECT、DATA_SELECTION等）
                                upperFieldType.contains("MULTI") ||        // 多选类型（包括MULTI_USER、MULTI_DEPARTMENT等）
                                upperFieldType.contains("ADDRESS") ||       // 地址类型
                                upperFieldType.contains("FILE") ||          // 文件附件
                                upperFieldType.contains("ATTACHMENT") ||    // 附件
                                upperFieldType.contains("IMAGE") ||         // 图片
                                upperFieldType.contains("USER") ||          // 人员选择（包括USER、MULTI_USER）
                                upperFieldType.contains("DEPT") ||          // 部门选择（包括DEPARTMENT、MULTI_DEPARTMENT）
                                upperFieldType.contains("DATA") ||          // 数据选择（包括DATA_SELECTION、MULTI_DATA_SELECTION）
                                upperFieldType.contains("GEOGRAPHY") ||     // 地理位置
                                upperFieldType.contains("GEO") ||           // 地理位置（简写）
                                upperFieldType.equals("JSONB") ||           // JSONB类型
                                upperFieldType.equals("JSON");              // JSON类型
        
        // 判断字符串值是否像JSON（以{或[开头）
        String strValue = fieldValue.toString().trim();
        boolean looksLikeJson = strValue.startsWith("{") || strValue.startsWith("[");
        
        return isComplexType && looksLikeJson;
    }

    /**
     * 构建数据响应Map（包含字段类型信息）
     */
    protected Map<String, Object> buildDataResponse(MetadataBusinessEntityDO entity, Map<String, Object> data, List<MetadataEntityFieldDO> fields) {
        Map<String, Object> response = new HashMap<>();
        response.put("entityId", String.valueOf(entity.getId()));
        response.put("entityName", entity.getDisplayName());
        response.put("data", data);

        // 构建字段类型映射
        Map<String, String> fieldTypeMap = new HashMap<>();
        for (MetadataEntityFieldDO field : fields) {
            fieldTypeMap.put(field.getFieldName(), field.getFieldType());
        }
        response.put("fieldType", fieldTypeMap);

        return response;
    }

    /**
     * 使用指定的AnylineService根据ID查询数据
     */
    protected Map<String, Object> queryDataByIdWithService(AnylineService<?> service, String tableName, Object id, List<MetadataEntityFieldDO> fields) {
        String primaryKeyField = getPrimaryKeyFieldName(fields);

        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(primaryKeyField, id);

        // 检查表中是否有软删除字段
        boolean hasDeletedField = fields.stream()
                .anyMatch(field -> "deleted".equalsIgnoreCase(field.getFieldName()));

        if (hasDeletedField) {
            configStore.and("deleted", 0);
        }

        DataSet dataSet = service.querys(tableName, configStore);
        if (dataSet == null || dataSet.size() == 0) {
            return null;
        }

        DataRow dataRow = dataSet.getRow(0);
        return convertDataRowToMap(dataRow, fields);
    }

    /**
     * 使用指定的AnylineService校验数据是否存在
     */
    public void validateDataExistsWithService(AnylineService<?> service, String tableName, Object id, List<MetadataEntityFieldDO> fields) {
        Map<String, Object> existingData = queryDataByIdWithService(service, tableName, id, fields);
        if (existingData == null || existingData.isEmpty()) {
            throw exception(BUSINESS_ENTITY_NOT_EXISTS);
        }
    }

    /**
     * 处理自动编号字段
     */
    public void processAutoNumberFields(List<MetadataEntityFieldDO> fields, Map<String, Object> processedData) {
        for (MetadataEntityFieldDO field : fields) {
            try {
                // 检查字段是否配置了自动编号
                if (autoNumberService.hasAutoNumber(field.getId())) {
                    String fieldName = field.getFieldName();
                    
                    // 如果用户没有提供值，则生成自动编号
                    if (!processedData.containsKey(fieldName) || processedData.get(fieldName) == null) {
                        // 准备上下文数据，将当前的processedData作为上下文传递
                        Map<String, Object> contextData = new HashMap<>(processedData);
                        
                        // 为字段引用规则准备数据，使用字段ID作为key
                        for (MetadataEntityFieldDO f : fields) {
                            if (f.getFieldName() != null && processedData.containsKey(f.getFieldName())) {
                                contextData.put("field_" + f.getId(), processedData.get(f.getFieldName()));
                            }
                        }
                        
                        // 生成自动编号
                        String autoNumber = autoNumberService.generateNumber(field.getId(), contextData);
                        processedData.put(fieldName, autoNumber);
                        
                        log.info("为字段 " + fieldName + " 生成自动编号: " + autoNumber);
                    }
                }
            } catch (Exception e) {
                // 如果字段是必填的（数据库NOT NULL约束），自动编号生成失败应该抛出异常
                if (BooleanStatusEnum.isYes(field.getIsRequired())) {
                    log.error("必填字段 {} 自动编号生成失败: {}", field.getFieldName(), e.getMessage(), e);
                    throw exception(AUTO_NUMBER_GENERATE_FAILED, "字段[{}]自动编号生成失败: {}", 
                        field.getDisplayName(), e.getMessage());
                }
                // 非必填字段，自动编号生成失败不应该阻塞整个数据创建过程，记录警告日志
                log.warn("为字段 " + field.getFieldName() + " 生成自动编号失败: " + e.getMessage());
            }
        }
    }

    /**
     * 为表名添加双引号以处理PostgreSQL的大小写敏感性
     */
    public String quoteTableName(String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) {
            return tableName;
        }
        // 如果表名已经有引号，直接返回
        if (tableName.startsWith("\"") && tableName.endsWith("\"")) {
            return tableName;
        }
        // 为表名添加双引号
        return "\"" + tableName + "\"";
    }

    // ========== 抽象方法定义 ==========

    /**
     * 执行统一的数据处理流程（用于create操作）
     */
    public Map<String, Object> executeProcess(OperationType operationType, Long entityId, Map<String, Object> data,
                                               String methodCode) {
        return executeProcess(operationType, entityId, null, data, methodCode);
    }

    /**
     * 执行统一的数据处理流程（用于update/delete/get操作）
     *
     * @param operationType 操作类型
     * @param entityId 实体ID
     * @param id 数据ID（update/delete/get操作必填）
     * @param data 数据
     * @param methodCode 方法代码
     * @return 处理结果
     */
    public Map<String, Object> executeProcess(OperationType operationType, Long entityId, Object id, Map<String, Object> data,
                                               String methodCode) {
        log.info("开始执行" + operationType.getDescription() + "，实体ID：" + entityId + "，数据ID：" + id + "，方法：" + methodCode);

        try {
            //1. 校验实体存在
            MetadataBusinessEntityDO entity = validateEntityExists(entityId);

            //2. 校验字段列表存在
            List<MetadataEntityFieldDO> fields = getEntityFields(entityId);

            //3. 初始化上下文
            ProcessContext context = initializeContext(operationType, entity, fields, data, methodCode);
            context.setId(id); // 设置数据ID


            //4. 请求数据完整性校验（基础属性）
            validateDataIntegrity(data, fields);

            //5. 处理数据并设置默认值
            Map<String, Object> processedData = processDataAndSetDefaults(data, fields);

            context.setProcessedData(processedData);

            // 6. 功能权限校验
            validatePermission(context);//todo 暂未实现

            // 7. 数据标准化与补全
            standardizeData(context);//todo 暂未实现

            // 8. 初步数据校验------数据校验规则 ----核心功能!!!
            validateData(context);//todo 暂未实现

            // 9. 唯一性校验和条件校验
            validateUniqueness(context);//todo 暂未实现

            // 10. 前置自动化工作流触发
            executePreWorkflow(context);//暂未实现

            // 11. 数据编号
            generateDataNumber(context);//todo 暂未实现

            // 12. 数据存储
            storeData(context);//todo 实现了create的方法

            // 13. 后置自动化工作流触发
            executePostWorkflow(context);//todo 暂未实现

            // 14. 结果格式化
            Map<String, Object> result = formatResult(context);// 已实现

            // 15. 日志记录
            logProcess(context);

            return result;

        } catch (Exception e) {
            log.error("执行" + operationType.getDescription() + "异常，实体ID：" + entityId + "，方法：" + methodCode + "，异常：" + e.getMessage());
            throw new RuntimeException("执行" + operationType.getDescription() + "异常：" + e.getMessage(), e);
        }
    }

    protected void validateDataIntegrity(Map<String, Object> data, List<MetadataEntityFieldDO> fields) {
    }


    protected Map<String, Object> processDataAndSetDefaults(Map<String, Object> data, List<MetadataEntityFieldDO> fields) {

        return null;
    }

    // ========== 抽象方法 ==========

    /**
     * 0. 数据可用性校验
     */
    protected MetadataBusinessEntityDO validateDataAvailability(OperationType operationType, Long entityId, Map<String, Object> data,
                                            String methodCode, Object id) {

        //1. 校验实体确实存在

        // 1. 校验实体存在
        MetadataBusinessEntityDO entity = validateEntityExists(entityId);


        return entity;
    }

    /**
     * 1. 初始化上下文
     */
   protected ProcessContext initializeContext(OperationType operationType, MetadataBusinessEntityDO entityDO, List<MetadataEntityFieldDO> fields, Map<String, Object> data,
                                              String methodCode) {
       // Create a new ProcessContext instance
       ProcessContext processContext = new ProcessContext();
       processContext.setEntity(entityDO);

       processContext.setFields(fields);
       // Set operation type
       processContext.setOperationType(operationType);

       // Set entity details
       processContext.setEntityId(entityDO.getId());

       // Set data and method code
       processContext.setData(data);
       processContext.setMethodCode(methodCode);

       // Return the populated context


       // 5. 获取临时数据源服务
       MetadataDatasourceDO datasource = metadataDatasourceCoreService.getDatasource(entityDO.getDatasourceId());
       if (datasource == null) {
           throw exception(DATASOURCE_NOT_EXISTS);
       }

       AnylineService<?> temporaryService = temporaryDatasourceService.createTemporaryService(datasource);
       log.info("成功切换到数据源：{}", datasource.getCode());
       processContext.setTemporaryService(temporaryService);

       return processContext;
   }



    /**
     * 2. 功能权限校验
     */
    protected void validatePermission(ProcessContext context) {

        //todo
    }

    /**
     * 3. 数据标准化与补全
     */
    protected void standardizeData(ProcessContext context) {

        //todo
    }

    /**
     * 4. 初步数据校验
     */
    protected void validateData(ProcessContext context) {
        Long entityId = context.getEntityId();
        Map<String, Object> data = context.getData();
        List<MetadataEntityFieldDO> fields = context.getFields();
        Object id = context.getId();
        OperationType operationType = context.getOperationType();

        log.info("开始执行数据校验：entityId={}, 操作类型={}, 字段数量={}", entityId, operationType.getDescription(), fields.size());

        // 对于UPDATE操作，需要将ID添加到data中，以便唯一性校验时能够排除当前记录
        Map<String, Object> dataForValidation = data;
        if (operationType == OperationType.UPDATE && id != null) {
            // 查找主键字段名
            String primaryKeyField = fields.stream()
                .filter(f -> f.getIsPrimaryKey() != null && f.getIsPrimaryKey() == 1)
                .map(MetadataEntityFieldDO::getFieldName)
                .findFirst()
                .orElse("id");
            
            // 创建包含ID的临时数据副本用于校验
            dataForValidation = new java.util.HashMap<>(data);
            dataForValidation.put(primaryKeyField, id);
            log.info("UPDATE操作：将ID[{}]添加到校验数据中，字段名：{}", id, primaryKeyField);
        }

        // 使用校验管理器执行所有字段的校验
        validationManager.validateEntity(entityId, fields, dataForValidation);

        log.info("数据校验完成：entityId={}", entityId);
    }

    /**
     * 5. 唯一性校验和条件校验
     */
    protected void validateUniqueness(ProcessContext context) {

    }

    /**
     * 6. 前置自动化工作流触发
     */
    protected void executePreWorkflow(ProcessContext context) {

    }

    /**
     * 7. 数据编号
     */
    protected void generateDataNumber(ProcessContext context) {
        // 只有在创建操作时才处理自动编号字段
        if (context.getOperationType() == OperationType.CREATE) {
            processAutoNumberFields(context.getFields(), context.getProcessedData());
            log.info("新增操作：已触发自动编号规则");
        } else {
            log.debug("非新增操作（{}），跳过自动编号规则", context.getOperationType().getDescription());
        }
    }

    /**
     * 8. 数据存储
     */
    protected void storeData(ProcessContext context) {

        MetadataBusinessEntityDO entity = context.getEntity();

        Map<String, Object> processedData = context.getProcessedData();

        Long entityId = context.getEntityId();
        List<MetadataEntityFieldDO> fields = context.getFields();

        // 5. 获取临时数据源服务
        MetadataDatasourceDO datasource = metadataDatasourceCoreService.getDatasource(entity.getDatasourceId());
        if (datasource == null) {
            throw exception(DATASOURCE_NOT_EXISTS);
        }

        AnylineService<?> temporaryService = temporaryDatasourceService.createTemporaryService(datasource);
        log.info("成功切换到数据源：{}", datasource.getCode());

        // 6. 动态业务表忽略租户条件 - 使用TenantUtils.executeIgnore包装操作
        TenantUtils.executeIgnore(() -> {

            // 7. 执行插入
            if (log.isDebugEnabled()) {
                log.debug("createData -> processedData before insert: {}", processedData);
            }



            // 8. 查询插入后的完整数据
            Object primaryKeyValue = getPrimaryKeyValue(processedData, fields);
//            log.info("从处理数据中获取主键值: {}, 插入结果: {}", primaryKeyValue, insertResult);


        }); // TenantUtils.executeIgnore 闭合

    }


    /**
     * 9. 后置自动化工作流触发
     */
    protected void executePostWorkflow(ProcessContext context) {

    }

    /**
     * 10. 结果格式化
     */
    protected Map<String, Object> formatResult(ProcessContext context) {
        Map<String, Object> processedData = context.getProcessedData();

        MetadataBusinessEntityDO entity = context.getEntity();
        Long entityId = context.getEntityId();
        List<MetadataEntityFieldDO> fields = context.getFields();
        AnylineService<?> temporaryService = context.getTemporaryService();

        return TenantUtils.executeIgnore(() -> {
        Object primaryKeyValue = getPrimaryKeyValue(processedData, fields);
        // 确保主键值不为null
        if (primaryKeyValue == null) {
            log.warn("无法获取主键值，跳过查询插入后的数据，实体ID: {}, 表名: {}", entityId, entity.getTableName());
            // 返回插入的数据
            return buildDataResponse(entity, processedData, fields);
        }

        Map<String, Object> resultData = queryDataByIdWithService(temporaryService, quoteTableName(entity.getTableName()), primaryKeyValue, fields);
        // 9. 构建响应（移除多表写入逻辑，直接返回结果）
        return buildDataResponse(entity, resultData, fields);

    }); // TenantUtils.executeIgnore 闭合

    }

    /**
     * 11. 日志记录
     */
    protected void logProcess(ProcessContext context) {

    }

    // ========== 内部类 ==========

    /**
     * 操作类型枚举
     */
    protected enum OperationType {
        CREATE("创建数据"),
        UPDATE("更新数据"),
        DELETE("删除数据"),
        GET("查询数据"),
        GET_PAGE("分页查询数据"),
        GET_PAGE_OR("OR条件分页查询数据");

        private final String description;

        OperationType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 流程上下文
     */
    @Data
    protected static class ProcessContext {
        private OperationType operationType;
        private Long entityId;
        private Object id; // 数据ID，用于update/delete/get操作
        private Map<String, Object> data;
        private String methodCode;
        // 核心上下文字段
        private MetadataBusinessEntityDO entity;
        private List<MetadataEntityFieldDO> fields; // 实体字段列表
        private Map<String, Object> processedData; // 处理后的数据
        private AnylineService<?> temporaryService;

    }
}
