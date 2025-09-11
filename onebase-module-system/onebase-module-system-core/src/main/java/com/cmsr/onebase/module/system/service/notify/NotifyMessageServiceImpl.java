package com.cmsr.onebase.module.system.service.notify;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.controller.admin.notify.vo.message.NotifyMessageMyPageReqVO;
import com.cmsr.onebase.module.system.controller.admin.notify.vo.message.NotifyMessagePageReqVO;
import com.cmsr.onebase.module.system.dal.database.NotifyMessageDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.notify.NotifyMessageDO;
import com.cmsr.onebase.module.system.dal.dataobject.notify.NotifyTemplateDO;

import jakarta.annotation.Resource;

/**
 * 站内信 Service 实现类
 *
 * @author xrcoder
 */
@Service
@Validated
public class NotifyMessageServiceImpl implements NotifyMessageService {

    @Resource
    private NotifyMessageDataRepository notifyMessageDataRepository;

    @Override
    public Long createNotifyMessage(Long userId, Integer userType,
                                    NotifyTemplateDO template, String templateContent, Map<String, Object> templateParams) {
        NotifyMessageDO message = new NotifyMessageDO().setUserId(userId).setUserType(userType)
                .setTemplateId(template.getId()).setTemplateCode(template.getCode())
                .setTemplateType(template.getType()).setTemplateNickname(template.getNickname())
                .setTemplateContent(templateContent).setTemplateParams(templateParams).setReadStatus(false);
        notifyMessageDataRepository.insert(message);
        return message.getId();
    }

    @Override
    public PageResult<NotifyMessageDO> getNotifyMessagePage(NotifyMessagePageReqVO pageReqVO) {
        return notifyMessageDataRepository.findPage(pageReqVO);
    }

    @Override
    public PageResult<NotifyMessageDO> getMyMyNotifyMessagePage(NotifyMessageMyPageReqVO pageReqVO, Long userId, Integer userType) {
        return notifyMessageDataRepository.findMyPage(pageReqVO, userId, userType);
    }

    @Override
    public NotifyMessageDO getNotifyMessage(Long id) {
        return notifyMessageDataRepository.findById(id);
    }

    @Override
    public List<NotifyMessageDO> getUnreadNotifyMessageList(Long userId, Integer userType, Integer size) {
        return notifyMessageDataRepository.findUnreadList(userId, userType, size);
    }

    @Override
    public Long getUnreadNotifyMessageCount(Long userId, Integer userType) {
        return notifyMessageDataRepository.countUnread(userId, userType);
    }

    @Override
    public int updateNotifyMessageRead(Collection<Long> ids, Long userId, Integer userType) {
        return notifyMessageDataRepository.updateReadStatus(ids, userId, userType);
    }

    @Override
    public int updateAllNotifyMessageRead(Long userId, Integer userType) {
        return notifyMessageDataRepository.updateAllReadStatus(userId, userType);
    }

}
