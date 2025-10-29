package com.cmsr.onebase.module.formula.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ContextData {
    /**
     * 数据记录，List类型。
     * Map的key为filedId字段名，value为字段值filedValue
     */
    private List<Map<String, Object>> recordList;
}
