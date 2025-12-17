package com.cmsr.onebase.module.system.vo.config;

import lombok.Data;

@Data
public class SystemGeneralConfigSaveReqVO {
    /**
     * 参数分类
     */
    private String category;
    /**
     * 参数名称
     */
    private String name;
    /**
     * 参数键名
     *
     */
    private String configKey;
    /**
     * 参数键值
     */
    private String configValue;

    /**
     * 互斥项
     */
    private String exclusiveItem;

    /**
     * 参数类型
     *
     * 枚举
     */
    private Integer status;

    /**
     * 归属企业ID
     */
    private Long corpId;

    /**
     * 备注
     */
    private String remark;
}
