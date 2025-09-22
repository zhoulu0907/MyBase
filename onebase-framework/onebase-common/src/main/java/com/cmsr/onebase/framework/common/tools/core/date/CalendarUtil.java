package com.cmsr.onebase.framework.common.tools.core.date;

import com.cmsr.onebase.framework.common.tools.core.date.format.DateParser;
import com.cmsr.onebase.framework.common.tools.core.util.StrUtil;

import java.text.ParsePosition;
import java.util.Calendar;

/**
 * 针对{@link Calendar} 对象封装工具类
 *
 * @author looly
 * @since 5.3.0
 */
public class CalendarUtil {

    /**
     * 是否为本月最后一天
     *
     * @param calendar {@link Calendar}
     * @return 是否为本月最后一天
     * @since 5.8.27
     */
    public static boolean isLastDayOfMonth(Calendar calendar) {
        return calendar.get(Calendar.DAY_OF_MONTH) == calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 使用指定{@link DateParser}解析字符串为{@link Calendar}
     *
     * @param str     日期字符串
     * @param lenient 是否宽容模式
     * @param parser  {@link DateParser}
     * @return 解析后的 {@link Calendar}，解析失败返回{@code null}
     * @since 5.7.14
     */
    public static Calendar parse(CharSequence str, boolean lenient, DateParser parser) {
        final Calendar calendar = Calendar.getInstance(parser.getTimeZone(), parser.getLocale());
        calendar.clear();
        calendar.setLenient(lenient);

        return parser.parse(StrUtil.str(str), new ParsePosition(0), calendar) ? calendar : null;
    }
}