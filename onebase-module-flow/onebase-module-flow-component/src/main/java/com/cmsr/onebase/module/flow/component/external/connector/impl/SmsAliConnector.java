package com.cmsr.onebase.module.flow.component.external.connector.impl;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.flow.component.external.connector.AbstractConnector;
import com.cmsr.onebase.module.flow.component.external.connector.ConnectorExecutor;
import com.cmsr.onebase.module.flow.component.external.connector.exception.ConnectorConfigException;
import com.cmsr.onebase.module.flow.component.external.connector.exception.ConnectorExecutionException;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 阿里云短信连接器实现
 * 支持短信发送功能
 *
 * @author zhoulu
 * @since 2025-01-10
 */
@Slf4j
@Component
public class SmsAliConnector extends AbstractConnector {

    @Override
    public String getConnectorType() {
        return "SMS_ALI";
    }

    @Override
    public String getConnectorName() {
        return "阿里云短信连接器";
    }

    @Override
    public String getConnectorDescription() {
        return "阿里云短信发送连接器，支持模板短信和验证码短信";
    }

    @Override
    protected Map<String, Object> doExecute(String actionType, Map<String, Object> config) throws Exception {
        // 1. 创建阿里云短信客户端
        Client client = createSmsClient(config);

        // 2. 构建短信请求
        SendSmsRequest request = buildSmsRequest(config);

        // 3. 发送短信
        Map<String, Object> inputData = getInputData(config);
        log.info("开始发送阿里云短信，手机号: {}, 模板Code: {}",
                 inputData.get("phoneNumbers"), inputData.get("templateCode"));

        SendSmsResponse response = client.sendSmsWithOptions(request, createRuntimeOptions());

        if (response.getBody() != null && response.getBody().getCode() != null) {
            log.info("阿里云短信发送成功，BizId: {}, Code: {}",
                     response.getBody().getBizId(), response.getBody().getCode());
            return buildSuccessResult("短信发送成功", Map.of(
                    "bizId", response.getBody().getBizId(),
                    "code", response.getBody().getCode()
            ));
        } else {
            log.error("阿里云短信发送失败，响应为空");
            throw new ConnectorExecutionException(getConnectorType(), actionType, "短信发送失败，响应为空");
        }
    }

    @Override
    public boolean validateConfig(Map<String, Object> config) {
        return config != null &&
               config.containsKey("accessKey") &&
               config.containsKey("secretKey") &&
               config.containsKey("regionId") &&
               config.containsKey("signName");
    }

    /**
     * 创建阿里云短信客户端
     */
    private Client createSmsClient(Map<String, Object> config) throws Exception {
        Config aliConfig = new Config()
                .setAccessKeyId(config.get("accessKey").toString())
                .setAccessKeySecret(config.get("secretKey").toString())
                .setRegionId(config.get("regionId").toString());

        return new Client(aliConfig);
    }

    /**
     * 构建短信请求
     */
    private SendSmsRequest buildSmsRequest(Map<String, Object> config) {
        Map<String, Object> inputData = getInputData(config);

        // 手机号码（阿里云API需要逗号分隔的字符串）
        Object phoneNumbersObj = inputData.get("phoneNumbers");
        String phoneNumbers;
        if (phoneNumbersObj instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> phoneList = (List<String>) phoneNumbersObj;
            phoneNumbers = String.join(",", phoneList);
        } else if (phoneNumbersObj instanceof String) {
            phoneNumbers = phoneNumbersObj.toString();
        } else {
            phoneNumbers = "";
        }

        // 短信签名
        String signName = config.getOrDefault("signName", "").toString();

        // 短信模板Code
        String templateCode = inputData.getOrDefault("templateCode", "").toString();

        // 模板参数（需要转为JSON字符串）
        Object templateParamObj = inputData.get("templateParam");
        @SuppressWarnings("unchecked")
        Map<String, String> templateParamMap = templateParamObj instanceof Map ?
            (Map<String, String>) templateParamObj : new HashMap<>();
        String templateParam = JsonUtils.toJsonString(templateParamMap);

        return new SendSmsRequest()
                .setPhoneNumbers(phoneNumbers)
                .setSignName(signName)
                .setTemplateCode(templateCode)
                .setTemplateParam(templateParam);
    }

    /**
     * 创建运行时选项
     */
    private RuntimeOptions createRuntimeOptions() {
        return new RuntimeOptions();
    }
}
