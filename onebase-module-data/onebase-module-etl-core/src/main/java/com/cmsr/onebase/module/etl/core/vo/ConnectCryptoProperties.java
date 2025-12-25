package com.cmsr.onebase.module.etl.core.vo;

import com.cmsr.onebase.framework.desensitize.annotation.PasswordCrypto;
import lombok.Data;

@Data
public class ConnectCryptoProperties {

    private String connectMode;

    private String host;

    private String port;

    private String database;

    private String username;

    private String driver;

    private String jdbcUrl;

    @PasswordCrypto
    private String password;
}
