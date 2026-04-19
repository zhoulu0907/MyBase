package com.cmsr.onebase.module.tiangong.vo.alert;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 右下提示框
 */
@Schema(description = "提示框 VO")
@Data
public class AlertResVO {

    @Schema(description = "提醒标题")
    private String title;

    @Schema(description = "提醒内容")
    private String content;

    @Schema(description = "跳转连接")
    private String linkUrl;
}