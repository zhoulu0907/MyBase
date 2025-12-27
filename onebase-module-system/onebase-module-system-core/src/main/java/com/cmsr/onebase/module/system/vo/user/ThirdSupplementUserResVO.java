package com.cmsr.onebase.module.system.vo.user;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 补充用户信息VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThirdSupplementUserResVO {

    @Schema(description = "用户编号", example = "1024")
    private Long id;

    @Schema(description = "用户账号", example = "onebase")
    private String userName;

    @ExcelProperty("用户名称")
    private String nickName;

    @Schema(description = "用户邮箱", example = "a@b.cn")
    private String email;


}
