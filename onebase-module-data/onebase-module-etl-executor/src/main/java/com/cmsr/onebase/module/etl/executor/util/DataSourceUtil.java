package com.cmsr.onebase.module.etl.executor.util;

import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import com.cmsr.onebase.module.etl.common.excute.ExecuteRequest;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bouncycastle.crypto.engines.SM2Engine;

import java.nio.charset.StandardCharsets;

/**
 * @Author：huangjie
 * @Date：2025/11/6 11:29
 */
public class DataSourceUtil {

    private static final String PRIVATE_KEY = "4cfb2e4fe10a10364604e69644c01df8c880ebf82703af0fbb20d96d9b4faad7";

    private static final String PUBLIC_KEY = "045efee7520c3ed4b3c6bb75424a3ae25039e25bd859731a1f6464cb7e5f7dfb419bcba55cc6adfb7f3e224a6e8949709a3664ff2dc4b822f50ee77bbd64ce3946";

    private static final SM2 sm2;

    static {
        sm2 = new SM2(PRIVATE_KEY, PUBLIC_KEY);
        // 使用 C1C3C2 模式解密,与前端保持一致
        sm2.setMode(SM2Engine.Mode.C1C3C2);
    }

    public static HikariDataSource createDataSource(ExecuteRequest executeRequest) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(executeRequest.getJdbcDriverClass());
        config.setJdbcUrl(executeRequest.getJdbcUrl());
        config.setUsername(executeRequest.getJdbcUserName());
        config.setPassword(executeRequest.getJdbcPassword());
        config.setMaximumPoolSize(1);
        config.setMinimumIdle(1);
        HikariDataSource dataSource = new HikariDataSource(config);
        return dataSource;
    }

    public static String getDecryptoPassword(String encryptoPassword) {
        byte[] plainBytes = sm2.decrypt(encryptoPassword, KeyType.PrivateKey);
        return new String(plainBytes, StandardCharsets.UTF_8);
    }
}
