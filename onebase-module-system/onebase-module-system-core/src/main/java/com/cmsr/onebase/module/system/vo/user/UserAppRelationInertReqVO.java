package com.cmsr.onebase.module.system.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
@Schema(description = "用户应用关联创建VO")
@Data
public class UserAppRelationInertReqVO {
    @Schema(description = "用户Id", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @NotNull(message = "用户Id不能为空")
    private Long userId;

    @Schema(description = "姓名")
    private String nickName;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "应用id")
    @NotNull(message = "企业id list不能为空")
    private List<Long> applicationIdList;

}
