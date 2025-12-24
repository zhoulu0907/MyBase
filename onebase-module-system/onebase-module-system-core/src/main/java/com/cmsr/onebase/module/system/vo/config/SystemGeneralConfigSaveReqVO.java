package com.cmsr.onebase.module.system.vo.config;

import com.cmsr.onebase.framework.common.validation.InEnum;
import com.cmsr.onebase.module.system.enums.config.ConfigTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SystemGeneralConfigSaveReqVO {
    /**
     * 参数分类
     */
    @Schema(description = "参数分类", example = "")
    @InEnum(value = ConfigTypeEnum.class, message = "参数类型必须是 {value}")
    private String configType;
    /**
     * 参数名称
     */
    @Schema(description = "名称", example = "")
    private String name;
    /**
     * 参数键名
     *
     */
    @Schema(description = "配置项", example = "")
    private String configKey;
    /**
     * 参数键值
     */
    @Schema(description = "配置值", example = "")
    private String configValue;

    /**
     * 互斥项
     */
    @Schema(description = "互斥项", example = "")
    private String exclusiveItem;

    /**
     * 参数类型
     *
     * 枚举
     */
    @Schema(description = "状态", example = "")
    private Integer status;

    /**
     * 归属企业ID
     */
    @Schema(description = "企业id", example = "")
    private Long corpId;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "")
    private String remark;
}
