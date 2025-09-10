package com.cmsr.onebase.module.build.controller.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/14 18:09
 */
@Data
@Schema(description = "应用管理 - 数据权限组 Response VO")
public class AuthDetailDataPermissionVO {

    @Schema(description = "数据访问权限")
    private List<AuthDataGroupVO> authDataGroups;

    private List<AuthFieldInfoVO> fieldInfo;

}
