package com.cmsr.onebase.module.app.core.enums.protocol.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName PageSpec
 * @Description 页面规格定义，包含页面的基本配置、路由、数据模型、上下文、业务流程、方法和组件等信息
 * @Author mickey
 * @Date 2025/1/27 10:30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageSpec {

    /**
     * 基本信息配置
     */
    private BasicInfo basicInfo;

    /**
     * 路由信息
     */
    private Router router;

    /**
     * 数据模型
     */
    private Metadata metadata;

    /**
     * 页面上下文
     */
    private Context context;

    /**
     * 业务流程配置
     */
    private Bpm bpm;

    /**
     * 页面方法
     */
    private Methods methods;

    /**
     * 组件列表
     */
    private List<Component> components;
}
