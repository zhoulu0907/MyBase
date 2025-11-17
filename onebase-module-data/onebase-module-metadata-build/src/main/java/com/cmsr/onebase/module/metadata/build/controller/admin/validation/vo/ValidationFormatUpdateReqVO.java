package com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 格式校验更新请求VO
 *
 * @author matianyu
 * @date 2025-08-28
 */
@Schema(description = "管理后台 - 格式校验更新请求 VO")
@Data
public class ValidationFormatUpdateReqVO {

    @Schema(description = "规则组ID（前端传入的id即校验规则组ID，用于定位唯一该类型校验记录）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "规则组ID不能为空")
    private Long id;

    @Schema(description = "规则组名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "用户信息校验")
    @NotBlank(message = "规则组名称不能为空")
    private String rgName;

    @Schema(description = "是否启用", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "是否启用不能为空")
    private Integer isEnabled;

    @Schema(description = "格式代码", example = "EMAIL")
    @JsonAlias({"formatType", "formatValidationType"})
    private String formatCode;

    @Schema(description = "正则表达式", example = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    @JsonAlias("regex")
    private String regexPattern;

    @Schema(description = "标识符", example = "i")
    private String flags;

    @Schema(description = "提示信息", example = "请输入有效的邮箱地址")
    private String promptMessage;

    @Schema(description = "运行模式", example = "1")
    private Integer runMode;

    @Schema(description = "校验方式", example = "POP")
    private String valMethod;

    @Schema(description = "弹窗提示内容", example = "不满足条件，无法提交")
    private String popPrompt;

    @Schema(description = "弹窗类型", example = "SHORT")
    private String popType;
    
    /**
     * 兼容性方法：为BeanUtils提供promptMessage字段的getter
     * 优先返回popPrompt的值，如果popPrompt为空则返回promptMessage的值
     */
    @JsonProperty("promptMessage")  
    public String getPromptMessage() {
        return popPrompt != null ? popPrompt : promptMessage;
    }
}
