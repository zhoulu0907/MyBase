package com.cmsr.onebase.module.flow.build.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Connector Type List VO
 * @Author：zhoulu
 * @Date：2025/01/20
 */
@Data
@Schema(description = "Connector Type List")
public class ConnectorTypeListVO {

    @Schema(description = "连接器类别：level1-level2-level3")
    private String Category;

    @Schema(description = "node_name")
    private String nodeName;

    @Schema(description = "node_code")
    private String nodeCode;

}