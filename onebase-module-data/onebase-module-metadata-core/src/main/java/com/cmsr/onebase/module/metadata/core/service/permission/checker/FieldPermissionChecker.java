package com.cmsr.onebase.module.metadata.core.service.permission.checker;

import cn.hutool.core.collection.CollectionUtil;
import com.cmsr.onebase.module.app.api.security.bo.FieldPermission;
import com.cmsr.onebase.module.app.api.security.bo.FieldPermissionItem;
import com.cmsr.onebase.module.metadata.core.dal.database.TemporaryDatasourceService;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.domain.query.MetadataPermissionContext;
import com.cmsr.onebase.module.metadata.core.domain.query.ProcessContext;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.service.datasource.MetadataDatasourceCoreService;
import com.cmsr.onebase.module.metadata.core.service.permission.PermissionChecker;
import com.cmsr.onebase.module.metadata.core.service.permission.exception.PermissionDeniedException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.entity.DataRow;
import org.anyline.entity.DataSet;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.service.AnylineService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.DATASOURCE_NOT_EXISTS;

/**
 * 字段权限校验器
 *
 * 校验用户对特定字段的访问权限，包括：
 * 1. 字段可读权限（canRead）- 用于查询操作
 * 2. 字段可写权限（canEdit）- 用于创建和更新操作
 * 3. 字段下载权限（canDownload，针对文件类字段）
 * 实现说明：
 * 1. 数据中的key可能是字段ID（字符串形式）或字段名，需要统一处理
 * 2. 系统字段不受字段权限控制，始终允许访问。系统字段的判断通过MetadataEntityFieldDO的isSystemField字段来确定
 * 3. 字段权限配置来自app_auth_field表，通过FieldPermission对象传递
 *
 * @author zhangxihui
 * @date 2025-10-31
 */
@Slf4j
@Component
public class FieldPermissionChecker implements PermissionChecker {

    @Resource
    private MetadataDatasourceCoreService metadataDatasourceCoreService;
    @Resource
    private TemporaryDatasourceService temporaryDatasourceService;

    private static final String PERMISSION_TYPE = "字段权限";

    @Override
    public String getPermissionType() {
        return PERMISSION_TYPE;
    }

    @Override
    public boolean supports(ProcessContext context) {
        return context != null
                && context.getMetadataPermissionContext() != null
                && context.getMetadataPermissionContext().getFieldPermission() != null
                && context.getData() != null
                && !context.getData().isEmpty();
    }

    @Override
    public void check(ProcessContext context) {
        MetadataPermissionContext permissionContext = context.getMetadataPermissionContext();
        FieldPermission fieldPermission = permissionContext.getFieldPermission();

        if (fieldPermission == null) {
            log.warn("字段权限对象为空，跳过字段权限校验");
            return;
        }

        // 如果配置了全部拒绝，直接抛出异常
        if (fieldPermission.isAllDenied()) {
            throw new PermissionDeniedException(
                    PERMISSION_TYPE,
                    "ALL_DENIED",
                    "无权访问任何字段"
            );
        }

        // 如果配置了全部允许，直接通过
        if (fieldPermission.isAllAllowed()) {
            log.debug("字段权限：全部允许");
            return;
        }

        MetadataDataMethodOpEnum operationType = context.getOperationType();
        Map<String, Object> data = context.getProcessedData() != null
                ? context.getProcessedData()
                : context.getData();

        // 根据操作类型进行不同的字段权限校验
        switch (operationType) {
            case CREATE:
                // 创建操作：检查字段的编辑权限
                checkFieldEditPermission(fieldPermission, data, context.getFields());
                break;

            case UPDATE:
                // 更新操作：只检查实际被修改的字段的编辑权限
                checkFieldEditPermissionForUpdate(fieldPermission, data, context);
                break;

            default:
                log.debug("操作类型{}不需要进行字段权限校验", operationType);
        }

        log.debug("字段权限校验通过：operationType={}, fieldCount={}",
                operationType,
                data.size());
    }

    @Override
    public int getOrder() {
        return 30;
    }

    /**
     * 检查字段的编辑权限（用于更新操作）
     *
     * 更新操作时，只检查实际被修改的字段是否有编辑权限
     * 如果某个字段在请求中没有出现（即没有修改），即使该字段没有编辑权限，也不应该报错
     *
     * @param fieldPermission 字段权限对象
     * @param data 请求数据（key可能是字段ID字符串或字段名）
     * @param context 处理上下文
     */
    private void checkFieldEditPermissionForUpdate(FieldPermission fieldPermission,
                                                   Map<String, Object> data,
                                                   ProcessContext context) {
        List<MetadataEntityFieldDO> fields = context.getFields();
        Object id = context.getId();
        MetadataBusinessEntityDO entity = context.getEntity();

        // 1. 查询原始数据
        MetadataDatasourceDO datasource = metadataDatasourceCoreService.getDatasource(entity.getDatasourceId());
        if (datasource == null) {
            throw exception(DATASOURCE_NOT_EXISTS);
        }
        AnylineService<?> temporaryService = temporaryDatasourceService.createTemporaryService(datasource);
        Map<String, Object> originalData = queryOriginalData(temporaryService, entity, id, fields);
        if (originalData == null || originalData.isEmpty()) {
            log.error("无法查询到原始数据，使用全量字段权限检查：id={}", id);
            throw new PermissionDeniedException(
                    PERMISSION_TYPE,
                    "DATA_NOT_EXIST",
                    "无法查询到原始数据"
            );
        }

        // 2. 统一转换为字段ID为key的格式，便于对比
        Map<Long, Object> originalDataById = convertToFieldIdMap(originalData, fields);
        Map<Long, Object> requestDataById = convertToFieldIdMap(data, fields);

        // 3. 找出实际被修改的字段
        Set<Long> modifiedFieldIds = findModifiedFields(originalDataById, requestDataById);

        if (modifiedFieldIds.isEmpty()) {
            log.info("更新操作：没有字段被修改，跳过字段权限检查");
            return;
        }

        log.info("更新操作：检测到{}个字段被修改，fieldIds={}", modifiedFieldIds.size(), modifiedFieldIds);

        // 4. 只对实际被修改的字段进行权限检查
        checkModifiedFieldsPermission(fieldPermission, modifiedFieldIds, fields);
    }

    /**
     * 检查字段的编辑权限（用于创建操作）
     *
     * 用于CREATE操作，确保用户只能编辑有权限的字段
     * 如果数据中包含无权编辑的字段，将抛出PermissionDeniedException异常
     *
     * @param fieldPermission 字段权限对象
     * @param data 数据（key可能是字段ID字符串或字段名）
     * @param fields 实体字段列表，用于字段ID和字段名的映射
     */
    private void checkFieldEditPermission(FieldPermission fieldPermission,
                                          Map<String, Object> data,
                                          List<MetadataEntityFieldDO> fields) {
        List<FieldPermissionItem> permissionFields = fieldPermission.getFields();

        if (permissionFields == null || permissionFields.isEmpty()) {
            throw new PermissionDeniedException(
                    PERMISSION_TYPE,
                    "NO_FIELDS",
                    "未配置字段权限"
            );
        }

        // 构建可编辑字段ID集合
        Set<Long> editableFieldIds = permissionFields.stream()
                .filter(FieldPermissionItem::isCanEdit)
                .map(FieldPermissionItem::getFieldId)
                .collect(Collectors.toSet());

        if (editableFieldIds.isEmpty()) {
            throw new PermissionDeniedException(
                    PERMISSION_TYPE,
                    "NO_EDITABLE_FIELDS",
                    "没有可编辑的字段权限"
            );
        }

        // 构建字段映射：字段ID -> 字段名，字段名 -> 字段ID
        Map<String, Long> fieldNameToIdMap = new HashMap<>();
        Map<Long, String> fieldIdToNameMap = new HashMap<>();
        if (fields != null) {
            for (MetadataEntityFieldDO field : fields) {
                if (field.getId() != null && field.getFieldName() != null) {
                    fieldNameToIdMap.put(field.getFieldName().toLowerCase(), field.getId());
                    fieldIdToNameMap.put(field.getId(), field.getFieldName());
                }
            }
        }

        // 检查数据中的每个字段是否有编辑权限
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();

            // 系统字段跳过权限检查
            if (isSystemField(key, fields, fieldNameToIdMap)) {
                log.debug("跳过系统字段的编辑权限校验：{}", key);
                continue;
            }

            Long fieldId = fieldNameToIdMap.get(key.toLowerCase());
            if (fieldId == null) {
                // 无法解析字段ID，可能是无效字段，记录警告但继续处理
                log.warn("无法解析字段ID，跳过权限校验：key={}", key);
                continue;
            }

            if (!editableFieldIds.contains(fieldId)) {
                String fieldName = fieldIdToNameMap.getOrDefault(fieldId, key);
                throw new PermissionDeniedException(
                        PERMISSION_TYPE,
                        "FIELD_EDIT",
                        String.format("无权编辑字段：fieldId=%d, fieldName=%s", fieldId, fieldName)
                );
            }
        }

        log.debug("字段编辑权限校验通过，可编辑字段数：{}", editableFieldIds.size());
    }

    /**
     * 判断是否为系统字段
     *
     * 系统字段不受字段权限控制，始终允许访问
     * 通过查找对应的MetadataEntityFieldDO对象，检查其isSystemField字段来判断
     *
     * @param key 字段标识（可能是字段ID字符串或字段名）
     * @param fields 字段列表
     * @param fieldNameToIdMap 字段名到字段ID的映射（可为null）
     * @return true表示是系统字段
     */
    private boolean isSystemField(String key, List<MetadataEntityFieldDO> fields, Map<String, Long> fieldNameToIdMap) {
        if (StringUtils.isEmpty(key) || CollectionUtil.isEmpty(fieldNameToIdMap)) {
            return false;
        }

        // 获取fieldId
        Long fieldId = fieldNameToIdMap.get(key.toLowerCase());

        // 如果解析到了字段ID，通过字段ID查找
        if (fieldId != null) {
            for (MetadataEntityFieldDO field : fields) {
                if (field.getId() != null && field.getId().equals(fieldId)) {
                    return field.getIsSystemField() != null && field.getIsSystemField() == 1;
                }
            }
        }

        // 如果通过字段ID没找到，尝试通过字段名查找
        for (MetadataEntityFieldDO field : fields) {
            if (field.getFieldName() != null && field.getFieldName().equalsIgnoreCase(key)) {
                return field.getIsSystemField() != null && field.getIsSystemField() == 1;
            }
        }

        // 如果找不到对应的字段，默认返回false（不是系统字段）
        return false;
    }

    /**
     * 查询原始数据
     *
     * 根据ID查询更新前的原始数据
     *
     * @param temporaryService 临时数据源服务
     * @param entity 实体对象
     * @param id 数据ID
     * @param fields 字段列表
     * @return 原始数据（字段名为key）
     */
    private Map<String, Object> queryOriginalData(AnylineService<?> temporaryService,
                                                  MetadataBusinessEntityDO entity,
                                                  Object id,
                                                  List<MetadataEntityFieldDO> fields) {
        if (temporaryService == null || entity == null || id == null) {
            return null;
        }

        try {
            // 获取主键字段名
            String primaryKeyField = getPrimaryKeyFieldName(fields);

            // 构建查询条件
            DefaultConfigStore configStore = new DefaultConfigStore();
            configStore.and(primaryKeyField, id);

            // 查询数据
            DataSet dataSet = temporaryService.querys(quoteTableName(entity.getTableName()), configStore);
            if (dataSet == null || dataSet.size() == 0) {
                log.warn("查询原始数据为空：id={}, tableName={}", id, entity.getTableName());
                return null;
            }

            DataRow dataRow = dataSet.getRow(0);
            return dataRow.map();
        } catch (Exception e) {
            log.error("查询原始数据失败：id={}, tableName={}, error={}", id, entity.getTableName(), e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取主键字段名
     *
     * @param fields 字段列表
     * @return 主键字段名
     */
    private String getPrimaryKeyFieldName(List<MetadataEntityFieldDO> fields) {

        // 查找名为id的字段
        for (MetadataEntityFieldDO field : fields) {
            if ("id".equalsIgnoreCase(field.getFieldName())) {
                return field.getFieldName();
            }
        }

        // 默认返回id
        return "id";
    }

    /**
     * 将数据转换为字段ID为key的Map
     *
     * 统一数据格式，便于对比原始数据和请求数据
     *
     * @param data 原始数据（key可能是字段ID字符串或字段名）
     * @param fields 字段列表
     * @return 转换后的数据（字段ID为key）
     */
    private Map<Long, Object> convertToFieldIdMap(Map<String, Object> data, List<MetadataEntityFieldDO> fields) {
        Map<Long, Object> result = new HashMap<>();

        // 构建字段名到字段ID的映射
        Map<String, Long> fieldNameToIdMap = new HashMap<>();
        for (MetadataEntityFieldDO field : fields) {
            if (field.getId() != null && field.getFieldName() != null) {
                fieldNameToIdMap.put(field.getFieldName().toLowerCase(), field.getId());
            }
        }

        // 转换数据
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // 跳过系统字段
            if (isSystemField(key, fields, fieldNameToIdMap)) {
                continue;
            }

            Long fieldId = fieldNameToIdMap.get(key.toLowerCase());
            if (fieldId != null) {
                result.put(fieldId, value);
            }
        }

        return result;
    }

    /**
     * 找出实际被修改的字段
     *
     * 对比原始数据和请求数据，找出值发生变化的字段
     *
     * @param originalData 原始数据（字段ID为key）
     * @param requestData 请求数据（字段ID为key）
     * @return 被修改的字段ID集合
     */
    private Set<Long> findModifiedFields(Map<Long, Object> originalData, Map<Long, Object> requestData) {
        Set<Long> modifiedFieldIds = new HashSet<>();

        // 检查请求数据中的字段是否与原始数据不同
        for (Map.Entry<Long, Object> entry : requestData.entrySet()) {
            Long fieldId = entry.getKey();
            Object requestValue = entry.getValue();
            Object originalValue = originalData.get(fieldId);

            // 如果值不同，则认为字段被修改
            if (!isValueEqual(originalValue, requestValue)) {
                modifiedFieldIds.add(fieldId);
                log.debug("检测到字段被修改：fieldId={}, 原值={}, 新值={}", fieldId, originalValue, requestValue);
            }
        }

        return modifiedFieldIds;
    }

    /**
     * 判断两个值是否相等
     *
     * 处理null值、字符串和数字类型的比较
     *
     * @param value1 值1
     * @param value2 值2
     * @return true表示相等
     */
    private boolean isValueEqual(Object value1, Object value2) {
        // 都为空，认为相等
        if (value1 == null && value2 == null) {
            return true;
        }

        // 一个为空，一个不为空，认为不相等
        if (value1 == null || value2 == null) {
            return false;
        }

        // 字符串比较（忽略大小写和前后空格）
        if (value1 instanceof String && value2 instanceof String) {
            return value1.toString().trim().equals(value2.toString().trim());
        }

        // 直接比较
        return value1.equals(value2);
    }

    /**
     * 检查被修改字段的权限
     *
     * 只对实际被修改的字段进行编辑权限检查
     *
     * @param fieldPermission 字段权限对象
     * @param modifiedFieldIds 被修改的字段ID集合
     * @param fields 字段列表
     */
    private void checkModifiedFieldsPermission(FieldPermission fieldPermission,
                                               Set<Long> modifiedFieldIds,
                                               List<MetadataEntityFieldDO> fields) {
        List<FieldPermissionItem> permissionFields = fieldPermission.getFields();

        if (permissionFields == null || permissionFields.isEmpty()) {
            throw new PermissionDeniedException(
                    PERMISSION_TYPE,
                    "NO_FIELDS",
                    "未配置字段权限"
            );
        }

        // 构建可编辑字段ID集合
        Set<Long> editableFieldIds = permissionFields.stream()
                .filter(FieldPermissionItem::isCanEdit)
                .map(FieldPermissionItem::getFieldId)
                .collect(Collectors.toSet());

        if (editableFieldIds.isEmpty()) {
            throw new PermissionDeniedException(
                    PERMISSION_TYPE,
                    "NO_EDITABLE_FIELDS",
                    "没有可编辑的字段权限"
            );
        }

        // 构建字段ID到字段名的映射
        Map<Long, String> fieldIdToNameMap = new HashMap<>();
        for (MetadataEntityFieldDO field : fields) {
            if (field.getId() != null && field.getFieldName() != null) {
                fieldIdToNameMap.put(field.getId(), field.getFieldName());
            }
        }

        // 检查被修改的字段是否有编辑权限
        for (Long fieldId : modifiedFieldIds) {
            if (!editableFieldIds.contains(fieldId)) {
                String fieldName = fieldIdToNameMap.getOrDefault(fieldId, String.valueOf(fieldId));
                throw new PermissionDeniedException(
                        PERMISSION_TYPE,
                        "FIELD_EDIT",
                        String.format("无权编辑字段：fieldId=%d, fieldName=%s", fieldId, fieldName)
                );
            }
        }

        log.debug("被修改字段的编辑权限校验通过，修改字段数：{}", modifiedFieldIds.size());
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
}

