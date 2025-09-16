package com.cmsr.onebase.framework.common.tools.core.util;

import com.cmsr.onebase.framework.common.tools.core.lang.PatternPool;

/**
 * 电话号码工具类
 */
public class PhoneUtil {

    /**
     * 验证是否为座机号码（中国大陆）
     *
     * @param value 值
     * @return 是否为座机号码（中国大陆）
     * @since 5.3.11
     */
    public static boolean isTel(CharSequence value) {
        return ReUtil.isMatch(PatternPool.TEL, value);
    }

    /**
     * 验证是否为座机号码+手机号码（CharUtil中国）+ 400 + 800电话 + 手机号号码（中国香港）
     *
     * @param value 值
     * @return 是否为座机号码+手机号码（中国大陆）+手机号码（中国香港）+手机号码（中国台湾）+手机号码（中国澳门）
     * @since 5.3.11
     */
    public static boolean isPhone(CharSequence value) {
        return isMobile(value) || isTel400800(value) || isMobileHk(value) || isMobileTw(value) || isMobileMo(value);
    }

    /**
     * 验证是否为手机号码（中国大陆）
     *
     * @param value 值
     * @return 是否为手机号码（中国大陆）
     * @since 5.3.11
     */
    public static boolean isMobile(CharSequence value) {
        return ReUtil.isMatch(PatternPool.MOBILE, value);
    }

    /**
     * 验证是否为座机号码（中国大陆）+ 400 + 800
     *
     * @param value 值
     * @return 是否为座机号码（中国大陆）
     * @since 5.6.3
     * @author dazer, ourslook
     */
    public static boolean isTel400800(CharSequence value) {
        return ReUtil.isMatch(PatternPool.TEL_400_800, value);
    }

    /**
     * 验证是否为手机号码（中国香港）
     * @param value 手机号码
     * @return 是否为中国香港手机号码
     * @since 5.6.3
     * @author dazer, ourslook
     */
    public static boolean isMobileHk(CharSequence value) {
        return ReUtil.isMatch(PatternPool.MOBILE_HK, value);
    }

    /**
     * 验证是否为手机号码（中国台湾）
     * @param value 手机号码
     * @return 是否为中国台湾手机号码
     * @since 5.6.6
     * @author ihao
     */
    public static boolean isMobileTw(CharSequence value) {
        return ReUtil.isMatch(PatternPool.MOBILE_TW, value);
    }

    /**
     * 验证是否为手机号码（中国澳门）
     * @param value 手机号码
     * @return 是否为中国澳门手机号码
     * @since 5.6.6
     * @author ihao
     */
    public static boolean isMobileMo(CharSequence value) {
        return ReUtil.isMatch(PatternPool.MOBILE_MO, value);
    }
}
