package com.cmsr.onebase.module.etl.build.vo.datasource;

import com.cmsr.onebase.framework.desensitize.serializer.PasswordJsonDeserializer;
import com.cmsr.onebase.framework.desensitize.serializer.PasswordJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

@Data
public class ConnectProperties {
    private String connectMode;
    private String host;
    private String port;
    private String database;
    private String username;
    private String driver;
    private String jdbcUrl;
    @JsonSerialize(using = PasswordJsonSerializer.class)
    @JsonDeserialize(using = PasswordJsonDeserializer.class)
    private String password;
}
