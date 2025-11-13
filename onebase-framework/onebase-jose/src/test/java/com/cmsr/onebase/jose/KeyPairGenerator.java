package com.cmsr.onebase.jose;

import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;

import java.io.FileReader;
import java.io.FileWriter;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class KeyPairGenerator {

    public static void main(String[] args) throws Exception {
        // 1. 生成密钥对
        String prefix = "onebase-framework/onebase-jose/src/main/resources/";
        String keyId = "OneBase3.0-2025年11月12日";
        RSAKey keyGenerator = new RSAKeyGenerator(2048)
                .keyUse(KeyUse.ENCRYPTION)
                .algorithm(JWEAlgorithm.ECDH_1PU_A256KW)
                .keyID(keyId)
                .generate();

        // 密钥保存文件
        String publicKeyPem = prefix + "onebase_pub.pem";
        String privateKeyPem = prefix + "onebase_pri.pem";

        saveKeyPems(keyGenerator, publicKeyPem, privateKeyPem);

        // 验证
        RSAPublicKey pubKey = getFromPublicKeyPem(publicKeyPem);
        RSAPrivateKey priKey = getFromPrivateKeyPem(privateKeyPem);

        RSAKey loadedPublicKey = new RSAKey.Builder(pubKey)
                .keyUse(KeyUse.ENCRYPTION)
                .keyID(keyId).build();
        RSAKey loadedPrivateKey = new RSAKey.Builder(pubKey)
                .privateKey(priKey)
                .keyUse(KeyUse.ENCRYPTION)
                .keyID(keyId).build();

        boolean isPubMatch = loadedPublicKey.toPublicKey().equals(keyGenerator.toPublicKey());
        boolean isPriMatch = loadedPrivateKey.toPrivateKey().equals(keyGenerator.toPrivateKey());

        if (isPubMatch && isPriMatch) {
            System.out.println("生成完毕!");
        }
    }

    private static RSAPrivateKey getFromPrivateKeyPem(String privateKeyPem) throws Exception {
        try (FileReader keyReader = new FileReader(privateKeyPem)) {
            PEMParser pemParser = new PEMParser(keyReader);
            PEMKeyPair pemKeyPair = (PEMKeyPair) pemParser.readObject();
            PrivateKeyInfo privateKeyInfo = pemKeyPair.getPrivateKeyInfo();

            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            PrivateKey privateKey = converter.getPrivateKey(privateKeyInfo);
            return (RSAPrivateKey) privateKey;
        }
    }

    private static RSAPublicKey getFromPublicKeyPem(String publicKeyPem) throws Exception {
        try (FileReader keyReader = new FileReader(publicKeyPem)) {
            PEMParser pemParser = new PEMParser(keyReader);
            SubjectPublicKeyInfo publicKeyInfo = (SubjectPublicKeyInfo) pemParser.readObject();

            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            return (RSAPublicKey) converter.getPublicKey(publicKeyInfo);
        }
    }

    private static void saveKeyPems(RSAKey keyGenerator, String publicKeyPem, String privateKeyPem) throws Exception {
        RSAKey publicJWK = keyGenerator.toPublicJWK();
        PublicKey publicKey = publicJWK.toPublicKey();
        try (FileWriter fileWriter = new FileWriter(publicKeyPem)) {
            JcaPEMWriter pemWriter = new JcaPEMWriter(fileWriter);
            pemWriter.writeObject(publicKey);
            pemWriter.close();
        }
        PrivateKey privateKey = keyGenerator.toPrivateKey();
        try (FileWriter fileWriter = new FileWriter(privateKeyPem)) {
            JcaPEMWriter pemWriter = new JcaPEMWriter(fileWriter);
            pemWriter.writeObject(privateKey);
            pemWriter.close();
        }
    }

}
