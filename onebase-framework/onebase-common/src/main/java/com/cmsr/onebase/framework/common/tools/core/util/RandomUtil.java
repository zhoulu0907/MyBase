package com.cmsr.onebase.framework.common.tools.core.util;

import com.cmsr.onebase.framework.common.tools.core.date.DateField;
import com.cmsr.onebase.framework.common.tools.core.date.DateTime;
import com.cmsr.onebase.framework.common.tools.core.date.DateUtil;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机工具类
 *
 */
public class RandomUtil {

    /**
     * 用于随机选的数字
     */
    public static final String BASE_NUMBER = "0123456789";
    /**
     * 用于随机选的字符
     */
    public static final String BASE_CHAR = "abcdefghijklmnopqrstuvwxyz";
    /**
     * 用于随机选的字符和数字（小写）
     */
    public static final String BASE_CHAR_NUMBER_LOWER = BASE_CHAR + BASE_NUMBER;
    /**
     * 用于随机选的字符和数字（包括大写和小写字母）
     */
    public static final String BASE_CHAR_NUMBER = BASE_CHAR.toUpperCase() + BASE_CHAR_NUMBER_LOWER;

    /**
     * 随机获得数组中的元素
     *
     * @param <T>   元素类型
     * @param array 列表
     * @return 随机元素
     * @since 3.3.0
     */
    public static <T> T randomEle(final T[] array) {
        return randomEle(array, array.length);
    }

    /**
     * 随机获得数组中的元素
     *
     * @param <T>   元素类型
     * @param array 列表
     * @param limit 限制列表的前N项
     * @return 随机元素
     * @since 3.3.0
     */
    public static <T> T randomEle(final T[] array, int limit) {
        if (array.length < limit) {
            limit = array.length;
        }
        return array[randomInt(limit)];
    }

    /**
     * 以当天为基准，随机产生一个日期
     *
     * @param min 偏移最小天，可以为负数表示过去的时间（包含）
     * @param max 偏移最大天，可以为负数表示过去的时间（不包含）
     * @return 随机日期（随机天，其它时间不变）
     * @since 4.0.8
     */
    public static DateTime randomDay(final int min, final int max) {
        return randomDate(DateUtil.date(), DateField.DAY_OF_YEAR, min, max);
    }

    /**
     * 以给定日期为基准，随机产生一个日期
     *
     * @param baseDate  基准日期
     * @param dateField 偏移的时间字段，例如时、分、秒等
     * @param min       偏移最小量，可以为负数表示过去的时间（包含）
     * @param max       偏移最大量，可以为负数表示过去的时间（不包含）
     * @return 随机日期
     * @since 4.5.8
     */
    public static DateTime randomDate(Date baseDate, final DateField dateField, final int min, final int max) {
        if (null == baseDate) {
            baseDate = DateUtil.date();
        }

        return DateUtil.offset(baseDate, dateField, randomInt(min, max));
    }

    /**
     * 获得指定范围内的随机数 [0,limit)
     *
     * @param limitExclude 限制随机数的范围，不包括这个数
     * @return 随机数
     * @see Random#nextInt(int)
     */
    public static int randomInt(final int limitExclude) {
        return getRandom().nextInt(limitExclude);
    }

    /**
     * 获得指定范围内的随机数
     *
     * @param minInclude 最小数（包含）
     * @param maxExclude 最大数（不包含）
     * @return 随机数
     */
    public static int randomInt(final int minInclude, final int maxExclude) {
        return randomInt(minInclude, maxExclude, true, false);
    }

    /**
     * 获得指定范围内的随机数
     *
     * @param min        最小数
     * @param max        最大数
     * @param includeMin 是否包含最小值
     * @param includeMax 是否包含最大值
     * @return 随机数
     */
    public static int randomInt(int min, int max, final boolean includeMin, final boolean includeMax) {
        if (!includeMin) {
            min++;
        }
        if (includeMax) {
            max++;
        }
        return getRandom().nextInt(min, max);
    }

    /**
     * 获得随机数int值
     *
     * @return 随机数
     * @see Random#nextInt()
     */
    public static int randomInt() {
        return getRandom().nextInt();
    }

    /**
     * 获得随机Boolean值
     *
     * @return true or false
     * @since 4.5.9
     */
    public static boolean randomBoolean() {
        return 0 == randomInt(2);
    }

    /**
     * 获得一个随机的字符串（只包含数字和字符）
     *
     * @param length 字符串的长度
     * @return 随机字符串
     */
    public static String randomString(final int length) {
        return randomString(BASE_CHAR_NUMBER, length);
    }

    /**
     * 获得一个随机的字符串
     *
     * @param baseString 随机字符选取的样本
     * @param length     字符串的长度
     * @return 随机字符串
     */
    public static String randomString(final String baseString, int length) {
        if (StrUtil.isEmpty(baseString)) {
            return StrUtil.EMPTY;
        }
        if (length < 1) {
            length = 1;
        }

        final StringBuilder sb = new StringBuilder(length);
        final int baseLength = baseString.length();
        for (int i = 0; i < length; i++) {
            final int number = randomInt(baseLength);
            sb.append(baseString.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 获得指定范围内的随机数[min, max)
     *
     * @param minInclude 最小数（包含）
     * @param maxExclude 最大数（不包含）
     * @return 随机数
     * @see ThreadLocalRandom#nextLong(long, long)
     * @since 3.3.0
     */
    public static long randomLong(final long minInclude, final long maxExclude) {
        return randomLong(minInclude, maxExclude, true, false);
    }

    /**
     * 获得指定范围内的随机数
     *
     * @param min        最小数
     * @param max        最大数
     * @param includeMin 是否包含最小值
     * @param includeMax 是否包含最大值
     * @return 随机数
     */
    public static long randomLong(long min, long max, final boolean includeMin, final boolean includeMax) {
        if (!includeMin) {
            min++;
        }
        if (includeMax) {
            max++;
        }
        return getRandom().nextLong(min, max);
    }

    /**
     * 获得一个只包含数字的字符串
     *
     * @param length 字符串的长度
     * @return 随机字符串
     */
    public static String randomNumbers(final int length) {
        return randomString(BASE_NUMBER, length);
    }

    /**
     * 获取随机数生成器对象<br>
     * ThreadLocalRandom是JDK 7之后提供并发产生随机数，能够解决多个线程发生的竞争争夺。
     *
     * <p>
     * 注意：此方法返回的{@link ThreadLocalRandom}不可以在多线程环境下共享对象，否则有重复随机数问题。
     * 见：https://www.jianshu.com/p/89dfe990295c
     * </p>
     *
     * @return {@link ThreadLocalRandom}
     * @since 3.1.2
     */
    public static ThreadLocalRandom getRandom() {
        return ThreadLocalRandom.current();
    }

    /**
     * 获取SHA1PRNG的{@link SecureRandom}，类提供加密的强随机数生成器 (RNG)<br>
     * 注意：此方法获取的是伪随机序列发生器PRNG（pseudo-random number generator）
     *
     * <p>
     * 相关说明见：https://stackoverflow.com/questions/137212/how-to-solve-slow-java-securerandom
     *
     * @return {@link SecureRandom}
     * @since 3.1.2
     */
    public static SecureRandom getSecureRandom() {
        return getSecureRandom(null);
    }

    /**
     * 获取SHA1PRNG的{@link SecureRandom}，类提供加密的强随机数生成器 (RNG)<br>
     * 注意：此方法获取的是伪随机序列发生器PRNG（pseudo-random number generator）
     *
     * <p>
     * 相关说明见：https://stackoverflow.com/questions/137212/how-to-solve-slow-java-securerandom
     *
     * @param seed 随机数种子
     * @return {@link SecureRandom}
     * @see #createSecureRandom(byte[])
     * @since 5.5.2
     */
    public static SecureRandom getSecureRandom(final byte[] seed) {
        return createSecureRandom(seed);
    }

    /**
     * 创建{@link SecureRandom}，类提供加密的强随机数生成器 (RNG)<br>
     *
     * @param seed 自定义随机种子
     * @return {@link SecureRandom}
     * @since 4.6.5
     */
    public static SecureRandom createSecureRandom(final byte[] seed) {
        return (null == seed) ? new SecureRandom() : new SecureRandom(seed);
    }


}
