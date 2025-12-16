package com.cmsr.onebase.module.system.vo.config;

import lombok.Data;

@Data
public class SystemGeneralConfigUpdateReqVO {
    /**
     * 参数分类
     */
    private Long id;
    /**
     * 参数名称
     */
    private String name;
    /**
     * 参数键值
     */
    private String configValue;

    /**
     * 备注
     */
    private String remark;
}
