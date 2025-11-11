package com.cmsr.onebase.module.flow.context.graph.nodes;

import com.cmsr.onebase.module.flow.context.condition.Conditions;
import com.cmsr.onebase.module.flow.context.graph.NodeData;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/22 9:24
 */
@Data
public class StartDateFieldNodeData extends NodeData implements Serializable {

    public static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 应用ID，后补充
     */
    private Long applicationId;
    /**
     * 流程ID，后补充
     */
    private Long processId;

    private Long entityId;

    /**
     * 偏移字段ID
     */
    private Long offsetFiledId;

    /**
     * 偏移模式  before  after
     */
    private String offsetMode;

    private Integer offsetValue;

    /**
     * 偏移单位 day hour minute
     */
    private String offsetUnit;

    /**
     * 每日执行时间  09:00
     */
    private String dailyExecTime;

    private boolean batchMode;

    private Integer batchSize;

    /**
     * 过滤条件
     */
    private List<Conditions> filterCondition;

    public String createCronExpression() {
        Cron cron = new Cron();
        cron.setHourAndMinute(dailyExecTime);
        return cron.toCron();
    }

    /**
     * 根据 offsetMode offsetValue offsetUnit 计算出 offsetTime
     *
     * @param triggerTime
     * @return
     */
    public List<String> calculateOffTime(LocalDateTime triggerTime) {
        if (triggerTime == null || offsetValue == null || offsetUnit == null || offsetMode == null) {
            throw new IllegalArgumentException("参数错误");
        }
        LocalDateTime resultTime;
        // 根据偏移模式和单位计算偏移时间
        if ("before".equals(offsetMode)) {
            // 在触发时间之后
            switch (offsetUnit) {
                case "day":
                    resultTime = triggerTime.plusDays(offsetValue);
                    break;
                case "hour":
                    resultTime = triggerTime.plusHours(offsetValue);
                    break;
                case "minute":
                    resultTime = triggerTime.plusMinutes(offsetValue);
                    break;
                default:
                    throw new IllegalArgumentException("未知的偏移单位:" + offsetUnit);
            }
        } else if ("after".equals(offsetMode)) {
            // 在触发时间之前
            switch (offsetUnit) {
                case "day":
                    resultTime = triggerTime.minusDays(offsetValue);
                    break;
                case "hour":
                    resultTime = triggerTime.minusHours(offsetValue);
                    break;
                case "minute":
                    resultTime = triggerTime.minusMinutes(offsetValue);
                    break;
                default:
                    throw new IllegalArgumentException("未知的偏移单位:" + offsetUnit);
            }
        } else {
            resultTime = triggerTime;
        }
        //resultTime 设置 时分秒为 00:00:00
        LocalDateTime startTime = resultTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
        //resultTime 设置 时分秒为 23:59:59
        LocalDateTime endTime = resultTime.withHour(23).withMinute(59).withSecond(59).withNano(999_999_999);
        return List.of(startTime.format(TIMESTAMP_FORMATTER), endTime.format(TIMESTAMP_FORMATTER));
    }

    public String calculateOffTime(LocalDate triggerTime) {
        if (triggerTime == null || offsetValue == null || offsetUnit == null || offsetMode == null) {
            throw new IllegalArgumentException("参数错误");
        }
        LocalDate resultTime = triggerTime;
        // 根据偏移模式和单位计算偏移时间
        if ("before".equals(offsetMode)) {
            resultTime = resultTime.plusDays(offsetValue);
        } else if ("after".equals(offsetMode)) {
            resultTime = resultTime.minusDays(offsetValue);
        } else {
            resultTime = triggerTime;
        }
        return resultTime.format(DATE_FORMATTER);
    }

}
