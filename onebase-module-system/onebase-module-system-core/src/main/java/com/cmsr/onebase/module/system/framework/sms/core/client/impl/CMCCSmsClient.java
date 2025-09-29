package com.cmsr.onebase.module.system.framework.sms.core.client.impl;

import com.cmsr.onebase.framework.common.core.KeyValue;
import com.cmsr.onebase.module.system.framework.sms.core.client.dto.SmsReceiveRespDTO;
import com.cmsr.onebase.module.system.framework.sms.core.client.dto.SmsSendRespDTO;
import com.cmsr.onebase.module.system.framework.sms.core.client.dto.SmsTemplateRespDTO;
import com.cmsr.onebase.module.system.framework.sms.core.property.SmsChannelProperties;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/27 18:07
 */
public class CMCCSmsClient extends AbstractSmsClient {


    public CMCCSmsClient(SmsChannelProperties properties) {
        super(properties);

    }

    @Override
    public SmsSendRespDTO sendSms(Long logId, String mobile, String apiTemplateId, List<KeyValue<String, Object>> templateParams) throws Throwable {
        return null;
    }

    @Override
    public List<SmsReceiveRespDTO> parseSmsReceiveStatus(String text) throws Throwable {
        return List.of();
    }

    @Override
    public SmsTemplateRespDTO getSmsTemplate(String apiTemplateId) throws Throwable {
        return null;
    }
}
