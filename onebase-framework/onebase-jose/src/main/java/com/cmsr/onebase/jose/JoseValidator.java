package com.cmsr.onebase.jose;

import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jwt.EncryptedJWT;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.PrivateKey;
import java.util.Date;

public class JoseValidator {
    private static final String PRIVATE_KEY_PEM = "/onebase_pri.pem";

    public static boolean validate(String token) throws Exception {
        Date now = new Date();

        EncryptedJWT jwt = EncryptedJWT.parse(token);

        try (InputStream resourceStream = JoseValidator.class.getResourceAsStream(PRIVATE_KEY_PEM);
             InputStreamReader resourceInputStreamReader = new InputStreamReader(resourceStream)
        ) {
            PEMParser pemParser = new PEMParser(resourceInputStreamReader);
            PEMKeyPair pemKeyPair = (PEMKeyPair) pemParser.readObject();
            PrivateKeyInfo privateKeyInfo = pemKeyPair.getPrivateKeyInfo();

            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            PrivateKey privateKey = converter.getPrivateKey(privateKeyInfo);
            RSADecrypter decrypter = new RSADecrypter(privateKey);
            jwt.decrypt(decrypter);

            Date expirationTime = jwt.getJWTClaimsSet().getExpirationTime();
            if (expirationTime.before(now)) {
                return false;
            }
            return true;
        }
    }

}
