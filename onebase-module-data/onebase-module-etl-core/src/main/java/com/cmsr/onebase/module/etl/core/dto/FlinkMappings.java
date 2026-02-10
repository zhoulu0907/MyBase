package com.cmsr.onebase.module.etl.core.dto;

import com.cmsr.onebase.module.etl.core.dal.dataobject.EtlFlinkMappingDO;
import com.cmsr.onebase.module.etl.core.enums.EtlConstants;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/12/22 16:59
 */
public class FlinkMappings {

    private MultiKeyMap<String, String> mappings = new MultiKeyMap<>();

    public FlinkMappings(List<EtlFlinkMappingDO> values) {
        for (EtlFlinkMappingDO mappingDO : values) {
            mappings.put(mappingDO.getDatasourceType().toLowerCase(), mappingDO.getOriginType().toLowerCase(), mappingDO.getFlinkType());
        }
    }

    public String getFlinkType(String datasourceType, String type) {
        String normalizedType = type.toLowerCase();
        String flinkType = mappings.get(datasourceType.toLowerCase(), normalizedType);
        if (StringUtils.isNotBlank(flinkType)) {
            return flinkType;
        }
        flinkType = mappings.get(EtlConstants.DEFAULT.toLowerCase(), normalizedType);
        if (StringUtils.isNotBlank(flinkType)) {
            return flinkType;
        }
        // 去除类型名称中的长度/精度信息后重试，如 varchar(255) -> varchar, numeric(10,2) -> numeric
        String baseType = normalizedType.replaceAll("\\(.*\\)", "").trim();
        if (!baseType.equals(normalizedType)) {
            flinkType = mappings.get(datasourceType.toLowerCase(), baseType);
            if (StringUtils.isNotBlank(flinkType)) {
                return flinkType;
            }
            flinkType = mappings.get(EtlConstants.DEFAULT.toLowerCase(), baseType);
            if (StringUtils.isNotBlank(flinkType)) {
                return flinkType;
            }
        }
        throw new RuntimeException("未找到对应的字段类型映射: " + datasourceType + "-" + type);
    }
}
