package com.cmsr.onebase.module.app.build.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @Author：huangjie
 * @Date：2025/7/24 14:51
 */
public class VersionUtils {

    public static final String INIT_VERSION = "v0.0.1";

    public static final Integer OPERATION_TYPE_PUBLISH = 1;
    public static final Integer OPERATION_TYPE_SAVE = 2;


    /**
     * 递增版本号，比如 v1.0.0 -> v1.0.1 、 v1.0.1 -> v1.0.2
     *
     * @param versionNumber
     * @return
     */
    public static String increaseVersionNumber(String versionNumber) {
        if (StringUtils.isEmpty(versionNumber)) {
            return INIT_VERSION;
        }
        // 去掉前缀"v"
        String versionWithoutPrefix = versionNumber.startsWith("v") ? versionNumber.substring(1) : versionNumber;

        // 按"."分割版本号
        String[] versionParts = versionWithoutPrefix.split("\\.");

        // 确保至少有三个部分
        if (versionParts.length < 3) {
            // 如果不足三位，补齐
            String[] temp = new String[3];
            for (int i = 0; i < 3; i++) {
                if (i < versionParts.length) {
                    temp[i] = versionParts[i];
                } else {
                    temp[i] = "0";
                }
            }
            versionParts = temp;
        }

        // 递增最后一位
        int patchVersion = Integer.parseInt(versionParts[2]);
        versionParts[2] = String.valueOf(patchVersion + 1);

        return "v" + versionParts[0] + "." + versionParts[1] + "." + versionParts[2];
    }
}
