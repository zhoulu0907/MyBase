package com.cmsr.onebase.module.system.vo.user;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
@Data
public class ThirdUserAppCombinedInsertReqVO   {

    @Schema(description = "手机", example = "onebase")
    @NotBlank(message = "手机不能为空")
    private String mobile;

    @ExcelProperty("用户名称")
    @NotBlank(message = "用户昵称不能为空")
    private String nickname;

    @Schema(description = "用户邮箱", example = "a@b.cn")
    private String email;

    @Schema(description = "用户头像", example = "")
    private String avatar;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "应用id")
    @NotNull(message = "应用id列表")
    private List<Long> applicationIdList;

}
