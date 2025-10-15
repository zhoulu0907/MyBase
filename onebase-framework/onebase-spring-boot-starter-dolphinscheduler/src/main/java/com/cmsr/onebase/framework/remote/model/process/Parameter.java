package com.cmsr.onebase.framework.remote.model.process;

/**
 * 任务/全局参数 DTO（仅数据传输，无业务逻辑）
 */
public class Parameter {

    /** 参数名 */
    private String prop;
    /** 参数值 */
    private String value;
    /** 方向：IN/OUT */
    private String direct;
    /** 类型：VARCHAR/INTEGER/LONG 等 */
    private String type;

    public String getProp() { return prop; }
    public void setProp(String prop) { this.prop = prop; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public String getDirect() { return direct; }
    public void setDirect(String direct) { this.direct = direct; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}

