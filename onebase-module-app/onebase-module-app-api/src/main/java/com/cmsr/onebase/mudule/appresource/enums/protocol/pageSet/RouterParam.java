package com.cmsr.onebase.mudule.appresource.enums.protocol.pageSet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName RouterParam
 * @Description 路由参数定义
 * @Author mickey
 * @Date 2025/1/27 10:30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouterParam {

    /**
     * 参数名称
     */
    private String name;

    /**
     * 参数类型
     */
    private String type;
}
