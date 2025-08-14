package com.cmsr.onebase.module.system.enums.license;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LicenseSecretKeyEnum {

    /**
     * License 凭证解密密钥
     */
    LICENSE_SECRET_KEY("1234567812345678");

    /**
     * License 凭证解密密钥值
     */
    private final String secretKey;

}
