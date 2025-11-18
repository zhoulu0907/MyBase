package com.cmsr.onebase.module.flow.build.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/11/17 17:26
 */
@Data
@Schema(description = "节点分类")
public class NodeTypeVO {

    @Schema(description = "level1_code")
    private String level1Code;

    @Schema(description = "level2_code")
    private String level2Code;

    @Schema(description = "level3_code")
    private String level3Code;

    @Schema(description = "type_name")
    private String typeName;

    @Schema(description = "type_code")
    private String typeCode;

    @Schema(description = "简单备注k")
    private String simpleRemark;

    @Schema(description = "详细描述")
    private String detailDescription;

}
