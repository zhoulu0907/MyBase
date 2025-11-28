package com.cmsr.onebase.module.metadata.runtime.semantic.strategy;

import org.anyline.data.param.ConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Component;

@Component
public class OperatorConditionApplier {
    public void apply(ConfigStore configs, String fieldName, String operator, Object value) {
        if (operator == null) { configs.and(Compare.LIKE, fieldName, value); return; }
        String op = operator.trim().toUpperCase();
        switch (op) {
            case "EQUALS": configs.and(Compare.EQUAL, fieldName, value); break;
            case "NOT_EQUALS": configs.and(Compare.NOT_EQUAL, fieldName, value); break;
            case "GREATER_THAN": configs.and(Compare.GREAT, fieldName, value); break;
            case "GREATER_EQUALS": configs.and(Compare.GREAT_EQUAL, fieldName, value); break;
            case "LESS_THAN": configs.and(Compare.LESS, fieldName, value); break;
            case "LESS_EQUALS": configs.and(Compare.LESS_EQUAL, fieldName, value); break;
            case "CONTAINS": configs.and(Compare.LIKE, fieldName, value); break;
            case "NOT_CONTAINS": configs.and(Compare.NOT_LIKE, fieldName, value); break;
            case "EARLIER_THAN": configs.and(Compare.LESS, fieldName, value); break;
            case "LATER_THAN": configs.and(Compare.GREAT, fieldName, value); break;
            case "EXISTS_IN": configs.and(Compare.IN, fieldName, value); break;
            case "NOT_EXISTS_IN": configs.and(Compare.NOT_IN, fieldName, value); break;
            case "RANGE":
                if (value instanceof java.util.Map) {
                    @SuppressWarnings("unchecked") java.util.Map<String,Object> range = (java.util.Map<String,Object>) value;
                    Object start = range.get("start");
                    Object end = range.get("end");
                    if (start != null) configs.and(Compare.GREAT_EQUAL, fieldName, start);
                    if (end != null) configs.and(Compare.LESS_EQUAL, fieldName, end);
                } else {
                    configs.and(Compare.GREAT_EQUAL, fieldName, value);
                }
                break;
            case "IS_EMPTY": configs.and(Compare.EQUAL, fieldName, ""); break;
            case "IS_NOT_EMPTY": configs.and(Compare.NOT_EQUAL, fieldName, ""); break;
            default: configs.and(Compare.LIKE, fieldName, value); break;
        }
    }
}

