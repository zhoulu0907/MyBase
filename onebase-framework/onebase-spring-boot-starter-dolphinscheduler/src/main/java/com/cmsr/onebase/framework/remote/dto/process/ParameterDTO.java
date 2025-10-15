package com.cmsr.onebase.framework.remote.dto.process;

import lombok.Data;

/**
 * 任务/全局参数 DTO（仅数据传输）
 */
@Data
public class ParameterDTO {
    /** 参数名 */
    private String prop;
    /** 参数值 */
    private String value;
    /** 方向：IN/OUT */
    private String direct;
    /** 类型：VARCHAR/INTEGER/LONG 等 */
    private String type;
}

