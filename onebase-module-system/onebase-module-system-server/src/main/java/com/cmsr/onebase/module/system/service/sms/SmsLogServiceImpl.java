package com.cmsr.onebase.module.system.service.sms;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.controller.admin.sms.vo.log.SmsLogPageReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.sms.SmsChannelDO;
import com.cmsr.onebase.module.system.dal.dataobject.sms.SmsLogDO;
import com.cmsr.onebase.module.system.dal.dataobject.sms.SmsTemplateDO;
import com.cmsr.onebase.module.system.dal.mysql.sms.SmsLogMapper;
import com.cmsr.onebase.module.system.enums.sms.SmsReceiveStatusEnum;
import com.cmsr.onebase.module.system.enums.sms.SmsSendStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * 短信日志 Service 实现类
 *
 * @author zzf
 */
@Slf4j
@Service
public class SmsLogServiceImpl implements SmsLogService {

    @Resource
    private SmsLogMapper smsLogMapper;

    @Resource
    private DataRepository dataRepository;

    @Override
    public Long createSmsLog(String mobile, Long userId, Integer userType, Boolean isSend,
                             SmsTemplateDO template, String templateContent, Map<String, Object> templateParams) {
        SmsLogDO.SmsLogDOBuilder logBuilder = SmsLogDO.builder();
        // 根据是否要发送，设置状态
        logBuilder.sendStatus(Objects.equals(isSend, true) ? SmsSendStatusEnum.INIT.getStatus()
                : SmsSendStatusEnum.IGNORE.getStatus());
        // 设置手机相关字段
        logBuilder.mobile(mobile).userId(userId).userType(userType);
        // 设置模板相关字段
        logBuilder.templateId(template.getId()).templateCode(template.getCode()).templateType(template.getType());
        logBuilder.templateContent(templateContent).templateParams(templateParams)
                .apiTemplateId(template.getApiTemplateId());
        // 设置渠道相关字段
        logBuilder.channelId(template.getChannelId()).channelCode(template.getChannelCode());
        // 设置接收相关字段
        logBuilder.receiveStatus(SmsReceiveStatusEnum.INIT.getStatus());

        // 插入数据库
        SmsLogDO logDO = logBuilder.build();
        dataRepository.insert(logDO);
		//smsLogMapper.insert(logDO);
        return logDO.getId();
    }

    @Override
    public void updateSmsSendResult(Long id, Boolean success,
                                    String apiSendCode, String apiSendMsg,
                                    String apiRequestId, String apiSerialNo) {
        SmsSendStatusEnum sendStatus = success ? SmsSendStatusEnum.SUCCESS : SmsSendStatusEnum.FAILURE;
        dataRepository.update(SmsLogDO.builder().id(id)
                .sendStatus(sendStatus.getStatus()).sendTime(LocalDateTime.now())
                .apiSendCode(apiSendCode).apiSendMsg(apiSendMsg)
                .apiRequestId(apiRequestId).apiSerialNo(apiSerialNo).build());
        //smsLogMapper.updateById(SmsLogDO.builder().id(id)
        //        .sendStatus(sendStatus.getStatus()).sendTime(LocalDateTime.now())
        //        .apiSendCode(apiSendCode).apiSendMsg(apiSendMsg)
        //        .apiRequestId(apiRequestId).apiSerialNo(apiSerialNo).build());
    }

    @Override
    public void updateSmsReceiveResult(Long id, Boolean success, LocalDateTime receiveTime,
                                       String apiReceiveCode, String apiReceiveMsg) {
        SmsReceiveStatusEnum receiveStatus = Objects.equals(success, true) ?
                SmsReceiveStatusEnum.SUCCESS : SmsReceiveStatusEnum.FAILURE;

        dataRepository.update(SmsLogDO.builder().id(id).receiveStatus(receiveStatus.getStatus())
                .receiveTime(receiveTime).apiReceiveCode(apiReceiveCode).apiReceiveMsg(apiReceiveMsg).build());

        //smsLogMapper.updateById(SmsLogDO.builder().id(id).receiveStatus(receiveStatus.getStatus())
        //        .receiveTime(receiveTime).apiReceiveCode(apiReceiveCode).apiReceiveMsg(apiReceiveMsg).build());
    }

    @Override
    public PageResult<SmsLogDO> getSmsLogPage(SmsLogPageReqVO pageReqVO) {

        //ConfigStore configStore = new DefaultConfigStore();
        //
        //if (StringUtils.isNotBlank(pageReqVO.getSignature())) {
        //    configStore.and(Compare.LIKE, "signature", pageReqVO.getSignature());
        //}
        //if (null != pageReqVO.getStatus()) {
        //    configStore.and(Compare.EAUAL, "status", pageReqVO.getStatus());
        //}
        //if (null != pageReqVO.getCreateTime()) {
        //    configStore.and(Compare.EAUAL, "create_time", pageReqVO.getCreateTime());
        //}
        //
        //return dataRepository.findPageWithConditions(SmsChannelDO.class,configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());

        return smsLogMapper.selectPage(pageReqVO);
    }

}
