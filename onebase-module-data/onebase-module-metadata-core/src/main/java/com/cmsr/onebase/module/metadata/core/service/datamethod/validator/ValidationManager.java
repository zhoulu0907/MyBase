package com.cmsr.onebase.module.metadata.core.service.datamethod.validator;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.domain.query.MetadataDataMethodSubEntityContext;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.service.number.AutoNumberService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

import java.util.List;
import java.util.Map;

/**
 * 校验管理器
 * 
 * 负责协调各种校验服务的执行，统一管理校验流程
 *
 * @author bty418
 * @date 2025-10-17
 */
@Component
public class ValidationManager {

    private static final Logger log = Logger.getLogger(ValidationManager.class.getName());

    private final List<ValidationService> validationServices;
    
    @Resource
    private AutoNumberService autoNumberService;

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
    public void validateField(Long entityId, MetadataEntityFieldDO field, Object value, Map<String, Object> data, List<MetadataDataMethodSubEntityContext> subEntities) {
        Long fieldId = field.getId();
        String fieldType = field.getFieldType();
        String fieldName = field.getFieldName();

        log.fine("开始校验字段：entityId=" + entityId + ", fieldId=" + fieldId + ", fieldName=" + fieldName + ", fieldType=" + fieldType);

        // 检查是否为自动编号字段，如果是则跳过所有校验
        if (autoNumberService.hasAutoNumber(fieldId)) {
            log.fine("字段" + fieldName + "是自动编号字段，跳过所有校验");
            return;
        }

        // 遍历所有校验服务，执行支持的校验
        for (ValidationService service : validationServices) {
            try {
                if("CHILD_NOT_EMPTY".equals(service.getValidationType())){
                    continue;
                }
                // 检查是否支持该字段类型
                if (service.supports(fieldType)) {
                    log.fine("执行" + service.getValidationType() + "校验：fieldName=" + fieldName);
                    service.validate(entityId, fieldId, field, value, data, subEntities);
                }
            } catch (Exception e) {
                // 构造详细的错误消息：字段名-校验类型-失败原因
                String validationType = getValidationTypeName(service.getValidationType());
                String errorMessage = String.format("字段[%s]-%s校验失败：%s",
                            field.getDisplayName(), validationType, e.getMessage());
                log.severe(errorMessage);
                throw new RuntimeException(errorMessage, e);
            }
        }

        log.fine("字段" + fieldName + "校验完成");
    }

    /**
     * 获取校验类型的中文名称
     *
     * @param validationType 校验类型代码
     * @return 校验类型中文名称
     */
    private String getValidationTypeName(String validationType) {
        if (validationType == null) {
            return "未知";
        }
        
        return switch (validationType.toUpperCase()) {
            case "REQUIRED" -> "必填";
            case "LENGTH" -> "长度";
            case "FORMAT" -> "格式";
            case "UNIQUE" -> "唯一性";
            case "RANGE" -> "范围";
            case "REGEX" -> "正则表达式";
            case "CUSTOM" -> "自定义";
            default -> validationType;
        };
    }

    /**
     * 执行实体所有字段的校验
     *
     * @param entityId      实体ID
     * @param fields        字段列表
     * @param data          数据对象
     * @param operationType
     */
    public void validateEntity(Long entityId, List<MetadataEntityFieldDO> fields, Map<String, Object> data, List<MetadataDataMethodSubEntityContext> subEntities, MetadataDataMethodOpEnum operationType) {
        log.info("开始校验实体：entityId=" + entityId + ", 字段数量=" + fields.size());

        for (ValidationService service : validationServices){
            try{
                if("CHILD_NOT_EMPTY".equals(service.getValidationType())){
                    log.fine("执行" + service.getValidationType() + "校验：entityId=" + entityId);
                    service.validate(entityId, null, null, null, data, subEntities);
                }
            }catch (Exception e){
                String errorMessage = String.format("子表非空%s校验失败：%s", service.getValidationType(), e.getMessage());;
                log.severe(errorMessage);
                throw new RuntimeException(errorMessage, e);
            }
        }

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

            //跳过当前不更新的字段(更新时生效)
            if (operationType == MetadataDataMethodOpEnum.UPDATE) {
                if (value == null) {
                    continue;
                }
            }

            validateField(entityId, field, value, data, subEntities);
        }

        log.info("实体" + entityId + "校验完成");
    }
}
