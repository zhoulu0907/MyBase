package com.cmsr.onebase.module.flow.sched;

/**
 * @Author：huangjie
 * @Date：2025/10/14 10:48
 */
public class ScheduleMainTest {

    public static void main(String[] args) {
        args = new String[]{"-mqEndpoints", "10.0.104.38:8081", "-redisAddress", "10.0.104.38:6379", "-processId", "123456", "-msgType", "norm"};
        ScheduleMain.main(args);
    }

}