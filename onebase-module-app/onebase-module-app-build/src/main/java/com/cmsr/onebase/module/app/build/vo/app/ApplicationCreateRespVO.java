package com.cmsr.onebase.module.app.build.vo.app;

import com.cmsr.onebase.framework.common.enums.CommonPublishModelEnum;
import com.cmsr.onebase.framework.common.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/7/22 16:15
 */
@Schema(description = "应用管理 - 应用创建/修改 Request VO")
@Data
public class ApplicationCreateRespVO {

    @Schema(description = "应用ID")
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

    @Schema(description = "图标类型")
    private String iconName;

    @Schema(description = "图标颜色")
    private String iconColor;

    @Schema(description = "应用描述")
    private String description;

    @Schema(description = "标签ID")
    private List<Long> tagIds;

    @Schema(description = "发布模式")
    @InEnum(value = CommonPublishModelEnum.class, message = "返回值类型必须是 {value}")
    private String publishModel;

}
