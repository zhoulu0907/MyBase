package com.cmsr.onebase.module.app.core.enums.protocol.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName RouterMeta
 * @Description 路由元数据，包含认证要求、标题等信息
 * @Author mickey
 * @Date 2025/1/27 10:30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouterMeta {

    /**
     * 是否需要认证
     */
    private Boolean authRequired;

    /**
     * 页面标题
     */
    private String title;
}
