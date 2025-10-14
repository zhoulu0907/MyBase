package com.cmsr.onebase.module.flow.message;

import com.google.gson.Gson;
import org.apache.commons.cli.*;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 消息发送主类
 * 用于向RocketMQ发送流程处理时间消息
 *
 * @author huangjie
 * @since 2025/10/14
 */
public class MessageMain {

    private static final Logger LOGGER = Logger.getLogger(MessageMain.class.getName());

    private static final ClientServiceProvider PROVIDER = ClientServiceProvider.loadService();

    private static final Gson GSON = new Gson();

    public static final String TIME_TOPIC = "flow-process-time-topic";

    // 退出代码
    private static final int EXIT_CODE_SUCCESS = 0;      // 正常退出
    private static final int EXIT_CODE_ERROR = 1;        // 异常退出

    /**
     * 程序入口点
     *
     * @param args 命令行参数，应包含JSON格式的InputParams对象
     */
    public static void main(String[] args) {
        try {
            InputParams inputParams = parseInputParams(args);
            sendMessage(inputParams);
            System.exit(EXIT_CODE_SUCCESS);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "发送消息时发生错误: " + e.getMessage(), e);
            System.exit(EXIT_CODE_ERROR);
        }
    }


    /**
     * 解析输入参数
     */
    private static InputParams parseInputParams(String[] args) throws ParseException {
        Options options = createOptions();

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        InputParams inputParams = new InputParams();
        inputParams.setEndpoints(cmd.getOptionValue("endpoints"));
        if (cmd.hasOption("topic")) {
            inputParams.setTopic(cmd.getOptionValue("topic"));
        } else {
            inputParams.setTopic(TIME_TOPIC);
        }
        inputParams.setProcessId(Long.parseLong(cmd.getOptionValue("processId")));
        inputParams.setJobType(cmd.getOptionValue("jobType"));
        inputParams.setMessageTag(cmd.getOptionValue("msgTag"));

        return inputParams;
    }

    private static Options createOptions() {
        Options options = new Options();

        Option endpointsOption = new Option("e", "endpoints", true, "RocketMQ server endpoints");
        endpointsOption.setRequired(true);
        options.addOption(endpointsOption);

        Option topicOption = new Option("T", "topic", true, "Topic name");
        topicOption.setRequired(false);
        options.addOption(topicOption);

        Option processIdOption = new Option("p", "processId", true, "Process ID");
        processIdOption.setRequired(true);
        options.addOption(processIdOption);

        Option jobTypeOption = new Option("p", "jobType", true, "Process ID");
        jobTypeOption.setRequired(true);
        options.addOption(jobTypeOption);

        Option msgTagOption = new Option("t", "msgTag", true, "Message tag");
        msgTagOption.setRequired(true);
        options.addOption(msgTagOption);

        return options;
    }


    /**
     * 发送消息到RocketMQ
     */
    private static void sendMessage(InputParams inputParams) throws ClientException {
        Producer producer = null;
        try {
            // 创建生产者
            producer = createProducer(inputParams.getEndpoints(), inputParams.getTopic());
            // 构建并发送消息
            SendReceipt sendReceipt = buildAndSendMessage(producer, inputParams);
            LOGGER.log(Level.INFO, "消息已发送：" + sendReceipt.getMessageId());
        } finally {
            if (producer != null) {
                try {
                    producer.close();
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "关闭生产者时发生错误: " + e.getMessage(), e);
                }
            }
        }
    }


    /**
     * 创建RocketMQ生产者
     *
     * @param endpoints 服务器地址
     * @return Producer实例
     * @throws ClientException 客户端异常
     */
    private static Producer createProducer(String endpoints, String topic) throws ClientException {
        ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
                .setEndpoints(endpoints)
                .build();
        return PROVIDER.newProducerBuilder()
                .setClientConfiguration(clientConfiguration)
                .setTopics(topic)
                .build();
    }

    /**
     * 构建并发送消息
     *
     * @param producer    生产者实例
     * @param inputParams 输入参数
     * @return SendReceipt 发送回执
     * @throws ClientException 客户端异常
     */
    private static SendReceipt buildAndSendMessage(Producer producer, InputParams inputParams) throws ClientException {
        // 构建消息体
        Map<String, Object> messageBody = new HashMap<>();
        messageBody.put("processId", inputParams.getProcessId());
        messageBody.put("tag", inputParams.getMessageTag());
        messageBody.put("time", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        String messageJson = GSON.toJson(messageBody);
        LOGGER.log(Level.INFO, "发送消息：" + messageJson);
        Message message = PROVIDER.newMessageBuilder()
                .setTopic(inputParams.getTopic())
                .setTag(inputParams.getMessageTag())
                .setBody(messageJson.getBytes(StandardCharsets.UTF_8))
                .build();
        return producer.send(message);
    }
}