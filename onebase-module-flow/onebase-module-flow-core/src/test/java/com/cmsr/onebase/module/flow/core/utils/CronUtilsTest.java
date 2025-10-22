package com.cmsr.onebase.module.flow.core.utils;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author：huangjie
 * @Date：2025/10/11 17:30
 */
class CronUtilsTest {


    @Test
    void nextExecuteTime() throws Exception {
        //String cron = "0 10 12 * * *";
        String cron = "0 10 12 * * ?";
        boolean valid = CronUtils.isValid(cron);
        System.out.println(valid);
        List<String> list = CronUtils.nextExecuteTime(cron, 5);
        System.out.println(list);
    }
}