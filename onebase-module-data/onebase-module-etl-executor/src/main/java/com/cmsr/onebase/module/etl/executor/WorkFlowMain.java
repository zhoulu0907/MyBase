package com.cmsr.onebase.module.etl.executor;

import com.cmsr.onebase.module.etl.common.excute.ExecuteRequest;
import com.cmsr.onebase.module.etl.executor.util.JacksonUtil;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @Author：huangjie
 * @Date：2025/11/6 9:45
 */
public class WorkFlowMain {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            throw new RuntimeException("参数长度非法");
        }
        String input = args[0];
        byte[] decode = Base64.getDecoder().decode(input.getBytes(StandardCharsets.UTF_8));
        String inputJson = new String(decode, StandardCharsets.UTF_8);
        ExecuteRequest executeRequest = JacksonUtil.fromJson(inputJson, ExecuteRequest.class);
        try (WorkFlowExecutor workFlowExecutor = new WorkFlowExecutor(executeRequest)) {
            workFlowExecutor.execute();
        }
    }

}
