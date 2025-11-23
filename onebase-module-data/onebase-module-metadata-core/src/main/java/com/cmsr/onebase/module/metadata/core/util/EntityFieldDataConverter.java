package com.cmsr.onebase.module.metadata.core.util;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataEntityFieldCoreService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 实体字段数据转换工具类
 * <p>
 * 统一管理字段ID与字段名称之间的转换逻辑，提供类型安全的数据转换方法。
 * 主要用于处理前端传入的字段ID格式数据与系统内部使用的字段名称格式数据之间的转换。
 * </p>
 * <p>
 * <b>数据格式说明：</b>
 * <ul>
 *   <li><b>字段ID格式 (IdKeyMap)</b>：Map&lt;Long, Object&gt;，key为字段ID，value为字段值</li>
 *   <li><b>字段名称格式 (NameKeyMap)</b>：Map&lt;String, Object&gt;，key为字段名称，value为字段值</li>
 * </ul>
 * </p>
 * <p>
 * <b>转换规则：</b>
 * <ul>
 *   <li>对于无法匹配到字段定义的字段ID，将自动忽略，避免无效数据传递</li>
 *   <li>对于重复的字段ID，取最后一个值</li>
 *   <li>空值字段会被保留（null值也会被传递）</li>
 * </ul>
 * </p>
 *
 * @author system
 * @date 2025-01-25
 */
@Slf4j
public class EntityFieldDataConverter {

    /**
     * 将字段ID为键的Map转换为字段名称为键的Map
     * <p>
     * <b>使用场景：</b>前端传入的数据通常以字段ID为key，需要转换为字段名称才能进行后续处理。
     * </p>
     * <p>
     * <b>示例：</b>
     * <pre>
     * 输入：
     *   entityId = 1001L
     *   idKeyMap = {1001L: "张三", 1002L: 25}
     * 
     * 假设字段映射关系：
     *   1001L → "name"
     *   1002L → "age"
     * 
     * 输出：
     *   {name: "张三", age: 25}
     * </pre>
     * </p>
     *
     * @param entityId           实体ID，用于查询字段定义
     * @param idKeyMap           以字段ID为key的Map，格式：Map&lt;Long, Object&gt;
     * @param fieldCoreService   字段核心服务，用于查询字段定义
     * @return 以字段名称为key的Map，格式：Map&lt;String, Object&gt;
     *         如果输入为空，返回空的不可变Map
     * @throws IllegalArgumentException 当entityId为null时抛出
     */
    public static Map<String, Object> convertIdKeyMapToNameKeyMap(
            Long entityId, 
            Map<Long, Object> idKeyMap,
            MetadataEntityFieldCoreService fieldCoreService) {
        
        // 参数校验
        if (entityId == null) {
            throw new IllegalArgumentException("实体ID不能为空");
        }
        
        if (idKeyMap == null || idKeyMap.isEmpty()) {
            log.debug("字段ID Map为空，返回空Map。entityId={}", entityId);
            return Collections.emptyMap();
        }
        
        if (fieldCoreService == null) {
            throw new IllegalArgumentException("字段核心服务不能为空");
        }
        
        try {
            // 查询实体字段定义
            List<MetadataEntityFieldDO> fields = fieldCoreService.getEntityFieldListByEntityId(entityId);
            
            if (CollectionUtils.isEmpty(fields)) {
                log.warn("实体字段定义为空，无法进行转换。entityId={}", entityId);
                return Collections.emptyMap();
            }
            
            // 构建字段ID到字段名称的映射
            Map<Long, String> idToNameMap = buildIdToNameMap(fields);
            
            // 统计转换信息
            int totalFields = idKeyMap.size();
            int matchedFields = 0;
            List<Long> unmatchedFieldIds = new ArrayList<>();
            
            // 执行转换
            Map<String, Object> resultMap = new LinkedHashMap<>(idKeyMap.size());
            for (Map.Entry<Long, Object> entry : idKeyMap.entrySet()) {
                Long fieldId = entry.getKey();
                Object value = entry.getValue();
                
                if (fieldId == null) {
                    log.warn("字段ID为null，跳过该字段。entityId={}", entityId);
                    continue;
                }
                
                String fieldName = idToNameMap.get(fieldId);
                if (fieldName == null) {
                    unmatchedFieldIds.add(fieldId);
                    log.debug("字段ID未匹配到字段名称，跳过。entityId={}, fieldId={}", entityId, fieldId);
                    continue;
                }
                
                resultMap.put(fieldName, value);
                matchedFields++;
            }
            
            // 记录转换结果日志
            if (!unmatchedFieldIds.isEmpty()) {
                log.warn("字段转换完成，部分字段ID未匹配到字段名称。entityId={}, 总字段数={}, 匹配成功={}, 未匹配字段ID={}",
                        entityId, totalFields, matchedFields, unmatchedFieldIds);
            } else {
                log.debug("字段转换完成。entityId={}, 总字段数={}, 全部匹配成功", entityId, totalFields);
            }
            
            return resultMap;
            
        } catch (Exception e) {
            log.error("字段ID转换为字段名称失败。entityId={}, error={}", entityId, e.getMessage(), e);
            throw new RuntimeException("字段数据转换失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 将字段名称为键的Map转换为字段ID为键的Map
     * <p>
     * <b>使用场景：</b>需要将系统内部的字段名称格式数据转换为前端使用的字段ID格式。
     * </p>
     * <p>
     * <b>示例：</b>
     * <pre>
     * 输入：
     *   entityId = 1001L
     *   nameKeyMap = {name: "张三", age: 25}
     * 
     * 假设字段映射关系：
     *   "name" → 1001L
     *   "age" → 1002L
     * 
     * 输出：
     *   {1001L: "张三", 1002L: 25}
     * </pre>
     * </p>
     *
     * @param entityId           实体ID，用于查询字段定义
     * @param nameKeyMap         以字段名称为key的Map，格式：Map&lt;String, Object&gt;
     * @param fieldCoreService   字段核心服务，用于查询字段定义
     * @return 以字段ID为key的Map，格式：Map&lt;Long, Object&gt;
     *         如果输入为空，返回空的不可变Map
     * @throws IllegalArgumentException 当entityId为null时抛出
     */
    public static Map<Long, Object> convertNameKeyMapToIdKeyMap(
            Long entityId,
            Map<String, Object> nameKeyMap,
            MetadataEntityFieldCoreService fieldCoreService) {
        
        // 参数校验
        if (entityId == null) {
            throw new IllegalArgumentException("实体ID不能为空");
        }
        
        if (nameKeyMap == null || nameKeyMap.isEmpty()) {
            log.debug("字段名称Map为空，返回空Map。entityId={}", entityId);
            return Collections.emptyMap();
        }
        
        if (fieldCoreService == null) {
            throw new IllegalArgumentException("字段核心服务不能为空");
        }
        
        try {
            // 查询实体字段定义
            List<MetadataEntityFieldDO> fields = fieldCoreService.getEntityFieldListByEntityId(entityId);
            
            if (CollectionUtils.isEmpty(fields)) {
                log.warn("实体字段定义为空，无法进行转换。entityId={}", entityId);
                return Collections.emptyMap();
            }
            
            // 构建字段名称到字段ID的映射
            Map<String, Long> nameToIdMap = buildNameToIdMap(fields);
            
            // 统计转换信息
            int totalFields = nameKeyMap.size();
            int matchedFields = 0;
            List<String> unmatchedFieldNames = new ArrayList<>();
            
            // 执行转换
            Map<Long, Object> resultMap = new LinkedHashMap<>(nameKeyMap.size());
            for (Map.Entry<String, Object> entry : nameKeyMap.entrySet()) {
                String fieldName = entry.getKey();
                Object value = entry.getValue();
                
                if (!StringUtils.hasText(fieldName)) {
                    log.warn("字段名称为空，跳过该字段。entityId={}", entityId);
                    continue;
                }
                
                Long fieldId = nameToIdMap.get(fieldName);
                if (fieldId == null) {
                    unmatchedFieldNames.add(fieldName);
                    log.debug("字段名称未匹配到字段ID，跳过。entityId={}, fieldName={}", entityId, fieldName);
                    continue;
                }
                
                resultMap.put(fieldId, value);
                matchedFields++;
            }
            
            // 记录转换结果日志
            if (!unmatchedFieldNames.isEmpty()) {
                log.warn("字段转换完成，部分字段名称未匹配到字段ID。entityId={}, 总字段数={}, 匹配成功={}, 未匹配字段名称={}",
                        entityId, totalFields, matchedFields, unmatchedFieldNames);
            } else {
                log.debug("字段转换完成。entityId={}, 总字段数={}, 全部匹配成功", entityId, totalFields);
            }
            
            return resultMap;
            
        } catch (Exception e) {
            log.error("字段名称转换为字段ID失败。entityId={}, error={}", entityId, e.getMessage(), e);
            throw new RuntimeException("字段数据转换失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 转换排序字段：支持字段ID或字段名称，若传入字段ID则转换为字段名称
     * <p>
     * <b>使用场景：</b>前端可能传入字段ID或字段名称作为排序字段，统一转换为字段名称。
     * </p>
     * <p>
     * <b>示例：</b>
     * <pre>
     * 输入：
     *   entityId = 1001L
     *   sortField = "1001"  (字段ID)
     * 
     * 假设字段映射关系：
     *   1001L → "name"
     * 
     * 输出：
     *   "name"
     * 
     * 如果输入的是字段名称（如"age"），则原样返回
     * </pre>
     * </p>
     *
     * @param entityId           实体ID，用于查询字段定义
     * @param sortField          排序字段，可能是字段ID（数字字符串）或字段名称
     * @param fieldCoreService   字段核心服务，用于查询字段定义
     * @return 字段名称，如果输入为空或无效则返回null
     */
    public static String convertSortFieldToName(
            Long entityId,
            String sortField,
            MetadataEntityFieldCoreService fieldCoreService) {
        
        if (!StringUtils.hasText(sortField)) {
            return sortField;
        }
        
        if (entityId == null || fieldCoreService == null) {
            log.warn("参数不完整，无法转换排序字段。entityId={}, sortField={}", entityId, sortField);
            return sortField;
        }
        
        try {
            // 尝试将排序字段解析为字段ID（Long类型）
            Long fieldId;
            try {
                fieldId = Long.valueOf(sortField.trim());
            } catch (NumberFormatException e) {
                // 不是数字字符串，直接按字段名称处理
                log.debug("排序字段不是数字，按字段名称处理。entityId={}, sortField={}", entityId, sortField);
                return sortField;
            }
            
            // 查询字段定义并查找对应的字段名称
            List<MetadataEntityFieldDO> fields = fieldCoreService.getEntityFieldListByEntityId(entityId);
            if (CollectionUtils.isEmpty(fields)) {
                log.warn("实体字段定义为空，无法转换排序字段。entityId={}, sortField={}", entityId, sortField);
                return sortField;
            }
            
            for (MetadataEntityFieldDO field : fields) {
                if (field.getId() != null && field.getId().equals(fieldId)) {
                    String fieldName = field.getFieldName();
                    log.debug("排序字段ID转换为字段名称。entityId={}, fieldId={}, fieldName={}", 
                            entityId, fieldId, fieldName);
                    return fieldName;
                }
            }
            
            log.warn("未找到对应的字段定义，保持原值。entityId={}, fieldId={}", entityId, fieldId);
            return sortField;
            
        } catch (Exception e) {
            log.error("转换排序字段失败，保持原值。entityId={}, sortField={}, error={}", 
                    entityId, sortField, e.getMessage(), e);
            return sortField;
        }
    }
    
    /**
     * 构建字段ID到字段名称的映射
     *
     * @param fields 字段定义列表
     * @return 字段ID到字段名称的映射
     */
    private static Map<Long, String> buildIdToNameMap(List<MetadataEntityFieldDO> fields) {
        Map<Long, String> result = new LinkedHashMap<>();
        for (MetadataEntityFieldDO field : fields) {
            if (field.getId() != null && StringUtils.hasText(field.getFieldName())) {
                Long fieldId = field.getId();
                String fieldName = field.getFieldName();
                if (result.containsKey(fieldId)) {
                    log.warn("发现重复的字段ID，保留第一个。fieldId={}, existing={}, replacement={}", 
                            fieldId, result.get(fieldId), fieldName);
                } else {
                    result.put(fieldId, fieldName);
                }
            }
        }
        return result;
    }
    
    /**
     * 构建字段名称到字段ID的映射
     *
     * @param fields 字段定义列表
     * @return 字段名称到字段ID的映射
     */
    private static Map<String, Long> buildNameToIdMap(List<MetadataEntityFieldDO> fields) {
        Map<String, Long> result = new LinkedHashMap<>();
        for (MetadataEntityFieldDO field : fields) {
            if (field.getId() != null && StringUtils.hasText(field.getFieldName())) {
                String fieldName = field.getFieldName();
                Long fieldId = field.getId();
                if (result.containsKey(fieldName)) {
                    log.warn("发现重复的字段名称，保留第一个。fieldName={}, existing={}, replacement={}", 
                            fieldName, result.get(fieldName), fieldId);
                } else {
                    result.put(fieldName, fieldId);
                }
            }
        }
        return result;
    }
    
    /**
     * 验证字段数据是否有效
     * <p>
     * 检查Map中的字段ID或字段名称是否都在实体字段定义中存在。
     * </p>
     *
     * @param entityId           实体ID
     * @param idKeyMap           以字段ID为key的Map（可选）
     * @param nameKeyMap         以字段名称为key的Map（可选）
     * @param fieldCoreService   字段核心服务
     * @return 验证结果，包含无效字段ID列表和无效字段名称列表
     */
    public static ValidationResult validateFieldData(
            Long entityId,
            Map<Long, Object> idKeyMap,
            Map<String, Object> nameKeyMap,
            MetadataEntityFieldCoreService fieldCoreService) {
        
        ValidationResult result = new ValidationResult();
        
        if (entityId == null || fieldCoreService == null) {
            result.addError("实体ID或字段核心服务不能为空");
            return result;
        }
        
        try {
            List<MetadataEntityFieldDO> fields = fieldCoreService.getEntityFieldListByEntityId(entityId);
            if (CollectionUtils.isEmpty(fields)) {
                result.addError("实体字段定义为空");
                return result;
            }
            
            // 构建字段映射
            Set<Long> validFieldIds = new HashSet<>();
            for (MetadataEntityFieldDO field : fields) {
                if (field.getId() != null) {
                    validFieldIds.add(field.getId());
                }
            }
            
            Set<String> validFieldNames = fields.stream()
                    .map(MetadataEntityFieldDO::getFieldName)
                    .filter(StringUtils::hasText)
                    .collect(Collectors.toSet());
            
            // 验证字段ID
            if (idKeyMap != null && !idKeyMap.isEmpty()) {
                for (Long fieldId : idKeyMap.keySet()) {
                    if (fieldId != null && !validFieldIds.contains(fieldId)) {
                        result.addInvalidFieldId(fieldId);
                    }
                }
            }
            
            // 验证字段名称
            if (nameKeyMap != null && !nameKeyMap.isEmpty()) {
                for (String fieldName : nameKeyMap.keySet()) {
                    if (StringUtils.hasText(fieldName) && !validFieldNames.contains(fieldName)) {
                        result.addInvalidFieldName(fieldName);
                    }
                }
            }
            
        } catch (Exception e) {
            result.addError("验证字段数据时发生异常: " + e.getMessage());
            log.error("验证字段数据失败。entityId={}, error={}", entityId, e.getMessage(), e);
        }
        
        return result;
    }
    
    /**
     * 字段数据验证结果
     */
    public static class ValidationResult {
        private final List<String> errors = new ArrayList<>();
        private final List<Long> invalidFieldIds = new ArrayList<>();
        private final List<String> invalidFieldNames = new ArrayList<>();
        
        public void addError(String error) {
            errors.add(error);
        }
        
        public void addInvalidFieldId(Long fieldId) {
            invalidFieldIds.add(fieldId);
        }
        
        public void addInvalidFieldName(String fieldName) {
            invalidFieldNames.add(fieldName);
        }
        
        public boolean isValid() {
            return errors.isEmpty() && invalidFieldIds.isEmpty() && invalidFieldNames.isEmpty();
        }
        
        public List<String> getErrors() {
            return Collections.unmodifiableList(errors);
        }
        
        public List<Long> getInvalidFieldIds() {
            return Collections.unmodifiableList(invalidFieldIds);
        }
        
        public List<String> getInvalidFieldNames() {
            return Collections.unmodifiableList(invalidFieldNames);
        }
        
        @Override
        public String toString() {
            if (isValid()) {
                return "ValidationResult{valid=true}";
            }
            return String.format("ValidationResult{valid=false, errors=%s, invalidFieldIds=%s, invalidFieldNames=%s}",
                    errors, invalidFieldIds, invalidFieldNames);
        }
    }
}
