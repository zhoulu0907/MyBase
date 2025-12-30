package com.cmsr.onebase.plugin.model;

/**
 * 参数定义
 * <p>
 * 用于定义自定义函数的参数信息。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-18
 */
public class ParamDef {

    /**
     * 参数名称
     */
    private String name;

    /**
     * 参数描述
     */
    private String description;

    /**
     * 参数类型
     */
    private ParamType type;

    /**
     * 是否必填
     */
    private boolean required;

    /**
     * 默认值
     */
    private Object defaultValue;

    public ParamDef() {
    }

    public ParamDef(String name, ParamType type) {
        this.name = name;
        this.type = type;
        this.required = true;
    }

    public ParamDef(String name, String description, ParamType type) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.required = true;
    }

    public ParamDef(String name, String description, ParamType type, boolean required) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.required = required;
    }

    public ParamDef(String name, String description, ParamType type, boolean required, Object defaultValue) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.required = required;
        this.defaultValue = defaultValue;
    }

    // ==================== 静态工厂方法 ====================

    /**
     * 创建必填参数
     *
     * @param name 参数名称
     * @param type 参数类型
     * @return 参数定义
     */
    public static ParamDef required(String name, ParamType type) {
        return new ParamDef(name, type);
    }

    /**
     * 创建必填参数（带描述）
     *
     * @param name        参数名称
     * @param description 参数描述
     * @param type        参数类型
     * @return 参数定义
     */
    public static ParamDef required(String name, String description, ParamType type) {
        return new ParamDef(name, description, type, true);
    }

    /**
     * 创建可选参数
     *
     * @param name 参数名称
     * @param type 参数类型
     * @return 参数定义
     */
    public static ParamDef optional(String name, ParamType type) {
        return new ParamDef(name, null, type, false);
    }

    /**
     * 创建可选参数（带默认值）
     *
     * @param name         参数名称
     * @param type         参数类型
     * @param defaultValue 默认值
     * @return 参数定义
     */
    public static ParamDef optional(String name, ParamType type, Object defaultValue) {
        return new ParamDef(name, null, type, false, defaultValue);
    }

    /**
     * 创建可选参数（带描述和默认值）
     *
     * @param name         参数名称
     * @param description  参数描述
     * @param type         参数类型
     * @param defaultValue 默认值
     * @return 参数定义
     */
    public static ParamDef optional(String name, String description, ParamType type, Object defaultValue) {
        return new ParamDef(name, description, type, false, defaultValue);
    }

    // ==================== Getter/Setter ====================

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ParamType getType() {
        return type;
    }

    public void setType(ParamType type) {
        this.type = type;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * 参数类型枚举
     */
    public enum ParamType {
        /**
         * 字符串
         */
        STRING,
        /**
         * 数字
         */
        NUMBER,
        /**
         * 布尔
         */
        BOOLEAN,
        /**
         * 日期
         */
        DATE,
        /**
         * 日期时间
         */
        DATETIME,
        /**
         * 数组
         */
        ARRAY,
        /**
         * 对象
         */
        OBJECT,
        /**
         * 任意类型
         */
        ANY
    }
}
