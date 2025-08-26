package com.cmsr.onebase.module.metadata.service.component;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.FieldTypeConfigRespVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataComponentFieldTypeDO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.DataSet;
import org.anyline.entity.Order;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 元数据组件字段类型 Service 实现类
 *
 * @author matianyu
 * @date 2025-08-21
 */
@Service
@Slf4j
public class MetadataComponentFieldTypeServiceImpl implements MetadataComponentFieldTypeService {

    @Resource
    private AnylineService<?> anylineService;

    @Override
    public List<MetadataComponentFieldTypeDO> getAllFieldTypes() {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataComponentFieldTypeDO.STATUS, CommonStatusEnum.ENABLE.getStatus()); // 只获取启用的字段类型
        configStore.and("deleted", 0); // 未删除的记录
        configStore.order(MetadataComponentFieldTypeDO.SORT_ORDER, Order.TYPE.ASC);
        configStore.order("create_time", Order.TYPE.ASC);

        DataSet dataSet = anylineService.querys("metadata_component_field_type", configStore);
        return dataSet.entity(MetadataComponentFieldTypeDO.class);
    }

    @Override
    public List<FieldTypeConfigRespVO> getFieldTypeConfigs() {
        List<MetadataComponentFieldTypeDO> fieldTypes = getAllFieldTypes();
        return fieldTypes.stream()
                .map(this::convertToFieldTypeConfigRespVO)
                .collect(Collectors.toList());
    }

    @Override
    public MetadataComponentFieldTypeDO getByFieldTypeCode(String fieldTypeCode) {
        if (fieldTypeCode == null || fieldTypeCode.trim().isEmpty()) {
            return null;
        }

        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataComponentFieldTypeDO.FIELD_TYPE_CODE, fieldTypeCode);
        configStore.and(MetadataComponentFieldTypeDO.STATUS, CommonStatusEnum.ENABLE.getStatus());
        configStore.and("deleted", 0);

        DataSet dataSet = anylineService.querys("metadata_component_field_type", configStore);
        List<MetadataComponentFieldTypeDO> results = dataSet.entity(MetadataComponentFieldTypeDO.class);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public String mapFieldTypeToDatabaseType(String fieldTypeCode, Integer dataLength) {
        MetadataComponentFieldTypeDO fieldType = getByFieldTypeCode(fieldTypeCode);
        if (fieldType == null) {
            log.warn("未找到字段类型配置: {}, 使用默认类型", fieldTypeCode);
            return "VARCHAR(" + (dataLength != null && dataLength > 0 ? dataLength : 255) + ")";
        }

        String dataType = fieldType.getDataType();
        if (dataType == null || dataType.trim().isEmpty()) {
            log.warn("字段类型 {} 的数据类型为空，使用默认类型", fieldTypeCode);
            return "VARCHAR(" + (dataLength != null && dataLength > 0 ? dataLength : 255) + ")";
        }

        // 根据数据库类型进行映射
        switch (dataType.toUpperCase()) {
            case "BIGINT":
                return "BIGINT";
            case "VARCHAR":
                return "VARCHAR(" + (dataLength != null && dataLength > 0 ? dataLength : 255) + ")";
            case "TEXT":
                return "TEXT";
            case "TIMESTAMP":
                return "TIMESTAMP";
            case "BOOLEAN":
                return "BOOLEAN";
            case "INTEGER":
                return "INTEGER";
            case "DECIMAL":
                return "DECIMAL(18,2)";
            case "DATE":
                return "DATE";
            case "TIME":
                return "TIME";
            case "JSON":
            case "JSONB":
                return "JSONB";
            default:
                log.warn("未识别的数据类型: {}，使用默认类型", dataType);
                return "VARCHAR(" + (dataLength != null && dataLength > 0 ? dataLength : 255) + ")";
        }
    }

    /**
     * 转换字段类型DO为字段类型配置响应VO
     *
     * @param fieldTypeDO 字段类型DO
     * @return 字段类型配置响应VO
     */
    private FieldTypeConfigRespVO convertToFieldTypeConfigRespVO(MetadataComponentFieldTypeDO fieldTypeDO) {
        return BeanUtils.toBean(fieldTypeDO, FieldTypeConfigRespVO.class, respVO -> {
            respVO.setFieldType(fieldTypeDO.getFieldTypeCode());
            respVO.setDisplayName(fieldTypeDO.getFieldTypeName());
            respVO.setCategory(fieldTypeDO.getDataType());

            // 根据字段类型设置支持的配置选项
            String fieldTypeCode = fieldTypeDO.getFieldTypeCode();
            if (fieldTypeCode != null) {
                switch (fieldTypeCode.toUpperCase()) {
                    case "VARCHAR":
                    case "INTEGER":
                    case "BIGINT":
                    case "DECIMAL":
                        respVO.setSupportLength(true);
                        respVO.setDefaultLength(fieldTypeCode.equals("VARCHAR") ? 255 : 
                                               fieldTypeCode.equals("INTEGER") ? 11 : 
                                               fieldTypeCode.equals("BIGINT") ? 20 : 10);
                        respVO.setMaxLength(fieldTypeCode.equals("VARCHAR") ? 4000 : null);
                        break;
                    default:
                        respVO.setSupportLength(false);
                        respVO.setDefaultLength(null);
                        respVO.setMaxLength(null);
                        break;
                }

                // 小数类型支持小数位设置
                if ("DECIMAL".equalsIgnoreCase(fieldTypeCode)) {
                    respVO.setSupportDecimal(true);
                    respVO.setDefaultDecimal(2);
                } else {
                    respVO.setSupportDecimal(false);
                }
            }
        });
    }
}
