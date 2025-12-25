package com.cmsr.onebase.module.flow.build.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/11/17 17:26
 */
@Data
@Schema(description = "节点分类")
public class NodeConfigVO {

    @Schema(description = "level1_code")
    private String level1Code;

    @Schema(description = "level2_code")
    private String level2Code;

    @Schema(description = "level3_code")
    private String level3Code;

    @Schema(description = "类别名称")
    private String nodeName;

    @Schema(description = "类别编码，唯一的")
    private String nodeCode;

    @Schema(description = "简单备注")
    private String simpleRemark;

    @Schema(description = "详细描述")
    private String detailDescription;

}
