package com.cmsr.onebase.framework.common.tools.core.date;

import com.cmsr.onebase.framework.common.tools.core.date.format.DateParser;
import com.cmsr.onebase.framework.common.tools.core.date.format.DatePrinter;
import com.cmsr.onebase.framework.common.tools.core.date.format.FastDateFormat;
import com.cmsr.onebase.framework.common.tools.core.date.format.GlobalCustomFormat;
import com.cmsr.onebase.framework.common.tools.core.lang.Assert;
import com.cmsr.onebase.framework.common.tools.core.lang.PatternPool;
import com.cmsr.onebase.framework.common.tools.core.util.CharUtil;
import com.cmsr.onebase.framework.common.tools.core.util.NumberUtil;
import com.cmsr.onebase.framework.common.tools.core.util.ReUtil;
import com.cmsr.onebase.framework.common.tools.core.util.StrUtil;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.temporal.TemporalAccessor;
import java.util.*;

/**
 * 日期时间工具类
 *
 * @author xiaoleilu
 * @see LocalDateTimeUtil java8日志工具类
 * @see DatePattern 日期常用格式工具类
 */
public class DateUtil {

    /**
     * java.util.Date EEE MMM zzz 缩写数组
     */
    private final static String[] wtb = { //
            "sun", "mon", "tue", "wed", "thu", "fri", "sat", // 星期
            "jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec", // 月份
            "gmt", "ut", "utc", "est", "edt", "cst", "cdt", "mst", "mdt", "pst", "pdt"// 时间标准
    };

    /**
     * 获取指定日期偏移指定时间后的时间，生成的偏移日期不影响原日期
     *
     * @param date      基准日期
     * @param dateField 偏移的粒度大小（小时、天、月等）{@link DateField}
     * @param offset    偏移量，正数为向后偏移，负数为向前偏移
     * @return 偏移后的日期
     */
    public static DateTime offset(Date date, DateField dateField, int offset) {
        if (date == null) {
            return null;
        }
        return dateNew(date).offset(dateField, offset);
    }

    /**
     * 获得年的部分
     *
     * @param date 日期
     * @return 年的部分
     */
    public static int year(Date date) {
        return DateTime.of(date).year();
    }

    /**
     * @return 今年
     */
    public static int thisYear() {
        return year(date());
    }

    /**
     * 计算相对于dateToCompare的年龄，常用于计算指定生日在某年的年龄
     *
     * @param birthday      生日
     * @param dateToCompare 需要对比的日期
     * @return 年龄
     */
    public static int age(Date birthday, Date dateToCompare) {
        Assert.notNull(birthday, "Birthday can not be null !");
        if (null == dateToCompare) {
            dateToCompare = date();
        }
        return age(birthday.getTime(), dateToCompare.getTime());
    }

    /**
     * 计算相对于dateToCompare的年龄，常用于计算指定生日在某年的年龄<br>
     * 按照《最高人民法院关于审理未成年人刑事案件具体应用法律若干问题的解释》第二条规定刑法第十七条规定的“周岁”，按照公历的年、月、日计算，从周岁生日的第二天起算。
     * <ul>
     *     <li>2022-03-01出生，则相对2023-03-01，周岁为0，相对于2023-03-02才是1岁。</li>
     *     <li>1999-02-28出生，则相对2000-02-29，周岁为1</li>
     * </ul>
     *
     * @param birthday      生日
     * @param dateToCompare 需要对比的日期
     * @return 年龄
     */
    protected static int age(long birthday, long dateToCompare) {
        if (birthday > dateToCompare) {
            throw new IllegalArgumentException("Birthday is after dateToCompare!");
        }

        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dateToCompare);

        final int year = cal.get(Calendar.YEAR);
        final int month = cal.get(Calendar.MONTH);
        final int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

        // 复用cal
        cal.setTimeInMillis(birthday);
        int age = year - cal.get(Calendar.YEAR);

        //当前日期，则为0岁
        if (age == 0) {
            return 0;
        }

        final int monthBirth = cal.get(Calendar.MONTH);
        if (month == monthBirth) {
            final int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
            // issue#I6E6ZG，法定生日当天不算年龄，从第二天开始计算
            if (dayOfMonth <= dayOfMonthBirth) {
                // 如果生日在当月，但是未达到生日当天的日期，年龄减一
                age--;
            }
        } else if (month < monthBirth) {
            // 如果当前月份未达到生日的月份，年龄计算减一
            age--;
        }

        return age;
    }

    /**
     * 根据特定格式格式化日期
     *
     * @param date   被格式化的日期
     * @param format 日期格式，常用格式见： {@link DatePattern} {@link DatePattern#NORM_DATETIME_PATTERN}
     * @return 格式化后的字符串
     */
    public static String format(Date date, String format) {
        if (null == date || StrUtil.isBlank(format)) {
            return null;
        }

        // 检查自定义格式
        if (GlobalCustomFormat.isCustomFormat(format)) {
            return GlobalCustomFormat.format(date, format);
        }

        TimeZone timeZone = null;
        if (date instanceof DateTime) {
            timeZone = ((DateTime) date).getTimeZone();
        }
        return format(date, FastDateFormat.getInstance(format, timeZone));
    }

    /**
     * 根据特定格式格式化日期
     *
     * @param date   被格式化的日期
     * @param format {@link DatePrinter} 或 {@link FastDateFormat} {@link DatePattern#NORM_DATETIME_FORMAT}
     * @return 格式化后的字符串
     */
    public static String format(Date date, DatePrinter format) {
        if (null == format || null == date) {
            return null;
        }
        return format.format(date);
    }

    /**
     * 格式化日期间隔输出
     *
     * @param betweenMs 日期间隔
     * @param level     级别，按照天、小时、分、秒、毫秒分为5个等级
     * @return XX天XX小时XX分XX秒XX毫秒
     */
    public static String formatBetween(long betweenMs, BetweenFormatter.Level level) {
        return new BetweenFormatter(betweenMs, level).format();
    }

    /**
     * Date对象转换为{@link Instant}对象
     *
     * @param temporalAccessor Date对象
     * @return {@link Instant}对象
     * @since 5.0.2
     */
    public static Instant toInstant(TemporalAccessor temporalAccessor) {
        return TemporalAccessorUtil.toInstant(temporalAccessor);
    }

    /**
     * 转换为Calendar对象
     *
     * @param date 日期对象
     * @return Calendar对象
     */
    public static Calendar calendar(Date date) {
        if (date instanceof DateTime) {
            return ((DateTime) date).toCalendar();
        } else {
            return calendar(date.getTime());
        }
    }

    /**
     * 转换为Calendar对象，使用当前默认时区
     *
     * @param millis 时间戳
     * @return Calendar对象
     */
    public static Calendar calendar(long millis) {
        return calendar(millis, TimeZone.getDefault());
    }

    /**
     * 转换为Calendar对象
     *
     * @param millis   时间戳
     * @param timeZone 时区
     * @return Calendar对象
     * @since 5.7.22
     */
    public static Calendar calendar(long millis, TimeZone timeZone) {
        final Calendar cal = Calendar.getInstance(timeZone);
        cal.setTimeInMillis(millis);
        return cal;
    }

    /**
     * 是否闰年
     *
     * @param year 年
     * @return 是否闰年
     */
    public static boolean isLeapYear(int year) {
        return Year.isLeap(year);
    }

    /**
     * 当前日期，格式 yyyy-MM-dd
     *
     * @return 当前日期的标准形式字符串
     */
    public static String today() {
        return formatDate(new DateTime());
    }

    /**
     * 格式化日期部分（不包括时间）<br>
     * 格式 yyyy-MM-dd
     *
     * @param date 被格式化的日期
     * @return 格式化后的字符串
     */
    public static String formatDate(Date date) {
        if (null == date) {
            return null;
        }
        return DatePattern.NORM_DATE_FORMAT.format(date);
    }
    
    /**
     * 将特定格式的日期转换为Date对象
     *
     * @param dateStr 特定格式的日期
     * @param format  格式，例如yyyy-MM-dd
     * @return 日期对象
     */
    public static DateTime parse(CharSequence dateStr, String format) {
        return new DateTime(dateStr, format);
    }

    /**
     * 构建DateTime对象
     *
     * @param dateStr Date字符串
     * @param parser  格式化器,{@link FastDateFormat}
     * @return DateTime对象
     */
    public static DateTime parse(CharSequence dateStr, DateParser parser) {
        return new DateTime(dateStr, parser);
    }

    /**
     * 当前时间，转换为{@link DateTime}对象
     *
     * @return 当前时间
     */
    public static DateTime date() {
        return new DateTime();
    }

    /**
     * Long类型时间转为{@link DateTime}<br>
     * 只支持毫秒级别时间戳，如果需要秒级别时间戳，请自行×1000
     *
     * @param date Long类型Date（Unix时间戳）
     * @return 时间对象
     */
    public static DateTime date(long date) {
        return new DateTime(date);
    }

    /**
     * {@link Date}类型时间转为{@link DateTime}<br>
     * 如果date本身为DateTime对象，则返回强转后的对象，否则新建一个DateTime对象
     *
     * @param date Long类型Date（Unix时间戳），如果传入{@code null}，返回{@code null}
     * @return 时间对象
     * @since 3.0.7
     */
    public static DateTime date(Date date) {
        if (date == null) {
            return null;
        }
        if (date instanceof DateTime) {
            return (DateTime) date;
        }
        return dateNew(date);
    }

    /**
     * 根据已有{@link Date} 产生新的{@link DateTime}对象
     *
     * @param date Date对象，如果传入{@code null}，返回{@code null}
     * @return {@link DateTime}对象
     * @since 4.3.1
     */
    public static DateTime dateNew(Date date) {
        if (date == null) {
            return null;
        }
        return new DateTime(date);
    }

    /**
     * {@link TemporalAccessor}类型时间转为{@link DateTime}<br>
     * 始终根据已有{@link TemporalAccessor} 产生新的{@link DateTime}对象
     *
     * @param temporalAccessor {@link TemporalAccessor},常用子类： {@link LocalDateTime}、 LocalDate，如果传入{@code null}，返回{@code null}
     * @return 时间对象
     * @since 5.0.0
     */
    public static DateTime date(TemporalAccessor temporalAccessor) {
        if (temporalAccessor == null) {
            return null;
        }
        return new DateTime(temporalAccessor);
    }

    /**
     * {@link Calendar}类型时间转为{@link DateTime}<br>
     * 始终根据已有{@link Calendar} 产生新的{@link DateTime}对象
     *
     * @param calendar {@link Calendar}，如果传入{@code null}，返回{@code null}
     * @return 时间对象
     */
    public static DateTime date(Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        return new DateTime(calendar);
    }

    /**
     * 创建{@link SimpleDateFormat}，注意此对象非线程安全！<br>
     * 此对象默认为严格格式模式，即parse时如果格式不正确会报错。
     *
     * @param pattern 表达式
     * @return {@link SimpleDateFormat}
     * @since 5.5.5
     */
    public static SimpleDateFormat newSimpleFormat(String pattern) {
        return newSimpleFormat(pattern, null, null);
    }

    /**
     * 创建{@link SimpleDateFormat}，注意此对象非线程安全！<br>
     * 此对象默认为严格格式模式，即parse时如果格式不正确会报错。
     *
     * @param pattern  表达式
     * @param locale   {@link Locale}，{@code null}表示默认
     * @param timeZone {@link TimeZone}，{@code null}表示默认
     * @return {@link SimpleDateFormat}
     * @since 5.5.5
     */
    public static SimpleDateFormat newSimpleFormat(String pattern, Locale locale, TimeZone timeZone) {
        if (null == locale) {
            locale = Locale.getDefault(Locale.Category.FORMAT);
        }
        final SimpleDateFormat format = new SimpleDateFormat(pattern, locale);
        if (null != timeZone) {
            format.setTimeZone(timeZone);
        }
        format.setLenient(false);
        return format;
    }

    /**
     * 将日期字符串转换为{@link DateTime}对象，格式：<br>
     * <ol>
     * <li>yyyy-MM-dd HH:mm:ss</li>
     * <li>yyyy/MM/dd HH:mm:ss</li>
     * <li>yyyy.MM.dd HH:mm:ss</li>
     * <li>yyyy年MM月dd日 HH时mm分ss秒</li>
     * <li>yyyy-MM-dd</li>
     * <li>yyyy/MM/dd</li>
     * <li>yyyy.MM.dd</li>
     * <li>HH:mm:ss</li>
     * <li>HH时mm分ss秒</li>
     * <li>yyyy-MM-dd HH:mm</li>
     * <li>yyyy-MM-dd HH:mm:ss.SSS</li>
     * <li>yyyy-MM-dd HH:mm:ss.SSSSSS</li>
     * <li>yyyyMMddHHmmss</li>
     * <li>yyyyMMddHHmmssSSS</li>
     * <li>yyyyMMdd</li>
     * <li>EEE, dd MMM yyyy HH:mm:ss z</li>
     * <li>EEE MMM dd HH:mm:ss zzz yyyy</li>
     * <li>yyyy-MM-dd'T'HH:mm:ss'Z'</li>
     * <li>yyyy-MM-dd'T'HH:mm:ss.SSS'Z'</li>
     * <li>yyyy-MM-dd'T'HH:mm:ssZ</li>
     * <li>yyyy-MM-dd'T'HH:mm:ss.SSSZ</li>
     * </ol>
     *
     * @param dateCharSequence 日期字符串
     * @return 日期
     */
    public static DateTime parse(CharSequence dateCharSequence) {
        if (StrUtil.isBlank(dateCharSequence)) {
            return null;
        }
        String dateStr = dateCharSequence.toString();
        // 去掉两边空格并去掉中文日期中的“日”和“秒”，以规范长度
        dateStr = StrUtil.removeAll(dateStr.trim(), '日', '秒');
        int length = dateStr.length();

        if (NumberUtil.isNumber(dateStr)) {
            // 纯数字形式
            if (length == DatePattern.PURE_DATETIME_PATTERN.length()) {
                return parse(dateStr, DatePattern.PURE_DATETIME_FORMAT);
            } else if (length == DatePattern.PURE_DATETIME_MS_PATTERN.length()) {
                return parse(dateStr, DatePattern.PURE_DATETIME_MS_FORMAT);
            } else if (length == DatePattern.PURE_DATE_PATTERN.length()) {
                return parse(dateStr, DatePattern.PURE_DATE_FORMAT);
            } else if (length == DatePattern.PURE_TIME_PATTERN.length()) {
                return parse(dateStr, DatePattern.PURE_TIME_FORMAT);
            }else if(length == 13){
                // 时间戳
                return date(NumberUtil.parseLong(dateStr));
            }
        } else if (ReUtil.isMatch(PatternPool.TIME, dateStr)) {
            // HH:mm:ss 或者 HH:mm 时间格式匹配单独解析
            return parseTimeToday(dateStr);
        } else if (StrUtil.containsAnyIgnoreCase(dateStr, wtb)) {
            // JDK的Date对象toString默认格式，类似于：
            // Tue Jun 4 16:25:15 +0800 2019
            // Thu May 16 17:57:18 GMT+08:00 2019
            // Wed Aug 01 00:00:00 CST 2012
            return parseRFC2822(dateStr);
        } else if (StrUtil.contains(dateStr, 'T')) {
            // ISO8601时间
            return parseISO8601(dateStr);
        }

        //标准日期格式（包括单个数字的日期时间）
        dateStr = normalize(dateStr);
        if (ReUtil.isMatch(DatePattern.REGEX_NORM, dateStr)) {
            final int colonCount = StrUtil.count(dateStr, CharUtil.COLON);
            switch (colonCount) {
                case 0:
                    // yyyy-MM-dd
                    return parse(dateStr, DatePattern.NORM_DATE_FORMAT);
                case 1:
                    // yyyy-MM-dd HH:mm
                    return parse(dateStr, DatePattern.NORM_DATETIME_MINUTE_FORMAT);
                case 2:
                    final int indexOfDot = StrUtil.indexOf(dateStr, CharUtil.DOT);
                    if (indexOfDot > 0) {
                        final int length1 = dateStr.length();
                        // yyyy-MM-dd HH:mm:ss.SSS 或者 yyyy-MM-dd HH:mm:ss.SSSSSS
                        if (length1 - indexOfDot > 4) {
                            // 类似yyyy-MM-dd HH:mm:ss.SSSSSS，采取截断操作
                            dateStr = StrUtil.subPre(dateStr, indexOfDot + 4);
                        }
                        return parse(dateStr, DatePattern.NORM_DATETIME_MS_FORMAT);
                    }
                    // yyyy-MM-dd HH:mm:ss
                    return parse(dateStr, DatePattern.NORM_DATETIME_FORMAT);
            }
        }

        // 没有更多匹配的时间格式
        throw new DateException("No format fit for date String [{}] !", dateStr);
    }

    /**
     * 标准化日期，默认处理以空格区分的日期时间格式，空格前为日期，空格后为时间：<br>
     * 将以下字符替换为"-"
     *
     * <pre>
     * "."
     * "/"
     * "年"
     * "月"
     * </pre>
     * <p>
     * 将以下字符去除
     *
     * <pre>
     * "日"
     * </pre>
     * <p>
     * 将以下字符替换为":"
     *
     * <pre>
     * "时"
     * "分"
     * "秒"
     * </pre>
     * <p>
     * 当末位是":"时去除之（不存在毫秒时）
     *
     * @param dateStr 日期时间字符串
     * @return 格式化后的日期字符串
     */
    private static String normalize(CharSequence dateStr) {
        if (StrUtil.isBlank(dateStr)) {
            return StrUtil.str(dateStr);
        }

        // 日期时间分开处理
        final List<String> dateAndTime = StrUtil.splitTrim(dateStr, ' ');
        final int size = dateAndTime.size();
        if (size < 1 || size > 2) {
            // 非可被标准处理的格式
            return StrUtil.str(dateStr);
        }

        final StringBuilder builder = StrUtil.builder();

        // 日期部分（"\"、"/"、"."、"年"、"月"都替换为"-"）
        String datePart = dateAndTime.get(0).replaceAll("[/.年月]", "-");
        datePart = StrUtil.removeSuffix(datePart, "日");
        builder.append(datePart);

        // 时间部分
        if (size == 2) {
            builder.append(' ');
            String timePart = dateAndTime.get(1).replaceAll("[时分秒]", ":");
            timePart = StrUtil.removeSuffix(timePart, ":");
            //将ISO8601中的逗号替换为.
            timePart = timePart.replace(',', '.');
            builder.append(timePart);
        }

        return builder.toString();
    }

    /**
     * 解析时间，格式HH:mm 或 HH:mm:ss，日期默认为今天
     *
     * @param timeString 标准形式的日期字符串
     * @return 日期对象
     * @since 3.1.1
     */
    public static DateTime parseTimeToday(CharSequence timeString) {
        // issue#I9C2D4 处理时分秒
        timeString = StrUtil.replaceChars(timeString, "时分秒", ":");

        timeString = StrUtil.format("{} {}", today(), timeString);
        if (1 == StrUtil.count(timeString, ':')) {
            // 时间格式为 HH:mm
            return parse(timeString, DatePattern.NORM_DATETIME_MINUTE_PATTERN);
        } else {
            // 时间格式为 HH:mm:ss
            return parse(timeString, DatePattern.NORM_DATETIME_FORMAT);
        }
    }

    /**
     * 解析RFC2822时间，格式：<br>
     * <ol>
     * <li>EEE MMM dd HH:mm:ss z yyyy（例如：Wed Aug 01 00:00:00 CST 2012）</li>
     * </ol>
     *
     * @param source RFC2822时间
     * @return 日期对象
     * @since 4.6.9
     */
    public static DateTime parseRFC2822(CharSequence source) {
        if (source == null) {
            return null;
        }

        // issue#I9C2D4
        if(StrUtil.contains(source, ',')){
            if(StrUtil.contains(source, "星期")){
                return parse(source, FastDateFormat.getInstance(DatePattern.HTTP_DATETIME_PATTERN, Locale.CHINA));
            }
            return parse(source, DatePattern.HTTP_DATETIME_FORMAT_Z);
        }

        if(StrUtil.contains(source, "星期")){
            return parse(source, FastDateFormat.getInstance(DatePattern.JDK_DATETIME_PATTERN, Locale.CHINA));
        }
        return parse(source, DatePattern.JDK_DATETIME_FORMAT);
    }

    /**
     * 解析ISO8601时间，格式：<br>
     * <ol>
     * <li>yyyy-MM-dd'T'HH:mm:ss'Z'</li>
     * <li>yyyy-MM-dd'T'HH:mm:ss.SSS'Z'</li>
     * <li>yyyy-MM-dd'T'HH:mm:ssZ</li>
     * <li>yyyy-MM-dd'T'HH:mm:ss.SSSZ</li>
     * <li>yyyy-MM-dd'T'HH:mm:ss+0800</li>
     * <li>yyyy-MM-dd'T'HH:mm:ss+08:00</li>
     * </ol>
     *
     * @param iso8601String ISO8601时间
     * @return 日期对象
     * @since 5.8.34
     */
    public static DateTime parseISO8601(String iso8601String) {
        if (iso8601String == null) {
            return null;
        }
        final int length = iso8601String.length();
        if (StrUtil.contains(iso8601String, 'Z')) {
            if (length == DatePattern.UTC_PATTERN.length() - 4) {
                // 格式类似：2018-09-13T05:34:31Z，-4表示减去4个单引号的长度
                return parse(iso8601String, DatePattern.UTC_FORMAT);
            }

            final int patternLength = DatePattern.UTC_MS_PATTERN.length();
            // 格式类似：2018-09-13T05:34:31.999Z，-4表示减去4个单引号的长度
            // -4 ~ -6范围表示匹配毫秒1~3位的情况
            if (length <= patternLength && length >= patternLength - 6) {
                // issue#I7H34N，支持最多6位毫秒
                return parse(iso8601String, DatePattern.UTC_MS_FORMAT);
            }
        } else if (StrUtil.contains(iso8601String, '+')) {
            // 去除类似2019-06-01T19:45:43 +08:00加号前的空格
            iso8601String = iso8601String.replace(" +", "+");
            final String zoneOffset = StrUtil.subAfter(iso8601String, '+', true);
            if (StrUtil.isBlank(zoneOffset)) {
                throw new DateException("Invalid format: [{}]", iso8601String);
            }
            if (false == StrUtil.contains(zoneOffset, ':')) {
                // +0800转换为+08:00
                final String pre = StrUtil.subBefore(iso8601String, '+', true);
                iso8601String = pre + "+" + zoneOffset.substring(0, 2) + ":" + "00";
            }

            if (StrUtil.contains(iso8601String, CharUtil.DOT)) {
                // 带毫秒，格式类似：2018-09-13T05:34:31.999+08:00
                iso8601String = normalizeMillSeconds(iso8601String, ".", "+");
                return parse(iso8601String, DatePattern.UTC_MS_WITH_XXX_OFFSET_FORMAT);
            } else {
                // 格式类似：2018-09-13T05:34:31+08:00
                return parse(iso8601String, DatePattern.UTC_WITH_XXX_OFFSET_FORMAT);
            }
        } else if(ReUtil.contains("-\\d{2}:?00", iso8601String)){
            // Issue#2612，类似 2022-09-14T23:59:00-08:00 或者 2022-09-14T23:59:00-0800

            // 去除类似2019-06-01T19:45:43 -08:00加号前的空格
            iso8601String = iso8601String.replace(" -", "-");
            if(':' != iso8601String.charAt(iso8601String.length() - 3)){
                iso8601String = iso8601String.substring(0, iso8601String.length() - 2) + ":00";
            }

            if (StrUtil.contains(iso8601String, CharUtil.DOT)) {
                // 带毫秒，格式类似：2018-09-13T05:34:31.999-08:00
                iso8601String = normalizeMillSeconds(iso8601String, ".", "-");
                return new DateTime(iso8601String, DatePattern.UTC_MS_WITH_XXX_OFFSET_FORMAT);
            } else {
                // 格式类似：2018-09-13T05:34:31-08:00
                return new DateTime(iso8601String, DatePattern.UTC_WITH_XXX_OFFSET_FORMAT);
            }
        } else {
            if (length == DatePattern.UTC_SIMPLE_PATTERN.length() - 2) {
                // 格式类似：2018-09-13T05:34:31
                return parse(iso8601String, DatePattern.UTC_SIMPLE_FORMAT);
            } else if (length == DatePattern.UTC_SIMPLE_PATTERN.length() - 5) {
                // 格式类似：2018-09-13T05:34
                return parse(iso8601String + ":00", DatePattern.UTC_SIMPLE_FORMAT);
            } else if (StrUtil.contains(iso8601String, CharUtil.DOT)) {
                // 可能为：  2021-03-17T06:31:33.99
                iso8601String = normalizeMillSeconds(iso8601String, ".", null);
                return parse(iso8601String, DatePattern.UTC_SIMPLE_MS_FORMAT);
            }
        }
        // 没有更多匹配的时间格式
        throw new DateException("No format fit for date String [{}] !", iso8601String);
    }

    /**
     * 如果日期中的毫秒部分超出3位，会导致秒数增加，因此只保留前三位
     *
     * @param dateStr 日期字符串
     * @param before  毫秒部分的前一个字符
     * @param after   毫秒部分的后一个字符
     * @return 规范之后的毫秒部分
     */
    private static String normalizeMillSeconds(String dateStr, CharSequence before, CharSequence after) {
        if (StrUtil.isBlank(after)) {
            String millOrNaco = StrUtil.subPre(StrUtil.subAfter(dateStr, before, true), 3);
            return StrUtil.subBefore(dateStr, before, true) + before + millOrNaco;
        }
        String millOrNaco = StrUtil.subPre(StrUtil.subBetween(dateStr, before, after), 3);
        return StrUtil.subBefore(dateStr, before, true)
                + before
                + millOrNaco + after + StrUtil.subAfter(dateStr, after, true);
    }
    
}