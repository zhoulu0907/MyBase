package com.cmsr.onebase.module.system.service.mail;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.MAIL_ACCOUNT_NOT_EXISTS;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.MAIL_ACCOUNT_RELATE_TEMPLATE_EXISTS;

import java.util.List;

import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.system.controller.admin.mail.vo.account.MailAccountPageReqVO;
import com.cmsr.onebase.module.system.controller.admin.mail.vo.account.MailAccountSaveReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.mail.MailAccountDO;
import com.cmsr.onebase.module.system.dal.redis.RedisKeyConstants;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * 邮箱账号 Service 实现类
 *
 * @author wangjingyi
 * @since 2022-03-21
 */
@Service
@Validated
@Slf4j
public class MailAccountServiceImpl implements MailAccountService {

    @Resource
    private MailTemplateService mailTemplateService;

    @Resource
    private DataRepository dataRepository;

    @Override
    @TenantIgnore
    public Long createMailAccount(MailAccountSaveReqVO createReqVO) {
        MailAccountDO account = BeanUtils.toBean(createReqVO, MailAccountDO.class);
        dataRepository.insert(account);
        //mailAccountMapper.insert(account);
        return account.getId();
    }

    @Override
    @CacheEvict(value = RedisKeyConstants.MAIL_ACCOUNT, key = "#updateReqVO.id")
    @TenantIgnore
    public void updateMailAccount(MailAccountSaveReqVO updateReqVO) {
        // 校验是否存在
        validateMailAccountExists(updateReqVO.getId());

        // 更新
        MailAccountDO updateObj = BeanUtils.toBean(updateReqVO, MailAccountDO.class);
        dataRepository.update(updateObj);
		//mailAccountMapper.updateById(updateObj);
    }

    @Override
    @CacheEvict(value = RedisKeyConstants.MAIL_ACCOUNT, key = "#id")
    @TenantIgnore
    public void deleteMailAccount(Long id) {
        // 校验是否存在账号
        validateMailAccountExists(id);
        // 校验是否存在关联模版
        if (mailTemplateService.getMailTemplateCountByAccountId(id) > 0) {
            throw exception(MAIL_ACCOUNT_RELATE_TEMPLATE_EXISTS);
        }

        // 删除
        dataRepository.deleteById(MailAccountDO.class,id);
		//mailAccountMapper.deleteById(id);
    }

    private void validateMailAccountExists(Long id) {
        if (dataRepository.findById(MailAccountDO.class,id) == null) {
            throw exception(MAIL_ACCOUNT_NOT_EXISTS);
        }
		// if (mailAccountMapper.selectById(id) == null) {
          //  throw exception(MAIL_ACCOUNT_NOT_EXISTS);
        //}
    }

    @Override
    @TenantIgnore
    public MailAccountDO getMailAccount(Long id) {
        return dataRepository.findById(MailAccountDO.class,id);
		//return mailAccountMapper.selectById(id);
    }

    @Override
    @Cacheable(value = RedisKeyConstants.MAIL_ACCOUNT, key = "#id", unless = "#result == null")
    @TenantIgnore
    public MailAccountDO getMailAccountFromCache(Long id) {
        return getMailAccount(id);
    }

    @Override
    @TenantIgnore
    public PageResult<MailAccountDO> getMailAccountPage(MailAccountPageReqVO pageReqVO) {

        ConfigStore configStore = new DefaultConfigStore();
        if (StringUtils.isNotBlank(pageReqVO.getMail())) {
            configStore.and(Compare.EQUAL, "mail", pageReqVO.getMail());
        }
        if (StringUtils.isNotBlank(pageReqVO.getUsername())) {
            configStore.and(Compare.EQUAL, "username", pageReqVO.getUsername());
        }
        return dataRepository.findPageWithConditions(MailAccountDO.class,configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());
		//return mailAccountMapper.selectPage(pageReqVO);
    }

    @Override
    @TenantIgnore
    public List<MailAccountDO> getMailAccountList() {
        return dataRepository.findAll(MailAccountDO.class);
		//return mailAccountMapper.selectList();
    }

}
