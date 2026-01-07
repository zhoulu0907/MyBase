package com.cmsr.onebase.module.system.vo.user;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.pojo.PageParam;
import com.cmsr.onebase.framework.common.validation.InEnum;
import com.cmsr.onebase.module.system.enums.login.LongTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;


@Schema(description = "第三方用户注册用户")
@Data
public class ThirdUserRegisterReqVO {

    @Schema(description = "手机")
    private String mobile;

    @Schema(description = "应用ID")
    private Long appId;

    @Schema(description = "验证码",  example = "10")
    private String verifyCode;

}