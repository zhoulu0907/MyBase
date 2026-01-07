package com.cmsr.onebase.plugin.build.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 插件上传请求 VO
 *
 * @author matianyu
 * @date 2026-01-06
 */
@Schema(description = "管理后台 - 插件上传 Request VO")
@Data
public class PluginUploadReqVO {

    @Schema(description = "插件ZIP包", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "插件文件不能为空")
    private MultipartFile file;

}
