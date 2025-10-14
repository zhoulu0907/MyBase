package com.cmsr.onebase.module.flow.core.utils;

import org.springframework.scheduling.support.CronExpression;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @Author：huangjie
 * @Date：2025/10/11 17:25
 */
public class CronUtils {

    public static boolean isValid(String cron) {
        return CronExpression.isValidExpression(cron);
    }

    public static List<String> nextExecuteTime(String cron, int count) throws Exception {
        List<String> list = new ArrayList<>();
        CronExpression cronExpression = CronExpression.parse(cron);
        LocalDateTime nextTime = LocalDateTime.now();
        for (int i = 0; i < count; i++) {
            nextTime = cronExpression.next(nextTime);
            list.add(nextTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        return list;
    }

}
