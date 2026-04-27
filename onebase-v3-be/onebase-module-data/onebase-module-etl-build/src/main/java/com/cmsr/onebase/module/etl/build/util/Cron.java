package com.cmsr.onebase.module.etl.build.util;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author：huangjie
 * @Date：2025/9/12 15:07
 */
public class Cron {

    /**
     * 秒
     */
    private String second = "*";

    /**
     * 分
     */
    private String minute = "*";

    /**
     * 时
     */
    private String hour = "*";

    /**
     * 日（day-of-month）
     */
    private String day = "*";

    /**
     * 月
     */
    private String month = "*";

    /**
     * 星期（day-of-week）
     */
    private String week = "?";

    /**
     * 年
     */
    private String year = "*";

    /**
     * 生成 Quartz Cron 表达式：秒 分 时 日 月 周 年
     * 保证 day-of-month 与 day-of-week 互斥（其中一个必须为 '?'）。
     */
    public String toCron() {
        String sec = StringUtils.defaultIfBlank(StringUtils.trim(second), "*");
        String min = StringUtils.defaultIfBlank(StringUtils.trim(minute), "*");
        String hr = StringUtils.defaultIfBlank(StringUtils.trim(hour), "*");
        String d = StringUtils.defaultIfBlank(StringUtils.trim(day), "*");
        String mon = StringUtils.defaultIfBlank(StringUtils.trim(month), "*");
        String w = StringUtils.defaultIfBlank(StringUtils.trim(week), "?");
        String y = StringUtils.defaultIfBlank(StringUtils.trim(year), "*");

        // day-of-month 与 day-of-week 互斥
        boolean daySpecified = !(StringUtils.equalsAny(d, "*", "?"));
        boolean weekSpecified = !(StringUtils.equalsAny(w, "?", "*"));

        if (daySpecified && weekSpecified) {
            // 两者都指定时，以“按日”为主，周置为'?'
            w = "?";
        } else if (daySpecified) {
            w = "?";
        } else if (weekSpecified) {
            d = "?";
        } else {
            d = "*";
            w = "?";
        }

        return sec + " " + min + " " + hr + " " + d + " " + mon + " " + w + " " + y;
    }

    /**
     * 输入格式 06:30
     * 设置 hour、minute，并将 second 置为 0。
     *
     * @param triggerTime HH:mm
     */
    public void setHourAndMinute(String triggerTime) {
        if (StringUtils.isBlank(triggerTime)) {
            throw new IllegalArgumentException("triggerTime 不能为空");
        }
        String[] split = StringUtils.split(triggerTime.trim(), ":");
        if (split == null || split.length != 2) {
            throw new IllegalArgumentException("时间格式错误，期望：HH:mm");
        }
        int h = parseIntStrict(split[0], "小时应为数字");
        int m = parseIntStrict(split[1], "分钟应为数字");
        if (h < 0 || h > 23) {
            throw new IllegalArgumentException("小时范围应为 0-23");
        }
        if (m < 0 || m > 59) {
            throw new IllegalArgumentException("分钟范围应为 0-59");
        }
        this.hour = String.valueOf(h);
        this.minute = String.valueOf(m);
        this.second = "0";
    }

    /**
     * 输入格式代表星期的列表：MON、TUE、WED、THU、FRI、SAT、SUN
     * 如果传入 7 天则表示每周每天（'*'）；否则用逗号拼接。
     */
    public void setWeeks(List<String> repeatWeek) {
        if (repeatWeek == null || repeatWeek.isEmpty()) {
            this.week = "?";
            return;
        }
        List<String> normalized = repeatWeek.stream()
                .map(w -> normalizeWeekToken(StringUtils.trim(w)))
                .collect(Collectors.toList());
        this.week = normalized.size() == 7 ? "*" : StringUtils.join(normalized, ",");
    }

    /**
     * 输入格式代表日期的列表，如 02，09，10，11，12；支持 'first'（转为 1）与 'last'（转为 L）
     */
    public void setDays(List<String> repeatDay) {
        if (repeatDay == null || repeatDay.isEmpty()) {
            this.day = "*";
            return;
        }
        this.day = repeatDay.stream().map(d -> {
            String v = StringUtils.trim(d);
            if (StringUtils.isBlank(v)) {
                throw new IllegalArgumentException("日期项不能为空白");
            }
            if (StringUtils.equalsIgnoreCase(v, "last")) {
                return "L";
            } else if (StringUtils.equalsIgnoreCase(v, "first")) {
                return "1";
            } else {
                String n = stripLeadingZero(v);
                int di = parseIntStrict(n, "日期应为数字或 'first'/'last'");
                if (di < 1 || di > 31) {
                    throw new IllegalArgumentException("日期范围应为 1-31");
                }
                return String.valueOf(di);
            }
        }).collect(Collectors.joining(","));
    }

    /**
     * 输入格式 10-31
     *
     * @param triggerDate MM-dd
     */
    public void setMonthAndDay(String triggerDate) {
        if (StringUtils.isBlank(triggerDate)) {
            throw new IllegalArgumentException("triggerDate 不能为空");
        }
        String[] split = StringUtils.split(triggerDate.trim(), "-");
        if (split == null || split.length != 2) {
            throw new IllegalArgumentException("日期格式错误，期望：MM-dd");
        }
        int m = parseIntStrict(stripLeadingZero(split[0]), "月份应为数字");
        int d = parseIntStrict(stripLeadingZero(split[1]), "日期应为数字");
        if (m < 1 || m > 12) {
            throw new IllegalArgumentException("月份范围应为 1-12");
        }
        if (d < 1 || d > 31) {
            throw new IllegalArgumentException("日期范围应为 1-31");
        }
        this.month = String.valueOf(m);
        this.day = String.valueOf(d);
        this.week = "?";
    }

    /**
     * 输入格式：2025-10-25 09:00:00（必须包含秒）
     * 设置 year, month, day, hour, minute, second，并将 week 置为 "?"
     *
     * @param triggerTime 完整时间字符串
     */
    public void setDateTime(String triggerTime) {
        if (StringUtils.isBlank(triggerTime)) {
            throw new IllegalArgumentException("triggerTime 不能为空");
        }

        String[] dateTime = StringUtils.split(triggerTime.trim(), ' ');
        if (dateTime == null || dateTime.length != 2) {
            throw new IllegalArgumentException("时间格式错误，期望：yyyy-MM-dd HH:mm:ss");
        }

        // 解析日期
        String[] ymd = StringUtils.split(dateTime[0], '-');
        if (ymd == null || ymd.length != 3) {
            throw new IllegalArgumentException("日期格式错误，期望：yyyy-MM-dd");
        }

        int y, m, d;
        try {
            y = Integer.parseInt(ymd[0]);
            m = Integer.parseInt(ymd[1]);
            d = Integer.parseInt(ymd[2]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("日期包含非数字", e);
        }

        if (m < 1 || m > 12) {
            throw new IllegalArgumentException("月份范围应为 1-12");
        }
        if (d < 1 || d > 31) {
            throw new IllegalArgumentException("日期范围应为 1-31");
        }

        // 解析时间（必须包含秒）
        String[] hms = StringUtils.split(dateTime[1], ':');
        if (hms == null || hms.length != 3) {
            throw new IllegalArgumentException("时间格式错误，期望：HH:mm:ss");
        }

        int h, min, s;
        try {
            h = Integer.parseInt(hms[0]);
            min = Integer.parseInt(hms[1]);
            s = Integer.parseInt(hms[2]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("时间包含非数字", e);
        }

        if (h < 0 || h > 23) {
            throw new IllegalArgumentException("小时范围应为 0-23");
        }
        if (min < 0 || min > 59) {
            throw new IllegalArgumentException("分钟范围应为 0-59");
        }
        if (s < 0 || s > 59) {
            throw new IllegalArgumentException("秒范围应为 0-59");
        }

        // 赋值（Quartz 支持数字，无需前导零）
        this.year = String.valueOf(y);
        this.month = String.valueOf(m);
        this.day = String.valueOf(d);
        this.hour = String.valueOf(h);
        this.minute = String.valueOf(min);
        this.second = String.valueOf(s);

        // 与按日触发互斥，使用 ? 表示不指定星期
        this.week = "?";
    }

    // =========================
    // 私有辅助方法
    // =========================

    private int parseIntStrict(String s, String errMsg) {
        try {
            return Integer.parseInt(StringUtils.trim(s));
        } catch (Exception e) {
            throw new IllegalArgumentException(errMsg, e);
        }
    }

    private String stripLeadingZero(String s) {
        String t = StringUtils.trim(s);
        if (StringUtils.isBlank(t)) return t;
        return t.replaceFirst("^0+", "");
    }

    private static final List<String> WEEK_NAMES = Arrays.asList("SUN","MON","TUE","WED","THU","FRI","SAT");

    private String normalizeWeekToken(String token) {
        String t = StringUtils.upperCase(StringUtils.trim(token));
        if (StringUtils.isBlank(t)) {
            throw new IllegalArgumentException("星期项不能为空白");
        }
        // 允许数字 1-7，转名称；或名称 SUN..SAT
        if (t.matches("\\d+")) {
            int v = parseIntStrict(t, "星期应为 1-7 或 SUN..SAT");
            if (v < 1 || v > 7) {
                throw new IllegalArgumentException("星期范围应为 1-7");
            }
            return WEEK_NAMES.get(v - 1);
        }
        if (WEEK_NAMES.contains(t)) {
            return t;
        }
        throw new IllegalArgumentException("非法星期值：" + token + "，允许值：1-7 或 " + WEEK_NAMES);
    }

}