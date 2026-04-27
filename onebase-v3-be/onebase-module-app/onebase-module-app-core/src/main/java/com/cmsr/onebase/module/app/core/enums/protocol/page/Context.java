package com.cmsr.onebase.module.app.core.enums.protocol.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName Context
 * @Description 页面上下文，包含全局数据和页面数据
 * @Author mickey
 * @Date 2025/1/27 10:30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Context {

    /**
     * 全局数据
     */
    private GlobalData globalData;

    /**
     * 页面数据
     */
    private PageData pageData;
}
