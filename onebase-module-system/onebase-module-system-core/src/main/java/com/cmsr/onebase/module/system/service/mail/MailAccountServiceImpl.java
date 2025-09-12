package com.cmsr.onebase.module.system.service.mail;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.system.vo.mail.MailAccountPageReqVO;
import com.cmsr.onebase.module.system.vo.mail.MailAccountSaveReqVO;
import com.cmsr.onebase.module.system.dal.database.MailAccountDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.mail.MailAccountDO;
import com.cmsr.onebase.module.system.dal.redis.RedisKeyConstants;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.MAIL_ACCOUNT_NOT_EXISTS;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.MAIL_ACCOUNT_RELATE_TEMPLATE_EXISTS;

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
    private MailAccountDataRepository mailAccountDataRepository;

    @Override
    @TenantIgnore
    public Long createMailAccount(MailAccountSaveReqVO createReqVO) {
        MailAccountDO account = BeanUtils.toBean(createReqVO, MailAccountDO.class);
        mailAccountDataRepository.insert(account);
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
        mailAccountDataRepository.update(updateObj);
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
        mailAccountDataRepository.deleteById(id);
    }

    private void validateMailAccountExists(Long id) {
        if (mailAccountDataRepository.findById(id) == null) {
            throw exception(MAIL_ACCOUNT_NOT_EXISTS);
        }
    }

    @Override
    @TenantIgnore
    public MailAccountDO getMailAccount(Long id) {
        return mailAccountDataRepository.findById(id);
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
        return mailAccountDataRepository.findPage(pageReqVO);
    }

    @Override
    @TenantIgnore
    public List<MailAccountDO> getMailAccountList() {
        return mailAccountDataRepository.findAll();
    }

}
