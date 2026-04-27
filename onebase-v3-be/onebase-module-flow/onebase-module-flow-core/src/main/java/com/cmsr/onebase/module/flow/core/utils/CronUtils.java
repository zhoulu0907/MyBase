package com.cmsr.onebase.module.flow.core.utils;

import org.springframework.scheduling.support.CronExpression;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Cron表达式工具类
 *
 * @Author：huangjie
 * @Date：2025/10/11 17:25
 */
public class CronUtils {

    /**
     * 日期时间格式：yyyy-MM-dd HH:mm:ss
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 验证Cron表达式是否有效
     *
     * @param cron Cron表达式
     * @return true-有效，false-无效
     */
    public static boolean isValid(String cron) {
        return CronExpression.isValidExpression(cron);
    }

    /**
     * 获取接下来的N次执行时间
     *
     * @param cron Cron表达式
     * @param count 获取次数
     * @return 执行时间列表
     * @throws Exception 解析异常
     */
    public static List<String> nextExecuteTime(String cron, int count) throws Exception {
        List<String> list = new ArrayList<>();
        CronExpression cronExpression = CronExpression.parse(cron);
        LocalDateTime nextTime = LocalDateTime.now();
        for (int i = 0; i < count; i++) {
            nextTime = cronExpression.next(nextTime);
            list.add(nextTime.format(DATE_TIME_FORMATTER));
        }
        return list;
    }

}
