package com.cmsr.onebase.plugin.build.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 插件新版本上传请求 VO
 *
 * @author matianyu
 * @date 2026-01-06
 */
@Schema(description = "管理后台 - 插件新版本上传 Request VO")
@Data
public class PluginVersionUploadReqVO {

    @Schema(description = "插件ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "test-plugin")
    @NotBlank(message = "插件ID不能为空")
    private String pluginId;

    @Schema(description = "版本号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1.0.0")
    @NotBlank(message = "版本号不能为空")
    private String pluginVersion;

    @Schema(description = "版本描述", example = "新增xxx功能")
    private String pluginVersionDescription;

    @Schema(description = "插件安装包（zip或jar）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "插件文件不能为空")
    private MultipartFile file;

}
