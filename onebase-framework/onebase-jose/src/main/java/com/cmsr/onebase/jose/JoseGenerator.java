package com.cmsr.onebase.jose;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.IOUtils;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

public class JoseGenerator {

    private static final String PUBLIC_KEY_PEM = "/public-key.json";

    public static String generateToken(int expireSeconds) {
        try (InputStream resourceInputStream = JoseGenerator.class.getResourceAsStream(PUBLIC_KEY_PEM)) {
            String json = IOUtils.readInputStreamToString(resourceInputStream);
            RSAKey rsaJWK = RSAKey.parse(json);
            RSAKey publicKey = rsaJWK.toPublicJWK();

            JWEHeader jweHeader = new JWEHeader(JWEAlgorithm.RSA_OAEP_512, EncryptionMethod.A192CBC_HS384);

            Date now = new Date();
            JWTClaimsSet claimSet = new JWTClaimsSet.Builder()
                    .issuer("OneBase")
                    .subject("OneBase")
                    .expirationTime(new Date(now.getTime() + 1000L * expireSeconds))
                    .notBeforeTime(now)
                    .jwtID(UUID.randomUUID().toString())
                    .build();
            //
            EncryptedJWT jwt = new EncryptedJWT(jweHeader, claimSet);
            RSAEncrypter rsaEncrypter = new RSAEncrypter(publicKey);
            jwt.encrypt(rsaEncrypter);
            return jwt.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        int expireSeconds = 30;
        if (args.length >= 1) {
            expireSeconds = Integer.parseInt(args[0]);
        }
        String token = generateToken(expireSeconds);
        System.out.println("${setValue(accessToken=" + token + ")}");
    }
}
