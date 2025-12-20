package com.cmsr.onebase.module.system.vo.user;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ThirdUserAppCombinedUpdateReqVO {

    @Schema(description = "用户id", example = "onebase")
    @NotNull(message = "用户id不能为空")
    private Long id;

    @ExcelProperty("用户名称")
    private String nickName;

    @Schema(description = "用户邮箱", example = "a@b.cn")
    private String email;

    @Schema(description = "用户头像", example = "")
    private String avatar;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "应用id")
    private List<Long> applicationIdList;

}
