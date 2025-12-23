package com.cmsr.onebase.module.dashboard.build.vo.template;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@ApiModel("仪表盘模板返回 VO")
@Data
public class DashboardTemplateRespVO {

    @ApiModelProperty(value = "模板ID", required = true, example = "1")
    private String id;

    @ApiModelProperty(value = "模板名称", required = true)
    private String templateName;

    @ApiModelProperty(value = "模板内容（JSON格式）")
    private String content;

    @ApiModelProperty(value = "模板类型")
    private String templateType;

    @ApiModelProperty(value = "是否热门模板")
    private Integer hot;

    @ApiModelProperty(value = "应用ID")
    private Long appId;

    @ApiModelProperty(value = "租户ID")
    private Long tenantId;

    @ApiModelProperty(value = "创建者")
    private String creator;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "修改者")
    private String updater;

    @ApiModelProperty(value = "修改时间")
    private String updateTime;

    @ApiModelProperty(value = "索引图片")
    private String indexImage;

}