package com.cmsr.onebase.module.app.runtime.vo.custombutton;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "运行态-自定义按钮执行请求")
public class RuntimeCustomButtonExecuteReqVO {

    @NotBlank(message = "按钮编码不能为空")
    @Schema(description = "按钮编码。构建态创建按钮后生成的 buttonCode", requiredMode = Schema.RequiredMode.REQUIRED)
    private String buttonCode;

    @NotNull(message = "页面集ID不能为空")
    @Schema(description = "页面集ID。用于确认按钮所属页面集", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long pageSetId;

    @Schema(description = "菜单ID。用于权限校验；为空则跳过菜单操作权限校验")
    private Long menuId;

    @Schema(description = "页面ID。用于记录执行来源页面")
    private Long pageId;

    @Schema(description = "记录ID。单条记录执行时传当前业务记录ID")
    private String recordId;

    @Schema(description = "动作载荷JSON字符串。用于提交 UPDATE_FORM 字段值、OPEN_PAGE 额外参数等运行态数据")
    private String actionPayload;
}
