package com.cmsr.onebase.module.app.build.vo.app;

import com.cmsr.onebase.module.app.core.vo.tag.TagRespVO;
import com.cmsr.onebase.module.app.core.vo.app.AppUserPhotoDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/7/22 15:05
 */
@Schema(description = "应用管理 - 应用分页 Response VO")
@Data
public class ApplicationRespVO {

    @Schema(description = "应用Id")
    private Long id;

    @Schema(description = "应用uid")
    private String appUid;

    @Schema(description = "应用名称")
    private String appName;

    @Schema(description = "应用编码")
    private String appCode;

    @Schema(description = "应用模式")
    private String appMode;

    @Schema(description = "主题色")
    private String themeColor;

    @Schema(description = "图标名称")
    private String iconName;

    @Schema(description = "图标颜色")
    private String iconColor;

    @Schema(description = "版本号")
    private String versionNumber;

    @Schema(description = "状态")
    private Integer appStatus;

    @Schema(description = "应用状态")
    private String appStatusText;

    @Schema(description = "发布状态")
    private Integer publishStatus;

    @Schema(description = "应用描述")
    private String description;

    @Schema(description = "标签")
    private List<TagRespVO> tags;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "创建人")
    private Long creator;

    @Schema(description = "更新人")
    private Long updater;

    @Schema(description = "创建人")
    private String createUser;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "更新人")
    private String updateUser;

    @Schema(description = "发布模式")
    private String publishModel;

    @Schema(description = "头像集合")
    private List<AppUserPhotoDTO> userPhotoList;

    @Schema(description = "开发状态")
    private String developStatus;

    @Schema(description = "发布人")
    private String publisher;

    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

}
