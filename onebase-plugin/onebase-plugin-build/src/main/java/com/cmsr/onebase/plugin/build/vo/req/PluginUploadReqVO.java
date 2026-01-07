package com.cmsr.onebase.plugin.build.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 插件上传请求 VO（首次上传）
 *
 * @author matianyu
 * @date 2026-01-06
 */
@Schema(description = "管理后台 - 插件上传 Request VO")
@Data
public class PluginUploadReqVO {

    @Schema(description = "插件名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "示例插件")
    @NotBlank(message = "插件名称不能为空")
    private String pluginName;

    @Schema(description = "插件图标文件")
    private MultipartFile pluginIcon;

    @Schema(description = "插件描述", example = "这是一个示例插件")
    private String pluginDescription;

    @Schema(description = "插件版本", requiredMode = Schema.RequiredMode.REQUIRED, example = "1.0.0")
    @NotBlank(message = "插件版本不能为空")
    private String pluginVersion;

    @Schema(description = "版本描述", example = "初始版本")
    private String pluginVersionDescription;

    @Schema(description = "插件ZIP包", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "插件文件不能为空")
    private MultipartFile file;

}
