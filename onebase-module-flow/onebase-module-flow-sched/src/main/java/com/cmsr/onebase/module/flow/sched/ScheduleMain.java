package com.cmsr.onebase.module.flow.sched;

import com.google.gson.Gson;
import org.apache.commons.cli.*;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisClientConfig;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 消息发送主类
 * 用于向RocketMQ发送流程处理时间消息
 *
 * @author huangjie
 * @since 2025/10/14
 */
public class ScheduleMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleMain.class);

    private static final ClientServiceProvider PROVIDER = ClientServiceProvider.loadService();

    private static final Gson GSON = new Gson();

    public static final String JOB_EVENTS_TOPIC = "flow_process_job_events";

    public static final String JOB_EVENTS_RESULT = "flow:process:job:result:";

    // 退出代码
    private static final int EXIT_CODE_SUCCESS = 0;      // 正常退出
    private static final int EXIT_CODE_ERROR = 1;        // 异常退出

    private String mqEndpoints;

    private String redisAddress;

    private Long processId;

    private String msgType;

    private String uuid = UUID.randomUUID().toString();

    /**
     * 程序入口点
     */
    public static void main(String[] args) {
        try {
            ScheduleMain scheduleMain = new ScheduleMain();
            scheduleMain.parseInputParams(args);
            scheduleMain.sendMessage();
            boolean result = scheduleMain.readResult();
            if (result) {
                System.exit(EXIT_CODE_SUCCESS);
            } else {
                System.exit(EXIT_CODE_ERROR);
            }
        } catch (Exception e) {
            LOGGER.error("发送消息时发生错误: {}", e.getMessage(), e);
            System.exit(EXIT_CODE_ERROR);
        }
    }


    /**
     * 解析输入参数
     */
    private void parseInputParams(String[] args) throws ParseException {
        Options options = createOptions();

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        this.mqEndpoints = cmd.getOptionValue("mqEndpoints");
        this.redisAddress = cmd.getOptionValue("redisAddress");
        this.processId = Long.parseLong(cmd.getOptionValue("processId"));
        this.msgType = cmd.getOptionValue("msgType");
    }

    private Options createOptions() {
        Options options = new Options();

        Option endpointsOption = new Option("e", "mqEndpoints", true, "RocketMQ server endpoints");
        endpointsOption.setRequired(true);
        options.addOption(endpointsOption);

        Option redisAddressOption = new Option("r", "redisAddress", true, "Redis server address");
        redisAddressOption.setRequired(true);
        options.addOption(redisAddressOption);

        Option processIdOption = new Option("p", "processId", true, "Process ID");
        processIdOption.setRequired(true);
        options.addOption(processIdOption);

        Option msgTpyeOption = new Option("t", "msgType", true, "Message type");
        msgTpyeOption.setRequired(true);
        options.addOption(msgTpyeOption);

        return options;
    }


    /**
     * 发送消息到RocketMQ
     */
    private void sendMessage() throws ClientException {
        Producer producer = null;
        try {
            // 创建生产者
            producer = createProducer();
            // 构建并发送消息
            SendReceipt sendReceipt = buildAndSendMessage(producer);
            LOGGER.info("消息已发送：{}", sendReceipt.getMessageId());
        } finally {
            closeProducer(producer);
        }
    }


    /**
     * 创建RocketMQ生产者
     */
    private Producer createProducer() throws ClientException {
        ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
                .setEndpoints(mqEndpoints)
                .build();
        return PROVIDER.newProducerBuilder()
                .setClientConfiguration(clientConfiguration)
                .setTopics(JOB_EVENTS_TOPIC)
                .build();
    }

    private void closeProducer(Producer producer) {
        if (producer != null) {
            try {
                producer.close();
            } catch (IOException e) {
                LOGGER.warn("关闭生产者时发生错误: ", e.getMessage(), e);
            }
        }
    }

    /**
     * 构建并发送消息
     */
    private SendReceipt buildAndSendMessage(Producer producer) throws ClientException {
        // 构建消息体
        Map<String, Object> messageBody = new HashMap<>();
        messageBody.put("processId", processId);
        messageBody.put("msgType", msgType);
        messageBody.put("time", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        messageBody.put("uuid", uuid);
        // 发送消息
        String messageJson = GSON.toJson(messageBody);
        LOGGER.info("发送消息：{}", messageJson);
        Message message = PROVIDER.newMessageBuilder()
                .setTopic(JOB_EVENTS_TOPIC)
                .setBody(messageJson.getBytes(StandardCharsets.UTF_8))
                .build();
        return producer.send(message);
    }

    private boolean readResult() throws Exception {
        String key = JOB_EVENTS_RESULT + uuid;
        LOGGER.info("开始读取结果: {}", key);
        Jedis jedis = createJedis();
        try {
            for (int i = 1; i <= 60; i++) {
                TimeUnit.SECONDS.sleep(15);
                String result = jedis.get(key);
                if (result == null) {
                    LOGGER.info("调用返回值为空, 继续等待[{}/{}]", i, 60);
                }
                if (result != null) {
                    LOGGER.info("调用返回值: {}", result);
                    if (result.startsWith("ok") || result.startsWith("success")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        } finally {
            jedis.del(key);
            closeJedis(jedis);
        }
        return false;
    }


    private Jedis createJedis() {
        String[] addr = redisAddress.split(":");
        return new Jedis(addr[0], Integer.parseInt(addr[1]));
    }

    private void closeJedis(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }
}