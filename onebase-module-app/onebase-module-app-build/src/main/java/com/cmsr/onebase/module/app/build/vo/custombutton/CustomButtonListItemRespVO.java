package com.cmsr.onebase.module.app.build.vo.custombutton;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "自定义按钮-列表项")
public class CustomButtonListItemRespVO {

    @Schema(description = "按钮ID")
    private Long id;

    @Schema(description = "按钮编码")
    private String buttonCode;

    @Schema(description = "按钮名称")
    private String buttonName;

    @Schema(description = "操作范围")
    private String operationScope;

    @Schema(description = "动作类型")
    private String actionType;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "排序号")
    private Integer sortNo;
}
