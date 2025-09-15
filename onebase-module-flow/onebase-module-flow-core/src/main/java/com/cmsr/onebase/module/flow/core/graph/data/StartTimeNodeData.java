package com.cmsr.onebase.module.flow.core.graph.data;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/9/9 13:55
 */
@Data
public class StartTimeNodeData {
    /**
     * 不重复
     */
    public static final String REPEAT_TYPE_NONE = "none";

    /**
     * 每年
     */
    public static final String REPEAT_TYPE_YEAR = "year";

    /**
     * 每月
     */
    public static final String REPEAT_TYPE_MONTH = "month";

    /**
     * 每周
     */
    public static final String REPEAT_TYPE_WEEK = "week";

    /**
     * 每日
     */
    public static final String REPEAT_TYPE_DAY = "day";

    /**
     * 每小时
     */
    public static final String REPEAT_TYPE_HOUR = "hour";

    /**
     * 自定义cron表达式
     */
    public static final String REPEAT_TYPE_CRON = "cron";

    private String cronExpression;

    private String startTime;

    private String endTime;

    private Integer delaySeconds;

    private String repeatType;

    private List<String> repeatMonth;

    private List<String> repeatWeek;

    private List<String> repeatDay;

    private String triggerDatetime;

    private String triggerDate;

    private String triggerTime;

    public StartTimeNodeData() {

    }

    public StartTimeNodeData(Map<String, Object> data) {
        this.cronExpression = (String) data.get("cronExpression");
        this.startTime = (String) data.get("startTime");
        this.endTime = (String) data.get("endTime");
        this.delaySeconds = (Integer) data.get("delaySeconds");
        this.repeatType = (String) data.get("repeatType");
        this.repeatMonth = (List) data.get("repeatMonth");
        this.repeatWeek = (List) data.get("repeatWeek");
        this.repeatDay = (List) data.get("repeatDay");
        this.triggerDatetime = (String) data.get("triggerDatetime");
        this.triggerDate = (String) data.get("triggerDate");
        this.triggerTime = (String) data.get("triggerTime");
    }

    public String createCronExpression() {
        if (REPEAT_TYPE_DAY.equals(repeatType)) {
            Cron cron = new Cron();
            cron.setMinuteAndHour(triggerTime);
            return cron.toCron();
        }
        if (REPEAT_TYPE_WEEK.equals(repeatType)) {
            Cron cron = new Cron();
            cron.setMinuteAndHour(triggerTime);
            cron.setWeek(repeatWeek);
            return cron.toCron();
        }
        if (REPEAT_TYPE_MONTH.equals(repeatType)) {
            Cron cron = new Cron();
            cron.setMinuteAndHour(triggerTime);
            cron.setDay(repeatDay);
            return cron.toCron();
        }
        if (REPEAT_TYPE_YEAR.equals(repeatType)) {
            Cron cron = new Cron();
            cron.setMinuteAndHour(triggerTime);
            cron.setDay(triggerDate);
            return cron.toCron();
        }
        throw new UnsupportedOperationException("不支持的定时类型: " + repeatType);
    }


}
