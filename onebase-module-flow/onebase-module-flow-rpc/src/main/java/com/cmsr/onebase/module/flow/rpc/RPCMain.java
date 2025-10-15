package com.cmsr.onebase.module.flow.rpc;

import com.cmsr.onebase.module.flow.context.job.DateFieldJobService;
import com.cmsr.onebase.module.flow.context.job.TimerJobService;
import org.apache.commons.cli.*;
import org.redisson.Redisson;
import org.redisson.api.RRemoteService;
import org.redisson.api.RedissonClient;
import org.redisson.api.RemoteInvocationOptions;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 消息发送主类
 * 用于向RocketMQ发送流程处理时间消息
 *
 * @author huangjie
 * @since 2025/10/14
 */
public class RPCMain {

    private static final Logger LOGGER = Logger.getLogger(RPCMain.class.getName());

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
            callService(inputParams);
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
        inputParams.setJobType(cmd.getOptionValue("jobType"));
        inputParams.setProcessId(Long.parseLong(cmd.getOptionValue("processId")));
        // Redisson配置参数
        inputParams.setRedisAddress(cmd.getOptionValue("redisAddress"));
        return inputParams;
    }

    private static Options createOptions() {
        Options options = new Options();

        Option jobTypeOption = new Option("j", "jobType", true, "Job type (timer or fld)");
        jobTypeOption.setRequired(true);
        options.addOption(jobTypeOption);

        Option processIdOption = new Option("p", "processId", true, "Process ID");
        processIdOption.setRequired(true);
        options.addOption(processIdOption);

        Option redisAddressOption = new Option("r", "redisAddress", true, "Redis server address");
        redisAddressOption.setRequired(true);
        options.addOption(redisAddressOption);

        return options;
    }


    /**
     * 发送消息到RocketMQ
     */
    private static void callService(InputParams inputParams) throws Exception {
        RedissonClient redisson = null;
        try {
            redisson = createRedissonClient(inputParams.getRedisAddress());
            RemoteInvocationOptions options = RemoteInvocationOptions.defaults()
                    .expectAckWithin(30, TimeUnit.SECONDS)
                    .expectResultWithin(15, TimeUnit.MINUTES);
            String result;
            if (inputParams.getJobType().equalsIgnoreCase("timer")) {
                RRemoteService remoteService = redisson.getRemoteService(TimerJobService.KEY_PREFIX_TIMER);
                TimerJobService timerJobService = remoteService.get(TimerJobService.class, options);
                result = timerJobService.call(inputParams.getProcessId());
            } else if (inputParams.getJobType().equalsIgnoreCase("fld")) {
                RRemoteService remoteService = redisson.getRemoteService(DateFieldJobService.KEY_PREFIX_FLD);
                DateFieldJobService dateFieldJobService = remoteService.get(DateFieldJobService.class, options);
                result = dateFieldJobService.call(inputParams.getProcessId());
            } else {
                throw new Exception("不支持的Job类型:" + inputParams.getJobType());
            }
            LOGGER.log(Level.INFO, "调用返回值: " + result);
            if (result == null || !result.startsWith("ok") || !result.startsWith("success")) {
                throw new Exception("调用失败:" + result);
            }
        } finally {
            closeRedissonClient(redisson);
        }
    }

    /**
     * 创建Redisson客户端实例
     *
     * @param redisAddress Redis服务器地址
     * @return 配置好的RedissonClient实例
     */
    private static RedissonClient createRedissonClient(String redisAddress) {
        Config config = new Config();
        // 配置Redisson客户端连接参数
        config.useSingleServer().setAddress(redisAddress)
                .setConnectionMinimumIdleSize(1)
                .setConnectionPoolSize(2);
        return Redisson.create(config);
    }


    private static void closeRedissonClient(RedissonClient redisson) {
        if (redisson != null) {
            redisson.shutdown();
        }
    }

}