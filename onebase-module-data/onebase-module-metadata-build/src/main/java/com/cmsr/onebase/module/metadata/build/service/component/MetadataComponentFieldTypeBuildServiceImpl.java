package com.cmsr.onebase.module.metadata.build.service.component;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.FieldTypeConfigRespVO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataComponentFieldTypeDO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.DataSet;
import org.anyline.entity.Order;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
public class MetadataComponentFieldTypeBuildServiceImpl implements MetadataComponentFieldTypeBuildService {

    @Resource
    private AnylineService<?> anylineService;

    @Override
    public List<MetadataComponentFieldTypeDO> getAllFieldTypes() {
        try {
            DefaultConfigStore configStore = new DefaultConfigStore();
            configStore.and(MetadataComponentFieldTypeDO.STATUS, CommonStatusEnum.ENABLE.getStatus()); // 只获取启用的字段类型
            configStore.and("deleted", 0); // 未删除的记录
            configStore.order(MetadataComponentFieldTypeDO.SORT_ORDER, Order.TYPE.ASC);
            configStore.order("create_time", Order.TYPE.ASC);

            DataSet dataSet = anylineService.querys("metadata_component_field_type", configStore);
            List<MetadataComponentFieldTypeDO> results = dataSet.entity(MetadataComponentFieldTypeDO.class);

            // 如果数据库中没有数据，返回默认的字段类型配置
            if (results.isEmpty()) {
                log.warn("数据库表 metadata_component_field_type 中没有数据，返回默认字段类型配置");
                return getDefaultFieldTypes();
            }

            return results;
        } catch (Exception e) {
            log.error("查询字段类型配置失败，返回默认配置: {}", e.getMessage(), e);
            return getDefaultFieldTypes();
        }
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
                // VARCHAR类型才使用dataLength
                return "VARCHAR(" + (dataLength != null && dataLength > 0 ? dataLength : 255) + ")";
            case "TEXT":
            case "LONGVARCHAR":
                // TEXT/LONGVARCHAR 类型固定映射为 TEXT，不受 dataLength 影响
                // 包括：TEXT、LONG_TEXT、单选列表、多选列表、结构化对象、数组列表、
                // 文件、图片、地理位置、用户多选、部门多选、数据多选等
                // 这些类型不应受 dataLength 限制，因为它们可能存储较长或可变长度的数据
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
                        respVO.setDefaultLength(fieldTypeCode.equals("VARCHAR") ? 255
                                : fieldTypeCode.equals("INTEGER") ? 11 : fieldTypeCode.equals("BIGINT") ? 20 : 10);
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

    /**
     * 获取默认的字段类型配置（当数据库表不存在或没有数据时使用）
     */
    private List<MetadataComponentFieldTypeDO> getDefaultFieldTypes() {
        List<MetadataComponentFieldTypeDO> defaultTypes = new ArrayList<>();

        // 文本类型
        defaultTypes.add(createDefaultFieldType("VARCHAR", "文本", "字符串类型", "VARCHAR", 1));
        defaultTypes.add(createDefaultFieldType("TEXT", "长文本", "文本类型", "TEXT", 2));

        // 数字类型
        defaultTypes.add(createDefaultFieldType("INTEGER", "整数", "整数类型", "INTEGER", 3));
        defaultTypes.add(createDefaultFieldType("BIGINT", "长整数", "长整数类型", "BIGINT", 4));
        defaultTypes.add(createDefaultFieldType("DECIMAL", "小数", "小数类型", "DECIMAL", 5));

        // 时间类型
        defaultTypes.add(createDefaultFieldType("DATE", "日期", "日期类型", "DATE", 6));
        defaultTypes.add(createDefaultFieldType("DATETIME", "日期时间", "日期时间类型", "TIMESTAMP", 7));
        defaultTypes.add(createDefaultFieldType("TIME", "时间", "时间类型", "TIME", 8));

        // 布尔类型
        defaultTypes.add(createDefaultFieldType("BOOLEAN", "布尔", "布尔类型", "BOOLEAN", 9));

        // 选择类型
        defaultTypes.add(createDefaultFieldType("SINGLE_SELECT", "单选", "单选类型", "VARCHAR", 10));
        defaultTypes.add(createDefaultFieldType("MULTI_SELECT", "多选", "多选类型", "VARCHAR", 11));

        // 其他类型
        defaultTypes.add(createDefaultFieldType("JSON", "JSON", "JSON类型", "JSON", 12));
        defaultTypes.add(createDefaultFieldType("AUTO_NUMBER", "自动编号", "自动编号类型", "VARCHAR", 13));

        return defaultTypes;
    }

    /**
     * 创建默认字段类型对象
     */
    private MetadataComponentFieldTypeDO createDefaultFieldType(String code, String name, String desc,
            String dataType, int sortOrder) {
        return MetadataComponentFieldTypeDO.builder()
                .fieldTypeCode(code)
                .fieldTypeName(name)
                .fieldTypeDesc(desc)
                .dataType(dataType)
                .sortOrder(sortOrder)
                .status(CommonStatusEnum.ENABLE.getStatus())
                .build();
    }
}
