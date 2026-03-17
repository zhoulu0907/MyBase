package com.cmsr.onebase.module.system.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 对外查询接口-获取用户创建的应用数量 Response VO
 *
 * @author yuxin
 * @date 2026-03-07
 */
@Schema(description = "对外查询接口-获取用户创建的应用数量 Response VO")
@Data
public class UserAppCountRespVO {


    @Schema(description = "租户/用户下应用数量", example = "1")
    private Long appCount;

}
