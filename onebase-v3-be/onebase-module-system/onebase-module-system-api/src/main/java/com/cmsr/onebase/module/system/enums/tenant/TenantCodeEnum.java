package com.cmsr.onebase.module.system.enums.tenant;

import com.cmsr.onebase.framework.common.util.object.ObjectUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 角色标识枚举
 */
@Getter
@AllArgsConstructor
public enum TenantCodeEnum {
    PLATFORM_TENANT("platform-tenant", "平台租户"),
    ;

    /**
     * 角色编码
     */
    private final String code;
    /**
     * 名字
     */
    private final String name;

    public static boolean isPlatformTenant(String code) {
        return ObjectUtils.equalsAny(code, PLATFORM_TENANT.getCode());
    }

}
