package com.cmsr.onebase.module.app.core.enums.protocol.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName BasicInfo
 * @Description 页面基本信息配置，包含标题、布局、宽度、边距、背景色等
 * @Author mickey
 * @Date 2025/1/27 10:30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasicInfo {

    /**
     * 页面标题
     */
    private String title;

    /**
     * 布局方式（vertical: 垂直布局, horizontal: 水平布局）
     */
    private String layout;

    /**
     * 宽度设置（auto: 自动宽度, 固定像素值）
     */
    private String width;

    /**
     * 边距设置
     */
    private String margin;

    /**
     * 背景颜色
     */
    private String backgroundColor;
}
