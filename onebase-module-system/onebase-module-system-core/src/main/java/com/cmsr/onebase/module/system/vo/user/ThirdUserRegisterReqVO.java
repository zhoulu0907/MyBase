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
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ThirdUserRegisterReqVO extends PageParam {

    @Schema(description = "手机")
    private String mobile;

    @Schema(description = "用户名称")
    private Long appId;

    @Schema(description = "验证码",  example = "10")
    private String verifyCode;

    @Schema(description = "密码/验证码",  example = "10")
    @NotBlank(message = "登录方式不能为空")
    @InEnum(value = LongTypeEnum.class, message = "返回值类型必须是 {value}")
    private String loginType;
}