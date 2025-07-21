package com.cmsr.onebase.module.system.service.notify;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.controller.admin.notify.vo.template.NotifyTemplatePageReqVO;
import com.cmsr.onebase.module.system.controller.admin.notify.vo.template.NotifyTemplateSaveReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.notify.NotifyMessageDO;
import com.cmsr.onebase.module.system.dal.dataobject.notify.NotifyTemplateDO;
import com.cmsr.onebase.module.system.dal.mysql.notify.NotifyTemplateMapper;
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
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.NOTIFY_TEMPLATE_CODE_DUPLICATE;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.NOTIFY_TEMPLATE_NOT_EXISTS;

/**
 * 站内信模版 Service 实现类
 *
 * @author xrcoder
 */
@Service
@Validated
@Slf4j
public class NotifyTemplateServiceImpl implements NotifyTemplateService {

    /**
     * 正则表达式，匹配 {} 中的变量
     */
    private static final Pattern PATTERN_PARAMS = Pattern.compile("\\{(.*?)}");

    @Resource
    private NotifyTemplateMapper notifyTemplateMapper;

    @Resource
    private DataRepository dataRepository;

    @Override
    public Long createNotifyTemplate(NotifyTemplateSaveReqVO createReqVO) {
        // 校验站内信编码是否重复
        validateNotifyTemplateCodeDuplicate(null, createReqVO.getCode());

        // 插入
        NotifyTemplateDO notifyTemplate = BeanUtils.toBean(createReqVO, NotifyTemplateDO.class);
        notifyTemplate.setParams(parseTemplateContentParams(notifyTemplate.getContent()));
        dataRepository.insert(notifyTemplate);
		//notifyTemplateMapper.insert(notifyTemplate);
        return notifyTemplate.getId();
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.NOTIFY_TEMPLATE,
            allEntries = true) // allEntries 清空所有缓存，因为可能修改到 code 字段，不好清理
    public void updateNotifyTemplate(NotifyTemplateSaveReqVO updateReqVO) {
        // 校验存在
        validateNotifyTemplateExists(updateReqVO.getId());
        // 校验站内信编码是否重复
        validateNotifyTemplateCodeDuplicate(updateReqVO.getId(), updateReqVO.getCode());

        // 更新
        NotifyTemplateDO updateObj = BeanUtils.toBean(updateReqVO, NotifyTemplateDO.class);
        updateObj.setParams(parseTemplateContentParams(updateObj.getContent()));
        dataRepository.save(updateObj);
		//notifyTemplateMapper.updateById(updateObj);
    }

    @VisibleForTesting
    public List<String> parseTemplateContentParams(String content) {
        return ReUtil.findAllGroup1(PATTERN_PARAMS, content);
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.NOTIFY_TEMPLATE,
            allEntries = true) // allEntries 清空所有缓存，因为 id 不是直接的缓存 code，不好清理
    public void deleteNotifyTemplate(Long id) {
        // 校验存在
        validateNotifyTemplateExists(id);
        // 删除
        dataRepository.deleteById(NotifyTemplateDO.class,id);
		//notifyTemplateMapper.deleteById(id);
    }

    private void validateNotifyTemplateExists(Long id) {
        if (dataRepository.findById(NotifyTemplateDO.class,id) == null) {
            throw exception(NOTIFY_TEMPLATE_NOT_EXISTS);
        }
		//if (notifyTemplateMapper.selectById(id) == null) {
          //  throw exception(NOTIFY_TEMPLATE_NOT_EXISTS);
        //}
    }

    @Override
    public NotifyTemplateDO getNotifyTemplate(Long id) {
        return dataRepository.findById(NotifyTemplateDO.class,id);
		//return notifyTemplateMapper.selectById(id);
    }

    @Override
    @Cacheable(cacheNames = RedisKeyConstants.NOTIFY_TEMPLATE, key = "#code",
            unless = "#result == null")
    public NotifyTemplateDO getNotifyTemplateByCodeFromCache(String code) {

        ConfigStore configStore = new DefaultConfigStore()
                .and(Compare.EQUAL, "code", code);

        return dataRepository.findOne(NotifyTemplateDO.class, configStore);

        //return notifyTemplateMapper.selectByCode(code);

    }

    @Override
    public PageResult<NotifyTemplateDO> getNotifyTemplatePage(NotifyTemplatePageReqVO pageReqVO) {

        ConfigStore configStore = new DefaultConfigStore();

        if (StringUtils.isNotBlank(pageReqVO.getCode())) {
            configStore.and(Compare.EQUAL, "code", pageReqVO.getCode());
        }
        if (StringUtils.isNotBlank(pageReqVO.getName())) {
            configStore.and(Compare.EQUAL, "name", pageReqVO.getPageNo());
        }
        if (null != pageReqVO.getStatus()) {
            configStore.and(Compare.EQUAL, "status", pageReqVO.getStatus());
        }
        if (null != pageReqVO.getCreateTime()) {
            configStore.and(Compare.EQUAL, "create_time", pageReqVO.getCreateTime());
        }

        return dataRepository.findPageWithConditions(NotifyTemplateDO.class,configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());

        //return notifyTemplateMapper.selectPage(pageReqVO);
    }

    @VisibleForTesting
    void validateNotifyTemplateCodeDuplicate(Long id, String code) {

        ConfigStore configStore = new DefaultConfigStore()
                .and(Compare.EQUAL, "code", code);

        NotifyTemplateDO template = dataRepository.findOne(NotifyTemplateDO.class, configStore);

        //NotifyTemplateDO template = notifyTemplateMapper.selectByCode(code);
        if (template == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的字典类型
        if (id == null) {
            throw exception(NOTIFY_TEMPLATE_CODE_DUPLICATE, code);
        }
        if (!template.getId().equals(id)) {
            throw exception(NOTIFY_TEMPLATE_CODE_DUPLICATE, code);
        }
    }

    /**
     * 格式化站内信内容
     *
     * @param content 站内信模板的内容
     * @param params  站内信内容的参数
     * @return 格式化后的内容
     */
    @Override
    public String formatNotifyTemplateContent(String content, Map<String, Object> params) {
        return StrUtil.format(content, params);
    }

}
