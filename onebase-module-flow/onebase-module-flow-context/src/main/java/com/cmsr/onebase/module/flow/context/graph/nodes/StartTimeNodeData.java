package com.cmsr.onebase.module.flow.context.graph.nodes;

import com.cmsr.onebase.module.flow.context.graph.JsonGraphConstant;
import com.cmsr.onebase.module.flow.context.graph.NodeData;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    //
    private Optional<LocalDateTime> startLocalDateTime;
    private Optional<LocalDateTime> endLocalDateTime;

    private void init() {
        if (startLocalDateTime != null || endLocalDateTime != null) {
            return;
        }
        startTime = StringUtils.trimToNull(startTime);
        if (StringUtils.isNotEmpty(startTime)) {
            startLocalDateTime = Optional.of(LocalDateTime.parse(startTime, JsonGraphConstant.DATE_TIME_FORMATTER));
        } else {
            startLocalDateTime = Optional.empty();
        }
        endTime = StringUtils.trimToNull(endTime);
        if (StringUtils.isNotEmpty(endTime)) {
            endLocalDateTime = Optional.of(LocalDateTime.parse(endTime, JsonGraphConstant.DATE_TIME_FORMATTER));
        } else {
            endLocalDateTime = Optional.empty();
        }
    }

    public boolean isCurrentTimeInRange() {
        init();
        if (startLocalDateTime.isPresent() && LocalDateTime.now().isBefore(startLocalDateTime.get())) {
            return false;
        }
        if (endLocalDateTime.isPresent() && LocalDateTime.now().isAfter(endLocalDateTime.get())) {
            return false;
        }
        return true;
    }

    public String createCronExpression() {
        if (REPEAT_TYPE_DAY.equalsIgnoreCase(repeatType)) {
            Cron cron = new Cron();
            cron.setMinuteAndHour(triggerTime);
            return cron.toCron();
        }
        if (REPEAT_TYPE_WEEK.equalsIgnoreCase(repeatType)) {
            Cron cron = new Cron();
            cron.setMinuteAndHour(triggerTime);
            cron.setWeek(repeatWeek);
            return cron.toCron();
        }
        if (REPEAT_TYPE_MONTH.equalsIgnoreCase(repeatType)) {
            Cron cron = new Cron();
            cron.setMinuteAndHour(triggerTime);
            cron.setDay(repeatDay);
            return cron.toCron();
        }
        if (REPEAT_TYPE_YEAR.equalsIgnoreCase(repeatType)) {
            Cron cron = new Cron();
            cron.setMinuteAndHour(triggerTime);
            cron.setDay(triggerDate);
            return cron.toCron();
        }
        if (REPEAT_TYPE_CRON.equalsIgnoreCase(repeatType)) {
            return triggerTime;
        }
        throw new UnsupportedOperationException("不支持的定时类型: " + repeatType);
    }


}
