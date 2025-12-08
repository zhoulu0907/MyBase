package com.cmsr.onebase.module.flow.context.graph.nodes;

import com.cmsr.onebase.module.flow.context.graph.NodeData;
import com.cmsr.onebase.module.flow.context.util.Cron;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/9 13:55
 */
@Data
public class StartTimeNodeData extends NodeData implements Serializable {
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

    /**
     * 应用ID，后补充
     */
    private Long applicationId;
    /**
     * 流程ID，后补充
     */
    private Long processId;

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


    public String createCronExpression() {
        if (REPEAT_TYPE_DAY.equalsIgnoreCase(repeatType)) {
            Cron cron = new Cron();
            cron.setHourAndMinute(triggerTime);
            return cron.toCron();
        }
        if (REPEAT_TYPE_WEEK.equalsIgnoreCase(repeatType)) {
            Cron cron = new Cron();
            cron.setWeeks(repeatWeek);
            cron.setHourAndMinute(triggerTime);
            return cron.toCron();
        }
        if (REPEAT_TYPE_MONTH.equalsIgnoreCase(repeatType)) {
            Cron cron = new Cron();
            cron.setDays(repeatDay);
            cron.setHourAndMinute(triggerTime);
            return cron.toCron();
        }
        if (REPEAT_TYPE_YEAR.equalsIgnoreCase(repeatType)) {
            Cron cron = new Cron();
            cron.setMonthAndDay(triggerDate);
            cron.setHourAndMinute(triggerTime);
            return cron.toCron();
        }
        if (REPEAT_TYPE_NONE.equalsIgnoreCase(repeatType)) {
            Cron cron = new Cron();
            cron.setDateTime(triggerTime);
            return cron.toCron();
        }
        if (REPEAT_TYPE_CRON.equalsIgnoreCase(repeatType)) {
            return triggerTime;
        }
        throw new UnsupportedOperationException("不支持的定时类型: " + repeatType);
    }


}
