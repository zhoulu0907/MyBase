package com.cmsr.onebase.module.system.vo.corp;

import com.cmsr.onebase.module.app.api.app.dto.AppUserPhotoDTO;
import com.cmsr.onebase.module.app.api.app.dto.TagVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CorpApplicationRespVO {

    @Schema(description = "Id")
    private Long id;
    @Schema(description = "应用名称")
    private String applicationName;
    @Schema(description = "应用uid")
    private String applicationUid;
    @Schema(description = "应用编码")
    private String applicationCode;
    @Schema(description = "应用Id")
    private Long applicationId;
    @Schema(description = "授权时间")
    private LocalDateTime authorizationTime ;
    @Schema(description = "版本号")
    private String versionNumber;
    @Schema(description = "过期时间")
    private LocalDateTime expiresTime;
    @Schema(description = "状态值")
    private Integer    showStatus;


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



    @Schema(description = "状态")
    private Integer appStatus;

    @Schema(description = "应用状态")
    private String appStatusText;

    @Schema(description = "应用描述")
    private String description;

    @Schema(description = "标签")
    private List<TagVO> tags;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "创建人")
    private String createUser;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "更新人")
    private String updateUser;

    @Schema(description = "发布模式")
    private String publishModel;

    @Schema(description = "头像集合")
    private  List<AppUserPhotoDTO> userPhotoList;

    @Schema(description = "开发状态")
    private String developStatus;

}
