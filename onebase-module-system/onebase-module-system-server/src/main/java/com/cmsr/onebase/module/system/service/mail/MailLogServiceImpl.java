package com.cmsr.onebase.module.system.service.mail;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.controller.admin.mail.vo.log.MailLogPageReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.mail.MailAccountDO;
import com.cmsr.onebase.module.system.dal.dataobject.mail.MailLogDO;
import com.cmsr.onebase.module.system.dal.dataobject.mail.MailTemplateDO;
import com.cmsr.onebase.module.system.enums.mail.MailSendStatusEnum;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

import static cn.hutool.core.exceptions.ExceptionUtil.getRootCauseMessage;

/**
 * 邮件日志 Service 实现类
 *
 * @author wangjingyi
 * @since 2022-03-21
 */
@Service
@Validated
public class MailLogServiceImpl implements MailLogService {

    //@Resource
    //private MailLogMapper mailLogMapper;

    @Resource
    private DataRepository dataRepository;

    @Override
    public PageResult<MailLogDO> getMailLogPage(MailLogPageReqVO pageVO) {

        ConfigStore configStore = new DefaultConfigStore();
        if (null != pageVO.getUserId()) {
            configStore.and(Compare.EQUAL, "user_id", pageVO.getUserId());
        }
        if (null != pageVO.getUserType()) {
            configStore.and(Compare.EQUAL, "user_type", pageVO.getUserType());
        }
        if (StringUtils.isNotBlank(pageVO.getToMail())) {
            configStore.and(Compare.LIKE, "to_mail", pageVO.getToMail());
        }
        if (null != pageVO.getAccountId()) {
            configStore.and(Compare.EQUAL, "accound_id", pageVO.getAccountId());
        }
        if (null != pageVO.getTemplateId()) {
            configStore.and(Compare.EQUAL, "template_id", pageVO.getTemplateId());
        }
        if (null != pageVO.getSendStatus()) {
            configStore.and(Compare.EQUAL, "send_status", pageVO.getSendStatus());
        }
        if (null != pageVO.getSendTime()) {
            configStore.and(Compare.EQUAL, "send_time", pageVO.getSendTime());
        }

        return dataRepository.findPageWithConditions(MailLogDO.class,configStore, pageVO.getPageNo(), pageVO.getPageSize());
    
		//return mailLogMapper.selectPage(pageVO);
	}

    @Override
    public MailLogDO getMailLog(Long id) {
        return dataRepository.findById(MailLogDO.class,id);
		//return mailLogMapper.selectById(id);
    }

    @Override
    public Long createMailLog(Long userId, Integer userType, String toMail,
                              MailAccountDO account, MailTemplateDO template,
                              String templateContent, Map<String, Object> templateParams, Boolean isSend) {
        MailLogDO.MailLogDOBuilder logDOBuilder = MailLogDO.builder();
        // 根据是否要发送，设置状态
        logDOBuilder.sendStatus(Objects.equals(isSend, true) ? MailSendStatusEnum.INIT.getStatus()
                : MailSendStatusEnum.IGNORE.getStatus())
                // 用户信息
                .userId(userId).userType(userType).toMail(toMail)
                .accountId(account.getId()).fromMail(account.getMail())
                // 模板相关字段
                .templateId(template.getId()).templateCode(template.getCode()).templateNickname(template.getNickname())
                .templateTitle(template.getTitle()).templateContent(templateContent).templateParams(templateParams);

        // 插入数据库
        MailLogDO logDO = logDOBuilder.build();
        dataRepository.insert(logDO);
		//mailLogMapper.insert(logDO);
        return logDO.getId();
    }

    @Override
    public void updateMailSendResult(Long logId, String messageId, Exception exception) {
        // 1. 成功
        if (exception == null) {
            dataRepository.update(new MailLogDO().setId(logId).setSendTime(LocalDateTime.now())
                    .setSendStatus(MailSendStatusEnum.SUCCESS.getStatus()).setSendMessageId(messageId));
					 //mailLogMapper.updateById(new MailLogDO().setId(logId).setSendTime(LocalDateTime.now())
                    //.setSendStatus(MailSendStatusEnum.SUCCESS.getStatus()).setSendMessageId(messageId));
            return;
        }
        // 2. 失败
        dataRepository.update(new MailLogDO().setId(logId).setSendTime(LocalDateTime.now())
                .setSendStatus(MailSendStatusEnum.FAILURE.getStatus()).setSendException(getRootCauseMessage(exception)));
	 	//mailLogMapper.updateById(new MailLogDO().setId(logId).setSendTime(LocalDateTime.now())
                //.setSendStatus(MailSendStatusEnum.FAILURE.getStatus()).setSendException(getRootCauseMessage(exception)));

    }

}
