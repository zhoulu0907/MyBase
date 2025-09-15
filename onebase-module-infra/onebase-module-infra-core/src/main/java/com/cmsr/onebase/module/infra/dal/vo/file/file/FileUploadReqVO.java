package com.cmsr.onebase.module.infra.dal.vo.file.file;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "管理后台 - 上传文件 Request VO")
@Data
public class FileUploadReqVO {

    @Schema(description = "文件附件", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "文件附件不能为空")
    private MultipartFile file;

    @Schema(description = "文件目录", example = "可选，也可指定路径如：XXX/YYY")
    private String directory;

}
