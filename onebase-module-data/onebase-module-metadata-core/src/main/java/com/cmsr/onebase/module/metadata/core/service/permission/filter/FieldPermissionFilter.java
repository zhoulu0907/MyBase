package com.cmsr.onebase.module.metadata.core.service.permission.filter;

import com.cmsr.onebase.module.app.api.security.bo.FieldPermission;
import com.cmsr.onebase.module.app.api.security.bo.FieldPermissionItem;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.enums.BooleanStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 字段权限过滤器
 * 
 * 根据字段权限配置，过滤查询结果中用户无权读取的字段
 *
 * @author zhangxihui
 * @date 2025-10-31
 */
@Slf4j
@Component
public class FieldPermissionFilter {

    /**
     * 过滤查询结果中的字段
     * 
     * 移除用户无权读取的字段数据
     *
     * @param data 查询结果数据
     * @param fieldPermission 字段权限配置
     * @param fields 实体字段列表
     * @return 过滤后的数据
     */
    public Map<String, Object> filterFields(Map<String, Object> data,
                                             FieldPermission fieldPermission,
                                             List<MetadataEntityFieldDO> fields) {
        if (fieldPermission == null) {
            log.debug("字段权限对象为空，不过滤字段");
            return data;
        }

        // 全部允许：不过滤任何字段
        if (fieldPermission.isAllAllowed()) {
            log.debug("字段权限：全部允许，不过滤字段");
            return data;
        }

        // 全部拒绝：返回空数据
        if (fieldPermission.isAllDenied()) {
            log.info("字段权限：全部拒绝，返回空数据");
            return new HashMap<>();
        }

        // 获取可读字段ID集合
        Set<Long> readableFieldIds = getReadableFieldIds(fieldPermission);
        
        if (readableFieldIds.isEmpty()) {
            log.warn("没有可读字段权限，返回空数据");
            return new HashMap<>();
        }

        // 构建字段名到字段ID的映射
        Map<String, MetadataEntityFieldDO> fieldNameToIdMap = fields.stream()
                .collect(Collectors.toMap(
                        f -> f.getFieldName().toUpperCase(),
                        f -> f,
                        (v1, v2) -> v1 // 如果有重复，取第一个
                ));

        // 过滤数据
        Map<String, Object> filteredData = new HashMap<>();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String fieldName = entry.getKey();
            String fieldNameUpper = fieldName.toUpperCase();
            
            // 系统字段（如ID、created_at等）始终保留
            if (isSystemField(fieldNameToIdMap,fieldNameUpper)) {
                filteredData.put(fieldName, entry.getValue());
                continue;
            }

            // 检查字段是否有读取权限
            MetadataEntityFieldDO metadataEntityFieldDO = fieldNameToIdMap.get(fieldNameUpper);
            if (metadataEntityFieldDO != null && readableFieldIds.contains(metadataEntityFieldDO.getId())) {
                filteredData.put(fieldName, entry.getValue());
            } else {
                log.debug("过滤无权读取的字段：{}", fieldName);
            }
        }

        log.info("字段权限过滤完成：原字段数={}, 过滤后字段数={}", 
                data.size(), 
                filteredData.size());

        return filteredData;
    }

    /**
     * 批量过滤查询结果列表中的字段
     *
     * @param dataList 查询结果列表
     * @param fieldPermission 字段权限配置
     * @param fields 实体字段列表
     * @return 过滤后的数据列表
     */
    public List<Map<String, Object>> filterFieldsInList(List<Map<String, Object>> dataList,
                                                          FieldPermission fieldPermission,
                                                          List<MetadataEntityFieldDO> fields) {
        if (dataList == null || dataList.isEmpty()) {
            return dataList;
        }

        return dataList.stream()
                .map(data -> filterFields(data, fieldPermission, fields))
                .collect(Collectors.toList());
    }

    /**
     * 获取可读字段ID集合
     *
     * @param fieldPermission 字段权限配置
     * @return 可读字段ID集合
     */
    private Set<Long> getReadableFieldIds(FieldPermission fieldPermission) {
        List<FieldPermissionItem> fieldItems = fieldPermission.getFields();
        
        if (fieldItems == null || fieldItems.isEmpty()) {
            return Collections.emptySet();
        }

        return fieldItems.stream()
                .filter(FieldPermissionItem::isCanRead)
                .map(FieldPermissionItem::getFieldId)
                .collect(Collectors.toSet());
    }

    /**
     * 判断是否为系统字段
     * 
     * 系统字段始终可读，不受字段权限控制
     *
     * @param fieldName 字段名（大写）
     * @return true表示是系统字段
     */
    private boolean isSystemField(Map<String, MetadataEntityFieldDO> fieldNameToIdMap, String fieldName) {
        if (null == fieldNameToIdMap.get(fieldName)){
            return false;
        }
        
        return fieldNameToIdMap.get(fieldName).getIsSystemField() == BooleanStatusEnum.YES.getStatus();
    }

    /**
     * 获取可查询的字段名列表
     * 
     * 用于在查询时只查询用户有权限读取的字段，优化查询性能
     *
     * @param fieldPermission 字段权限配置
     * @param fields 实体字段列表
     * @return 可查询的字段名列表
     */
    public List<String> getQueryableFieldNames(FieldPermission fieldPermission,
                                                List<MetadataEntityFieldDO> fields) {
        if (fieldPermission == null || fieldPermission.isAllAllowed()) {
            // 全部允许：返回所有字段
            return fields.stream()
                    .map(MetadataEntityFieldDO::getFieldName)
                    .collect(Collectors.toList());
        }

        if (fieldPermission.isAllDenied()) {
            // 全部拒绝：只返回系统字段
            return fields.stream()
                    .filter(f -> f.getIsSystemField() == BooleanStatusEnum.YES.getStatus())
                    .map(MetadataEntityFieldDO::getFieldName)
                    .collect(Collectors.toList());
        }

        // 获取可读字段ID集合
        Set<Long> readableFieldIds = getReadableFieldIds(fieldPermission);

        // 返回可读字段名列表 + 系统字段
        return fields.stream()
                .filter(f -> readableFieldIds.contains(f.getId()) 
                        || f.getIsSystemField() == BooleanStatusEnum.YES.getStatus())
                .map(MetadataEntityFieldDO::getFieldName)
                .collect(Collectors.toList());
    }
}

