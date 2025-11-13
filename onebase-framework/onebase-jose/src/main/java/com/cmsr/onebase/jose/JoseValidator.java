package com.cmsr.onebase.jose;

import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.IOUtils;
import com.nimbusds.jwt.EncryptedJWT;

import java.io.InputStream;
import java.util.Date;

public class JoseValidator {
    private static final String PRIVATE_KEY_PEM = "/private-key.json";

    public static boolean validate(String token) throws Exception {
        try (InputStream resourceStream = JoseValidator.class.getResourceAsStream(PRIVATE_KEY_PEM)) {
            String json = IOUtils.readInputStreamToString(resourceStream);
            RSAKey rsaKey = RSAKey.parse(json);
            EncryptedJWT jwt = EncryptedJWT.parse(token);

            RSADecrypter decrypter = new RSADecrypter(rsaKey.toPrivateKey());
            jwt.decrypt(decrypter);

            Date expirationTime = jwt.getJWTClaimsSet().getExpirationTime();
            Date now = new Date();
            if (expirationTime.before(now)) {
                throw new IllegalStateException("Token expired");
            }
            return true;
        }
    }

}
