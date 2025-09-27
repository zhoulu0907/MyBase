package com.cmsr.onebase.module.metadata.core.service.datamethod.validator;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;

import java.util.logging.Logger;

import java.util.List;
import java.util.Map;

/**
 * 校验管理器
 * 
 * 负责协调各种校验服务的执行，统一管理校验流程
 *
 */
public class ValidationManager {

    private static final Logger log = Logger.getLogger(ValidationManager.class.getName());

    private final List<ValidationService> validationServices;

    public ValidationManager(List<ValidationService> validationServices) {
        this.validationServices = validationServices;
    }

    /**
     * 执行字段校验
     *
     * @param entityId 实体ID
     * @param field 字段信息
     * @param value 字段值
     * @param data 完整数据对象
     */
    public void validateField(Long entityId, MetadataEntityFieldDO field, Object value, Map<String, Object> data) {
        Long fieldId = field.getId();
        String fieldType = field.getFieldType();
        String fieldName = field.getFieldName();

        log.fine("开始校验字段：entityId=" + entityId + ", fieldId=" + fieldId + ", fieldName=" + fieldName + ", fieldType=" + fieldType);

        // 遍历所有校验服务，执行支持的校验
        for (ValidationService service : validationServices) {
            try {
                // 检查是否支持该字段类型
                if (service.supports(fieldType)) {//todo 这里需要确认，字段类型的枚举是什么？？
                    log.fine("执行" + service.getValidationType() + "校验：fieldName=" + fieldName);
                    service.validate(entityId, fieldId, field, value, data);
                }
            } catch (Exception e) {
                log.severe("字段" + fieldName + "校验失败：" + e.getMessage());
                throw new RuntimeException("字段[" + field.getDisplayName() + "]校验失败：" + e.getMessage(), e);
            }
        }

        log.fine("字段" + fieldName + "校验完成");
    }

    /**
     * 执行实体所有字段的校验
     *
     * @param entityId 实体ID
     * @param fields 字段列表
     * @param data 数据对象
     */
    public void validateEntity(Long entityId, List<MetadataEntityFieldDO> fields, Map<String, Object> data) {
        log.info("开始校验实体：entityId=" + entityId + ", 字段数量=" + fields.size());

        for (MetadataEntityFieldDO field : fields) {
            String fieldName = field.getFieldName();
            Object value = data.get(fieldName);
            
            // 跳过系统字段和主键字段
            if (field.getIsSystemField() != null && field.getIsSystemField() == 1) {
                log.fine("跳过系统字段：" + fieldName);
                continue;
            }
            
            if (field.getIsPrimaryKey() != null && field.getIsPrimaryKey() == 1) {
                log.fine("跳过主键字段：" + fieldName);
                continue;
            }

            validateField(entityId, field, value, data);
        }

        log.info("实体" + entityId + "校验完成");
    }
}
