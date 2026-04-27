package com.cmsr.onebase.module.infra.dal.vo.app;

import com.cmsr.onebase.framework.common.validation.InEnum;
import com.cmsr.onebase.module.infra.enums.file.FileVisitModeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "用户 App - 上传文件 Request VO")
@Data
public class AppFileUploadReqVO {

    @Schema(description = "文件附件", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "文件附件不能为空")
    private MultipartFile file;

    @Schema(description = "文件目录", example = "可选，也可指定路径如：XXX/YYY")
    private String directory;

    @Schema(description = "文件保存标识",example = "public-公开访问，authen-文件需登录鉴权,permission-内部调用", requiredMode = Schema.RequiredMode.REQUIRED)
    @InEnum(value = FileVisitModeEnum.class, message = "访问标识必须是 {value}")
    private String visitMode;

}
