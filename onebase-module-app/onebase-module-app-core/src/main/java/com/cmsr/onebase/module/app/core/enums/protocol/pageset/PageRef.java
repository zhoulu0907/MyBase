package com.cmsr.onebase.module.app.core.enums.protocol.pageset;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName PageRef
 * @Description 页面引用定义
 * @Author mickey
 * @Date 2025/1/27 10:30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRef {

    /**
     * 页面名称
     */
    private String name;

    /**
     * 页面引用,唯一
     */
    private String ref;

    /**
     * 默认排序
     */
    private Integer defaultSeq;

    /**
     * 页面标题
     */
    private String title;

    /**
     * 是否为默认页面
     */
    private Boolean defaultPage;

    /**
     * 路由参数配置
     */
    private List<RouterParam> routerParams;
}
