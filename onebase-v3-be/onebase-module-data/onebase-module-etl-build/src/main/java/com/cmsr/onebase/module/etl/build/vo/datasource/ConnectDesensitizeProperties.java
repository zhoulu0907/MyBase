package com.cmsr.onebase.module.etl.build.vo.datasource;

import com.cmsr.onebase.framework.desensitize.annotation.PasswordDesensitize;
import lombok.Data;

@Data
public class ConnectDesensitizeProperties {

    private String connectMode;

    private String host;

    private String port;

    private String database;

    private String username;

    private String driver;

    private String jdbcUrl;

    @PasswordDesensitize
    private String password;
}
