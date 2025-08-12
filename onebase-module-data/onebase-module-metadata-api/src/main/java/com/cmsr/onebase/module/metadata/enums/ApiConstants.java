package com.cmsr.onebase.module.metadata.enums;

import com.cmsr.onebase.framework.common.enums.RpcConstants;

/**
 * Metadata API 相关的枚举
 *
 * @author matianyu
 * @date 2025-08-12
 */
public class ApiConstants {

    /**
     * 服务名
     *
     * 注意，需要保证和 spring.application.name 保持一致
     */
    public static final String NAME = "metadata-server";

    public static final String PREFIX = RpcConstants.RPC_API_PREFIX + "/metadata";

    public static final String VERSION = "1.0.0";

}
