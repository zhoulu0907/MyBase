package com.cmsr.onebase.module.system.service.mail;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.system.vo.mail.MailLogPageReqVO;
import com.cmsr.onebase.module.system.dal.database.MailLogDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.mail.MailAccountDO;
import com.cmsr.onebase.module.system.dal.dataobject.mail.MailLogDO;
import com.cmsr.onebase.module.system.dal.dataobject.mail.MailTemplateDO;
import com.cmsr.onebase.module.system.enums.mail.MailSendStatusEnum;
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

    @Resource
    private MailLogDataRepository mailLogDataRepository;

    @Override
    @TenantIgnore
    public PageResult<MailLogDO> getMailLogPage(MailLogPageReqVO pageVO) {
        return mailLogDataRepository.findPage(pageVO);
    }

    @Override
    @TenantIgnore
    public MailLogDO getMailLog(Long id) {
        return mailLogDataRepository.findById(id);
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
        mailLogDataRepository.insert(logDO);
        return logDO.getId();
    }

    @Override
    public void updateMailSendResult(Long logId, String messageId, Exception exception) {
        // 1. 成功
        if (exception == null) {
            MailLogDO mailLogDO = new MailLogDO().setSendTime(LocalDateTime.now())
                .setSendStatus(MailSendStatusEnum.SUCCESS.getStatus()).setSendMessageId(messageId);
            mailLogDO.setId(logId);
            mailLogDataRepository.update(mailLogDO);
            return;
        }
        // 2. 失败
        MailLogDO mailLogDO = new MailLogDO().setSendTime(LocalDateTime.now())
            .setSendStatus(MailSendStatusEnum.FAILURE.getStatus()).setSendException(getRootCauseMessage(exception));
        mailLogDO.setId(logId);
        mailLogDataRepository.update(mailLogDO);
    }

}
