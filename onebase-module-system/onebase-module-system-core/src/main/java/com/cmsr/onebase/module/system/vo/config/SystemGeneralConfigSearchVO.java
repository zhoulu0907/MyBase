package com.cmsr.onebase.module.system.vo.config;

import lombok.Data;

@Data
public class SystemGeneralConfigSearchVO {

    private String category;

    /**
     * 参数键名
     *
     */
    private String configKey;


    /**
     * 归属企业ID
     */
    private Long corpId;


    /**
     * 归属企业ID
     */
    private Long tenantId;


}
