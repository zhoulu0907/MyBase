package com.cmsr.onebase.module.system.framework.sms.core.client.impl;

import com.cmsr.onebase.framework.common.core.KeyValue;
import com.cmsr.onebase.module.system.framework.sms.core.client.dto.SmsReceiveRespDTO;
import com.cmsr.onebase.module.system.framework.sms.core.client.dto.SmsSendRespDTO;
import com.cmsr.onebase.module.system.framework.sms.core.client.dto.SmsTemplateRespDTO;
import com.cmsr.onebase.module.system.framework.sms.core.enums.SmsTemplateAuditStatusEnum;
import com.cmsr.onebase.module.system.framework.sms.core.property.SmsChannelProperties;
import kong.unirest.core.ContentType;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import kong.unirest.core.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * <a href="https://market.cucloud.cn/product.html?productid=1661415297416">中云数赢 - 经分助手</a>
 *
 * @author xiayuanming
 * @since 2025/12/22
 */
@Slf4j
public class InfoCloudOpassClient extends AbstractSmsClient {

    private static final String URL = "https://opassapi.infocloud.cc/message/send";

    public InfoCloudOpassClient(SmsChannelProperties properties) {
        super(properties);
        if (StringUtils.isBlank(properties.getApiKey())) {
            throw new IllegalArgumentException("apiKey 不能为空");
        }
        if (StringUtils.isBlank(properties.getApiSecret())) {
            throw new IllegalArgumentException("apiSecret 不能为空");
        }
    }


    @Override
    public SmsSendRespDTO sendSms(Long logId, String mobile, String apiTemplateId, List<KeyValue<String, Object>> templateParams) throws Throwable {
        Map<String, String> headers = new HashMap<>();
        headers.put("x-api-key", properties.getApiKey());
        headers.put("x-sign-method", HmacAlgorithms.HMAC_SHA_224.getName());
        headers.put("x-nonce", generateRandomString(10));
        headers.put("x-timestamp", Long.toString(System.currentTimeMillis()));
        Map<String, String> body = new HashMap<>();
        body.put("templateCode", apiTemplateId);
        body.put("phones", mobile);
        List<String> extractParams = templateParams.stream().map(param -> "\"" + param.getValue() + "\"").toList();
        String params = "[" + String.join(",", extractParams) + "]";
        body.put("templateParam", params);
        String signature = computeSignature(properties.getApiSecret(), headers, body);
        headers.put("x-sign", signature);
        HttpResponse<JsonNode> response = Unirest.post(URL)
                .contentType(ContentType.APPLICATION_JSON)
                .headers(headers)
                .body(body)
                .asJson();
        /**
         * # success
         * {
         *   "code" : 200,
         *   "data" : {
         *     "msgId" : "1200517842414903296",
         *     "desc" : "null"
         *   },
         *   "requestId" : "1766392225.690-92411933795-9026-61251"
         * }
         * # error
         * {
         *   "code" : 60003,
         *   "message" : "短信变量个数和模板变量个数不一样",
         *   "requestId" : "1766392344.132-92412012535-9024-61528"
         * }
         */
        JSONObject respObj = response.getBody().getObject();
        if (response.isSuccess() && respObj.getInt("code") == 200) {
            return new SmsSendRespDTO().setSuccess(true)
                    .setApiRequestId(respObj.getString("requestId"))
                    .setSerialNo(respObj.getJSONObject("data").getString("msgId"))
                    .setApiCode(respObj.getString("code"));
        } else {
            return new SmsSendRespDTO().setSuccess(false)
                    .setApiRequestId(respObj.getString("requestId"))
                    .setApiCode(respObj.getString("code"))
                    .setApiMsg(respObj.getString("message") + ":" + respObj.getJSONObject("data").toString());
        }
    }

    @Override
    public List<SmsReceiveRespDTO> parseSmsReceiveStatus(String text) throws Throwable {
        return List.of();
    }

    @Override
    public SmsTemplateRespDTO getSmsTemplate(String apiTemplateId) throws Throwable {
        return new SmsTemplateRespDTO()
                .setId(apiTemplateId)
                .setContent("")
                .setAuditStatus(SmsTemplateAuditStatusEnum.SUCCESS.getStatus())
                .setAuditReason("");
    }

    private static String generateRandomString(int length) {
        return RandomStringUtils.secureStrong().next(length, true, true);
    }

    private static String computeSignature(String secretKey, Map<String, String> headers, Map<String, String> body) {
        SortedMap<String, Object> sortedMap = new TreeMap<>();
        sortedMap.putAll(headers);
        sortedMap.putAll(body);
        List<String> params = new ArrayList<>();
        for (Map.Entry<String, Object> entry : sortedMap.entrySet()) {
            params.add(entry.getKey() + "=" + entry.getValue());
        }
        String paramString = String.join("&", params);
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_224, secretKey).hmacHex(paramString);
    }
}
