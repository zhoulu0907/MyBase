package com.cmsr.onebase.module.flow.build.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/12/22 11:03
 */
@Data
public class NodeConfigConnVO {

    @Schema(description = "连接配置类型")
    private String connConfigType;

    @Schema(description = "连接配置")
    private String connConfig;

}
