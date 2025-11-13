package com.cmsr.onebase.jose;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.UUID;

public class JoseGenerator {
    private static final String PUBLIC_KEY_PEM = "/onebase_pub.pem";

    public static String generateToken(int expireSeconds) throws Exception {

        try (InputStream resourceInputStream = JoseGenerator.class.getResourceAsStream(PUBLIC_KEY_PEM);
             InputStreamReader inputStreamReader = new InputStreamReader(resourceInputStream)
        ) {
            PEMParser pemParser = new PEMParser(inputStreamReader);
            SubjectPublicKeyInfo publicKeyInfo = (SubjectPublicKeyInfo) pemParser.readObject();

            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            RSAPublicKey publicKey = (RSAPublicKey) converter.getPublicKey(publicKeyInfo);

            JWTClaimsSet.Builder claimSetBuilder = new JWTClaimsSet.Builder();
            claimSetBuilder.issuer("onebase");
            claimSetBuilder.subject("onebase");

            claimSetBuilder.expirationTime(new Date(new Date().getTime() + 1000L * expireSeconds));
            claimSetBuilder.notBeforeTime(new Date());
            claimSetBuilder.jwtID(UUID.randomUUID().toString());
            //
            JWEHeader jweHeader = new JWEHeader(JWEAlgorithm.RSA_OAEP_512, EncryptionMethod.A192CBC_HS384);
            EncryptedJWT jwt = new EncryptedJWT(jweHeader, claimSetBuilder.build());

            RSAEncrypter rsaEncrypter = new RSAEncrypter(publicKey);
            jwt.encrypt(rsaEncrypter);

            return jwt.serialize();
        }
    }

    public static void main(String[] args) throws Exception {
        String token = generateToken(30);

        System.out.println("${setValue(accessToken=" + token + ")}");
    }
}
