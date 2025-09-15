package com.cmsr.onebase.module.flow.core.graph.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

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

    @BeforeEach
    void setUp() {
        startTimeNodeData = new StartTimeNodeData();
    }

    @Test
    @DisplayName("测试默认构造函数")
    void testDefaultConstructor() {
        StartTimeNodeData nodeData = new StartTimeNodeData();
        assertNotNull(nodeData);
        assertNull(nodeData.getRepeatType());
        assertNull(nodeData.getTriggerTime());
        assertNull(nodeData.getCronExpression());
    }

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

        StartTimeNodeData nodeData = new StartTimeNodeData(data);

        assertEquals("0 0 12 * * ?", nodeData.getCronExpression());
        assertEquals("2025-01-01 00:00:00", nodeData.getStartTime());
        assertEquals("2025-12-31 23:59:59", nodeData.getEndTime());
        assertEquals(30, nodeData.getDelaySeconds());
        assertEquals("day", nodeData.getRepeatType());
        assertArrayEquals(new String[]{"01", "06", "12"}, nodeData.getRepeatMonth());
        assertArrayEquals(new String[]{"MON", "FRI"}, nodeData.getRepeatWeek());
        assertArrayEquals(new String[]{"1", "15"}, nodeData.getRepeatDay());
        assertEquals("2025-02-05 19:16", nodeData.getTriggerDatetime());
        assertEquals("01-01", nodeData.getTriggerDate());
        assertEquals("16:01", nodeData.getTriggerTime());
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
    @DisplayName("3. 每周重复测试 - 周日和周六")
    void testWeekRepeatType() {
        startTimeNodeData.setRepeatType(StartTimeNodeData.REPEAT_TYPE_WEEK);
        startTimeNodeData.setRepeatWeek(new String[]{"SUN", "SAT"});
        startTimeNodeData.setTriggerTime("19:16");

        String cronExpression = startTimeNodeData.createCronExpression();
        
        // 期望格式: * 16 19 * * SUN,SAT
        assertEquals("* 16 19 * * SUN,SAT", cronExpression);
    }

    @Test
    @DisplayName("3.1 每周重复测试 - 工作日")
    void testWeekRepeatTypeWeekdays() {
        startTimeNodeData.setRepeatType(StartTimeNodeData.REPEAT_TYPE_WEEK);
        startTimeNodeData.setRepeatWeek(new String[]{"MON", "TUE", "WED", "THU", "FRI"});
        startTimeNodeData.setTriggerTime("09:00");

        String cronExpression = startTimeNodeData.createCronExpression();
        assertEquals("* 0 9 * * MON,TUE,WED,THU,FRI", cronExpression);
    }

    @Test
    @DisplayName("3.2 每周重复测试 - 全部星期（应优化为*）")
    void testWeekRepeatTypeAllDays() {
        startTimeNodeData.setRepeatType(StartTimeNodeData.REPEAT_TYPE_WEEK);
        startTimeNodeData.setRepeatWeek(new String[]{"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"});
        startTimeNodeData.setTriggerTime("12:00");

        String cronExpression = startTimeNodeData.createCronExpression();
        // 当选择全部7天时，应该优化为 *
        assertEquals("* 0 12 * * *", cronExpression);
    }

    @Test
    @DisplayName("3.3 每周重复测试 - 单个星期")
    void testWeekRepeatTypeSingleDay() {
        startTimeNodeData.setRepeatType(StartTimeNodeData.REPEAT_TYPE_WEEK);
        startTimeNodeData.setRepeatWeek(new String[]{"WED"});
        startTimeNodeData.setTriggerTime("14:30");

        String cronExpression = startTimeNodeData.createCronExpression();
        assertEquals("* 30 14 * * WED", cronExpression);
    }

    @Test
    @DisplayName("4. 每月重复测试 - 指定日期")
    void testMonthRepeatTypeSpecificDays() {
        startTimeNodeData.setRepeatType(StartTimeNodeData.REPEAT_TYPE_MONTH);
        startTimeNodeData.setRepeatDay(new String[]{"1", "15"});
        startTimeNodeData.setTriggerTime("19:16");

        String cronExpression = startTimeNodeData.createCronExpression();
        
        // 期望格式: * 16 19 1,15 * ?
        assertEquals("* 16 19 1,15 * ?", cronExpression);
    }

    @Test
    @DisplayName("4.1 每月重复测试 - 月末")
    void testMonthRepeatTypeLastDay() {
        startTimeNodeData.setRepeatType(StartTimeNodeData.REPEAT_TYPE_MONTH);
        startTimeNodeData.setRepeatDay(new String[]{"last"});
        startTimeNodeData.setTriggerTime("23:00");

        String cronExpression = startTimeNodeData.createCronExpression();
        // last 应该转换为 L
        assertEquals("* 0 23 L * ?", cronExpression);
    }

    @Test
    @DisplayName("5. 每月重复测试 - 第一天")
    void testMonthRepeatTypeFirstDay() {
        startTimeNodeData.setRepeatType(StartTimeNodeData.REPEAT_TYPE_MONTH);
        startTimeNodeData.setRepeatDay(new String[]{"first"});
        startTimeNodeData.setTriggerTime("19:16");

        String cronExpression = startTimeNodeData.createCronExpression();
        
        // first 应该转换为 1
        assertEquals("* 16 19 1 * ?", cronExpression);
    }

    @Test
    @DisplayName("5.1 每月重复测试 - 混合日期")
    void testMonthRepeatTypeMixedDays() {
        startTimeNodeData.setRepeatType(StartTimeNodeData.REPEAT_TYPE_MONTH);
        startTimeNodeData.setRepeatDay(new String[]{"first", "15", "last"});
        startTimeNodeData.setTriggerTime("10:30");

        String cronExpression = startTimeNodeData.createCronExpression();
        assertEquals("* 30 10 1,15,L * ?", cronExpression);
    }

    @Test
    @DisplayName("5.2 每月重复测试 - 边界日期")
    void testMonthRepeatTypeBoundaryDays() {
        startTimeNodeData.setRepeatType(StartTimeNodeData.REPEAT_TYPE_MONTH);
        startTimeNodeData.setRepeatDay(new String[]{"1", "31"});
        startTimeNodeData.setTriggerTime("08:00");

        String cronExpression = startTimeNodeData.createCronExpression();
        assertEquals("* 0 8 1,31 * ?", cronExpression);
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

    @Test
    @DisplayName("测试字段的getter和setter")
    void testGettersAndSetters() {
        startTimeNodeData.setCronExpression("0 0 12 * * ?");
        startTimeNodeData.setStartTime("2025-01-01 00:00:00");
        startTimeNodeData.setEndTime("2025-12-31 23:59:59");
        startTimeNodeData.setDelaySeconds(60);
        startTimeNodeData.setRepeatType("day");
        startTimeNodeData.setRepeatMonth(new String[]{"01", "06"});
        startTimeNodeData.setRepeatWeek(new String[]{"MON", "FRI"});
        startTimeNodeData.setRepeatDay(new String[]{"1", "15"});
        startTimeNodeData.setTriggerDatetime("2025-02-05 19:16");
        startTimeNodeData.setTriggerDate("01-01");
        startTimeNodeData.setTriggerTime("16:01");

        assertEquals("0 0 12 * * ?", startTimeNodeData.getCronExpression());
        assertEquals("2025-01-01 00:00:00", startTimeNodeData.getStartTime());
        assertEquals("2025-12-31 23:59:59", startTimeNodeData.getEndTime());
        assertEquals(60, startTimeNodeData.getDelaySeconds());
        assertEquals("day", startTimeNodeData.getRepeatType());
        assertArrayEquals(new String[]{"01", "06"}, startTimeNodeData.getRepeatMonth());
        assertArrayEquals(new String[]{"MON", "FRI"}, startTimeNodeData.getRepeatWeek());
        assertArrayEquals(new String[]{"1", "15"}, startTimeNodeData.getRepeatDay());
        assertEquals("2025-02-05 19:16", startTimeNodeData.getTriggerDatetime());
        assertEquals("01-01", startTimeNodeData.getTriggerDate());
        assertEquals("16:01", startTimeNodeData.getTriggerTime());
    }
}