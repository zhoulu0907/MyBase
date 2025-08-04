package com.cmsr.onebase.module.system.service.sms;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.SMS_CHANNEL_HAS_CHILDREN;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.SMS_CHANNEL_NOT_EXISTS;

import java.util.List;

import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.controller.admin.sms.vo.channel.SmsChannelPageReqVO;
import com.cmsr.onebase.module.system.controller.admin.sms.vo.channel.SmsChannelSaveReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.sms.SmsChannelDO;
import com.cmsr.onebase.module.system.framework.sms.core.client.SmsClient;
import com.cmsr.onebase.module.system.framework.sms.core.client.SmsClientFactory;
import com.cmsr.onebase.module.system.framework.sms.core.property.SmsChannelProperties;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * 短信渠道 Service 实现类
 *
 * @author zzf
 */
@Service
@Slf4j
public class SmsChannelServiceImpl implements SmsChannelService {

    @Resource
    private SmsClientFactory smsClientFactory;

    @Resource
    private SmsTemplateService smsTemplateService;

    @Resource
    private DataRepository dataRepository;

    @Override
    public Long createSmsChannel(SmsChannelSaveReqVO createReqVO) {
        SmsChannelDO channel = BeanUtils.toBean(createReqVO, SmsChannelDO.class);
        dataRepository.insert(channel);
		//smsChannelMapper.insert(channel);
        return channel.getId();
    }

    @Override
    public void updateSmsChannel(SmsChannelSaveReqVO updateReqVO) {
        // 校验存在
        validateSmsChannelExists(updateReqVO.getId());
        // 更新
        SmsChannelDO updateObj = BeanUtils.toBean(updateReqVO, SmsChannelDO.class);
        dataRepository.update(updateObj);
		//smsChannelMapper.updateById(updateObj);
    }

    @Override
    public void deleteSmsChannel(Long id) {
        // 校验存在
        validateSmsChannelExists(id);
        // 校验是否有在使用该账号的模版
        if (smsTemplateService.getSmsTemplateCountByChannelId(id) > 0) {
            throw exception(SMS_CHANNEL_HAS_CHILDREN);
        }
        // 删除
        dataRepository.deleteById(SmsChannelDO.class,id);
		//smsChannelMapper.deleteById(id);
    }

    private SmsChannelDO validateSmsChannelExists(Long id) {
        SmsChannelDO channel = dataRepository.findById(SmsChannelDO.class,id);
		//SmsChannelDO channel = smsChannelMapper.selectById(id);
        if (channel == null) {
            throw exception(SMS_CHANNEL_NOT_EXISTS);
        }
        return channel;
    }

    @Override
    public SmsChannelDO getSmsChannel(Long id) {
        return dataRepository.findById(SmsChannelDO.class,id);
		//return smsChannelMapper.selectById(id);
    }

    @Override
    public List<SmsChannelDO> getSmsChannelList() {
        return dataRepository.findAll(SmsChannelDO.class);
		//return smsChannelMapper.selectList();
    }

    @Override
    public PageResult<SmsChannelDO> getSmsChannelPage(SmsChannelPageReqVO pageReqVO) {

        ConfigStore configStore = new DefaultConfigStore();

        if (StringUtils.isNotBlank(pageReqVO.getSignature())) {
            configStore.and(Compare.LIKE, "signature", pageReqVO.getSignature());
        }
        if (null != pageReqVO.getStatus()) {
            configStore.and(Compare.EQUAL, "status", pageReqVO.getStatus());
        }
        if (null != pageReqVO.getCreateTime()) {
            configStore.and(Compare.EQUAL, "create_time", pageReqVO.getCreateTime());
        }

        return dataRepository.findPageWithConditions(SmsChannelDO.class,configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());

        //return smsChannelMapper.selectPage(pageReqVO);
    }

    @Override
    public SmsClient getSmsClient(Long id) {
        SmsChannelDO channel = dataRepository.findById(SmsChannelDO.class,id);
		//SmsChannelDO channel = smsChannelMapper.selectById(id);
        SmsChannelProperties properties = BeanUtils.toBean(channel, SmsChannelProperties.class);
        return smsClientFactory.createOrUpdateSmsClient(properties);
    }

    @Override
    public SmsClient getSmsClient(String code) {
        return smsClientFactory.getSmsClient(code);
    }

}
