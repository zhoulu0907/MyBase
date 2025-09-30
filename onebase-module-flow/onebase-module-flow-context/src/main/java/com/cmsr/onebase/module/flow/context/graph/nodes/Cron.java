package com.cmsr.onebase.module.flow.context.graph.nodes;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;
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
        hour = String.valueOf(NumberUtils.toInt(split[0]));
        minute = String.valueOf(NumberUtils.toInt(split[1]));
    }


    public void setWeek(List<String> repeatWeek) {
        week = repeatWeek.size() == 7 ? "*" : StringUtils.join(repeatWeek, ",");
        day = "?";
    }

    public void setDay(List<String> repeatDay) {
        day = repeatDay.stream().map(day -> {
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
        String[] split = StringUtils.split(triggerDate, "-");
        month = split[0];
        day = split[1];
    }
}
