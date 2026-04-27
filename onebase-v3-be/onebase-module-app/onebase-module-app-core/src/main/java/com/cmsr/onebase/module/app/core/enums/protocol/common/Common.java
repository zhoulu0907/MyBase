package com.cmsr.onebase.module.app.core.enums.protocol.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Common {
    /**
     * apiVersion，资源 API 版本
     */
    private String apiVersion;

    /**
     * kind，资源类型
     */
    private String kind;

    /**
     * metadata，资源元数据
     */
    private Metadata metadata;
}
