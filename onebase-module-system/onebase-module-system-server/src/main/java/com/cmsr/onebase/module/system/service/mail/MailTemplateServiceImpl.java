package com.cmsr.onebase.module.system.service.mail;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.system.controller.admin.mail.vo.template.MailTemplatePageReqVO;
import com.cmsr.onebase.module.system.controller.admin.mail.vo.template.MailTemplateSaveReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.mail.MailAccountDO;
import com.cmsr.onebase.module.system.dal.dataobject.mail.MailTemplateDO;
import com.cmsr.onebase.module.system.dal.mysql.mail.MailTemplateMapper;
import com.cmsr.onebase.module.system.dal.redis.RedisKeyConstants;
import com.google.common.annotations.VisibleForTesting;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.MAIL_TEMPLATE_CODE_EXISTS;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.MAIL_TEMPLATE_NOT_EXISTS;

/**
 * 邮箱模版 Service 实现类
 *
 * @author wangjingyi
 * @since 2022-03-21
 */
@Service
@Validated
@Slf4j
public class MailTemplateServiceImpl implements MailTemplateService {

    /**
     * 正则表达式，匹配 {} 中的变量
     */
    private static final Pattern PATTERN_PARAMS = Pattern.compile("\\{(.*?)}");

    //@Resource
    //private MailTemplateMapper mailTemplateMapper;

    @Resource
    private DataRepository dataRepository;

    @Override
    @TenantIgnore
    public Long createMailTemplate(MailTemplateSaveReqVO createReqVO) {
        // 校验 code 是否唯一
        validateCodeUnique(null, createReqVO.getCode());

        // 插入
        MailTemplateDO template = BeanUtils.toBean(createReqVO, MailTemplateDO.class)
                .setParams(parseTemplateContentParams(createReqVO.getContent()));
        dataRepository.insert(template);
		//mailTemplateMapper.insert(template);
        return template.getId();
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.MAIL_TEMPLATE,
            allEntries = true) // allEntries 清空所有缓存，因为可能修改到 code 字段，不好清理
    @TenantIgnore
    public void updateMailTemplate(@Valid MailTemplateSaveReqVO updateReqVO) {
        // 校验是否存在
        validateMailTemplateExists(updateReqVO.getId());
        // 校验 code 是否唯一
        validateCodeUnique(updateReqVO.getId(),updateReqVO.getCode());

        // 更新
        MailTemplateDO updateObj = BeanUtils.toBean(updateReqVO, MailTemplateDO.class)
                .setParams(parseTemplateContentParams(updateReqVO.getContent()));
        dataRepository.update(updateObj);
		//mailTemplateMapper.updateById(updateObj);
    }

    @VisibleForTesting
    @TenantIgnore
    void validateCodeUnique(Long id, String code) {

        ConfigStore configStore = new DefaultConfigStore()
                .and(Compare.EQUAL, "code", code)
                .and(Compare.EQUAL, "deleted", false);
        MailTemplateDO template = dataRepository.findOne(MailTemplateDO.class,configStore);

		//MailTemplateDO template = mailTemplateMapper.selectByCode(code);

        if (template == null) {
            return;
        }
        // 存在 template 记录的情况下
        if (id == null // 新增时，说明重复
                || ObjUtil.notEqual(id, template.getId())) { // 更新时，如果 id 不一致，说明重复
            throw exception(MAIL_TEMPLATE_CODE_EXISTS);
        }
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.MAIL_TEMPLATE,
            allEntries = true) // allEntries 清空所有缓存，因为 id 不是直接的缓存 code，不好清理
    @TenantIgnore
    public void deleteMailTemplate(Long id) {
        // 校验是否存在
        validateMailTemplateExists(id);

        // 删除
        dataRepository.deleteById(MailTemplateDO.class,id);
		//mailTemplateMapper.deleteById(id);
    }

    private void validateMailTemplateExists(Long id) {
        if (dataRepository.findById(MailTemplateDO.class,id) == null) {
            throw exception(MAIL_TEMPLATE_NOT_EXISTS);
        }
		//if (mailTemplateMapper.selectById(id) == null) {
          //  throw exception(MAIL_TEMPLATE_NOT_EXISTS);
        //}
    }

    @Override
    @TenantIgnore
    public MailTemplateDO getMailTemplate(Long id) {
		return dataRepository.findById(MailTemplateDO.class,id);
		//return mailTemplateMapper.selectById(id);
	}

    @Override
    @Cacheable(value = RedisKeyConstants.MAIL_TEMPLATE, key = "#code", unless = "#result == null")
    @TenantIgnore
    public MailTemplateDO getMailTemplateByCodeFromCache(String code) {
        ConfigStore configStore = new DefaultConfigStore()
                .and(Compare.EQUAL, "code", code)
                .and(Compare.EQUAL, "deleted", false);
        return dataRepository.findOne(MailTemplateDO.class,configStore);
		//return mailTemplateMapper.selectByCode(code);
    }

    @Override
    @TenantIgnore
    public PageResult<MailTemplateDO> getMailTemplatePage(MailTemplatePageReqVO pageReqVO) {

        ConfigStore configStore = new DefaultConfigStore()
                .and(Compare.EQUAL, "deleted", false);

        // 构建查询条件
        if (null != pageReqVO.getStatus()) {
            configStore.and(Compare.EQUAL, "status", pageReqVO.getStatus());
        }
        if (StringUtils.isNotBlank(pageReqVO.getCode())) {
            configStore.and(Compare.LIKE, "code", pageReqVO.getCode());
        }
        if (StringUtils.isNotBlank(pageReqVO.getName())) {
            configStore.and(Compare.LIKE, "name", pageReqVO.getName());
        }
        if (null != pageReqVO.getAccountId()) {
            configStore.and(Compare.EQUAL, "account_id", pageReqVO.getAccountId());
        }
        if (null != pageReqVO.getCreateTime()) {
            configStore.and(Compare.EQUAL, "create_time", pageReqVO.getCreateTime());
        }
        // 添加排序条件，按ID降序排列
        configStore.order("id", "DESC");

        return dataRepository.findPageWithConditions(
                MailTemplateDO.class,
                configStore,
                pageReqVO.getPageNo(),
                pageReqVO.getPageSize()
        );
		//return mailTemplateMapper.selectPage(pageReqVO);
    }

    @Override
    @TenantIgnore
    public List<MailTemplateDO> getMailTemplateList() {
		return dataRepository.findAll(MailTemplateDO.class);
		//return mailTemplateMapper.selectList();
	}

    @Override
    @TenantIgnore
    public String formatMailTemplateContent(String content, Map<String, Object> params) {
        return StrUtil.format(content, params);
    }

    @VisibleForTesting
    @TenantIgnore
    public List<String> parseTemplateContentParams(String content) {
        return ReUtil.findAllGroup1(PATTERN_PARAMS, content);
    }

    @Override
    @TenantIgnore
    public long getMailTemplateCountByAccountId(Long accountId) {

        ConfigStore configStore = new DefaultConfigStore()
                .and(Compare.EQUAL, "account_id", accountId)
                .and(Compare.EQUAL, "deleted", false);
        List<MailTemplateDO> list = dataRepository.findAll(MailTemplateDO.class, configStore);

        return list.size();
		
		//return mailTemplateMapper.selectCountByAccountId(accountId);
		
    }

}
