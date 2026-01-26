package com.cmsr.onebase.module.flow.build.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 环境配置下拉选项VO
 * <p>
 * 用于连接器实例编辑页面的环境选择下拉框
 *
 * @author kanten
 * @since 2026-01-24
 */
@Schema(description = "环境配置下拉选项VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnvOptionVO {

    @Schema(description = "环境配置UUID（提交时使用）")
    private String value;

    @Schema(description = "显示文本（格式：环境名称 (环境编码)）")
    private String label;

    @Schema(description = "启用状态（0-禁用，1-启用）")
    private Integer activeStatus;
}
