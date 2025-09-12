package com.cmsr.onebase.module.flow.core.graph.data;

import org.apache.commons.lang3.StringUtils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

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
     * 日
     */
    private String day = "*";

    /**
     * 月
     */
    private String month = "*";

    /**
     * 星期
     */
    private String week = "?";

    public String toCron() {
        return second + " " + minute + " " + hour + " " + day + " " + month + " " + week;
    }

    public void setMinuteAndHour(String triggerTime) {
        String[] split = StringUtils.split(triggerTime, ":");
        hour = split[0];
        minute = split[1];
    }


    public void setWeek(String[] repeatWeek) {
        week = repeatWeek.length == 7 ? "*" : StringUtils.join(repeatWeek, ",");
    }

    public void setDay(String[] repeatDay) {
        day = Stream.of(repeatDay).map(day -> {
            if (day.equalsIgnoreCase("last")) {
                return "L";
            } else if (day.equalsIgnoreCase("first")) {
                return "1";
            } else {
                return day;
            }
        }).collect(Collectors.joining(","));
    }

    public void setDay(String triggerDate) {
        String[] split = StringUtils.split(triggerDate, ":");
        month = split[0];
        day = split[1];
    }
}
