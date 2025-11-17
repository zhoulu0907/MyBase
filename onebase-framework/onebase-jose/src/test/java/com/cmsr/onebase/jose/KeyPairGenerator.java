package com.cmsr.onebase.jose;

import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;

public class KeyPairGenerator {

    //@Test
    public void test1() throws Exception {
        // 1. 生成密钥对
        String prefix = "onebase-framework/onebase-jose/src/main/resources/";
        String keyId = "OneBase3.0-2025年11月12日";
        RSAKey rsaKey = new RSAKeyGenerator(2048)
                .keyUse(KeyUse.ENCRYPTION)
                .algorithm(JWEAlgorithm.ECDH_1PU_A256KW)
                .keyID(keyId)
                .generate();
        // 密钥保存文件
        String publicKeyFile = prefix + "public-key.json";
        String privateKeyFile = prefix + "private-key.json";
        saveKeyFile(rsaKey.toPublicJWK().toJSONString(), publicKeyFile);
        saveKeyFile(rsaKey.toJSONString(), privateKeyFile);
    }


    private static void saveKeyFile(String key, String keyFile) throws Exception {
        try (FileWriter sw = new FileWriter(keyFile)) {
            sw.write(key);
        }
    }

    @Test
    public void test2() throws Exception {
        JoseGenerator.main(new String[]{"9000"});
        //var b = JoseValidator.validate("eyJlbmMiOiJBMTkyQ0JDLUhTMzg0IiwiYWxnIjoiUlNBLU9BRVAtNTEyIn0.ZZ7ZPjPeGZmgstrEe1PEnp4tV72zHnwEPhmk9jf09M0TbzYFjREBViicRSshKSB_OeobZ7M9fQir5X-sySmF5Kp1T57wRYzEkb3LRS4Pp62AoQha2jqD_vH3PbZS_t0nGXoL_PwuOjUcR70HdooKl-YOUxVrx23HzTXLstd_2IZqvKH6kvc-60CGeWElLYIKMSgD0uGf58zzTF8F3I2QNENu7rVT8fXrlZRehPvDv1kptWmPaEyPbo7aOAzy8ZAo0QH_XZPhIaK978s_7xUFnwkYWO2hMHDzxme_cCerU8cYc_2UycQsmXpjQXyCj3VH5IEws2PwgPE5UfyaUhHmTA.fRm3h2eqJAq8Sfwu3dDsaQ.ZMbx1Ewt8_74crxVZTMeWvtatHmxQ0tlpZj4bCzDCKxk0wCS4CWMP3S8I0FrIb3ZugTrVNI7jXsAvCHD3Gfk5Id-2RXKHPZ2880wPwvcrCjj5v7EK5LUT7B7VZk4_Tpo_oYU12PODqBCggSNG_jKMEvRj6BFV0aeM55-xRYaFok.Ocku5T6EDk21D2efkPLzhdb_NgumJ-7Y");
        //System.out.println(b);
    }

}
