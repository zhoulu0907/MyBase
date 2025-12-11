package com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 格式校验 创建/保存 请求VO
 *
 * @author bty418
 * @date 2025-08-28
 */
@Data
public class ValidationFormatSaveReqVO {

    @Schema(description = "字段UUID", example = "01onal1s-0000-0000-0000-000000000003")
    private String fieldUuid;

    @Schema(description = "字段ID（兼容旧版，与fieldUuid二选一）", example = "164329365983232003")
    private String fieldId;

    @Schema(description = "是否启用(0/1)", example = "1")
    private Integer isEnabled;

    @Schema(description = "格式代码：TEXT/EMAIL/MOBILE/ID_CARD/URL/IP/REGEX", example = "TEXT")
    @JsonAlias({"formatType", "formatValidationType"})
    private String formatCode;

    @Schema(description = "正则表达式")
    @JsonAlias("regex")
    private String regexPattern;

    @Schema(description = "正则标志位：i/m/s等", example = "i")
    private String flags;

    @Schema(description = "提示信息")
    private String promptMessage;

    @Schema(description = "版本标识")
    private Long versionTag;

    @Schema(description = "校验方式", example = "POP")
    private String valMethod;

    @Schema(description = "弹窗提示内容", example = "不满足条件，无法提交")
    private String popPrompt;

    @Schema(description = "弹窗类型", example = "SHORT")
    private String popType;

    @Schema(description = "规则组名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "规则组名称不能为空")
    private String rgName;
    
    /**
     * 兼容性方法：为BeanUtils提供promptMessage字段的getter
     * 优先返回popPrompt的值，如果popPrompt为空则返回promptMessage的值
     */
    @JsonProperty("promptMessage")
    public String getPromptMessage() {
        return popPrompt != null ? popPrompt : promptMessage;
    }
}
