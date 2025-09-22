package com.cmsr.onebase.module.flow.core.graph.data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * StartTimeNodeData 测试类
 * 覆盖所有重复类型场景的测试
 *
 * @Author：huangjie
 * @Date：2025/9/12 17:00
 */
class StartTimeNodeDataTest {

    private StartTimeNodeData startTimeNodeData;


    @Test
    @DisplayName("测试Map构造函数")
    void testMapConstructor() {
        Map<String, Object> data = new HashMap<>();
        data.put("cronExpression", "0 0 12 * * ?");
        data.put("startTime", "2025-01-01 00:00:00");
        data.put("endTime", "2025-12-31 23:59:59");
        data.put("delaySeconds", 30);
        data.put("repeatType", "day");
        data.put("repeatMonth", new String[]{"01", "06", "12"});
        data.put("repeatWeek", new String[]{"MON", "FRI"});
        data.put("repeatDay", new String[]{"1", "15"});
        data.put("triggerDatetime", "2025-02-05 19:16");
        data.put("triggerDate", "01-01");
        data.put("triggerTime", "16:01");

        startTimeNodeData = new StartTimeNodeData(data);


    }

    @Test
    @DisplayName("1. 不重复模式测试 - 应该抛出异常")
    void testNoneRepeatType() {
        startTimeNodeData.setRepeatType(StartTimeNodeData.REPEAT_TYPE_NONE);
        startTimeNodeData.setTriggerDatetime("2025-02-05 19:16");

        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> startTimeNodeData.createCronExpression()
        );
        assertTrue(exception.getMessage().contains("不支持的定时类型"));
    }

    @Test
    @DisplayName("2. 每日重复测试")
    void testDayRepeatType() {
        startTimeNodeData.setRepeatType(StartTimeNodeData.REPEAT_TYPE_DAY);
        startTimeNodeData.setTriggerTime("16:01");

        String cronExpression = startTimeNodeData.createCronExpression();

        // 期望格式: * 1 16 * * ?
        assertEquals("* 1 16 * * ?", cronExpression);
    }

    @Test
    @DisplayName("2.1 每日重复边界时间测试 - 00:00")
    void testDayRepeatTypeMidnight() {
        startTimeNodeData.setRepeatType(StartTimeNodeData.REPEAT_TYPE_DAY);
        startTimeNodeData.setTriggerTime("00:00");

        String cronExpression = startTimeNodeData.createCronExpression();
        assertEquals("* 0 0 * * ?", cronExpression);
    }

    @Test
    @DisplayName("2.2 每日重复边界时间测试 - 23:59")
    void testDayRepeatTypeEndOfDay() {
        startTimeNodeData.setRepeatType(StartTimeNodeData.REPEAT_TYPE_DAY);
        startTimeNodeData.setTriggerTime("23:59");

        String cronExpression = startTimeNodeData.createCronExpression();
        assertEquals("* 59 23 * * ?", cronExpression);
    }


    @Test
    @DisplayName("6. 每年重复测试")
    void testYearRepeatType() {
        startTimeNodeData.setRepeatType(StartTimeNodeData.REPEAT_TYPE_YEAR);
        startTimeNodeData.setTriggerDate("01:01");  // 注意：代码中使用冒号分隔月日
        startTimeNodeData.setTriggerTime("16:04");

        String cronExpression = startTimeNodeData.createCronExpression();

        // 期望格式: * 4 16 01 01 ?
        assertEquals("* 4 16 01 01 ?", cronExpression);
    }

    @Test
    @DisplayName("6.1 每年重复测试 - 生日场景")
    void testYearRepeatTypeBirthday() {
        startTimeNodeData.setRepeatType(StartTimeNodeData.REPEAT_TYPE_YEAR);
        startTimeNodeData.setTriggerDate("12:25");  // 12月25日
        startTimeNodeData.setTriggerTime("00:00");

        String cronExpression = startTimeNodeData.createCronExpression();
        assertEquals("* 0 0 25 12 ?", cronExpression);
    }

    @Test
    @DisplayName("6.2 每年重复测试 - 闰年日期")
    void testYearRepeatTypeLeapYear() {
        startTimeNodeData.setRepeatType(StartTimeNodeData.REPEAT_TYPE_YEAR);
        startTimeNodeData.setTriggerDate("02:29");  // 2月29日
        startTimeNodeData.setTriggerTime("12:00");

        String cronExpression = startTimeNodeData.createCronExpression();
        assertEquals("* 0 12 29 02 ?", cronExpression);
    }

    @Test
    @DisplayName("7. 自定义cron表达式测试 - 应该抛出异常")
    void testCronRepeatType() {
        startTimeNodeData.setRepeatType(StartTimeNodeData.REPEAT_TYPE_CRON);
        startTimeNodeData.setCronExpression("0 0 12 * * ?");

        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> startTimeNodeData.createCronExpression()
        );
        assertTrue(exception.getMessage().contains("不支持的定时类型"));
    }

    @Test
    @DisplayName("7.1 每小时重复测试 - 应该抛出异常")
    void testHourRepeatType() {
        startTimeNodeData.setRepeatType(StartTimeNodeData.REPEAT_TYPE_HOUR);
        startTimeNodeData.setTriggerTime("30");  // 每小时的第30分钟

        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> startTimeNodeData.createCronExpression()
        );
        assertTrue(exception.getMessage().contains("不支持的定时类型"));
    }

    @Test
    @DisplayName("测试无效的重复类型")
    void testInvalidRepeatType() {
        startTimeNodeData.setRepeatType("invalid");

        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> startTimeNodeData.createCronExpression()
        );
        assertTrue(exception.getMessage().contains("不支持的定时类型: invalid"));
    }

    @Test
    @DisplayName("测试null重复类型")
    void testNullRepeatType() {
        startTimeNodeData.setRepeatType(null);

        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> startTimeNodeData.createCronExpression()
        );
        assertTrue(exception.getMessage().contains("不支持的定时类型: null"));
    }

    @Test
    @DisplayName("测试常量值正确性")
    void testConstants() {
        assertEquals("none", StartTimeNodeData.REPEAT_TYPE_NONE);
        assertEquals("year", StartTimeNodeData.REPEAT_TYPE_YEAR);
        assertEquals("month", StartTimeNodeData.REPEAT_TYPE_MONTH);
        assertEquals("week", StartTimeNodeData.REPEAT_TYPE_WEEK);
        assertEquals("day", StartTimeNodeData.REPEAT_TYPE_DAY);
        assertEquals("hour", StartTimeNodeData.REPEAT_TYPE_HOUR);
        assertEquals("cron", StartTimeNodeData.REPEAT_TYPE_CRON);
    }


}