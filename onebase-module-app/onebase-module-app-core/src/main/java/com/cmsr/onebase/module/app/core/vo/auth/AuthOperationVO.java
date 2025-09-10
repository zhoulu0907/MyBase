package com.cmsr.onebase.module.app.core.vo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * @Author：huangjie
 * @Date：2025/8/7 17:04
 */
@Data
@Schema(description = "应用管理 - 操作权限 Response VO")
public class AuthOperationVO {

    @Schema(description = "主键Id")
    private Long id;

    @Schema(description = "操作编码")
    private String operationCode;

    @Schema(description = "操作名称")
    private String displayName;

    @Schema(description = "是否允许")
    private Integer isAllowed = NumberUtils.INTEGER_ONE;

}
