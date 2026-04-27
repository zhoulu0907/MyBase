package com.cmsr.onebase.framework.security.build.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AiBridgeCryptoUtilsTest {

    @Test
    void hmacSm3() {
        String secret = "secret";
        String data = "POST|/build/ai/test|1710000000|nonce|deadbeef";
        String sig1 = AiBridgeCryptoUtils.hmacSm3Hex(secret, data);
        String sig2 = AiBridgeCryptoUtils.hmacSm3Hex(secret, data);
        Assertions.assertEquals(sig1, sig2);
        String sig3 = AiBridgeCryptoUtils.hmacSm3Hex(secret, data + "x");
        Assertions.assertNotEquals(sig1, sig3);
    }
}
