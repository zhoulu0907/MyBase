package com.cmsr.onebase.module.infra.dal.vo.file.file;

import com.cmsr.onebase.framework.common.enums.UserTypeEnum;
import com.cmsr.onebase.framework.common.validation.InEnum;
import com.cmsr.onebase.module.infra.enums.file.FileVisitModeEnum;
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


    @Schema(description = "文件保存标识",example = "public-公开访问，private-各runMode私有访问", requiredMode = Schema.RequiredMode.REQUIRED)
    @InEnum(value = FileVisitModeEnum.class, message = "访问标识 {value}")
    private String visitMode;

}
