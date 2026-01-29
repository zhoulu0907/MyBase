package com.cmsr.onebase.module.flow.build.vo;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新动作请求VO
 *
 * @author kanten
 * @since 2026-01-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "更新动作请求VO")
public class UpdateActionReqVO {

    @Schema(description = "表单值", example = "{\"url\":\"https://api.example.com\",\"method\":\"POST\"}", required = true)
    @NotNull(message = "表单值不能为空")
    private JsonNode formValues;
}
