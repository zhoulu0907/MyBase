package com.cmsr.onebase.plugin.build.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 插件版本更新请求 VO
 *
 * @author matianyu
 * @date 2026-01-06
 */
@Schema(description = "管理后台 - 插件版本更新 Request VO")
@Data
public class PluginVersionUpdateReqVO {

    @Schema(description = "版本记录ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "版本记录ID不能为空")
    private Long id;

    @Schema(description = "版本描述", example = "修复xxx问题")
    private String pluginVersionDescription;

    @Schema(description = "插件安装包（可选，重新上传则覆盖原包）")
    private MultipartFile file;

}
