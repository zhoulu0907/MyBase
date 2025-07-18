package com.cmsr.onebase.framework.common.util.snowflake;

/**
 * @ClassName SnowflakeIdGenerator
 * @Description 雪花ID生成器，提供分布式唯一ID生成功能
 * @Author mickey
 * @Date 2025/7/7 09:13
 */
public class SnowflakeIdGenerator {
    // 起始的时间戳（可设置为系统首次使用的时间）
    private final static long START_TIMESTAMP = 1625097600000L; // 2021-07-01 00:00:00

    // 每一部分占用的位数
    private final static long SEQUENCE_BIT = 12; // 序列号占用的位数
    private final static long MACHINE_BIT = 5;   // 机器标识占用的位数
    private final static long DATACENTER_BIT = 5;// 数据中心占用的位数

    // 每一部分的最大值
    private final static long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);
    private final static long MAX_MACHINE_NUM = ~(-1L << MACHINE_BIT);
    private final static long MAX_DATACENTER_NUM = ~(-1L << DATACENTER_BIT);

    // 每一部分向左的位移
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private final static long TIMESTAMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;

    private final long datacenterId; // 数据中心ID
    private final long machineId;    // 机器ID
    private long sequence = 0L;      // 序列号
    private long lastTimestamp = -1L;// 上一次时间戳

    /**
     * 构造函数
     * @param datacenterId 数据中心ID (0~31)
     * @param machineId    机器ID (0~31)
     */
    public SnowflakeIdGenerator(long datacenterId, long machineId) {
        if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
            throw new IllegalArgumentException("Datacenter ID can't be greater than " + MAX_DATACENTER_NUM + " or less than 0");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("Machine ID can't be greater than " + MAX_MACHINE_NUM + " or less than 0");
        }
        this.datacenterId = datacenterId;
        this.machineId = machineId;
    }

    /**
     * 生成下一个ID
     * @return 雪花ID
     */
    public synchronized long nextId() {
        long currentTimestamp = getCurrentTimestamp();

        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id for " + (lastTimestamp - currentTimestamp) + " milliseconds");
        }

        if (currentTimestamp == lastTimestamp) {
            // 相同毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 同一毫秒的序列数已经达到最大
            if (sequence == 0L) {
                currentTimestamp = getNextMill();
            }
        } else {
            // 不同毫秒内，序列号置为0
            sequence = 0L;
        }

        lastTimestamp = currentTimestamp;

        // 时间戳部分 | 数据中心部分 | 机器标识部分 | 序列号部分
        return ((currentTimestamp - START_TIMESTAMP) << TIMESTAMP_LEFT)
                | (datacenterId << DATACENTER_LEFT)
                | (machineId << MACHINE_LEFT)
                | sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     * @return 当前时间戳
     */
    private long getNextMill() {
        long mill = getCurrentTimestamp();
        while (mill <= lastTimestamp) {
            mill = getCurrentTimestamp();
        }
        return mill;
    }

    /**
     * 获取当前时间戳
     * @return 当前时间(毫秒)
     */
    private long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

}
