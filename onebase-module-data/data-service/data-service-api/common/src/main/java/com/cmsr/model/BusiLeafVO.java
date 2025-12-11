package com.cmsr.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "业务资源结点")
@Data
public class BusiLeafVO {
    @Serial
    private static final long serialVersionUID = 8191619596741217494L;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "ID")
    private Long id;
    @Schema(description = "名称")
    private String name;
    @Schema(description = "是否叶子")
    private Boolean leaf;
    @Schema(description = "权重")
    private Integer weight;
    @Schema(description = "额外标识")
    private Integer extraFlag;
    @Schema(description = "额外标识1")
    private Integer extraFlag1;
    @Schema(description = "类型")
    private String type;
    @Schema(description = "子节点")
    private List<BusiNodeVO> children;
    @Schema(description = "独立权重")
    private Integer ext;
}
