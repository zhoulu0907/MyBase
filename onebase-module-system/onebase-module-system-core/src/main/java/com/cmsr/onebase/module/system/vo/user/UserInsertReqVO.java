package com.cmsr.onebase.module.system.vo.user;

import cn.hutool.core.util.ObjUtil;
import com.cmsr.onebase.framework.common.enums.UserTypeEnum;
import com.cmsr.onebase.framework.common.validation.InEnum;
import com.cmsr.onebase.framework.common.validation.Mobile;
import com.cmsr.onebase.module.system.framework.operatelog.core.DeptParseFunction;
import com.cmsr.onebase.module.system.framework.operatelog.core.PostParseFunction;
import com.cmsr.onebase.module.system.framework.operatelog.core.SexParseFunction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mzt.logapi.starter.annotation.DiffLogField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Set;

@Schema(description = "管理后台 - 用户创建/修改 Request VO")
@Data
public class UserInsertReqVO {

    @Schema(description = "用户编号", example = "1024")
    private Long id;

    @Schema(description = "用户账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "onebase")
    @NotBlank(message = "用户账号不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "用户账号由 数字、字母 组成")
    @Size(min = 4, max = 30, message = "用户账号长度为 4-30 个字符")
    @DiffLogField(name = "用户账号")
    private String username;

    @Schema(description = "用户昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "onebase")
    @Size(max = 30, message = "用户昵称长度不能超过30个字符")
    @DiffLogField(name = "用户昵称")
    private String nickname;

    // @Schema(description = "备注", example = "我是一个用户")
    // @DiffLogField(name = "备注")
    // private String remark;

    @Schema(description = "部门编号", example = "我是一个用户")
    @DiffLogField(name = "部门", function = DeptParseFunction.NAME)
    private Long deptId;

    @Schema(description = "岗位编号数组", example = "1")
    @DiffLogField(name = "岗位", function = PostParseFunction.NAME)
    private Set<Long> postIds;

    @Schema(description = "用户邮箱", example = "onebase@aaa.com")
    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过 50 个字符")
    @DiffLogField(name = "用户邮箱")
    private String email;

    @Schema(description = "手机号码", example = "15601691300")
    @Mobile
    @NotBlank(message = "手机号码不能为空")
    @DiffLogField(name = "手机号码")
    private String mobile;

    @Schema(description = "用户性别，参见 SexEnum 枚举类", example = "1")
    @DiffLogField(name = "用户性别", function = SexParseFunction.NAME)
    private Integer sex;

    @Schema(description = "用户头像", example = "https://www.cmsr.com")
    @DiffLogField(name = "用户头像")
    private String avatar;

    @Schema(description = "管理员类型", example = "2")
    @DiffLogField(name = "管理员类型")
    private Integer adminType;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "角色Ids", example = "[1,2]")
    private Set<Long> roleIds;


    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    private String password;

    @Schema(description = "来自平台克隆的用户id", example = "")
    private Long platformUserId;


    @Schema(description = "类型", example = "1")
    @InEnum(value= UserTypeEnum.class, message = "用户类型不正确：{value}")
    private Integer userType;

    @AssertTrue(message = "密码不能为空")
    @JsonIgnore
    public boolean isPasswordValid() {
        return id != null // 修改时，不需要传递
                || (ObjUtil.isAllNotEmpty(password)); // 新增时，必须都传递 password
    }

}
