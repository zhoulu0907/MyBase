package com.cmsr.onebase.module.app.core.enums.protocol.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName Component
 * @Description 页面组件配置，包含组件的基本信息、数据配置、特殊配置和客户端配置
 * @Author mickey
 * @Date 2025/1/27 10:30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Component {

    /**
     * 组件ID
     */
    private String id;

    /**
     * 组件类型
     */
    private String type;

    /**
     * 组件配置模板
     */
    private String configTemplate;

//TODO
}
