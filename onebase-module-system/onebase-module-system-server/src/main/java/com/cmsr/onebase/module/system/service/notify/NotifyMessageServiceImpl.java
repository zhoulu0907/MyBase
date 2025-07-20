package com.cmsr.onebase.module.system.service.notify;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.controller.admin.notify.vo.message.NotifyMessageMyPageReqVO;
import com.cmsr.onebase.module.system.controller.admin.notify.vo.message.NotifyMessagePageReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.notify.NotifyMessageDO;
import com.cmsr.onebase.module.system.dal.dataobject.notify.NotifyTemplateDO;
import com.cmsr.onebase.module.system.dal.mysql.notify.NotifyMessageMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 站内信 Service 实现类
 *
 * @author xrcoder
 */
@Service
@Validated
public class NotifyMessageServiceImpl implements NotifyMessageService {

    @Resource
    private NotifyMessageMapper notifyMessageMapper;

    @Resource
    private DataRepository dataRepository;

    @Override
    public Long createNotifyMessage(Long userId, Integer userType,
                                    NotifyTemplateDO template, String templateContent, Map<String, Object> templateParams) {
        NotifyMessageDO message = new NotifyMessageDO().setUserId(userId).setUserType(userType)
                .setTemplateId(template.getId()).setTemplateCode(template.getCode())
                .setTemplateType(template.getType()).setTemplateNickname(template.getNickname())
                .setTemplateContent(templateContent).setTemplateParams(templateParams).setReadStatus(false);
        dataRepository.insert(message);
        return message.getId();
    }

    @Override
    public PageResult<NotifyMessageDO> getNotifyMessagePage(NotifyMessagePageReqVO pageReqVO) {

        ConfigStore configStore = new DefaultConfigStore();
        if (null != pageReqVO.getUserId()) {
            configStore.and(Compare.EAUALS, "user_id", pageReqVO.getUserId());
        }
        if (null != pageReqVO.getUserType()) {
            configStore.and(Compare.EAUAL, "user_type", pageReqVO.getUserType());
        }
        if (StringUtils.isNotBlank(pageReqVO.getTemplateCode())) {
            configStore.and(Compare.LIKE, "template_code", pageReqVO.getTemplateCode());
        }
        if (null != pageReqVO.getTemplateType()) {
            configStore.and(Compare.EAUAL, "template_type", pageReqVO.getTemplateType());
        }
        if (null != pageReqVO.getCreateTime()) {
            configStore.and(Compare.EAUAL, "create_time", pageReqVO.getCreateTime());
        }

        return dataRepository.findPageWithConditions(NotifyMessageDO.class,configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());

    }

    @Override
    public PageResult<NotifyMessageDO> getMyMyNotifyMessagePage(NotifyMessageMyPageReqVO pageReqVO, Long userId, Integer userType) {

        ConfigStore configStore = new DefaultConfigStore();
        if (null != pageReqVO.getUserId()) {
            configStore.and(Compare.EAUALS, "user_id", pageReqVO.getUserId());
        }
        if (null != pageReqVO.getUserType()) {
            configStore.and(Compare.EAUAL, "user_type", pageReqVO.getUserType());
        }

        return dataRepository.findPageWithConditions(NotifyMessageDO.class,configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());

    }

    @Override
    public NotifyMessageDO getNotifyMessage(Long id) {
        return dataRepository.findById(NotifyMessageDO.class,id);
    }

    @Override
    public List<NotifyMessageDO> getUnreadNotifyMessageList(Long userId, Integer userType, Integer size) {

        ConfigStore configStore = new DefaultConfigStore();
        if (null != userId) {
            configStore.and(Compare.EAUAL, "user_id", userId);
        }
        if (null != userType) {
            configStore.and(Compare.EAUAL, "user_type", userType);
        }
        if (null != size) {
            configStore.limit(size);
        }
        return dataRepository.findAll(NotifyMessageDO.class,configStore);

    }

    @Override
    public Long getUnreadNotifyMessageCount(Long userId, Integer userType) {

        ConfigStore configStore = new DefaultConfigStore();
        if (null != userId) {
            configStore.and(Compare.EAUAL, "user_id", userId);
        }
        if (null != userType) {
            configStore.and(Compare.EAUAL, "user_type", userType);
        }

        List<NotifyMessageDO> notifyMessageDOList = dataRepository.findAll(NotifyMessageDO.class, configStore);
        return (long) notifyMessageDOList.size();

    }

    @Override
    public int updateNotifyMessageRead(Collection<Long> ids, Long userId, Integer userType) {
        return notifyMessageMapper.updateListRead(ids, userId, userType);
    }

    @Override
    public int updateAllNotifyMessageRead(Long userId, Integer userType) {
        return notifyMessageMapper.updateListRead(userId, userType);
    }

}
