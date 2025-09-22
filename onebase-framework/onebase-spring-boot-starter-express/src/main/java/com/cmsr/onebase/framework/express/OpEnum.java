package com.cmsr.onebase.framework.express;

/**
 * 操作符枚举
 * @Author：huangjie
 * @Date：2025/9/16 21:29
 */
public enum OpEnum {
    
    /** 等于 */
    EQUALS("等于"),
    
    /** 不等于 */
    NOT_EQUALS("不等于"),
    
    /** 包含 */
    CONTAINS("包含"),
    
    /** 不包含 */
    NOT_CONTAINS("不包含"),
    
    /** 存在于 */
    EXISTS_IN("存在于"),
    
    /** 不存在于 */
    NOT_EXISTS_IN("不存在于"),
    
    /** 大于 */
    GREATER_THAN("大于"),
    
    /** 大于等于 */
    GREATER_EQUALS("大于等于"),
    
    /** 小于 */
    LESS_THAN("小于"),
    
    /** 小于等于 */
    LESS_EQUALS("小于等于"),
    
    /** 晚于 */
    LATER_THAN("晚于"),
    
    /** 早于 */
    EARLIER_THAN("早于"),
    
    /** 范围 */
    RANGE("范围"),
    
    /** 为空 */
    IS_EMPTY("为空"),
    
    /** 不为空 */
    IS_NOT_EMPTY("不为空"),
    
    /** 包含全部 */
    CONTAINS_ALL("包含全部"),
    
    /** 不包含全部 */
    NOT_CONTAINS_ALL("不包含全部"),
    
    /** 包含任一 */
    CONTAINS_ANY("包含任一"),
    
    /** 不包含任一 */
    NOT_CONTAINS_ANY("不包含任一");
    
    private final String description;
    
    OpEnum(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
