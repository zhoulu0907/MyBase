package com.cmsr.onebase.module.flow.api.dto;

/**
 * @Author：huangjie
 * @Date：2025/9/19 11:09
 */
public enum TriggerEventEnum {
    
    /**
     * 创建前触发
     */
    BEFORE_CREATE("beforeCreate", "创建前触发"),
    
    /**
     * 创建后触发
     */
    AFTER_CREATE("afterCreate", "创建后触发"),
    
    /**
     * 更新前触发
     */
    BEFORE_UPDATE("beforeUpdate", "更新前触发"),
    
    /**
     * 更新后触发
     */
    AFTER_UPDATE("afterUpdate", "更新后触发"),
    
    /**
     * 删除前触发
     */
    BEFORE_DELETE("beforeDelete", "删除前触发"),
    
    /**
     * 删除后触发
     */
    AFTER_DELETE("afterDelete", "删除后触发");
    
    private final String code;
    private final String description;
    
    TriggerEventEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
}
