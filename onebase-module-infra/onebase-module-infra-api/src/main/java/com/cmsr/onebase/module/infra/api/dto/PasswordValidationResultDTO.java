package com.cmsr.onebase.module.infra.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 密码校验结果DTO
 *
 * @author chengyuansen
 * @date 2025-11-07
 */
@Schema(description = "密码校验结果")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordValidationResultDTO {

    /**
     * 校验是否通过
     */
    @Schema(description = "校验是否通过", example = "true")
    private Boolean valid;

    /**
     * 校验失败的错误消息
     */
    @Schema(description = "失败时的错误消息", example = "密码长度过短")
    private String message;

}
