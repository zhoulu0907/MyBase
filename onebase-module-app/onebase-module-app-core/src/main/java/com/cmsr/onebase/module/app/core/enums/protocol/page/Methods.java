package com.cmsr.onebase.module.app.core.enums.protocol.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @ClassName Methods
 * @Description 页面方法配置，包含提交、保存等业务逻辑方法
 * @Author mickey
 * @Date 2025/1/27 10:30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Methods {

    /**
     * 提交方法配置
     */
    private Map<String, Object> submit;

    /**
     * 保存方法配置
     */
    private Map<String, Object> save;
}
