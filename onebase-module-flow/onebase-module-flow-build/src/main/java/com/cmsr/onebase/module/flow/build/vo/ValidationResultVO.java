package com.cmsr.onebase.module.flow.build.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 校验结果VO
 *
 * @author kanten
 * @since 2026-01-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "校验结果VO")
public class ValidationResultVO {

    @Schema(description = "是否校验通过", example = "true")
    private Boolean valid;

    @Schema(description = "错误信息列表", example = "[\"URL格式不正确\", \"请求头缺失\"]")
    private List<String> errors;
}
