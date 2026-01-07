package com.cmsr.onebase.module.system.vo.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SystemGeneralConfigUpdateReqVO {
    /**
     * 参数分类
     */
    @Schema(description = "id", example = "")
    private Long id;

    /**
     * appId
     */
    @Schema(description = "appId", example = "")
    private Long appId;

    @Schema(description = "key", example = "")
    private String configKey;

    /**
     * 参数名称
     */
    @Schema(description = "名称", example = "")
    private String name;
    /**
     * 参数键值
     */
    @Schema(description = "互斥项", example = "")
    private String configValue;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "")
    private String remark;
}
