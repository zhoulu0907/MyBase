package com.cmsr.onebase.module.flow.build.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/12/22 11:03
 */
@Data
public class NodeConfigActionVO {

    @Schema(description = "动作配置类型")
    private String actionConfigType;

    @Schema(description = "动作配置")
    private String actionConfig;

}
