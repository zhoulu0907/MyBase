package com.cmsr.onebase.module.metadata.core.service.datamethod.datamethodImpl;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.enums.BooleanStatusEnum;
import com.cmsr.onebase.module.metadata.core.service.datamethod.AbstractMetadataDataMethodCoreService;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.DataRow;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.invalidParamException;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.ENTITY_FIELD_NOT_EXISTS;
@Slf4j
@Component
public class MetadataDataMethodUpdateImpl extends AbstractMetadataDataMethodCoreService {


    /**
     * 校验更新数据
     */
    protected void validateDataIntegrity(Map<String, Object> data, List<MetadataEntityFieldDO> fields) {
        // 更新时不校验必填，只校验数据类型等
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String fieldName = entry.getKey();
            MetadataEntityFieldDO field = fields.stream()
                    .filter(f -> f.getFieldName().equals(fieldName))
                    .findFirst()
                    .orElse(null);

            if (field == null) {
                throw exception(ENTITY_FIELD_NOT_EXISTS, fieldName);
            }

            // 不允许更新主键字段 - 使用新的枚举值：1-是，0-否
            if (BooleanStatusEnum.isYes(field.getIsPrimaryKey())) {
                throw invalidParamException("不允许更新主键字段");
            }

            // 不允许更新自动编号字段
            if (autoNumberService.hasAutoNumber(field.getId())) {
                throw invalidParamException("不允许更新自动编号字段[{}]", field.getDisplayName());
            }
        }
    }

    /**
     * 处理更新数据
     */
    protected Map<String, Object> processDataAndSetDefaults(Map<String, Object> data, List<MetadataEntityFieldDO> fields) {
        Map<String, Object> processedData = new HashMap<>();

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String fieldName = entry.getKey();
            MetadataEntityFieldDO field = fields.stream()
                    .filter(f -> f.getFieldName().equals(fieldName))
                    .findFirst()
                    .orElse(null);

            // 只处理非主键和非系统字段 - 使用新的枚举值：1-是，0-否
            if (field != null &&
                    !BooleanStatusEnum.isYes(field.getIsPrimaryKey()) &&
                    !BooleanStatusEnum.isYes(field.getIsSystemField())) {
                processedData.put(fieldName, entry.getValue());
            }
        }

        // 处理复杂类型字段（数组、对象等）的JSON序列化
        processComplexTypeFields(fields, processedData);

        return processedData;
    }

    /**
     * 处理复杂类型字段的JSON序列化
     * 对于数组和对象类型的字段值，需要序列化为JSON字符串存储到数据库
     *
     * @param fields 字段列表
     * @param processedData 待处理的数据
     */
    private void processComplexTypeFields(List<MetadataEntityFieldDO> fields, Map<String, Object> processedData) {
        for (MetadataEntityFieldDO field : fields) {
            String fieldName = field.getFieldName();
            String fieldType = field.getFieldType();
            
            if (fieldName == null || fieldType == null) {
                continue;
            }
            
            Object fieldValue = processedData.get(fieldName);
            if (fieldValue == null) {
                continue;
            }
            
            // 判断是否需要JSON序列化的字段类型
            if (needsJsonSerialization(fieldType, fieldValue)) {
                try {
                    // 将复杂对象序列化为JSON字符串
                    String jsonString = JsonUtils.toJsonString(fieldValue);
                    processedData.put(fieldName, jsonString);
                    log.debug("字段 {} (类型: {}) 的值已序列化为JSON: {}", fieldName, fieldType, jsonString);
                } catch (Exception e) {
                    log.error("字段 {} 的值序列化为JSON失败: {}", fieldName, e.getMessage(), e);
                    // 序列化失败时，保持原值
                }
            }
        }
    }

    /**
     * 判断字段类型是否需要JSON序列化
     * 
     * @param fieldType 字段类型
     * @param fieldValue 字段值
     * @return 是否需要序列化
     */
    private boolean needsJsonSerialization(String fieldType, Object fieldValue) {
        if (fieldType == null) {
            return false;
        }
        
        String upperFieldType = fieldType.toUpperCase();
        
        // 字段类型包含以下关键字的需要JSON序列化
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
        
        // 同时判断值是否为复杂对象（List或Map）
        boolean isComplexValue = fieldValue instanceof List || fieldValue instanceof Map;
        
        return isComplexType && isComplexValue;
    }

    /**
     * 更新操作的数据存储
     *
     * @param context 处理上下文
     */
    @Override
    protected void storeData(ProcessContext context) {
        MetadataBusinessEntityDO entity = context.getEntity();
        Map<String, Object> processedData = context.getProcessedData();
        Long entityId = context.getEntityId();
        List<MetadataEntityFieldDO> fields = context.getFields();
        AnylineService<?> temporaryService = context.getTemporaryService();
        Object id = context.getId();

        TenantUtils.executeIgnore(() -> {
            // 1. 校验数据存在
            validateDataExistsWithService(temporaryService, quoteTableName(entity.getTableName()), id, fields);

            // 2. 获取主键字段名
            String primaryKeyField = getPrimaryKeyFieldName(fields);

            // 3. 构建更新条件
            ConfigStore configStore = new DefaultConfigStore();
            configStore.and(primaryKeyField, id);

            // 4. 执行更新
            DataRow dataRow = new DataRow(processedData);
            long updateCount = temporaryService.update(quoteTableName(entity.getTableName()), dataRow, configStore);
            log.info("更新数据成功，实体ID: {}, 表名: {}, 更新记录数: {}", entityId, entity.getTableName(), updateCount);

            return null;
        });
    }




}
