package com.cmsr.onebase.module.app.controller.admin.app.vo;

import com.cmsr.onebase.module.app.controller.admin.tag.vo.TagRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/7/22 15:05
 */
@Schema(description = "应用管理 - 应用分页 Response VO")
@Data
public class ApplicationPageRespVO {

    @Schema(description = "应用名称")
    private String appName;

    @Schema(description = "应用编码")
    private String appCode;

    @Schema(description = "应用描述")
    private String description;

    @Schema(description = "图标类型")
    private String iconType;

    @Schema(description = "图标颜色")
    private String iconColor;

    @Schema(description = "应用状态")
    private String appStatusText;

    private List<TagRespVO> tags;

    @Schema(description = "创建时间")
    private String createTime;

    @Schema(description = "创建人")
    private String createUser;

    @Schema(description = "更新时间")
    private String updateTime;

    @Schema(description = "更新人")
    private String updateUser;
}
