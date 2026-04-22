package com.cmsr.onebase.module.app.build.vo.custombutton;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "自定义按钮-排序请求")
public class CustomButtonSortReqVO {

    @NotNull(message = "页面集ID不能为空")
    @Schema(description = "页面集ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long pageSetId;

    @Valid
    @NotEmpty(message = "排序列表不能为空")
    @Schema(description = "排序项", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Item> items;

    @Data
    public static class Item {

        @NotNull(message = "按钮ID不能为空")
        @Schema(description = "按钮ID", requiredMode = Schema.RequiredMode.REQUIRED)
        private Long id;

        @NotNull(message = "排序号不能为空")
        @Schema(description = "排序号", requiredMode = Schema.RequiredMode.REQUIRED)
        private Integer sortNo;
    }
}
