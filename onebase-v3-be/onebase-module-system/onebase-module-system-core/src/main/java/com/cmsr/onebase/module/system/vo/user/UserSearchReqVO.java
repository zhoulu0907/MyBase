package com.cmsr.onebase.module.system.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "平台管理员模糊查询")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchReqVO {

    @Schema(description = "关键字", example = "onebase")
    private String keyword;

    @Schema(description = "状态", example = "1")
    private Integer status;
}
