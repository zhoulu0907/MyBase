package com.cmsr.onebase.framework.common.util.string;

import com.github.f4b6a3.uuid.UuidCreator;

public class UuidUtils {
    public static String fetchUuid() {
        return UuidCreator.getTimeOrderedEpoch().toString();
    }
}
