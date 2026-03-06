package com.cmsr.onebase.framework.security.build.util;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.nio.charset.StandardCharsets;
import java.security.Security;

public class AiBridgeCryptoUtils {

    private AiBridgeCryptoUtils() {
    }

    public static String sm3Hex(String data) {
        SM3Digest sm3 = new SM3Digest();
        byte[] in = StringUtils.defaultString(data).getBytes(StandardCharsets.UTF_8);
        sm3.update(in, 0, in.length);
        byte[] out = new byte[sm3.getDigestSize()];
        sm3.doFinal(out, 0);
        return Hex.encodeHexString(out);
    }

    public static String hmacSha256Hex(String secret, String data) {
        if (StringUtils.isBlank(secret)) {
            return "";
        }
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_256, secret).hmacHex(data);
    }

    public static String hmacSm3Hex(String secret, String data) {
        if (StringUtils.isBlank(secret)) {
            return "";
        }
        HMac hmac = new HMac(new SM3Digest());
        byte[] key = secret.getBytes(StandardCharsets.UTF_8);
        hmac.init(new KeyParameter(key));
        byte[] msg = data.getBytes(StandardCharsets.UTF_8);
        hmac.update(msg, 0, msg.length);
        byte[] out = new byte[hmac.getMacSize()];
        hmac.doFinal(out, 0);
        return Hex.encodeHexString(out);
    }

    public static void ensureBc() {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    public record EncResult(String cipherBase64, String ivBase64) {
    }
}
