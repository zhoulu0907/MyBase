package com.cmsr.onebase.module.metadata.build.service.component;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.FieldTypeConfigRespVO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataComponentFieldTypeDO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataComponentFieldTypeRepository;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataTypeCodeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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
    private MetadataComponentFieldTypeRepository fieldTypeRepository;

    @Override
    public List<MetadataComponentFieldTypeDO> getAllFieldTypes() {
        try {
            List<MetadataComponentFieldTypeDO> results = fieldTypeRepository.findAllEnabled();

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
        return fieldTypeRepository.findByFieldTypeCode(fieldTypeCode);
    }

    @Override
    public String mapFieldTypeToDatabaseType(String fieldTypeCode, Integer dataLength) {
        MetadataComponentFieldTypeDO fieldType = getByFieldTypeCode(fieldTypeCode);
        if (fieldType == null) {
            log.warn("未找到字段类型配置: {}, 使用默认类型", fieldTypeCode);
            return buildVarcharType(dataLength);
        }

        String dataType = fieldType.getDataType();
        if (dataType == null || dataType.trim().isEmpty()) {
            log.warn("字段类型 {} 的数据类型为空，使用默认类型", fieldTypeCode);
            return buildVarcharType(dataLength);
        }

        MetadataDataTypeCodeEnum dataTypeCode = MetadataDataTypeCodeEnum.fromCode(dataType);
        if (dataTypeCode == null) {
            log.warn("未识别的数据类型: {}，使用默认类型", dataType);
            return buildVarcharType(dataLength);
        }

        // 根据数据库类型进行映射
        switch (dataTypeCode) {
            case BIGINT:
                return MetadataDataTypeCodeEnum.BIGINT.getCode();
            case VARCHAR:
                return buildVarcharType(dataLength);
            case TEXT:
            case LONGVARCHAR:
                // TEXT/LONGVARCHAR 类型固定映射为 TEXT，不受 dataLength 影响
                // 包括：TEXT、LONG_TEXT、单选列表、多选列表、结构化对象、数组列表、
                // 文件、图片、地理位置、用户多选、部门多选、数据多选等
                // 这些类型不应受 dataLength 限制，因为它们可能存储较长或可变长度的数据
                return MetadataDataTypeCodeEnum.TEXT.getCode();
            case TIMESTAMP:
                return MetadataDataTypeCodeEnum.TIMESTAMP.getCode();
            case BOOLEAN:
                return MetadataDataTypeCodeEnum.BOOLEAN.getCode();
            case INTEGER:
                return MetadataDataTypeCodeEnum.INTEGER.getCode();
            case DECIMAL:
                return "DECIMAL(18,2)";
            case DATE:
                return MetadataDataTypeCodeEnum.DATE.getCode();
            case TIME:
                return MetadataDataTypeCodeEnum.TIME.getCode();
            case JSON:
            case JSONB:
                return MetadataDataTypeCodeEnum.JSONB.getCode();
            default:
                log.warn("未识别的数据类型: {}，使用默认类型", dataType);
                return buildVarcharType(dataLength);
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
            MetadataDataTypeCodeEnum fieldType = MetadataDataTypeCodeEnum.fromCode(fieldTypeCode);
            if (fieldType == null) {
                respVO.setSupportLength(false);
                respVO.setDefaultLength(null);
                respVO.setMaxLength(null);
                respVO.setSupportDecimal(false);
                return;
            }

            switch (fieldType) {
                case VARCHAR:
                    respVO.setSupportLength(true);
                    respVO.setDefaultLength(255);
                    respVO.setMaxLength(4000);
                    break;
                case INTEGER:
                    respVO.setSupportLength(true);
                    respVO.setDefaultLength(11);
                    respVO.setMaxLength(null);
                    break;
                case BIGINT:
                    respVO.setSupportLength(true);
                    respVO.setDefaultLength(20);
                    respVO.setMaxLength(null);
                    break;
                case DECIMAL:
                    respVO.setSupportLength(true);
                    respVO.setDefaultLength(10);
                    respVO.setMaxLength(null);
                    break;
                default:
                    respVO.setSupportLength(false);
                    respVO.setDefaultLength(null);
                    respVO.setMaxLength(null);
                    break;
            }

            // 小数类型支持小数位设置
            if (fieldType == MetadataDataTypeCodeEnum.DECIMAL) {
                respVO.setSupportDecimal(true);
                respVO.setDefaultDecimal(2);
            } else {
                respVO.setSupportDecimal(false);
            }
        });
    }

    /**
     * 获取默认的字段类型配置（当数据库表不存在或没有数据时使用）
     */
    private List<MetadataComponentFieldTypeDO> getDefaultFieldTypes() {
        List<MetadataComponentFieldTypeDO> defaultTypes = new ArrayList<>();

        // 文本类型
        defaultTypes.add(createDefaultFieldType(MetadataDataTypeCodeEnum.VARCHAR.getCode(), "文本", "字符串类型", MetadataDataTypeCodeEnum.VARCHAR.getCode(), 1));
        defaultTypes.add(createDefaultFieldType(MetadataDataTypeCodeEnum.TEXT.getCode(), "长文本", "文本类型", MetadataDataTypeCodeEnum.TEXT.getCode(), 2));

        // 数字类型
        defaultTypes.add(createDefaultFieldType(MetadataDataTypeCodeEnum.INTEGER.getCode(), "整数", "整数类型", MetadataDataTypeCodeEnum.INTEGER.getCode(), 3));
        defaultTypes.add(createDefaultFieldType(MetadataDataTypeCodeEnum.BIGINT.getCode(), "长整数", "长整数类型", MetadataDataTypeCodeEnum.BIGINT.getCode(), 4));
        defaultTypes.add(createDefaultFieldType(MetadataDataTypeCodeEnum.DECIMAL.getCode(), "小数", "小数类型", MetadataDataTypeCodeEnum.DECIMAL.getCode(), 5));

        // 时间类型
        defaultTypes.add(createDefaultFieldType(MetadataDataTypeCodeEnum.DATE.getCode(), "日期", "日期类型", MetadataDataTypeCodeEnum.DATE.getCode(), 6));
        defaultTypes.add(createDefaultFieldType(MetadataDataTypeCodeEnum.DATETIME.getCode(), "日期时间", "日期时间类型", MetadataDataTypeCodeEnum.TIMESTAMP.getCode(), 7));
        defaultTypes.add(createDefaultFieldType(MetadataDataTypeCodeEnum.TIME.getCode(), "时间", "时间类型", MetadataDataTypeCodeEnum.TIME.getCode(), 8));

        // 布尔类型
        defaultTypes.add(createDefaultFieldType(MetadataDataTypeCodeEnum.BOOLEAN.getCode(), "布尔", "布尔类型", MetadataDataTypeCodeEnum.BOOLEAN.getCode(), 9));

        // 选择类型
        defaultTypes.add(createDefaultFieldType(MetadataDataTypeCodeEnum.SINGLE_SELECT.getCode(), "单选", "单选类型", MetadataDataTypeCodeEnum.VARCHAR.getCode(), 10));
        defaultTypes.add(createDefaultFieldType(MetadataDataTypeCodeEnum.MULTI_SELECT.getCode(), "多选", "多选类型", MetadataDataTypeCodeEnum.VARCHAR.getCode(), 11));

        // 其他类型
        defaultTypes.add(createDefaultFieldType(MetadataDataTypeCodeEnum.JSON.getCode(), "JSON", "JSON类型", MetadataDataTypeCodeEnum.JSON.getCode(), 12));
        defaultTypes.add(createDefaultFieldType(MetadataDataTypeCodeEnum.AUTO_NUMBER.getCode(), "自动编号", "自动编号类型", MetadataDataTypeCodeEnum.VARCHAR.getCode(), 13));

        return defaultTypes;
    }

    private String buildVarcharType(Integer dataLength) {
        int len = (dataLength != null && dataLength > 0) ? dataLength : 255;
        return MetadataDataTypeCodeEnum.VARCHAR.getCode() + "(" + len + ")";
    }

    /**
     * 创建默认字段类型对象
     */
    private MetadataComponentFieldTypeDO createDefaultFieldType(String code, String name, String desc,
            String dataType, int sortOrder) {
        MetadataComponentFieldTypeDO fieldType = new MetadataComponentFieldTypeDO();
        fieldType.setFieldTypeCode(code);
        fieldType.setFieldTypeName(name);
        fieldType.setFieldTypeDesc(desc);
        fieldType.setDataType(dataType);
        fieldType.setSortOrder(sortOrder);
        fieldType.setStatus(CommonStatusEnum.ENABLE.getStatus());
        return fieldType;
    }
}
