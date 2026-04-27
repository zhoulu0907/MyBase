package com.cmsr.onebase.module.etl.executor.provider.dao;

import lombok.Data;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author：huangjie
 * @Date：2025/11/9 9:00
 */
@Data
public class EtlFlinkMappings {

    public static final String DEFAULT = "default";

//    private String datasourceType;
//
//    private String originType;
//
//    private String flinkType;

    private MultiKeyMap<String, String> mappings = new MultiKeyMap<>();


    public void add(String datasourceType, String originType, String flinkType) {
        mappings.put(datasourceType.toLowerCase(), originType.toLowerCase(), flinkType);
    }

    public String getFlinkType(String datasourceType, String type) {
        String normalizedType = type.toLowerCase();
        String flinkType = mappings.get(datasourceType.toLowerCase(), normalizedType);
        if (StringUtils.isNotBlank(flinkType)) {
            return flinkType;
        }
        flinkType = mappings.get(DEFAULT.toLowerCase(), normalizedType);
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
            flinkType = mappings.get(DEFAULT.toLowerCase(), baseType);
            if (StringUtils.isNotBlank(flinkType)) {
                return flinkType;
            }
        }
        throw new RuntimeException("未找到对应的字段类型映射: " + datasourceType + "-" + type);
    }
}
