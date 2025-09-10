package com.cmsr.onebase.module.app.core.enums.protocol.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName Bpm
 * @Description 业务流程配置，定义页面是否关联业务流程
 * @Author mickey
 * @Date 2025/1/27 10:30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bpm {

    /**
     * 是否启用业务流程
     */
    private Boolean enable;
}
