package com.cmsr.onebase.module.system.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserRelationAppReqVO {
    @Schema(description = "用户id")
    private  Long userId;

    @Schema(description = "应用名称")
    private String appName ;
}
