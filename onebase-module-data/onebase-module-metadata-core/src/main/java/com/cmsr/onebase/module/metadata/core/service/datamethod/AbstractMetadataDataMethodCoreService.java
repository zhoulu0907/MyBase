package com.cmsr.onebase.module.metadata.core.service.datamethod;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.framework.uid.UidGenerator;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.domain.query.MetadataDataMethodRequestContext;
import com.cmsr.onebase.module.metadata.core.domain.query.MetadataPermissionContext;
import com.cmsr.onebase.module.metadata.core.domain.query.ProcessContext;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.service.datamethod.strategy.FieldValueStorageStrategy;
import com.cmsr.onebase.module.metadata.core.service.datamethod.strategy.FieldValueStorageStrategyFactory;
import com.cmsr.onebase.module.metadata.core.service.datamethod.strategy.FieldValueTransformMode;
import com.cmsr.onebase.module.metadata.core.service.datamethod.validator.ValidationManager;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataEntityFieldCoreService;
import com.cmsr.onebase.module.metadata.core.service.datasource.MetadataDatasourceCoreService;
import com.cmsr.onebase.module.metadata.core.dal.database.TemporaryDatasourceService;
import com.cmsr.onebase.module.metadata.core.service.number.AutoNumberService;
import com.cmsr.onebase.module.metadata.core.enums.BooleanStatusEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.entity.DataRow;
import org.anyline.entity.DataSet;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.service.AnylineService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.*;


/**
 * 数据方法核心服务类
 * <p>
 * 定义了基于设计文档的标准步流程框架，所有数据操作方法将遵循此流程。
 * 包含所有公共方法，供子类复用。
 *
 * @author zhangxihui
 * @date 2025-10-1
 */
@Slf4j
public abstract class AbstractMetadataDataMethodCoreService implements MetadataDataMethodCoreServiceV2 {


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
    @Resource
    protected com.cmsr.onebase.module.metadata.core.service.permission.PermissionManager permissionManager;
    @Resource
    protected com.cmsr.onebase.module.metadata.core.service.permission.PermissionQueryHelper permissionQueryHelper;
    @Resource
    protected FieldValueStorageStrategyFactory fieldValueStorageStrategyFactory;

    // ========== 公共方法 ==========

    @Override
    public Map<String, Object> executeProcess(MetadataDataMethodRequestContext methodCoreContext) {

        return doExecuteProcess(methodCoreContext);

    }

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
                } else if("DATETIME".equals(fieldType) && value instanceof Timestamp timestamp){
                    Instant instant = timestamp.toInstant();
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
                    String timeStr = dateTimeFormatter.format(instant);
                    resultMap.put(fieldName, timeStr);
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
     * @param fieldType  字段类型
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
                upperFieldType.contains("DEPARTMENT") ||    // 部门选择（包括DEPARTMENT、MULTI_DEPARTMENT）
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
                    if (!processedData.containsKey(fieldName)
                            || processedData.get(fieldName) == null
                    || (processedData.get(fieldName) instanceof String && StringUtils.isBlank((String) processedData.get(fieldName)))
                    ) {
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
     * 注意：达梦数据库不需要双引号，否则会导致大小写敏感问题
     */
    public String quoteTableName(String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) {
            return tableName;
        }
        // 如果表名已经有引号，直接返回
        if (tableName.startsWith("\"") && tableName.endsWith("\"")) {
            return tableName;
        }
        // 暂时不添加双引号，避免达梦数据库大小写匹配问题
        // TODO: 后续根据数据库类型动态决定是否添加双引号
        return tableName;
    }

    // ========== 抽象方法定义 ==========

    /**
     * 执行统一的数据处理流程（用于create操作）
     */
//    public Map<String, Object> executeProcess(MetadataDataMethodOpEnum operationType, Long entityId, Map<String, Object> data,
//                                               String methodCode) {
//        return executeProcess(operationType, entityId, null, data, methodCode);
//    }

    /**
     * 执行统一的数据处理流程（用于update/delete/get操作）
     *
     * @param requestContext 请求上下文
     * @return 标准处理结果
     * @author zhangxihui
     */
    public Map<String, Object> doExecuteProcess(MetadataDataMethodRequestContext requestContext) {

        Long entityId = requestContext.getEntityId();

        try {

            //1. 校验实体存在
            MetadataBusinessEntityDO entity = validateEntityExists(entityId);

            //2. 校验字段列表存在
            List<MetadataEntityFieldDO> fields = getEntityFields(entityId);
            //3. 初始化上下文
            ProcessContext context = initializeContext(entity, fields, requestContext);

            //4. 请求数据完整性校验（基础属性）
            validateDataIntegrity(requestContext.getData(), fields);

            //5. 处理数据并设置默认值
            Map<String, Object> processedData = processDataAndSetDefaults(requestContext.getData(), fields);

            context.setProcessedData(processedData);

            // 6. 功能权限校验
            if (requestContext.isEnableAuthCheck()) {
                validatePermission(context);
            }

            // 7. 初步数据校验------数据校验规则 ----核心功能!!!
            validateData(context);

            // 10. 前置自动化工作流触发
            executePreWorkflow(context);

            // 9. 数据编号
            generateDataNumber(context);

            // 10. 数据存储
            storeData(context);

            // 13. 后置自动化工作流触发
            executePostWorkflow(context);


            // 12. 获取数据
            getData(context);

            // 13. 结果格式化
            Map<String, Object> result = formatResult(context);// 已实现

            // 14. 日志记录
            logProcess(context);

            return result;

        } catch (Exception e) {
            log.error("执行元数据系统方法失败。请求上下文: [{}]", requestContext, ExceptionUtils.getRootCause(e));
            throw exception(DATA_METHOD_EXEC_FAIL, e.getMessage());
//            throw new RuntimeException("执行" + requestContext.getMetadataDataMethodOpEnum() + "异常：" + e.getMessage(), e);
        }
    }

    protected void validateDataIntegrity(Map<String, Object> data, List<MetadataEntityFieldDO> fields) {
    }

    /**
     * 将字段ID转换为字段名
     * 前端传入的数据是以字段ID为key，需要转换为字段名才能进行后续处理
     *
     * @param data   原始数据（字段ID为key）
     * @param fields 字段列表
     * @return 转换后的数据（字段名为key）
     */
    protected Map<String, Object> convertFieldIdToFieldName(Map<String, Object> data, List<MetadataEntityFieldDO> fields) {
        Map<String, Object> convertedData = new HashMap<>();

        // 构建字段ID到字段名的映射
        Map<String, String> fieldIdToNameMap = new HashMap<>();
        for (MetadataEntityFieldDO field : fields) {
            if (field.getId() != null && field.getFieldName() != null) {
                fieldIdToNameMap.put(String.valueOf(field.getId()), field.getFieldName());
            }
        }

        // 转换数据的key
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // 如果key是字段ID，则转换为字段名
            String fieldName = fieldIdToNameMap.getOrDefault(key, key);
            convertedData.put(fieldName, value);

            log.debug("字段转换: {} -> {}, 值: {}", key, fieldName, value);
        }

        log.info("字段ID转换完成，原始数据key数量: {}, 转换后数据key数量: {}", data.size(), convertedData.size());
        return convertedData;
    }

    protected Map<String, Object> processDataAndSetDefaults(Map<String, Object> data, List<MetadataEntityFieldDO> fields) {

        return null;
    }

    // ========== 抽象方法 ==========

    /**
     * 1. 初始化上下文
     */
    protected ProcessContext initializeContext(MetadataBusinessEntityDO entityDO, List<MetadataEntityFieldDO> fields, MetadataDataMethodRequestContext requestContext) {
        ProcessContext processContext = new ProcessContext();
        processContext.setRequestContext(requestContext);

        //如果追踪ID为空，那么创建一个新的追踪ID。如果不为空，则使用传入的追踪ID。
        if (requestContext.getTraceId() == null) {
            requestContext.setTraceId(UUID.randomUUID().toString());
        }
        processContext.setTraceId(requestContext.getTraceId());
        processContext.setEntity(entityDO);


        processContext.setFields(fields);
        processContext.setOperationType(requestContext.getMetadataDataMethodOpEnum());

        processContext.setEntityId(entityDO.getId());

        processContext.setData(requestContext.getData());
        processContext.setMethodCode(requestContext.getMethodCode());
        processContext.setId(requestContext.getId());
        processContext.setSubEntities(requestContext.getSubEntities());
        // 5. 获取临时数据源服务
        MetadataDatasourceDO datasource = metadataDatasourceCoreService.getDatasource(entityDO.getDatasourceId());
        if (datasource == null) {
            throw exception(DATASOURCE_NOT_EXISTS);
        }

        AnylineService<?> temporaryService = temporaryDatasourceService.createTemporaryService(datasource);
        log.info("成功切换到数据源：{}", datasource.getCode());
        processContext.setTemporaryService(temporaryService);

        MetadataPermissionContext permissionContext = requestContext.getPermissionContext();
        processContext.setMetadataPermissionContext(permissionContext);

        return processContext;
    }


    /**
     * 2. 功能权限校验
     */
    protected void validatePermission(ProcessContext context) {
        log.info("开始执行权限校验：entityId={}, operationType={}", 
                context.getEntityId(), 
                context.getOperationType());

        // 使用权限管理器执行完整的权限校验流程
        permissionManager.checkPermission(context);

        log.info("权限校验完成：entityId={}", context.getEntityId());
    }

    /**
     * 4. 初步数据校验
     */
    protected void validateData(ProcessContext context) {
        Long entityId = context.getEntityId();
        Map<String, Object> data = context.getData();
        List<MetadataEntityFieldDO> fields = context.getFields();
        Object id = context.getId();
        MetadataDataMethodOpEnum operationType = context.getOperationType();

        log.info("开始执行数据校验：entityId={}, 操作类型={}, 字段数量={}", entityId, operationType.getDescription(), fields.size());

        // 对于UPDATE操作，需要将ID添加到data中，以便唯一性校验时能够排除当前记录
        Map<String, Object> dataForValidation = data;
        if (operationType == MetadataDataMethodOpEnum.UPDATE && id != null) {
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
        validationManager.validateEntity(entityId, fields, dataForValidation, operationType);

        log.info("数据校验完成：entityId={}", entityId);
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
        if (context.getOperationType() == MetadataDataMethodOpEnum.CREATE) {
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

        //处理子表逻辑
        if (CollectionUtils.isNotEmpty(context.getSubEntities()) || MetadataDataMethodOpEnum.DELETE == context.getOperationType()) {
            handleSubEntities(context);
        }

    }

    /**
     * 处理子表逻辑
     * @param context
     */
    protected  void handleSubEntities(ProcessContext context){

    }


    /**
     * 9. 后置自动化工作流触发
     */
    protected void executePostWorkflow(ProcessContext context) {

    }

    protected Map<String, Object> getData(ProcessContext context) {
        MetadataBusinessEntityDO entity = context.getEntity();
        List<MetadataEntityFieldDO> fields = context.getFields();
        AnylineService<?> temporaryService = context.getTemporaryService();
        Object id = context.getId();
        if (entity == null || CollectionUtils.isEmpty(fields) || temporaryService == null || id == null) {
            return null;
        }

        return TenantUtils.executeIgnore(() -> {
            Map<String, Object> resultData = queryDataByIdWithService(
                    temporaryService,
                    quoteTableName(entity.getTableName()),
                    id,
                    fields
            );
            if (resultData == null) {
                return null;
            }
            applyFieldStorageStrategies(resultData, fields, FieldValueTransformMode.READ, context);
            Map<String, Object> filtered = filterQueryResultFields(resultData, context);
            context.setProcessedData(filtered);
            return filtered;
        });
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
                applyFieldStorageStrategies(processedData, fields, FieldValueTransformMode.READ, context);
                // 返回插入的数据
                return buildDataResponse(entity, processedData, fields);
            }

            Map<String, Object> resultData = queryDataByIdWithService(temporaryService, quoteTableName(entity.getTableName()), primaryKeyValue, fields);
            applyFieldStorageStrategies(resultData, fields, FieldValueTransformMode.READ, context);
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

    // 将name：value的格式变成id：value的格式
    public Map convertNameToId(Long entityId, Map<String, Object> map) {

        Map newData = new HashMap();// 存放id:value格式
        List<MetadataEntityFieldDO> targetfields = getEntityFields(entityId);

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String dataKey = entry.getKey();
            Object dataValue = entry.getValue();

            // 将data的key转换为大写后，与targetfields中的fieldName进行匹配
            String dataKeyUpper = dataKey.toUpperCase();
            for (MetadataEntityFieldDO field : targetfields) {
                if (field.getFieldName() != null && field.getFieldName().toUpperCase().equals(dataKeyUpper)) {
                    // 找到匹配的字段，使用fieldId作为key
                    newData.put(field.getId(), dataValue);
                    break;
                }
            }
        }

        // 插入新数据字段key为小写，更新和删除的时候从数据库获取的字段key为大写 统一转大写进行匹配判断
        Set<String> upperKeySet = map.keySet().stream().map(String::toUpperCase).collect(Collectors.toSet());

        // 值为null的字段也放到参数里，触发流程时需要全量的字段信息
        for (MetadataEntityFieldDO field : targetfields) {
            if(!upperKeySet.contains(field.getFieldName().toUpperCase())){
                newData.put(field.getId(), null);
            };
        }
        return newData;
    }

    /**
     * 根据字段类型应用存储策略，确保字段值形态一致
     *
     * @param data   待处理的数据
     * @param fields 字段定义
     */
    protected void applyFieldStorageStrategies(Map<String, Object> data, List<MetadataEntityFieldDO> fields) {
        applyFieldStorageStrategies(data, fields, FieldValueTransformMode.STORE);
    }

    protected void applyFieldStorageStrategies(Map<String, Object> data, List<MetadataEntityFieldDO> fields,
                                               FieldValueTransformMode mode) {
        applyFieldStorageStrategies(data, fields, mode, null);
    }

    /**
     * 根据字段类型应用存储策略，确保字段值形态一致（带完整上下文）
     *
     * @param data    待处理的数据
     * @param fields  字段定义
     * @param mode    转换模式
     * @param context 处理上下文（可选，某些策略需要访问上下文）
     */
    protected void applyFieldStorageStrategies(Map<String, Object> data, List<MetadataEntityFieldDO> fields,
                                               FieldValueTransformMode mode, ProcessContext context) {
        if (data == null || data.isEmpty() || CollectionUtils.isEmpty(fields) || fieldValueStorageStrategyFactory == null) {
            return;
        }
        Map<String, MetadataEntityFieldDO> fieldMap = fields.stream()
                .filter(field -> field.getFieldName() != null && !field.getFieldName().isEmpty())
                .collect(Collectors.toMap(MetadataEntityFieldDO::getFieldName,
                        Function.identity(), (origin, duplicate) -> origin));
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            MetadataEntityFieldDO metadataField = fieldMap.get(entry.getKey());
            if (metadataField == null) {
                continue;
            }
            FieldValueStorageStrategy strategy = fieldValueStorageStrategyFactory.getStrategy(metadataField.getFieldType());
            Object transformedValue;
            if (context != null) {
                // 使用带上下文的方法，允许策略访问 ProcessContext
                transformedValue = strategy.transform(entry.getValue(), mode, context, metadataField);
            } else {
                // 使用无上下文的方法，保持向后兼容
                transformedValue = strategy.transform(entry.getValue(), mode);
            }
            entry.setValue(transformedValue);
        }
    }

    // ========== 权限查询辅助方法 ==========

    /**
     * 应用查询权限过滤
     * 
     * 在查询前调用，向 ConfigStore 添加数据权限过滤条件
     * 供子类在 queryData、getData 等查询方法中使用
     *
     * @param configStore Anyline 查询配置
     * @param context 处理上下文
     */
    public void applyQueryPermissionFilter(org.anyline.data.param.ConfigStore configStore,
                                               ProcessContext context) {
        if (context.getMetadataPermissionContext() == null) {
            log.debug("权限上下文为空，跳过查询权限过滤");
            return;
        }

        permissionQueryHelper.applyQueryPermissionFilter(
                configStore,
                context.getMetadataPermissionContext(),
                context.getLoginUserCtx(),
                context.getFields()
        );
    }

    /**
     * 过滤查询结果中的字段
     * 
     * 在查询后调用，移除用户无权读取的字段
     * 供子类在 getData 等单条查询方法中使用
     *
     * @param data 查询结果数据
     * @param context 处理上下文
     * @return 过滤后的数据
     */
    protected Map<String, Object> filterQueryResultFields(Map<String, Object> data,
                                                           ProcessContext context) {
        if (context.getMetadataPermissionContext() == null) {
            return data;
        }

        return permissionQueryHelper.filterQueryResult(
                data,
                context.getMetadataPermissionContext(),
                context.getFields()
        );
    }

    /**
     * 批量过滤查询结果列表中的字段
     * 
     * 在查询后调用，移除用户无权读取的字段
     * 供子类在 queryData 等列表查询方法中使用
     *
     * @param dataList 查询结果列表
     * @param context 处理上下文
     * @return 过滤后的数据列表
     */
    protected List<Map<String, Object>> filterQueryResultListFields(List<Map<String, Object>> dataList,
                                                                      ProcessContext context) {
        if (context.getMetadataPermissionContext() == null) {
            return dataList;
        }

        return permissionQueryHelper.filterQueryResultList(
                dataList,
                context.getMetadataPermissionContext(),
                context.getFields()
        );
    }
}
